/**
 * 登录相关模型定义
 */
export interface LoginParams {
  username?: string
  password?: string
  captcha?: string
}

/**
 * 验证码数据接口
 */
export interface CaptchaData {
  image: string // Base64 编码的图片
  key: string // 验证码的验证 key
}

// 接口定义：Token 响应
export interface TokenResponse {
  accessToken: string
  refreshToken: string
  deviceId: string
}
