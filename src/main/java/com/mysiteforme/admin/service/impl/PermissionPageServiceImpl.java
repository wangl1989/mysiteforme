/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 13:55:47
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:05
 * @ Description: 权限页面服务实现类 提供权限页面的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysiteforme.admin.entity.PermissionPage;
import com.mysiteforme.admin.dao.PermissionPageDao;
import com.mysiteforme.admin.service.PermissionPageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;



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
