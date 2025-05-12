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
            <el-button type="primary" @click="handleAdd" v-auth="'dict_add'" v-ripple
              >新增字典</el-button
            >
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
          :title="isEdit ? '编辑字典' : '新增字典'"
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
            <el-form-item label="字典类型" prop="type">
              <el-input
                v-model="formData.type"
                placeholder="请输入字典类型"
                :disabled="typeDisabled"
              ></el-input>
            </el-form-item>
            <el-form-item label="标签名" prop="label">
              <el-input v-model="formData.label" placeholder="请输入标签名"></el-input>
            </el-form-item>
            <el-form-item label="数据值" prop="value">
              <el-input v-model="formData.value" placeholder="请输入数据值"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input
                type="textarea"
                v-model="formData.description"
                placeholder="请输入描述"
                :rows="3"
              ></el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
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
                :title="isEdit ? '取消编辑字典' : '取消新增字典'"
                @click="dialogVisible = false"
                >取消</el-button
              >
              <el-button
                :title="isEdit ? '保存编辑字典' : '保存新增字典'"
                type="primary"
                @click="submitForm"
                :loading="submitLoading"
                >确定</el-button
              >
            </div>
          </template>
        </ElDialog>

        <!-- 编辑字典类型对话框 -->
        <ElDialog v-model="typeDialogVisible" title="编辑字典类型" width="500px" destroy-on-close>
          <el-form
            ref="typeFormRef"
            :model="typeFormData"
            :rules="typeRules"
            label-width="100px"
            class="dict-form"
          >
            <el-form-item label="原字典类型" prop="oldType">
              <el-input v-model="typeFormData.oldType" disabled></el-input>
            </el-form-item>
            <el-form-item label="新字典类型" prop="newType">
              <el-input v-model="typeFormData.newType" placeholder="请输入新的字典类型"></el-input>
            </el-form-item>
          </el-form>

          <template #footer>
            <div class="dialog-footer">
              <el-button title="取消编辑字典类型" @click="typeDialogVisible = false"
                >取消</el-button
              >
              <el-button
                title="保存字典类型"
                type="primary"
                @click="submitTypeForm"
                :loading="submitLoading"
                >确定</el-button
              >
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

  // 搜索栏配置 (Aligned with Account.vue structure)
  const formItems: SearchFormItem[] = [
    {
      label: '字典类型',
      prop: 'type',
      type: 'input',
      config: {
        placeholder: '请输入字典类型搜索',
        clearable: true
      }
    },
    {
      label: '标签名',
      prop: 'label',
      type: 'input',
      config: {
        placeholder: '请输入标签名搜索',
        clearable: true
      }
    },
    {
      label: '数据值',
      prop: 'value',
      type: 'input',
      config: {
        placeholder: '请输入数据值搜索',
        clearable: true
      }
    }
  ]

  // 列定义 (Moved definition outside, added formatter inline)
  const columnOptions: ColumnOption[] = [
    { prop: 'type', label: '字典类型' },
    { prop: 'label', label: '标签名' },
    { prop: 'value', label: '数据值' },
    { prop: 'description', label: '描述' },
    { prop: 'sort', label: '排序', sortable: true, checked: false },
    { prop: 'createDate', label: '创建时间', sortable: true, checked: false },
    { prop: 'updateDate', label: '更新时间', sortable: true, checked: false },
    {
      prop: 'actions',
      label: '操作',
      fixed: 'right',
      width: 280,
      formatter: (row: DictRecord) =>
        h(ElSpace, null, () => [
          h(ArtButtonTable, {
            title: '字典编辑',
            type: 'edit',
            auth: 'dict_edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            title: '根据类型增加字典',
            icon: '&#xe623;',
            iconColor: '#67C23A',
            auth: 'dict_type_add',
            onClick: () => handleAddByType(row)
          }),
          h(ArtButtonTable, {
            title: '编辑字典类型',
            icon: '&#xe720;',
            iconColor: '#E6A23C',
            auth: 'dict_edit_type',
            onClick: () => handleEditType(row)
          }),
          h(ArtButtonTable, {
            title: '删除字典',
            type: 'delete',
            auth: 'dict_delete',
            onClick: () => handleDelete(row)
          })
        ])
    }
  ]

  // 列定义与动态显隐 (Pass a function returning columnOptions)
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
    newType: [{ required: true, message: '请输入新的字典类型', trigger: 'blur' }]
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
    type: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
    label: [{ required: true, message: '请输入标签名', trigger: 'blur' }],
    value: [{ required: true, message: '请输入数据值', trigger: 'blur' }]
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
        ElMessage.error(res.message || '获取字典列表失败')
      }
    } catch (error) {
      console.error('获取字典列表失败:', error)
      ElMessage.error('获取字典列表时发生错误')
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
    ElMessageBox.confirm('确认删除该字典吗？此操作不可恢复！', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          const res = await DictService.deleteDict(row.id)
          if (res.success) {
            ElMessage.success('删除成功')
            loadDictList()
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除字典失败:', error)
          ElMessage.error('删除字典时发生错误')
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除')
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
            ElMessage.success('字典类型更新成功')
            typeDialogVisible.value = false
            loadDictList()
          } else {
            ElMessage.error(res.message || '操作失败')
          }
        } catch (error) {
          console.error('更新字典类型失败:', error)
          ElMessage.error('更新字典类型时发生错误')
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
            ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
            dialogVisible.value = false
            loadDictList()
          } else {
            ElMessage.error(res.message || '操作失败')
          }
        } catch (error) {
          console.error(isEdit.value ? '编辑失败:' : '新增失败:', error)
          ElMessage.error(isEdit.value ? '编辑时发生错误' : '新增时发生错误')
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
