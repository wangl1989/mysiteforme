package com.mysiteforme.admin.entity.response;

import lombok.Data;

@Data
public class LocationResponse {

    private String ip;

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
