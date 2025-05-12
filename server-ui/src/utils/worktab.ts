import { useWorktabStore } from '@/store/modules/worktab'
import { RouteLocationNormalized } from 'vue-router'
import { getIframeRoutes } from './menu'
import { isIframe } from './utils'
import { useSettingStore } from '@/store/modules/setting'
import { HOME_PAGE } from '@/router'

/**
 * 根据当前路由信息设置工作标签页（worktab）
 * @param to 当前路由对象
 */
export const setWorktab = (to: RouteLocationNormalized): void => {
  const worktabStore = useWorktabStore()
  const { meta, path, name, params, query } = to
  if (!meta.isHideTab) {
    // 如果是 iframe 页面，则特殊处理工作标签页
    if (isIframe(path)) {
      const iframeRoute = getIframeRoutes().find((route: any) => route.path === to.path)

      if (iframeRoute?.meta) {
        worktabStore.openTab({
          title: iframeRoute.meta.title,
          path,
          name: name as string,
          keepAlive: meta.keepAlive as boolean,
          params,
          query
        })
      }
    } else if (useSettingStore().showWorkTab || path === HOME_PAGE) {
      worktabStore.openTab({
        title: meta.title as string,
        path,
        name: name as string,
        keepAlive: meta.keepAlive as boolean,
        params,
        query
      })
    }
  }
}
