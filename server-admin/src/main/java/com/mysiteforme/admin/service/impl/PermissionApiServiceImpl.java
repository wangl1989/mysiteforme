
package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionApiDao;
import com.mysiteforme.admin.entity.PermissionApi;
import com.mysiteforme.admin.service.PermissionApiService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionApiServiceImpl extends ServiceImpl<PermissionApiDao, PermissionApi> implements PermissionApiService {

    @Override
    public boolean checkApiUrlUnique(String apiUrl, String httpMethod, Long id) {
        LambdaQueryWrapper<PermissionApi> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PermissionApi::getApiUrl, apiUrl);
        queryWrapper.eq(PermissionApi::getHttpMethod, httpMethod);
        queryWrapper.eq(PermissionApi::getDelFlag,false);
        if (id != null) {
            queryWrapper.ne(PermissionApi::getId, id);
        }
        return baseMapper.selectCount(queryWrapper) == 0;
    }

}