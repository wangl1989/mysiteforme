
package com.mysiteforme.admin.service.impl;

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
        QueryWrapper<PermissionApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("api_url", apiUrl);
        queryWrapper.eq("http_method", httpMethod);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        return baseMapper.selectCount(queryWrapper) == 0;
    }

}