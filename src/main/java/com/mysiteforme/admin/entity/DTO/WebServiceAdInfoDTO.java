package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebServiceAdInfoDTO implements Serializable {

    /**
     * 国家
     */
    private String nation;

    /**
     * 国家代码（ISO3166标准3位数字码）
     */
    private Integer nation_code;

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

    /**
     * 行政区划代码
     */
    private Integer adcode;
}
