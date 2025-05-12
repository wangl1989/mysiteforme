export interface QuartzTaskRecordModel {
  id: number
  name: string // 任务名称
  cron: string // 任务表达式
  groupName: string // 任务组名
  targetBean: string // 执行的类
  trgetMethod: string // 执行方法
  params: string // 执行参数
  status: number // 任务状态 0:正常  1：暂停
  remarks: string // 备注
  createTime: string
  updateTime: string
}

export interface QuartzTaskListParams {
  page: number
  limit: number
  name?: string // 任务名称
  status?: number // 任务状态 0:正常  1：暂停
  sortByCreateDateAsc?: boolean
}

export interface AddQuartzTaskParams {
  name: string // 任务名称
  cron: string // 任务表达式
  groupName: string // 任务组名
  targetBean: string // 执行的类
  trgetMethod: string // 执行方法
  params: string // 执行参数
  status: number // 任务状态 0:正常  1：暂停
  remarks: string // 备注
}

export interface EditQuartzTaskParams {
  id: number // 任务ID
  name: string // 任务名称
  cron: string // 任务表达式
  groupName: string // 任务组名
  targetBean: string // 执行的类
  trgetMethod: string // 执行方法
  params: string // 执行参数
  status: number // 任务状态 0:正常  1：暂停
  remarks: string // 备注
}
