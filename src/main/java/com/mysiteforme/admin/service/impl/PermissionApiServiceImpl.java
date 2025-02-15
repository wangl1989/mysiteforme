/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:34
 * @ Description: 权限API服务实现类 提供权限API的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionApi;
import com.mysiteforme.admin.dao.PermissionApiDao;
import com.mysiteforme.admin.service.PermissionApiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionApiServiceImpl extends ServiceImpl<PermissionApiDao, PermissionApi> implements PermissionApiService {

    @Override
    public void savePermissionApiDTO(PermissionApiDTO permissionApiDTO) {
        PermissionApi permissionApi = new PermissionApi();
        BeanUtils.copyProperties(permissionApiDTO,permissionApi);
        baseMapper.insert(permissionApi);
    }
}
