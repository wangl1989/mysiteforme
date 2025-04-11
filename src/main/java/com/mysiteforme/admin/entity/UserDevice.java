package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * UserDevice 类用于表示用户设备信息，继承自 DataEntity 类。
 * 该类包含了用户设备的详细信息，如设备ID、设备类型、设备名称等。
 * 通过注解 @TableName 指定了数据库表名为 "user_device"。
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_device")
@Data
@Getter
@Setter
public class UserDevice extends DataEntity {
    /**
     * 用户登录名称，表示该设备所属的用户。
     */
    private String userName;

    /**
     * 用户浏览器信息
     */
    private String userAgent;
    /**
     * 设备ID，唯一标识一个设备。
     */
    private String deviceId;

    /**
     * 设备类型，描述设备的种类（如手机、平板、电脑等）。
     */
    private String deviceType;

    /**
     * 设备名称，用户自定义的设备名称。
     */
    private String deviceName;

    /**
     * 设备型号，描述设备的具体型号。
     */
    private String deviceModel;

    /**
     * 操作系统版本，描述设备上安装的操作系统版本。
     */
    private String osVersion;

    /**
     * 浏览器信息，描述设备上使用的浏览器信息。
     */
    private String browserInfo;

    /**
     * 最后登录IP，记录设备最后一次登录的IP地址。
     */
    private String lastLoginIp;

    /**
     * 最后登录位置，记录设备最后一次登录的地理位置。
     */
    private String lastLoginLocation;

    /**
     * 设备指纹，用于唯一标识设备的指纹信息。
     */
    private String deviceFingerprint;

    /**
     * 用户登出时间
     */
    private Date loginOutDate;
}
