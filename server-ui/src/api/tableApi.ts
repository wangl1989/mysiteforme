import request from '@/utils/http'
import { PageResult, BaseResult } from '@/types/axios'
import {
  TableRecordModel,
  TableListParam,
  AddTableParam,
  EditTableParam,
  TableFieldRecordModel,
  TableFieldListParam,
  ShowTableFieldListParam,
  AddTableFieldParam,
  EditTableFieldParam,
  FieldIsExistParam,
  DeleteFieldParam
} from './model/tableModel'

export class TableService {
  // 获取表格列表
  static getTableList(params: TableListParam) {
    return request.get<PageResult<TableRecordModel>>({
      url: '/api/admin/table/list',
      params
    })
  }

  // 新增表格
  static addTable(params: AddTableParam) {
    return request.post<BaseResult>({
      url: '/api/admin/table/add',
      params
    })
  }

  // 编辑表格
  static editTable(params: EditTableParam) {
    return request.put<BaseResult>({
      url: '/api/admin/table/edit',
      params
    })
  }

  // 删除表格
  static deleteTable(params: { tableName: string }) {
    return request.del<BaseResult>({
      url: '/api/admin/table/delete',
      params
    })
  }

  // 获取字段列表（分页）
  static getFieldList(params: TableFieldListParam) {
    return request.get<PageResult<TableFieldRecordModel>>({
      url: '/api/admin/table/getFieldList',
      params
    })
  }

  // 获取字段列表（不分页）
  static showFields(params: ShowTableFieldListParam) {
    return request.get<BaseResult<TableFieldRecordModel>>({
      url: '/api/admin/table/showFields',
      params
    })
  }

  // 新增字段
  static addField(params: AddTableFieldParam) {
    return request.post<BaseResult>({
      url: '/api/admin/table/addField',
      params
    })
  }

  // 编辑字段
  static editField(params: EditTableFieldParam) {
    return request.put<BaseResult>({
      url: '/api/admin/table/editField',
      params
    })
  }

  // 判断字段是否存在
  static fieldIsExist(params: FieldIsExistParam) {
    return request.get<BaseResult>({
      url: '/api/admin/table/fieldIsExist',
      params
    })
  }

  // 删除字段
  static deleteField(params: DeleteFieldParam) {
    return request.del<BaseResult>({
      url: '/api/admin/table/deleteField',
      params
    })
  }
}
