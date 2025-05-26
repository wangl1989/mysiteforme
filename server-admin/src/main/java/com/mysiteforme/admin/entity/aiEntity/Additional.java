package com.mysiteforme.admin.entity.aiEntity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.io.Serializable;

@Data
public class Additional implements Serializable {

    private String chatId;

    @JsonPropertyDescription("基于经验推断的额外需求。例如:'建议添加文章状态字段（如草稿、已发布），以支持文章的生命周期管理。'")
    private String additionalRequirement;
}
