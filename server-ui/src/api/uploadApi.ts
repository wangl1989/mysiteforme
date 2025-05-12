import request from '@/utils/http'
import { PageResult, BaseResult } from '@/types/axios'
import {
  UploadListParams,
  UploadRecord,
  AddUploadBaseInfoParam,
  EditUploadBaseInfoParam,
  UploadFormData,
  UploadResponse
} from './model/uploadModel'

export class UploadService {
  static upload(params: UploadFormData) {
    return request.post<BaseResult<UploadResponse>>({
      url: '/api/admin/file/upload',
      data: params,
      transformRequest: [
        (data) => {
          // 对于FormData，不进行任何转换，直接返回
          return data
        }
      ]
    })
  }

  // 获取上传基础信息分页列表
  static getUploadBaseInfoList(params: UploadListParams) {
    return request.get<PageResult<UploadRecord>>({
      url: '/api/admin/uploadBaseInfo/list',
      params
    })
  }

  /**
   * 删除基本上传信息
   * @param params 上传信息ID
   */
  static deleteUploadBaseInfo(params: number) {
    return request.del<BaseResult>({
      url: '/api/admin/uploadBaseInfo/delete',
      params
    })
  }

  static addUploadBaseInfo(params: AddUploadBaseInfoParam) {
    return request.post<BaseResult>({
      url: '/api/admin/uploadBaseInfo/add',
      params
    })
  }

  static updateUploadBaseInfo(params: EditUploadBaseInfoParam) {
    return request.put<BaseResult>({
      url: '/api/admin/uploadBaseInfo/edit',
      params
    })
  }
}
