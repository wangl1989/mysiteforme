package com.mysiteforme.admin.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class TableFieldDTO {

    private String columnName;

    private String columnType;

    private String columnComment;

    private Integer charLength;

    private Integer numberLength;

    private Boolean isNullable;

}
