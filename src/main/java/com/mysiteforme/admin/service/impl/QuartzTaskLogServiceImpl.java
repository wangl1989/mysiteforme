/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:45
 * @ Description: 定时任务日志服务实现类 提供定时任务日志的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.dao.QuartzTaskLogDao;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskLogRequest;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskRequest;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class QuartzTaskLogServiceImpl extends ServiceImpl<QuartzTaskLogDao, QuartzTaskLog> implements QuartzTaskLogService {

    @Override
    public IPage<QuartzTaskLog> selectPageQuartzTaskLog(PageListQuartzTaskLogRequest request) {
        LambdaQueryWrapper<QuartzTaskLog> wrapper = new LambdaQueryWrapper<>();
        if(request != null){
            if(StringUtils.isNotBlank(request.getName())) {
                wrapper.like(QuartzTaskLog::getName, request.getName());
            }
            if(request.getSortByCreateDateDesc() != null){
                wrapper.orderByDesc(request.getSortByCreateDateDesc(),QuartzTaskLog::getCreateDate);
            }
            if(request.getSortByCreateDateAsc() != null){
                wrapper.orderByAsc(request.getSortByCreateDateAsc(),QuartzTaskLog::getCreateDate);
            }
        }else{
            request = new PageListQuartzTaskLogRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }
}
