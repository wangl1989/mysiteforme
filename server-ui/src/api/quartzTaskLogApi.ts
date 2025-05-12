import request from '@/utils/http'
import { BaseResult, PageResult } from '@/types/axios'
import { QuartzTaskRecordModel, QuartzTaskLogListParams } from './model/quartzTaskLogModel'

export class QuartzTaskLogService {
  // 获取定时任务日志分页列表
  static getQuartzTaskLogPageList(params: QuartzTaskLogListParams) {
    return request.get<PageResult<QuartzTaskRecordModel>>({
      url: '/api/admin/quartzTaskLog/list',
      params
    })
  }

  // 删除定时任务日志
  static deleteQuartzTaskLog(params: { id: number }) {
    return request.del<BaseResult<QuartzTaskRecordModel>>({
      url: '/api/admin/quartzTaskLog/delete',
      params
    })
  }
}
