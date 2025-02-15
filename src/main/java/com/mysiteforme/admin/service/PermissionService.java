/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:39
 * @ Description: 权限Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionDTO;
import com.mysiteforme.admin.entity.Permission;

public interface PermissionService extends IService<Permission> {

    public void savePermissionDTO(PermissionDTO permissionDTO);

}
