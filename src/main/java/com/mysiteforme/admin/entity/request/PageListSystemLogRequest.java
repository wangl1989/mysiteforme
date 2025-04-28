package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PageListSystemLogRequest extends BasePageRequest {

    private String type;
    private String title;
    private String username;
    private String httpMethod;
    
    private Boolean sortByCreateDateAsc;
}
