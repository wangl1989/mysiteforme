package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Log;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统日志 服务类
 * </p>
 *
 * @author wangl
 * @since 2018-01-13
 */
public interface LogService extends IService<Log> {

    /**
     * 查询最近15天的日志数量统计
     * @return 每日日志数量列表
     */
    List<Integer> selectSelfMonthData();

}
