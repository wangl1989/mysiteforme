package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.PermissionPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
public interface PermissionPageService extends IService<PermissionPage> {

    public void savePermissionPageDTO(PermissionPageDTO permissionPageDTO);
}
