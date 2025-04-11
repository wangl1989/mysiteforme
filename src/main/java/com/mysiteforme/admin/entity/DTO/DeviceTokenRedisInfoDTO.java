package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

@Data
public class DeviceTokenRedisInfoDTO {
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 是否是当前设备
     */
    private Boolean isCurrentDevice;

}
