import request from '@/utils/http'
import { BaseResult } from '@/types/axios'

import { SystemRecord, SiteUploadTypeResponse } from './model/systemModel'

export class SystemService {
  // 获取系统信息
  static systemRecord() {
    return request.get<BaseResult<SystemRecord>>({
      url: '/api/admin/site/current'
    })
  }

  // 获取上传信息集合
  static uploadTypeList() {
    return request.get<BaseResult<SiteUploadTypeResponse>>({
      url: '/api/admin/site/uploadTypeList'
    })
  }

  // 编辑系统信息
  static editSystem(params: SystemRecord) {
    return request.put<BaseResult>({
      url: '/api/admin/site/edit',
      params
    })
  }
}
