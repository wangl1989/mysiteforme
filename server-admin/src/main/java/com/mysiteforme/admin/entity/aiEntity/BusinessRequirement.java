package com.mysiteforme.admin.entity.aiEntity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.io.Serializable;

@Data
public class BusinessRequirement implements Serializable {

    private String chatId;

    @JsonPropertyDescription("模块的建议名称，例如 '博客模块'")
    private String moduleName;

    @JsonPropertyDescription("对模块内容的简要介绍：需要包含核心功能。例如 '该模块用于创建和管理博客内容，包括文章发布、编辑和展示功能。'")
    private String moduleDescription;

}
