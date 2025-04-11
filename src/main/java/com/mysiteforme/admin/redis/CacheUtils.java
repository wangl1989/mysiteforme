package com.mysiteforme.admin.redis;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.mysiteforme.admin.dao.MenuDao;
import com.mysiteforme.admin.dao.RoleDao;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.MessageConstants;

import lombok.RequiredArgsConstructor;

/**
 * Created by wangl on 2018/1/20.
 * todo:
 */
@Component
@RequiredArgsConstructor
public class CacheUtils {

    private final UserDao userDao;

    private final RoleDao roleDao;

    private final MenuDao menuDao;

    private final CacheManager cacheManager;

    /**
     * 清除用户缓存
     */
    private void evictUserCache(Long userId){
        User user = userDao.selectById(userId);
        if(user == null || user.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
        }
        String idCacheKey = "id:" + user.getId();
        String loginNameCacheKey = "loginName:" + user.getLoginName();
        String emailCacheKey = null;
        if(StringUtils.isNotBlank(user.getEmail())){
            emailCacheKey = "email:" + user.getEmail();
        }
        String telCacheKey = null;
        if(StringUtils.isNotBlank(user.getTel())){
            telCacheKey = "tel:" + user.getTel();
        }
        Cache cache = cacheManager.getCache("system::user");
        if(cache != null){
            cache.evict(idCacheKey);
            cache.evict(loginNameCacheKey);
            if(StringUtils.isNotBlank(emailCacheKey)){
                cache.evict(emailCacheKey);
            }
            if(StringUtils.isNotBlank(telCacheKey)){
                cache.evict(telCacheKey);
            }
        }
        Cache detailCache = cacheManager.getCache("system::user::details");
        if(detailCache != null){
            detailCache.evict(idCacheKey);
            detailCache.evict(loginNameCacheKey);
        }
    }


    /**
     * 清除当前用户的直接权限缓存
     * @param userId 用户id
     */
    private void evictUserPermsCache(Long userId){
        Cache cache = cacheManager.getCache("system::user::userPerms");
        if(cache != null){
            cache.evict(userId);
        }
    }

    /**
     * 清除当前用户的菜单缓存(菜单+权限)
     * @param userId 用户id
     */
    private void evictUserMenuCache(Long userId){
        Cache cache = cacheManager.getCache("system::menu::userMenu");
        if(cache != null){
            cache.evict(userId);
        }
    }

    /**
     * 清除角色菜单权限缓存
     * @param roleId 角色ID
     */
    private void evictRoleMenusCache(Long roleId){
        Cache cache = cacheManager.getCache("system::role::menuPerms");
        if(cache != null){
            cache.evict(roleId);
        }
    }
    /**
     * 更新角色时的缓存清除链条
     * @param roleId 角色id
     */
    public void evictCacheOnRoleChange(Long roleId) {
        
        // 2. 查询拥有此角色的所有用户
        Set<Long> userIds = roleDao.getUserIdsByRoleId(roleId);
        // 如果用户id集合不为空且不包含超级管理员的ID，则添加超级管理员ID
        if(userIds.isEmpty() || !userIds.contains(1L)){
            userIds.add(1L);
        }
        
        // 3. 清除这些用户的关联缓存（包含用户基本缓存，用户直接权限缓存）
        userIds.forEach(userId -> {
            evictUserCache(userId);
            evictUserPermsCache(userId);
            evictUserMenuCache(userId);
        });

    }

    public void evictCacheOnRoleChangeSuperAdmin(){
        evictUserCache(1L);
        evictUserPermsCache(1L);
        evictUserMenuCache(1L);
    }

    /**
     * 更新用户时的缓存清除链条
     * @param userId 用户id
     */
    public void evictCacheOnUserChange(Long userId){
        // 1. 清除用户基本缓存，用注解形式

        // 2. 清除用户直接权限缓存
        evictUserPermsCache(userId);
        // 3. 清除用户菜单缓存
        evictUserMenuCache(userId);

    }

    /**
     * 更新权限时的缓存清除链条
     * @param permId 权限id
     */
    public void evictCacheOnPermissionChange(Long permId){
        // 1. 清除权限缓存
        // 2. 清除角色权限缓存
        Set<Long> userIds = roleDao.getUserIdsByPermissionId(permId);
        if(userIds.isEmpty() || !userIds.contains(1L)){
            userIds.add(1L);
        }
        userIds.forEach(userId -> {
            evictUserCache(userId);
            evictUserPermsCache(userId);
            evictUserMenuCache(userId);
        });
        // 3. 清除用户角色缓存
        List<Long> roleIds = roleDao.getRoleIdsByPermissionId(permId);
        roleIds.forEach(this::evictRoleMenusCache);
    }

    /**
     * 更新菜单时的缓存清除链条
     * @param menuId 菜单id
     */
    public void evictCacheOnMenuChange(Long menuId){
        // 1. 清除菜单缓存
        // 2. 清除用户菜单缓存
        Menu menu = menuDao.selectById(menuId);
        if(menu == null || menu.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.Menu.MENU_NOT_FOUND).build();
        }
        Set<Long> userIds = menuDao.getUserIdsByMenuId(menuId);
        if(userIds.isEmpty() || !userIds.contains(1L)){
            userIds.add(1L);
        }
        userIds.forEach(userId -> {
            evictUserCache(userId);
            evictUserPermsCache(userId);
            evictUserMenuCache(userId);
        });
        // 3. 清除角色菜单缓存
        Set<Long> roleIds = roleDao.getRoleIdsByMenuId(menuId);
        roleIds.forEach(this::evictRoleMenusCache);
        // 4. 清除权限菜单缓存
    }
    
}
