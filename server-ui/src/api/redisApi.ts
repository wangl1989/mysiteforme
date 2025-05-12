import request from '@/utils/http'
import { BaseResult, PageResult } from '@/types/axios'
import { RedisRecordModel, RedisListParam } from './model/redisModel'

export class RedisApi {
  static getRedisRecord(params: RedisListParam) {
    return request.get<PageResult<RedisRecordModel>>({
      url: '/api/admin/redis/list',
      params
    })
  }

  // 删除Redis记录
  static deleteRedisRecord(key: string) {
    return request.del<BaseResult>({
      url: '/api/admin/redis/delete',
      params: { key }
    })
  }
}
