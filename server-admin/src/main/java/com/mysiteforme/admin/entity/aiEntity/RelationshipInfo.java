package com.mysiteforme.admin.entity.aiEntity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.io.Serializable;

@Data
public class RelationshipInfo implements Serializable {

    private String chatId;

    /**
     * 当前表中的外键字段名称
     */
    @JsonPropertyDescription("源实体，例如 'blog_comments'")
    private String fromEntity;

    /**
     * 目标关联表的名称
     */
    @JsonPropertyDescription("目标关联表的名称，例如 'blog_user'")
    private String toEntity;

    /**
     * 关系类型
     */
    @JsonPropertyDescription("关系类型，例如 'ONE_TO_MANY', 'MANY_TO_ONE', 'MANY_TO_MANY'")
    private String relationshipType;

    /**
     * 关联描述
     */
    @JsonPropertyDescription("两张表之间的关联关系的详细介绍。例如：'每篇博客文章由一个用户撰写，一个用户可以撰写多篇博客文章。'")
    private String description;
}
