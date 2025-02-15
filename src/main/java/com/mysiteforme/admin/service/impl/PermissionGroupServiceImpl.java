/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 22:39:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:23:57
 * @ Description: 权限组服务实现类 提供权限组的服务
 */

package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionGroup;
import com.mysiteforme.admin.dao.PermissionGroupDao;
import com.mysiteforme.admin.service.PermissionGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionGroupDTO;



@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionGroupServiceImpl extends ServiceImpl<PermissionGroupDao, PermissionGroup> implements PermissionGroupService {

    @Override
    public void savePermissionGroupDTO(PermissionGroupDTO permissionGroupDTO) {
        PermissionGroup permissionGroup = new PermissionGroup();
        BeanUtils.copyProperties(permissionGroupDTO,permissionGroup);
        baseMapper.insert(permissionGroup);
    }
}
