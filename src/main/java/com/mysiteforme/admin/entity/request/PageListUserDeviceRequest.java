package com.mysiteforme.admin.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListUserDeviceRequest extends BasePageRequest{

    private String userName;

    private String deviceId;

    private String deviceType;

    private String deviceName;

    private Boolean sortByCreateDateAsc;


}
