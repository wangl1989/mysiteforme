/**
 * 资源记录类型
 */
export interface ResourcesRecord {
  id: string
  createId: number
  createDate: string
  updateId: number
  updateDate: string
  delFlag: boolean
  remarks: string | null
  createUser: string | null
  updateUser: string | null
  fileName: string
  source: 'local' | 'oss' | 'qiniu'
  webUrl: string
  hash: string
  fileSize: string
  fileType: string
  originalNetUrl: string
}

/**
 * 资源列表查询参数
 */
export interface ResourcesListParams {
  page: number
  limit: number
  fileType?: string
  hash?: string
  source?: 'local' | 'oss' | 'qiniu'
  sortByCreateDateAsc?: boolean
}
