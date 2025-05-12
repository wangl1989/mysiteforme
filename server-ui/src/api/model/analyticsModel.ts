// 访问记录对象
export interface VisitLogsData {
  userId?: number
  loginName?: string
  nickName?: string
  referrer: string
  entryPage: string
  title: string
  screenSize: string
  timeOnPage: number
  language: string
  deviceId: string // 设备ID
}

// 用户点击事件对象
export interface EventLogsData {
  eventType: string // 事件类型，如点击、滚动等
  elementId: string // 元素ID
  elementClass: string // 元素类名
  pageUrl: string // 当前页面URL
  deviceId: string // 设备ID
  elementText?: string // 元素文本内容
  elementPath?: string // 元素路径
  elementHref?: string // 元素链接
}

// 批量发送点击事件参数
export interface BatchEventLogsData {
  userId?: number
  loginName?: string
  nickName?: string
  events: EventLogsData[]
}

// 今日实时统计模型
export interface TodayStatsResponse {
  totalVisits: number // 今天的访问量
  uniqueVisitors: number // 今天的独立访客数量
  newUsers: number // 今天的新访客数量
  totalClicks: number // 今天的总点击量
  avgVisitDuration: number // 今日平均访问时长
  bounceRate: number // 今天的跳出率
  allTotalVisits: number // 所有时间的总访问量
  allTotalUser: number // 所有时间的新访客数量
  allTotalClicks: number // 所有时间的总点击量
  visitsPercent: number // 访问比较上周百分比
  visitorsPercent: number // 独立访客比较上周百分比
  clickPercent: number // 点击比较上周百分比
  newUserPercent: number // 新用户比较上周百分比
}

// 获取N天之前到现在的每天访问数据
export interface TrendStatsDataResponse {
  days: number[] // 日期
  visits: number[] // 访问量
}

// 获取年度每月访问数据
export interface MonthlyStatsDataResponse {
  months: string[] // 月份
  visits: number[] // 访问量
}

// 获取首页用户集合
export interface IndexUserCollectionResponse {
  id: number // 用户ID
  loginName: string // 用户名
  nickName: string // 昵称
  icon: string // 头像
  email: string // 邮箱
  location?: string | null // 地理位置
  percent?: number // 资料完善百分比
}

// 获取首页用户操作日志集合
export interface IndexUserOperationLogResponse {
  id: number // 日志ID
  title: string // 日志标题
  icon?: string // 图标
  method: string // 请求方法
  userName: string // 操作用户名称
  createTime: string // 创建时间
}

// 获取首页用户系统数据内容
export interface IndexSystemDataResponse {
  id: number // 系统ID
  roleCount: number // 角色数量
  menuCount: number // 菜单数量
  permissionCount: number // 权限数量
  rolePercent: number // 角色百分比
  menuPercent: number // 菜单百分比
  permissionPercent: number // 权限百分比
}
