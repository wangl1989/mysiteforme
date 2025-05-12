package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IndexLogDTO implements Serializable {

    private Long id;

    private String userName;

    private String title;

    private String httpMethod;

    private String requestUri;

    private Date createDate;

    private String method;

}
