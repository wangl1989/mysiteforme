package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetTableFieldConfigByTableNameRequest {

    @NotBlank(message = MessageConstants.TableFieldConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;

    @NotBlank(message = MessageConstants.TableFieldConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;
}
