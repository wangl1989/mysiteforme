// 表格列表页数据模型
export interface TableRecordModel {
  name: string // 表的名称
  comment: string // 表备注
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  schemaName: string // 数据库名称
  tableRows: number // 表有多少条数据
  createTime: string // 创建时间
  updateTime: string // 更新时间
}

// 表格列表查询参数对象
export interface TableListParam {
  page: number // 页码
  limit: number // 每页条数
  schemaName: string // 数据库名称
  tableName?: string // 表名称
}

// 新增表格参数对象
export interface AddTableParam {
  schemaName: string // 数据库名称
  tableName: string // 表名称
  comment: string // 表备注
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  fieldList: AddListTableFieldParam[] // 字段列表
}

// 新增字段参数对象
export interface AddListTableFieldParam {
  columnName: string // 字段名称
  length: number // 字段长度
  type: string // 字段类型
  isNullable: boolean // 是否可为空
  comment: string // 字段备注
}

// 编辑表格参数对象
export interface EditTableParam {
  schemaName: string // 数据库名称
  oldTableName: string // 旧表名称
  tableName: string // 新表名称
  comment: string // 表备注
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
}

// 字段列表数据模型
export interface TableFieldRecordModel {
  columnName: string // 字段名称
  length: number // 字段长度
  charLength?: number // 字符长度
  numberLength?: number // 数字长度
  type: string // 字段类型
  isNullable: boolean // 是否可为空
  comment: string // 字段备注
}

// 查询字段列表参数对象
export interface TableFieldListParam {
  page: number // 页码
  limit: number // 每页条数
  schemaName: string // 数据库名称
  tableName: string // 表名称
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  columnName?: string // 字段名称
}

// 查询字段列表参数对象
export interface ShowTableFieldListParam {
  schemaName: string // 数据库名称
  tableName: string // 表名称
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  columnName?: string // 字段名称
}

// 添加字段参数对象
export interface AddTableFieldParam {
  schemaName: string // 数据库名称
  tableName: string // 表名称
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  columnName: string // 字段名称
  length: number // 字段长度
  type: string // 字段类型
  isNullable: boolean // 是否可为空
  comment: string // 字段备注
}

// 编辑字段参数对象
export interface EditTableFieldParam {
  schemaName: string // 数据库名称
  tableName: string // 表名称
  tableType: number // table类型 1.基本类型  2.树结构类型 3.关联类型
  columnName: string // 字段名称
  length: number // 字段长度
  type: string // 字段类型
  isNullable: boolean // 是否可为空
  comment: string // 字段备注
  oldName: string // 旧字段名称
}

// 判断是否存在参数对象
export interface FieldIsExistParam {
  schemaName: string // 数据库名称
  fieldName: string // 字段名称
  tableName: string // 表名称
}

// 删除字段参数对象
export interface DeleteFieldParam {
  tableName: string // 表名称
  fieldName: string // 字段名称
}
