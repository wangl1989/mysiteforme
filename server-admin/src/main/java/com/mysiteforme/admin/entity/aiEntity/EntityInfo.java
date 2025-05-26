package com.mysiteforme.admin.entity.aiEntity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EntityInfo implements Serializable {

    private String chatId;

    /**
     * 业务名称
     */
    @JsonPropertyDescription("数据库表对应的业务名称，需要简明扼要。例如：'博客文章','博客用户','博客评论'")
    private String businessName;

    /**
     * 实体名称
     */
    @JsonPropertyDescription("数据库表的名称，并且需要统一开头，且不是以'sys_'开头。例如 'blog_posts', 'blog_users', 'blog_comments'")
    private String entityName;

    /**
     * 实体描述
     */
    @JsonPropertyDescription("数据库表对应业务的详细介绍。例如：'存储博客文章的基本信息和内容'")
    private String entityDescription;

    /**
     * 字段集合
     */
    @JsonPropertyDescription("该表的字段列表")
    private List<FieldInfo> fields;
}
