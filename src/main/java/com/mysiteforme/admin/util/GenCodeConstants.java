package com.mysiteforme.admin.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenCodeConstants {

    public static final String BASE_PACKAGE = "com.mysiteforme.admin";

    public static final String WINDOWS_GENERATOR_PATH = "D://site";

    public static final String LINUX_GENERATOR_PATH = "/opt/site";

    public static final List<String> FIXED_TABLE_NAME = Arrays.asList("information_schema","performance_schema","mysql","sys");

    public static final List<String> TABLE_NAME_FILTER_PREFIX = Arrays.asList("sys_","qrtz_","quartz_");

    /**
     * 数据表通用字段
     */
    public static final List<String> DATA_TABLE_COMMON_FIELD = Arrays.asList("id","create_date","create_by","update_date","update_by","remarks","del_flag");

    /**
     * 树形结构数据表通用字段
     */
    public static final List<String> TREE_TABLE_COMMON_FIELD = Arrays.asList("id","parent_id","parent_ids","level","sort","create_date","create_by","update_date","update_by","remarks","del_flag");

    /**
     * 数据表类型：1. 普通数据表，
     */
    public static final Integer DATA_TABLE_TYPE = 1;

    /**
     * 树形结构数据表类型: 2.树形结构数据表
     */
    public static final Integer TREE_TABLE_TYPE = 2;

    /**
     * 表单组件类型列表
     */
    public static final List<String> FORM_COMPONENT_TYPES = Arrays.asList("INPUT",
            "INPUT_NUMBER",
            "TEXTAREA",
            "SELECT",
            "RADIO",
            "CHECKBOX",
            "DATE_PICKER",
            "DATETIME_PICKER",
            "TIME_PICKER",
            "COLOR_PICKER",
            "ICON_PICKER",
            "SWITCH",
            "FILE_UPLOAD",
            "IMAGE_UPLOAD",
            "RICH_TEXT");

    /**
     * 数据库类型与Java类型映射
     */
    public static final Map<String, String> DB_TO_JAVA_TYPE_MAP = new HashMap<>();
    static {
        // 数值类型
        DB_TO_JAVA_TYPE_MAP.put("tinyint", "Integer");
        DB_TO_JAVA_TYPE_MAP.put("smallint", "Integer");
        DB_TO_JAVA_TYPE_MAP.put("mediumint", "Integer");
        DB_TO_JAVA_TYPE_MAP.put("int", "Integer");
        DB_TO_JAVA_TYPE_MAP.put("integer", "Integer");
        DB_TO_JAVA_TYPE_MAP.put("bigint", "Long");
        DB_TO_JAVA_TYPE_MAP.put("float", "Float");
        DB_TO_JAVA_TYPE_MAP.put("double", "Double");
        DB_TO_JAVA_TYPE_MAP.put("decimal", "BigDecimal");

        // 字符串类型
        DB_TO_JAVA_TYPE_MAP.put("char", "String");
        DB_TO_JAVA_TYPE_MAP.put("varchar", "String");
        DB_TO_JAVA_TYPE_MAP.put("tinytext", "String");
        DB_TO_JAVA_TYPE_MAP.put("text", "String");
        DB_TO_JAVA_TYPE_MAP.put("mediumtext", "String");
        DB_TO_JAVA_TYPE_MAP.put("longtext", "String");

        // 日期时间类型
        DB_TO_JAVA_TYPE_MAP.put("date", "LocalDate");
        DB_TO_JAVA_TYPE_MAP.put("time", "LocalTime");
        DB_TO_JAVA_TYPE_MAP.put("datetime", "LocalDateTime");
        DB_TO_JAVA_TYPE_MAP.put("timestamp", "LocalDateTime");

        // 布尔类型
        DB_TO_JAVA_TYPE_MAP.put("bit", "Boolean");
        DB_TO_JAVA_TYPE_MAP.put("boolean", "Boolean");
    }

    /**
     * 数据库类型与表单组件类型映射
     */
    public static final  Map<String, FormComponentType> DB_TO_FORM_COMPONENT_TYPE_MAP = new HashMap<>();
    static {
        // 数值类型
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("tinyint", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("smallint", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("mediumint", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("int", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("integer", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("bigint", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("float", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("double", FormComponentType.INPUT_NUMBER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("decimal", FormComponentType.INPUT_NUMBER);

        // 字符串类型
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("char", FormComponentType.INPUT);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("varchar", FormComponentType.INPUT);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("tinytext", FormComponentType.INPUT);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("text", FormComponentType.TEXTAREA);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("mediumtext", FormComponentType.TEXTAREA);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("longtext", FormComponentType.TEXTAREA);

        // 日期时间类型
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("date", FormComponentType.DATE_PICKER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("time", FormComponentType.TIME_PICKER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("datetime", FormComponentType.DATETIME_PICKER);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("timestamp", FormComponentType.DATETIME_PICKER);

        // 布尔类型
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("bit", FormComponentType.SWITCH);
        DB_TO_FORM_COMPONENT_TYPE_MAP.put("boolean", FormComponentType.SELECT);
    }
}
