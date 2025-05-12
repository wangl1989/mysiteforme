import request from '@/utils/http'
import { BaseResult } from '@/types/axios'
import {
  Role,
  RoleRecord,
  RoleListParams,
  SaveRolePermissionsParams,
  RoleMenusPermissions,
  UpdateRoleData,
  SaveRoleData
} from './model/roleModel'
import { PageResult } from '@/types/axios'

export class RoleService {
  // 获取用户所有可用角色
  static getUserAllRoles() {
    return request.get<BaseResult<Role[]>>({
      url: '/api/admin/role/userAllRole'
    })
  }

  // 获取角色分页列表
  static getRoleList(params: RoleListParams) {
    return request.get<PageResult<RoleRecord>>({
      url: '/api/admin/role/list',
      params
    })
  }

  // 获取角色的权限列表
  static getRolePermissions(roleId: number) {
    return request.get<BaseResult<RoleMenusPermissions>>({
      url: '/api/admin/role/getRoleMenusPers',
      params: { roleId }
    })
  }

  // 保存角色权限
  static saveRolePermissions(params: SaveRolePermissionsParams) {
    return request.post<BaseResult>({
      url: '/api/admin/role/saveRoleMenusPers',
      data: params
    })
  }

  // 更新角色
  static updateRole(params: UpdateRoleData) {
    return request.put<BaseResult>({
      url: '/api/admin/role/edit',
      data: params
    })
  }

  // 新增角色
  static addRole(params: SaveRoleData) {
    return request.post<BaseResult>({
      url: '/api/admin/role/add',
      data: params
    })
  }

  // 删除角色
  static deleteRole(roleId: number) {
    return request.del<BaseResult>({
      url: '/api/admin/role/delete?id=' + roleId
    })
  }
}
