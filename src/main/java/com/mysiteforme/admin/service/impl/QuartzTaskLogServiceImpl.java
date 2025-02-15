/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:24:45
 * @ Description: 定时任务日志服务实现类 提供定时任务日志的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.dao.QuartzTaskLogDao;
import com.mysiteforme.admin.service.QuartzTaskLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class QuartzTaskLogServiceImpl extends ServiceImpl<QuartzTaskLogDao, QuartzTaskLog> implements QuartzTaskLogService {

}
