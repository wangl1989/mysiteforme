package com.mysiteforme.admin.entity.request;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class BaseTableFieldRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段长度
     */
    private Integer length;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 字段是否可以为空
     */
    private Boolean isNullable;
    /**
     * 字段描述
     */
    private String comment;

    /***
     * 表名
     */
    private String tableName;

    /***
     * 表类型
     */
    private Integer tableType;

    /***
     * 表的注释
     */
    private String tableComment;

    /***
     * 是否为系统默认选项值(switch类型的按钮用得到)
     */
    private Boolean defaultValue;

    /**
     * 字段原名称
     */
    private String oldName;

}
