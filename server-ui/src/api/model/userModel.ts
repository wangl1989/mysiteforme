/**
 * 用户相关模型定义
 */
export interface UserDetailResponse {
  id: number
  loginName: string
  nickName: string
  icon: string
  email: string
  tel: string
  roles: Array<{
    id: number
    name: string
    isDefault: boolean
    remarks?: string
  }>
  permissions: Array<{
    id: number
    permissionName: string
    permissionCode: string
    permissionType: string
    icon: string | null
    sort: number
    remarks: string | null
    menu?: {
      id: number
      name: string
      parentId: number
      parentIds: string
      level: string
      sort: number
      remarks: string | null
    }
    button?: {
      id: number
      buttonKey: string
      buttonName: string
    } | null
    api?: {
      id: number
      apiUrl: string
      httpMethod: string
      common: boolean
    } | null
    page?: {
      id: number
      pageUrl: string
    } | null
  }>
  status: number // 状态 1.在线 2.离线 3.正常 4.锁定 5.注销
  delFlag: boolean
  deviceId: string | null
  location?: string | null
  remarks?: string | null
}

/**
 * 用户列表查询参数
 */
export interface UserListParams {
  loginName?: string // 用户名，模糊搜索
  tel?: string // 手机号，精确搜索
  email?: string // 邮箱，模糊搜索
  location?: string // 地理位置
  sortByCreateDateAsc?: boolean | null // 根据创建时间排序
  sortByLoginNameAsc?: boolean | null // 根据用户名排序
  sortByUpdateDateAsc?: boolean | null // 根据更新时间排序
  page?: number // 页码，默认1
  limit?: number // 每页数量，默认10
}

/**
 * 用户记录模型
 */
export interface UserRecord {
  id: number
  loginName: string
  nickName: string
  icon: string
  email: string
  tel: string
  locked: boolean
  delFlag: boolean
  remarks: string
  createDate: string
  createUserNickName: string
  createUserLoginName: string
  createId: number
  updateDate: string
  updateUserNickName: string
  updateUserLoginName: string
  updateId: number
  roles: Array<{
    id: number
    name: string
    isDefault: boolean
  }>
}

// 用户编辑参数接口
export interface EditUserParams {
  id: number
  roleSet: Array<{
    id: number
  }>
  email: string
  tel: string
  nickName: string
  icon?: string
  remarks?: string
}

// 用户新增参数接口
export interface AddUserParams {
  loginName: string // 登录名规则: 必须以英文字母开头，只能包含字母、数字、下划线，最小3个字符，最大10个字符
  roles: Array<{
    id: number
  }>
  email: string
  tel: string
  nickName: string
  icon?: string
  remarks?: string
}

// 更改密码的参数接口
export interface ChangePasswordParams {
  oldPwd: string // 旧密码 ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d\W_]{8,20}$
  newPwd: string // 新密码 ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d\W_]{8,20}$
}

// 分配用户权限的参数接口
export interface AssignUserPermissionParams {
  userId: number
  permissionIds: number[]
}

// 接口定义：用户定位响应
export interface UserLocationResponse {
  ip: string
  province: string
  city: string
  district: string
}

// 更新当前用户信息的参数接口
export interface UpdateCurrentUserInfoParams {
  nickName?: string
  email?: string
  tel?: string
  remarks?: string
  icon?: string
  location?: string
}

/**
 * 用户当前设备
 */
export interface UserDeviceRecord {
  ip: string // 此设备最后登录ip地址
  deviceId: string // 设备ID
  country: string // 登录国家
  province: string // 登录省份
  city: string // 登录城市
  region: string // 登录地区
  os: string // 操作系统
  browser: string // 浏览器
  online: boolean // 是否在线
  currentDevice: boolean // 是否当前设备
}
