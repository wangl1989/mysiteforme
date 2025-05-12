package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class LocationDTO implements Serializable {

    private String ip;

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
    private String region;
}
