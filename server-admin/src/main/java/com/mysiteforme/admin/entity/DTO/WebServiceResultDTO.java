package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebServiceResultDTO implements Serializable {

    /**
     * 用于定位的IP地址
     */
    private String ip;

    /**
     * 当前经纬度
     */
    private WebServiceLocationDTO location;

    /**
     * 定位行政区划信息
     */
    private WebServiceAdInfoDTO ad_info;
}
