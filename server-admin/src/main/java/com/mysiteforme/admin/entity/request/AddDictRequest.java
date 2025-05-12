package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDictRequest {

    /**
     * 数据值
     */
    @NotBlank(message = MessageConstants.Dict.DICT_VALUE_EMPTY)
    private String value;
    /**
     * 标签名
     */
    @NotBlank(message = MessageConstants.Dict.DICT_LABEL_EMPTY)
    private String label;
    /**
     * 类型
     */
    @NotBlank(message = MessageConstants.Dict.DICT_TYPE_EMPTY)
    private String type;
    /**
     * 描述
     */
    private String description;
    /**
     * 排序（升序）
     */
    private Integer sort;

}
