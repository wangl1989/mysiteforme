/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:39:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:16
 * @ Description: 权限组Service
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionGroupDTO;

public interface PermissionGroupService extends IService<PermissionGroup> {

    public void savePermissionGroupDTO(PermissionGroupDTO permissionGroupDTO);
}
