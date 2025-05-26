package com.mysiteforme.admin.entity.aiEntity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FieldInfo implements Serializable {

    /**
     * 字段对应的业务名称
     */
    @JsonPropertyDescription("字段对应的业务名称。例如：'标题','内容','作者ID'")
    private String fieldBusinessName;

    /**
     * 字段名称
     */
    @JsonPropertyDescription("字段名称,规则是小写英文字母(单词之间用下划线`_`连接)。例如：'title','content','author_id'")
    private String fieldName;

    /**
     * 字段描述
     */
    @JsonPropertyDescription("对该字段的简短描述或用途说明。例如：'博客的标题','博客文章的正文内容'")
    private String fieldDescription;

    /**
     * 字段类型
     */
    @JsonPropertyDescription("字段的数据类型，例如 'VARCHAR(255)', 'TEXT', 'INT', 'BIGINT', 'BOOLEAN', 'TIMESTAMP', 'DATE'")
    private String dataType;

    /**
     * 是否必须
     */
    @JsonPropertyDescription("字段是否允许为空 (true/false), 默认为 true (可空)")
    private Boolean required;

    /**
     * 字段默认值
     */
    @JsonPropertyDescription("字段的默认值（如果有）。")
    private String defaultValue;

    /**
     * 字段规则
     */
    @JsonPropertyDescription("该字段对应的规则内容。例如：'长度限制为255字符','自动增长','主键'")
    private List<String> businessRules;
}
