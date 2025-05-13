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
            <span class="info-label">{{ $t('tableField.headerInfo.tableName') }}:</span>
            <span class="info-value">{{ tableInfo.tableName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">{{ $t('tableField.headerInfo.dbName') }}:</span>
            <span class="info-value">{{ tableInfo.schemaName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">{{ $t('tableField.headerInfo.tableType') }}:</span>
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
              :title="$t('tableField.button.add')"
              type="primary"
              @click="handleAdd"
              v-auth="'field_add'"
              v-ripple
            >
              {{ $t('tableField.button.add') }}
            </ElButton>
            <ElButton
              :title="$t('tableField.button.batchDelete')"
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedFields.length === 0"
              v-auth="'field_batch_delete'"
              v-ripple
            >
              {{ $t('tableField.button.batchDelete') }}
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
        :title="dialogType === 'add' ? $t('tableField.addTitle') : $t('tableField.editTitle')"
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
          <el-form-item :label="$t('tableField.form.fieldName')" prop="columnName">
            <el-input
              v-model="formData.columnName"
              :placeholder="$t('tableField.form.inputFieldName')"
              :disabled="dialogType === 'edit'"
            />
          </el-form-item>
          <el-form-item :label="$t('tableField.form.fieldType')" prop="type">
            <el-select
              v-model="formData.type"
              :placeholder="$t('tableField.form.selectFieldType')"
              style="width: 100%"
            >
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
          <el-form-item :label="$t('tableField.form.fieldLength')" prop="length">
            <el-input-number
              v-model="formData.length"
              :placeholder="$t('tableField.form.inputFieldLength')"
              style="width: 100%"
              :min="0"
              :disabled="!needLength(formData.type)"
              controls-position="right"
            />
          </el-form-item>
          <el-form-item :label="$t('tableField.form.isNotNull')" prop="isNullable">
            <el-switch
              v-model="formData.isNullable"
              :active-value="true"
              :inactive-value="false"
            ></el-switch>
          </el-form-item>
          <el-form-item :label="$t('tableField.form.comment')" prop="comment">
            <el-input
              v-model="formData.comment"
              type="textarea"
              :rows="3"
              :placeholder="$t('tableField.form.inputComment')"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button :title="$t('tableField.button.cancel')" @click="fieldDialogVisible = false">
            {{ $t('tableField.button.cancel') }}
          </el-button>
          <el-button
            :title="$t('tableField.button.confirm')"
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
          >
            {{ $t('tableField.button.confirm') }}
          </el-button>
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
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { useCheckedColumns, ColumnOption } from '@/composables/useCheckedColumns'
  import type { SearchFormItem } from '@/types/search-form'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

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
  const formFilters = ref<TableFieldListParam>({
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
      label: t('tableField.search.fieldName'),
      prop: 'columnName',
      type: 'input',
      config: {
        placeholder: t('tableField.search.inputFieldName'),
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: t('tableField.column.selection'), type: 'selection', width: 55 },
    { prop: 'columnName', label: t('tableField.column.fieldName'), minWidth: 150 },
    { prop: 'length', label: t('tableField.column.fieldLength'), minWidth: 100 },
    { prop: 'type', label: t('tableField.column.fieldType'), minWidth: 120 },
    {
      prop: 'isNullable',
      label: t('tableField.column.isNotNull'), // 注意：规则是 isNullable，显示文本反过来
      minWidth: 120,
      formatter: (row) =>
        h(
          ElTag,
          { type: !row.isNullable ? 'success' : 'danger' }, // API 返回 true 表示允许为空，false 表示不允许
          () => (!row.isNullable ? t('tableField.column.no') : t('tableField.column.yes'))
        )
    },
    { prop: 'comment', label: t('tableField.column.comment'), minWidth: 180 },
    {
      prop: 'actions',
      label: t('tableField.column.actions'),
      fixed: 'right',
      width: 180,
      formatter: (row) =>
        h(ElSpace, null, () => [
          h(ArtButtonTable, {
            title: t('tableField.button.edit'),
            type: 'edit',
            auth: 'field_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: t('tableField.button.delete'),
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
        ...formFilters.value,
        tableName: tableInfo.tableName, // 确保使用最新的tableInfo值
        schemaName: tableInfo.schemaName,
        tableType: tableInfo.tableType
      })
      if (res.success) {
        fieldList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('tableField.message.getListFailed'))
        console.error('字段列表加载失败:', res.message)
      }
    } catch (error) {
      console.error('获取字段列表失败:', error)
      ElMessage.error(t('tableField.message.getListError'))
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

        formFilters.value.tableName = props.tableName
        formFilters.value.schemaName = props.schemaName
        formFilters.value.tableType = props.tableType
        // 重置搜索条件和分页
        formFilters.value.columnName = initialSearchState.columnName
        formFilters.value.page = initialSearchState.page
        formFilters.value.limit = initialSearchState.limit

        // 确保有表名和数据库名再加载数据
        if (formFilters.value.tableName && formFilters.value.schemaName) {
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

      formFilters.value.tableName = props.tableName
      formFilters.value.schemaName = props.schemaName
      formFilters.value.tableType = props.tableType

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
      { required: true, message: t('tableField.validation.fieldNameRequired'), trigger: 'blur' },
      { max: 64, message: t('tableField.validation.fieldNameLength'), trigger: 'blur' }
    ],
    type: [
      { required: true, message: t('tableField.validation.fieldTypeRequired'), trigger: 'change' }
    ],
    length: [
      {
        validator: (rule, value, callback) => {
          if (needLength(formData.type) && (value === null || value === undefined || value < 0)) {
            callback(new Error(t('tableField.validation.fieldLengthRequired')))
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
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadFieldList()
  }

  // 重置查询
  const handleReset = () => {
    // 只重置搜索字段和分页，保持 schema/table 信息
    formFilters.value.columnName = initialSearchState.columnName
    formFilters.value.page = initialSearchState.page
    formFilters.value.limit = initialSearchState.limit
    loadFieldList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: TableFieldRecordModel[]) => {
    selectedFields.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadFieldList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
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
            ElMessage.success(
              dialogType.value === 'add'
                ? t('tableField.message.addSuccess')
                : t('tableField.message.editSuccess')
            )
            fieldDialogVisible.value = false
            loadFieldList() // 重新加载数据
          } else {
            ElMessage.error(
              res.message ||
                (dialogType.value === 'add'
                  ? t('tableField.message.addFailed')
                  : t('tableField.message.editFailed'))
            )
          }
        } catch (error) {
          console.error(dialogType.value === 'add' ? '添加字段失败:' : '编辑字段失败:', error)
          ElMessage.error(
            dialogType.value === 'add'
              ? t('tableField.message.addError')
              : t('tableField.message.editError')
          )
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedFields.value.length === 0) {
      ElMessage.warning(t('tableField.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(t('tableField.message.batchDeleteConfirm'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
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
            ElMessage.success(t('tableField.message.batchDeleteSuccess'))
          } else {
            ElMessage.warning(
              t('tableField.message.batchDeletePartial', {
                success: selectedFields.value.length - failedCount,
                failed: failedCount
              })
            )
          }
          loadFieldList() // 重新加载数据
        } catch (error) {
          console.error('批量删除字段过程中发生错误:', error)
          ElMessage.error(t('tableField.message.batchDeleteError'))
        } finally {
          loading.value = false // 结束 loading
        }
      })
      .catch(() => {
        ElMessage.info(t('tableField.message.cancelBatchDelete'))
      })
  }

  // 处理单个删除
  const handleDelete = (row: TableFieldRecordModel) => {
    ElMessageBox.confirm(
      t('tableField.message.deleteConfirm', { name: row.columnName }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await TableService.deleteField({
            tableName: tableInfo.tableName,
            fieldName: row.columnName
            // 根据API定义，不需要schemaName参数
          })
          if (res.success) {
            ElMessage.success(t('tableField.message.deleteSuccess'))
            loadFieldList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('tableField.message.deleteFailed'))
          }
        } catch (error) {
          console.error('删除字段失败:', error)
          ElMessage.error(t('tableField.message.deleteError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('tableField.message.cancelDelete'))
      })
  }

  // 获取表格类型对应的文本
  const getTableTypeText = (typeCode: number): string => {
    const map: Record<number, string> = {
      1: t('tableField.headerInfo.basicType'),
      2: t('tableField.headerInfo.treeType'),
      3: t('tableField.headerInfo.relationType')
    }
    return map[typeCode] || t('tableField.headerInfo.unknownType', { typeCode })
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
