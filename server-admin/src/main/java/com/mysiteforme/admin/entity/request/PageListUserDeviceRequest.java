package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListUserDeviceRequest extends BasePageRequest{

    private Long userId;

    private String deviceId;

    private Boolean sortByCreateDateAsc;


}
