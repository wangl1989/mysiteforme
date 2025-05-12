package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * <a href="http://whois.pconline.com.cn/">免费IP地址查询</a>
 */
@Data
public class PconlineDTO implements Serializable {

    private String ip;

    /**
     * 省
     */
    private String pro;

    /**
     * 省code
     */
    private String proCode;

    /**
     * 市
     */
    private String city;

    /**
     * 市code
     */
    private String cityCode;

    /**
     * 区
     */
    private String region;

    /**
     * 区code
     */
    private String regionCode;

    /**
     * 综合地址
     */
    private String addr;

    /**
     * 国家名称
     */
    private String regionNames;

    /**
     * 错误信息
     */
    private String err;


}
