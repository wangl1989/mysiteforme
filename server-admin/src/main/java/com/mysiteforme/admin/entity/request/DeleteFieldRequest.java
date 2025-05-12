package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteFieldRequest {

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String tableName;

    @NotBlank(message = MessageConstants.Table.FIELD_NAME_NOT_EMPTY)
    private String fieldName;

}
