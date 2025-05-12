export interface TableConfigModel {
  id: number // 表格ID
  tableName: string // 表格名称
  tableType: number // 表格类型
  tablePrefix?: string // 表格前缀
  schemaName: string // 数据库名称
  fieldCount: number // 字段数量
  businessName: string // 业务名称
  moduleName?: string // 模块名称
  packageName?: string // 包名称
  author?: string // 作者
  generatePath?: string // 生成路径
  options?: string // 选项
  remarks?: string // 备注
  createId: number | 0 // 创建者ID
  createDate: string | null // 创建日期
  updateId: number | 0 // 更新者ID
  updateDate: string | null // 更新日期 (list显示)
  delFlag: boolean | false // 删除标志
}

export interface TableFieldConfigModel {
  id: number // 字段ID
  tableName: string // 表格名称
  schemaName: string // schemaName的值
  businessName: string // 业务名称
  tableConfigId: number // table配置ID
  columnName: string // 字段名称
  columnType: string // 字段类型
  columnComment: string // 字段注释
  javaType: string // 映射的Java类型，如String
  javaFieldName: string // 映射的Java属性名，如userName
  isUnique?: boolean // 是否唯一
  isNullable?: boolean // 是否可为空
  isListVisible?: boolean // 是否在列表中显示
  isAddVisible?: boolean // 是否在新增中显示
  isEditVisible?: boolean // 是否在编辑中显示
  isDetailVisible?: boolean // 是否在详情中显示
  isQueryField?: boolean // 是否作为查询条件
  queryType?: string // 查询方式：如 '='
  formComponentType: string // 表单组件类型
  associatedType?: number // 关联的类型 1.字典类型  2.关联表名
  associatedDictType?: string // 字典类型
  associatedTable?: string // 关联表名称
  associatedTableField?: string // 关联表字段
  dictType?: string // 字典类型: 如果组件是Select/Radio/Checkbox，关联的字典类型，用于获取选项
  sort?: number // 排序
  validationRules?: string // 校验规则
  remarks?: string // 备注
}

export interface SimpleTableField {
  columnName?: string
  columnType?: string
  columnComment?: string
  charLength?: number
  numberLength?: number
  isNullable?: boolean
}

/**
 * 表格类型枚举
 */
export enum TableType {
  BASIC = 1, // 基本数据表
  TREE = 2 // 树形数据表
}

/**
 * 表格类型枚举文本映射
 */
export const TableTypeTextMap: Record<number, string> = {
  [TableType.BASIC]: '基本数据表',
  [TableType.TREE]: '树形数据表'
}

/**
 * 表格列表查询参数
 */
export interface TableConfigListParams {
  page: number // 页码
  limit: number // 每页条数
  tableName?: string // 表格名称
  tableType?: number // 表格类型
  schemaName?: string // 数据库名称
  businessName?: string // 业务名称
  delFlag?: boolean // 删除标志
  sortByUpdateDateAsc?: boolean // 更新日期升序
}

/**
 * 基础表格配置
 */
export interface BaseTableConfig {
  tableName: string // 表格名称
  tablePrefix?: string // 表格前缀
  schemaName: string // 数据库名称
  businessName: string // 业务名称
  moduleName?: string // 模块名称
  packageName?: string // 包名称
  author?: string // 作者
  generatePath?: string // 生成路径
  options?: string // 选项
  remarks?: string // 备注
}

/**
 * 新增表格请求参数
 */
export type AddTableConfigParams = BaseTableConfig

/**
 * 编辑表格请求参数
 */
export interface EditTableConfigParams extends BaseTableConfig {
  id: number // 表格ID
}

/**
 * 同步字段列表请求参数
 */
export interface SyncTableFieldListParams {
  tableName: string // 表格名称
  schemaName: string // 数据库名称
}

/**
 * 字段配置列表请求参数
 */
export interface TableFieldConfigListParams {
  tableConfigId: number // 表格配置ID
  columnName?: string // 字段名称
  businessName?: string // 业务名称
  formComponentType?: string // 表单组件类型
  isQueryField?: boolean // 是否作为查询参数
  page?: number // 页码
  limit?: number // 每页条数
}

/**
 * 编辑字段配置请求参数
 */
export interface EditTableFieldConfigParams {
  id: number // 字段ID
  businessName: string // 业务名称
  formComponentType: string // 表单组件类型
  dictType: string // 字典类型
  sort: number // 排序值
  isUnique?: boolean // 是否唯一
  isNullable?: boolean // 是否可为空
  isListVisible: boolean // 是否列表页可见
  isAddVisible: boolean // 是否新增时可见
  isEditVisible: boolean // 是否编辑时可见
  isDetailVisible: boolean // 是否详情时可见
  isQueryField: boolean // 是否作为查询参数
  queryType?: string
  validationRules?: string // 校验规则
  remarks?: string // 备注
  associatedType?: number | null // 1: 字典类型, 2: 关联表名
  associatedDictType?: string // 字典类型
  associatedTable?: string // 关联表名
  associatedTableField?: string // 关联表字段
}

export interface getSimpleTableFieldParam {
  tableName: string // 表格名称
  schemaName: string // 数据库名称
}
