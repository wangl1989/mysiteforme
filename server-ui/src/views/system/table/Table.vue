<template>
  <ArtTableFullScreen>
    <div class="table-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        :isExpand="true"
        @reset="handleReset"
        @search="search"
      >
        <template #prepend>
          <el-select
            v-model="formFilters.schemaName"
            placeholder="请选择数据库名称"
            style="width: 260px"
            @change="handleSchemaChange"
          >
            <el-option v-for="item in schemaNameList" :key="item" :label="item" :value="item" />
          </el-select>
        </template>
      </ArtSearchBar>
      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadTableList"
        >
          <template #left>
            <ElButton type="primary" @click="handleAdd" v-auth="'table_add'" v-ripple>
              新增表格
            </ElButton>
            <ElButton
              title="批量删除表格"
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedTables.length === 0"
              v-auth="'table_batch_delete'"
              v-ripple
            >
              批量删除
            </ElButton>
          </template>
        </ArtTableHeader>
        <!-- 表格 -->
        <ArtTable
          :data="tableList"
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
        :title="dialogType === 'add' ? '新增表格' : '编辑表格'"
        width="800px"
        :close-on-click-modal="false"
        :destroy-on-close="true"
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
              style="width: 100%"
              :disabled="dialogType === 'edit'"
            >
              <el-option v-for="item in schemaNameList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="表格名称" prop="tableName">
            <el-input v-model="formData.tableName" placeholder="请输入表格名称" />
          </el-form-item>
          <el-form-item label="表格注释" prop="comment">
            <el-input v-model="formData.comment" placeholder="请输入表格注释" />
          </el-form-item>
          <el-form-item label="表格类型" prop="tableType">
            <el-select
              v-model="formData.tableType"
              placeholder="请选择表格类型"
              style="width: 100%"
              :disabled="dialogType === 'edit'"
            >
              <el-option label="基本类型" :value="1" />
              <el-option label="树结构类型" :value="2" />
              <el-option label="关联类型" :value="3" />
            </el-select>
          </el-form-item>

          <!-- 只有新增表格时才需要字段列表 -->
          <template v-if="dialogType === 'add'">
            <el-divider content-position="center">字段设置</el-divider>
            <div class="field-header">
              <div style="flex: 1">
                <el-button type="primary" link v-auth="'field_add'" @click="addFieldRow">
                  <el-icon><Plus /></el-icon> 添加字段
                </el-button>
              </div>
            </div>

            <!-- 新增：字段列表容器 -->
            <div class="field-list-container">
              <!-- 新增：手动添加表头 -->
              <div class="field-list-header field-row">
                <div class="field-item field-name">字段名称</div>
                <div class="field-item field-type">字段类型</div>
                <div class="field-item field-length">字段长度</div>
                <div class="field-item field-nullable">不是 null</div>
                <div class="field-item field-comment">字段注释</div>
                <div class="field-item field-action">操作</div>
              </div>

              <!-- 修改：使用 v-for 循环渲染每一行 -->
              <div v-for="(field, index) in formData.fieldList" :key="index" class="field-row">
                <!-- 字段名称 -->
                <div class="field-item field-name">
                  <el-form-item
                    :prop="`fieldList.${index}.columnName`"
                    :rules="[
                      { required: true, message: '请输入字段名称', trigger: 'blur' },
                      { max: 64, message: '长度不能超过64个字符', trigger: 'blur' }
                    ]"
                    class="no-margin"
                  >
                    <el-input v-model="field.columnName" placeholder="请输入字段名称" />
                  </el-form-item>
                </div>
                <!-- 字段类型 -->
                <div class="field-item field-type">
                  <el-form-item
                    :prop="`fieldList.${index}.type`"
                    :rules="[{ required: true, message: '请选择字段类型', trigger: 'change' }]"
                    class="no-margin"
                  >
                    <el-select
                      v-model="field.type"
                      placeholder="请选择字段类型"
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
                </div>
                <!-- 字段长度 -->
                <div class="field-item field-length">
                  <el-form-item
                    :prop="`fieldList.${index}.length`"
                    :rules="[
                      { type: 'number', message: '长度必须为数字', trigger: 'blur' },
                      {
                        required: needLength(field.type),
                        message: '请输入字段长度',
                        trigger: 'blur'
                      }
                    ]"
                    class="no-margin"
                  >
                    <el-input-number
                      v-model="field.length"
                      placeholder="请输入长度"
                      style="width: 100%"
                      :min="1"
                      :disabled="!needLength(field.type)"
                      controls-position="right"
                    />
                  </el-form-item>
                </div>
                <!-- 允许为空 -->
                <div class="field-item field-nullable">
                  <el-form-item :prop="`fieldList.${index}.isNullable`" class="no-margin">
                    <el-switch
                      v-model="field.isNullable"
                      :active-value="true"
                      :inactive-value="false"
                    />
                  </el-form-item>
                </div>
                <!-- 字段注释 -->
                <div class="field-item field-comment">
                  <el-form-item :prop="`fieldList.${index}.comment`" class="no-margin">
                    <el-input v-model="field.comment" placeholder="请输入字段注释" />
                  </el-form-item>
                </div>
                <!-- 操作 -->
                <div class="field-item field-action">
                  <el-button
                    type="danger"
                    link
                    :icon="Close"
                    v-auth="'field_delete'"
                    @click="removeFieldRow(index)"
                    :disabled="formData.fieldList.length <= 1"
                  />
                </div>
              </div>
            </div>
          </template>
        </el-form>
        <template #footer>
          <el-button
            :title="dialogType === 'add' ? '取消新增表格' : '取消编辑表格'"
            @click="dialogVisible = false"
            >取消</el-button
          >
          <el-button
            :title="dialogType === 'add' ? '保存新增表格' : '保存编辑表格'"
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >确定</el-button
          >
        </template>
      </ElDialog>

      <!-- 添加TableField对话框组件 -->
      <TableField
        v-if="showTableField"
        v-model:visible="showTableField"
        :tableName="tableFieldInfo.tableName"
        :schemaName="tableFieldInfo.schemaName"
        :tableType="tableFieldInfo.tableType"
      />
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, h, nextTick } from 'vue' // 引入 h 和 nextTick
  import {
    ElMessage,
    ElMessageBox,
    ElButton,
    ElCard,
    ElTag,
    ElSpace,
    ElIcon,
    ElSelect,
    ElOption,
    ElInput,
    ElDialog,
    ElForm,
    ElFormItem,
    ElDivider,
    ElInputNumber,
    ElSwitch
  } from 'element-plus' // 按需引入 Element Plus 组件
  import { Plus, Close } from '@element-plus/icons-vue'
  import { TableService as TableConfigService } from '@/api/tableConfigApi'
  import { TableService } from '@/api/tableApi'
  import {
    TableRecordModel,
    TableListParam,
    AddTableParam,
    EditTableParam,
    AddListTableFieldParam
  } from '@/api/model/tableModel'
  import type { FormInstance, FormRules } from 'element-plus'
  import { formatDate } from '@/utils/date'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { useCheckedColumns, ColumnOption } from '@/composables/useCheckedColumns'
  import type { SearchFormItem } from '@/types/search-form' // 确认路径

  // 引入TableField组件
  import TableField from './TableField.vue'
  // 注意： v-auth 指令需要全局注册或局部导入才能在 h 函数中使用，这里假设已全局注册
  // 如果没有，需要找到 v-auth 的实现方式并在 h 函数中手动调用其逻辑，或者调整组件结构

  // 加载状态
  const loading = ref(false)
  const submitLoading = ref(false)

  // 添加TableField对话框相关状态变量
  const showTableField = ref(false)
  const tableFieldInfo = reactive({
    tableName: '',
    schemaName: '',
    tableType: 1
  })

  // 表格数据列表
  const tableList = ref<TableRecordModel[]>([])

  // 下拉列表数据
  const schemaNameList = ref<string[]>([])

  // 选中的表格记录
  const selectedTables = ref<TableRecordModel[]>([])

  // 搜索表单初始值
  const initialSearchState = {
    page: 1,
    limit: 10,
    schemaName: '',
    tableName: ''
  }

  // 响应式搜索表单数据
  const formFilters = ref<TableListParam>({ ...initialSearchState })

  // 分页信息 (保留用于显示总数，或者可以直接从 formFilters 读取 current 和 size)
  const pagination = reactive({
    total: 0
  })

  // 搜索栏配置
  const formItems: SearchFormItem[] = [
    // {
    //   label: '数据库名称', // 移到 ArtSearchBar 的 prepend 插槽
    //   prop: 'schemaName',
    //   type: 'select',
    //   config: {
    //     placeholder: '请选择数据库名称',
    //     clearable: true,
    //     options: () => schemaNameList.value.map(name => ({ label: name, value: name })) // 动态选项
    //   },
    //   onChange: handleSchemaChange // 如果需要在单个select变化时触发
    // },
    {
      label: '表格名称',
      prop: 'tableName',
      type: 'input',
      config: {
        placeholder: '请输入表格名称搜索',
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: '勾选', type: 'selection' },
    { prop: 'name', label: '表格名称', minWidth: 150 },
    { prop: 'comment', label: '表格注释', minWidth: 180 },
    {
      prop: 'tableType',
      label: '表格类型',
      minWidth: 120,
      formatter: (row) => h(ElTag, null, () => getTableTypeText(row.tableType))
    },
    { prop: 'schemaName', label: '数据库名称', minWidth: 150, checked: false },
    { prop: 'tableRows', label: '数据量', minWidth: 100 },
    {
      prop: 'createTime',
      label: '创建时间',
      minWidth: 170,
      formatter: (row) => (row.createTime ? formatDate(row.createTime) : '-')
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      minWidth: 170,
      formatter: (row) => (row.updateTime ? formatDate(row.updateTime) : '-')
    },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 220,
      formatter: (row) =>
        h(ElSpace, null, () => [
          h(ArtButtonTable, {
            title: '编辑表格',
            type: 'edit',
            auth: 'table_edit', // 传递权限标识
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: '管理字段列表',
            iconColor: '#14f326',
            icon: '&#xe67b;', // 使用 Element Plus 图标名称或 SVG
            auth: 'field_manage',
            onClick: () => handleManageFields(row)
          }),
          h(ArtButtonTable, {
            title: '删除表格',
            type: 'delete',
            auth: 'table_delete',
            onClick: () => handleDelete(row)
          })
        ])
    }
  ]

  // 使用 Hook 获取响应式的列配置和选中状态
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 对话框相关
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive<AddTableParam & { oldTableName?: string }>({
    schemaName: '',
    tableName: '',
    comment: '',
    tableType: 1,
    fieldList: [createListField()]
  })

  // 创建默认字段
  function createListField(): AddListTableFieldParam {
    return {
      columnName: '',
      length: 50,
      type: 'varchar',
      isNullable: false,
      comment: ''
    }
  }

  // 是否需要长度字段
  const needLength = (type: string): boolean => {
    return ['varchar', 'int', 'bigint', 'tinyint', 'decimal', 'double'].includes(type || '')
  }

  // 表单验证规则
  const formRules = reactive<FormRules>({
    schemaName: [{ required: true, message: '请选择数据库名称', trigger: 'change' }],
    tableName: [
      { required: true, message: '请输入表格名称', trigger: 'blur' },
      { max: 64, message: '长度不能超过64个字符', trigger: 'blur' }
    ],
    tableType: [{ required: true, message: '请选择表格类型', trigger: 'change' }]
    // 字段列表的验证规则直接写在 template 的 el-form-item 上
  })

  // 加载表格列表数据
  const loadTableList = async () => {
    loading.value = true
    try {
      // 使用 formFilters 作为参数
      const params: TableListParam = { ...formFilters.value }
      const res = await TableService.getTableList(params)
      if (res.success) {
        tableList.value = res.data.records
        pagination.total = res.data.total // 更新总数
      } else {
        ElMessage.error(res.message || '获取表格列表失败')
      }
    } catch (error) {
      console.error('获取表格列表失败:', error)
      ElMessage.error('获取表格列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadTableList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters.value, initialSearchState) // 重置为初始状态
    // 可能需要保留 schemaName，如果它有默认值的话
    if (schemaNameList.value.length > 0 && !initialSearchState.schemaName) {
      formFilters.value.schemaName = schemaNameList.value[0]
    }
    loadTableList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: TableRecordModel[]) => {
    selectedTables.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadTableList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
    loadTableList()
  }

  // 获取数据库名称列表
  const getSchemaNameList = async () => {
    try {
      const res = await TableConfigService.getSchemaNameList()
      if (res.success && res.data) {
        schemaNameList.value = res.data
        // 如果当前没有选择数据库，且有数据库列表，则默认选择第一个
        if (!formFilters.value.schemaName && res.data.length > 0) {
          formFilters.value.schemaName = res.data[0]
          // 使用选择的数据库名称加载表格列表
          loadTableList()
        } else if (formFilters.value.schemaName) {
          // 如果已经有选中的数据库，则直接加载
          loadTableList()
        }
      } else {
        ElMessage.error(res.message || '获取数据库名称列表失败')
      }
    } catch (error) {
      console.error('获取数据库名称列表失败:', error)
      ElMessage.error('获取数据库名称列表时发生错误')
    }
  }

  // 处理数据库名称变更 (通过 ArtSearchBar 的 change 事件或 Select 的 change 事件触发)
  const handleSchemaChange = () => {
    formFilters.value.page = 1 // 切换数据库时重置到第一页
    loadTableList()
  }

  // 处理添加字段行
  const addFieldRow = () => {
    formData.fieldList.push(createListField())
  }

  // 处理移除字段行
  const removeFieldRow = (index: number) => {
    if (formData.fieldList.length > 1) {
      formData.fieldList.splice(index, 1)
    }
  }

  // 处理添加表格
  const handleAdd = () => {
    dialogType.value = 'add'
    dialogVisible.value = true // 先打开对话框

    nextTick(() => {
      // 确保 formRef 存在
      if (formRef.value) {
        formData.fieldList = [createListField()] // 确保字段列表重置
        formData.oldTableName = '' // 确保旧表名清空
        formData.comment = ''
        formData.tableName = ''

        // 3. 设置新增时的默认值 (在 resetFields 之后设置)
        formData.schemaName = formFilters.value.schemaName || '' // 使用当前选中的库名，或为空
      }
    })
  }

  // 处理编辑表格
  const handleEdit = (row: TableRecordModel) => {
    dialogType.value = 'edit'
    dialogVisible.value = true // 先打开对话框

    nextTick(() => {
      if (formRef.value) {
        // 1. 清除编辑前的校验状态 (编辑时不重置值)
        formRef.value.clearValidate()

        // 2. 填充表单数据
        formData.schemaName = row.schemaName
        formData.tableName = row.name
        formData.oldTableName = row.name // 保存旧表名
        formData.comment = row.comment
        formData.tableType = row.tableType
        // 编辑模式下不需要字段列表
        formData.fieldList = []
      }
    })
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
            // 校验字段列表是否至少有一个有效字段 (或者根据业务需求决定)
            if (
              !formData.fieldList ||
              formData.fieldList.length === 0 ||
              !formData.fieldList[0].columnName
            ) {
              ElMessage.warning('请至少添加并配置一个字段')
              submitLoading.value = false
              return
            }
            // 提交整个 formData，其中包含了 fieldList
            res = await TableService.addTable(formData)
          } else {
            // 编辑表格，只提交需要的部分
            const editParams: EditTableParam = {
              schemaName: formData.schemaName,
              oldTableName: formData.oldTableName || '',
              tableName: formData.tableName,
              comment: formData.comment,
              tableType: formData.tableType
            }
            res = await TableService.editTable(editParams)
          }

          if (res.success) {
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '编辑成功')
            dialogVisible.value = false
            loadTableList() // 重新加载数据
          } else {
            ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '编辑失败'))
          }
        } catch (error) {
          console.error(dialogType.value === 'add' ? '添加表格失败:' : '编辑表格失败:', error)
          ElMessage.error(dialogType.value === 'add' ? '添加表格时发生错误' : '编辑表格时发生错误')
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedTables.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm(
      '确定要删除选中的表格吗？此操作会删除表及其所有数据，且不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
      .then(async () => {
        // 构造批量删除参数 (根据 API 定义，只传递 tableName)
        selectedTables.value.map((table) => ({
          // schemaName: table.schemaName, // API 定义不需要 schemaName，暂时注释
          tableName: table.name
        }))
        // 注意：假设有一个批量删除接口 TableService.deleteTables，如果没有则保持原有循环方式
        // const res = await TableService.deleteTables(deleteParams); // 假设的批量接口

        // ---- 如果没有批量接口，保持原有逻辑 ----
        loading.value = true // 开始loading
        const deletePromises = selectedTables.value.map(
          (table) => TableService.deleteTable({ tableName: table.name }) // 只传递 tableName
        )
        try {
          const results = await Promise.allSettled(deletePromises) // 使用 allSettled 获取所有结果
          const failedCount = results.filter(
            (r) => r.status === 'rejected' || (r.status === 'fulfilled' && !r.value.success)
          ).length

          if (failedCount === 0) {
            ElMessage.success('批量删除成功')
          } else {
            ElMessage.warning(
              `批量删除完成，${selectedTables.value.length - failedCount} 个成功，${failedCount} 个失败。`
            )
            // 可以选择性地显示失败详情
          }
          loadTableList() // 重新加载数据
        } catch (error) {
          // Promise.allSettled 本身不太可能抛错，但以防万一
          console.error('批量删除表格过程中发生错误:', error)
          ElMessage.error('批量删除表格时发生未知错误')
        } finally {
          loading.value = false // 结束loading
        }
        // --- 保持原有逻辑结束 ---
      })
      .catch(() => {
        ElMessage.info('取消了批量删除操作') // 添加 .catch 处理
      })
  }

  // 处理单个删除
  const handleDelete = (row: TableRecordModel) => {
    ElMessageBox.confirm(
      `确定要删除表格 "${row.name}" 吗？此操作会删除表及其所有数据，且不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          // 删除接口需要 tableName (根据 API 定义)
          const res = await TableService.deleteTable({ tableName: row.name })
          if (res.success) {
            ElMessage.success('删除成功')
            loadTableList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除表格失败:', error)
          ElMessage.error('删除表格时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('取消了删除操作') // 添加 .catch 处理
      })
  }

  // 处理管理字段
  const handleManageFields = (row: TableRecordModel) => {
    // 设置要传递给TableField组件的信息
    tableFieldInfo.tableName = row.name
    tableFieldInfo.schemaName = row.schemaName
    tableFieldInfo.tableType = row.tableType

    // 显示TableField对话框
    showTableField.value = true
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

  // 组件挂载时加载数据
  onMounted(() => {
    getSchemaNameList() // 获取数据库列表，成功后会自动加载表格列表
  })
</script>

<style lang="scss" scoped>
  // .page-content 样式可以移除，因为 ArtTableFullScreen 通常会处理根布局
  // .page-content {
  //   width: 100%;
  //   height: 100%;
  // }

  .table-page {
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
  }

  .art-table-card {
    display: flex;
    flex: 1; // 让卡片占据剩余空间
    flex-direction: column;
    overflow: hidden; // 防止内容溢出卡片

    :deep(.el-card__body) {
      display: flex;
      flex-direction: column;
      height: 100%; // 让 card body 也撑满
      padding: 15px; // 可以根据需要调整内边距
    }
  }

  // 移除旧的搜索表单和按钮样式

  /*
  .compact-form { ... }
  .search-buttons { ... }
  */

  // 弹窗内字段列表相关样式保持不变
  .field-form {
    margin-bottom: 15px;
  }

  .field-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .field-list-container {
    border: 1px solid #ebeef5;
    border-bottom: none;
  }

  .field-row {
    display: flex;
    align-items: center;
    min-height: 55px;
    padding: 5px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: 1px solid #ebeef5;
    }
  }

  .field-list-header {
    min-height: 40px;
    padding: 5px 0;
    font-weight: bold;
    color: #606266;
    background-color: #fafafa;
  }

  .field-item {
    box-sizing: border-box;
    padding: 0 10px;
  }

  .field-name {
    flex: 0 0 150px;
  }

  .field-type {
    flex: 0 0 150px;
  }

  .field-length {
    flex: 0 0 120px;
  }

  .field-nullable {
    flex: 0 0 80px;
    text-align: center;
  }

  .field-comment {
    flex: 1;
    min-width: 180px;
  }

  .field-action {
    flex: 0 0 60px;
    text-align: center;
  }

  :deep(.field-item .el-form-item.no-margin) {
    width: 100%;
    margin-bottom: 0;
  }

  :deep(.field-item .el-form-item__content) {
    margin-left: 0 !important;
  }

  :deep(.field-item .el-input),
  :deep(.field-item .el-input-number),
  :deep(.field-item .el-select) {
    width: 100%;
  }

  :deep(.field-item .el-input-number .el-input__inner) {
    text-align: left;
  }

  :deep(.field-action .el-button) {
    margin: 0;
  }

  :deep(.el-dialog__body) {
    max-height: 65vh;
    padding: 20px;
    overflow-y: auto;
  }

  :deep(.field-nullable .el-form-item__content) {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
  // 特殊处理 ArtSearchBar 前置 Select 的样式
  :deep(.art-search-bar .el-input-group__prepend) {
    padding: 0 10px 0 0; // 调整右侧 padding
    background-color: transparent;
    border: none;
    box-shadow: none;
  }
</style>
