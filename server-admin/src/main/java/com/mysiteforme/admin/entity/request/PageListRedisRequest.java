package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListRedisRequest extends BasePageRequest {

    /**
     * Redis key pattern for filtering
     */
    private String keyPattern;

    /**
     * Sort by expiration time ascending
     */
    private Boolean sortByExpireAsc;

}
