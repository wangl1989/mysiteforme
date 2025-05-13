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
            <ElButton
              title="{{ $t('quartzTask.button.add') }}"
              @click="handleAdd"
              v-auth="'quartzjob_add'"
              v-ripple
              >{{ $t('quartzTask.button.add') }}</ElButton
            >
            <ElButton
              title="{{ $t('quartzTask.button.batchDelete') }}"
              type="danger"
              @click="handleBatchDelete"
              v-auth="'quartzjob_delete'"
              v-ripple
              >{{ $t('quartzTask.button.batchDelete') }}</ElButton
            >
            <ElButton
              title="{{ $t('quartzTask.button.batchPause') }}"
              type="warning"
              @click="handleBatchPause"
              v-auth="'quartzjob_pause'"
              v-ripple
              >{{ $t('quartzTask.button.batchPause') }}</ElButton
            >
            <ElButton
              title="{{ $t('quartzTask.button.batchResume') }}"
              type="success"
              @click="handleBatchResume"
              v-auth="'quartzjob_resume'"
              v-ripple
              >{{ $t('quartzTask.button.batchResume') }}</ElButton
            >
            <ElButton
              title="{{ $t('quartzTask.button.batchRun') }}"
              type="info"
              @click="handleBatchRun"
              v-auth="'quartzjob_run'"
              v-ripple
              >{{ $t('quartzTask.button.batchRun') }}</ElButton
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
        :title="
          dialogType === 'add'
            ? $t('quartzTask.dialog.addTitle')
            : $t('quartzTask.dialog.editTitle')
        "
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
          <ElFormItem :label="$t('quartzTask.form.groupName')" prop="groupName">
            <ElInput
              v-model="formData.groupName"
              :placeholder="$t('quartzTask.form.groupNamePlaceholder')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.name')" prop="name">
            <ElInput v-model="formData.name" :placeholder="$t('quartzTask.form.namePlaceholder')" />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.cron')" prop="cron">
            <ElInput v-model="formData.cron" :placeholder="$t('quartzTask.form.cronPlaceholder')" />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.targetBean')" prop="targetBean">
            <ElInput
              v-model="formData.targetBean"
              :placeholder="$t('quartzTask.form.targetBeanPlaceholder')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.targetMethod')" prop="trgetMethod">
            <ElInput
              v-model="formData.trgetMethod"
              :placeholder="$t('quartzTask.form.targetMethodPlaceholder')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.params')" prop="params">
            <ElInput
              v-model="formData.params"
              :placeholder="$t('quartzTask.form.paramsPlaceholder')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.status')" prop="status">
            <ElRadioGroup v-model="formData.status">
              <ElRadio :value="0">{{ $t('quartzTask.status.normal') }}</ElRadio>
              <ElRadio :value="1">{{ $t('quartzTask.status.pause') }}</ElRadio>
            </ElRadioGroup>
          </ElFormItem>
          <ElFormItem :label="$t('quartzTask.form.remarks')" prop="remarks">
            <ElInput
              v-model="formData.remarks"
              type="textarea"
              :rows="3"
              :placeholder="$t('quartzTask.form.remarksPlaceholder')"
            />
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton
            :title="
              dialogType === 'add'
                ? $t('quartzTask.dialog.cancelAdd')
                : $t('quartzTask.dialog.cancelEdit')
            "
            @click="dialogVisible = false"
            >{{ $t('common.cancel') }}</ElButton
          >
          <ElButton
            :title="
              dialogType === 'add'
                ? $t('quartzTask.dialog.submitAdd')
                : $t('quartzTask.dialog.submitEdit')
            "
            type="primary"
            @click="submitForm"
            :loading="submitLoading"
            >{{ $t('common.confirm') }}</ElButton
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
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

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
  const formFilters = ref<QuartzTaskListParams>({ ...initialSearchState })

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
      { required: true, message: t('quartzTask.rules.name.required'), trigger: 'blur' },
      { max: 100, message: t('quartzTask.rules.name.length'), trigger: 'blur' }
    ],
    cron: [{ required: true, message: t('quartzTask.rules.cron.required'), trigger: 'blur' }],
    targetBean: [
      { required: true, message: t('quartzTask.rules.targetBean.required'), trigger: 'blur' }
    ],
    trgetMethod: [
      { required: true, message: t('quartzTask.rules.targetMethod.required'), trigger: 'blur' }
    ],
    status: [{ required: true, message: t('quartzTask.rules.status.required'), trigger: 'change' }]
  })

  // 搜索表单配置
  const formItems: SearchFormItem[] = [
    {
      label: t('quartzTask.form.name'),
      prop: 'name',
      type: 'input',
      elColSpan: 5,
      config: {
        placeholder: t('quartzTask.search.namePlaceholder'),
        clearable: true
      }
    },
    {
      label: t('quartzTask.form.status'),
      prop: 'status',
      type: 'select',
      options: () => [
        { label: t('quartzTask.status.normal'), value: 0 },
        { label: t('quartzTask.status.pause'), value: 1 }
      ],
      config: {
        placeholder: t('quartzTask.search.statusPlaceholder'),
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { type: 'selection' },
    { prop: 'groupName', label: t('quartzTask.table.groupName'), minWidth: 150 },
    { prop: 'name', label: t('quartzTask.table.name'), minWidth: 150 },
    { prop: 'cron', label: t('quartzTask.table.cron'), minWidth: 150 },
    { prop: 'targetBean', label: t('quartzTask.table.targetBean'), minWidth: 180 },
    { prop: 'trgetMethod', label: t('quartzTask.table.targetMethod'), minWidth: 120 },
    { prop: 'params', label: t('quartzTask.table.params'), minWidth: 120 },
    {
      prop: 'status',
      label: t('quartzTask.table.status'),
      width: 100,
      formatter: (row) => {
        return h(
          ElTag,
          {
            type: row.status === 0 ? 'success' : 'warning',
            size: 'small'
          },
          () => (row.status === 0 ? t('quartzTask.status.normal') : t('quartzTask.status.pause'))
        )
      }
    },
    { prop: 'updateDate', label: t('quartzTask.table.updateDate'), minWidth: 180, sortable: true },
    {
      prop: 'actions',
      label: t('quartzTask.table.operation'),
      fixed: 'right',
      width: 320,
      formatter: (row) => {
        return h(ElSpace, { size: 'small' }, () => [
          h(ArtButtonTable, {
            title: t('quartzTask.operation.edit'),
            type: 'edit',
            auth: 'quartzjob_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: t('quartzTask.operation.delete'),
            type: 'delete',
            auth: 'quartzjob_delete',
            onClick: () => handleDelete(row)
          }),
          row.status === 0
            ? h(ArtButtonTable, {
                title: t('quartzTask.operation.pause'),
                type: 'warning',
                auth: 'quartzjob_pause',
                onClick: () => handlePause(row)
              })
            : h(ArtButtonTable, {
                title: t('quartzTask.operation.resume'),
                type: 'success',
                auth: 'quartzjob_resume',
                onClick: () => handleResume(row)
              }),
          h(ArtButtonTable, {
            title: t('quartzTask.operation.run'),
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
      const res = await QuartzTaskService.getQuartzTaskPageList(formFilters.value)
      if (res.success) {
        taskList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('quartzTask.message.getListFailed'))
      }
    } catch (error) {
      console.error(t('quartzTask.message.getListError'), error)
      ElMessage.error(t('quartzTask.message.getListError'))
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadTaskList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters.value, initialSearchState)
    loadTaskList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: QuartzTaskRecordModel[]) => {
    selectedTasks.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadTaskList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
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
            ElMessage.success(
              dialogType.value === 'add'
                ? t('quartzTask.message.addSuccess')
                : t('quartzTask.message.editSuccess')
            )
            dialogVisible.value = false
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(
              res.message ||
                (dialogType.value === 'add'
                  ? t('quartzTask.message.addFailed')
                  : t('quartzTask.message.editFailed'))
            )
          }
        } catch (error) {
          console.error(
            dialogType.value === 'add'
              ? t('quartzTask.message.addError')
              : t('quartzTask.message.editError'),
            error
          )
          ElMessage.error(
            dialogType.value === 'add'
              ? t('quartzTask.message.addError')
              : t('quartzTask.message.editError')
          )
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 处理单个删除
  const handleDelete = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(
      t('quartzTask.dialog.confirmDelete', { name: row.name }),
      t('quartzTask.dialog.confirmDeleteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await QuartzTaskService.deleteQuartzTask([row.id])
          if (res.success) {
            ElMessage.success(t('quartzTask.message.deleteSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.deleteFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.deleteError'), error)
          ElMessage.error(t('quartzTask.message.deleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning(t('quartzTask.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(
      t('quartzTask.dialog.confirmBatchDelete'),
      t('quartzTask.dialog.confirmDeleteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.deleteQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success(t('quartzTask.message.batchDeleteSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.batchDeleteFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.batchDeleteError'), error)
          ElMessage.error(t('quartzTask.message.batchDeleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 暂停单个任务
  const handlePause = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(
      t('quartzTask.dialog.confirmPause', { name: row.name }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await QuartzTaskService.paushQuartzTask([row.id])
          if (res.success) {
            ElMessage.success(t('quartzTask.message.pauseSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.pauseFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.pauseError'), error)
          ElMessage.error(t('quartzTask.message.pauseError'))
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 批量暂停任务
  const handleBatchPause = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning(t('quartzTask.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(t('quartzTask.dialog.confirmBatchPause'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.paushQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success(t('quartzTask.message.batchPauseSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.batchPauseFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.batchPauseError'), error)
          ElMessage.error(t('quartzTask.message.batchPauseError'))
        }
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 恢复单个任务
  const handleResume = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(
      t('quartzTask.dialog.confirmResume', { name: row.name }),
      t('common.tips'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await QuartzTaskService.resumeQuartzTask([row.id])
          if (res.success) {
            ElMessage.success(t('quartzTask.message.resumeSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.resumeFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.resumeError'), error)
          ElMessage.error(t('quartzTask.message.resumeError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTask.message.cancelResume'))
      })
  }

  // 批量恢复任务
  const handleBatchResume = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning(t('quartzTask.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(t('quartzTask.dialog.confirmBatchResume'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.resumeQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success(t('quartzTask.message.batchResumeSuccess'))
            loadTaskList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTask.message.batchResumeFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.batchResumeError'), error)
          ElMessage.error(t('quartzTask.message.batchResumeError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTask.message.cancelBatchResume'))
      })
  }

  // 执行单个任务
  const handleRun = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(t('quartzTask.dialog.confirmRun', { name: row.name }), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskService.runQuartzTask([row.id])
          if (res.success) {
            ElMessage.success(t('quartzTask.message.runSuccess'))
          } else {
            ElMessage.error(res.message || t('quartzTask.message.runFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.runError'), error)
          ElMessage.error(t('quartzTask.message.runError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTask.message.cancelRun'))
      })
  }

  // 批量执行任务
  const handleBatchRun = () => {
    if (selectedTasks.value.length === 0) {
      ElMessage.warning(t('quartzTask.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(t('quartzTask.dialog.confirmBatchRun'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const taskIds = selectedTasks.value.map((task) => task.id)
          const res = await QuartzTaskService.runQuartzTask(taskIds)
          if (res.success) {
            ElMessage.success(t('quartzTask.message.batchRunSuccess'))
          } else {
            ElMessage.error(res.message || t('quartzTask.message.batchRunFailed'))
          }
        } catch (error) {
          console.error(t('quartzTask.message.batchRunError'), error)
          ElMessage.error(t('quartzTask.message.batchRunError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTask.message.cancelBatchRun'))
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
