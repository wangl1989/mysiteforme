/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:15
 * @ Description: 权限服务实现类 提供权限的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.entity.DTO.PermissionDTO;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.service.PermissionService;


@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

    @Override
    public void savePermissionDTO(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO,permission);
        baseMapper.insert(permission);
    }

}
