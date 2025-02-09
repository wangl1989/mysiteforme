package com.mysiteforme.admin.base;

import com.mysiteforme.admin.realm.AuthRealm.ShiroUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 系统用户工具类
 * 提供获取当前登录用户信息的静态方法
 * @author wangl
 * @since 2017/11/25
 */
public class MySysUser {
    /**
     * 获取当前登录用户的头像
     * @return 用户头像URL
     */
    public static String icon() {
        ShiroUser user = ShiroUser();
        return user != null ? user.getIcon() : null;
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Long id() {
        ShiroUser user = ShiroUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户的登录名
     * @return 登录名
     */
    public static String loginName() {
        ShiroUser user = ShiroUser();
        return user != null ? user.getLoginName() : null;
    }

    /**
     * 获取当前登录用户的昵称
     * @return 用户昵称
     */
    public static String nickName(){
        ShiroUser user = ShiroUser();
        return user != null ? user.getNickName() : null;
    }

    public static ShiroUser ShiroUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            Object principal = subject.getPrincipal();
            if (principal instanceof ShiroUser) {
                return (ShiroUser) principal;
            }
        }
        return null;
    }
}
