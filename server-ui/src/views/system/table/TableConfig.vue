<template>
  <ArtTableFullScreen>
    <div class="tableconfig-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters as any"
        :items="formItems"
        @reset="resetQuery"
        @search="search"
        :isExpand="true"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadTableConfigList"
        >
          <template #left>
            <ElButton type="primary" @click="handleAdd" v-auth="'tableconfig_add'" v-ripple>
              {{ $t('tableConfig.button.add') }}
            </ElButton>
            <ElButton
              :title="$t('tableConfig.button.batchDelete')"
              type="danger"
              @click="handleBatchDelete"
              v-auth="'tableconfig_batch_delete'"
              v-ripple
            >
              {{ $t('tableConfig.button.batchDelete') }}
            </ElButton>
            <ElButton
              type="success"
              @click="handleDownloadCode"
              v-auth="'tableconfig_download'"
              v-ripple
            >
              {{ $t('tableConfig.button.downloadCode') }}
            </ElButton>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="tableConfigList"
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

      <!-- 添加/编辑对话框 -->
      <ElDialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? $t('tableConfig.addTitle') : $t('tableConfig.editTitle')"
        width="580px"
        :close-on-click-modal="false"
      >
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          label-position="right"
        >
          <el-form-item :label="$t('tableConfig.form.dbName')" prop="schemaName">
            <el-select
              v-model="formData.schemaName"
              :placeholder="$t('tableConfig.form.selectDbName')"
              filterable
              clearable
              @change="handleSchemaChange"
              style="width: 100%"
            >
              <el-option v-for="item in schemaNameList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.tableName')" prop="tableName">
            <el-select
              v-model="formData.tableName"
              :placeholder="$t('tableConfig.form.selectTableName')"
              filterable
              clearable
              style="width: 100%"
              :disabled="!formData.schemaName"
            >
              <el-option v-for="item in tableNameList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.tablePrefix')" prop="tablePrefix">
            <el-input
              v-model="formData.tablePrefix"
              :placeholder="$t('tableConfig.form.inputTablePrefix')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.businessName')" prop="businessName">
            <el-input
              v-model="formData.businessName"
              :placeholder="$t('tableConfig.form.inputBusinessName')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.moduleName')" prop="moduleName">
            <el-input
              v-model="formData.moduleName"
              :placeholder="$t('tableConfig.form.inputModuleName')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.packageName')" prop="packageName">
            <el-input
              v-model="formData.packageName"
              :placeholder="$t('tableConfig.form.inputPackageName')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.author')" prop="author">
            <el-input v-model="formData.author" :placeholder="$t('tableConfig.form.inputAuthor')" />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.generatePath')" prop="generatePath">
            <el-input
              v-model="formData.generatePath"
              :placeholder="$t('tableConfig.form.inputGeneratePath')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.options')" prop="options">
            <el-input
              v-model="formData.options"
              type="textarea"
              :rows="3"
              :placeholder="$t('tableConfig.form.inputOptions')"
            />
          </el-form-item>
          <el-form-item :label="$t('tableConfig.form.remarks')" prop="remarks">
            <el-input
              v-model="formData.remarks"
              type="textarea"
              :rows="3"
              :placeholder="$t('tableConfig.form.inputRemarks')"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button
            :title="
              dialogType === 'add'
                ? $t('tableConfig.button.cancel')
                : $t('tableConfig.button.cancel')
            "
            @click="dialogVisible = false"
            >{{ $t('tableConfig.button.cancel') }}</el-button
          >
          <el-button
            :title="
              dialogType === 'add'
                ? $t('tableConfig.button.confirm')
                : $t('tableConfig.button.confirm')
            "
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >{{ $t('tableConfig.button.confirm') }}</el-button
          >
        </template>
      </ElDialog>

      <!-- 字段配置组件 -->
      <TableFieldConfig
        :visible="showFieldConfig"
        @update:visible="showFieldConfig = $event"
        :tableConfig="currentTableConfig"
        @refresh="refreshTableData"
      />
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, h } from 'vue'
  import {
    ElMessage,
    ElMessageBox,
    ElButton,
    ElTag,
    ElSpace,
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElDialog,
    ElTableColumn
  } from 'element-plus'
  import { TableService } from '@/api/tableConfigApi'
  import {
    TableConfigModel,
    TableConfigListParams,
    BaseTableConfig,
    TableTypeTextMap,
    SyncTableFieldListParams
  } from '@/api/model/tableConfigModel'
  import type { FormInstance, FormRules } from 'element-plus'
  import { formatDate } from '@/utils/date'
  import TableFieldConfig from './TableFieldConfig.vue'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { useCheckedColumns, ColumnOption } from '@/composables/useCheckedColumns'
  import type { SearchFormItem } from '@/types/search-form'
  import FileSaver from 'file-saver'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 加载状态
  const loading = ref(false)
  const submitLoading = ref(false)

  // 表格配置数据列表
  const tableConfigList = ref<TableConfigModel[]>([])

  // 下拉列表数据
  const schemaNameList = ref<string[]>([])
  const tableNameList = ref<string[]>([])

  // 选中的表格配置记录
  const selectedConfigs = ref<TableConfigModel[]>([])

  // 当前选中的表格配置(用于字段管理)
  const currentTableConfig = ref<TableConfigModel | undefined>(undefined)
  // 字段配置对话框显示状态
  const showFieldConfig = ref(false)

  // 搜索表单初始值
  const initialSearchState = {
    page: 1,
    limit: 10,
    tableName: '',
    schemaName: '',
    businessName: '',
    delFlag: undefined,
    sortByUpdateDateAsc: false
  }

  // 响应式搜索表单数据
  const formFilters = ref<TableConfigListParams & { delFlag?: string }>({
    ...initialSearchState,
    delFlag: undefined
  })

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // 搜索栏配置
  const formItems: SearchFormItem[] = [
    {
      label: t('tableConfig.search.tableName'),
      prop: 'tableName',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: t('tableConfig.search.inputTableName'),
        clearable: true
      }
    },
    {
      label: t('tableConfig.search.dbName'),
      labelWidth: '100px',
      prop: 'schemaName',
      type: 'select',
      elColSpan: 5,
      options: () => schemaNameList.value.map((name) => ({ label: name, value: name })),
      config: {
        placeholder: t('tableConfig.search.selectDbName'),
        clearable: true
      }
    },
    {
      label: t('tableConfig.search.businessName'),
      labelWidth: '100px',
      prop: 'businessName',
      type: 'input',
      elColSpan: 6,
      config: {
        placeholder: t('tableConfig.search.inputBusinessName'),
        clearable: true
      }
    },
    {
      label: t('tableConfig.search.status'),
      labelWidth: '100px',
      prop: 'delFlag',
      type: 'select',
      elColSpan: 5,
      options: () => [
        { label: t('tableConfig.search.normal'), value: '0' },
        { label: t('tableConfig.search.deleted'), value: '1' }
      ],
      config: {
        placeholder: t('tableConfig.search.selectStatus'),
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: t('tableConfig.column.selection'), type: 'selection' },
    {
      prop: 'tableType',
      label: t('tableConfig.column.tableType'),
      minWidth: 120,
      formatter: (row) => h(ElTag, null, () => getTableTypeText(row.tableType))
    },
    { prop: 'tableName', label: t('tableConfig.column.tableName'), minWidth: 180 },
    {
      prop: 'tablePrefix',
      label: t('tableConfig.column.tablePrefix'),
      minWidth: 120,
      checked: false
    },
    { prop: 'schemaName', label: t('tableConfig.column.dbName'), minWidth: 150 },
    { prop: 'businessName', label: t('tableConfig.column.businessName'), minWidth: 150 },
    {
      prop: 'moduleName',
      label: t('tableConfig.column.moduleName'),
      minWidth: 150,
      checked: false
    },
    {
      prop: 'packageName',
      label: t('tableConfig.column.packageName'),
      minWidth: 150,
      checked: false
    },
    { prop: 'author', label: t('tableConfig.column.author'), minWidth: 120 },
    {
      prop: 'generatePath',
      label: t('tableConfig.column.generatePath'),
      minWidth: 200,
      checked: false
    },
    {
      prop: 'fieldCount',
      label: t('tableConfig.column.fieldCount'),
      minWidth: 120,
      formatter: (row) => {
        if (row.fieldCount > 0) {
          return h(
            ElButton,
            {
              title: t('tableConfig.column.fieldCount'),
              type: 'primary',
              link: true,
              onClick: () => openFieldConfig(row)
            },
            () => t('tableConfig.column.fieldCountText', { count: row.fieldCount })
          )
        } else {
          return h('span', '-')
        }
      }
    },
    { prop: 'remarks', label: t('tableConfig.column.remarks'), minWidth: 150 },
    {
      prop: 'updateDate',
      label: t('tableConfig.column.updateTime'),
      minWidth: 170,
      formatter: (row) => (row.updateDate ? formatDate(row.updateDate) : '-')
    },
    {
      prop: 'delFlag',
      label: t('tableConfig.column.status'),
      minWidth: 100,
      checked: false,
      formatter: (row) =>
        h(ElTag, { type: row.delFlag ? 'danger' : 'success' }, () =>
          row.delFlag ? t('tableConfig.column.statusDeleted') : t('tableConfig.column.statusNormal')
        )
    },
    {
      prop: 'actions',
      label: t('tableConfig.column.actions'),
      fixed: 'right',
      width: 220,
      formatter: (row) =>
        h(ElSpace, null, () => {
          const buttons = []

          if (!row.delFlag) {
            buttons.push(
              h(ArtButtonTable, {
                title: t('tableConfig.button.edit'),
                type: 'edit',
                auth: 'tableconfig_edit',
                onClick: () => handleEdit(row)
              }),
              h(ArtButtonTable, {
                title: t('tableConfig.button.syncFields'),
                text: t('tableConfig.button.syncFields'),
                auth: 'tableconfig_sync_field',
                onClick: () => handleSyncFields(row)
              }),
              h(ArtButtonTable, {
                title: t('tableConfig.button.delete'),
                type: 'delete',
                auth: 'tableconfig_delete',
                onClick: () => handleDelete(row)
              })
            )
          } else {
            buttons.push(
              h(ArtButtonTable, {
                title: t('tableConfig.button.recover'),
                icon: '&#xe64b',
                iconColor: '#67c23a',
                auth: 'tableconfig_recover',
                onClick: () => handleRecover(row)
              }),
              h(ArtButtonTable, {
                title: t('tableConfig.button.completeDelete'),
                type: 'delete',
                auth: 'tableconfig_complete_delete',
                onClick: () => handleCompletelyDelete(row)
              })
            )
          }

          return buttons
        })
    }
  ]

  // 列定义与动态显隐 (Pass a function returning columnOptions)
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 对话框相关
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive<BaseTableConfig & { id?: number }>({
    tableName: '',
    tablePrefix: '',
    schemaName: '',
    businessName: '',
    moduleName: '',
    packageName: '',
    author: '',
    generatePath: '',
    options: '',
    remarks: ''
  })

  // 表单验证规则
  const formRules = reactive<FormRules>({
    tableName: [
      { required: true, message: t('tableConfig.validation.tableNameRequired'), trigger: 'blur' },
      { max: 100, message: t('tableConfig.validation.tableNameLength'), trigger: 'blur' }
    ],
    tablePrefix: [
      { max: 30, message: t('tableConfig.validation.tablePrefixLength'), trigger: 'blur' }
    ],
    schemaName: [
      { required: true, message: t('tableConfig.validation.dbNameRequired'), trigger: 'blur' },
      { max: 100, message: t('tableConfig.validation.dbNameLength'), trigger: 'blur' }
    ],
    businessName: [
      {
        required: true,
        message: t('tableConfig.validation.businessNameRequired'),
        trigger: 'blur'
      },
      { max: 100, message: t('tableConfig.validation.businessNameLength'), trigger: 'blur' }
    ]
  })

  // 加载表格配置列表数据
  const loadTableConfigList = async (params?: TableConfigListParams) => {
    loading.value = true
    try {
      const queryParams: TableConfigListParams = params || {
        ...formFilters.value,
        delFlag: formFilters.value.delFlag === '1' ? true : false
      }
      const res = await TableService.getTableConfigList(queryParams)
      if (res.success) {
        tableConfigList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('tableConfig.message.getListFailed'))
      }
    } catch (error) {
      console.error('获取表格配置列表失败:', error)
      ElMessage.error(t('tableConfig.message.getListError'))
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    // 将字符串类型的delFlag转换为布尔值传给API
    const params: TableConfigListParams = { ...formFilters.value }
    if (formFilters.value.delFlag === '1') {
      params.delFlag = true
    } else if (formFilters.value.delFlag === '0') {
      params.delFlag = false
    } else {
      params.delFlag = undefined
    }
    // 使用转换后的参数
    loadTableConfigList(params)
  }

  // 重置查询
  const resetQuery = () => {
    Object.assign(formFilters.value, {
      ...initialSearchState,
      delFlag: undefined
    }) // 重置为初始状态
    loadTableConfigList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: TableConfigModel[]) => {
    selectedConfigs.value = selection
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedConfigs.value.length === 0) {
      ElMessage.warning(t('tableConfig.message.selectAtLeastOne'))
      return
    }

    // 过滤掉已经标记为删除的记录
    const validConfigs = selectedConfigs.value.filter((config) => !config.delFlag)

    if (validConfigs.length === 0) {
      ElMessage.warning(t('tableConfig.message.allDeleted'))
      return
    }

    ElMessageBox.confirm(t('tableConfig.message.batchDeleteConfirm'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        const deletePromises = validConfigs.map((config) =>
          TableService.deleteTableConfig(config.id)
        )

        try {
          const results = await Promise.all(deletePromises)
          const success = results.every((res) => res.success)

          if (success) {
            ElMessage.success(t('tableConfig.message.batchDeleteSuccess'))
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(t('tableConfig.message.batchDeleteFailed'))
          }
        } catch (error) {
          console.error('批量删除表格配置失败:', error)
          ElMessage.error(t('tableConfig.message.batchDeleteError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('tableConfig.message.cancelBatchDelete'))
      })
  }

  // 处理单个删除
  const handleDelete = (row: TableConfigModel) => {
    ElMessageBox.confirm(
      t('tableConfig.message.deleteConfirm', { name: row.tableName }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await TableService.deleteTableConfig(row.id)
          if (res.success) {
            ElMessage.success(t('tableConfig.message.deleteSuccess'))
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('tableConfig.message.deleteFailed'))
          }
        } catch (error) {
          console.error('删除表格配置失败:', error)
          ElMessage.error(t('tableConfig.message.deleteError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('tableConfig.message.cancelDelete'))
      })
  }

  // 处理彻底删除
  const handleCompletelyDelete = (row: TableConfigModel) => {
    ElMessageBox.confirm(
      t('tableConfig.message.completeDeleteConfirm', { name: row.tableName }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await TableService.completelyDeleteTableConfig(row.id)
          if (res.success) {
            ElMessage.success(t('tableConfig.message.completeDeleteSuccess'))
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('tableConfig.message.completeDeleteFailed'))
          }
        } catch (error) {
          console.error('彻底删除表格配置失败:', error)
          ElMessage.error(t('tableConfig.message.completeDeleteError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('tableConfig.message.cancelDelete'))
      })
  }

  // 处理恢复
  const handleRecover = (row: TableConfigModel) => {
    ElMessageBox.confirm(
      t('tableConfig.message.recoverConfirm', { name: row.tableName }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'info'
      }
    )
      .then(async () => {
        try {
          const res = await TableService.recoverTableConfig(row.id)
          if (res.success) {
            ElMessage.success(t('tableConfig.message.recoverSuccess'))
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('tableConfig.message.recoverFailed'))
          }
        } catch (error) {
          console.error('恢复表格配置失败:', error)
          ElMessage.error(t('tableConfig.message.recoverError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('tableConfig.message.cancelRecover'))
      })
  }

  // 同步字段列表
  const handleSyncFields = async (row: TableConfigModel) => {
    try {
      // 构造正确的参数对象
      const params: SyncTableFieldListParams = {
        tableName: row.tableName,
        schemaName: row.schemaName
      }
      const res = await TableService.syncFieldsByTableName(params)
      if (res.success) {
        ElMessage.success(t('tableConfig.message.syncFieldsSuccess'))
        loadTableConfigList() // 重新加载数据
      } else {
        ElMessage.error(res.message || t('tableConfig.message.syncFieldsFailed'))
      }
    } catch (error) {
      console.error('同步字段列表失败:', error)
      ElMessage.error(t('tableConfig.message.syncFieldsError'))
    }
  }

  // 处理添加
  const handleAdd = async () => {
    dialogType.value = 'add'
    resetForm()
    // 获取数据库名称列表
    await getSchemaNameList()
    dialogVisible.value = true
  }

  // 处理编辑
  const handleEdit = async (row: TableConfigModel) => {
    dialogType.value = 'edit'
    resetForm()
    // 获取数据库名称列表
    await getSchemaNameList()
    // 复制数据到表单
    Object.assign(formData, row)
    // 如果有数据库名称，获取对应的表名列表
    if (formData.schemaName) {
      await getTableNameList(formData.schemaName)
    }
    dialogVisible.value = true
  }

  // 重置表单
  const resetForm = () => {
    formData.id = undefined
    formData.tableName = ''
    formData.tablePrefix = ''
    formData.schemaName = ''
    formData.businessName = ''
    formData.moduleName = ''
    formData.packageName = ''
    formData.author = ''
    formData.generatePath = ''
    formData.options = ''
    formData.remarks = ''
  }

  // 提交表单
  const submitForm = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          let res
          if (dialogType.value === 'add') {
            res = await TableService.addTableConfig(formData)
          } else {
            res = await TableService.editTableConfig({
              ...formData,
              id: formData.id!
            })
          }

          if (res.success) {
            ElMessage.success(
              dialogType.value === 'add'
                ? t('tableConfig.message.addSuccess')
                : t('tableConfig.message.editSuccess')
            )
            dialogVisible.value = false
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(
              res.message ||
                (dialogType.value === 'add'
                  ? t('tableConfig.message.addFailed')
                  : t('tableConfig.message.editFailed'))
            )
          }
        } catch (error) {
          console.error(
            dialogType.value === 'add' ? '添加表格配置失败:' : '编辑表格配置失败:',
            error
          )
          ElMessage.error(
            dialogType.value === 'add'
              ? t('tableConfig.message.addError')
              : t('tableConfig.message.editError')
          )
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadTableConfigList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
    loadTableConfigList()
  }

  // 获取数据库名称列表
  const getSchemaNameList = async () => {
    try {
      const res = await TableService.getSchemaNameList()
      if (res.success && res.data) {
        schemaNameList.value = res.data
      } else {
        ElMessage.error(res.message || t('tableConfig.message.getDbListFailed'))
      }
    } catch (error) {
      console.error('获取数据库名称列表失败:', error)
      ElMessage.error(t('tableConfig.message.getDbListError'))
    }
  }

  // 获取表名列表
  const getTableNameList = async (schemaName: string) => {
    try {
      const res = await TableService.getTableNameList({ schemaName: schemaName })
      if (res.success && res.data) {
        tableNameList.value = res.data
      } else {
        ElMessage.error(res.message || t('tableConfig.message.getTableListFailed'))
      }
    } catch (error) {
      console.error('获取表名列表失败:', error)
      ElMessage.error(t('tableConfig.message.getTableListError'))
    }
  }

  // 处理数据库名称变更
  const handleSchemaChange = async (val: string) => {
    // 清空表名
    formData.tableName = ''
    // 如果选择了数据库，获取表名列表
    if (val) {
      await getTableNameList(val)
    } else {
      tableNameList.value = []
    }
  }

  // 获取表格类型对应的文本
  const getTableTypeText = (typeCode: number): string => {
    return TableTypeTextMap[typeCode] || `未知类型(${typeCode})`
  }

  // 打开字段配置页面
  const openFieldConfig = (tableConfig: TableConfigModel) => {
    // 设置当前选中的表格配置
    currentTableConfig.value = tableConfig

    // 直接显示字段配置对话框，让子组件自行加载数据
    showFieldConfig.value = true
  }

  // 刷新表格数据
  const refreshTableData = async () => {
    await loadTableConfigList()
  }

  // 下载源码
  const handleDownloadCode = () => {
    if (selectedConfigs.value.length === 0) {
      ElMessage.warning(t('tableConfig.message.selectAtLeastOne'))
      return
    }

    // 过滤掉已经标记为删除的记录
    const validConfigs = selectedConfigs.value.filter((config) => !config.delFlag)

    if (validConfigs.length === 0) {
      ElMessage.warning(t('tableConfig.message.allDeleted'))
      return
    }

    ElMessageBox.confirm(t('tableConfig.message.downloadConfirm'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'info'
    })
      .then(async () => {
        try {
          // 获取选中配置的ID列表
          const ids = validConfigs.map((config) => config.id)
          // 显示加载状态
          loading.value = true

          const res = await TableService.downloadCode({ ids })

          // 使用FileSaver进行下载，直接处理blob数据
          FileSaver.saveAs(res as Blob, '源码.zip')

          ElMessage.success(t('tableConfig.message.downloadSuccess'))
        } catch (error) {
          console.error('下载源码失败:', error)
          ElMessage.error(t('tableConfig.message.downloadError'))
        } finally {
          loading.value = false
        }
      })
      .catch(() => {
        ElMessage.info(t('tableConfig.message.cancelDownload'))
      })
  }

  // 组件挂载时加载数据
  onMounted(async () => {
    // 先加载数据库名称列表
    await getSchemaNameList()
    // 然后加载表格配置列表
    loadTableConfigList()
  })
</script>

<style lang="scss" scoped></style>
