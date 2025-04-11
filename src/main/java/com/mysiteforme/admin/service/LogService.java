/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:28:29
 * @ Description: 日志Service
 */

package com.mysiteforme.admin.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.entity.request.PageListSystemLogRequest;


public interface LogService extends IService<Log> {

    /**
     * 查询最近15天的日志数量统计
     * @return 每日日志数量列表
     */
    List<Integer> selectSelfMonthData();

    /**
     * 分页查询日志
     * @param request 查询参数
     * @return 分页查询结果
     */
    IPage<Log> selectPageLogs(PageListSystemLogRequest request);

}
