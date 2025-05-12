export type MenuListType = {
  id: number
  path: string // 路由
  name: string // 组件名
  component?: string // 改为字符串类型，表示组件路径
  parentId?: number //父节点ID
  parentIds?: string //父节点集合
  level?: number
  sort?: number
  remarks?: string
  updateDate?: string // 添加更新时间字段
  meta: {
    title: string // 菜单名称
    icon?: string // 菜单图标
    color?: string | null // 新增 color 字段
    showBadge?: boolean // 是否显示徽标
    showTextBadge?: string // 是否显示新徽标
    isHide?: boolean // 是否在菜单中隐藏
    isHideTab?: boolean // 是否在标签页中隐藏
    link?: string // 链接
    isIframe?: boolean // 是否是 iframe
    keepAlive: boolean // 是否缓存
    originalPath?: string // 原始path路径
    buttonKeys?: string[] // 按钮权限标识集合
    authList?: Array<{
      id: number
      permissionName?: string //权限名称
      permissionCode?: string //权限编码
      permissionType?: number //权限类型
      icon?: string //权限LOGO
      sort?: number //权限排序值
      remarks?: string //权限备注
      button?: {
        id: number
        buttonKey?: string
        buttonName?: string
      }
      api?: {
        id: number
        apiUrl?: string
        httpMethod?: string
        common: boolean
      }
      page?: {
        id: number
        pageUrl?: string
      }
    }> // 可操作权限
    isInMainContainer?: boolean // 是否在主容器中
  }
  children?: MenuListType[] // 子菜单
}
