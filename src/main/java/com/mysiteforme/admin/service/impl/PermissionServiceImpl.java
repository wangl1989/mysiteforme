package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.entity.DTO.PermissionDTO;
import com.mysiteforme.admin.entity.Permission;
import com.mysiteforme.admin.service.PermissionService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangl
 * @since 2025-02-14
 */
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
