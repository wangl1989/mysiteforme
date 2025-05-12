package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PageListDictRequest extends BasePageRequest {

    private String type;

    private String value;

    private String label;

    private String description;

    private Boolean sortByCreateDateAsc;

    private Boolean sortBySortAsc;
}
