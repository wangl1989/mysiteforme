package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageListResourceRequest extends BasePageRequest {
    
    private String fileType;

    private String hash;

    private String source;

    private Boolean sortByCreateDateAsc;

    private List<Long> ids;
}
