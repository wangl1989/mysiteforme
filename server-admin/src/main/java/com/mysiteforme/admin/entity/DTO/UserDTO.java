/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 23:11:23
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:46:27
 * @ Description: 用户登录接收的数据
 */

package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

@Data
public class UserDTO {
 
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否记住我
     */
    private Boolean rememberMe;

    /**
     * 验证码
     */ 
    private String captcha;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String deviceModel;

}
