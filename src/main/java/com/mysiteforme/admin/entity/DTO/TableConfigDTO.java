package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.TableConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TableConfigDTO extends TableConfig {

    private Integer page;
    private Integer limit;

}