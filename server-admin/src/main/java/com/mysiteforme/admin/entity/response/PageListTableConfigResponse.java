package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PageListTableConfigResponse implements Serializable {

    private Long id;

    private Integer tableType;

    private String tableName;

    private String tablePrefix;

    private String businessName;

    private String schemaName;

    private String moduleName;

    private String packageName;

    private String author;

    private String generatePath;

    private String options;

    private String remarks;

    private Long createId;

    private Long updateId;

    private String createUser;

    private String updateUser;

    private Date createDate;

    private Date updateDate;

    private Integer fieldCount;

    private Boolean delFlag;

}
