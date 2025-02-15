package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionButton;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
public interface PermissionButtonService extends IService<PermissionButton> {

    public void savePermissionButtonDTO(PermissionButtonDTO permissionButtonDTO);
}
