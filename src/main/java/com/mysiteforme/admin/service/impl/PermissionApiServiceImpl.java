package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionApi;
import com.mysiteforme.admin.dao.PermissionApiDao;
import com.mysiteforme.admin.service.PermissionApiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;


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
public class PermissionApiServiceImpl extends ServiceImpl<PermissionApiDao, PermissionApi> implements PermissionApiService {

    @Override
    public void savePermissionApiDTO(PermissionApiDTO permissionApiDTO) {
        PermissionApi permissionApi = new PermissionApi();
        BeanUtils.copyProperties(permissionApiDTO,permissionApi);
        baseMapper.insert(permissionApi);
    }
}
