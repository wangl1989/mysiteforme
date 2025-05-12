<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-input v-model="searchForm.name" placeholder="请输入角色名称"></el-input>
      </el-col>
      <div style="width: 12px"></div>
      <el-col :xs="24" :sm="12" :lg="6" class="el-col2">
        <el-button title="搜索角色" v-ripple @click="loadRoleData" v-auth="'role_search'"
          >搜索</el-button
        >
        <el-button @click="showDialog('add')" v-auth="'role_add'" v-ripple>新增角色</el-button>
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
        <el-table-column label="角色名称" prop="name" />
        <el-table-column label="描述" prop="remarks" />
        <el-table-column label="是否默认" prop="isDefault">
          <template #default="scope">
            <el-tag :type="scope.row.isDefault ? 'danger' : 'warning'">
              {{ scope.row.isDefault ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="delFlag">
          <template #default="scope">
            <el-tag :type="scope.row.delFlag ? 'primary' : 'info'">
              {{ scope.row.delFlag ? '禁用' : '启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="更新时间"
          prop="updateDate"
          sortable
          @sort-change="handleSortChange"
        >
          <template #default="scope">
            {{ formatDate(scope.row.updateDate) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="250px">
          <template #default="scope">
            <ArtButtonTable
              title="编辑角色"
              type="edit"
              auth="role_edit"
              @click="showDialog('edit', scope.row)"
            />
            <ArtButtonTable
              title="删除角色"
              type="delete"
              auth="role_delete"
              @click="deleteRole(scope.row)"
            />
            <ArtButtonTable
              title="给角色分配菜单权限"
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
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      width="30%"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
        <!-- <el-form-item label="状态">
          <el-switch v-model="form.status" />
        </el-form-item> -->
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            :title="dialogType === 'add' ? '取消新增角色' : '取消编辑角色'"
            @click="dialogVisible = false"
            >取消</el-button
          >
          <el-button
            :title="dialogType === 'add' ? '保存新增角色' : '保存编辑角色'"
            type="primary"
            @click="handleSubmit(formRef)"
            >提交</el-button
          >
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
        ElMessage.error(response.message || '获取角色列表失败')
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
      ElMessage.error('获取角色列表时发生错误')
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
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    remarks: [{ required: true, message: '请输入角色描述', trigger: 'blur' }]
  })

  const form = reactive({
    id: 0,
    name: '',
    remarks: '',
    isDefault: false
  })

  const deleteRole = (row: RoleRecord) => {
    ElMessageBox.confirm('确定删除该角色吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
      .then(async () => {
        try {
          const response = await RoleService.deleteRole(row.id)
          if (response.success) {
            ElMessage.success('删除成功')
            loadRoleData() // 重新加载数据
          } else {
            ElMessage.error(response.message || '删除失败')
          }
        } catch (error) {
          console.error('删除角色失败:', error)
          ElMessage.error('删除角色失败，请重试')
        }
      })
      .catch(() => {
        // 用户取消删除操作
        ElMessage.info('用户取消删除角色操作')
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
              ElMessage.success('新增成功')
              dialogVisible.value = false
              formEl.resetFields()
              loadRoleData() // 重新加载数据
            } else {
              ElMessage.error(response.message || '新增失败')
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
              ElMessage.success('修改成功')
              dialogVisible.value = false
              formEl.resetFields()
              loadRoleData() // 重新加载数据
            } else {
              ElMessage.error(response.message || '修改失败')
            }
          }
        } catch (error) {
          console.error('提交角色数据失败:', error)
          ElMessage.error('操作失败，请重试')
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
