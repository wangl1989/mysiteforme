/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 11:43:29
 * @ Description: 权限Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.entity.VO.PermissionApiVO;
import com.mysiteforme.admin.entity.request.BasePermissionRequest;
import com.mysiteforme.admin.entity.request.PageListPermissionRequest;
import com.mysiteforme.admin.exception.MyException;

public interface PermissionService extends IService<Permission> {

    IPage<Permission> selectPagePermission(PageListPermissionRequest request);

    Boolean checkPermissionCode(Long id,String permissionCode);

    void saveOrUpdatePermission(BasePermissionRequest request);

    void deletePermission(Long id) throws MyException;

    // 根据url和method获取接口信息
    PermissionApiVO getApiByUrlAndMethod(String url,String method);

    // API权限相关方法
    void saveOrUpdatePermissionApiDTO(PermissionApiDTO permissionApiDTO);
    
    // 页面权限相关方法
    void saveOrUpdatePermissionPageDTO(PermissionPageDTO permissionPageDTO);
    
    // 按钮权限相关方法
    void saveOrUpdatePermissionButtonDTO(PermissionButtonDTO permissionButtonDTO);

}
