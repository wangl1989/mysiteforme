/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:09
 * @ Description: 资源服务实现类 提供资源的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysiteforme.admin.entity.request.PageListResourceRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.service.RescourceService;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RescourceServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements RescourceService {

    @Override
    public IPage<Rescource> selectPageRescource(PageListResourceRequest request) {
        LambdaQueryWrapper<Rescource> wrapper = new LambdaQueryWrapper<>();
        if(request != null) {
            if (StringUtils.isNotBlank(request.getFileType())) {
                wrapper.like(Rescource::getFileType, request.getFileType());
            }
            if (StringUtils.isNotBlank(request.getHash())) {
                wrapper.eq(Rescource::getHash, request.getHash());
            }
            if (StringUtils.isNotBlank(request.getSource())) {
                wrapper.eq(Rescource::getSource, request.getSource());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), Rescource::getCreateDate);
        }else{
            request = new PageListResourceRequest();
        }
        return baseMapper.selectPage(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

}
