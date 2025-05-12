package com.mysiteforme.admin.entity.VO;

import lombok.Data;

@Data
public class DeviceTokenInfo {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 日常访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;
}
