package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionPage;
import com.mysiteforme.admin.dao.PermissionPageDao;
import com.mysiteforme.admin.service.PermissionPageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;


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
public class PermissionPageServiceImpl extends ServiceImpl<PermissionPageDao, PermissionPage> implements PermissionPageService {

    @Override
    public void savePermissionPageDTO(PermissionPageDTO permissionPageDTO) {
        PermissionPage permissionPage = new PermissionPage();
        BeanUtils.copyProperties(permissionPageDTO,permissionPage);
        baseMapper.insert(permissionPage);
    }
}
