package com.mysiteforme.admin.entity.response;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class TableFieldConfigResponse {
    /**
     * 表配置ID
     */
    private Long tableConfigId;
    /**
     * 数据库中的字段名
     */
    private String columnName;

    /**
     * 业务名称
     */
    private String businessName;
    /**
     * 数据库字段类型
     */
    private String columnType;
    /**
     * 原始字段注释，可作为参考或默认描述
     */
    private String columnComment;

    /**
     * 字段长度
     */
    private Long columnLength;
    /**
     * 映射的Java类型，如String
     */
    private String javaType;
    /**
     * 映射的Java属性名，如userName
     */
    private String javaFieldName;
    /**
     * 是否唯一
     */
    private Boolean isUnique;
    /**
     * 是否必填，用于表单校验
     */
    private Boolean isNullable;
    /**
     * 是否在列表页显示
     */
    private Boolean isListVisible;
    /**
     * 是否在新增表单页显示
     */
    private Boolean isAddVisible;

    /**
     * 是否在编辑表单中展示
     */
    private Boolean isEditVisible;

    /**
     * 是否在详情页展示
     */
    private Boolean isDetailVisible;
    /**
     * 是否作为查询条件
     */
    private Boolean isQueryField;
    /**
     * (查询方式，如 '='
     */
    private String queryType;
    /**
     * 前端表单组件类型，如 'Input'
     */
    private String formComponentType;
    /**
     * 如果组件是Select/Radio/Checkbox，关联的字典类型，用于获取选项
     */
    private String associatedDictType;

    /**
     * 关联的类型 1.字典类型  2.关联表名
     */
    private Integer associatedType;

    /**
     * 关联表名称
     */
    private String associatedTable;

    /**
     * 关联表字段
     */
    private String associatedTableField;
    /**
     * 字段在表单/列表中的显示顺序
     */
    private Integer sort;
    /**
     * JSON格式，存储更复杂的校验规则
     */
    private String validationRules;
}
