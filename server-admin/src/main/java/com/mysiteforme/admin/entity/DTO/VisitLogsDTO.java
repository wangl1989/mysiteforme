package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class VisitLogsDTO implements Serializable {

    private Long userId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 系统信息
     */
    private String os;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * agent完整信息
     */
    private String userAgent;

    /**
     * 用户IP
     */
    private String ipAddress;

    /**
     * 国家
     */
    private String country;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

}
