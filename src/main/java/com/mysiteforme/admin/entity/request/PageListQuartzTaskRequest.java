package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListQuartzTaskRequest extends BasePageRequest{

    private String name;

    private Integer status;

    private Boolean sortByCreateDateAsc;

    private Boolean sortByCreateDateDesc;
}
