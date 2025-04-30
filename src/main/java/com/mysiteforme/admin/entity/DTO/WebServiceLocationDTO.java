package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebServiceLocationDTO implements Serializable {

    /**
     * 纬度
     */
    private Float lat;

    /**
     * 经度
     */
    private Float lng;
}
