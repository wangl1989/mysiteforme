/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:26:32
 * @ Description:
 *      在 Spring 中，@Cacheable 注解在同一个类中自调用时不会生效，
 *      因为 Spring AOP 代理无法拦截同一个类中的方法调用。
 *      为了解决这个问题，可以将需要缓存的方法提取到一个单独的服务类中，
 *      然后在原来的服务类中注入这个新服务类并调用其方法。
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.dao.RoleDao;
import com.mysiteforme.admin.dao.UserDao;
import com.mysiteforme.admin.entity.Role;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.RoleVO;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userCacheService")
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {

    private final UserDao baseMapper;

    private final RoleDao roleDao;

    private final PermissionDao permissionDao;

    /**
     * 根据ID查询用户详细信息
     * @param id 用户ID
     * @return 用户详细信息
     */
    @Cacheable(value = "system::user::details", key = "'id:'+#id", unless = "#result == null")
    @Override
    public UserVO findUserByIdDetails(Long id) {
        User user = baseMapper.selectById(id);
        if(user == null || user.getDelFlag()){
            throw MyException.builder().businessError(MessageConstants.User.USER_NOT_FOUND).build();
        }
        // 检查用户ID是否是超级管理员
        if(id == 1L){
            return this.getSuperAdminUserDetail(user);
        }else{
            return baseMapper.findUserDetailById(id);
        }
    }

    @Override
    public UserVO getSuperAdminUserDetail(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper.eq("del_flag", false);
        List<Role> roles = roleDao.selectList(roleWrapper);
        Set<RoleVO> roleVOs = roles.stream().map(role -> new RoleVO(role.getId(), role.getName(),role.getIsDefault(),role.getRemarks())).collect(Collectors.toSet());
        userVO.setRoles(roleVOs);
        userVO.setPermissions(permissionDao.allPermission());
        return userVO;
    }

}
