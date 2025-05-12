import { App, Directive, watchEffect } from 'vue'
import { useMenuStore } from '@/store/modules/menu'
import { useUserStore } from '@/store/modules/user'

/**
 * 菜单权限指令
 * 用法：
 * 1. 基本使用
 * <div v-menu-auth="'Layout/system/user'">只有拥有用户管理菜单权限的用户才能看到此内容</div>
 *
 * 2. 在导航菜单中使用
 * <el-menu-item v-menu-auth="'system/role'">角色管理</el-menu-item>
 *
 * 3. 在按钮中使用
 * <el-button v-menu-auth="'system/setting'">系统设置</el-button>
 *
 * 4. 支持绝对路径和相对路径
 * <div v-menu-auth="'/system/log/Log'">支持前导斜杠的写法</div>
 *
 * 注意：传入的值应该是菜单组件的路径，与菜单定义中的component属性值保持一致
 */

// 创建一个缓存对象，用于存储已收集的组件路径和权限结果
const componentPathsCache = {
  paths: [] as string[],
  initialized: false,
  // 添加用户ID字段，用于检测用户是否变更
  currentUserId: ''
}

// 用于收集组件路径的函数
const collectComponentPaths = (menus: any[]): string[] => {
  if (!menus || !menus.length) return []

  const paths: string[] = []

  const traverseMenus = (items: any[]) => {
    items.forEach((menu) => {
      // 如果菜单有component属性，则加入集合
      if (menu.component) {
        paths.push(menu.component)

        // 同时添加带斜杠和不带斜杠的版本，以支持更灵活的路径匹配
        if (menu.component.startsWith('/')) {
          paths.push(menu.component.substring(1))
        } else {
          paths.push('/' + menu.component)
        }
      }

      // 如果有子菜单，递归处理
      if (menu.children && menu.children.length) {
        traverseMenus(menu.children)
      }
    })
  }

  traverseMenus(menus)
  return paths
}

// 初始化缓存
const initializeCache = () => {
  try {
    const menuStore = useMenuStore()
    const userStore = useUserStore()

    // 获取当前用户ID
    const userId = userStore.info?.id || ''

    // 如果用户ID变了，需要重新初始化缓存
    if (componentPathsCache.currentUserId !== String(userId)) {
      componentPathsCache.initialized = false
      componentPathsCache.currentUserId = String(userId)
    }

    // 如果缓存已初始化且用户ID没变，则不需要重新收集
    if (componentPathsCache.initialized) return

    const menuList = menuStore.menuList

    // 确保菜单列表已加载
    if (!menuList || menuList.length === 0) {
      return
    }

    // 收集所有组件路径
    componentPathsCache.paths = collectComponentPaths(menuList)
    componentPathsCache.initialized = true
  } catch (error) {
    console.error('初始化菜单权限组件路径缓存失败:', error)
    // 出错时重置缓存状态，以便下次可以重试
    componentPathsCache.initialized = false
  }
}

// 重置缓存的函数，在用户登出或权限变更时调用
export const resetMenuAuthCache = () => {
  componentPathsCache.paths = []
  componentPathsCache.initialized = false
  componentPathsCache.currentUserId = ''
}

// 设置权限监听
// 定义一个更具体的函数类型，代替通用的Function类型
let unwatchFn: (() => void) | null = null

const setupPermissionWatcher = () => {
  // 如果已经设置了监听，先取消之前的监听
  if (unwatchFn) {
    unwatchFn()
    unwatchFn = null
  }

  try {
    const menuStore = useMenuStore()
    const userStore = useUserStore()

    let prevUserId = userStore.info?.id
    let prevMenuListLength = menuStore.menuList?.length || 0

    // 监听菜单列表和用户信息的变化
    unwatchFn = watchEffect(() => {
      const menuList = menuStore.menuList
      const userId = userStore.info?.id

      // 只有当用户ID发生变化时，才重置缓存
      // 防止页面刷新时不必要的重置
      if (prevUserId !== undefined && userId !== prevUserId) {
        resetMenuAuthCache()
        // 重新初始化
        initializeCache()
      }

      // 或者当菜单列表长度变化时，重新初始化
      // 这通常意味着权限可能已更新
      if (menuList && menuList.length > 0 && menuList.length !== prevMenuListLength) {
        componentPathsCache.initialized = false
        initializeCache()
      }

      // 更新先前的值
      prevUserId = userId
      prevMenuListLength = menuList?.length || 0
    })
  } catch (error) {
    console.error('设置菜单权限监听失败:', error)
  }
}

const menuAuthDirective: Directive = {
  mounted(el: HTMLElement, binding: any) {
    // 确保缓存已初始化
    initializeCache()

    // 获取要检查的组件路径
    const pathToCheck = binding.value

    // 如果缓存尚未初始化或路径为空，则暂不处理
    if (!componentPathsCache.initialized || !pathToCheck) {
      return
    }

    // 使用缓存的组件路径检查权限
    const hasPermission = componentPathsCache.paths.includes(pathToCheck)

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

export function setupMenuPermissionDirective(app: App) {
  // 在注册指令时初始化缓存，这样只会在应用启动时初始化一次
  initializeCache()

  // 设置监听菜单和用户变化
  setupPermissionWatcher()

  app.directive('menu-auth', menuAuthDirective)

  // 向全局暴露重置缓存的方法，以便在需要时手动调用
  // 例如在用户手动刷新权限时
  app.config.globalProperties.$resetMenuAuthCache = resetMenuAuthCache
}
