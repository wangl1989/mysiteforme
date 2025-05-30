/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:12
 * @ Description: 菜单服务实现类 提供菜单的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.entity.VO.*;
import com.mysiteforme.admin.entity.request.AddMenuRequest;
import com.mysiteforme.admin.entity.request.UpdateMenuRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.PermissionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.dao.MenuDao;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.redis.CacheUtils;
import com.mysiteforme.admin.service.MenuService;
import org.springframework.util.CollectionUtils;


@Service("menuService")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {

    private final CacheUtils cacheUtils;

    private final UserCacheService userCacheService;

    private final PermissionService permissionService;

    /**
     * 保存或更新菜单
     * 同时清除菜单和用户相关的所有缓存
     * @param menu 菜单对象
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveMenu(Menu menu) {
        if(menu.getSort() == null || menu.getSort() == 0 || menu.getSort() == 1){
            menu.setSort(getMaxSort(menu.getParentId(),null));
        }
        if(menu.getParentId() == null || menu.getParentId() == 0){
            menu.setLevel(1);
        }else{
            Menu parentMenu = this.getById(menu.getParentId());
            if(parentMenu==null){
                throw MyException.builder().businessError(MessageConstants.Menu.MENU_PARENT_ID_INVALID).build();
            }
            menu.setParentIds(parentMenu.getParentIds());
            menu.setLevel(parentMenu.getLevel()+1);
        }
        saveOrUpdate(menu);
        menu.setParentIds(StringUtils.isBlank(menu.getParentIds())? menu.getId()+"," : menu.getParentIds()+menu.getId()+",");
        saveOrUpdate(menu);
        cacheUtils.evictCacheOnMenuChange(menu.getId());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updateMenu(Menu menu) {
        Menu oldMenu = this.getById(menu.getId());
        if(oldMenu == null) {
            throw MyException.builder().businessError(MessageConstants.Menu.MENU_NOT_FOUND).build();
        }
        // 设置排序值
        if(menu.getSort() == null || menu.getSort() == 0 || menu.getSort() == 1){
            menu.setSort(getMaxSort(menu.getParentId(),menu.getId()));
        }
        if(menu.getParentId() == null || menu.getParentId() == 0){
            // 如果是顶级菜单，直接设置level是1，一级parentIds（它本身）
            menu.setLevel(1);
            menu.setParentIds(menu.getId() + ",");
        }else{
            if(menu.getId().equals(menu.getParentId())){
                throw MyException.builder().businessError(MessageConstants.Menu.PARENT_ID_ERROR).build();
            }
            // 如果父目录更换了（这里的parentId一定不为NULL），那么就要重新设置level以及parentIds
            if(!menu.getParentId().equals(oldMenu.getParentId())) {
                Menu parentMenu = this.getById(menu.getParentId());
                if (parentMenu == null) {
                    throw MyException.builder().businessError(MessageConstants.Menu.MENU_PARENT_ID_INVALID).build();
                }
                menu.setLevel(parentMenu.getLevel() + 1);
                // 如果父目录更换了就需要重设排序值
                menu.setSort(getMaxSort(menu.getParentId(),menu.getId()));
                menu.setParentIds(parentMenu.getParentIds()+menu.getId()+",");
            }
        }
        saveOrUpdate(menu);
        cacheUtils.evictCacheOnMenuChange(menu.getId());
    }

    private Integer getMaxSort(Long parentId, Long id){
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if(parentId != null){
            wrapper.eq(Menu::getParentId, parentId);
        }else{
            wrapper.isNull(Menu::getParentId);
        }
        wrapper.eq(Menu::getDelFlag, false);
        if(id != null) {
            wrapper.ne(Menu::getId, id);
        }
        wrapper.orderByDesc(Menu::getSort);
        wrapper.last("LIMIT 1");
        Menu maxSortMenu = getOne(wrapper);
        if(maxSortMenu != null) {
            return  maxSortMenu.getSort() + 1;
        }else{
            return 1;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMenu(Long id) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getDelFlag,false);
        queryWrapper.eq(Permission::getMenuId,id);
        List<Permission> permissionList = permissionService.list(queryWrapper);
        if(!permissionList.isEmpty()){
            throw MyException.builder().businessError(MessageConstants.Menu.MENU_HAS_PERMISSIONS).build();
        }
        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(Menu::getDelFlag,false);
        menuWrapper.eq(Menu::getParentId,id);
        List<Menu> menuList = list(menuWrapper);
        if(!menuList.isEmpty()){
            throw MyException.builder().businessError(MessageConstants.Menu.MENU_HAS_SUBMENU).build();
        }
        Menu menu = this.getById(id);
        menu.setDelFlag(true);
        this.saveOrUpdate(menu);
        cacheUtils.evictCacheOnMenuChange(id);
    }

    /**
     * 根据菜单名称获取菜单数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    @Override
    public Long getCountByName(String name,Long id) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        if(id != null){
            wrapper.ne("id",id);
        }
        return count(wrapper);
    }

    /**
     * 获取指定用户的显示菜单
     * 结果会被缓存
     * @param id 用户ID
     * @param isDetail 是否展示详情
     * @return 用户可见的菜单列表
     */
    @Cacheable(value = "system::menu::userMenu", key = "T(String).valueOf(#id)", condition = "#isCurrent != null and #isCurrent == false ")
    @Override
    public List<MenuTreeVO> getShowMenuByUser(Long id, Boolean isDetail) {
        List<MenuTreeVO> total = baseMapper.getUserMenus(id == 1L ? null : id);
        UserVO userVO = userCacheService.findUserByIdDetails(id);
        Set<PermissionVO> permissions = userVO.getPermissions();
        Map<Long,Set<PermissionVO>> permissionMap = Maps.newHashMap();

        permissions.forEach(permission -> {
            MenuVO menuVO = permission.getMenu();
            if(menuVO != null) {
                // 使用TreeSet替代HashSet，并传入比较器
                permissionMap.computeIfAbsent(permission.getMenu().getId(),
                        k -> new TreeSet<>()).add(permission);
            }
        });

        return getMenuTree(total, permissionMap, isDetail);
    }

    /**
     * 递归获取树形菜单
     * @param parentMenu 父菜单
     * @param totalMenus 所有菜单
     * @param treeVOs 树形菜单列表
     * @param isCurrent 是否为展示当前菜单
     * @return 树形菜单列表
     */
    private List<MenuTreeVO> getMenuTree(List<MenuTreeVO> total, Map<Long,Set<PermissionVO>> permissionMap, Boolean isDetail) {
        // 用于存储根节点
        List<MenuTreeVO> rootNodes = new ArrayList<>();
        // 用于快速查找子节点
        Map<Long, MenuTreeVO> nodeMap = new HashMap<>();

        // 初始化所有节点，并存储到Map中
        for (MenuTreeVO m : total) {
            // 处理空meta对象
            MenuMetaVO meta = Optional.ofNullable(m.getMeta()).orElseGet(() -> {
                MenuMetaVO newMeta = new MenuMetaVO();
                m.setMeta(newMeta);
                return newMeta;
            });
            // 设置权限列表（使用空集合兜底）
            // TreeSet已经排好序，直接使用
            Set<PermissionVO> permissionSet = permissionMap.getOrDefault(m.getId(), Collections.emptySet());
            if(isDetail) {
                // 如果是正常展示正常详情(包含权限详细信息)
                meta.setAuthList(permissionSet);
            } else {
                // 如果是展示当前用户菜单(包含权限按钮key的集合)
                if(!CollectionUtils.isEmpty(permissionSet)) {
                    Set<String> buttonKeys = permissionSet.stream()
                            .filter(p -> p.getButton() != null &&
                                    p.getPermissionType() == PermissionType.BUTTON.getCode())
                            .map(p -> p.getButton().getButtonKey())
                            .collect(Collectors.toSet());
                    meta.setButtonKeys(buttonKeys);
                }
            }
            nodeMap.put(m.getId(), m);
        }

        // 构建树结构
        for (MenuTreeVO m : total) {
            Long parentId = m.getParentId();
            if (parentId == null) {
                // 根节点
                rootNodes.add(m);
            } else {
                // 子节点，挂载到父节点
                MenuTreeVO parent = nodeMap.get(parentId);
                if (parent != null) {
                    List<MenuTreeVO> children = parent.getChildren();
                    if (children == null || children.isEmpty()) {
                        children = new ArrayList<>();
                    }
                    children.add(m);
                    parent.setChildren(children);
                }
            }
        }

        return rootNodes;
    }

}
