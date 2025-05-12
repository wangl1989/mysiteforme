package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDictTypeRequest {

    @NotBlank(message = MessageConstants.Dict.DICT_OLD_TYPE_EMPTY)
    private String oldType;

    @NotBlank(message = MessageConstants.Dict.DICT_NEW_TYPE_EMPTY)
    private String newType;
}
