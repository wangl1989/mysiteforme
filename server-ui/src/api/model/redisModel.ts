export interface RedisRecordModel {
  key: string
  type: string
  ttl: number
  value: string
}

// Redis列表查询参数对象
export interface RedisListParam {
  page: number // 页码
  limit: number // 每页条数
  keyPattern?: string // 键
  sortByExpireAsc?: boolean // 是否按过期时间升序排序
}
