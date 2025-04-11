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
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.entity.VO.*;
import com.mysiteforme.admin.entity.request.AddMenuRequest;
import com.mysiteforme.admin.entity.request.UpdateMenuRequest;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
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
import com.google.common.collect.Lists;
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

    private final UserService userService;

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
            request.setLevel(1);
            if(request.getSort() == null || request.getSort() == 0){
                QueryWrapper<Menu> wrapper = new QueryWrapper<>();
                wrapper.eqSql("sort","select max(sort) from sys_menu where parent_id is null");
                List<Menu> menus = this.list(wrapper);
                int sort = 0;
                if(menus != null && !menus.isEmpty()){
                    sort =  menus.get(0).getSort() +1;
                }
                request.setSort(sort);
            }
        }else{
            Menu parentMenu = this.getById(request.getParentId());
            if(parentMenu==null){
                throw MyException.builder().businessError(MessageConstants.Menu.MENU_PARENT_ID_INVALID).build();
            }
            request.setParentIds(parentMenu.getParentIds());
            request.setLevel(parentMenu.getLevel()+1);
            if(request.getSort() == null || request.getSort() == 0) {
                QueryWrapper<Menu> wrapper = new QueryWrapper<>();
                wrapper.eqSql("sort", "select max(sort) from sys_menu").eq("parent_id", request.getParentId());
                List<Menu> menus = this.list(wrapper);
                int sort = 0;
                if (menus != null && !menus.isEmpty()) {
                    sort = menus.get(0).getSort() + 1;
                }
                request.setSort(sort);
            }
        }
        saveOrUpdate(menu);
        request.setParentIds(StringUtils.isBlank(menu.getParentIds())? menu.getId()+"," : menu.getParentIds()+menu.getId()+",");
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
        if(!oldMenu.getName().equals(request.getName())) {
            if(this.getCountByName(request.getName())>0){
                throw MyException.builder().businessError(MessageConstants.Menu.MENU_NAME_EXISTS).build();
            }
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(request,menu);
        if(request.getParentId() == null || request.getParentId() == 0){
            request.setLevel(1);
            if(oldMenu.getParentId() != null) {
                QueryWrapper<Menu> wrapper = new QueryWrapper<>();
                wrapper.eqSql("sort", "select max(sort) from sys_menu where parent_id is null");
                List<Menu> menus = this.list(wrapper);
                int sort = 0;
                if (menus != null && !menus.isEmpty()) {
                    sort = menus.get(0).getSort() + 1;
                }
                request.setSort(sort);
                request.setParentIds(request.getId() + ",");
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
                QueryWrapper<Menu> wrapper = new QueryWrapper<>();
                wrapper.eqSql("sort", "select max(sort) from sys_menu").eq("parent_id", request.getParentId());
                List<Menu> menus = this.list(wrapper);
                int sort = 0;
                if (menus != null && !menus.isEmpty()) {
                    sort = menus.get(0).getSort() + 1;
                }
                request.setSort(sort);
                request.setParentIds(parentMenu.getParentIds()+menu.getId()+",");
            }
        }
        saveOrUpdate(menu);
        cacheUtils.evictCacheOnMenuChange(menu.getId());
    }

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
    }

    /**
     * 根据菜单名称获取菜单数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    @Override
    public long getCountByName(String name) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return count(wrapper);
    }

    /**
     * 获取树形结构的菜单列表
     * 只包含显示的菜单，按sort排序
     * @return 树形菜单列表
     */
    @Override
    public List<ZtreeVO> showTreeMenus() {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("is_show",true);
        wrapper.orderBy(false,false,"sort");
        List<Menu> totalMenus = baseMapper.selectList(wrapper);
        List<ZtreeVO> treeVOs = Lists.newArrayList();
        return getZTree(null,totalMenus,treeVOs);
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
                permissionMap.computeIfAbsent(permission.getMenu().getId(), k -> new HashSet<>()).add(permission);
            }
        });
        List<MenuTreeVO> result = Lists.newArrayList();
        return getMenuTree(total,permissionMap);
    }

    /**
     * 递归构建菜单树
     * @param tree 当前节点，首次调用传null
     * @param total 所有菜单列表
     * @param result 构建结果列表
     * @return 树形结构的菜单列表
     */
    private List<ZtreeVO> getZTree(ZtreeVO tree, List<Menu> total, List<ZtreeVO> result) {
        Long pid = tree == null?null:tree.getId();
        List<ZtreeVO> childList = Lists.newArrayList();
        for (Menu m : total){
            if(Objects.equals(pid, m.getParentId())) {
                ZtreeVO ztreeVO = new ZtreeVO();
                ztreeVO.setId(m.getId());
                ztreeVO.setName(m.getName());
                ztreeVO.setPid(pid);
                childList.add(ztreeVO);
                getZTree(ztreeVO,total,result);
            }
        }
        if(tree != null){
            tree.setChildren(childList);
        }else{
            result = childList;
        }
        return result;
    }

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
                        children = new ArrayList();
                    }
                    children.add(m);
                    parent.setChildren(children);
                }
            }
        }

        return rootNodes;
    }

}
