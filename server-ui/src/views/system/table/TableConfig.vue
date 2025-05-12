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
              新增表格配置
            </ElButton>
            <ElButton
              title="批量删除表格配置"
              type="danger"
              @click="handleBatchDelete"
              v-auth="'tableconfig_batch_delete'"
              v-ripple
            >
              批量删除
            </ElButton>
            <ElButton
              type="success"
              @click="handleDownloadCode"
              v-auth="'tableconfig_download'"
              v-ripple
            >
              下载源码
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
        :title="dialogType === 'add' ? '新增表格配置' : '编辑表格配置'"
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
          <el-form-item label="数据库名称" prop="schemaName">
            <el-select
              v-model="formData.schemaName"
              placeholder="请选择数据库名称"
              filterable
              clearable
              @change="handleSchemaChange"
              style="width: 100%"
            >
              <el-option v-for="item in schemaNameList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="表格名称" prop="tableName">
            <el-select
              v-model="formData.tableName"
              placeholder="请选择表格名称"
              filterable
              clearable
              style="width: 100%"
              :disabled="!formData.schemaName"
            >
              <el-option v-for="item in tableNameList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="表格前缀" prop="tablePrefix">
            <el-input v-model="formData.tablePrefix" placeholder="请输入表格前缀" />
          </el-form-item>
          <el-form-item label="业务名称" prop="businessName">
            <el-input v-model="formData.businessName" placeholder="请输入业务名称" />
          </el-form-item>
          <el-form-item label="模块名称" prop="moduleName">
            <el-input v-model="formData.moduleName" placeholder="请输入模块名称" />
          </el-form-item>
          <el-form-item label="包名称" prop="packageName">
            <el-input v-model="formData.packageName" placeholder="请输入包名称" />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="formData.author" placeholder="请输入作者" />
          </el-form-item>
          <el-form-item label="生成路径" prop="generatePath">
            <el-input v-model="formData.generatePath" placeholder="请输入生成路径" />
          </el-form-item>
          <el-form-item label="选项" prop="options">
            <el-input
              v-model="formData.options"
              type="textarea"
              :rows="3"
              placeholder="请输入选项，如JSON格式配置"
            />
          </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input
              v-model="formData.remarks"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button
            :title="dialogType === 'add' ? '取消新增表格配置' : '取消编辑表格配置'"
            @click="dialogVisible = false"
            >取消</el-button
          >
          <el-button
            :title="dialogType === 'add' ? '保存新增表格配置' : '保存编辑表格配置'"
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >确定</el-button
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
  const formFilters = reactive<TableConfigListParams & { delFlag?: string }>({
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
      label: '表格名称',
      prop: 'tableName',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: '请输入表格名称',
        clearable: true
      }
    },
    {
      label: '数据库名称',
      labelWidth: '100px',
      prop: 'schemaName',
      type: 'select',
      elColSpan: 5,
      options: () => schemaNameList.value.map((name) => ({ label: name, value: name })),
      config: {
        placeholder: '请选择数据库名称',
        clearable: true
      }
    },
    {
      label: '业务名称',
      labelWidth: '100px',
      prop: 'businessName',
      type: 'input',
      elColSpan: 6,
      config: {
        placeholder: '请输入业务名称',
        clearable: true
      }
    },
    {
      label: '配置状态',
      labelWidth: '100px',
      prop: 'delFlag',
      type: 'select',
      elColSpan: 5,
      options: () => [
        { label: '正常', value: '0' },
        { label: '已删除', value: '1' }
      ],
      config: {
        placeholder: '请选择配置状态',
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: '勾选', type: 'selection' },
    {
      prop: 'tableType',
      label: '表格类型',
      minWidth: 120,
      formatter: (row) => h(ElTag, null, () => getTableTypeText(row.tableType))
    },
    { prop: 'tableName', label: '表格名称', minWidth: 180 },
    { prop: 'tablePrefix', label: '表格前缀', minWidth: 120, checked: false },
    { prop: 'schemaName', label: '数据库名称', minWidth: 150 },
    { prop: 'businessName', label: '业务名称', minWidth: 150 },
    { prop: 'moduleName', label: '模块名称', minWidth: 150, checked: false },
    { prop: 'packageName', label: '包名称', minWidth: 150, checked: false },
    { prop: 'author', label: '作者', minWidth: 120 },
    { prop: 'generatePath', label: '生成路径', minWidth: 200, checked: false },
    {
      prop: 'fieldCount',
      label: '可配置字段',
      minWidth: 120,
      formatter: (row) => {
        if (row.fieldCount > 0) {
          return h(
            ElButton,
            {
              title: '可配置字段列表',
              type: 'primary',
              link: true,
              onClick: () => openFieldConfig(row)
            },
            () => `共 ${row.fieldCount} 个字段`
          )
        } else {
          return h('span', '-')
        }
      }
    },
    { prop: 'remarks', label: '备注', minWidth: 150 },
    {
      prop: 'updateDate',
      label: '更新时间',
      minWidth: 170,
      formatter: (row) => (row.updateDate ? formatDate(row.updateDate) : '-')
    },
    {
      prop: 'delFlag',
      label: '状态',
      minWidth: 100,
      checked: false,
      formatter: (row) =>
        h(ElTag, { type: row.delFlag ? 'danger' : 'success' }, () =>
          row.delFlag ? '已删除' : '正常'
        )
    },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 220,
      formatter: (row) =>
        h(ElSpace, null, () => {
          const buttons = []

          if (!row.delFlag) {
            buttons.push(
              h(ArtButtonTable, {
                title: '编辑表格配置',
                type: 'edit',
                auth: 'tableconfig_edit',
                onClick: () => handleEdit(row)
              }),
              h(ArtButtonTable, {
                title: '同步表格字段配置',
                text: '同步字段',
                auth: 'tableconfig_sync_field',
                onClick: () => handleSyncFields(row)
              }),
              h(ArtButtonTable, {
                title: '删除表格配置',
                type: 'delete',
                auth: 'tableconfig_delete',
                onClick: () => handleDelete(row)
              })
            )
          } else {
            buttons.push(
              h(ArtButtonTable, {
                title: '恢复表格配置',
                icon: '&#xe64b',
                iconColor: '#67c23a',
                auth: 'tableconfig_recover',
                onClick: () => handleRecover(row)
              }),
              h(ArtButtonTable, {
                title: '彻底删除表格配置',
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
      { required: true, message: '请输入表格名称', trigger: 'blur' },
      { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
    ],
    tablePrefix: [{ max: 30, message: '长度不能超过30个字符', trigger: 'blur' }],
    schemaName: [
      { required: true, message: '请输入数据库名称', trigger: 'blur' },
      { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
    ],
    businessName: [
      { required: true, message: '请输入业务名称', trigger: 'blur' },
      { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
    ]
  })

  // 加载表格配置列表数据
  const loadTableConfigList = async (params?: TableConfigListParams) => {
    loading.value = true
    try {
      const queryParams: TableConfigListParams = params || {
        ...formFilters,
        delFlag: formFilters.delFlag === '1' ? true : false
      }
      const res = await TableService.getTableConfigList(queryParams)
      if (res.success) {
        tableConfigList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || '获取表格配置列表失败')
      }
    } catch (error) {
      console.error('获取表格配置列表失败:', error)
      ElMessage.error('获取表格配置列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.page = 1 // 搜索时重置为第一页
    // 将字符串类型的delFlag转换为布尔值传给API
    const params: TableConfigListParams = { ...formFilters }
    if (formFilters.delFlag === '1') {
      params.delFlag = true
    } else if (formFilters.delFlag === '0') {
      params.delFlag = false
    } else {
      params.delFlag = undefined
    }
    // 使用转换后的参数
    loadTableConfigList(params)
  }

  // 重置查询
  const resetQuery = () => {
    Object.assign(formFilters, {
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
      ElMessage.warning('请至少选择一条记录')
      return
    }

    // 过滤掉已经标记为删除的记录
    const validConfigs = selectedConfigs.value.filter((config) => !config.delFlag)

    if (validConfigs.length === 0) {
      ElMessage.warning('所选记录均已被删除，请重新选择')
      return
    }

    ElMessageBox.confirm('确定要删除选中的表格配置吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
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
            ElMessage.success('批量删除成功')
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error('部分或全部删除失败，请重试')
          }
        } catch (error) {
          console.error('批量删除表格配置失败:', error)
          ElMessage.error('批量删除表格配置时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了批量删除操作')
      })
  }

  // 处理单个删除
  const handleDelete = (row: TableConfigModel) => {
    ElMessageBox.confirm(`确定要删除表格配置 "${row.tableName}" 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await TableService.deleteTableConfig(row.id)
          if (res.success) {
            ElMessage.success('删除成功')
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除表格配置失败:', error)
          ElMessage.error('删除表格配置时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了删除操作')
      })
  }

  // 处理彻底删除
  const handleCompletelyDelete = (row: TableConfigModel) => {
    ElMessageBox.confirm(
      `确定要彻底删除表格配置 "${row.tableName}" 吗？这会把所有配置字段一并删除,且不能恢复！！！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await TableService.completelyDeleteTableConfig(row.id)
          if (res.success) {
            ElMessage.success('彻底删除成功')
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '彻底删除失败')
          }
        } catch (error) {
          console.error('彻底删除表格配置失败:', error)
          ElMessage.error('彻底删除表格配置时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了彻底删除操作')
      })
  }

  // 处理恢复
  const handleRecover = (row: TableConfigModel) => {
    ElMessageBox.confirm(`确定要恢复表格配置 "${row.tableName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
      .then(async () => {
        try {
          const res = await TableService.recoverTableConfig(row.id)
          if (res.success) {
            ElMessage.success('恢复成功')
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '恢复失败')
          }
        } catch (error) {
          console.error('恢复表格配置失败:', error)
          ElMessage.error('恢复表格配置时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了恢复操作')
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
        ElMessage.success('同步字段列表成功')
        loadTableConfigList() // 重新加载数据
      } else {
        ElMessage.error(res.message || '同步字段列表失败')
      }
    } catch (error) {
      console.error('同步字段列表失败:', error)
      ElMessage.error('同步字段列表时发生错误')
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
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '编辑成功')
            dialogVisible.value = false
            loadTableConfigList() // 重新加载数据
          } else {
            ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '编辑失败'))
          }
        } catch (error) {
          console.error(
            dialogType.value === 'add' ? '添加表格配置失败:' : '编辑表格配置失败:',
            error
          )
          ElMessage.error(
            dialogType.value === 'add' ? '添加表格配置时发生错误' : '编辑表格配置时发生错误'
          )
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.page = page
    loadTableConfigList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.limit = size
    formFilters.page = 1 // 切换每页数量时重置为第一页
    loadTableConfigList()
  }

  // 获取数据库名称列表
  const getSchemaNameList = async () => {
    try {
      const res = await TableService.getSchemaNameList()
      if (res.success && res.data) {
        schemaNameList.value = res.data
      } else {
        ElMessage.error(res.message || '获取数据库名称列表失败')
      }
    } catch (error) {
      console.error('获取数据库名称列表失败:', error)
      ElMessage.error('获取数据库名称列表时发生错误')
    }
  }

  // 获取表名列表
  const getTableNameList = async (schemaName: string) => {
    try {
      const res = await TableService.getTableNameList({ schemaName: schemaName })
      if (res.success && res.data) {
        tableNameList.value = res.data
      } else {
        ElMessage.error(res.message || '获取表名列表失败')
      }
    } catch (error) {
      console.error('获取表名列表失败:', error)
      ElMessage.error('获取表名列表时发生错误')
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
      ElMessage.warning('请至少选择一条记录')
      return
    }

    // 过滤掉已经标记为删除的记录
    const validConfigs = selectedConfigs.value.filter((config) => !config.delFlag)

    if (validConfigs.length === 0) {
      ElMessage.warning('所选记录均已被删除，请重新选择')
      return
    }

    ElMessageBox.confirm('确定要下载选中的表格配置源码吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
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

          ElMessage.success('源码下载成功')
        } catch (error) {
          console.error('下载源码失败:', error)
          ElMessage.error('下载源码时发生错误')
        } finally {
          loading.value = false
        }
      })
      .catch(() => {
        ElMessage.info('取消了下载操作')
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
