/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:43:43
 * @ Description: 定时任务数据层接口 提供定时任务的增删改查功能
 */

package com.mysiteforme.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysiteforme.admin.entity.QuartzTask;


public interface QuartzTaskDao extends BaseMapper<QuartzTask> {

}
