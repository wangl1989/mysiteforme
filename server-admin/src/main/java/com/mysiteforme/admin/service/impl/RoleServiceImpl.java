/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:18
 * @ Description: 角色服务实现类 提供角色的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.request.PageListRoleRequest;
import com.mysiteforme.admin.entity.request.SaveRoleMenuPerRequest;
import com.mysiteforme.admin.entity.response.BaseRoleResponse;
import com.mysiteforme.admin.entity.response.PageListRoleResponse;
import com.mysiteforme.admin.entity.response.RoleMenuPerResponse;
import com.mysiteforme.admin.service.UserCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.dao.RoleDao;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.VO.PermissionVO;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.CacheUtils;
import com.mysiteforme.admin.service.RoleService;
import com.mysiteforme.admin.util.MessageConstants;

@Service
@Transactional(readOnly = true, rollbackFor = MyException.class)
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {


    private final CacheUtils cacheUtils;

    private final UserCacheService userCacheService;

    public RoleServiceImpl(CacheUtils cacheUtils, UserCacheService userCacheService) {
        this.cacheUtils = cacheUtils;
        this.userCacheService = userCacheService;
    }

    @Override
    public IPage<PageListRoleResponse> selectPageUser(PageListRoleRequest request) {
        return baseMapper.selectPageRole(new Page<>(request.getPage(),request.getLimit()),request);
    }

    /**
     * 保存角色信息及其菜单关系
     * 同时更新角色缓存并清除角色列表缓存
     * @param role 角色对象
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRole(Role role) {
        baseMapper.insert(role);
        //清除超级管理员缓存
        cacheUtils.evictCacheOnRoleChangeSuperAdmin();
    }

    /**
     * 根据ID获取角色信息(包含权限集合)
     * 结果会被缓存
     * @param id 角色ID
     * @return 角色对象
     */
    @Override
    public Role getRoleById(Long id) {
        return baseMapper.selectRoleById(id);
    }

    @Override
    public Integer getIsDefaultRoleCount(Long id){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDefault,true);
        if(id != null){
            wrapper.ne(Role::getId,id);
        }
        List<Role> roleList = list(wrapper);
        return roleList.size();
    }

    /**
     * 更新角色信息及其菜单关系
     * 同时清除相关的角色、用户、菜单缓存
     * @param role 角色对象
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRole(Role role) {
        baseMapper.updateById(role);
        // 清除角色相关缓存
        cacheUtils.evictCacheOnRoleChange(role.getId());
    }

    /**
     * 删除角色（软删除）及其关联关系
     * 同时清除相关的角色、用户缓存
     * @param id 要删除的角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRole(Long id) {
        Set<Long> userIds = baseMapper.getUserIdsByRoleId(id);
        if(!userIds.isEmpty()){
            throw MyException.builder().businessError(MessageConstants.Role.ROLE_HAS_USER).build();
        }
        Role role = getById(id);
        if(role.getIsDefault()){
            throw MyException.builder().businessError(MessageConstants.Role.ROLE_CURRENT_ROLE_IS_DEFAULT_DELETE).build();
        }
        role.setDelFlag(true);
        baseMapper.updateById(role);
        // 删除原先的菜单关系
        baseMapper.dropRoleMenus(role.getId());
        // 删除原先的角色用户关系
        baseMapper.dropRoleUsers(role.getId());
        // 删除角色权限
        baseMapper.dropRolePermissions(role.getId());
        // 清除角色相关缓存
        cacheUtils.evictCacheOnRoleChange(role.getId());
    }

    /**
     * 获取指定角色名称的数量
     * @param name 角色名称
     * @return 角色数量
     */
    @Override
    public Long getRoleNameCount(String name) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getName,name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public List<BaseRoleResponse> userAllRole(Long id) {
        if(id.equals(1L)) {
            return baseMapper.myRoleList(null);
        }else{
            return baseMapper.myRoleList(id);
        }
    }

    @Override
    public RoleMenuPerResponse getUserRoleMenusPermissions(Long id) {
        RoleMenuPerResponse response = new RoleMenuPerResponse();
        response.setPermissionIds(baseMapper.getPemissionIdsByRoleId(id));
        response.setMenuIds(baseMapper.getMenuIdsByRoleId(id));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenusPermissions(SaveRoleMenuPerRequest request) {
        // 删除原先的菜单关系
        baseMapper.dropRoleMenus(request.getRoleId());
        //保存角色菜单关系
        baseMapper.saveRoleMenus(request.getRoleId(),request.getMenuIds());
        //保存角色权限关系
        Role role = baseMapper.selectById(request.getRoleId());
        if(role == null || role.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.Permission.ASSIGN_PERMISSION_USER_NOT_FOUND).build();
        }
        Long currentUserId = MySecurityUser.id();
        if(currentUserId == null){
            throw MyException.builder().unauthorized().build();
        }
        if(currentUserId.equals(1L)) {
            // 如果不是超级管理员,则需要进行权限检查：当前用户只能分配当前用户拥有的权限。
            // 获取当前登录用户的权限集合
            UserVO securityUser = userCacheService.findUserByIdDetails(MySecurityUser.id());
            Set<PermissionVO> permissionVOList = securityUser.getPermissions();
            // 当前登录用户拥有的权限ID集合
            Set<Long> roleOwnPermissionIds = permissionVOList.stream().map(PermissionVO::getId).collect(Collectors.toSet());
            // 只能给角色分配当前角色拥有的权限
            if(!roleOwnPermissionIds.containsAll(request.getPermissionIds())){
                throw MyException.builder().businessError(MessageConstants.Permission.ASSIGN_PERMISSION_ROLE_MORE).build();
            }
        }
        // 先移除角色对应的权限集合
        baseMapper.dropRolePermissions(request.getRoleId());
        // 再插入数据
        baseMapper.saveRolePermissions(request.getRoleId(),request.getPermissionIds());
        // 清除角色相关缓存
        cacheUtils.evictCacheOnRoleChange(role.getId());
    }

    /**
     * 获取角色对象
     * @return 角色对象
     */
    public Role getDefaultRole(){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDelFlag,false);
        wrapper.eq(Role::getIsDefault,true);
        List<Role> roleList = list(wrapper);
        if(roleList.isEmpty()){
            return getById(1L);
        }else{
            return roleList.get(0);
        }
    }
}
