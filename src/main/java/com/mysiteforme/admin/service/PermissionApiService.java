/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:28:50
 * @ Description: 权限API服务
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionApi;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;

public interface PermissionApiService extends IService<PermissionApi> {

    public void savePermissionApiDTO(PermissionApiDTO permissionApiDTO);
}
