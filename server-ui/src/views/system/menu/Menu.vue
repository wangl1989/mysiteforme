<template>
  <div class="page-content">
    <el-row :gutter="20" style="margin-left: 15px">
      <el-button
        v-auth="'root_menu_add'"
        type="primary"
        @click="showModel('menu', null, true)"
        v-ripple
        >新增主菜单</el-button
      >
    </el-row>

    <art-table :data="tableData">
      <template #default>
        <el-table-column label="菜单名称">
          <template #default="scope">
            {{ formatMenuTitle(scope.row.meta?.title) }}
          </template>
        </el-table-column>
        <el-table-column label="路由" prop="path" />

        <el-table-column prop="meta.authList" label="可操作权限">
          <template #default="scope">
            <!-- 检查是否有权限列表 -->
            <div
              v-if="scope.row.meta?.authList && scope.row.meta.authList.length > 0"
              v-auth="'permission_manage'"
            >
              <!-- 权限按钮 -->
              <el-button
                title="展示菜单权限"
                type="primary"
                link
                @click="toggleAuthList(scope.$index)"
              >
                共 {{ scope.row.meta.authList.length }} 个权限
              </el-button>

              <!-- 自定义的权限列表弹出框，通过状态控制 -->
              <ElDialog
                v-model="authListVisible[scope.$index]"
                :title="`权限列表【所属菜单：${formatMenuTitle(scope.row.meta?.title)}】`"
                :width="1200"
                :append-to-body="true"
                :destroy-on-close="false"
                align-center
              >
                <permission-list
                  :permissions="scope.row.meta.authList"
                  @edit="(item) => showModel('button', item)"
                  @delete="deleteAuth"
                />
              </ElDialog>
            </div>
            <!-- 如果没有权限则显示 '-' -->
            <span v-else></span>
          </template>
        </el-table-column>

        <el-table-column prop="sort" label="排序值" />

        <el-table-column label="编辑时间" prop="updateDate">
          <template #default="scope">{{ formatDate(scope.row.updateDate) }}</template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <ArtButtonTable
              title="新增菜单或权限"
              auth="sub_menu_add"
              type="add"
              @click="showModel('menu', null, false, scope.row)"
            />
            <ArtButtonTable
              title="编辑菜单"
              auth="menu_edit"
              type="edit"
              @click="showDialog('edit', scope.row)"
            />
            <ArtButtonTable
              title="删除菜单或权限"
              auth="menu_delete"
              type="delete"
              @click="deleteMenu(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>

    <ElDialog :title="dialogTitle" v-model="dialogVisible" width="700px" align-center>
      <el-form
        ref="formRef"
        :model="activeForm"
        :rules="activeRules"
        label-width="85px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="菜单类型">
          <el-radio-group
            v-model="labelPosition"
            :disabled="disableMenuType"
            @change="handleFormTypeChange"
          >
            <el-radio-button value="menu" label="menu">菜单</el-radio-button>
            <el-radio-button value="button" label="button">权限</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <template v-if="labelPosition === 'menu'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="菜单标题" prop="title">
                <el-input v-model="menuForm.title" placeholder="菜单标题"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="路由地址" prop="path">
                <el-input v-model="menuForm.path" placeholder="路由地址"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="菜单名称" prop="name">
                <el-input v-model="menuForm.name" placeholder="菜单名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="图标" prop="icon">
                <ArtIconSelector
                  v-model="menuForm.icon"
                  :iconType="iconType"
                  :defaultIcon="menuForm.icon"
                  :iconColor="menuForm.color"
                  width="230px"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="组件路径" prop="component">
                <el-input
                  v-model="menuForm.component"
                  placeholder="例如: /system/user/index"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="颜色" prop="color">
                <el-color-picker
                  v-model="menuForm.color"
                  :predefine="predefineColors"
                  class="full-width-color-picker"
                ></el-color-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="外部链接" prop="link">
                <el-input
                  v-model="menuForm.link"
                  placeholder="外部链接/内嵌地址(https://www.baidu.com)"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="文本徽章" prop="showTextBadge">
                <el-input v-model="menuForm.showTextBadge" placeholder="留空则不显示"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="菜单排序" prop="sort" style="width: 100%">
                <el-input-number
                  v-model="menuForm.sort"
                  style="width: 100%"
                  @change="handleChange"
                  :min="1"
                  controls-position="right"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="5">
              <el-tooltip content="菜单是否在侧边栏隐藏" placement="top">
                <el-form-item label="隐藏菜单" prop="isHidden">
                  <el-switch v-model="menuForm.isHidden"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="5">
              <el-tooltip content="页面是否使用 keep-alive 缓存" placement="top">
                <el-form-item label="页面缓存" prop="keepAlive">
                  <el-switch v-model="menuForm.keepAlive"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="5">
              <el-tooltip content="是否为内嵌外部链接 (iframe)" placement="top">
                <el-form-item label="是否内嵌" prop="isIframe">
                  <el-switch v-model="menuForm.isIframe"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="5">
              <el-tooltip content="是否在标签页栏隐藏" placement="top">
                <el-form-item label="隐藏Tab" prop="isHideTab">
                  <el-switch v-model="menuForm.isHideTab"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="5">
              <el-tooltip content="是否在顶部和侧边栏显示徽标" placement="top">
                <el-form-item label="显示徽标" prop="showBadge">
                  <el-switch v-model="menuForm.showBadge"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
            <el-col :span="5">
              <el-tooltip content="当前路由是否在主布局内显示 (通常为 false)" placement="top">
                <el-form-item label="主布局内" prop="isInMainContainer">
                  <el-switch v-model="menuForm.isInMainContainer"></el-switch>
                </el-form-item>
              </el-tooltip>
            </el-col>
          </el-row>

          <el-form-item label="备注" prop="remarks">
            <el-input
              type="textarea"
              v-model="menuForm.remarks"
              placeholder="请输入备注信息"
              :rows="5"
            ></el-input>
          </el-form-item>
        </template>

        <template v-if="labelPosition === 'button'">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="权限类型">
                <el-radio-group v-model="permissionForm.permissionType" :disabled="isEdit">
                  <el-radio-button :value="1" label="1">路由</el-radio-button>
                  <el-radio-button :value="2" label="2">按钮</el-radio-button>
                  <el-radio-button :value="3" label="3">API</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="权限名称" prop="permissionName">
                <el-input v-model="permissionForm.permissionName" placeholder="权限名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限标识" prop="permissionCode">
                <el-input v-model="permissionForm.permissionCode" placeholder="权限标识"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="图标" prop="icon">
                <ArtIconSelector
                  v-model="permissionForm.icon"
                  :iconType="iconType"
                  :defaultIcon="permissionForm.icon"
                  :iconColor="permissionForm.color"
                  width="230px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="颜色" prop="color">
                <el-color-picker
                  v-model="permissionForm.color"
                  :predefine="predefineColors"
                  class="full-width-color-picker"
                ></el-color-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="权限排序" prop="sort" style="width: 100%">
                <el-input-number
                  v-model="permissionForm.sort"
                  style="width: 100%"
                  @change="handleChange"
                  :min="1"
                  controls-position="right"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 路由权限特有表单 -->
          <template v-if="permissionForm.permissionType === 1">
            <el-divider>路由权限信息</el-divider>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="页面地址" prop="pagePermission.pageUrl">
                  <el-input
                    v-model="permissionForm.pagePermission.pageUrl"
                    placeholder="页面请求的地址"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="pagePermission.remarks">
              <el-input
                type="textarea"
                v-model="permissionForm.remarks"
                placeholder="请输入备注信息"
                :rows="3"
              ></el-input>
            </el-form-item>
          </template>

          <!-- 按钮权限特有表单 -->
          <template v-if="permissionForm.permissionType === 2">
            <el-divider>按钮权限信息</el-divider>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="按钮名称" prop="button.buttonName">
                  <el-input
                    v-model="permissionForm.button.buttonName"
                    placeholder="按钮的功能名称"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="按钮标识" prop="button.buttonKey">
                  <el-input
                    v-model="permissionForm.button.buttonKey"
                    placeholder="按钮标识，如：user_add"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="button.remarks">
              <el-input
                type="textarea"
                v-model="permissionForm.remarks"
                placeholder="请输入备注信息"
                :rows="3"
              ></el-input>
            </el-form-item>
          </template>

          <!-- API权限特有表单 -->
          <template v-if="permissionForm.permissionType === 3">
            <el-divider>API权限信息</el-divider>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="API地址" prop="api.apiUrl">
                  <el-input
                    v-model="permissionForm.api.apiUrl"
                    placeholder="API的请求地址"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="请求方法" prop="api.httpMethod">
                  <el-radio-group v-model="permissionForm.api.httpMethod">
                    <el-radio-button label="GET" value="GET" />
                    <el-radio-button label="POST" value="POST" />
                    <el-radio-button label="PUT" value="PUT" />
                    <el-radio-button label="DELETE" value="DELETE" />
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="是否公共" prop="api.common">
                  <el-switch v-model="permissionForm.api.common"></el-switch>
                  <span class="form-tip">公共API无需权限即可访问</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="api.remarks">
              <el-input
                type="textarea"
                v-model="permissionForm.remarks"
                placeholder="请输入备注信息"
                :rows="3"
              ></el-input>
            </el-form-item>
          </template>
        </template>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm()"> 确 定 </el-button>
        </span>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { useMenuStore } from '@/store/modules/menu'
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { IconTypeEnum } from '@/enums/appEnum'
  import { formatMenuTitle, processMenuDetail } from '@/utils/menu'
  import { formatDate } from '@/utils/date'
  import { reactive, ref, computed, nextTick, onMounted } from 'vue'
  import PermissionList from '@/components/Permission/PermissionList.vue'
  import { menuService, MenuApiService } from '@/api/menuApi'
  import type { AddMenuParams, EditMenuParams } from '@/api/model/menuModel'
  import '@/utils/browserPatch'
  import {
    PermissionTypeEnum,
    AddPermissionParams,
    EditPermissionParams,
    ApiPermission,
    PagePermission,
    ButtonPermission
  } from '@/api/model/menuModel'

  // 菜单列表数据
  const menuDataList = ref<any[]>([])

  // 获取菜单元数据
  const fetchMenuData = async () => {
    try {
      const response = await MenuApiService.getCurrentMenuDetail()
      if (response.success && response.data) {
        // 处理菜单数据为需要的格式
        if (Array.isArray(response.data)) {
          menuDataList.value = response.data.map((menu) => processMenuDetail(menu))
        } else {
          // 如果返回单个对象，也包装成数组
          menuDataList.value = [processMenuDetail(response.data)]
        }
      } else {
        ElMessage.error(response.message || '获取菜单数据失败')
      }
    } catch (error) {
      console.error('获取菜单数据失败:', error)
      ElMessage.error('获取菜单数据失败')
    }
  }

  // 在组件挂载时获取菜单数据
  onMounted(() => {
    fetchMenuData()
  })

  // 用于跟踪每个菜单的权限列表是否显示
  const authListVisible = reactive<Record<number, boolean>>({})

  // 切换权限列表的显示/隐藏
  const toggleAuthList = (index: number) => {
    authListVisible[index] = !authListVisible[index]
  }

  const dialogVisible = ref(false)

  // 菜单表单数据
  const menuForm = reactive({
    title: '',
    path: '',
    name: '',
    icon: '',
    color: '',
    sort: 1,
    keepAlive: true,
    isHidden: false,
    parentId: null,
    link: '',
    isIframe: false,
    remarks: '',
    component: '',
    showBadge: false,
    showTextBadge: '',
    isHideTab: false,
    isInMainContainer: false
  })

  // 权限表单数据
  const permissionForm = reactive({
    permissionName: '',
    permissionCode: '',
    permissionType: PermissionTypeEnum.ROUTE,
    menuId: 0,
    icon: '',
    color: '',
    sort: 1,
    remarks: '',
    api: {
      apiUrl: '',
      httpMethod: 'GET',
      common: false,
      remarks: ''
    } as ApiPermission,
    pagePermission: {
      pageUrl: '',
      remarks: ''
    } as PagePermission,
    button: {
      buttonName: '',
      buttonKey: '',
      remarks: ''
    } as ButtonPermission
  })

  // 预定义颜色
  const predefineColors = ref([
    '#409EFF', // 主题蓝
    '#67C23A', // 成功绿
    '#E6A23C', // 警告黄
    '#F56C6C', // 错误红
    '#909399', // 信息灰
    '#9370DB', // 紫罗兰
    '#FFC0CB', // 粉红
    '#90EE90', // 淡绿
    '#B0E0E6', // 粉蓝
    '#FF69B4', // 热粉
    '#778899', // 亮蓝灰
    '#00CED1', // 深天蓝
    '#FFDAB9' // 桃红
  ])

  const iconType = ref(IconTypeEnum.UNICODE)

  const labelPosition = ref('menu')

  // 菜单表单验证规则
  const menuRules = reactive<FormRules>({
    title: [
      { required: true, message: '请输入菜单标题', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
    name: [
      { required: true, message: '请输入菜单名称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ]
  })

  // 权限表单验证规则
  const permissionRules = reactive<FormRules>({
    permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
    permissionCode: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
    'api.apiUrl': [{ required: true, message: '请输入API地址', trigger: 'blur' }],
    'api.httpMethod': [{ required: true, message: '请选择请求方法', trigger: 'change' }],
    'pagePermission.pageUrl': [{ required: true, message: '请输入页面地址', trigger: 'blur' }],
    'button.buttonName': [{ required: true, message: '请输入按钮名称', trigger: 'blur' }],
    'button.buttonKey': [{ required: true, message: '请输入按钮标识', trigger: 'blur' }]
  })

  // 动态计算当前活动的表单对象和规则
  const activeForm = computed(() => {
    return labelPosition.value === 'menu' ? menuForm : permissionForm
  })

  const activeRules = computed(() => {
    return labelPosition.value === 'menu' ? menuRules : permissionRules
  })

  const tableData = computed(() => menuDataList.value)

  const isEdit = ref(false)
  const formRef = ref<FormInstance>()
  const dialogTitle = computed(() => {
    if (isEdit.value) {
      if (labelPosition.value === 'menu') {
        return `编辑菜单【${currentEditRow.value.name}】`
      } else {
        // 编辑权限时显示权限名称
        return `编辑权限【${currentEditRow.value.permissionName || ''}】`
      }
    } else {
      if (currentEditRow.value) {
        if (labelPosition.value === 'menu') {
          return `新建子菜单【父级菜单：${currentEditRow.value.name}】`
        } else {
          return `新建权限【所属菜单：${currentEditRow.value.name || currentEditRow.value.meta?.title || ''}】`
        }
      } else {
        if (labelPosition.value === 'menu') {
          return `新建菜单`
        } else {
          return `新建权限`
        }
      }
    }
  })

  // 用于记录当前编辑的行数据
  const currentEditRow = ref<any>(null)
  // 用于区分是新增主菜单还是操作栏新增
  const isMainMenuAdd = ref(false)

  const showDialog = (type: string, row: any) => {
    showModel('menu', row, false)
  }

  const handleChange = () => {}

  const submitForm = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          const menuStore = useMenuStore()

          if (labelPosition.value === 'menu') {
            // 菜单表单处理逻辑
            const params: AddMenuParams | EditMenuParams = {
              title: menuForm.title,
              path: menuForm.path,
              name: menuForm.name,
              parentId: menuForm.parentId || undefined,
              icon: menuForm.icon,
              color: menuForm.color,
              sort: menuForm.sort,
              isHide: menuForm.isHidden,
              link: menuForm.link,
              isIframe: menuForm.isIframe,
              keepAlive: menuForm.keepAlive,
              remarks: menuForm.remarks,
              component: menuForm.component,
              showBadge: menuForm.showBadge,
              showTextBadge: menuForm.showTextBadge,
              isHideTab: menuForm.isHideTab,
              isInMainContainer: menuForm.isInMainContainer
            }

            if (isEdit.value && currentEditRow.value) {
              // 编辑菜单
              const editParams: EditMenuParams = {
                ...params,
                id: currentEditRow.value.id
              }
              const res = await MenuApiService.editMenu(editParams)
              if (!res.success) {
                throw new Error(res.message || '编辑菜单失败')
              }
            } else {
              // 新增菜单
              const addParams: AddMenuParams = { ...params }
              if (!isMainMenuAdd.value && currentEditRow.value) {
                addParams.parentId = currentEditRow.value.id
              }

              const res = await MenuApiService.addMenu(addParams)
              if (!res.success) {
                throw new Error(res.message || '新增菜单失败')
              }
            }
          } else {
            // 权限表单处理逻辑
            // 只保留当前选中的权限类型对应的数据
            let permissionData: AddPermissionParams | EditPermissionParams = {
              permissionName: permissionForm.permissionName,
              permissionCode: permissionForm.permissionCode,
              permissionType: permissionForm.permissionType,
              menuId: permissionForm.menuId,
              icon: permissionForm.icon,
              color: permissionForm.color,
              sort: permissionForm.sort,
              remarks: permissionForm.remarks
            }

            // 根据权限类型，添加对应的特定数据
            switch (permissionForm.permissionType) {
              case PermissionTypeEnum.ROUTE:
                permissionData.pagePermission = { ...permissionForm.pagePermission }
                break
              case PermissionTypeEnum.BUTTON:
                permissionData.button = { ...permissionForm.button }
                break
              case PermissionTypeEnum.API:
                permissionData.api = { ...permissionForm.api }
                break
            }

            if (isEdit.value && currentEditRow.value) {
              // 编辑权限
              const editPermissionParams: EditPermissionParams = {
                ...permissionData,
                id: currentEditRow.value.id
              }
              const res = await MenuApiService.editPermission(editPermissionParams)
              if (!res.success) {
                throw new Error(res.message || '编辑权限失败')
              }
            } else {
              // 新增权限
              const res = await MenuApiService.addPermission(permissionData)
              if (!res.success) {
                throw new Error(res.message || '新增权限失败')
              }
            }
          }

          ElMessage.success(`${isEdit.value ? '编辑' : '新增'}成功`)
          dialogVisible.value = false
          // 刷新列表数据
          const { menuList: newMenuList, closeLoading } = await menuService.getMenuList(0) // 立即获取最新数据
          menuStore.setMenuList(newMenuList) // 更新 store
          fetchMenuData() // 刷新菜单列表
          closeLoading() // 关闭加载动画
        } catch (error: any) {
          ElMessage.error(error.message || `${isEdit.value ? '编辑' : '新增'}失败`)
        }
      }
    })
  }

  const showModel = (type: string, row?: any, mainMenuAdd: boolean = false, parentRow?: any) => {
    dialogVisible.value = true
    labelPosition.value = type
    isEdit.value = !!row
    isMainMenuAdd.value = mainMenuAdd

    // 修改逻辑：优先保存parentRow（如果存在），否则保存row
    // 这样确保即使初始type是menu，也能保存菜单行数据
    currentEditRow.value = parentRow || row

    resetForm()

    if (isEdit.value) {
      nextTick(() => {
        try {
          if (type === 'menu') {
            // 编辑菜单数据填充
            menuForm.title = formatMenuTitle(currentEditRow.value.meta?.title)
            menuForm.path = currentEditRow.value.meta?.originalPath || currentEditRow.value.path
            menuForm.name = currentEditRow.value.name
            menuForm.parentId = currentEditRow.value.parentId
            menuForm.icon = currentEditRow.value.meta?.icon
            menuForm.color = currentEditRow.value.meta?.color || null
            menuForm.sort = currentEditRow.value.sort || 1
            menuForm.keepAlive = currentEditRow.value.meta?.keepAlive ?? true
            menuForm.isHidden =
              currentEditRow.value.meta?.isHide !== undefined
                ? currentEditRow.value.meta.isHide
                : false
            menuForm.link = currentEditRow.value.meta?.link || ''
            menuForm.isIframe = currentEditRow.value.meta?.isIframe || false
            menuForm.remarks = currentEditRow.value.remarks || ''
            menuForm.component = currentEditRow.value.component || ''
            menuForm.showBadge = currentEditRow.value.meta?.showBadge || false
            menuForm.showTextBadge = currentEditRow.value.meta?.showTextBadge || ''
            menuForm.isHideTab = currentEditRow.value.meta?.isHideTab || false
            menuForm.isInMainContainer = currentEditRow.value.meta?.isInMainContainer ?? false
          } else if (type === 'button' && row) {
            // 确保在表单加载前设置为权限表单类型
            labelPosition.value = 'button'

            // 编辑权限数据填充
            permissionForm.permissionName = row.permissionName || ''
            permissionForm.permissionCode = row.permissionCode || ''
            permissionForm.permissionType = Number(row.permissionType) || PermissionTypeEnum.ROUTE
            permissionForm.menuId = row.menuId || 0
            permissionForm.icon = row.icon || ''
            permissionForm.color = row.color || null
            permissionForm.sort = row.sort || 1
            permissionForm.remarks = row.remarks
            permissionForm.menuId = row.menu?.id

            // 权限表单类型特定数据需要重置
            permissionForm.api = {
              apiUrl: '',
              httpMethod: 'GET',
              common: false,
              remarks: ''
            }
            permissionForm.pagePermission = {
              pageUrl: '',
              remarks: ''
            }
            permissionForm.button = {
              buttonName: '',
              buttonKey: '',
              remarks: ''
            }

            // 根据权限类型设置特定字段
            if (permissionForm.permissionType === PermissionTypeEnum.ROUTE && row.page) {
              permissionForm.pagePermission = { ...row.page }
            } else if (permissionForm.permissionType === PermissionTypeEnum.BUTTON && row.button) {
              permissionForm.button = { ...row.button }
            } else if (permissionForm.permissionType === PermissionTypeEnum.API && row.api) {
              permissionForm.api = { ...row.api }
            }

            // 如果权限有嵌套的api对象，也要考虑这种情况
            if (permissionForm.permissionType === PermissionTypeEnum.API) {
              // 处理可能存在的不同数据结构
              if (row.api) {
                permissionForm.api = { ...row.api }
              } else if (row.apiUrl) {
                // 处理扁平结构
                permissionForm.api.apiUrl = row.apiUrl
                permissionForm.api.httpMethod = row.httpMethod || 'GET'
                permissionForm.api.common = row.common || false
                permissionForm.api.remarks = row.remarks || ''
              }
            }
          }
        } catch (e) {
          console.error('Error during form data assignment in nextTick:', e)
        }
      })
    } else {
      // 新增权限时，确保设置正确的菜单ID
      if (type === 'button' && currentEditRow.value) {
        // 设置当前选中菜单的ID
        permissionForm.menuId = currentEditRow.value.id || 0
      } else if (parentRow && currentEditRow.value) {
        // 如果是从菜单行添加，预先设置menuId，以备后续切换到权限表单
        permissionForm.menuId = currentEditRow.value.id || 0
      }
    }
  }

  const resetForm = () => {
    formRef.value?.resetFields()
    // isMainMenuAdd.value = false

    // 保存当前的 menuId，避免被重置
    const currentMenuId = permissionForm.menuId

    // 重置菜单表单
    Object.assign(menuForm, {
      title: '',
      path: '',
      name: '',
      icon: '',
      color: null,
      sort: 1,
      keepAlive: true,
      isHidden: false,
      link: '',
      isIframe: false,
      remarks: '',
      component: '',
      showBadge: false,
      showTextBadge: '',
      isHideTab: false,
      isInMainContainer: false
    })

    // 重置权限表单，但保留 menuId
    Object.assign(permissionForm, {
      permissionName: '',
      permissionCode: '',
      permissionType: PermissionTypeEnum.ROUTE,
      menuId: currentMenuId, // 保留之前的 menuId
      icon: '',
      color: null,
      sort: 1,
      remarks: '',
      api: {
        apiUrl: '',
        httpMethod: 'GET',
        common: false,
        remarks: ''
      },
      pagePermission: {
        pageUrl: '',
        remarks: ''
      },
      button: {
        buttonName: '',
        buttonKey: '',
        remarks: ''
      }
    })
  }

  const deleteMenu = async (row: any) => {
    try {
      await ElMessageBox.confirm(`确定要删除该菜单吗？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      // 调用删除菜单API
      const res = await MenuApiService.deleteMenu(row.id)
      if (!res.success) {
        throw new Error(res.message || '删除菜单失败')
      }

      ElMessage.success('删除成功')

      // 刷新菜单列表
      const { menuList: newMenuList, closeLoading } = await menuService.getMenuList(0)
      useMenuStore().setMenuList(newMenuList)
      fetchMenuData() // 刷新菜单列表
      closeLoading()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '删除失败')
      }
    }
  }

  const deleteAuth = async (row: any) => {
    try {
      await ElMessageBox.confirm('确定要删除该权限吗？删除后无法恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      // 调用删除权限API
      const res = await MenuApiService.deletePermission(row.id)
      if (!res.success) {
        throw new Error(res.message || '删除权限失败')
      }

      ElMessage.success('删除成功')

      // 刷新菜单列表
      const { menuList: newMenuList, closeLoading } = await menuService.getMenuList(0)
      useMenuStore().setMenuList(newMenuList)
      fetchMenuData() // 刷新菜单列表
      closeLoading()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '删除失败')
      }
    }
  }

  // 修改计算属性，增加锁定控制参数
  const disableMenuType = computed(() => {
    if (isEdit.value && labelPosition.value === 'button') return true
    if (isEdit.value && labelPosition.value === 'menu') return true
    if (!isEdit.value && labelPosition.value === 'menu' && isMainMenuAdd.value) return true
    return false
  })

  // 切换表单类型处理方法
  const handleFormTypeChange = () => {
    // 清除表单验证结果，防止切换时显示错误
    if (formRef.value) {
      nextTick(() => {
        formRef.value?.clearValidate()
      })
    }

    // 如果从菜单切换到权限，且有当前选中的行数据，则设置menuId
    if (labelPosition.value === 'button' && currentEditRow.value && !isEdit.value) {
      permissionForm.menuId = currentEditRow.value.id || 0
    }
  }
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

    :deep(.small-btn) {
      height: 30px !important;
      padding: 0 10px !important;
      font-size: 12px !important;
    }

    .full-width-color-picker {
      width: 100%;

      :deep(.el-color-picker__trigger) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        height: 32px;
        padding: 0 12px;
        border-radius: 4px;
      }

      :deep(.el-color-picker__color) {
        border-radius: 3px;
      }
    }

    .form-tip {
      margin-left: 10px;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }
</style>
