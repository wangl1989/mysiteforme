package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FieldIsExistRequest {

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;

    @NotBlank(message = MessageConstants.Table.FIELD_NAME_NOT_EMPTY)
    private String fieldName;

    @NotBlank(message = MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;
}
