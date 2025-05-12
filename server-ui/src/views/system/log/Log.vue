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
              title="批量删除系统操作日志"
              type="danger"
              @click="handleDelete"
              v-auth="'log_delete'"
              v-ripple
              >批量删除</el-button
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
      label: '操作用户',
      prop: 'username',
      type: 'input',
      config: {
        placeholder: '请输入用户名搜索',
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: '操作标题',
      prop: 'title',
      type: 'input',
      config: {
        placeholder: '请输入日志标题搜索',
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: '请求类型',
      prop: 'httpMethod',
      type: 'select',
      config: {
        placeholder: '请选择请求类型',
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
  const formFilters = reactive({ ...initialSearchState })

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
        jsonRawData.value = { 内容: '空数据' }
      }
      jsonDialogVisible.value = true
    } catch (error) {
      console.error('解析JSON数据失败:', error)
      jsonRawData.value = { 内容: jsonString || '无法解析的数据' }
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
    { label: '勾选', type: 'selection' },
    { label: '操作用户', prop: 'username' },
    { label: '操作标题', prop: 'title' },
    { label: 'IP地址', prop: 'remoteAddr' },
    { label: '请求URI', prop: 'requestUri' },
    { label: '请求方法', prop: 'httpMethod' },
    { label: '浏览器', prop: 'browser' },
    { label: '位置', prop: 'area' },
    { label: '请求参数', prop: 'params' },
    { label: '返回数据', prop: 'response' },
    { label: '请求耗时', prop: 'useTime' },
    { label: '异常信息', prop: 'exception' },
    { label: '创建时间', prop: 'createDate' }
  ]

  // 动态列配置 for ArtTable using useCheckedColumns
  const { columnChecks, columns } = useCheckedColumns(() => [
    { type: 'selection' },
    { prop: 'username', label: '操作用户' },
    { prop: 'title', label: '操作标题' },
    { prop: 'remoteAddr', label: 'IP地址' },
    { prop: 'requestUri', label: '请求URI' },
    {
      prop: 'httpMethod',
      label: '请求方法',
      formatter: (row) => {
        return h(
          ElTag,
          { type: getHttpMethodType(row.httpMethod) as any, size: 'small' },
          () => row.httpMethod
        )
      }
    },
    { prop: 'browser', label: '浏览器' },
    {
      prop: 'area',
      label: '位置',
      formatter: (row) => `${row.province || ''}${row.city ? ' - ' + row.city : ''}`
    },
    {
      prop: 'params',
      label: '请求参数',
      formatter: (row) => {
        return h(
          'button',
          {
            class: 'el-button el-button--primary el-button--small is-link',
            onClick: () => showJsonDialog(row.params, '请求参数')
          },
          '查看请求参数'
        )
      }
    },
    {
      prop: 'response',
      label: '返回数据',
      formatter: (row) => {
        return h(
          'button',
          {
            class: 'el-button el-button--primary el-button--small is-link',
            onClick: () => showJsonDialog(row.response, '返回数据')
          },
          '查看返回数据'
        )
      }
    },
    {
      prop: 'useTime',
      label: '请求耗时',
      formatter: (row) => `${row.useTime}ms`
    },
    {
      prop: 'exception',
      label: '异常信息',
      checked: false,
      formatter: (row) => {
        if (row.exception) {
          return h(
            'button',
            {
              class: 'el-button el-button--danger el-button--small is-link',
              onClick: () => showJsonDialog(row.exception, '异常信息')
            },
            '查看异常'
          )
        } else {
          return h('span', '-')
        }
      }
    },
    { prop: 'createDate', label: '创建时间', sortable: true }
  ])

  // 加载日志列表数据
  const loadLogList = async () => {
    loading.value = true
    try {
      // 使用 formFilters 发送请求
      const res = await LogService.getLogList(formFilters as LogListParams)
      if (res.success) {
        logList.value = res.data.records
        pagination.total = res.data.total
        pagination.current = res.data.current
        pagination.size = res.data.size
        pagination.pages = res.data.pages
      } else {
        ElMessage.error(res.message || '获取日志列表失败')
      }
    } catch (error) {
      console.error('获取日志列表失败:', error)
      ElMessage.error('获取日志列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.page = 1 // 搜索时重置为第一页
    loadLogList()
  }

  // 重置查询 (handleReset for ArtSearchBar)
  const handleReset = () => {
    Object.assign(formFilters, { ...initialSearchState })
    loadLogList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: LogRecord[]) => {
    selectedLogs.value = selection
  }

  // 处理批量删除
  const handleDelete = () => {
    if (selectedLogs.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要删除选中的日志记录吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const logIds = selectedLogs.value.map((log) => parseInt(log.id))
          const res = await LogService.deleteLog(logIds)
          if (res.success) {
            ElMessage.success('删除成功')
            loadLogList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除日志记录失败:', error)
          ElMessage.error('删除日志记录时发生错误')
        }
      })
      .catch(() => {
        // 用户取消操作
        ElMessage.info('已取消删除')
      })
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
