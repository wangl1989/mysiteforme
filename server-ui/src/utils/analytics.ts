import { AnalyticsService } from '@/api/analyticsApi'
import { VisitLogsData, EventLogsData, BatchEventLogsData } from '@/api/model/analyticsModel'
import { getDeviceId, getDeviceIdSync } from '@/utils/deviceId'
import { useUserStore } from '@/store/modules/user'

class Analytics {
  private deviceIdPromise: Promise<string>
  private eventQueue: EventLogsData[] = []
  private flushTimeout: number | null = null
  private pageEnterTime: number | null = null
  private isPageVisible: boolean = true
  private hiddenTime: number | null = null
  private currentRoute: any = null
  private totalInvisibleTime: number = 0

  constructor() {
    this.deviceIdPromise = getDeviceId()
    this.initVisibilityTracking()
  }

  // 初始化页面可见性跟踪
  private initVisibilityTracking(): void {
    document.addEventListener('visibilitychange', () => {
      if (document.hidden) {
        this.isPageVisible = false
        this.hiddenTime = Date.now()
      } else {
        this.isPageVisible = true

        // 计算页面不可见的时间
        if (this.hiddenTime) {
          this.totalInvisibleTime += Date.now() - this.hiddenTime
          this.hiddenTime = null
        }
      }
    })
  }

  // 获取当前用户信息的辅助方法
  private getCurrentUserInfo() {
    const userStore = useUserStore()
    // 直接获取当前状态，不使用computed
    return {
      userId: userStore.info.id || undefined,
      loginName: userStore.info.username || '',
      nickName: userStore.info.name || ''
    }
  }

  // 记录页面访问 - 返回清理函数
  async trackPageView(route: any, fromRoute?: any): Promise<() => void> {
    // 获取referrer - 优先使用上一个路由的路径
    let referrer = document.referrer
    if (fromRoute && fromRoute.path) {
      // 如果有上一个路由，使用它的路径作为referrer
      referrer = window.location.origin + fromRoute.path
    }
    // 重置计时器和不可见时间累计
    this.pageEnterTime = Date.now()
    this.currentRoute = route
    this.totalInvisibleTime = 0
    this.hiddenTime = null

    // 确保有设备ID
    const deviceId = await this.deviceIdPromise
    // 获取当前用户信息
    const userInfo = this.getCurrentUserInfo()

    // 准备页面访问数据（但不立即发送）
    const visitData: VisitLogsData = {
      userId: userInfo.userId, // 未登录用户为0
      loginName: userInfo.loginName, // 未登录用户为空
      nickName: userInfo.nickName, // 未登录用户为空
      referrer: referrer,
      entryPage: route.path,
      title: document.title,
      screenSize: `${window.innerWidth}x${window.innerHeight}`,
      timeOnPage: 0, // 将在离开页面时计算
      language: navigator.language,
      deviceId
    }

    // 页面离开时记录停留时间并发送数据
    const sendPageViewData = () => {
      // 只在有进入时间的情况下计算
      if (!this.pageEnterTime) return

      // 计算实际可见时间（总时间减去不可见时间）
      const totalTime = Date.now() - this.pageEnterTime

      // 如果页面当前不可见，加上当前不可见的时间
      if (this.hiddenTime) {
        this.totalInvisibleTime += Date.now() - this.hiddenTime
      }
      // 计算有效停留时间（总时间减去不可见时间）
      const effectiveTime = Math.max(0, totalTime - this.totalInvisibleTime)

      // 过滤异常值（超过30分钟可能是用户离开）
      if (effectiveTime <= 0 || effectiveTime >= 30 * 60 * 1000) return

      // 更新停留时间
      visitData.timeOnPage = effectiveTime

      // 使用 sendBeacon API 确保数据发送，即使页面正在卸载
      if (navigator.sendBeacon) {
        const blob = new Blob([JSON.stringify(visitData)], { type: 'application/json' })
        navigator.sendBeacon(`${import.meta.env.VITE_API_URL}/api/analytics/visitLogs`, blob)
      } else {
        // 回退到常规 AJAX
        AnalyticsService.postVisitLogs(visitData).catch((err) =>
          console.error('发送页面访问数据失败:', err)
        )
      }
    }

    // 监听页面离开事件
    window.addEventListener('beforeunload', sendPageViewData, { once: true })

    // 返回清理函数
    return () => {
      window.removeEventListener('beforeunload', sendPageViewData)
      sendPageViewData() // 在组件卸载时发送数据
    }
  }

  // 记录用户点击事件
  async trackEvent(element: HTMLElement, eventType: string = 'click'): Promise<void> {
    // 使用同步方法获取设备ID，如果不存在则使用异步方法
    let deviceId = getDeviceIdSync()
    if (!deviceId) {
      deviceId = await this.deviceIdPromise
    }
    // 获取当前路由路径
    const currentPath = this.currentRoute?.path || window.location.pathname

    // 获取元素的文本内容
    let elementText = ''
    // 尝试获取元素的文本内容（优先获取innerText，其次是textContent）
    if (element.getAttribute('title') && element.getAttribute('title')?.trim()) {
      elementText = element.getAttribute('title')?.trim().substring(0, 100) || ''
    } else if (element.getAttribute('aria-label') && element.getAttribute('aria-label')?.trim()) {
      elementText = element.getAttribute('aria-label')?.trim() || ''
    } else if (element.innerText && element.innerText.trim()) {
      elementText = element.innerText.trim().substring(0, 100) // 限制长度
    } else if (element.textContent && element.textContent.trim()) {
      elementText = element.textContent.trim().substring(0, 100)
    }

    // 如果是链接，获取href属性
    const elementHref = element.tagName === 'A' ? (element as HTMLAnchorElement).href : ''

    const eventData: EventLogsData = {
      eventType,
      elementId: element.id || '',
      elementClass: Array.from(element.classList).join(' '),
      pageUrl: currentPath,
      elementText,
      elementPath: this.getElementPath(element),
      elementHref,
      deviceId
    }

    // 使用队列批量处理事件
    this.queueEvent(eventData)
  }

  // 获取元素的DOM路径，帮助识别元素在页面中的位置
  private getElementPath(element: HTMLElement, maxLength: number = 5): string {
    const path: string[] = []
    let currentElement: HTMLElement | null = element
    let index = 0

    while (currentElement && index < maxLength) {
      let identifier = currentElement.tagName.toLowerCase()

      // 添加ID（如果有）
      if (currentElement.id) {
        identifier += `#${currentElement.id}`
      }
      // 否则添加类名（如果有）
      else if (currentElement.className && typeof currentElement.className === 'string') {
        const classes = currentElement.className
          .split(' ')
          .filter((c) => c)
          .join('.')
        if (classes) {
          identifier += `.${classes}`
        }
      }

      path.unshift(identifier)
      currentElement = currentElement.parentElement
      index++
    }

    return path.join(' > ')
  }

  // 添加事件到队列
  queueEvent(eventData: EventLogsData): void {
    this.eventQueue.push(eventData)
    // 设置延迟发送
    if (!this.flushTimeout) {
      this.flushTimeout = window.setTimeout(() => {
        this.flushEvents()
      }, 2000) // 2秒后发送
    }

    // 队列达到10个事件时立即发送
    if (this.eventQueue.length >= 10) {
      this.flushEvents()
    }
  }

  // 发送队列中的所有事件
  flushEvents(): void {
    if (this.flushTimeout) {
      clearTimeout(this.flushTimeout)
      this.flushTimeout = null
    }

    if (this.eventQueue.length === 0) return

    // 复制队列并清空
    const events = [...this.eventQueue]
    // 获取当前用户信息
    const userInfo = this.getCurrentUserInfo()

    const batchEventLogsData: BatchEventLogsData = {
      userId: userInfo.userId || 0, // 未登录用户为0
      loginName: userInfo.loginName, // 未登录用户为空
      nickName: userInfo.nickName, // 未登录用户为空
      events: events
    }
    this.eventQueue = []

    // 发送批量事件
    AnalyticsService.batchEventLogs(batchEventLogsData).catch((err) =>
      console.error('批量发送事件失败:', err)
    )
  }

  // 强制刷新所有待发送的事件
  forceFlush(): void {
    this.flushEvents()
  }
}

export const analytics = new Analytics()

// 在页面卸载前强制发送所有事件
window.addEventListener('beforeunload', () => {
  analytics.forceFlush()
})
