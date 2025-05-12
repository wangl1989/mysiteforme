import request from '@/utils/http'
import { PageResult } from '@/types/axios'
import { ResourcesRecord, ResourcesListParams } from './model/resourcesModel'

export class ResourcesService {
  // 获取资源分页列表
  static getResourcesList(params: ResourcesListParams) {
    return request.get<PageResult<ResourcesRecord>>({
      url: '/api/admin/rescource/list',
      params
    })
  }
}
