import request from '@/utils/http'
import { BaseResult } from '@/types/axios'
import {
  VisitLogsData,
  EventLogsData,
  BatchEventLogsData,
  TodayStatsResponse,
  TrendStatsDataResponse,
  MonthlyStatsDataResponse,
  IndexUserCollectionResponse,
  IndexUserOperationLogResponse,
  IndexSystemDataResponse
} from './model/analyticsModel'

export class AnalyticsService {
  // 发送访问记录数据
  static postVisitLogs(params: VisitLogsData) {
    return request.post<BaseResult>({
      url: '/api/analytics/visitLogs',
      params
    })
  }

  // 发送用户点击事件数据
  static postEventLogs(params: EventLogsData) {
    return request.post<BaseResult>({
      url: '/api/analytics/eventLogs',
      params
    })
  }

  // 批量发送获取用户点击事件数据
  static batchEventLogs(params: BatchEventLogsData) {
    return request.post<BaseResult>({
      url: '/api/analytics/batchEventLogs',
      params
    })
  }

  // 获取今日实时统计
  static getTodayStats() {
    return request.get<BaseResult<TodayStatsResponse>>({
      url: '/api/admin/dailyStats/today'
    })
  }

  // 获取N天之前到现在的每天访问数据
  static getTrendStats(params: number) {
    return request.get<BaseResult<TrendStatsDataResponse>>({
      url: `/api/admin/dailyStats/day/trend`,
      params: { days: params }
    })
  }

  // 获取年度每月访问数据
  static getMonthlyStats() {
    return request.get<BaseResult<MonthlyStatsDataResponse>>({
      url: '/api/admin/dailyStats/month/trend'
    })
  }

  // 获取首页用户集合
  static getIndexUserCollection(params: number) {
    return request.get<BaseResult<IndexUserCollectionResponse[]>>({
      url: '/api/admin/dailyStats/user/list',
      params: { limit: params }
    })
  }

  // 获取首页用户操作日志集合
  static getIndexUserOperationLog(params: number) {
    return request.get<BaseResult<IndexUserOperationLogResponse[]>>({
      url: '/api/admin/dailyStats/log/list',
      params: { limit: params }
    })
  }

  // 获取首页用户系统数据内容
  static getIndexSystemData() {
    return request.get<BaseResult<IndexSystemDataResponse>>({
      url: '/api/admin/dailyStats/user/systemData'
    })
  }
}
