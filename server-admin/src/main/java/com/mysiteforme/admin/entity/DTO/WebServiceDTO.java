package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

@Data
public class WebServiceDTO {
    /**
     * 状态码，0为正常，其它为异常
     */
    private Integer status;

    /**
     * 对status的描述
     */
    private String message;

    /**
     * IP定位结果
     */
    private WebServiceResultDTO result;
}
