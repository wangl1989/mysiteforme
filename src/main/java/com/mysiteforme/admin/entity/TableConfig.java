package com.mysiteforme.admin.entity;

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
@TableName("sys_table_config")
public class TableConfig extends DataEntity {

    @Serial
	private static final long serialVersionUID = 1L;

    /**
     * 数据库中的表名
     */
	@TableField("table_name")
	private String tableName;

	/**
	 * 数据表类型：1. 普通数据表，2.树形结构数据表
	 */
	@TableField("table_type")
	private Integer tableType;

	/**
	 * 需要移除的表格前缀
	 */
	@TableField("table_prefix")
	private String tablePrefix;
    /**
     * 数据库模式名，如果需要
     */
	@TableField("schema_name")
	private String schemaName;
    /**
     * 业务名称，如“用户管理”
     */
	@TableField("business_name")
	private String businessName;
    /**
     * 生成代码所属模块
     */
	@TableField("module_name")
	private String moduleName;
    /**
     * 生成代码的包路径
     */
	@TableField("package_name")
	private String packageName;
    /**
     * 作者
     */
	@TableField("author")
	private String author;
    /**
     * 代码生成路径
     */
	@TableField("generate_path")
	private String generatePath;
    /**
     * JSON格式，存储其他表级别的配置，如是否生成Controller、Service等
     */
	@TableField("options")
	private String options;



}
