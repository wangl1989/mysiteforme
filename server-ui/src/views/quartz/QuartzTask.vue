<template>
  <ArtTableFullScreen>
    <div class="quartz-task-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters as any"
        :items="formItems"
        @reset="handleReset"
        @search="search"
        :isExpand="true"
      ></ArtSearchBar>
      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadTaskList"
        >
          <template #left>
            <ElButton title="新增定时任务" @click="handleAdd" v-auth="'quartzjob_add'" v-ripple
              >新增任务</ElButton
            >
            <ElButton
              title="批量删除定时任务"
              type="danger"
              @click="handleBatchDelete"
              v-auth="'quartzjob_delete'"
              v-ripple
              >批量删除</ElButton
            >
            <ElButton
              title="批量暂停定时任务"
              type="warning"
              @click="handleBatchPause"
              v-auth="'quartzjob_pause'"
              v-ripple
              >批量暂停</ElButton
            >
            <ElButton
              title="批量恢复定时任务"
              type="success"
              @click="handleBatchResume"
              v-auth="'quartzjob_resume'"
              v-ripple
              >批量恢复</ElButton
            >
            <ElButton
              title="立即执行定时任务"
              type="info"
              @click="handleBatchRun"
              v-auth="'quartzjob_run'"
              v-ripple
              >立即执行</ElButton
            >
          </template>
        </ArtTableHeader>
        <!-- 表格 -->
        <ArtTable
          :data="taskList"
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
        :title="dialogType === 'add' ? '新增定时任务' : '编辑定时任务'"
        width="600px"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <ElForm
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          label-position="right"
        >
          <ElFormItem label="任务组名" prop="groupName">
            <ElInput v-model="formData.groupName" placeholder="请输入任务组名" />
          </ElFormItem>
          <ElFormItem label="任务名称" prop="name">
            <ElInput v-model="formData.name" placeholder="请输入任务名称" />
          </ElFormItem>
          <ElFormItem label="Cron表达式" prop="cron">
            <ElInput v-model="formData.cron" placeholder="请输入Cron表达式" />
          </ElFormItem>
          <ElFormItem label="目标Bean" prop="targetBean">
            <ElInput v-model="formData.targetBean" placeholder="请输入目标Bean名称" />
          </ElFormItem>
          <ElFormItem label="目标方法" prop="trgetMethod">
            <ElInput v-model="formData.trgetMethod" placeholder="请输入目标方法名称" />
          </ElFormItem>
          <ElFormItem label="执行参数" prop="params">
            <ElInput v-model="formData.params" placeholder="请输入执行参数" />
          </ElFormItem>
          <ElFormItem label="任务状态" prop="status">
            <ElRadioGroup v-model="formData.status">
              <ElRadio :value="0">正常</ElRadio>
              <ElRadio :value="1">暂停</ElRadio>
            </ElRadioGroup>
          </ElFormItem>
          <ElFormItem label="备注" prop="remarks">
            <ElInput
              v-model="formData.remarks"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton
            :title="dialogType === 'add' ? '取消新增定时任务' : '取消编辑定时任务'"
            @click="dialogVisible = false"
            >取消</ElButton
          >
          <ElButton
            :title="dialogType === 'add' ? '保存新增定时任务' : '保存编辑定时任务'"
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >确定</ElButton
          >
        </template>
      </ElDialog>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, h } from 'vue'
  import { ElMessage, ElMessageBox, ElTag, ElButton, ElSpace } from 'element-plus'
  import { QuartzTaskService } from '@/api/quartzTaskApi'
  import {
    QuartzTaskRecordModel,
    QuartzTaskListParams,
    AddQuartzTaskParams,
    EditQuartzTaskParams
  } from '@/api/model/quartzTaskModel'
  import type { FormInstance, FormRules } from 'element-plus'
  import { useCheckedColumns, type ColumnOption } from '@/composables/useCheckedColumns'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import type { SearchFormItem } from '@/types/search-form'

  // 加载状态
  const loading = ref(false)
  const submitLoading = ref(false)

  // 任务数据列表
  const taskList = ref<QuartzTaskRecordModel[]>([])

  // 选中的任务记录
  const selectedTasks = ref<QuartzTaskRecordModel[]>([])

  // 初始化搜索条件
  const initialSearchState = {
    name: '',
    status: undefined,
    page: 1,
    limit: 10,
    sortByCreateDateAsc: false
  }

  // 搜索表单数据
  const formFilters = reactive<QuartzTaskListParams>({ ...initialSearchState })

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // 对话框相关
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive<AddQuartzTaskParams & { id?: number }>({
    name: '',
    cron: '',
    groupName: '',
    targetBean: '',
    trgetMethod: '',
    params: '',
    status: 0,
    remarks: ''
  })

  // 表单验证规则
  const formRules = reactive<FormRules>({
    name: [
      { required: true, message: '请输入任务名称', trigger: 'blur' },
      { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
    ],
    cron: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }],
    targetBean: [{ required: true, message: '请输入目标Bean', trigger: 'blur' }],
    trgetMethod: [{ required: true, message: '请输入目标方法', trigger: 'blur' }],
    status: [{ required: true, message: '请选择任务状态', trigger: 'change' }]
  })

  // 搜索表单配置
  const formItems: SearchFormItem[] = [
    {
      label: '任务名称',
      prop: 'name',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: '请输入任务名称搜索',
        clearable: true
      }
    },
    {
      label: '任务状态',
      prop: 'status',
      type: 'select',
      options: () => [
        { label: '正常', value: 0 },
        { label: '暂停', value: 1 }
      ],
      config: {
        placeholder: '请选择状态',
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { prop: 'groupName', label: '任务组名', minWidth: 150 },
    { prop: 'name', label: '任务名称', minWidth: 150 },
    { prop: 'cron', label: 'Cron表达式', minWidth: 150 },
    { prop: 'targetBean', label: '目标Bean', minWidth: 180 },
    { prop: 'trgetMethod', label: '目标方法', minWidth: 120 },
    { prop: 'params', label: '参数', minWidth: 120 },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      formatter: (row) => {
        return h(
          ElTag,
          {
            type: row.status === 0 ? 'success' : 'warning',
            size: 'small'
          },
          () => (row.status === 0 ? '正常' : '暂停')
        )
      }
    },
    { prop: 'updateDate', label: '更新时间', minWidth: 180, sortable: true },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 320,
      formatter: (row) => {
        return h(ElSpace, { size: 'small' }, () => [
          h(ArtButtonTable, {
            title: '编辑定时任务',
            type: 'edit',
            auth: 'quartzjob_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: '删除定时任务',
            type: 'delete',
            auth: 'quartzjob_delete',
            onClick: () => handleDelete(row)
          }),
          row.status === 0
            ? h(ArtButtonTable, {
                title: '暂停定时任务',
                type: 'warning',
                auth: 'quartzjob_pause',
                onClick: () => handlePause(row)
              })
            : h(ArtButtonTable, {
                title: '恢复定时任务',
                type: 'success',
                auth: 'quartzjob_resume',
                onClick: () => handleResume(row)
              }),
          h(ArtButtonTable, {
            title: '立即执行任务',
            type: 'info',
            auth: 'quartzjob_run',
            onClick: () => handleRun(row)
          })
        ])
      }
    }
  ]

  // 使用动态列hook
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 加载定时任务列表数据
  const loadTaskList = async () => {
    loading.value = true
    try {
      const res = await QuartzTaskService.getQuartzTaskPageList(formFilters)
      if (res.success) {
        taskList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || '获取定时任务列表失败')
      }
    } catch (error) {
      console.error('获取定时任务列表失败:', error)
      ElMessage.error('获取定时任务列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.page = 1 // 搜索时重置为第一页
    loadTaskList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters, initialSearchState)
    loadTaskList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: QuartzTaskRecordModel[]) => {
    selectedTasks.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.page = page
    loadTaskList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.limit = size
    formFilters.page = 1 // 切换每页数量时重置为第一页
    loadTaskList()
  }

  // 处理添加任务
  const handleAdd = () => {
    dialogType.value = 'add'
    resetForm()
    dialogVisible.value = true
  }

  // 处理编辑任务
  const handleEdit = (row: QuartzTaskRecordModel) => {
    dialogType.value = 'edit'
    resetForm()

    // 复制数据到表单
    formData.id = row.id
    formData.name = row.name
    formData.cron = row.cron
    formData.groupName = row.groupName
    formData.targetBean = row.targetBean
    formData.trgetMethod = row.trgetMethod
    formData.params = row.params
    formData.status = row.status
    formData.remarks = row.remarks

    dialogVisible.value = true
  }

  // 重置表单
  const resetForm = () => {
    formData.id = undefined
    formData.name = ''
    formData.cron = ''
    formData.targetBean = ''
    formData.groupName = ''
    formData.trgetMethod = ''
    formData.params = ''
    formData.status = 0
    formData.remarks = ''

    // 重置表单验证
    if (formRef.value) {
      formRef.value.resetFields()
    }
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
            res = await QuartzTaskService.addQuartzTask(formData)
          } else {
            // 编辑任务
            const editParams: EditQuartzTaskParams = {
              id: formData.id as number,
              name: formData.name,
              cron: formData.cron,
              groupName: formData.groupName,
              targetBean: formData.targetBean,
              trgetMethod: formData.trgetMethod,
              params: formData.params,
              status: formData.status,
              remarks: formData.remarks
            }
            res = await QuartzTaskService.editQuartzTask(editParams)
          }

          if (res.success) {
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '编辑成功')
            dialogVisible.value = false
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '编辑失败'))
          }
        } catch (error) {
          console.error(dialogType.value === 'add' ? '添加任务失败:' : '编辑任务失败:', error)
          ElMessage.error(dialogType.value === 'add' ? '添加任务时发生错误' : '编辑任务时发生错误')
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理单个删除
  const handleDelete = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(`确定要删除任务 "${row.name}" 吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskService.deleteQuartzTask([row.id])
          if (res.success) {
            ElMessage.success('删除成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除任务失败:', error)
          ElMessage.error('删除任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要删除选中的任务吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.deleteQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success('批量删除成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '批量删除失败')
          }
        } catch (error) {
          console.error('批量删除任务失败:', error)
          ElMessage.error('批量删除任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 暂停单个任务
  const handlePause = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(`确定要暂停任务 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskService.paushQuartzTask([row.id])
          if (res.success) {
            ElMessage.success('暂停成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '暂停失败')
          }
        } catch (error) {
          console.error('暂停任务失败:', error)
          ElMessage.error('暂停任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 批量暂停任务
  const handleBatchPause = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要暂停选中的任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.paushQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success('批量暂停成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '批量暂停失败')
          }
        } catch (error) {
          console.error('批量暂停任务失败:', error)
          ElMessage.error('批量暂停任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 恢复单个任务
  const handleResume = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(`确定要恢复任务 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskService.resumeQuartzTask([row.id])
          if (res.success) {
            ElMessage.success('恢复成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '恢复失败')
          }
        } catch (error) {
          console.error('恢复任务失败:', error)
          ElMessage.error('恢复任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消恢复任务')
      })
  }

  // 批量恢复任务
  const handleBatchResume = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要恢复选中的任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.resumeQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success('批量恢复成功')
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '批量恢复失败')
          }
        } catch (error) {
          console.error('批量恢复任务失败:', error)
          ElMessage.error('批量恢复任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消批量恢复任务')
      })
  }

  // 执行单个任务
  const handleRun = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(`确定要立即执行任务 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskService.runQuartzTask([row.id])
          if (res.success) {
            ElMessage.success('执行成功')
          } else {
            ElMessage.error(res.message || '执行失败')
          }
        } catch (error) {
          console.error('执行任务失败:', error)
          ElMessage.error('执行任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消执行任务')
      })
  }

  // 批量执行任务
  const handleBatchRun = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要立即执行选中的任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.runQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success('批量执行成功')
          } else {
            ElMessage.error(res.message || '批量执行失败')
          }
        } catch (error) {
          console.error('批量执行任务失败:', error)
          ElMessage.error('批量执行任务时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消立即执行选中的任务')
      })
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadTaskList()
  })
</script>

<style lang="scss" scoped>
  .quartz-task-page {
    width: 100%;
    height: 100%;
  }
</style>
