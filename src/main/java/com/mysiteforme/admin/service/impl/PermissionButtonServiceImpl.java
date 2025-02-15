package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionButton;
import com.mysiteforme.admin.dao.PermissionButtonDao;
import com.mysiteforme.admin.service.PermissionButtonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;


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
public class PermissionButtonServiceImpl extends ServiceImpl<PermissionButtonDao, PermissionButton> implements PermissionButtonService {

    @Override
    public void savePermissionButtonDTO(PermissionButtonDTO permissionButtonDTO) {
        PermissionButton permissionButton = new PermissionButton();
        BeanUtils.copyProperties(permissionButtonDTO,permissionButton);
        baseMapper.insert(permissionButton);
    }
}
