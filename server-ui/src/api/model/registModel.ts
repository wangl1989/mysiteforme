/**
 * 发送邮箱地址
 */
export interface RegistSendEmailParam {
  email: string // 邮箱地址 (验证正则表达式：^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$)
}

/**
 * 验证邮箱地址
 */
export interface CheckEmailParam {
  email: string // 邮箱地址
  code: string // 验证码(6位数字)
}

/**
 * 注册账号参数
 */
export interface RegisterUserParam {
  email: string // 邮箱地址
  loginName: string // 登录账号 (验证正则表达式: ^[a-zA-Z][a-zA-Z0-9_-]{3,9}$ , 必须以英文字母开头，只能包含字母、数字、下划线，最小3个字符，最大10个字符)
  password: string // 密码 (验证正则表达式: ^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d\\W_]{8,20}$ ,密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字)
}

/**
 * 忘记密码参数
 */
export interface ForgetPasswordParam {
  email: string // 邮箱地址 (验证正则表达式：^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$)
}

/**
 * 验证忘记密码发送的邮箱地址
 */
export interface CheckRestPasswordEmailParam {
  email: string // 邮箱地址
  code: string // 验证码 6位数字
}

export interface ResetPasswordParam {
  email: string // 邮箱 规则同上
  password: string // 密码 规则同上
}
