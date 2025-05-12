<template>
  <el-dialog
    :model-value="props.visible"
    width="90%"
    :append-to-body="true"
    :destroy-on-close="true"
    fullscreen
    @closed="handleClose"
    @update:model-value="(val) => emit('update:visible', val)"
    class="table-field-dialog"
  >
    <div class="table-field-content">
      <div class="header-info-card">
        <div class="header-info">
          <div class="info-item">
            <span class="info-label">表名称:</span>
            <span class="info-value">{{ tableInfo.tableName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">数据库名称:</span>
            <span class="info-value">{{ tableInfo.schemaName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">表类型:</span>
            <span class="info-value">
              <el-tag size="small">{{ getTableTypeText(tableInfo.tableType) }}</el-tag>
            </span>
          </div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="search"
        :isExpand="true"
      />

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadFieldList"
        >
          <template #left>
            <ElButton
              title="新增表格字段"
              type="primary"
              @click="handleAdd"
              v-auth="'field_add'"
              v-ripple
            >
              新增字段
            </ElButton>
            <ElButton
              title="批量删除表格字段"
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedFields.length === 0"
              v-auth="'field_batch_delete'"
              v-ripple
            >
              批量删除
            </ElButton>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="fieldList"
          selection
          v-loading="loading"
          :currentPage="formFilters.page"
          :pageSize="formFilters.limit"
          :total="pagination.total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          @selection-change="handleSelectionChange"
          height="100%"
          :marginTop="10"
        >
          <template #default>
            <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
          </template>
        </ArtTable>
      </ElCard>

      <!-- 添加/编辑字段对话框 (保持内部结构不变) -->
      <el-dialog
        v-model="fieldDialogVisible"
        :title="dialogType === 'add' ? '新增字段' : '编辑字段'"
        width="600px"
        :close-on-click-modal="false"
        :destroy-on-close="true"
        :append-to-body="true"
      >
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          label-position="right"
        >
          <el-form-item label="字段名称" prop="columnName">
            <el-input
              v-model="formData.columnName"
              placeholder="请输入字段名称"
              :disabled="dialogType === 'edit'"
            />
          </el-form-item>
          <el-form-item label="字段类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择字段类型" style="width: 100%">
              <el-option label="varchar" value="varchar" />
              <el-option label="int" value="int" />
              <el-option label="bigint" value="bigint" />
              <el-option label="tinyint" value="tinyint" />
              <el-option label="double" value="double" />
              <el-option label="decimal" value="decimal" />
              <el-option label="text" value="text" />
              <el-option label="datetime" value="datetime" />
              <el-option label="date" value="date" />
              <el-option label="time" value="time" />
              <el-option label="timestamp" value="timestamp" />
              <el-option label="blob" value="blob" />
            </el-select>
          </el-form-item>
          <el-form-item label="字段长度" prop="length">
            <el-input-number
              v-model="formData.length"
              placeholder="请输入长度"
              style="width: 100%"
              :min="0"
              :disabled="!needLength(formData.type)"
              controls-position="right"
            />
          </el-form-item>
          <el-form-item label="不是 null" prop="isNullable">
            <el-switch
              v-model="formData.isNullable"
              :active-value="true"
              :inactive-value="false"
            ></el-switch>
          </el-form-item>
          <el-form-item label="字段注释" prop="comment">
            <el-input
              v-model="formData.comment"
              type="textarea"
              :rows="3"
              placeholder="请输入字段注释"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button
            :title="dialogType === 'add' ? '取消新增字段' : '取消编辑字段'"
            @click="fieldDialogVisible = false"
            >取消</el-button
          >
          <el-button
            :title="dialogType === 'add' ? '保存新增字段' : '保存编辑字段'"
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >确定</el-button
          >
        </template>
      </el-dialog>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
  import { ref, reactive, watch, h, nextTick, onMounted } from 'vue'
  import {
    ElMessage,
    ElMessageBox,
    ElDialog, // 需要显式导入使用的 Element Plus 组件
    ElTag,
    ElButton,
    ElCard,
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElInputNumber,
    ElSwitch,
    ElSpace
  } from 'element-plus'
  import { TableService } from '@/api/tableApi'
  import {
    TableFieldRecordModel,
    TableFieldListParam,
    AddTableFieldParam,
    EditTableFieldParam
  } from '@/api/model/tableModel'
  import type { FormInstance, FormRules } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue' // 需要导入
  import { useCheckedColumns, ColumnOption } from '@/composables/useCheckedColumns'
  import type { SearchFormItem } from '@/types/search-form' // 确认路径

  // 定义属性
  const props = defineProps({
    visible: {
      type: Boolean,
      default: false
    },
    tableName: {
      type: String,
      required: true
    },
    schemaName: {
      type: String,
      required: true
    },
    tableType: {
      type: Number,
      required: true
    }
  })

  // 定义事件
  const emit = defineEmits(['update:visible'])

  // 对话框可见状态 (主对话框)
  // const dialogVisible = ref(false); // 移除内部状态

  // 表信息 (从 props 获取，保持响应式更新)
  const tableInfo = reactive({
    tableName: props.tableName,
    schemaName: props.schemaName,
    tableType: props.tableType
  })

  // 加载状态
  const loading = ref(false)
  const submitLoading = ref(false) // 提交字段表单的加载状态

  // 字段数据列表
  const fieldList = ref<TableFieldRecordModel[]>([])

  // 选中的字段记录
  const selectedFields = ref<TableFieldRecordModel[]>([])

  // 搜索表单初始值
  const initialSearchState = {
    page: 1,
    limit: 10,
    columnName: ''
  }

  // 响应式搜索表单数据 (包含来自 props 的固定参数)
  const formFilters = reactive<TableFieldListParam>({
    ...initialSearchState,
    schemaName: props.schemaName,
    tableName: props.tableName,
    tableType: props.tableType
  })

  // 分页信息 (仅需 total)
  const pagination = reactive({
    total: 0
  })

  // 搜索栏配置
  const formItems: SearchFormItem[] = [
    {
      label: '字段名称',
      prop: 'columnName',
      type: 'input',
      config: {
        placeholder: '请输入字段名称搜索',
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: '勾选', type: 'selection', width: 55 },
    { prop: 'columnName', label: '字段名称', minWidth: 150 },
    { prop: 'length', label: '字段长度', minWidth: 100 },
    { prop: 'type', label: '字段类型', minWidth: 120 },
    {
      prop: 'isNullable',
      label: '不是 null', // 注意：规则是 isNullable，显示文本反过来
      minWidth: 120,
      formatter: (row) =>
        h(
          ElTag,
          { type: !row.isNullable ? 'success' : 'danger' }, // API 返回 true 表示允许为空，false 表示不允许
          () => (!row.isNullable ? '否' : '是')
        )
    },
    { prop: 'comment', label: '字段注释', minWidth: 180 },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 180,
      formatter: (row) =>
        h(ElSpace, null, () => [
          h(ArtButtonTable, {
            title: '编辑字段',
            type: 'edit',
            auth: 'field_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: '删除字段',
            type: 'delete',
            auth: 'field_batch_delete', // 注意：权限标识可能需要区分单行删除和批量删除
            onClick: () => handleDelete(row)
          })
        ])
    }
  ]

  // 使用 Hook 获取响应式的列配置和选中状态
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 添加/编辑字段对话框相关
  const fieldDialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>() // 这是内层表单的 ref

  // 内层表单数据的初始默认值
  const defaultFormData = {
    columnName: '',
    length: 50,
    type: 'varchar',
    isNullable: false,
    comment: '',
    oldName: ''
  }

  // 内层表单数据
  const formData = reactive<AddTableFieldParam & { oldName?: string }>({
    schemaName: props.schemaName,
    tableName: props.tableName,
    tableType: props.tableType,
    ...defaultFormData
  })

  // 加载字段列表数据
  const loadFieldList = async () => {
    if (!tableInfo.tableName || !tableInfo.schemaName) {
      console.error('表名或数据库名为空，无法加载字段列表')
      return
    }

    loading.value = true
    try {
      // 使用 formFilters (已包含 schema, table 等信息)
      const res = await TableService.getFieldList({
        ...formFilters,
        tableName: tableInfo.tableName, // 确保使用最新的tableInfo值
        schemaName: tableInfo.schemaName,
        tableType: tableInfo.tableType
      })
      if (res.success) {
        fieldList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || '获取字段列表失败')
        console.error('字段列表加载失败:', res.message)
      }
    } catch (error) {
      console.error('获取字段列表失败:', error)
      ElMessage.error('获取字段列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 监听父组件传递的 props 更新
  watch(
    () => props.visible,
    (newVal) => {
      if (newVal) {
        // 当对话框打开时，更新 tableInfo 和 formFilters 并加载数据
        tableInfo.tableName = props.tableName
        tableInfo.schemaName = props.schemaName
        tableInfo.tableType = props.tableType

        formFilters.tableName = props.tableName
        formFilters.schemaName = props.schemaName
        formFilters.tableType = props.tableType
        // 重置搜索条件和分页
        formFilters.columnName = initialSearchState.columnName
        formFilters.page = initialSearchState.page
        formFilters.limit = initialSearchState.limit

        // 确保有表名和数据库名再加载数据
        if (formFilters.tableName && formFilters.schemaName) {
          loadFieldList()
        } else {
          console.error('表名或数据库名为空，无法加载字段列表')
        }
      }
    },
    { immediate: true } // 添加immediate，确保组件创建时触发一次
  )

  // 组件挂载时初始化
  onMounted(() => {
    // 如果visible为true且有表名和数据库名，则加载数据
    if (props.visible && props.tableName && props.schemaName) {
      tableInfo.tableName = props.tableName
      tableInfo.schemaName = props.schemaName
      tableInfo.tableType = props.tableType

      formFilters.tableName = props.tableName
      formFilters.schemaName = props.schemaName
      formFilters.tableType = props.tableType

      loadFieldList()
    }
  })

  // 是否需要长度字段
  const needLength = (type: string): boolean => {
    const lowerType = (type || '').toLowerCase()
    return ['varchar', 'int', 'bigint', 'tinyint', 'decimal', 'double'].includes(lowerType)
  }

  // 内层表单验证规则
  const formRules = reactive<FormRules>({
    columnName: [
      { required: true, message: '请输入字段名称', trigger: 'blur' },
      { max: 64, message: '长度不能超过64个字符', trigger: 'blur' }
    ],
    type: [{ required: true, message: '请选择字段类型', trigger: 'change' }],
    length: [
      {
        validator: (rule, value, callback) => {
          if (needLength(formData.type) && (value === null || value === undefined || value < 0)) {
            callback(new Error('请输入有效的字段长度'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  })

  // 搜索
  const search = () => {
    formFilters.page = 1 // 搜索时重置为第一页
    loadFieldList()
  }

  // 重置查询
  const handleReset = () => {
    // 只重置搜索字段和分页，保持 schema/table 信息
    formFilters.columnName = initialSearchState.columnName
    formFilters.page = initialSearchState.page
    formFilters.limit = initialSearchState.limit
    loadFieldList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: TableFieldRecordModel[]) => {
    selectedFields.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.page = page
    loadFieldList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.limit = size
    formFilters.page = 1 // 切换每页数量时重置为第一页
    loadFieldList()
  }

  // 重置内层表单 (尝试使用 resetFields)
  const resetInnerForm = () => {
    console.log('开始重置表单数据')
    // 先手动重置表单数据到初始状态
    Object.keys(defaultFormData).forEach((key) => {
      // @ts-expect-error：动态key访问
      formData[key] = defaultFormData[key]
    })

    console.log('重置后的表单数据:', formData)

    // 然后再使用resetFields清除校验状态
    nextTick(() => {
      if (formRef.value) {
        formRef.value.clearValidate()
        // 确保 schema/table 信息正确 (从 tableInfo 获取最新值)
        formData.schemaName = tableInfo.schemaName
        formData.tableName = tableInfo.tableName
        formData.tableType = tableInfo.tableType
      }
    })
  }

  // 处理添加字段
  const handleAdd = () => {
    dialogType.value = 'add'
    // 先重置数据，再打开弹窗
    resetInnerForm()
    fieldDialogVisible.value = true
  }

  // 处理编辑字段
  const handleEdit = (row: TableFieldRecordModel) => {
    dialogType.value = 'edit'
    fieldDialogVisible.value = true

    nextTick(() => {
      if (formRef.value) {
        formRef.value.clearValidate() // 编辑时只清除校验
      }
      // 填充数据
      formData.schemaName = tableInfo.schemaName
      formData.tableName = tableInfo.tableName
      formData.tableType = tableInfo.tableType
      formData.columnName = row.columnName
      formData.oldName = row.columnName // 保存旧字段名
      formData.length = row.length
      formData.type = row.type
      formData.isNullable = row.isNullable
      formData.comment = row.comment
    })
  }

  // 提交内层表单
  const submitForm = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          let res

          // 确保提交时 schema/table/type 是最新的
          const submitData = {
            ...formData,
            schemaName: tableInfo.schemaName,
            tableName: tableInfo.tableName,
            tableType: tableInfo.tableType
          }

          if (dialogType.value === 'add') {
            // AddTableFieldParam 不需要 oldName，直接传递 submitData
            // 类型断言确保 submitData 符合 AddTableFieldParam (假设它符合)
            res = await TableService.addField(submitData as AddTableFieldParam)
          } else {
            // 编辑字段
            const editParams: EditTableFieldParam = {
              ...submitData,
              oldName: formData.oldName || submitData.columnName // 确保 oldName 有值
            }
            res = await TableService.editField(editParams)
          }

          if (res.success) {
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '编辑成功')
            fieldDialogVisible.value = false
            loadFieldList() // 重新加载数据
          } else {
            ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '编辑失败'))
          }
        } catch (error) {
          console.error(dialogType.value === 'add' ? '添加字段失败:' : '编辑字段失败:', error)
          ElMessage.error(dialogType.value === 'add' ? '添加字段时发生错误' : '编辑字段时发生错误')
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedFields.value.length === 0) {
      ElMessage.warning('请至少选择一个字段')
      return
    }

    ElMessageBox.confirm('确定要删除选中的字段吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        loading.value = true // 开始 loading
        const deletePromises = selectedFields.value.map((field) =>
          TableService.deleteField({
            tableName: tableInfo.tableName,
            fieldName: field.columnName
            // 根据API定义，不需要schemaName参数
          })
        )

        try {
          const results = await Promise.allSettled(deletePromises)
          const failedCount = results.filter(
            (r) => r.status === 'rejected' || (r.status === 'fulfilled' && !r.value.success)
          ).length

          if (failedCount === 0) {
            ElMessage.success('批量删除成功')
          } else {
            ElMessage.warning(
              `批量删除完成，${selectedFields.value.length - failedCount} 个成功，${failedCount} 个失败。`
            )
          }
          loadFieldList() // 重新加载数据
        } catch (error) {
          console.error('批量删除字段过程中发生错误:', error)
          ElMessage.error('批量删除字段时发生未知错误')
        } finally {
          loading.value = false // 结束 loading
        }
      })
      .catch(() => {
        ElMessage.info('取消了批量删除操作') // 添加 .catch 处理
      })
  }

  // 处理单个删除
  const handleDelete = (row: TableFieldRecordModel) => {
    ElMessageBox.confirm(`确定要删除字段 "${row.columnName}" 吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await TableService.deleteField({
            tableName: tableInfo.tableName,
            fieldName: row.columnName
            // 根据API定义，不需要schemaName参数
          })
          if (res.success) {
            ElMessage.success('删除成功')
            loadFieldList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除字段失败:', error)
          ElMessage.error('删除字段时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了删除操作') // 添加 .catch 处理
      })
  }

  // 获取表格类型对应的文本
  const getTableTypeText = (typeCode: number): string => {
    const map: Record<number, string> = {
      1: '基本类型',
      2: '树结构类型',
      3: '关联类型'
    }
    return map[typeCode] || `未知类型(${typeCode})`
  }

  // 对话框关闭处理函数
  const handleClose = () => {
    emit('update:visible', false)
  }
</script>

<style lang="scss" scoped>
  /* 使 Dialog 内容区域可滚动 */
  .table-field-dialog {
    :deep(.el-dialog__body) {
      height: calc(100% - 54px); // 减去 header 高度，可能需要微调
      padding: 0; // 移除默认 padding，由内部控制
      overflow: hidden; // 防止 body 滚动
    }
  }

  .table-field-content {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 20px; // 内部内容区域的 padding
    overflow-y: auto; // 内容本身可滚动
  }

  .header-info-card {
    padding: 15px 20px; // 调整 padding
    margin-bottom: 15px; // 调整 margin
    background: #f8f9fa; // 简化背景
    border-left: 4px solid #4c84ff;
    border-radius: 4px; // 减小圆角
    // box-shadow: 0 1px 4px 0 rgb(0 0 0 / 5%); // 减淡阴影
  }

  .header-info {
    display: flex;
    flex-wrap: wrap;
    gap: 15px 25px; // 调整间距
    font-size: 14px;

    .info-item {
      display: flex;
      align-items: center;

      .info-label {
        margin-right: 8px; // 调整间距
        color: #606266;
      }

      .info-value {
        display: inline-flex;
        align-items: center;
        font-weight: 500; // 调整字重
        color: #303133;
      }
    }
  }

  .art-table-card {
    display: flex;
    flex: 1; // 让卡片占据剩余空间
    flex-direction: column;
    margin-top: 15px; // 与搜索栏间距
    overflow: hidden; // 防止内容溢出卡片

    :deep(.el-card__body) {
      display: flex;
      flex-direction: column;
      height: 100%; // 让 card body 也撑满
      padding: 15px; // 可以根据需要调整内边距
      overflow: hidden; // 防止内部溢出
    }
  }
</style>
