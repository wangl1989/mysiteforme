/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:12
 * @ Description: 菜单服务实现类 提供菜单的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.entity.VO.*;
import com.mysiteforme.admin.entity.request.AddMenuRequest;
import com.mysiteforme.admin.entity.request.UpdateMenuRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
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


@Service("menuService")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {

    private final CacheUtils cacheUtils;

    private final UserCacheService userCacheService;

    private final PermissionService permissionService;
    /**
     * 查询所有菜单
     * 结果会被缓存
     * @param map 查询参数，包含isShow等条件
     * @return 菜单列表
     */
    @Cacheable(value = "system::menu::allMenus",key = "'allMenus_isShow_'+#map['isShow'].toString()",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Menu> selectAllMenus(Map<String,Object> map) {
        return baseMapper.getMenus(map);
    }

    /**
     * 保存或更新菜单
     * 同时清除菜单和用户相关的所有缓存
     * @param menu 菜单对象
     */
    @Caching(evict = {
            @CacheEvict(value = "system::menu::allMenus",allEntries = true)
    })
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveMenu(AddMenuRequest request) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(request,menu);
        if(request.getParentId() == null || request.getParentId() == 0){
            menu.setLevel(1);
            if(request.getSort() == null || request.getSort() == 0 || request.getSort() == 1){
                menu.setSort(getMaxSort(request.getParentId(),null));
            }
        }else{
            Menu parentMenu = this.getById(request.getParentId());
            if(parentMenu==null){
                throw MyException.builder().businessError(MessageConstants.Menu.MENU_PARENT_ID_INVALID).build();
            }
            menu.setParentIds(parentMenu.getParentIds());
            menu.setLevel(parentMenu.getLevel()+1);
            if(request.getSort() == null || request.getSort() == 0 || request.getSort() == 1) {
                menu.setSort(getMaxSort(request.getParentId(),null));
            }
        }
        saveOrUpdate(menu);
        menu.setParentIds(StringUtils.isBlank(menu.getParentIds())? menu.getId()+"," : menu.getParentIds()+menu.getId()+",");
        saveOrUpdate(menu);
        cacheUtils.evictCacheOnMenuChange(menu.getId());
    }

    @Caching(evict = {
            @CacheEvict(value = "system::menu::allMenus",allEntries = true)
    })
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updateMenu(UpdateMenuRequest request) {
        Menu oldMenu = this.getById(request.getId());
        if(oldMenu == null) {
            throw MyException.builder().businessError(MessageConstants.Menu.MENU_NOT_FOUND).build();
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(request,menu);
        if(request.getParentId() == null || request.getParentId() == 0){
            menu.setLevel(1);
            if(oldMenu.getParentId() != null) {
                menu.setSort(getMaxSort(request.getParentId(),request.getId()));
                menu.setParentIds(request.getId() + ",");
            }
        }else{
            if(request.getId().equals(request.getParentId())){
                throw MyException.builder().businessError(MessageConstants.Menu.PARENT_ID_ERROR).build();
            }
            if(!request.getParentId().equals(oldMenu.getParentId())) {
                Menu parentMenu = this.getById(request.getParentId());
                if (parentMenu == null) {
                    throw MyException.builder().businessError(MessageConstants.Menu.MENU_PARENT_ID_INVALID).build();
                }
                request.setLevel(parentMenu.getLevel() + 1);
                menu.setSort(getMaxSort(request.getParentId(),request.getId()));
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
     * @return 用户可见的菜单列表
     */
    @Cacheable(value = "system::menu::userMenu",key = "T(String).valueOf(#id)",unless = "#result == null or #result.size() == 0")
    @Override
    public List<MenuTreeVO> getShowMenuByUser(Long id) {
        Map<String,Object> map = Maps.newHashMap();
        if(1L != id) {
            map.put("userId", id);
        }
        List<MenuTreeVO> total = baseMapper.getUserMenus(map);
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

        return getMenuTree(total, permissionMap);
    }

    /**
     * 递归获取树形菜单
     * @param parentMenu 父菜单
     * @param totalMenus 所有菜单
     * @param treeVOs 树形菜单列表
     * @return 树形菜单列表
     */
    private List<MenuTreeVO> getMenuTree(List<MenuTreeVO> total, Map<Long,Set<PermissionVO>> permissionMap) {
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
            meta.setAuthList(permissionMap.getOrDefault(m.getId(), Collections.emptySet()));
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
