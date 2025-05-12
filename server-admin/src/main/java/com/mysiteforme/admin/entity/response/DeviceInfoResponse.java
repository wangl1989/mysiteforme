package com.mysiteforme.admin.entity.response;

import lombok.Data;

@Data
public class DeviceInfoResponse {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 最后登录IP
     */
    private String ip;

    /**
     * 登录国家
     */
    private String country;

    /**
     * 登录省份
     */
    private String province;

    /**
     * 登录城市
     */
    private String city;

    /**
     * 登录地区
     */
    private String region;

    /**
     * 登录系统
     */
    private String os;

    /**
     * 登录浏览器
     */
    private String browser;

    /**
     * 设备是否在线
     */
    private Boolean online;

    /**
     * 是否未当前设备
     */
    private Boolean currentDevice;
}
