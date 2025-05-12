import request from '@/utils/http'
import { BaseResult, PageResult } from '@/types/axios'
import { CaptchaData, LoginParams, TokenResponse } from './model/loginModel'
import {
  UserDetailResponse,
  UserListParams,
  EditUserParams,
  AddUserParams,
  UserRecord,
  ChangePasswordParams,
  AssignUserPermissionParams,
  UserLocationResponse,
  UpdateCurrentUserInfoParams,
  UserDeviceRecord
} from './model/userModel'
export class UserService {
  // 登录 - 接收 body 和 header 值
  static login(body: LoginParams, deviceId: string) {
    // 构建 headers
    const headers = {
      'Device-Id': deviceId
    }

    // 发送登录请求
    return request.post<BaseResult<TokenResponse>>({
      url: '/login',
      params: body,
      headers
    })
  }

  // 获取验证码
  static getCaptcha() {
    return request.get<BaseResult<CaptchaData>>({
      url: '/genCaptcha',
      // 添加时间戳防止缓存
      params: { t: new Date().getTime() },
      // 显式设置不发送凭证
      withCredentials: false
    })
  }

  // 获取当前用户详情 - 简化后的方法，不再处理 token 刷新逻辑
  static getCurrentUser() {
    // 不需要任何额外参数，认证头和刷新逻辑将由 HTTP 拦截器自动处理
    return request.get<BaseResult<UserDetailResponse>>({
      url: '/api/admin/user/currentUser'
    })
  }

  // 获取用户列表（分页）
  static getUserList(params: UserListParams) {
    return request.get<PageResult<UserRecord>>({
      url: '/api/admin/user/list',
      params
    })
  }

  // 获取用户详情
  static getUserDetail(userId: number) {
    return request.get<BaseResult<UserDetailResponse>>({
      url: '/api/admin/user/detail',
      params: { id: userId }
    })
  }

  // 编辑用户信息
  static editUser(params: EditUserParams) {
    return request.put<BaseResult>({
      url: '/api/admin/user/edit',
      params
    })
  }

  // 新增用户
  static addUser(params: AddUserParams) {
    return request.post<BaseResult>({
      url: '/api/admin/user/add',
      params
    })
  }

  // 删除用户
  static deleteUser(userId: number) {
    return request.del<BaseResult>({
      url: '/api/admin/user/delete',
      params: { id: userId }
    })
  }

  // 当前登录用户更改密码
  static changePassword(params: ChangePasswordParams) {
    return request.post<BaseResult>({
      url: '/api/admin/user/changePassword',
      params
    })
  }

  // 额外分配用户权限
  static assignUserPermission(params: AssignUserPermissionParams) {
    return request.post<BaseResult>({
      url: '/api/admin/user/assignUserPermission',
      params
    })
  }

  // 根据用户ID获取已经给他单独分配的权限ID集合
  static getAssignedPermissionIds(userId: number) {
    return request.get<BaseResult<number[]>>({
      url: '/api/admin/user/userPermission',
      params: { userId }
    })
  }

  // 获取用户定位
  static getUserLocation() {
    return request.get<BaseResult<UserLocationResponse>>({
      url: '/api/admin/user/location'
    })
  }

  // 更新当前用户信息
  static updateCurrentUserInfo(params: UpdateCurrentUserInfoParams) {
    return request.put<BaseResult>({
      url: '/api/admin/user/editCurrentUser',
      params
    })
  }

  // 获取用户当前设备
  static getUserDevice() {
    return request.get<BaseResult<UserDeviceRecord>>({
      url: '/api/admin/userDevice/userDevices'
    })
  }
}
