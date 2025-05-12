export interface SystemRecord {
  id: number // 系统ID
  name: string // 网站名称
  url?: string // 网站网址
  openMessage?: true // 是否开启留言 单选，可选值[true/false]
  isNoName?: false // 是否可以匿名 单选，可选值[true/false]
  version?: string // 当前版本信息
  author?: string // 开发作者
  authorIcon?: string // 作者头像 带预览的图片上传控件
  fileUploadType: string // 文件上传方式 单选，可选值：api接口
  weibo?: string // 微博地址
  webServicekey?: string // webServicekey key
  qq?: string // QQ号
  git?: string // 码云gitee地址
  github?: string // github地址
  phone?: string // 手机号 验证手机号
  email?: string // 邮箱 验证邮箱
  address?: string // 地址
  logo?: string // 网站logo 带预览的图片上传控件
  server?: string // 服务器环境 单选，可选值[window/Linux/macOs]
  myDatabase?: string // 数据库版本：mysql，mongodb....
  maxUpload?: number // 文件最大上传限制 数字输入框
  keywords?: string // 默认关键字   网站优化keywords值
  description?: string // 网站描述  多行文本框
  powerby?: string // 版权信息
  record?: string // 网站备案号
  remarks: string | '' // 个人简介 多行文本框
}

export interface SiteUploadTypeResponse {
  typeCode: string // 上传类型code
  remarks: string // 备注
}

/**
 * 上传类型枚举
 */
export enum UploadTypeEnum {
  LOCAL = 'local', // 本地上传
  QINIU = 'qiniu', // 七牛云
  OSS = 'oss', // 阿里云
  COS = 'cos', // 腾讯云COS
  BITIFUL = 'bitiful' // 滨纷云
}
