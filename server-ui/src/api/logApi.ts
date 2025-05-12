import request from '@/utils/http'
import { PageResult, BaseResult } from '@/types/axios'
import { LogRecord, LogListParams } from './model/logModel'

export class LogService {
  // 获取资源分页列表
  static getLogList(params: LogListParams) {
    return request.get<PageResult<LogRecord>>({
      url: '/api/admin/log/list',
      params
    })
  }

  static deleteLog(ids: number[]) {
    const queryParams = ids?.map((id) => `ids=${id}`).join('&')
    return request.del<BaseResult>({
      url: `/api/admin/log/delete?${queryParams}`
    })
  }
}
