package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddTableFieldRequest {

    @NotBlank(message = MessageConstants.Table.TABLE_FIELD_COLUMN_NAME_NOT_EMPTY)
    private String columnName;

    @NotNull(message = MessageConstants.Table.TABLE_FIELD_COLUMN_LENGTH_NOT_EMPTY)
    private Integer length;

    @NotBlank(message = MessageConstants.Table.TABLE_FIELD_COLUMN_TYPE_NOT_EMPTY)
    private String type;
    /**
     * 字段是否可以为空
     */
    @NotNull(message = MessageConstants.Table.TABLE_FIELD_ISNUllVALUE_TYPE_NOT_EMPTY)
    private Boolean isNullable;
    /**
     * 字段描述
     */
    @NotBlank(message = MessageConstants.Table.TABLE_FIELD_COLUMN_COMMENT_NOT_EMPTY)
    private String comment;

    @NotBlank(message = MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;

    @NotNull(message = MessageConstants.Table.TABLE_TYPE_NOT_NULL)
    private Integer tableType;
}
