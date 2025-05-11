package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TableConfigResponse {

    private Long id;

    /**
     * 数据库中的表名
     */
    private String tableName;

    /**
     * 数据表类型：1. 普通数据表，2.树形结构数据表 3.关联表
     */
    private Integer tableType;

    /**
     * 需要移除的表格前缀
     */
    private String tablePrefix;
    /**
     * 数据库模式名，如果需要
     */
    private String schemaName;
    /**
     * 业务名称，如“用户管理”
     */
    private String businessName;
    /**
     * 生成代码所属模块
     */
    private String moduleName;
    /**
     * 生成代码的包路径
     */
    private String packageName;
    /**
     * 作者
     */
    private String author;
    /**
     * 代码生成路径
     */
    private String generatePath;
    /**
     * JSON格式，存储其他表级别的配置，如是否生成Controller、Service等
     */
    private String options;

    /**
     * 是否拥有sort字段
     */
    private Boolean hasSortField;

    /**
     * 是否为系统文件
     */
    private Boolean isSysFile;

    /**
     * 是否验证String字符串为空
     */
    private Boolean isValidateStringNull;

    /**
     * 是否验证对象为空
     */
    private Boolean isValidateObjectNull;

    /**
     * 是否验证正则字符串
     */
    private Boolean isValidateRuler;

    /**
     * 是否检测图片地址
     */
    private Boolean isCheckImgUrl;

    /**
     * 是否校验字典
     */
    private Boolean isDictList;

    /**
     * 新增时需要导入的包的集合
     */
    private Set<String> addPackages;

    /**
     * 编辑时需要导入的包的集合
     */
    private Set<String> updatePackages;

    /**
     * 列表时需要导入的包的集合
     */
    private Set<String> pageListPackages;

    /**
     * 字段列表
     */
    private List<TableFieldConfigResponse> fieldList;
}
