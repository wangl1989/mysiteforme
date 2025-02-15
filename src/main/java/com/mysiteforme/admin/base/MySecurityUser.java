/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 19:31:28
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 19:48:20
 * @ Description: 获取登录用户的基本信息
 */

package com.mysiteforme.admin.base;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mysiteforme.admin.security.MyUserDetails;

public class MySecurityUser {

    /**
     * 获取当前登录用户的头像
     * @return 用户头像URL
     */
    public static String icon() {
        MyUserDetails user = securityUser();
        return user != null ? user.getIcon() : null;
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Long id() {
        MyUserDetails user = securityUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户的登录名
     * @return 登录名
     */
    public static String loginName() {
        MyUserDetails user = securityUser();
        return user != null ? user.getLoginName() : null;
    }

    /**
     * 获取当前登录用户的昵称
     * @return 用户昵称
     */
    public static String nickName(){
        MyUserDetails user = securityUser();
        return user != null ? user.getNickName() : null;
    }

    public static MyUserDetails securityUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof MyUserDetails myUserDetails) {
                return myUserDetails;
            }
        }
        return null;
    }

}
