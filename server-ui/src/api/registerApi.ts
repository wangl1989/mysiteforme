import request from '@/utils/http'
import { BaseResult } from '@/types/axios'
import {
  RegistSendEmailParam,
  CheckEmailParam,
  RegisterUserParam,
  ForgetPasswordParam,
  CheckRestPasswordEmailParam,
  ResetPasswordParam
} from './model/registModel'

export class RegisterService {
  /**
   * 发送邮箱验证码
   */
  static sendEmail(param: RegistSendEmailParam) {
    return request.post<BaseResult>({
      url: '/register/sendEmail',
      data: param
    })
  }

  /**
   * 验证邮箱地址
   */
  static checkEmail(param: CheckEmailParam) {
    return request.post<BaseResult>({
      url: '/register/checkEmail',
      data: param
    })
  }

  /**
   * 注册账号
   */
  static registerUser(param: RegisterUserParam) {
    return request.post<BaseResult>({
      url: '/register/user',
      data: param
    })
  }

  /**
   * 忘记密码
   */
  static forgetPassword(param: ForgetPasswordParam) {
    return request.post<BaseResult>({
      url: '/register/forgetPassword',
      data: param
    })
  }

  /**
   * 验证忘记密码发送的邮箱地址
   */
  static checkRestPassowrdEmail(param: CheckRestPasswordEmailParam) {
    return request.post<BaseResult>({
      url: '/register/checkRestPasswordEmail',
      data: param
    })
  }

  /**
   * 重置密码
   */
  static resetPassword(param: ResetPasswordParam) {
    return request.put<BaseResult>({
      url: '/register/resetPassword',
      data: param
    })
  }
}
