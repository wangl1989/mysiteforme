package com.mysiteforme.admin.service;

import com.mysiteforme.admin.entity.request.AddClickEventsRequest;
import com.mysiteforme.admin.entity.request.PageListVisitLogsRequest;
import com.mysiteforme.admin.entity.VisitLogs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * <p>
 * 访问记录表 服务类
 * </p>
 *
 * @author wangl1989
 * @since 2025-05-04
 */
public interface VisitLogsService extends IService<VisitLogs> {

    /**
     * 分页查询访问记录表
     */
    IPage<VisitLogs> selectPageVisitLogs(PageListVisitLogsRequest request);

    /**
     * 新增访问记录表
     */
    void createVisitLogs(VisitLogs visitLogs);

    /**
     * 新增点击事件
     */
    void createClickEvents(AddClickEventsRequest request);

}
