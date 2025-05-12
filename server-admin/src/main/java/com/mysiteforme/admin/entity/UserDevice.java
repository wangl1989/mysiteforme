package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * UserDevice 类用于表示用户设备信息，继承自 DataEntity 类。
 * 该类包含了用户设备的详细信息，如设备ID、设备类型、设备名称等。
 * 通过注解 @TableName 指定了数据库表名为 "user_device"。
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_device")
@Data
public class UserDevice extends DataEntity {
    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 设备ID，唯一标识一个设备。
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 用户设备信息
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 最后登录IP，记录设备最后一次登录的IP地址。
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 当前登录IP
     */
    @TableField("this_login_ip")
    private String thisLoginIp;

    /**
     * 上次登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 本次登录时间
     */
    @TableField("this_login_time")
    private LocalDateTime thisLoginTime;

    /**
     * 用户登出时间
     */
    @TableField("login_out_date")
    private LocalDateTime loginOutDate;

    /**
     * 首次出现时间
     */
    @TableField("first_seen")
    private LocalDateTime firstSeen;

    /**
     * 最后活跃时间
     */
    @TableField("last_seen")
    private LocalDateTime lastSeen;

}
