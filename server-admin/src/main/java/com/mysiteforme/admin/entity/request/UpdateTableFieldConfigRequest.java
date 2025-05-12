package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTableFieldConfigRequest {

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;

    @NotBlank(message = MessageConstants.TableFieldConfig.BUSINESS_NAME_NOT_EMPTY)
    private String businessName;

    private String formComponentType;

    /**
     * 关联的类型 1.字典类型  2.关联表名
     */
    private Integer associatedType;

    /**
     * 关联表名称
     */
    private String associatedTable;

    /**
     * 关联表字段
     */
    private String associatedTableField;

    /**
     * 字典类型
     */
    private String associatedDictType;
    
    private Integer sort;

    private Boolean isNullable;

    private Boolean isUnique;
    
    // Other fields that can be updated
    private Boolean isListVisible;
    
    private Boolean isAddVisible;

    private Boolean isEditVisible;

    private Boolean isDetailVisible;

    private Boolean isQueryField;
    
    private String queryType;
    
    private String validationRules;

    private String remarks;
}
