/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:29:48
 * @ Description: 任务执行日志Service
 */

package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.entity.request.PageListQuartzTaskLogRequest;

public interface QuartzTaskLogService extends IService<QuartzTaskLog> {

    IPage<QuartzTaskLog> selectPageQuartzTaskLog(PageListQuartzTaskLogRequest request);

}
