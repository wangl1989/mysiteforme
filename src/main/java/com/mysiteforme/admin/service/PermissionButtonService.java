/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:08
 * @ Description: 权限按钮Service
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionButton;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;

public interface PermissionButtonService extends IService<PermissionButton> {

    public void savePermissionButtonDTO(PermissionButtonDTO permissionButtonDTO);
}
