export interface DictRecord {
  id: number // 字典ID
  createId: number | 0 // 创建者ID
  createDate: string | null // 创建日期
  updateId: number | 0 // 更新者ID
  updateDate: string | null // 更新日期 (list显示)
  delFlag: boolean | false // 删除标志
  value: string | null // 数据值 (list显示)
  label: string | null // 标签名 (list显示)
  type: string | null // 类型 (list显示)
  description: string | null // 描述 (list显示)
  sort: number | 0 // 排序 (list显示)
  parentId: number | 0 // 父级ID
}

/**
 * 字典列表查询参数
 */
export interface DictListParams {
  page: number // 页码
  limit: number // 每页条数
  type?: string // 类型
  value?: string // 数据值
  label?: string // 标签名
  description?: string // 描述
  sortByCreateDateAsc?: boolean // 创建日期升序
  sortBySortAsc?: boolean // 排序升序
}

/**
 * 基础字典参数
 */
export interface BaseDictParams {
  value: string // 数据值（必填）
  label: string // 标签名（必填）
  type: string // 类型（必填）
  description?: string // 描述（非必填）
  sort?: number // 排序（可选）
}

/**
 * 新增字典请求参数
 */
export type AddDictParams = BaseDictParams

/**
 * 编辑字典请求参数
 */
export interface EditDictParams extends BaseDictParams {
  id: number // 字典ID（必填）
}

/**
 * 编辑字典类型请求参数
 */
export interface EditDictTypeParams {
  oldType: string // 旧类型
  newType: string // 新类型
}
