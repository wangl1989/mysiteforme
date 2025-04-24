package com.mysiteforme.admin.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class TableFieldDTO {

    private String columnName;

    private String columnType;

    private String columnComment;

    private Long charLength;

    private Long numberLength;

    private Boolean isNullable;

}
