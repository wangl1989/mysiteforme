/**
 * Token 相关工具函数
 */

import { useUserStore } from '@/store/modules/user'
import { router } from '@/router'
import { ElMessage } from 'element-plus'

/**
 * 解析 JWT Token，获取其中的 payload 部分
 * @param token JWT token 字符串
 * @returns 解析后的 payload 对象，解析失败则返回 null
 */
export function parseJwt(token: string): any {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        })
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('解析JWT Token出错:', error)
    return null
  }
}

/**
 * 检查 Token 是否即将过期（5分钟内）
 * @param token JWT token 字符串
 * @returns 是否即将过期
 */
export function isTokenExpiringSoon(token: string): boolean {
  const decoded = parseJwt(token)
  if (!decoded || !decoded.exp) return true

  const expirationTime = decoded.exp * 1000 // 转换为毫秒
  const currentTime = Date.now()
  const timeRemaining = expirationTime - currentTime

  // 如果剩余时间小于5分钟（300000毫秒），则返回 true
  return timeRemaining < 300000
}

/**
 * 检查 Token 是否已经过期
 * @param token JWT token 字符串
 * @returns 是否已过期
 */
export function isTokenExpired(token: string): boolean {
  const decoded = parseJwt(token)
  if (!decoded || !decoded.exp) return true

  const expirationTime = decoded.exp * 1000 // 转换为毫秒
  const currentTime = Date.now()

  return currentTime > expirationTime
}

/**
 * 处理 Token 过期的情况：登出并跳转到登录页
 */
export function handleTokenExpired(message = '登录信息已过期，请重新登录'): void {
  const userStore = useUserStore()
  ElMessage.error(message)
  userStore.logOut()
  router.push('/login')
}

/**
 * 获取当前 Token
 * @returns 当前的 access token，如果不存在则返回 null
 */
export function getCurrentToken(): string | null {
  const userStore = useUserStore()
  return userStore.info.accessToken || userStore.info.token || null
}

/**
 * 获取当前刷新 Token
 * @returns 当前的 refresh token，如果不存在则返回 null
 */
export function getCurrentRefreshToken(): string | null {
  const userStore = useUserStore()
  return userStore.info.refreshToken || null
}

/**
 * 获取设备 ID
 * @returns 当前缓存的设备 ID，不存在则返回空字符串
 */
export function getDeviceId(): string {
  return localStorage.getItem('deviceId') || ''
}
