/**
 * @ Author: wangl
 * @ Create Time: 2025-02-17 18:44:32
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 18:49:38
 * @ Description: 公用头部参数
 */

package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

@Data
public class GlobalHeadParam {

    private String token;

    private String deviceId;

    private String username;

    private String authHeader;

}
