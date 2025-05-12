<template>
  <div class="table-preview-container">
    <el-tabs type="border-card">
      <el-tab-pane label="列表页预览">
        <div class="preview-list">
          <h3>列表页展示</h3>
          <div class="search-form">
            <el-form :model="searchForm" inline>
              <el-row :gutter="20">
                <template v-for="field in queryFields" :key="field.id">
                  <el-col :xs="24" :sm="12" :md="8" :lg="6">
                    <el-form-item
                      :label="field.businessName || field.columnComment || field.columnName"
                    >
                      <el-input
                        v-if="isInputType(field)"
                        v-model="searchForm[field.columnName]"
                        :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                        clearable
                        style="width: 100%"
                      />

                      <el-input
                        v-else-if="isSelectType(field)"
                        v-model="searchForm[field.columnName]"
                        :placeholder="`请选择${field.businessName || field.columnComment || field.columnName}`"
                        clearable
                        style="width: 100%"
                      />

                      <el-date-picker
                        v-else-if="isDateType(field)"
                        v-model="searchForm[field.columnName]"
                        :placeholder="`选择${field.businessName || field.columnComment || field.columnName}`"
                        style="width: 100%"
                      />

                      <el-input-number
                        v-else-if="isNumberType(field)"
                        v-model="searchForm[field.columnName]"
                        :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                        style="width: 100%"
                      />

                      <el-switch
                        v-else-if="isSwitchType(field)"
                        v-model="searchForm[field.columnName]"
                      />

                      <el-input
                        v-else
                        v-model="searchForm[field.columnName]"
                        :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                        clearable
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </template>
              </el-row>
              <div class="search-buttons">
                <el-button type="primary">搜索</el-button>
                <el-button>重置</el-button>
              </div>
            </el-form>
          </div>

          <div class="table-area">
            <div class="table-toolbar">
              <el-button type="primary">新增</el-button>
              <el-button type="danger">批量删除</el-button>
            </div>
            <el-table :data="tableData" border stripe style="width: 100%">
              <el-table-column type="selection" width="55" />
              <template v-for="field in listFields" :key="field.id">
                <el-table-column
                  :prop="field.columnName"
                  :label="field.businessName || field.columnComment || field.columnName"
                  :min-width="getColumnWidth(field)"
                />
              </template>
              <el-table-column label="操作" fixed="right" width="180">
                <template #default>
                  <el-button type="primary" link>编辑</el-button>
                  <el-button type="danger" link>删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination">
              <el-pagination
                background
                :total="100"
                :page-size="10"
                :current-page="1"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, size, prev, pager, next, jumper"
                @size-change="() => {}"
                @current-change="() => {}"
              />
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="新增表单预览">
        <div class="preview-form">
          <h3>新增表单</h3>
          <el-form
            :model="addForm"
            label-width="120px"
            label-position="right"
            class="preview-add-form"
          >
            <el-row :gutter="24">
              <template v-for="field in addFields" :key="field.id">
                <el-col :xs="24" :sm="12">
                  <el-form-item
                    :label="field.businessName || field.columnComment || field.columnName"
                    :prop="field.columnName"
                    :required="!field.isNullable"
                  >
                    <el-input
                      v-if="isInputType(field)"
                      v-model="addForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />

                    <el-input
                      v-else-if="isTextareaType(field)"
                      v-model="addForm[field.columnName]"
                      type="textarea"
                      :rows="3"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-input
                      v-else-if="isSelectType(field)"
                      v-model="addForm[field.columnName]"
                      :placeholder="`请选择${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />

                    <el-date-picker
                      v-else-if="isDateType(field)"
                      v-model="addForm[field.columnName]"
                      :placeholder="`选择${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-input-number
                      v-else-if="isNumberType(field)"
                      v-model="addForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-switch
                      v-else-if="isSwitchType(field)"
                      v-model="addForm[field.columnName]"
                    />

                    <el-input
                      v-else
                      v-model="addForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </template>
            </el-row>
            <div class="form-buttons">
              <el-button>取消</el-button>
              <el-button type="primary">确定</el-button>
            </div>
          </el-form>
        </div>
      </el-tab-pane>

      <el-tab-pane label="编辑表单预览">
        <div class="preview-form">
          <h3>编辑表单</h3>
          <el-form
            :model="editForm"
            label-width="120px"
            label-position="right"
            class="preview-edit-form"
          >
            <el-row :gutter="24">
              <template v-for="field in editFields" :key="field.id">
                <el-col :xs="24" :sm="12">
                  <el-form-item
                    :label="field.businessName || field.columnComment || field.columnName"
                    :prop="field.columnName"
                    :required="!field.isNullable"
                  >
                    <el-input
                      v-if="isInputType(field)"
                      v-model="editForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />

                    <el-input
                      v-else-if="isTextareaType(field)"
                      v-model="editForm[field.columnName]"
                      type="textarea"
                      :rows="3"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-input
                      v-else-if="isSelectType(field)"
                      v-model="editForm[field.columnName]"
                      :placeholder="`请选择${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />

                    <el-date-picker
                      v-else-if="isDateType(field)"
                      v-model="editForm[field.columnName]"
                      :placeholder="`选择${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-input-number
                      v-else-if="isNumberType(field)"
                      v-model="editForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      style="width: 100%"
                    />

                    <el-switch
                      v-else-if="isSwitchType(field)"
                      v-model="editForm[field.columnName]"
                    />

                    <el-input
                      v-else
                      v-model="editForm[field.columnName]"
                      :placeholder="`请输入${field.businessName || field.columnComment || field.columnName}`"
                      clearable
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </template>
            </el-row>
            <div class="form-buttons">
              <el-button>取消</el-button>
              <el-button type="primary">确定</el-button>
            </div>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, reactive, onMounted } from 'vue'
  import { TableConfigModel, TableFieldConfigModel } from '@/api/model/tableConfigModel'

  // 定义传入的属性
  const props = defineProps<{
    tableConfig?: TableConfigModel
    fieldList?: TableFieldConfigModel[]
  }>()

  // 根据配置过滤出列表显示字段
  const listFields = computed(() => props.fieldList?.filter((field) => field.isListVisible) || [])

  // 根据配置过滤出查询条件字段
  const queryFields = computed(() => props.fieldList?.filter((field) => field.isQueryField) || [])

  // 根据配置过滤出新增表单字段
  const addFields = computed(() => props.fieldList?.filter((field) => field.isAddVisible) || [])

  // 根据配置过滤出编辑表单字段
  const editFields = computed(() => props.fieldList?.filter((field) => field.isEditVisible) || [])

  // 搜索表单数据
  const searchForm = reactive<Record<string, any>>({})

  // 新增表单数据
  const addForm = reactive<Record<string, any>>({})

  // 编辑表单数据
  const editForm = reactive<Record<string, any>>({})

  // 模拟表格数据
  const tableData = ref<any[]>([
    // 默认添加一条演示数据
    {}
  ])

  // 组件初始化
  onMounted(() => {
    initForms()
    initTableData()
  })

  // 初始化表单数据和表格数据
  const initForms = () => {
    if (props.fieldList) {
      // 初始化搜索、新增和编辑表单
      props.fieldList.forEach((field) => {
        const fieldName = field.columnName

        // 查询表单初始化
        if (field.isQueryField) {
          searchForm[fieldName] = getDefaultValue(field)
        }

        // 新增表单初始化
        if (field.isAddVisible) {
          addForm[fieldName] = getDefaultValue(field)
        }

        // 编辑表单初始化
        if (field.isEditVisible) {
          editForm[fieldName] = getDefaultValue(field)
        }
      })
    }
  }

  // 初始化表格演示数据
  const initTableData = () => {
    if (props.fieldList) {
      const demoRow: Record<string, any> = {}

      // 为列表字段生成模拟数据
      listFields.value.forEach((field) => {
        demoRow[field.columnName] = getMockValue(field)
      })

      // 生成5条模拟数据
      tableData.value = Array(5)
        .fill(0)
        .map((_, index) => {
          const item = { ...demoRow, id: index + 1 }
          // 对每个字段稍微变化一下值，以便区分
          Object.entries(item).forEach(([key, value]) => {
            if (typeof value === 'string' && key !== 'id') {
              ;(item as Record<string, any>)[key] = `${value}${index + 1}`
            }
          })
          return item
        })
    }
  }

  // 根据字段类型生成默认值
  const getDefaultValue = (field: TableFieldConfigModel): any => {
    const type = field.columnType?.toLowerCase() || ''

    if (type.includes('int') || type.includes('number') || type.includes('decimal')) {
      return 0
    } else if (type.includes('date') || type.includes('time')) {
      return ''
    } else if (type.includes('bool')) {
      return false
    } else {
      return ''
    }
  }

  // 生成模拟数据值
  const getMockValue = (field: TableFieldConfigModel): any => {
    const type = field.columnType?.toLowerCase() || ''
    const name = field.columnName?.toLowerCase() || ''

    if (type.includes('int') || type.includes('number') || type.includes('decimal')) {
      return Math.floor(Math.random() * 100)
    } else if (type.includes('date') || type.includes('time')) {
      return '2023-10-24'
    } else if (type.includes('bool')) {
      return Math.random() > 0.5
    } else if (name.includes('name') || name.includes('title')) {
      return '示例名称'
    } else if (name.includes('desc') || name.includes('remark')) {
      return '这是一段示例描述文本，用于演示效果'
    } else if (name.includes('status')) {
      return '正常'
    } else if (name.includes('type')) {
      return '类型A'
    } else {
      return `${field.businessName || field.columnComment || field.columnName}示例值`
    }
  }

  // 获取表格列宽度
  const getColumnWidth = (field: TableFieldConfigModel): number => {
    const name = field.columnName.toLowerCase()
    const type = field.columnType?.toLowerCase() || ''

    if (type.includes('int') || type.includes('number') || type.includes('decimal')) {
      return 100
    } else if (type.includes('date') || type.includes('time')) {
      return 160
    } else if (type.includes('bool') || name.includes('status')) {
      return 100
    } else if (name.includes('desc') || name.includes('remark') || name.includes('content')) {
      return 250
    } else {
      return 150
    }
  }

  // 判断字段是否为输入框类型
  const isInputType = (field: TableFieldConfigModel): boolean => {
    return field.formComponentType === 'INPUT'
  }

  // 判断字段是否为文本域类型
  const isTextareaType = (field: TableFieldConfigModel): boolean => {
    return field.formComponentType === 'TEXTAREA'
  }

  // 判断字段是否为下拉框类型
  const isSelectType = (field: TableFieldConfigModel): boolean => {
    return (
      field.formComponentType === 'SELECT' ||
      field.formComponentType === 'RADIO' ||
      field.formComponentType === 'CHECKBOX'
    )
  }

  // 判断字段是否为日期类型
  const isDateType = (field: TableFieldConfigModel): boolean => {
    return (
      field.formComponentType === 'DATE_PICKER' ||
      field.formComponentType === 'TIME_PICKER' ||
      field.formComponentType === 'DATETIME_PICKER'
    )
  }

  // 判断字段是否为数字类型
  const isNumberType = (field: TableFieldConfigModel): boolean => {
    return field.formComponentType === 'INPUT_NUMBER'
  }

  // 判断字段是否为开关类型
  const isSwitchType = (field: TableFieldConfigModel): boolean => {
    return field.formComponentType === 'SWITCH'
  }
</script>

<style lang="scss" scoped>
  .table-preview-container {
    padding: 0;

    .el-tabs {
      min-height: 600px;
    }

    h3 {
      margin-top: 0;
      margin-bottom: 20px;
      font-size: 18px;
      color: var(--el-text-color-primary);
    }

    .preview-list,
    .preview-form {
      padding: 20px;
    }

    .search-form {
      padding: 20px;
      margin-bottom: 20px;
      background-color: var(--el-bg-color);
      border-radius: 4px;
    }

    .search-buttons {
      display: flex;
      justify-content: center;
      margin-top: 10px;
    }

    .table-toolbar {
      display: flex;
      gap: 10px;
      margin-bottom: 10px;
    }

    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }

    .form-buttons {
      display: flex;
      gap: 10px;
      justify-content: center;
      margin-top: 20px;
    }

    .preview-add-form,
    .preview-edit-form {
      padding: 20px;
      background-color: var(--el-bg-color);
      border-radius: 4px;
    }
  }
</style>
