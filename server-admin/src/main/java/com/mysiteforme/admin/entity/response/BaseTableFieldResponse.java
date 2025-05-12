package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BaseTableFieldResponse implements Serializable {

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
    private Long length;

    /**
     * 字符串长度
     */
    private Long charLength;

    /**
     * 数字长度
     */
    private Long numberLength;
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

}
