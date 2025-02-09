package com.mysiteforme.admin.base;

import com.mysiteforme.admin.realm.AuthRealm.ShiroUser;
import org.apache.shiro.SecurityUtils;

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
        return ShiroUser().getIcon();
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Long id() {
        return ShiroUser().getId();
    }

    /**
     * 获取当前登录用户的登录名
     * @return 登录名
     */
    public static String loginName() {
        return ShiroUser().getLoginName();
    }

    /**
     * 获取当前登录用户的昵称
     * @return 用户昵称
     */
    public static String nickName(){
        return ShiroUser().getNickName();
    }

    public static ShiroUser ShiroUser() {
        return (ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }
}
