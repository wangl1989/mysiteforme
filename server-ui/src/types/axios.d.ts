export type ErrorMessageMode = 'none' | 'modal' | 'message' | undefined

export interface RequestOptions {
  joinParamsToUrl?: boolean
  formatDate?: boolean
  isTransformResponse?: boolean
  isReturnNativeResponse?: boolean
  joinPrefix?: boolean
  apiUrl?: string
  errorMessageMode?: ErrorMessageMode
  joinTime?: boolean
  ignoreCancelToken?: boolean
  withToken?: boolean
}

// 基础接口返回的数据结构
export interface BaseResult<T = any> {
  code: number // 状态码
  success: boolean // 是否成功
  message: string // 消息
  data: T // 数据
  token?: string // 可选字段，用于返回 token
}

// 分页数据结构，继承基础结果结构
export interface PaginationResult<T> extends BaseResult {
  currentPage: number // 当前页
  pageSize: number // 每页条数
  lastPage: number // 总页数
  total: number // 总条数
  data: T
}

/**
 * 通用分页数据结构
 */
export interface PageData<T> {
  records: T[] // 记录列表
  total: number // 总记录数
  size: number // 每页大小
  current: number // 当前页码
  pages: number // 总页数
}

export interface PageResult<T = any> {
  data: PageData<T>
  code: number
  success: boolean
  message: string
  timestamp: number
}
