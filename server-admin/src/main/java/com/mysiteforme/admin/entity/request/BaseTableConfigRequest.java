package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseTableConfigRequest {

    @NotBlank(message = MessageConstants.TableConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;

    @NotBlank(message = MessageConstants.TableConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;

    private String tablePrefix;

    @NotBlank(message = MessageConstants.TableConfig.BUSINESS_NAME_NOT_EMPTY)
    private String businessName;

    private String moduleName;

    private String packageName;

    private String author;

    private String generatePath;

    private String options;

    private String remarks;

}
