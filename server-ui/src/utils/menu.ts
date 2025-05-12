import { $t } from '@/language'
import { MenuListType } from '@/types/menu'
import { MenuDetail } from '@/api/model/menuModel'

// 创建递归函数处理嵌套路由
/**
 * 处理路由配置,转换为菜单数据结构
 * @param route 路由配置对象
 * @param parentPath 父级路径
 * @returns 处理后的菜单项
 */
export const processRoute = (route: MenuListType, parentPath = ''): MenuListType => {
  // 构建完整路径
  const currentPath = route.path
    ? route.meta?.isIframe
      ? route.path // isIframe 为 true 时直接使用原始路径
      : parentPath
        ? `${parentPath}/${route.path}`.replace(/\/+/g, '/') // 规范化路径,避免多余的斜杠
        : route.path
    : ''
  route.meta.originalPath = route.path
  return {
    id: route.id ?? Math.random(), // 使用空值合并运算符
    name: route.name,
    path: currentPath,
    component: route.component,
    meta: route.meta ?? {}, // 使用空值合并运算符
    remarks: route.remarks,
    parentId: route.parentId,
    parentIds: route.parentIds,
    level: route.level,
    sort: route.sort,
    updateDate: route.updateDate, // 添加 updateDate 字段
    children: Array.isArray(route.children)
      ? route.children.map((child) => processRoute(child, currentPath))
      : []
  }
}

/**
 * 保存 iframe 路由到 sessionStorage 中
 * @param list iframe 路由列表
 */
export const saveIframeRoutes = (list: MenuListType[]): void => {
  if (list.length > 0) {
    sessionStorage.setItem('iframeRoutes', JSON.stringify(list))
  }
}

// 获取 iframe 路由
export const getIframeRoutes = () => {
  return JSON.parse(sessionStorage.getItem('iframeRoutes') || '[]')
}

/**
 * 格式化菜单标题
 * @param title 菜单标题，可以是 i18n 的 key，也可以是字符串，比如：'用户列表'
 * @returns 格式化后的菜单标题
 */
export const formatMenuTitle = (title: string) => {
  return title.startsWith('menus.') ? $t(title) : title
}

/**
 * 处理菜单详情数据,转换为菜单列表数据结构
 * @param menuDetail 菜单详情对象
 * @param parentPath 父级路径
 * @returns 处理后的菜单项
 */
export const processMenuDetail = (menuDetail: MenuDetail, parentPath = ''): MenuListType => {
  // 构建完整路径
  const currentPath = menuDetail.path
    ? menuDetail.meta?.isIframe
      ? menuDetail.path // isIframe 为 true 时直接使用原始路径
      : parentPath
        ? `${parentPath}/${menuDetail.path}`.replace(/\/+/g, '/') // 规范化路径,避免多余的斜杠
        : menuDetail.path
    : ''

  // 确保meta中存在originalPath字段
  if (menuDetail.meta) {
    menuDetail.meta.originalPath = menuDetail.path
  }

  return {
    id: menuDetail.id,
    name: menuDetail.name,
    path: currentPath,
    component: menuDetail.component || '',
    meta: menuDetail.meta || {},
    remarks: menuDetail.remarks || '',
    parentId: menuDetail.parentId,
    parentIds: menuDetail.parentIds,
    level: menuDetail.level,
    sort: menuDetail.sort,
    updateDate: menuDetail.updateDate,
    children: Array.isArray(menuDetail.children)
      ? menuDetail.children.map((child) => processMenuDetail(child, currentPath))
      : []
  }
}
