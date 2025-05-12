import request from '@/utils/http'
import { PageResult, BaseResult } from '@/types/axios'
import {
  TableConfigModel,
  TableConfigListParams,
  AddTableConfigParams,
  EditTableConfigParams,
  EditTableFieldConfigParams,
  SimpleTableField,
  TableFieldConfigModel,
  SyncTableFieldListParams,
  TableFieldConfigListParams,
  getSimpleTableFieldParam
} from './model/tableConfigModel'

export class TableService {
  // 下载源码
  static downloadCode(params: { ids: number[] }) {
    return request.post<Blob>({
      url: '/api/admin/tableConfig/downloadCode',
      params,
      responseType: 'blob'
    })
  }

  // 获取表格配置列表
  static getTableConfigList(params: TableConfigListParams) {
    return request.get<PageResult<TableConfigModel>>({
      url: '/api/admin/tableConfig/list',
      params
    })
  }

  // 新增表格配置
  static addTableConfig(params: AddTableConfigParams) {
    return request.post<BaseResult>({
      url: '/api/admin/tableConfig/add',
      params
    })
  }

  // 编辑表格配置
  static editTableConfig(params: EditTableConfigParams) {
    return request.put<BaseResult>({
      url: '/api/admin/tableConfig/edit',
      params
    })
  }

  // 删除表格配置
  static deleteTableConfig(id: number) {
    return request.del<BaseResult>({
      url: `/api/admin/tableConfig/delete?id=${id}`
    })
  }

  // 恢复表格配置
  static recoverTableConfig(id: number) {
    return request.post<BaseResult>({
      url: `/api/admin/tableConfig/recover?id=${id}`
    })
  }

  // 彻底删除表格配置
  static completelyDeleteTableConfig(id: number) {
    return request.del<BaseResult>({
      url: `/api/admin/tableConfig/completelyDelete?id=${id}`
    })
  }

  // 获取表名列表
  // params: 数据库名称
  static getTableNameList(params: { schemaName: string }) {
    return request.get<BaseResult<string[]>>({
      url: '/api/admin/tableConfig/getTableNameList',
      params
    })
  }

  // 获取模式名(数据库名)列表
  static getSchemaNameList() {
    return request.get<BaseResult<string[]>>({
      url: '/api/admin/tableConfig/getSchemaNameList'
    })
  }

  // 同步字段列表接口
  static syncFieldsByTableName(params: SyncTableFieldListParams) {
    return request.post<BaseResult>({
      url: '/api/admin/tableFieldConfig/syncFieldsByTableName',
      params
    })
  }

  // 根据表单配置获取字段集合
  static getTableFieldConfigList(params: TableFieldConfigListParams) {
    return request.get<PageResult<TableFieldConfigModel>>({
      url: '/api/admin/tableFieldConfig/list',
      params
    })
  }

  // 编辑字段配置
  static editTableFieldConfig(params: EditTableFieldConfigParams) {
    return request.put<BaseResult>({
      url: '/api/admin/tableFieldConfig/updateFieldConfig',
      params
    })
  }

  // 获取表的字段简单信息列表
  static getSimpleTableField(params: getSimpleTableFieldParam) {
    return request.get<BaseResult<SimpleTableField>>({
      url: '/api/admin/tableFieldConfig/getSimpleTableField',
      params
    })
  }

  // 字段排序
  static fieldSort(params: { ids: number[] }) {
    return request.post<BaseResult>({
      url: '/api/admin/tableFieldConfig/sortFields',
      params
    })
  }

  // 删除字段配置
  static deleteTableFieldConfig(params: { id: number }) {
    return request.del<BaseResult>({
      url: '/api/admin/tableFieldConfig/delete',
      params
    })
  }
}
