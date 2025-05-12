<template>
  <ArtTableFullScreen>
    <div class="redis-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="search"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadRedisList"
        >
          <template #left>
            <el-button
              title="批量删除Redis数据"
              type="danger"
              @click="handleBatchDelete"
              v-auth="'redis_batch_delete'"
              v-ripple
            >
              批量删除
            </el-button>
          </template>
          <template #right>
            <el-form-item label="排序:" style="margin-right: 16px; margin-bottom: 0">
              <el-switch
                v-model="sortByExpireAsc"
                @change="search"
                active-text="过期时间升序"
                inactive-text="过期时间降序"
                inline-prompt
                style="

--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              />
            </el-form-item>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="redisList"
          selection
          v-loading="loading"
          :currentPage="formFilters.page"
          :pageSize="formFilters.limit"
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
        <el-dialog v-model="jsonDialogVisible" :title="jsonDialogTitle" width="60%">
          <div v-if="isDataTooLarge" class="data-size-warning">
            <el-alert
              :title="`数据较大 (${(dataSize / 1024).toFixed(2)} KB)，可能影响浏览器性能`"
              type="warning"
              :closable="false"
            >
              <template #default>
                <el-button size="small" @click="toggleFullData" type="primary">
                  {{ isFullData ? '显示部分数据' : '显示完整数据' }}
                </el-button>
              </template>
            </el-alert>
          </div>
          <div class="json-viewer">
            <pre v-html="formattedJson"></pre>
          </div>
        </el-dialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, computed, h } from 'vue'
  import { ElMessage, ElMessageBox, ElTag, ElButton, ElFormItem, ElSwitch } from 'element-plus'
  import { RedisApi } from '@/api/redisApi'
  import { RedisRecordModel, RedisListParam } from '@/api/model/redisModel'
  import { useCheckedColumns, type ColumnOption } from '@/composables/useCheckedColumns'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { SearchFormItem } from '@/types/search-form'

  // 加载状态
  const loading = ref(false)

  // Redis数据列表
  const redisList = ref<RedisRecordModel[]>([])

  // 选中的记录
  const selectedRedisRecords = ref<RedisRecordModel[]>([])

  // 定义表单搜索初始值
  const initialSearchState = {
    keyPattern: '',
    page: 1,
    limit: 10
  }

  // 排序状态
  const sortByExpireAsc = ref(false)

  // 统一搜索和分页状态
  const formFilters = reactive({ ...initialSearchState })

  // 搜索栏配置
  const formItems: SearchFormItem[] = [
    {
      label: '键',
      prop: 'keyPattern',
      type: 'input',
      config: {
        placeholder: '请输入key搜索',
        clearable: true
      }
    }
  ]

  // 获取Redis类型对应的标签颜色
  const getRedisTypeColor = (
    type: string
  ): 'success' | 'warning' | 'danger' | 'info' | 'primary' => {
    const typeMap: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'primary'> = {
      string: 'success',
      list: 'primary',
      hash: 'warning',
      set: 'info',
      zset: 'danger'
    }

    return typeMap[type] || 'info' // 使用'info'作为默认类型
  }

  // 格式化TTL显示
  const formatTTL = (ttl: number): string => {
    if (ttl < 0) {
      // Handle -1 (永不过期) and -2 (已过期)
      return ttl === -1 ? '永不过期' : '已过期'
    }
    if (ttl < 60) {
      return `${ttl}秒`
    } else if (ttl < 3600) {
      return `${Math.floor(ttl / 60)}分钟${ttl % 60}秒`
    } else if (ttl < 86400) {
      return `${Math.floor(ttl / 3600)}小时${Math.floor((ttl % 3600) / 60)}分钟`
    } else {
      return `${Math.floor(ttl / 86400)}天${Math.floor((ttl % 86400) / 3600)}小时`
    }
  }

  // 列定义
  const columnOptions: ColumnOption[] = [
    { prop: 'key', label: '键名', minWidth: 250 },
    {
      prop: 'type',
      label: '类型',
      width: 120,
      formatter: (row) =>
        h(ElTag, { type: getRedisTypeColor(row.type), size: 'small' }, () => row.type)
    },
    {
      prop: 'ttl',
      label: 'TTL',
      width: 120,
      formatter: (row) => h('span', null, formatTTL(row.ttl))
    },
    {
      prop: 'value',
      label: '值',
      minWidth: 180,
      formatter: (row) =>
        h(ArtButtonTable, {
          title: '查看Redis值',
          text: '查看值',
          icon: '&#xe745;',
          iconColor: '#409EFF',
          auth: 'redis_show_value',
          onClick: () => showJsonDialog(row.value, '键值数据')
        })
    },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 120,
      formatter: (row) =>
        h(ArtButtonTable, {
          title: '删除Redis数据',
          type: 'delete',
          auth: 'redis_delete',
          onClick: () => handleDelete(row)
        })
    }
  ]

  // 列定义与动态显隐
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // JSON预览对话框
  const jsonDialogVisible = ref(false)
  const jsonDialogTitle = ref('')
  const jsonRawData = ref<any>({}) // Keep type any for flexibility
  const isFullData = ref(false)
  const isDataTooLarge = ref(false)
  const dataSize = ref(0)
  const MAX_SAFE_SIZE = 10000

  // 展示JSON数据对话框
  const showJsonDialog = (value: any, title: string) => {
    // Allow any type for value
    try {
      jsonDialogTitle.value = title
      jsonRawData.value = value ?? { 内容: '空数据' } // Handle null/undefined

      const strValue = typeof value === 'string' ? value : JSON.stringify(value ?? {})
      dataSize.value = strValue.length
      isDataTooLarge.value = dataSize.value > MAX_SAFE_SIZE
      isFullData.value = false

      jsonDialogVisible.value = true
    } catch (error) {
      console.error('处理数据失败:', error)
      jsonRawData.value = { 内容: String(value ?? '无法解析的数据') } // Ensure content is stringifiable
      jsonDialogVisible.value = true
    }
  }

  // 切换完整数据查看
  const toggleFullData = () => {
    isFullData.value = !isFullData.value
  }

  // 格式化JSON并添加语法高亮
  const formattedJson = computed(() => {
    let displayValue = jsonRawData.value
    let originalString = ''
    let suffix = ''

    try {
      if (typeof displayValue === 'string') {
        originalString = displayValue
      } else {
        originalString = JSON.stringify(displayValue, null, 2)
      }
    } catch {
      originalString = String(displayValue ?? '') // Fallback to string conversion
    }

    if (isDataTooLarge.value && !isFullData.value) {
      displayValue = originalString.substring(0, MAX_SAFE_SIZE)
      suffix = '...(数据已截断)'
      try {
        // Try to format truncated string for basic display
        displayValue = JSON.stringify(
          JSON.parse(displayValue + (displayValue.startsWith('{') ? '}' : ']')),
          null,
          2
        ) // Basic heuristic parse
        displayValue = displayValue.substring(0, displayValue.lastIndexOf('\n'))
      } catch {
        displayValue = originalString.substring(0, MAX_SAFE_SIZE)
      }
    } else {
      try {
        // Use originalString which should be valid JSON string or original string
        const parsed = JSON.parse(originalString)
        displayValue = JSON.stringify(parsed, null, 2)
      } catch {
        displayValue = originalString // Show raw if not valid JSON
      }
    }

    displayValue = displayValue.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')

    // Fixed regex escapes and highlighting logic
    displayValue = displayValue.replace(
      /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*")/g, // Try escaping the backslash
      function (match: string) {
        return '<span class="json-string">' + match + '</span>'
      }
    )
    displayValue = displayValue.replace(/\b(true|false)\b/g, '<span class="json-boolean">$1</span>')
    displayValue = displayValue.replace(/\b(null)\b/g, '<span class="json-null">$1</span>')
    displayValue = displayValue.replace(
      /(-?\d+(?:\.\d*)?(?:[eE][+-]?\d+)?)/g, // Regex for numbers
      '<span class="json-number">$1</span>'
    )
    // Attempt to highlight keys after string highlighting
    displayValue = displayValue.replace(
      /(<span class="json-string">)("[^"\n]+")(<\/span>)\s*:/g, // Regex for keys
      '$1<span class="json-key">$2</span>$3:'
    )

    return displayValue + suffix
  })

  // 加载Redis列表数据
  const loadRedisList = async () => {
    loading.value = true
    try {
      // Construct params including the separate sort state
      const params: RedisListParam = {
        ...formFilters,
        sortByExpireAsc: sortByExpireAsc.value // Add sort param here
      }
      const res = await RedisApi.getRedisRecord(params)
      if (res.success) {
        redisList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || '获取Redis列表失败')
      }
    } catch (error) {
      console.error('获取Redis列表失败:', error)
      ElMessage.error('获取Redis列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.page = 1 // Ensure page reset on explicit search / sort change
    loadRedisList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters, initialSearchState)
    sortByExpireAsc.value = false // Reset sort state as well
    loadRedisList()
  }

  // 监听选中行变化
  const handleSelectionChange = (selection: RedisRecordModel[]) => {
    selectedRedisRecords.value = selection
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedRedisRecords.value.length === 0) {
      ElMessage.warning('请至少选择一条记录')
      return
    }

    ElMessageBox.confirm('确定要删除选中的Redis键吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          // Use Promise.allSettled for better error handling if needed
          const deletePromises = selectedRedisRecords.value.map((record) =>
            RedisApi.deleteRedisRecord(record.key)
          )
          const results = await Promise.all(deletePromises) // Keep Promise.all for simplicity now

          // Check results - more granular feedback could be added
          const allSucceeded = results.every((res) => res.success)
          if (allSucceeded) {
            ElMessage.success('批量删除成功')
            loadRedisList() // Reload data
          } else {
            // Find which ones failed if necessary, or give a general error
            ElMessage.error('部分或全部删除失败，请重试')
            loadRedisList() // Still reload to reflect potential partial success
          }
        } catch (error) {
          console.error('批量删除Redis记录失败:', error)
          ElMessage.error('批量删除Redis记录时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('已取消批量删除') // Add feedback for cancel
      })
  }

  // 处理单个删除
  const handleDelete = (row: RedisRecordModel) => {
    ElMessageBox.confirm(`确定要删除Redis键 "${row.key}" 吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await RedisApi.deleteRedisRecord(row.key)
          if (res.success) {
            ElMessage.success('删除成功')
            loadRedisList() // Reload data
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除Redis记录失败:', error)
          ElMessage.error('删除Redis记录时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除') // Add feedback for cancel
      })
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.page = page
    loadRedisList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.limit = size
    formFilters.page = 1
    loadRedisList()
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadRedisList()
  })
</script>

<style lang="scss" scoped>
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

  .data-size-warning {
    margin-bottom: 10px;
  }
</style>
