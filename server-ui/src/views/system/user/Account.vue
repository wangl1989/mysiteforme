<template>
  <ArtTableFullScreen>
    <div class="account-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="search"
        auth="user_search"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="loadUserData"
        >
          <template #left>
            <ElButton @click="showDialog('add')" v-auth="'user_add'" v-ripple>
              {{ $t('user.account.add') }}
            </ElButton>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
        <ArtTable
          :data="tableData"
          selection
          pagination
          :currentPage="pagination.current"
          :pageSize="pagination.size"
          :total="pagination.total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          v-loading="loading"
          :marginTop="10"
          height="100%"
        >
          <template #default>
            <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
          </template>
        </ArtTable>
        <ElDialog
          v-model="dialogVisible"
          :title="dialogType === 'add' ? $t('user.account.addTitle') : $t('user.account.editTitle')"
          width="30%"
          destroy-on-close
        >
          <div v-if="dialogType === 'add'" class="info-alert">
            <div class="info-icon">
              <i class="iconfont-sys" v-html="'&#xe71d;'"></i>
            </div>
            <div class="info-content">
              <p>{{ $t('user.account.defaultPassword') }}</p>
            </div>
          </div>
          <div v-loading="formLoading">
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
              <el-form-item :label="$t('user.account.userName')" prop="nickName">
                <el-input v-model="formData.nickName" />
              </el-form-item>
              <el-form-item :label="$t('user.account.loginName')" prop="loginName">
                <el-input v-model="formData.loginName" :disabled="dialogType === 'edit'" />
                <div v-if="dialogType === 'add'" class="form-tip">
                  {{ $t('user.account.loginNameRule') }}
                </div>
              </el-form-item>
              <el-form-item :label="$t('user.account.phone')" prop="tel">
                <el-input v-model="formData.tel" />
              </el-form-item>
              <el-form-item :label="$t('user.account.email')" prop="email">
                <el-input v-model="formData.email" />
              </el-form-item>
              <el-form-item :label="$t('user.account.role')" prop="roles">
                <el-checkbox-group v-model="formData.roleIds">
                  <el-checkbox v-for="role in roleOptions" :key="role.id" :value="role.id">
                    {{ role.name }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item :label="$t('user.account.remarks')" prop="remarks">
                <el-input v-model="formData.remarks" type="textarea" :rows="3" />
              </el-form-item>
            </el-form>
          </div>
          <template #footer>
            <div class="dialog-footer">
              <el-button
                :title="
                  dialogType === 'add'
                    ? $t('user.account.cancelAdd')
                    : $t('user.account.cancelEdit')
                "
                @click="dialogVisible = false"
                >{{ $t('user.account.cancel') }}</el-button
              >
              <el-button
                :title="
                  dialogType === 'add' ? $t('user.account.saveAdd') : $t('user.account.saveEdit')
                "
                type="primary"
                @click="handleSubmit"
                :loading="submitLoading"
                >{{ $t('user.account.submit') }}</el-button
              >
            </div>
          </template>
        </ElDialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { ElDialog, FormInstance, ElTag } from 'element-plus'
  import type { FormRules } from 'element-plus'
  import { UserService } from '@/api/usersApi'
  import { UserRecord, UserListParams } from '@/api/model/userModel'
  import { RoleService } from '@/api/roleApi'
  import { Role } from '@/api/model/roleModel'
  import { ApiStatus } from '@/utils/http/status'
  import { formatDate } from '@/utils/date'
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { SearchChangeParams, SearchFormItem } from '@/types/search-form'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { useCheckedColumns } from '@/composables/useCheckedColumns'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter()

  const dialogType = ref('add')
  const dialogVisible = ref(false)
  const loading = ref(false)
  const formLoading = ref(false)
  const submitLoading = ref(false)

  // 表单项变更处理
  const handleFormChange = (params: SearchChangeParams): void => {
    console.log('表单项变更:', params)
  }

  // 导航到权限分配页面
  const navigateToPermissionPage = (row: UserRecord) => {
    router.push({
      path: '/system/menu/menuPermission',
      query: {
        userId: row.id.toString(),
        userName: row.nickName,
        type: 'user'
      }
    })
  }

  // 表单配置项
  const formItems: SearchFormItem[] = [
    {
      label: t('user.account.formItems.loginName'),
      prop: 'loginName',
      type: 'input',
      config: {
        placeholder: t('user.account.formItems.loginNamePlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: t('user.account.formItems.phone'),
      prop: 'tel',
      type: 'input',
      config: {
        placeholder: t('user.account.formItems.phonePlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: t('user.account.formItems.email'),
      prop: 'email',
      type: 'input',
      config: {
        placeholder: t('user.account.formItems.emailPlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: t('user.account.formItems.location'),
      prop: 'location',
      type: 'input',
      config: {
        placeholder: t('user.account.formItems.locationPlaceholder'),
        clearable: true
      },
      onChange: handleFormChange
    }
  ]

  // 表单数据
  const formData = reactive({
    id: 0,
    nickName: '',
    loginName: '',
    tel: '',
    email: '',
    icon: '',
    roleIds: [] as number[],
    remarks: ''
  })

  // 定义表单搜索初始值
  const initialSearchState = {
    nickName: '',
    loginName: '',
    tel: '',
    email: '',
    page: 1,
    limit: 10
  }

  // 响应式表单数据
  const formFilters = ref({ ...initialSearchState })

  // 重置表单
  const handleReset = () => {
    Object.assign(formFilters.value, { ...initialSearchState })
    // 重新加载数据
    loadUserData()
  }

  // 角色选项
  const roleOptions = ref<Role[]>([])

  // 列配置
  const columnOptions = [
    { label: t('user.account.table.selection'), type: 'selection' },
    { label: t('user.account.table.userName'), prop: 'nickName' },
    { label: t('user.account.table.phone'), prop: 'tel' },
    { label: t('user.account.table.loginName'), prop: 'loginName' },
    { label: t('user.account.table.role'), prop: 'roles' },
    { label: t('user.account.table.status'), prop: 'status' },
    { label: t('user.account.table.location'), prop: 'location' },
    { label: t('user.account.table.updateDate'), prop: 'updateDate' },
    { label: t('user.account.table.operation'), prop: 'operation' }
  ]

  // 1: 在线 2: 离线 3: 正常 4: 锁定 5: 注销
  const getTagType = (status: number) => {
    switch (status) {
      case 1: // 在线
        return 'success'
      case 2: // 离线
        return 'info'
      case 3: // 正常
        return 'primary'
      case 4: // 设备锁定
        return 'danger'
      case 5: // 账号锁定
        return 'danger'
      case 6: // 注销
        return 'warning'
      default:
        return 'info'
    }
  }

  // 构建标签文本
  const buildTagText = (status: number) => {
    switch (status) {
      case 1:
        return t('user.account.status.online')
      case 2:
        return t('user.account.status.offline')
      case 3:
        return t('user.account.status.normal')
      case 4:
        return t('user.account.status.deviceLocked')
      case 5:
        return t('user.account.status.accountLocked')
      case 6:
        return t('user.account.status.cancelled')
      default:
        return t('user.account.status.unknown')
    }
  }

  // 动态列配置
  const { columnChecks, columns } = useCheckedColumns(() => [
    { type: 'selection' }, // 勾选列
    // { type: 'expand', label: '展开', width: 80 }, // 展开列
    // { type: 'index', label: '序号', width: 80 }, // 序号列
    {
      prop: 'icon',
      label: t('user.account.table.userName'),
      formatter: (row: any) => {
        return h('div', { class: 'user', style: 'display: flex; align-items: center' }, [
          h('img', { class: 'avatar', src: formatAvatar(row.icon, row.id) }),
          h('div', {}, [
            h('p', { class: 'user-name' }, row.nickName),
            h('p', { class: 'email' }, row.email)
          ])
        ])
      }
    },
    { prop: 'tel', label: t('user.account.table.phone') },
    { prop: 'loginName', label: t('user.account.table.loginName') },
    {
      prop: 'roles',
      label: t('user.account.table.role'),
      formatter: (row: any) => {
        if (!row.roles || row.roles.length === 0) {
          return h('span', '-')
        }
        return h(
          'div',
          {},
          row.roles.map((role: any) => {
            return h(
              ElTag,
              {
                key: role.id,
                class: 'role-tag',
                style: {
                  marginRight: '5px',
                  marginBottom: '5px'
                }
              },
              () => role.name
            )
          })
        )
      }
    },
    {
      prop: 'status',
      label: t('user.account.table.status'),
      formatter: (row) => {
        return h(ElTag, { type: getTagType(row.status) }, () => buildTagText(row.status))
      }
    },
    { prop: 'location', label: t('user.account.table.location') },
    {
      prop: 'updateDate',
      label: t('user.account.table.updateDate'),
      sortable: true,
      formatter: (row: any) => {
        return formatDate(row.updateDate)
      }
    },
    {
      prop: 'operation',
      label: t('user.account.table.operation'),
      width: 180,
      // fixed: 'right', // 固定在右侧
      // disabled: true,
      formatter: (row: any) => {
        return h('div', [
          h(ArtButtonTable, {
            title: t('user.account.operation.edit'),
            type: 'edit',
            auth: 'user_edit',
            onClick: () => showDialog('edit', row)
          }),
          h(ArtButtonTable, {
            title: t('user.account.operation.delete'),
            type: 'delete',
            auth: 'user_delete',
            onClick: () => deleteUser(row.id)
          }),
          h(ArtButtonTable, {
            title: t('user.account.operation.assign'),
            type: 'more',
            auth: 'user_assign',
            onClick: () => navigateToPermissionPage(row)
          })
        ])
      }
    }
  ])

  // 加载角色选项
  const loadRoleOptions = async () => {
    try {
      const response = await RoleService.getUserAllRoles()
      if (response.code === ApiStatus.success) {
        roleOptions.value = response.data
      } else {
        ElMessage.error(response.message || t('user.account.dialog.operationFailed'))
      }
    } catch (error) {
      console.error('获取角色列表失败:', error)
      ElMessage.error(t('user.account.dialog.operationFailed'))
    }
  }

  // 表格数据
  const tableData = ref<UserRecord[]>([])

  // 分页信息
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
  })

  // 加载用户数据
  const loadUserData = async () => {
    loading.value = true
    try {
      // 直接使用 formFilters，因为它现在包含了所有参数
      const response = await UserService.getUserList(formFilters.value as UserListParams)
      if (response.success) {
        tableData.value = response.data.records
        // 更新分页组件状态 (pagination 对象)
        pagination.total = response.data.total
        pagination.current = response.data.current
        pagination.size = response.data.size
        pagination.pages = response.data.pages
      } else {
        ElMessage.error(response.message || t('user.account.dialog.operationFailed'))
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
      ElMessage.error(t('user.account.dialog.operationFailed'))
    } finally {
      loading.value = false
    }
  }

  // 加载用户详情
  const loadUserDetail = async (userId: number) => {
    if (!userId) {
      console.error('无效的用户ID:', userId)
      return
    }

    formLoading.value = true
    try {
      const response = await UserService.getUserDetail(userId)

      if (response.code === ApiStatus.success && response.data) {
        const userData = response.data

        // 清空之前的数据
        resetFormData()

        // 设置表单数据
        formData.id = userData.id || 0
        formData.nickName = userData.nickName || ''
        formData.loginName = userData.loginName || ''
        formData.tel = userData.tel || ''
        formData.email = userData.email || ''
        formData.icon = userData.icon || ''
        formData.remarks = userData.remarks || ''

        // 设置角色ID列表
        if (userData.roles && Array.isArray(userData.roles)) {
          formData.roleIds = userData.roles.map((role) => role.id)
        } else {
          formData.roleIds = []
        }
      } else {
        ElMessage.error(response.message || t('user.account.dialog.operationFailed'))
      }
    } catch (error) {
      console.error('获取用户详情失败:', error)
      ElMessage.error(t('user.account.dialog.operationFailed'))
    } finally {
      formLoading.value = false
    }
  }

  // 处理头像URL
  const formatAvatar = (icon: string, userId: number) => {
    if (!icon) {
      return `https://api.dicebear.com/9.x/adventurer/svg?seed=${userId}`
    }

    if (icon.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + icon
    }

    return icon
  }

  const showDialog = async (type: string, row?: UserRecord) => {
    dialogVisible.value = true
    dialogType.value = type

    // 重置表单
    resetFormData()

    // 加载角色选项
    await loadRoleOptions()

    if (type === 'edit' && row) {
      // 编辑模式：加载用户详情
      await loadUserDetail(row.id)
    }
  }

  // 重置表单数据
  const resetFormData = () => {
    formData.id = 0
    formData.nickName = ''
    formData.loginName = ''
    formData.tel = ''
    formData.email = ''
    formData.icon = ''
    formData.roleIds = []
    formData.remarks = ''
  }

  const deleteUser = (userId: number) => {
    ElMessageBox.confirm(
      t('user.account.dialog.confirmDelete'),
      t('user.account.dialog.confirmDeleteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'error'
      }
    )
      .then(async () => {
        try {
          const response = await UserService.deleteUser(userId)
          if (response.success) {
            ElMessage.success(t('user.account.dialog.deleteSuccess'))
            loadUserData() // 重新加载数据
          } else {
            ElMessage.error(response.message || t('user.account.dialog.deleteFailed'))
          }
        } catch (error) {
          console.error('注销用户失败:', error)
          ElMessage.error(t('user.account.dialog.deleteError'))
        }
      })
      .catch(() => {
        // 用户取消操作，无需提示或处理，捕获 rejection 防止控制台报错
        ElMessage.info(t('user.account.dialog.cancelDelete')) // 可以选择性地添加提示
      })
  }

  const search = () => {
    formFilters.value.page = 1 // 搜索时重置为第一页
    loadUserData()
  }

  // 处理分页变化
  const handlePageChange = (page: number) => {
    formFilters.value.page = page // 更新 formFilters 中的页码
    loadUserData()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    formFilters.value.limit = size // 更新 formFilters 中的每页数量
    formFilters.value.page = 1 // 切换每页数量时重置为第一页
    loadUserData()
  }

  const rules = reactive<FormRules>({
    nickName: [
      { required: true, message: t('user.account.rules.nickName.required'), trigger: 'blur' },
      { min: 2, max: 20, message: t('user.account.rules.nickName.length'), trigger: 'blur' }
    ],
    loginName: [
      { required: true, message: t('user.account.rules.loginName.required'), trigger: 'blur' },
      { min: 3, max: 10, message: t('user.account.rules.loginName.length'), trigger: 'blur' },
      {
        pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
        message: t('user.account.rules.loginName.pattern'),
        trigger: 'blur'
      }
    ],
    tel: [
      { pattern: /^1[3-9]\d{9}$/, message: t('user.account.rules.tel.pattern'), trigger: 'blur' }
    ],
    email: [{ type: 'email', message: t('user.account.rules.email.pattern'), trigger: 'blur' }]
  })

  const formRef = ref<FormInstance>()

  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true

        try {
          let response

          if (dialogType.value === 'add') {
            // 构建新增用户的参数
            const addParams = {
              loginName: formData.loginName,
              nickName: formData.nickName,
              tel: formData.tel,
              email: formData.email,
              icon: formData.icon,
              remarks: formData.remarks,
              roles: formData.roleIds.map((id) => ({ id }))
            }
            response = await UserService.addUser(addParams)
          } else {
            // 构建编辑用户的参数
            const editParams = {
              id: formData.id,
              nickName: formData.nickName,
              tel: formData.tel,
              email: formData.email,
              icon: formData.icon,
              remarks: formData.remarks,
              roleSet: formData.roleIds.map((id) => ({ id }))
            }
            response = await UserService.editUser(editParams)
          }

          if (response.code === ApiStatus.success) {
            ElMessage.success(
              dialogType.value === 'add'
                ? t('user.account.dialog.addSuccess')
                : t('user.account.dialog.editSuccess')
            )
            dialogVisible.value = false
            loadUserData() // 操作完成后重新加载数据
          } else {
            ElMessage.error(response.message || t('user.account.dialog.operationFailed'))
          }
        } catch (error) {
          console.error('保存用户信息失败:', error)
          ElMessage.error(t('user.account.dialog.saveError'))
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  onMounted(() => {
    loadUserData()
  })
</script>

<style lang="scss" scoped>
  .account-page {
    // 添加表格容器样式
    .table-container {
      flex: 1;
      min-height: 0; // 重要：允许容器收缩
      padding: 16px; // 根据需求调整内边距
    }

    .form-tip {
      margin-top: 5px;
      font-size: 12px;
      line-height: 1.4;
      color: #909399;
    }

    .info-alert {
      display: flex;
      align-items: flex-start;
      padding: 10px 15px;
      margin-bottom: 20px;
      background-color: #e6f7ff;
      border: 1px solid #91d5ff;
      border-radius: 4px;

      .info-icon {
        display: flex;
        align-items: center;
        margin-right: 8px;

        i {
          font-size: 20px;
          color: #1890ff;
        }
      }

      .info-content {
        flex: 1;
        font-size: 14px;
        line-height: 1.5;
        color: #333;

        p {
          margin: 0;
        }
      }
    }

    :deep(.user) {
      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 6px;
      }

      > div {
        margin-left: 10px;

        .user-name {
          font-weight: 500;
          color: var(--art-text-gray-800);
        }
      }
    }

    // 自定义状态标签样式
    :deep(.el-tag--success) {
      color: #67c23a;
      background-color: #f0f9eb;
      border-color: #e1f3d8;
    }

    :deep(.el-tag--info) {
      color: #909399;
      background-color: #f4f4f5;
      border-color: #e9e9eb;
    }

    :deep(.el-tag--primary) {
      color: #409eff;
      background-color: #ecf5ff;
      border-color: #d9ecff;
    }

    :deep(.el-tag--danger) {
      color: #f56c6c;
      background-color: #fef0f0;
      border-color: #fde2e2;
    }

    :deep(.el-tag--warning) {
      color: #e6a23c;
      background-color: #fdf6ec;
      border-color: #faecd8;
    }
  }
</style>
