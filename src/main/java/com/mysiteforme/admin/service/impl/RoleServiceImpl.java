package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RoleDao;
import com.mysiteforme.admin.entity.Menu;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    /**
     * 保存角色信息及其菜单关系
     * 同时更新角色缓存并清除角色列表缓存
     * @param role 角色对象，包含菜单集合
     */
    @Caching(
            put = {@CachePut(value = "role",key = "'role_id_'+T(String).valueOf(#result.id)",condition = "#result.id != null and #result.id != 0")},
            evict = {@CacheEvict(value = "roleAll",key = "'roleAll'" )
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRole(Role role) {
        baseMapper.insert(role);
        baseMapper.saveRoleMenus(role.getId(),role.getMenuSet());
    }

    /**
     * 根据ID获取角色信息
     * 结果会被缓存
     * @param id 角色ID
     * @return 角色对象
     */
    @Cacheable(value = "role",key = "'role_id_'+T(String).valueOf(#id)",unless = "#result == null")
    @Override
    public Role getRoleById(Long id) {
        return baseMapper.selectRoleById(id);
    }

    /**
     * 更新角色信息及其菜单关系
     * 同时清除相关的角色、用户、菜单缓存
     * @param role 角色对象，包含更新后的菜单集合
     */
    @Caching(evict = {
            @CacheEvict(value = "role",key = "'role_id_'+T(String).valueOf(#role.id)" ),
            @CacheEvict(value = "roleAll",key = "'roleAll'" ),
            @CacheEvict(value = "user",allEntries=true ),
            @CacheEvict(value = "allMenus",allEntries = true)
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRole(Role role) {
        baseMapper.updateById(role);
        baseMapper.dropRoleMenus(role.getId());
        baseMapper.saveRoleMenus(role.getId(),role.getMenuSet());
    }

    /**
     * 删除角色（软删除）及其关联关系
     * 同时清除相关的角色、用户缓存
     * @param role 要删除的角色对象
     */
    @Caching(evict = {
            @CacheEvict(value = "role",key = "'role_id_'+T(String).valueOf(#role.id)" ),
            @CacheEvict(value = "roleAll",key = "'roleAll'" ),
            @CacheEvict(value = "user",allEntries=true )
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRole(Role role) {
        role.setDelFlag(true);
        baseMapper.updateById(role);
        baseMapper.dropRoleMenus(role.getId());
        baseMapper.dropRoleUsers(role.getId());
    }

    /**
     * 保存角色的菜单关系
     * @param id 角色ID
     * @param menuSet 菜单集合
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRoleMenus(Long id, Set<Menu> menuSet) {
        baseMapper.saveRoleMenus(id,menuSet);
    }

    /**
     * 删除角色的所有菜单关系
     * @param id 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dropRoleMenus(Long id) {
        baseMapper.dropRoleMenus(id);
    }

    /**
     * 获取指定角色名称的数量
     * @param name 角色名称
     * @return 角色数量
     */
    @Override
    public Long getRoleNameCount(String name) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 获取所有未删除的角色列表
     * 结果会被缓存
     * @return 角色列表
     */
    @Cacheable(value = "roleAll",key = "'roleAll'",unless = "#result == null or #result.size() == 0")
    @Override
    public List<Role> selectAll() {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }
}
