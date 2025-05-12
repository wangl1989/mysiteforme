/**
 * @ Author: wangl
 * @ Create Time: 2025-02-18 12:18:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 12:31:54
 * @ Description:
 */
package com.mysiteforme.admin.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionPageDao;
import com.mysiteforme.admin.entity.PermissionPage;
import com.mysiteforme.admin.service.PermissionPageService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionPageServiceImpl extends ServiceImpl<PermissionPageDao, PermissionPage> implements PermissionPageService {

    @Override
    public PermissionPage getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public PermissionPage getByUrl(String url) {
        QueryWrapper<PermissionPage> wrapper = new QueryWrapper<>();
        wrapper.eq("page_url", url)
              .eq("del_flag", false);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkPageUrlUnique(String pageUrl, Long id) {
        QueryWrapper<PermissionPage> wrapper = new QueryWrapper<>();
        wrapper.eq("page_url", pageUrl)
              .eq("del_flag", false);
        if (id != null) {
            wrapper.ne("id", id);
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    
}