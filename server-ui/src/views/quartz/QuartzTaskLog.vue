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
            <ElButton title="批量删除定时任务" type="danger" @click="handleBatchDelete" v-ripple
              >批量删除</ElButton
            >
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
      <ElDialog v-model="errorDialogVisible" title="异常信息详情" width="60%">
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
  const formFilters = reactive<QuartzTaskLogListParams>({ ...initialSearchState })

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
      label: '任务名称',
      prop: 'name',
      type: 'input',
      elColSpan: 6,
      config: {
        placeholder: '请输入任务名称搜索',
        clearable: true
      }
    }
  ]

  // 表格列配置
  const columnOptions: ColumnOption[] = [
    { label: '勾选', type: 'selection' },
    { prop: 'jobId', label: '任务ID', width: 100 },
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
            type: getTaskStatusType(row.status),
            size: 'small'
          },
          () => getTaskStatusText(row.status)
        )
      }
    },
    {
      prop: 'times',
      label: '执行耗时',
      minWidth: 120,
      formatter: (row) => {
        return `${row.times || 0}ms`
      }
    },
    {
      prop: 'exception',
      label: '异常信息',
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
            '查看异常'
          )
        } else {
          return h('span', '-')
        }
      }
    },
    { prop: 'createDate', label: '创建时间', minWidth: 180, sortable: true },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 100,
      formatter: (row) => {
        return h(ArtButtonTable, {
          title: '删除定时任务日志',
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
      const res = await QuartzTaskLogService.getQuartzTaskLogPageList(formFilters)
      if (res.success) {
        logList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || '获取定时任务日志列表失败')
      }
    } catch (error) {
      console.error('获取定时任务日志列表失败:', error)
      ElMessage.error('获取定时任务日志列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.page = 1 // 搜索时重置为第一页
    loadLogList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters, initialSearchState)
    loadLogList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: QuartzTaskRecordModel[]) => {
    selectedLogs.value = selection
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.page = page
    loadLogList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.limit = size
    formFilters.page = 1 // 切换每页数量时重置为第一页
    loadLogList()
  }

  // 处理单个删除
  const handleDelete = (row: QuartzTaskRecordModel) => {
    ElMessageBox.confirm(`确定要删除任务日志吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await QuartzTaskLogService.deleteQuartzTaskLog({ id: row.id })
          if (res.success) {
            ElMessage.success('删除成功')
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除任务日志失败:', error)
          ElMessage.error('删除任务日志时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消删除操作')
      })
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedLogs.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要删除选中的任务日志吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          // 注意：当前API只支持单个删除，这里进行遍历删除
          const deletePromises = selectedLogs.value.map((log) =>
            QuartzTaskLogService.deleteQuartzTaskLog({ id: log.id })
          )

          const results = await Promise.all(deletePromises)
          const success = results.every((res) => res.success)

          if (success) {
            ElMessage.success('批量删除成功')
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error('部分或全部删除失败，请重试')
          }
        } catch (error) {
          console.error('批量删除任务日志失败:', error)
          ElMessage.error('批量删除任务日志时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('用户取消批量删除操作')
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
