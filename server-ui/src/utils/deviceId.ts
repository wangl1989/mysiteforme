import FingerprintJS from '@fingerprintjs/fingerprintjs'

// 单例模式实现设备ID管理
class DeviceIdManager {
  private static instance: DeviceIdManager
  private deviceIdPromise: Promise<string> | null = null

  private constructor() {}

  public static getInstance(): DeviceIdManager {
    if (!DeviceIdManager.instance) {
      DeviceIdManager.instance = new DeviceIdManager()
    }
    return DeviceIdManager.instance
  }

  /**
   * 获取或生成设备ID
   * 使用Promise缓存确保只调用一次FingerprintJS
   */
  public getDeviceId(): Promise<string> {
    if (!this.deviceIdPromise) {
      this.deviceIdPromise = this.initDeviceId()
    }
    return this.deviceIdPromise
  }

  /**
   * 同步获取设备ID（如果已经初始化）
   * 如果未初始化，返回null
   */
  public getDeviceIdSync(): string | null {
    return localStorage.getItem('deviceId')
  }

  /**
   * 初始化设备ID
   */
  private async initDeviceId(): Promise<string> {
    const storedDeviceId = localStorage.getItem('deviceId')
    if (storedDeviceId) {
      return storedDeviceId
    }

    try {
      // 加载 FingerprintJS 代理
      const fp = await FingerprintJS.load()
      // 获取访问者 ID
      const result = await fp.get()
      const visitorId = result.visitorId
      localStorage.setItem('deviceId', visitorId)
      return visitorId
    } catch (error) {
      console.error('FingerprintJS error:', error)
      // FingerprintJS 出错时的回退策略
      const fallbackId = `fallback-${Date.now()}-${Math.random().toString(36).substring(2)}`
      localStorage.setItem('deviceId', fallbackId)
      return fallbackId
    }
  }
}

// 导出单例实例
export const deviceIdManager = DeviceIdManager.getInstance()

// 便捷函数
export const getDeviceId = () => deviceIdManager.getDeviceId()
export const getDeviceIdSync = () => deviceIdManager.getDeviceIdSync()
