package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.TableFieldConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TableFieldConfigDTO extends TableFieldConfig {

    private Integer page;
    private Integer limit;

}