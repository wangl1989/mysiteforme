export interface LogRecord {
  id: string //日志ID
  createDate: string //日志创建日期
  delFlag: boolean //是否删除标志
  remarks: string | null //备注
  title: string | null //日志标题
  remoteAddr: string | null //操作IP地址
  username: string | null //操作用户名称
  requestUri: string | null //请求URI
  httpMethod: string | null //请求方法类型
  classMethod: string | null //请求实际方法
  params: string | null //方法参数
  response: string | string //方法返回对象
  useTime: number | 0 //请求耗时
  browser: string | string //浏览器
  area: string | string //地区
  province: string | string //省份
  city: string | null //城市
  isp: string | null //ISP供应商
  exception: string | null //异常信息
}

/**
 * 日志列表查询参数
 */
export interface LogListParams {
  page: number
  limit: number
  type?: string
  title?: string
  username?: string
  httpMethod?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  sortByCreateDateAsc?: boolean
}
