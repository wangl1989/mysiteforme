package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListTableFieldsRequest extends BasePageRequest{

    /**
     * 表格配置ID
     */
    @NotNull(message = MessageConstants.TableFieldConfig.TABLE_CONFIG_ID_NOT_NULL)
    private Long tableConfigId;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 表单类型
     */
    private String formComponentType;

    /**
     * 是否作为查询条件
     */
    private Boolean isQueryField;

}
