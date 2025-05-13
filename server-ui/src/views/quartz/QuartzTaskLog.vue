<template>
  <ArtTableFullScreen>
    <div class="quartz-task-log-page" id="table-full-screen">
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
          @refresh="loadLogList"
        >
          <template #left>
            <ElButton
              :title="$t('quartzTaskLog.button.batchDelete')"
              type="danger"
              @click="handleBatchDelete"
              v-ripple
            >
              {{ $t('quartzTaskLog.button.batchDelete') }}
            </ElButton>
          </template>
        </ArtTableHeader>
        <!-- 表格 -->
        <ArtTable
          :data="logList"
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

      <!-- 异常信息对话框 -->
      <ElDialog
        v-model="errorDialogVisible"
        :title="$t('quartzTaskLog.dialog.errorTitle')"
        width="60%"
      >
        <div class="error-content">
          <pre>{{ currentError }}</pre>
        </div>
      </ElDialog>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, h } from 'vue'
  import { ElMessage, ElMessageBox, ElTag, ElButton } from 'element-plus'
  import { QuartzTaskLogService } from '@/api/quartzTaskLogApi'
  import {
    QuartzTaskRecordModel,
    QuartzTaskLogListParams,
    getTaskStatusText,
    getTaskStatusType
  } from '@/api/model/quartzTaskLogModel'
  import { useCheckedColumns, type ColumnOption } from '@/composables/useCheckedColumns'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import type { SearchFormItem } from '@/types/search-form'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 加载状态
  const loading = ref(false)

  // 日志数据列表
  const logList = ref<QuartzTaskRecordModel[]>([])

  // 选中的日志记录
  const selectedLogs = ref<QuartzTaskRecordModel[]>([])

  // 初始化搜索条件
  const initialSearchState = {
    name: undefined,
    page: 1,
    limit: 10,
    sortByCreateDateAsc: false
  }

  // 搜索表单数据
  const formFilters = ref<QuartzTaskLogListParams>({ ...initialSearchState })

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // 异常信息对话框
  const errorDialogVisible = ref(false)
  const currentError = ref('')

  // 显示异常信息对话框
  const showErrorDialog = (error: string) => {
    currentError.value = error
    errorDialogVisible.value = true
  }

  // 搜索表单配置
  const formItems: SearchFormItem[] = [
    {
      label: t('quartzTaskLog.form.name'),
      prop: 'name',
      type: 'input',
      elColSpan: 6,
      config: {
        placeholder: t('quartzTaskLog.search.namePlaceholder'),
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: t('quartzTaskLog.table.selection'), type: 'selection' },
    { prop: 'jobId', label: t('quartzTaskLog.table.jobId'), width: 100 },
    { prop: 'name', label: t('quartzTaskLog.table.name'), minWidth: 150 },
    { prop: 'cron', label: t('quartzTaskLog.table.cron'), minWidth: 150 },
    { prop: 'targetBean', label: t('quartzTaskLog.table.targetBean'), minWidth: 180 },
    { prop: 'trgetMethod', label: t('quartzTaskLog.table.targetMethod'), minWidth: 120 },
    { prop: 'params', label: t('quartzTaskLog.table.params'), minWidth: 120 },
    {
      prop: 'status',
      label: t('quartzTaskLog.table.status'),
      width: 100,
      formatter: (row) => {
        return h(
          ElTag,
          {
            type: getTaskStatusType(row.status),
            size: 'small'
          },
          () => getTaskStatusText(row.status)
        )
      }
    },
    {
      prop: 'times',
      label: t('quartzTaskLog.table.times'),
      minWidth: 120,
      formatter: (row) => {
        return `${row.times || 0}ms`
      }
    },
    {
      prop: 'exception',
      label: t('quartzTaskLog.table.exception'),
      minWidth: 180,
      checked: false,
      formatter: (row) => {
        if (row.error) {
          return h(
            'button',
            {
              class: 'el-button el-button--danger el-button--small is-link',
              onClick: () => showErrorDialog(row.error)
            },
            t('quartzTaskLog.button.viewError')
          )
        } else {
          return h('span', '-')
        }
      }
    },
    {
      prop: 'createDate',
      label: t('quartzTaskLog.table.createDate'),
      minWidth: 180,
      sortable: true
    },
    {
      prop: 'actions',
      label: t('quartzTaskLog.table.operation'),
      fixed: 'right',
      width: 100,
      formatter: (row) => {
        return h(ArtButtonTable, {
          title: t('quartzTaskLog.operation.delete'),
          type: 'delete',
          onClick: () => handleDelete(row)
        })
      }
    }
  ]

  // 使用动态列hook
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 加载定时任务日志列表数据
  const loadLogList = async () => {
    loading.value = true
    try {
      const res = await QuartzTaskLogService.getQuartzTaskLogPageList(formFilters.value)
      if (res.success) {
        logList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('quartzTaskLog.message.getListFailed'))
      }
    } catch (error) {
      console.error(t('quartzTaskLog.message.getListError'), error)
      ElMessage.error(t('quartzTaskLog.message.getListError'))
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadLogList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters.value, initialSearchState)
    loadLogList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: QuartzTaskRecordModel[]) => {
    selectedLogs.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadLogList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
    loadLogList()
  }

  // 处理单个删除
  const handleDelete = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(
      t('quartzTaskLog.dialog.confirmDelete'),
      t('quartzTaskLog.dialog.confirmDeleteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          const res = await QuartzTaskLogService.deleteQuartzTaskLog({ id: row.id })
          if (res.success) {
            ElMessage.success(t('quartzTaskLog.message.deleteSuccess'))
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('quartzTaskLog.message.deleteFailed'))
          }
        } catch (error) {
          console.error(t('quartzTaskLog.message.deleteError'), error)
          ElMessage.error(t('quartzTaskLog.message.deleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTaskLog.message.cancelDelete'))
      })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedLogs.value.length === 0) {
      ElMessage.warning(t('quartzTaskLog.message.selectAtLeastOne'))
      return
    }

    ElMessageBox.confirm(
      t('quartzTaskLog.dialog.confirmBatchDelete'),
      t('quartzTaskLog.dialog.confirmDeleteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
      .then(async () => {
        try {
          // 注意：当前API只支持单个删除，这里进行遍历删除
          const deletePromises = selectedLogs.value.map((log) =>
            QuartzTaskLogService.deleteQuartzTaskLog({ id: log.id })
          )

          const results = await Promise.all(deletePromises)
          const success = results.every((res) => res.success)

          if (success) {
            ElMessage.success(t('quartzTaskLog.message.batchDeleteSuccess'))
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error(t('quartzTaskLog.message.batchDeletePartial'))
          }
        } catch (error) {
          console.error(t('quartzTaskLog.message.batchDeleteError'), error)
          ElMessage.error(t('quartzTaskLog.message.batchDeleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('quartzTaskLog.message.cancelBatchDelete'))
      })
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadLogList()
  })
</script>

<style lang="scss" scoped>
  .quartz-task-log-page {
    width: 100%;
    height: 100%;
  }

  .error-content {
    max-height: 60vh;
    padding: 10px;
    overflow-y: auto;
    background-color: #f8f8f8;
    border-radius: 4px;

    pre {
      margin: 0;
      font-family: monospace;
      font-size: 14px;
      line-height: 1.5;
      color: #d63031;
      word-wrap: break-word;
      white-space: pre-wrap;
    }
  }
</style>
