export interface QuartzTaskRecordModel {
  id: number
  jobId: number // 任务ID
  name: string // 定时任务名称
  cron: string
  groupName: string // 任务组名
  targetBean: string // 定制任务执行类
  trgetMethod: string // 定时任务执行方法
  params: string // 执行参数
  status: number // 任务状态
  error: string // 异常消息
  times: number // 执行时间
  createDate: string
  updateDate: string
}

export interface QuartzTaskLogListParams {
  page: number
  limit: number
  name?: number
  sortByCreateDateAsc?: boolean
}

// 任务状态枚举
export enum TaskStatus {
  NORMAL = 0, // 正常
  PAUSED = 1, // 暂停
  COMPLETE = 2, // 完成
  ERROR = 3, // 错误
  BLOCKED = 4 // 阻塞
}

// 获取任务状态文本
export function getTaskStatusText(status: number): string {
  switch (status) {
    case TaskStatus.NORMAL:
      return '正常'
    case TaskStatus.PAUSED:
      return '暂停'
    case TaskStatus.COMPLETE:
      return '完成'
    case TaskStatus.ERROR:
      return '错误'
    case TaskStatus.BLOCKED:
      return '阻塞'
    default:
      return '未知'
  }
}

// 获取任务状态类型
export function getTaskStatusType(status: number): 'success' | 'warning' | 'danger' | 'info' {
  switch (status) {
    case TaskStatus.NORMAL:
      return 'success'
    case TaskStatus.PAUSED:
      return 'warning'
    case TaskStatus.COMPLETE:
      return 'success'
    case TaskStatus.ERROR:
      return 'danger'
    case TaskStatus.BLOCKED:
      return 'info'
    default:
      return 'info'
  }
}
