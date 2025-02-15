/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 20:39:17
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:19:55
 * @ Description: 用户认证服务类 Security用户实现类
 */

package com.mysiteforme.admin.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.MessageUtil;

@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService{

    private final UserService userService;

    public SecurityUserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //返回UserDetails接口，我们必须写一个UserDetails的实现类返回，或者用框架已经写好的User类
        //1.从数据库中查询用户名信息及权限 2.封装成UserDetails数据返回
        UserVO user = userService.findUserByLoginNameDetails(username);
        Optional.ofNullable(user).orElseThrow(() -> MyException.builder().validationError(MessageUtil.getMessage(MessageConstants.Exception.EXCEPTION_USER_NOT_FOUND)).build());
        if (ObjectUtils.isEmpty(user.getRoles())) {
            throw MyException.builder().validationError(MessageUtil.getMessage(MessageConstants.Exception.EXCEPTION_USER_NO_ROLE)).build();
        }
        if (ObjectUtils.isEmpty(user.getPermissions())) {
            throw MyException.builder().validationError(MessageUtil.getMessage(MessageConstants.Exception.EXCEPTION_USER_NO_PERMISSION)).build();
        }

        return new MyUserDetails(user);
    }
    
}