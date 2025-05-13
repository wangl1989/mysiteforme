import axios, { InternalAxiosRequestConfig, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import EmojiText from '../emojo'
import { getDeviceIdSync } from '@/utils/deviceId'
import {
  getCurrentToken,
  getCurrentRefreshToken,
  isTokenExpiringSoon,
  isTokenExpired,
  handleTokenExpired
} from '@/utils/http/token'

// 创建axios实例
const axiosInstance = axios.create({
  timeout: 15000, // 请求超时时间(毫秒)
  baseURL: import.meta.env.VITE_API_URL, // API地址
  withCredentials: false, // 修改为 false，不携带 cookie
  transformRequest: [(data) => JSON.stringify(data)], // 请求数据转换为 JSON 字符串
  validateStatus: (status) => status >= 200 && status < 700, // 只接受 2xx 的状态码
  headers: {
    get: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' },
    post: { 'Content-Type': 'application/json;charset=utf-8' },
    put: { 'Content-Type': 'application/json;charset=utf-8' }
  },
  transformResponse: [
    (data, headers) => {
      // 响应数据转换
      const contentType = headers['content-type']
      if (contentType && contentType.includes('application/json')) {
        try {
          return JSON.parse(data)
        } catch {
          return data
        }
      }
      return data
    }
  ]
})

// 存储等待刷新 token 的请求队列
let refreshingTokenPromise: Promise<any> | null = null

// 请求刷新 token 的函数 - 修改函数名避免与参数名冲突
async function doRefreshToken(refreshTokenStr: string) {
  try {
    const userStore = useUserStore()
    // 替换为直接使用axios的原始请求
    const deviceId = localStorage.getItem('deviceId') || ''
    const baseURL = import.meta.env.VITE_API_URL // 获取API基础URL
    // 创建一个独立的axios实例，避免循环依赖
    const response = await axios({
      method: 'post',
      url: `${baseURL}/api/auth/refresh`,
      data: { refreshToken: refreshTokenStr },
      headers: {
        'Content-Type': 'application/json',
        'Device-Id': deviceId,
        'Accept-Language': userStore.language
      }
    })
    const result = response.data
    if (result.code === 200 && result.data) {
      // 更新 token
      userStore.setAccessToken(result.data.accessToken, result.data.refreshToken)
      return result.data.accessToken
    } else {
      throw new Error(result.message || '刷新令牌失败')
    }
  } catch (error) {
    console.error('Token刷新失败:', error)
    handleTokenExpired('令牌刷新失败，请重新登录')
    throw error
  }
}

// 处理请求拦截
axiosInstance.interceptors.request.use(
  async (request: InternalAxiosRequestConfig) => {
    // 设置语言
    const userStore = useUserStore()
    request.headers.set('Accept-Language', userStore.language)
    // 设置 Device-Id
    const deviceId = getDeviceIdSync() || ''
    if (deviceId) {
      request.headers.set('Device-Id', deviceId)
    }

    // 检查是否需要 token
    const token = getCurrentToken()
    if (token) {
      // 如果 token 即将过期且有刷新令牌，尝试刷新
      const refreshToken = getCurrentRefreshToken()
      const needsRefresh = isTokenExpiringSoon(token)

      if (needsRefresh && refreshToken) {
        if (!refreshingTokenPromise) {
          // 修改函数调用，使用新的函数名
          refreshingTokenPromise = doRefreshToken(refreshToken).finally(() => {
            refreshingTokenPromise = null
          })
        }

        try {
          // 等待 token 刷新完成
          const newToken = await refreshingTokenPromise
          request.headers.set('Authorization', `Bearer ${newToken}`)
        } catch (error) {
          // 如果刷新失败但旧 token 尚未完全过期，仍继续使用旧的
          if (!isTokenExpired(token)) {
            request.headers.set('Authorization', `Bearer ${token}`)
          } else {
            throw error // 如果已过期，则抛出错误，中断请求
          }
        }
      } else {
        // token 状态正常，直接使用
        request.headers.set('Authorization', `Bearer ${token}`)
      }
    }
    return request // 返回修改后的配置
  },
  (error) => {
    ElMessage.error(`服务器异常！ ${EmojiText[500]}`) // 显示错误消息
    return Promise.reject(error) // 返回拒绝的 Promise
  }
)

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (axios.isCancel(error)) {
      console.log('repeated request: ' + error.message)
    } else {
      // 处理 401 未授权错误
      if (error.response && error.response.status === 401) {
        // Token 无效或已过期
        handleTokenExpired('登录已过期，请重新登录')
        return Promise.reject(error)
      }

      // 处理其他错误
      const errorMessage = error.response?.data.message
      ElMessage.error(
        errorMessage
          ? `${errorMessage} ${EmojiText[500]}`
          : `请求超时或服务器异常！${EmojiText[500]}`
      )
    }
    return Promise.reject(error)
  }
)

// 请求
async function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  // 对 POST | PUT 请求特殊处理
  if (config.method?.toUpperCase() === 'POST' || config.method?.toUpperCase() === 'PUT') {
    // 如果已经有 data，则保留原有的 data
    if (config.params && !config.data) {
      config.data = config.params
      config.params = undefined // 使用 undefined 而不是空对象
    }
  }
  try {
    const res = await axiosInstance.request<T>({ ...config })
    return res.data
  } catch (e) {
    if (axios.isAxiosError(e)) {
      // 可以在这里处理 Axios 错误
    }
    return Promise.reject(e)
  }
}

// API 方法集合
const api = {
  get<T>(config: AxiosRequestConfig): Promise<T> {
    return request({ ...config, method: 'GET' }) // GET 请求
  },
  post<T>(config: AxiosRequestConfig): Promise<T> {
    return request({ ...config, method: 'POST' }) // POST 请求
  },
  put<T>(config: AxiosRequestConfig): Promise<T> {
    return request({ ...config, method: 'PUT' }) // PUT 请求
  },
  del<T>(config: AxiosRequestConfig): Promise<T> {
    return request({ ...config, method: 'DELETE' }) // DELETE 请求
  }
}

export default api
