import { fourDotsSpinnerSvg } from '@/assets/svg/loading'
import request from '@/utils/http'
import { BaseResult } from '@/types/axios'
// import { asyncRoutes } from '@/router/modules/asyncRoutes'
import { MenuListType } from '@/types/menu'
import { processRoute } from '@/utils/menu'
import { ElLoading } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import {
  AddMenuParams,
  EditMenuParams,
  AddPermissionParams,
  EditPermissionParams,
  MenuDetail
} from '@/api/model/menuModel' // 导入菜单和权限参数接口

// 菜单接口
export const menuService = {
  async getMenuList(
    delay: number = 0
  ): Promise<{ menuList: MenuListType[]; closeLoading: () => void }> {
    try {
      // 获取到的菜单数据
      // const menuList = asyncRoutes
      const response = await request.get<BaseResult<MenuListType[]>>({
        url: '/api/admin/user/currentMenu'
      })
      const menuList = response.data || []
      if (!Array.isArray(menuList)) {
        const userStore = useUserStore()
        ElMessage.error('用户菜单获取失败，请重新登录')
        userStore.logOut()
        return {
          menuList: [],
          closeLoading: () => {}
        }
      }
      // 处理后的菜单数据
      const processedMenuList: MenuListType[] = menuList.map((route: MenuListType) =>
        processRoute(route)
      )

      const loading = ElLoading.service({
        lock: true,
        background: 'rgba(0, 0, 0, 0)',
        svg: fourDotsSpinnerSvg,
        svgViewBox: '0 0 40 40'
      })

      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            menuList: processedMenuList,
            closeLoading: () => loading.close()
          })
        }, delay)
      })
    } catch (error) {
      console.error('获取菜单列表失败:', error)
      return {
        menuList: [],
        closeLoading: () => {}
      }
    }
  }
}

export class MenuApiService {
  // 获取菜单详细信息
  static getCurrentMenuDetail() {
    return request.get<BaseResult<MenuDetail>>({
      url: `/api/admin/menu/tree`
    })
  }

  // 新增菜单
  static addMenu(params: AddMenuParams) {
    // 直接返回 request.post 返回的 Promise
    return request.post<BaseResult<null>>({
      url: '/api/admin/menu/add',
      params
    })
  }

  // 编辑菜单
  static editMenu(params: EditMenuParams) {
    // 直接返回 request.put 返回的 Promise
    return request.put<BaseResult<null>>({
      url: '/api/admin/menu/edit',
      params
    })
  }

  // 新增权限
  static addPermission(params: AddPermissionParams) {
    return request.post<BaseResult<null>>({
      url: '/api/admin/permission/add',
      params
    })
  }

  // 编辑权限
  static editPermission(params: EditPermissionParams) {
    return request.put<BaseResult<null>>({
      url: '/api/admin/permission/edit',
      params
    })
  }

  // 删除菜单
  static deleteMenu(id: number) {
    return request.del<BaseResult<null>>({
      url: `/api/admin/menu/delete`,
      params: { id }
    })
  }

  // 删除权限
  static deletePermission(id: number) {
    return request.del<BaseResult<null>>({
      url: `/api/admin/permission/delete`,
      params: { id }
    })
  }
}

export default new MenuApiService()
