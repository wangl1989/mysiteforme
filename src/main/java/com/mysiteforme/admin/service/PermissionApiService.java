package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionApi;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
public interface PermissionApiService extends IService<PermissionApi> {

    public void savePermissionApiDTO(PermissionApiDTO permissionApiDTO);
}
