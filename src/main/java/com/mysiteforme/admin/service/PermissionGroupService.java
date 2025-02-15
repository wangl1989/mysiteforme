package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionGroupDTO;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
public interface PermissionGroupService extends IService<PermissionGroup> {

    public void savePermissionGroupDTO(PermissionGroupDTO permissionGroupDTO);
}
