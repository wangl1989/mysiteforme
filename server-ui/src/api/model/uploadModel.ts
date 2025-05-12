export interface UploadRecord {
  id: number //上传工具ID
  createDate: string //创建日期
  delFlag: boolean //是否删除标志
  remarks: string | null //备注
  type: string | null //类型 本地（local），qiniu（七牛云），oss（阿里云），cos（腾讯云），bitiful（滨纷云）
  basePath: string | null //前缀路径
  bucketName: string | null //bucket的目录名称
  dir: string | null //文件存储目录
  accessKey: string | null //accessKey
  secretKey: string | null //secretKey
  endpoint: string | null //地域
  region: string | null //区域
  testAccess: boolean | false //上传测试结果
  testWebUrl: string | null
  enable: boolean | false //是否启用
}

export interface UploadParams {
  file: any
  name: string
}

// 添加FormData类型声明，用于文件上传
export type UploadFormData = FormData

// 普通图片上传返回对象数据
export interface UploadResponse {
  name?: string
  url: string
}

/**
 * 日志列表查询参数
 */
export interface UploadListParams {
  page: number
  limit: number
  type?: string
  sortByCreateDateAsc?: boolean
}

export interface BaseUploadBaseInfoParam {
  type: string | null //类型 本地（local），qiniu（七牛云），oss（阿里云），cos（腾讯云），bitiful（滨纷云）
  basePath: string | null //前缀路径
  bucketName: string | null //bucket的目录名称
  dir: string | null //文件存储目录
  accessKey: string | null //accessKey
  secretKey: string | null //secretKey
  endpoint: string | null //地域
  region: string | null //区域
  testAccess: boolean | false //上传测试结果
  testWebUrl: string | null
  enable: boolean | false //是否启用
  remarks: string | null //备注
}

/**
 * 新增上传基本信息请求参数
 */
export type AddUploadBaseInfoParam = BaseUploadBaseInfoParam

/**
 * 编辑上传基础信息请求参数
 */
export interface EditUploadBaseInfoParam extends BaseUploadBaseInfoParam {
  id: number // 基础信息ID（必填）
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

/**
 * 上传基本对象
 */
export interface BaseUpload {
  type: string // 上传类型（必填）
  basePath: string // HTTP前缀（必填）
  testAccess: boolean // 上传测试结果是否成功
  testWebUrl: string // 上传测试地址
  enable?: boolean // 是否启用
  remarks?: string //备注信息
}

/**
 * 本地上传对象
 */
export interface LocalUpload extends BaseUpload {
  dir: string
}

/**
 * 七牛上传对象
 */
export interface QiniuUpload extends BaseUpload {
  bucketName: string
  dir: string
  secretKey: string
  accessKey: string
}

/**
 * 阿里云上传对象
 */
export interface OssUpload extends QiniuUpload {
  endpoint: string
}

/**
 * 腾讯云上传对象
 */
export interface CosUpload extends QiniuUpload {
  region: string
}

/**
 * 缤纷云上传对象
 */
export interface BitifulUpload extends QiniuUpload {
  endpoint: string
  region: string
}
