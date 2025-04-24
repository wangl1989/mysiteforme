package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTableRequest {

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;

    private String oldTableName;

    private String tableName;

    private String comment;
}
