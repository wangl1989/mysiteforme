// import { PageData } from '@/types/axios'

/**
 * 角色相关模型
 */

// 角色基本信息
export interface Role {
  id: number
  name: string
  isDefault: boolean
}

/**
 * 角色详细信息
 */
export interface RoleRecord {
  id: number
  name: string
  remarks: string
  isDefault: boolean
  delFlag: number
  createId: number
  updateId: number
  createDate: string
  updateDate: string
  createUserNickName: string
  updateUserNickName: string
  createUserLoginName: string
  updateUserLoginName: string
}

/**
 * 角色列表查询参数
 */
export interface RoleListParams {
  name?: string // 角色名称，模糊搜索
  sortByCreateDateAsc?: boolean | null // 根据创建时间排序
  page?: number // 当前页码
  limit?: number // 每页条数
}

/**
 * 新增角色权限模型
 */
export interface SaveRolePermissionsParams {
  roleId: number | string
  permissionIds: number[]
  menuIds: number[]
}

export interface SaveRoleData {
  name: string
  remarks?: string
  isDefault: boolean
}

export interface UpdateRoleData {
  id: number
  name: string
  remarks?: string
  isDefault: boolean
}

export interface RoleMenusPermissions {
  menuIds: number[]
  permissionIds: number[]
}
