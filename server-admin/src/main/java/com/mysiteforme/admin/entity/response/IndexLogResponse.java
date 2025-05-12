package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndexLogResponse implements Serializable {

    private Long id;

    private String userName;

    private String icon;

    private String title;

    private String createTime;

    private String method;
}
