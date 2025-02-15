package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionDTO;
import com.mysiteforme.admin.entity.Permission;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
public interface PermissionService extends IService<Permission> {

    public void savePermissionDTO(PermissionDTO permissionDTO);

}
