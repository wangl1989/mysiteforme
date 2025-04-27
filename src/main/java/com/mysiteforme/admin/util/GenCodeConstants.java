package com.mysiteforme.admin.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenCodeConstants {

    /**
     * 默认基础包名
     */
    public static final String BASE_PACKAGE = "com.mysiteforme.admin";

    /**
     * Windows生成路径
     */
    public static final String WINDOWS_GENERATOR_PATH = "D://site";

    /**
     * Linux生成路径
     */
    public static final String LINUX_GENERATOR_PATH = "/tmp/site";

    /**
     * 默认作者
     */
    public static final String DEFAULT_AUTHOR = "wangl";

    /**
     * 默认的实体类包名
     */
    public static final String DEFAULT_ENTITY_PACKAGE_NAME = "entity";

    /**
     * 默认的Controller包名
     */
    public static final String DEFAULT_CONTROLLER_PACKAGE_NAME = "controller";

    /**
     * 系统controller包名
     */
    public static final String DEFAULT_SYSTEM_CONTROLLER_PACKAGE_NAME = "controller.system";

    /**
     * 默认的Service包名
     */
    public static final String DEFAULT_SERVICE_PACKAGE_NAME = "service";

    /**
     * 默认的ServiceImpl包名(路径)
     */
    public static final String DEFAULT_SERVICE_IMPL_PACKAGE_NAME = "service.impl";

    /**
     * 默认的Mapper包名
     */
    public static final String DEFAULT_MAPPER_PACKAGE_NAME = "dao";

    /**
     * 默认的Mapper XML包名（路径）
     */
    public static final String DEFAULT_MAPPER_XML_PACKAGE_NAME = "mapper";

    /**
     * 默认的Mapper XML路径
     */
    public static final String DEFAULT_MAPPER_XML_PATH = "/resources/mapper/";

    /**
     * 需要移除的默认的表前缀
     */
    public static final String DEFAULT_TABLE_PREFIX = "sys_";

    /**
     * controller文件重命名
     */
    public static final String DEFAULT_FORMAT_CONTROLLER_FILE_NAME = "%sController";

    /**
     * service文件重命名
     */
    public static final String DEFAULT_FORMAT_SERVICE_FILE_NAME = "%sService";

    /**
     * serviceImpl文件重命名
     */
    public static final String DEFAULT_FORMAT_SERVICE_IMPL_FILE_NAME = "%sServiceImpl";

    /**
     * mapper文件重命名
     */
    public static final String DEFAULT_FORMAT_MAPPER_FILE_NAME = "%sDao";

    /**
     * mapper xml文件重命名
     */
    public static final String DEFAULT_FORMAT_MAPPER_XML_FILE_NAME = "%sMapper";

    /**
     * 默认的实体类Entity模板路径
     */
    public static final String DEFAULT_ENTITY_TEMPLATE_PATH = "/templates/vm/entity.java.vm";

    /**
     * 默认的控制器Controller模板路径
     */
    public static final String DEFAULT_CONTROLLER_TEMPLATE_PATH = "/templates/vm/controller.java.vm";

    /**
     * 默认的Service模板路径
     */
    public static final String DEFAULT_SERVICE_TEMPLATE_PATH = "/templates/vm/service.java.vm";

    /**
     * 默认的ServiceImpl模板路径
     */
    public static final String DEFAULT_SERVICE_IMPL_TEMPLATE_PATH = "/templates/vm/serviceImpl.java.vm";

    /**
     * 默认的Mapper模板路径
     */
    public static final String DEFAULT_MAPPER_TEMPLATE_PATH = "/templates/vm/mapper.java.vm";

    /**
     * 默认的Mapper XML模板路径
     */
    public static final String DEFAULT_MAPPER_XML_TEMPLATE_PATH = "/templates/vm/mapper.xml.vm";

    /**
     * 注入的表配置数据key前缀
     */
    public static final String INJECT_TABLE_CONFIG_DATA_KEY_PREFIX = "table_config_";

    /**
     * 注入的表字段配置数据key
     */
    public static final String INJECT_TABLE_FIELD_CONFIG_DATA_KEY_PREFIX = "field_config_";

    /**
     * sort字段字符串
     */
    public static final String FIELD_SORT_STRING = "sort";

    /**
     * 自定义生成java类型的文件
     */
    public static final String CUSTOM_JAVA_FILE_NAME = ".java";
    /**
     * 自定义生成ts类型的文件
     */
    public static final String CUSTOM_TS_FILE_NAME = ".ts";

    /**
     * 自定义生成vue类型的文件
     */
    public static final String CUSTOM_VUE_FILE_NAME = ".vue";

    /**
     * 默认request文件生成路径
     */
    public static final String DEFAULT_REQUEST_FILE_PATH = "/entity/request";

    /**
     * 分页列表参数
     */
    public static final String DEFAULT_FORMAT_PAGE_LIST_REQUEST_NAME = "PageList%sRequest";

    /**
     * 分页列表参数模板路径
     */
    public static final String DEFAULT_PAGE_LIST_REQUEST_TEMPLATE_PATH = "/templates/vm/pageListRequest.java.vm";

    /**
     * 新增对象文件名称
     */
    public static final String DEFAULT_FORMAT_ADD_REQUEST_NAME = "Add%sRequest";

    /**
     * 新增对象文件模板路径
     */
    public static final String DEFAULT_ADD_REQUEST_TEMPLATE_PATH = "/templates/vm/addRequest.java.vm";

    /**
     * 编辑对象文件名称
     */
    public static final String DEFAULT_FORMAT_UPDATE_REQUEST_NAME = "Update%sRequest";

    /**
     * 编辑对象文件模板路径
     */
    public static final String DEFAULT_UPDATE_REQUEST_TEMPLATE_PATH = "/templates/vm/updateRequest.java.vm";

    /**
     * 前端model文件重命名
     */
    public static final String DEFAULT_FORMAT_FRONT_MODEL_FILE_NAME = "%sModel";

    /**
     * 前端model文件生成路径
     */
    public static final String DEFAULT_FRONT_MODEL_FILE_PATH = "/front/src/api/model";

    /**
     * 前端model文件生成模板
     */
    public static final String DEFAULT_FRONT_MODEL_TEMPLATE_PATH = "/templates/vm/model.ts.vm";

    /**
     * 前端api文件重命名
     */
    public static final String DEFAULT_FORMAT_FRONT_API_FILE_NAME = "%sApi";

    /**
     * 前端api文件生成路径
     */
    public static final String DEFAULT_FRONT_API_FILE_PATH = "/front/src/api";

    /**
     * 前端api文件生成模板
     */
    public static final String DEFAULT_FRONT_API_TEMPLATE_PATH = "/templates/vm/api.ts.vm";

    /**
     * 前端视图文件生成的基础路径
     */
    public static final String DEFAULT_FRONT_VIEW_FILE_PATH = "/front/src/views";

    /**
     * 前端视图文件生成模板
     */
    public static final String DEFAULT_FRONT_VIEW_TEMPLATE_PATH = "/templates/vm/view.vue.vm";

    /**
     * 需要移除展示的固定表名，可扩展
     */
    public static final List<String> FIXED_TABLE_NAME = Arrays.asList("information_schema","performance_schema","mysql","sys");

    /**
     * 需要移除展示的表名前缀，可扩展
     */
    public static final List<String> TABLE_NAME_FILTER_PREFIX = Arrays.asList("sys_","qrtz_","quartz_");

    /**
     * 需要移除展示数据表通用字段
     */
    public static final List<String> DATA_TABLE_COMMON_FIELD = Arrays.asList("id","create_date","create_by","update_date","update_by","remarks","del_flag");

    /**
     * 需要移除展示树形结构数据表通用字段
     */
    public static final List<String> TREE_TABLE_COMMON_FIELD = Arrays.asList("id","parent_id","parent_ids","level","sort","create_date","create_by","update_date","update_by","remarks","del_flag");

    /**
     * 数据库字符串类型
     */
    public static final List<String> DB_STRING_FIELD = Arrays.asList("char","varchar","tinytext","text","mediumtext","longtext");

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

    /**
     * Java关键字
     */
    public static final String[] JAVA_KEY_WORKS = {"public","protected","private","protected","class","interface","abstract","implements","extends","new",
            "import","package","byte","char","boolean","short","int","float","long","double","void","null","true","false","if","else","while","for","switch",
            "case","default","do","break","continue","return","instanceof","static","final","super","this","native","strictfp","synchronized","transient","volatile",
            "catch","try","finally","throw","throws","enum","assert","throw","throws","this"};
}
