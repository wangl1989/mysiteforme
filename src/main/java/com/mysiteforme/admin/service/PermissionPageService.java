/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:25
 * @ Description: 权限页面Service
 */

package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;

public interface PermissionPageService extends IService<PermissionPage> {

    public void savePermissionPageDTO(PermissionPageDTO permissionPageDTO);
}
