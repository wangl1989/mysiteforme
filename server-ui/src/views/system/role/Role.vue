<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-input
          v-model="searchForm.name"
          :placeholder="$t('role.search.inputRoleName')"
        ></el-input>
      </el-col>
      <div style="width: 12px"></div>
      <el-col :xs="24" :sm="12" :lg="6" class="el-col2">
        <el-button
          :title="$t('role.button.search')"
          v-ripple
          @click="loadRoleData"
          v-auth="'role_search'"
        >
          {{ $t('role.button.search') }}
        </el-button>
        <el-button @click="showDialog('add')" v-auth="'role_add'" v-ripple>
          {{ $t('role.button.add') }}
        </el-button>
      </el-col>
    </el-row>

    <art-table
      :data="tableData"
      :currentPage="pagination.current"
      :pageSize="pagination.size"
      :total="pagination.total"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      v-loading="loading"
    >
      <template #default>
        <el-table-column :label="$t('role.table.roleName')" prop="name" />
        <el-table-column :label="$t('role.table.description')" prop="remarks" />
        <el-table-column :label="$t('role.table.isDefault')" prop="isDefault">
          <template #default="scope">
            <el-tag :type="scope.row.isDefault ? 'danger' : 'warning'">
              {{ scope.row.isDefault ? $t('role.table.yes') : $t('role.table.no') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('role.table.status')" prop="delFlag">
          <template #default="scope">
            <el-tag :type="scope.row.delFlag ? 'primary' : 'info'">
              {{ scope.row.delFlag ? $t('role.table.disabled') : $t('role.table.enabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('role.table.updateTime')"
          prop="updateDate"
          sortable
          @sort-change="handleSortChange"
        >
          <template #default="scope">
            {{ formatDate(scope.row.updateDate) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('role.table.operations')" width="250px">
          <template #default="scope">
            <ArtButtonTable
              :title="$t('role.button.edit')"
              type="edit"
              auth="role_edit"
              @click="showDialog('edit', scope.row)"
            />
            <ArtButtonTable
              :title="$t('role.button.delete')"
              type="delete"
              auth="role_delete"
              @click="deleteRole(scope.row)"
            />
            <ArtButtonTable
              :title="$t('role.button.assignPermission')"
              type="more"
              auth="role_assign"
              @click="navigateToPermissions(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? $t('role.dialog.addTitle') : $t('role.dialog.editTitle')"
      width="30%"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item :label="$t('role.dialog.roleName')" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="$t('role.dialog.description')" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('role.dialog.isDefault')" prop="isDefault">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
        <!-- <el-form-item label="状态">
          <el-switch v-model="form.status" />
        </el-form-item> -->
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            :title="
              dialogType === 'add' ? $t('role.dialog.cancelAdd') : $t('role.dialog.cancelEdit')
            "
            @click="dialogVisible = false"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            :title="
              dialogType === 'add' ? $t('role.dialog.submitAdd') : $t('role.dialog.submitEdit')
            "
            type="primary"
            @click="handleSubmit(formRef)"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import { RoleService } from '@/api/roleApi'
  import { RoleRecord, RoleListParams } from '@/api/model/roleModel'
  import { formatDate } from '@/utils/date'
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const dialogVisible = ref(false)
  const loading = ref(false)
  const router = useRouter()

  // 搜索表单
  const searchForm = reactive<RoleListParams>({
    name: '',
    page: 1,
    limit: 10,
    sortByCreateDateAsc: null
  })

  // 表格数据
  const tableData = ref<RoleRecord[]>([])

  // 分页信息
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
  })

  // 加载角色数据
  const loadRoleData = async () => {
    loading.value = true
    try {
      const response = await RoleService.getRoleList(searchForm)
      if (response.success) {
        tableData.value = response.data.records
        pagination.total = response.data.total
        pagination.current = response.data.current
        pagination.size = response.data.size
        pagination.pages = response.data.pages
      } else {
        ElMessage.error(response.message || t('role.message.getRoleListFailed'))
      }
    } catch (error) {
      console.error(t('role.message.getRoleListError'), error)
      ElMessage.error(t('role.message.operationFailed'))
    } finally {
      loading.value = false
    }
  }

  // 处理分页变化
  const handlePageChange = (page: number) => {
    searchForm.page = page
    loadRoleData()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    searchForm.limit = size
    searchForm.page = 1 // 切换每页数量时重置为第一页
    loadRoleData()
  }

  // 处理排序变化
  const handleSortChange = (column: any) => {
    if (column.prop === 'createDate') {
      searchForm.sortByCreateDateAsc =
        column.order === 'ascending' ? true : column.order === 'descending' ? false : null
    }
    loadRoleData()
  }

  const dialogType = ref('add')

  const showDialog = (type: string, row?: RoleRecord) => {
    dialogVisible.value = true
    dialogType.value = type

    if (type === 'edit' && row) {
      form.id = row.id
      form.name = row.name
      form.remarks = row.remarks || ''
      form.isDefault = row.isDefault
    } else {
      form.id = 0
      form.name = ''
      form.remarks = ''
      form.isDefault = false
    }
  }

  const navigateToPermissions = (row: RoleRecord) => {
    router.push({
      path: '/system/menu/menuPermission',
      query: {
        roleId: row.id.toString(),
        roleName: row.name,
        type: 'role'
      }
    })
  }

  const formRef = ref<FormInstance>()

  const rules = reactive<FormRules>({
    name: [
      { required: true, message: t('role.validation.roleNameRequired'), trigger: 'blur' },
      { min: 2, max: 20, message: t('role.validation.roleNameLength'), trigger: 'blur' }
    ],
    remarks: [
      { required: true, message: t('role.validation.descriptionRequired'), trigger: 'blur' }
    ]
  })

  const form = reactive({
    id: 0,
    name: '',
    remarks: '',
    isDefault: false
  })

  const deleteRole = (row: RoleRecord) => {
    ElMessageBox.confirm(t('role.message.confirmDelete'), t('role.message.confirmDeleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'error'
    })
      .then(async () => {
        try {
          const response = await RoleService.deleteRole(row.id)
          if (response.success) {
            ElMessage.success(t('role.message.deleteSuccess'))
            loadRoleData() // 重新加载数据
          } else {
            ElMessage.error(response.message || t('role.message.deleteFailed'))
          }
        } catch (error) {
          console.error(t('role.message.deleteError'), error)
          ElMessage.error(t('role.message.operationFailed'))
        }
      })
      .catch(() => {
        // 用户取消删除操作
        ElMessage.info(t('role.message.cancelDelete'))
      })
  }

  const handleSubmit = async (formEl: FormInstance | undefined) => {
    if (!formEl) return

    await formEl.validate(async (valid) => {
      if (valid) {
        try {
          if (dialogType.value === 'add') {
            // 调用新增角色API
            const response = await RoleService.addRole({
              name: form.name,
              remarks: form.remarks,
              isDefault: form.isDefault
            })
            if (response.success) {
              ElMessage.success(t('role.message.addSuccess'))
              dialogVisible.value = false
              formEl.resetFields()
              loadRoleData() // 重新加载数据
            } else {
              ElMessage.error(response.message || t('role.message.addFailed'))
            }
          } else {
            // 调用更新角色API
            const response = await RoleService.updateRole({
              id: form.id,
              name: form.name,
              remarks: form.remarks,
              isDefault: form.isDefault
            })
            if (response.success) {
              ElMessage.success(t('role.message.editSuccess'))
              dialogVisible.value = false
              formEl.resetFields()
              loadRoleData() // 重新加载数据
            } else {
              ElMessage.error(response.message || t('role.message.editFailed'))
            }
          }
        } catch (error) {
          console.error(t('role.message.operationFailed'), error)
          ElMessage.error(t('role.message.operationFailed'))
        }
      }
    })
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadRoleData()
  })
</script>

<style lang="scss" scoped>
  .page-content {
    .svg-icon {
      width: 1.8em;
      height: 1.8em;
      overflow: hidden;
      vertical-align: -8px;
      fill: currentcolor;
    }
  }

  /* 修复aria-hidden警告 */
  :deep(div[aria-hidden='true']) {
    display: none !important;
  }
</style>
