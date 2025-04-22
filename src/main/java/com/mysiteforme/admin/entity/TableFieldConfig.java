package com.mysiteforme.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mysiteforme.admin.base.DataEntity;

import java.io.Serial;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_table_field_config")
public class TableFieldConfig extends DataEntity {

    @Serial
	private static final long serialVersionUID = 1L;

    /**
     * 表配置ID
     */
	@TableField("table_config_id")
	private Long tableConfigId;
    /**
     * 数据库中的字段名
     */
	@TableField("column_name")
	private String columnName;

	/**
	 * 业务名称
	 */
	@TableField("business_name")
	private String businessName;
    /**
     * 数据库字段类型
     */
	@TableField("column_type")
	private String columnType;
    /**
     * 原始字段注释，可作为参考或默认描述
     */
	@TableField("column_comment")
	private String columnComment;

	/**
	 * 字段长度
	 */
	@TableField("column_length")
	private Integer columnLength;
    /**
     * 映射的Java类型，如String
     */
	@TableField("java_type")
	private String javaType;
    /**
     * 映射的Java属性名，如userName
     */
	@TableField("java_field_name")
	private String javaFieldName;
	/**
	 * 是否唯一
	 */
	@TableField("is_unique")
	private Boolean isUnique;
    /**
     * 是否必填，用于表单校验
     */
	@TableField("is_nullable")
	private Boolean isNullable;
    /**
     * 是否在列表页显示
     */
	@TableField("is_list_visible")
	private Boolean isListVisible;
    /**
     * 是否在新增表单页显示
     */
	@TableField("is_add_visible")
	private Boolean isAddVisible;

	/**
	 * 是否在编辑表单中展示
	 */
	@TableField("is_edit_visible")
	private Boolean isEditVisible;

	/**
	 * 是否在详情页展示
	 */
	@TableField("is_detail_visible")
	private Boolean isDetailVisible;
    /**
     * 是否作为查询条件
     */
	@TableField("is_query_field")
	private Boolean isQueryField;
    /**
     * (查询方式，如 '='
     */
	@TableField(value = "query_type", updateStrategy = FieldStrategy.ALWAYS)
	private String queryType;
    /**
     * 前端表单组件类型，如 'Input'
     */
	@TableField(value = "form_component_type", updateStrategy = FieldStrategy.ALWAYS)
	private String formComponentType;
    /**
     * 如果组件是Select/Radio/Checkbox，关联的字典类型，用于获取选项
     */
	@TableField(value = "associated_dict_type", updateStrategy = FieldStrategy.ALWAYS)
	private String associatedDictType;

	/**
	 * 关联的类型 1.字典类型  2.关联表名
	 */
	@TableField(value = "associated_type", updateStrategy = FieldStrategy.ALWAYS)
	private Integer associatedType;

	/**
	 * 关联表名称
	 */
	@TableField(value = "associated_table", updateStrategy = FieldStrategy.ALWAYS)
	private String associatedTable;

	/**
	 * 关联表字段
	 */
	@TableField(value = "associated_table_field", updateStrategy = FieldStrategy.ALWAYS)
	private String associatedTableField;
    /**
     * 字段在表单/列表中的显示顺序
     */
	@TableField("sort")
	private Integer sort;
    /**
     * JSON格式，存储更复杂的校验规则
     */
	@TableField(value = "validation_rules", updateStrategy = FieldStrategy.ALWAYS)
	private String validationRules;



}
