package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgentDTO implements Serializable {

    /**
     * 系统信息
     */
    private String os;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * userAgent完整信息
     */
    private String userAgent;
}
