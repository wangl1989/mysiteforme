/**
 * @ Author: wangl
 * @ Create Time: 2025-02-18 12:19:06
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 12:38:16
 * @ Description:
 */
package com.mysiteforme.admin.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionButtonDao;
import com.mysiteforme.admin.entity.PermissionButton;
import com.mysiteforme.admin.service.PermissionButtonService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionButtonServiceImpl extends ServiceImpl<PermissionButtonDao, PermissionButton> implements PermissionButtonService {

    @Override
    public PermissionButton getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public PermissionButton getByButtonKey(String buttonKey) {
        QueryWrapper<PermissionButton> wrapper = new QueryWrapper<>();
        wrapper.eq("button_key", buttonKey)
              .eq("del_flag", false);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkButtonKeyUnique(String buttonKey, Long id) {
        QueryWrapper<PermissionButton> wrapper = new QueryWrapper<>();
        wrapper.eq("button_key", buttonKey)
              .eq("del_flag", false);
        if (id != null) {
            wrapper.ne("id", id);
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

}