package com.mysiteforme.admin.entity.VO;

import com.mysiteforme.admin.entity.UserDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfoVO extends UserDevice {

    private Boolean online;

    private Boolean currentDevice;

}
