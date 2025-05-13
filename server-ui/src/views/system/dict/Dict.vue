<template>
  <ArtTableFullScreen>
    <div class="dict-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="search"
        auth="dict_search"
        :isExpand="true"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadDictList"
        >
          <template #left>
            <el-button type="primary" @click="handleAdd" v-auth="'dict_add'" v-ripple>{{
              $t('dict.add')
            }}</el-button>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="dictList"
          v-loading="loading"
          :currentPage="formFilters.page"
          :pageSize="formFilters.limit"
          :total="pagination.total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          :marginTop="10"
          height="100%"
        >
          <template #default>
            <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
          </template>
        </ArtTable>

        <!-- 添加/编辑字典对话框 -->
        <ElDialog
          v-model="dialogVisible"
          :title="isEdit ? $t('dict.edit') : $t('dict.add')"
          width="500px"
          destroy-on-close
        >
          <el-form
            ref="dictFormRef"
            :model="formData"
            :rules="rules"
            label-width="100px"
            class="dict-form"
          >
            <el-form-item :label="$t('dict.form.type')" prop="type">
              <el-input
                v-model="formData.type"
                :placeholder="$t('dict.form.typePlaceholder')"
                :disabled="typeDisabled"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('dict.form.label')" prop="label">
              <el-input
                v-model="formData.label"
                :placeholder="$t('dict.form.labelPlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('dict.form.value')" prop="value">
              <el-input
                v-model="formData.value"
                :placeholder="$t('dict.form.valuePlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('dict.form.description')" prop="description">
              <el-input
                type="textarea"
                v-model="formData.description"
                :placeholder="$t('dict.form.descriptionPlaceholder')"
                :rows="3"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('dict.form.sort')" prop="sort">
              <el-input-number
                v-model="formData.sort"
                :min="0"
                :max="999"
                controls-position="right"
              />
            </el-form-item>
          </el-form>

          <template #footer>
            <div class="dialog-footer">
              <el-button
                :title="isEdit ? $t('dict.edit') : $t('dict.add')"
                @click="dialogVisible = false"
                >{{ $t('dict.button.cancel') }}</el-button
              >
              <el-button
                :title="isEdit ? $t('dict.edit') : $t('dict.add')"
                type="primary"
                @click="submitForm"
                :loading="submitLoading"
                >{{ $t('dict.button.confirm') }}</el-button
              >
            </div>
          </template>
        </ElDialog>

        <!-- 编辑字典类型对话框 -->
        <ElDialog
          v-model="typeDialogVisible"
          :title="$t('dict.editType')"
          width="500px"
          destroy-on-close
        >
          <el-form
            ref="typeFormRef"
            :model="typeFormData"
            :rules="typeRules"
            label-width="100px"
            class="dict-form"
          >
            <el-form-item :label="$t('dict.form.oldType')" prop="oldType">
              <el-input v-model="typeFormData.oldType" disabled></el-input>
            </el-form-item>
            <el-form-item :label="$t('dict.form.newType')" prop="newType">
              <el-input
                v-model="typeFormData.newType"
                :placeholder="$t('dict.form.newTypePlaceholder')"
              ></el-input>
            </el-form-item>
          </el-form>

          <template #footer>
            <div class="dialog-footer">
              <el-button @click="typeDialogVisible = false">{{
                $t('dict.button.cancel')
              }}</el-button>
              <el-button type="primary" @click="submitTypeForm" :loading="submitLoading">{{
                $t('dict.button.confirm')
              }}</el-button>
            </div>
          </template>
        </ElDialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted, h } from 'vue'
  import { ElMessage, ElMessageBox, FormInstance, FormRules, ElButton, ElSpace } from 'element-plus'
  import { DictService } from '@/api/dictApi'
  import { useCheckedColumns, type ColumnOption } from '@/composables/useCheckedColumns'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import {
    DictRecord,
    DictListParams,
    AddDictParams,
    EditDictParams,
    EditDictTypeParams
  } from '@/api/model/dictModel'
  import { SearchFormItem } from '@/types/search-form'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 加载状态
  const loading = ref(false)

  // 字典数据列表
  const dictList = ref<DictRecord[]>([])

  // 定义表单搜索初始值
  const initialSearchState = {
    type: '',
    value: '',
    label: '',
    page: 1,
    limit: 10
  }

  // 统一搜索和分页状态
  const formFilters = ref({ ...initialSearchState })

  // 搜索栏配置
  const formItems: SearchFormItem[] = [
    {
      label: t('dict.form.type'),
      prop: 'type',
      type: 'input',
      config: {
        placeholder: t('dict.search.typePlaceholder'),
        clearable: true
      }
    },
    {
      label: t('dict.form.label'),
      prop: 'label',
      type: 'input',
      config: {
        placeholder: t('dict.search.labelPlaceholder'),
        clearable: true
      }
    },
    {
      label: t('dict.form.value'),
      prop: 'value',
      type: 'input',
      config: {
        placeholder: t('dict.search.valuePlaceholder'),
        clearable: true
      }
    }
  ]

  // 列定义
  const columnOptions: ColumnOption[] = [
    { prop: 'type', label: t('dict.table.type') },
    { prop: 'label', label: t('dict.table.label') },
    { prop: 'value', label: t('dict.table.value') },
    { prop: 'description', label: t('dict.table.description') },
    { prop: 'sort', label: t('dict.table.sort'), sortable: true, checked: false },
    { prop: 'createDate', label: t('dict.table.createDate'), sortable: true, checked: false },
    { prop: 'updateDate', label: t('dict.table.updateDate'), sortable: true, checked: false },
    {
      prop: 'actions',
      label: t('dict.table.operation'),
      fixed: 'right',
      width: 280,
      formatter: (row: DictRecord) =>
        h(ElSpace, null, () => [
          h(ArtButtonTable, {
            title: t('dict.operation.edit'),
            type: 'edit',
            auth: 'dict_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: t('dict.operation.addByType'),
            icon: '&#xe623;',
            iconColor: '#67C23A',
            auth: 'dict_type_add',
            onClick: () => handleAddByType(row)
          }),
          h(ArtButtonTable, {
            title: t('dict.operation.editType'),
            icon: '&#xe720;',
            iconColor: '#E6A23C',
            auth: 'dict_edit_type',
            onClick: () => handleEditType(row)
          }),
          h(ArtButtonTable, {
            title: t('dict.operation.delete'),
            type: 'delete',
            auth: 'dict_delete',
            onClick: () => handleDelete(row)
          })
        ])
    }
  ]

  // 列定义与动态显隐
  const { columns, columnChecks } = useCheckedColumns(() => columnOptions)

  // 分页信息
  const pagination = reactive({
    total: 0
  })

  // 对话框控制
  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const submitLoading = ref(false)
  const dictFormRef = ref<FormInstance>()
  const typeDisabled = ref(false)

  // 编辑类型对话框
  const typeDialogVisible = ref(false)
  const typeFormRef = ref<FormInstance>()
  const typeFormData = reactive<EditDictTypeParams>({
    oldType: '',
    newType: ''
  })

  // 类型表单校验规则
  const typeRules = reactive<FormRules>({
    newType: [{ required: true, message: t('dict.rules.newType.required'), trigger: 'blur' }]
  })

  // 表单数据
  const formData = reactive<AddDictParams & { id?: number }>({
    type: '',
    label: '',
    value: '',
    description: '',
    sort: 0
  })

  // 表单验证规则
  const rules = reactive<FormRules>({
    type: [{ required: true, message: t('dict.rules.type.required'), trigger: 'blur' }],
    label: [{ required: true, message: t('dict.rules.label.required'), trigger: 'blur' }],
    value: [{ required: true, message: t('dict.rules.value.required'), trigger: 'blur' }]
  })

  // 加载字典列表数据
  const loadDictList = async () => {
    loading.value = true
    try {
      const params: DictListParams = {
        ...formFilters.value,
        sortByCreateDateAsc: false,
        sortBySortAsc: true
      }

      const res = await DictService.getDictList(params)
      if (res.success) {
        dictList.value = res.data.records
        pagination.total = res.data.total
      } else {
        ElMessage.error(res.message || t('dict.messages.getListFailed'))
      }
    } catch (error) {
      console.error('获取字典列表失败:', error)
      ElMessage.error(t('dict.messages.getListError'))
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const search = () => {
    formFilters.value.page = 1
    loadDictList()
  }

  // 重置查询
  const handleReset = () => {
    Object.assign(formFilters.value, initialSearchState)
    loadDictList()
  }

  // 处理分页变化
  const handleCurrentChange = (page: number) => {
    formFilters.value.page = page
    loadDictList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size
    formFilters.value.page = 1
    loadDictList()
  }

  // 处理新增字典
  const handleAdd = () => {
    isEdit.value = false
    dialogVisible.value = true
    typeDisabled.value = false

    Object.assign(formData, {
      id: undefined,
      type: '',
      label: '',
      value: '',
      description: '',
      sort: 0
    })
    dictFormRef.value?.resetFields()
  }

  // 处理根据类型添加字典
  const handleAddByType = (row: DictRecord) => {
    isEdit.value = false
    dialogVisible.value = true
    typeDisabled.value = true

    Object.assign(formData, {
      id: undefined,
      type: row.type,
      label: '',
      value: '',
      description: '',
      sort: 0
    })
    dictFormRef.value?.resetFields()
    formData.type = row.type || ''
  }

  // 处理编辑字典
  const handleEdit = (row: DictRecord) => {
    isEdit.value = true
    dialogVisible.value = true
    typeDisabled.value = true

    Object.assign(formData, {
      id: row.id,
      type: row.type || '',
      label: row.label || '',
      value: row.value || '',
      description: row.description || '',
      sort: row.sort || 0
    })
    dictFormRef.value?.resetFields()
    formData.id = row.id
    formData.type = row.type || ''
    formData.label = row.label || ''
    formData.value = row.value || ''
    formData.description = row.description || ''
    formData.sort = row.sort || 0
  }

  // 处理编辑字典类型
  const handleEditType = (row: DictRecord) => {
    typeDialogVisible.value = true

    typeFormData.oldType = row.type || ''
    typeFormData.newType = ''
    typeFormRef.value?.resetFields()
    typeFormData.oldType = row.type || ''
  }

  // 处理删除字典
  const handleDelete = (row: DictRecord) => {
    ElMessageBox.confirm(t('dict.dialog.confirmDelete'), t('dict.dialog.confirmDeleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await DictService.deleteDict(row.id)
          if (res.success) {
            ElMessage.success(t('dict.dialog.deleteSuccess'))
            loadDictList()
          } else {
            ElMessage.error(res.message || t('dict.dialog.deleteFailed'))
          }
        } catch (error) {
          console.error('删除字典失败:', error)
          ElMessage.error(t('dict.dialog.deleteError'))
        }
      })
      .catch(() => {
        ElMessage.info(t('dict.dialog.cancelDelete'))
      })
  }

  // 提交类型表单
  const submitTypeForm = async () => {
    if (!typeFormRef.value) return

    await typeFormRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true

        try {
          const res = await DictService.editDictType(typeFormData)

          if (res.success) {
            ElMessage.success(t('dict.dialog.typeUpdateSuccess'))
            typeDialogVisible.value = false
            loadDictList()
          } else {
            ElMessage.error(res.message || t('dict.dialog.operationFailed'))
          }
        } catch (error) {
          console.error('更新字典类型失败:', error)
          ElMessage.error(t('dict.dialog.typeUpdateError'))
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 提交表单
  const submitForm = async () => {
    if (!dictFormRef.value) return

    await dictFormRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true

        try {
          let res

          if (isEdit.value && formData.id !== undefined) {
            // 编辑
            const params: EditDictParams = {
              id: formData.id,
              type: formData.type,
              label: formData.label,
              value: formData.value,
              description: formData.description,
              sort: formData.sort
            }
            res = await DictService.updateDict(params)
          } else {
            // 新增
            const params: AddDictParams = {
              type: formData.type,
              label: formData.label,
              value: formData.value,
              description: formData.description,
              sort: formData.sort
            }
            res = await DictService.addDict(params)
          }

          if (res.success) {
            ElMessage.success(
              isEdit.value ? t('dict.dialog.editSuccess') : t('dict.dialog.addSuccess')
            )
            dialogVisible.value = false
            loadDictList()
          } else {
            ElMessage.error(res.message || t('dict.dialog.operationFailed'))
          }
        } catch (error) {
          console.error(isEdit.value ? '编辑失败:' : '新增失败:', error)
          ElMessage.error(isEdit.value ? t('dict.dialog.editError') : t('dict.dialog.addError'))
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadDictList()
  })
</script>

<style lang="scss" scoped>
  .dict-form {
    .el-input-number {
      width: 100%;
    }
  }
</style>
