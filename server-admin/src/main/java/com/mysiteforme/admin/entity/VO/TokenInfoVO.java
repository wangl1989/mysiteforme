package com.mysiteforme.admin.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class TokenInfoVO {

    /**
     * 日常访问的token令牌，有效期通常是15分钟
     */
    private String accessToken;

    /**
     * 用于刷新日常令牌的刷新令牌，有效期通常是7天
     */
    private String refreshToken;

    /**
     * 用户的设备ID，有效期通常是永久
     */
    private String deviceId;
}
