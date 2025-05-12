import request from '@/utils/http'
import { BaseResult, PageResult } from '@/types/axios'
import {
  QuartzTaskRecordModel,
  QuartzTaskListParams,
  AddQuartzTaskParams,
  EditQuartzTaskParams
} from './model/quartzTaskModel'

export class QuartzTaskService {
  // 获取定时任务分页列表
  static getQuartzTaskPageList(params: QuartzTaskListParams) {
    return request.get<PageResult<QuartzTaskRecordModel>>({
      url: '/api/admin/quartzTask/list',
      params
    })
  }

  // 新增定时任务
  static addQuartzTask(params: AddQuartzTaskParams) {
    return request.post<BaseResult>({
      url: '/api/admin/quartzTask/add',
      params
    })
  }

  // 编辑定时任务
  static editQuartzTask(params: EditQuartzTaskParams) {
    return request.put<BaseResult>({
      url: '/api/admin/quartzTask/edit',
      params
    })
  }

  // 删除定时任务
  static deleteQuartzTask(ids: number[]) {
    const queryParams = ids?.map((id) => `ids=${id}`).join('&')
    return request.del<BaseResult>({
      url: `/api/admin/quartzTask/delete?${queryParams}`
    })
  }

  // 暂停选中的定时任务
  static paushQuartzTask(ids: number[]) {
    const queryParams = ids?.map((id) => `ids=${id}`).join('&')
    return request.post<BaseResult>({
      url: `/api/admin/quartzTask/paush?${queryParams}`
    })
  }

  // 恢复选中的定时任务运行
  static resumeQuartzTask(ids: number[]) {
    const queryParams = ids?.map((id) => `ids=${id}`).join('&')
    return request.post<BaseResult>({
      url: `/api/admin/quartzTask/resume?${queryParams}`
    })
  }

  // 立即执行选中的定时任务
  static runQuartzTask(ids: number[]) {
    const queryParams = ids?.map((id) => `ids=${id}`).join('&')
    return request.post<BaseResult>({
      url: `/api/admin/quartzTask/run?${queryParams}`
    })
  }
}
