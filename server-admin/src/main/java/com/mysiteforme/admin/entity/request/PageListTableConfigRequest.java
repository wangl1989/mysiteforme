package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListTableConfigRequest extends BasePageRequest{

    private String tableName;

    private Integer tableType;

    private String schemaName;

    private String businessName;

    private Boolean delFlag;

    private Boolean sortByUpdateDateAsc = true;

}
