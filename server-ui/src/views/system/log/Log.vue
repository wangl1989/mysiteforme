<template>
  <ArtTableFullScreen>
    <div class="log-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters as any"
        :items="formItems"
        @reset="handleReset"
        @search="search"
        :isExpand="true"
        auth="log_search"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadLogList"
        >
          <template #left>
            <el-button
              :title="$t('log.batchDelete')"
              type="danger"
              @click="handleDelete"
              v-auth="'log_delete'"
              v-ripple
              >{{ $t('log.batchDelete') }}</el-button
            >
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="logList"
          selection
          v-loading="loading"
          :currentPage="pagination.current"
          :pageSize="pagination.size"
          :total="pagination.total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          @selection-change="handleSelectionChange"
          :marginTop="10"
          height="100%"
        >
          <template #default>
            <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
          </template>
        </ArtTable>

        <!-- JSON数据预览对话框 -->
        <ElDialog v-model="jsonDialogVisible" :title="jsonDialogTitle" width="60%" destroy-on-close>
          <div class="json-viewer">
            <pre v-html="formattedJson"></pre>
          </div>
        </ElDialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, computed, h } from 'vue'
  import { ElMessage, ElMessageBox, ElTag, ElDialog } from 'element-plus'
  import { LogService } from '@/api/logApi'
  import { LogRecord, LogListParams } from '@/api/model/logModel'
  import { useCheckedColumns } from '@/composables/useCheckedColumns'
  import { SearchFormItem, SearchChangeParams } from '@/types/search-form'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 加载状态
  const loading = ref(false)

  // 日志数据列表
  const logList = ref<LogRecord[]>([])

  // 选中的日志记录
  const selectedLogs = ref<LogRecord[]>([])

  // 表单项变更处理 (如果 ArtSearchBar 需要)
  const handleFormChange = (params: SearchChangeParams): void => {
    console.log('表单项变更:', params)
  }

  // 表单配置项 for ArtSearchBar
  const formItems: SearchFormItem[] = [
    {
      label: t('log.form.username'),
      prop: 'username',
      type: 'input',
      config: {
        placeholder: t('log.form.usernamePlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: t('log.form.title'),
      prop: 'title',
      type: 'input',
      config: {
        placeholder: t('log.form.titlePlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: t('log.form.httpMethod'),
      prop: 'httpMethod',
      type: 'select',
      config: {
        placeholder: t('log.form.methodPlaceholder'),
        clearable: true
      },
      options: () => [
        { label: 'GET', value: 'GET' },
        { label: 'POST', value: 'POST' },
        { label: 'PUT', value: 'PUT' },
        { label: 'DELETE', value: 'DELETE' }
      ],
      onChange: handleFormChange
    }
  ]

  // 搜索及分页状态
  const initialSearchState = {
    username: '',
    title: '',
    httpMethod: '',
    page: 1,
    limit: 10,
    sortByCreateDateAsc: false // 保留排序参数
  }
  const formFilters = ref({ ...initialSearchState })

  // 分页信息 (用于驱动 ArtTable 分页器显示)
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
  })

  // JSON预览对话框
  const jsonDialogVisible = ref(false)
  const jsonDialogTitle = ref('')
  const jsonRawData = ref<any>({})

  // 格式化JSON并添加语法高亮
  const formattedJson = computed(() => {
    try {
      const json =
        typeof jsonRawData.value === 'string' ? JSON.parse(jsonRawData.value) : jsonRawData.value
      const formatted = JSON.stringify(json, null, 2)
      // 简单的语法高亮处理 (保持不变)
      return formatted
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(
          /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+-]?\d+)?)/g,
          (match) => {
            let cls = 'json-number'
            if (/^"/.test(match)) {
              if (/:$/.test(match)) {
                cls = 'json-key'
              } else {
                cls = 'json-string'
              }
            } else if (/true|false/.test(match)) {
              cls = 'json-boolean'
            } else if (/null/.test(match)) {
              cls = 'json-null'
            }
            return `<span class="${cls}">${match}</span>`
          }
        )
    } catch (e) {
      console.error('解析JSON数据失败:', e)
      return typeof jsonRawData.value === 'string'
        ? jsonRawData.value
        : JSON.stringify(jsonRawData.value, null, 2)
    }
  })

  // 展示JSON数据对话框
  const showJsonDialog = (jsonString: string, title: string) => {
    try {
      jsonDialogTitle.value = title
      if (jsonString) {
        jsonRawData.value = jsonString
      } else {
        jsonRawData.value = { [t('log.json.content')]: t('log.json.emptyData') }
      }
      jsonDialogVisible.value = true
    } catch (error) {
      console.error('解析JSON数据失败:', error)
      jsonRawData.value = { [t('log.json.content')]: jsonString || t('log.json.parseError') }
      jsonDialogVisible.value = true
    }
  }

  // 获取HTTP方法对应的标签类型
  const getHttpMethodType = (
    method: string | null
  ): 'success' | 'warning' | 'danger' | 'info' | 'primary' | '' => {
    if (!method) return ''
    const methodMap: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'primary'> = {
      GET: 'success',
      POST: 'primary',
      PUT: 'warning',
      DELETE: 'danger'
    }
    return methodMap[method] || ''
  }

  // 列配置 for ArtTableHeader
  const columnOptions = [
    { label: t('log.table.selection'), type: 'selection' },
    { label: t('log.table.username'), prop: 'username' },
    { label: t('log.table.title'), prop: 'title' },
    { label: t('log.table.remoteAddr'), prop: 'remoteAddr' },
    { label: t('log.table.requestUri'), prop: 'requestUri' },
    { label: t('log.table.httpMethod'), prop: 'httpMethod' },
    { label: t('log.table.browser'), prop: 'browser' },
    { label: t('log.table.location'), prop: 'area' },
    { label: t('log.table.params'), prop: 'params' },
    { label: t('log.table.response'), prop: 'response' },
    { label: t('log.table.useTime'), prop: 'useTime' },
    { label: t('log.table.exception'), prop: 'exception' },
    { label: t('log.table.createDate'), prop: 'createDate' }
  ]

  // 动态列配置 for ArtTable using useCheckedColumns
  const { columnChecks, columns } = useCheckedColumns(() => [
    { type: 'selection' },
    { prop: 'username', label: t('log.table.username') },
    { prop: 'title', label: t('log.table.title') },
    { prop: 'remoteAddr', label: t('log.table.remoteAddr') },
    { prop: 'requestUri', label: t('log.table.requestUri') },
    {
      prop: 'httpMethod',
      label: t('log.table.httpMethod'),
      formatter: (row) => {
        return h(
          ElTag,
          { type: getHttpMethodType(row.httpMethod) as any, size: 'small' },
          () => row.httpMethod
        )
      }
    },
    { prop: 'browser', label: t('log.table.browser') },
    {
      prop: 'area',
      label: t('log.table.location'),
      formatter: (row) => `${row.province || ''}${row.city ? ' - ' + row.city : ''}`
    },
    {
      prop: 'params',
      label: t('log.table.params'),
      formatter: (row) => {
        return h(
          'button',
          {
            class: 'el-button el-button--primary el-button--small is-link',
            onClick: () => showJsonDialog(row.params, t('log.dialog.params'))
          },
          t('log.button.viewParams')
        )
      }
    },
    {
      prop: 'response',
      label: t('log.table.response'),
      formatter: (row) => {
        return h(
          'button',
          {
            class: 'el-button el-button--primary el-button--small is-link',
            onClick: () => showJsonDialog(row.response, t('log.dialog.response'))
          },
          t('log.button.viewResponse')
        )
      }
    },
    {
      prop: 'useTime',
      label: t('log.table.useTime'),
      formatter: (row) => `${row.useTime}ms`
    },
    {
      prop: 'exception',
      label: t('log.table.exception'),
      checked: false,
      formatter: (row) => {
        if (row.exception) {
          return h(
            'button',
            {
              class: 'el-button el-button--danger el-button--small is-link',
              onClick: () => showJsonDialog(row.exception, t('log.dialog.exception'))
            },
            t('log.button.viewException')
          )
        } else {
          return h('span', '-')
        }
      }
    },
    { prop: 'createDate', label: t('log.table.createDate'), sortable: true }
  ])

  // 加载日志列表数据
  const loadLogList = async () => {
    loading.value = true
    try {
      // 使用 formFilters 发送请求
      const res = await LogService.getLogList(formFilters.value as LogListParams)
      if (res.success) {
        logList.value = res.data.records
        pagination.total = res.data.total
        pagination.current = res.data.current
        pagination.size = res.data.size
        pagination.pages = res.data.pages
      } else {
        ElMessage.error(res.message || t('log.message.getListFailed'))
      }
    } catch (error) {
      console.error('获取日志列表失败:', error)
      ElMessage.error(t('log.message.getListError'))
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadLogList()
  }

  // 重置查询 (handleReset for ArtSearchBar)
  const handleReset = () => {
    Object.assign(formFilters.value, { ...initialSearchState })
    loadLogList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: LogRecord[]) => {
    selectedLogs.value = selection
  }

  // 处理批量删除
  const handleDelete = () => {
    if (selectedLogs.value.length === 0) {
      ElMessage.warning(t('log.dialog.selectRecord'))
      return
    }

    ElMessageBox.confirm(t('log.dialog.confirmDelete'), t('log.dialog.confirmDeleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const logIds = selectedLogs.value.map((log) => parseInt(log.id))
          const res = await LogService.deleteLog(logIds)
          if (res.success) {
            ElMessage.success(t('log.dialog.deleteSuccess'))
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('log.dialog.deleteFailed'))
          }
        } catch (error) {
          console.error('删除日志记录失败:', error)
          ElMessage.error(t('log.dialog.deleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info(t('log.dialog.cancelDelete'))
      })
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

  // 组件挂载时加载数据
  onMounted(() => {
    loadLogList()
  })
</script>

<style lang="scss" scoped>
  .log-page {
    .art-table-card {
      margin-top: 16px;
    }

    .json-viewer {
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
        word-wrap: break-word;
        white-space: pre-wrap;
      }

      :deep(.json-key) {
        color: #881391;
      }

      :deep(.json-string) {
        color: #1a8b1a;
      }

      :deep(.json-number) {
        color: #1a5fb4;
      }

      :deep(.json-boolean) {
        color: #e95800;
      }

      :deep(.json-null) {
        color: #808080;
      }
    }
  }
</style>
