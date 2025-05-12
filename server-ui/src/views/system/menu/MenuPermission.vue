<template>
  <div class="menu-permission-wrapper">
    <div class="menu-permission-container">
      <el-row :gutter="20" class="full-height-row">
        <!-- 左侧菜单树 -->
        <el-col :span="8" class="full-height-col">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <span>菜单树</span>
                <div class="card-header-actions">
                  <!-- 用户权限保存按钮 -->
                  <span v-if="permissionType.toLowerCase() === 'user'">
                    <el-button
                      title="分配用户额外权限"
                      type="primary"
                      size="small"
                      @click="savePermissions"
                    >
                      保存用户权限
                    </el-button>
                  </span>

                  <!-- 角色权限保存按钮 -->
                  <span v-if="permissionType.toLowerCase() === 'role' || !permissionType">
                    <el-button
                      title="分配角色菜单和权限"
                      type="primary"
                      size="small"
                      @click="savePermissions"
                    >
                      保存角色权限
                    </el-button>
                  </span>
                </div>
              </div>
            </template>
            <div class="tree-container" style="overflow: hidden">
              <!-- 自定义菜单树，只显示菜单，不显示权限节点 -->
              <el-tree
                ref="menuTreeRef"
                :data="menuTreeData"
                node-key="id"
                :props="defaultProps"
                :default-expanded-keys="defaultExpandedKeys"
                :show-checkbox="showCheckbox"
                :default-checked-keys="checkedMenuIds"
                :check-strictly="true"
                @node-click="handleNodeClick"
                @node-expand="handleNodeExpand"
                @node-collapse="handleNodeCollapse"
                highlight-current
              >
                <template #default="{ node, data }">
                  <div class="custom-tree-node">
                    <span class="node-icon">
                      <i
                        class="iconfont-sys"
                        v-html="
                          data.meta?.icon ? data.meta.icon : node.expanded ? '&#xe8d6;' : '&#xe603;'
                        "
                        :style="{ color: data.meta?.color }"
                      ></i>
                    </span>
                    <span class="node-title">{{ formatTitle(data) }}</span>
                  </div>
                </template>
              </el-tree>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧权限详情 -->
        <el-col :span="16" class="full-height-col">
          <el-card class="box-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>权限详情</span>
                <span v-if="selectedMenu" class="selected-node-info">
                  当前选中菜单: {{ formatTitle(selectedMenu) }}
                </span>
                <span v-if="permissionType.toLowerCase() === 'user'" class="selected-node-info">
                  (用户: {{ userName }})
                </span>
                <span
                  v-else-if="permissionType.toLowerCase() === 'role'"
                  class="selected-node-info"
                >
                  (角色: {{ roleName }})
                </span>
              </div>
            </template>

            <div v-if="!selectedMenu" class="empty-state">
              <el-empty description="请在左侧选择一个菜单以查看权限详情" />
            </div>

            <div v-else class="permission-details" ref="permissionDetailsRef">
              <!-- 顶部汇总区域 -->
              <div class="permission-summary" v-if="hasAnySelectedPermissions">
                <div class="summary-header">
                  <span class="summary-title">已选权限</span>
                  <el-button type="primary" text size="small" @click="clearAllSelectedPermissions">
                    清空
                  </el-button>
                </div>
                <div
                  class="summary-tags"
                  :class="{ 'overflow-tags': getSelectedPermissionsCount > 15 }"
                >
                  <template v-for="(type, typeIdx) in permissionTypes" :key="typeIdx">
                    <template
                      v-for="(perm, idx) in getSelectedPermissionsByType(type.value)"
                      :key="type.value + '-' + idx"
                    >
                      <el-tag
                        size="small"
                        :type="type.tagType"
                        class="summary-tag"
                        closable
                        @close="handleRemovePermission(perm)"
                      >
                        {{ getPermissionDisplayName(perm) }}
                      </el-tag>
                    </template>
                  </template>
                </div>
              </div>

              <!-- API权限组 -->
              <el-collapse v-model="activeCollapses" accordion v-if="apiPermissions.length > 0">
                <el-collapse-item name="api">
                  <template #title>
                    <div class="collapse-title">
                      <div class="title-wrapper">
                        <div class="title-icon">
                          <el-icon><Connection /></el-icon>
                        </div>
                        <div class="title-text">API权限 ({{ apiPermissions.length }})</div>
                        <div class="selected-progress">
                          <el-progress
                            :percentage="
                              Math.round(
                                (getSelectedApiPermissions.length / apiPermissions.length) * 100
                              )
                            "
                            :format="
                              (format) =>
                                `${getSelectedApiPermissions.length}/${apiPermissions.length} (${format}%)`
                            "
                            :stroke-width="12"
                            :color="progressColor"
                          />
                        </div>
                      </div>
                    </div>
                  </template>
                  <div class="permission-list">
                    <el-empty v-if="apiPermissions.length === 0" description="暂无API权限" />
                    <div
                      v-else
                      class="permission-scrollable-container"
                      :style="{ maxHeight: apiListHeight ? apiListHeight + 'px' : 'none' }"
                    >
                      <div
                        v-for="(item, index) in apiPermissions"
                        :key="'api-' + index"
                        class="permission-item"
                      >
                        <div class="permission-content">
                          <div class="permission-actions">
                            <el-checkbox
                              v-model="item.checked"
                              @change="handleCheckPermission(item)"
                            ></el-checkbox>
                          </div>
                          <div class="permission-icon">
                            <i
                              v-if="item.icon"
                              class="iconfont-sys"
                              v-html="item.icon"
                              :style="{ color: item.color || '#409EFF' }"
                            ></i>
                            <i
                              v-if="!item.icon"
                              class="iconfont-sys"
                              v-html="'&#xe70b;'"
                              :style="{ color: item.color || '#409EFF' }"
                            ></i>
                          </div>
                          <div class="permission-info">
                            <span class="permission-name">{{ item.permissionName }}</span>
                            <div class="permission-extra">
                              <el-tag
                                size="small"
                                :type="getMethodTagType(item.api?.httpMethod)"
                                class="method-tag"
                              >
                                {{ item.api?.httpMethod }}
                              </el-tag>
                              <span class="api-url">{{ item.api?.apiUrl }}</span>
                              <el-tag
                                v-if="item.api?.common"
                                size="small"
                                type="success"
                                effect="plain"
                                class="common-tag"
                              >
                                公共API
                              </el-tag>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>

                <!-- 按钮权限组 -->
                <el-collapse-item name="button" v-if="buttonPermissions.length > 0">
                  <template #title>
                    <div class="collapse-title">
                      <div class="title-wrapper">
                        <div class="title-icon">
                          <el-icon><Mouse /></el-icon>
                        </div>
                        <div class="title-text">按钮权限 ({{ buttonPermissions.length }})</div>
                        <div class="selected-progress">
                          <el-progress
                            :percentage="
                              Math.round(
                                (getSelectedButtonPermissions.length / buttonPermissions.length) *
                                  100
                              )
                            "
                            :format="
                              (format) =>
                                `${getSelectedButtonPermissions.length}/${buttonPermissions.length} (${format}%)`
                            "
                            :stroke-width="12"
                            :color="progressColor"
                          />
                        </div>
                      </div>
                    </div>
                  </template>
                  <div class="permission-list">
                    <el-empty v-if="buttonPermissions.length === 0" description="暂无按钮权限" />
                    <div
                      v-else
                      class="permission-scrollable-container"
                      :style="{ maxHeight: buttonListHeight ? buttonListHeight + 'px' : 'none' }"
                    >
                      <div
                        v-for="(item, index) in buttonPermissions"
                        :key="'button-' + index"
                        class="permission-item"
                      >
                        <div class="permission-content">
                          <div class="permission-actions">
                            <el-checkbox
                              v-model="item.checked"
                              @change="handleCheckPermission(item)"
                            ></el-checkbox>
                          </div>
                          <div class="permission-icon">
                            <i
                              v-if="item.icon"
                              class="iconfont-sys"
                              v-html="item.icon"
                              :style="{ color: item.color || '#67C23A' }"
                            ></i>
                            <i
                              v-if="!item.icon"
                              class="iconfont-sys"
                              v-html="'&#xe72b;'"
                              :style="{ color: item.color || '#67C23A' }"
                            ></i>
                          </div>
                          <div class="permission-info">
                            <span class="permission-name">{{
                              item.button?.buttonName || item.permissionName
                            }}</span>
                            <div class="permission-extra">
                              <span class="button-key" v-if="item.button?.buttonKey">{{
                                item.button?.buttonKey
                              }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>

                <!-- 路由权限组 -->
                <el-collapse-item name="page" v-if="pagePermissions.length > 0">
                  <template #title>
                    <div class="collapse-title">
                      <div class="title-wrapper">
                        <div class="title-icon">
                          <el-icon><Link /></el-icon>
                        </div>
                        <div class="title-text">路由权限 ({{ pagePermissions.length }})</div>
                        <div class="selected-progress">
                          <el-progress
                            :percentage="
                              Math.round(
                                (getSelectedPagePermissions.length / pagePermissions.length) * 100
                              )
                            "
                            :format="
                              (format) =>
                                `${getSelectedPagePermissions.length}/${pagePermissions.length} (${format}%)`
                            "
                            :stroke-width="12"
                            :color="progressColor"
                          />
                        </div>
                      </div>
                    </div>
                  </template>
                  <div class="permission-list">
                    <el-empty v-if="pagePermissions.length === 0" description="暂无路由权限" />
                    <div
                      v-else
                      class="permission-scrollable-container"
                      :style="{ maxHeight: pageListHeight ? pageListHeight + 'px' : 'none' }"
                    >
                      <div
                        v-for="(item, index) in pagePermissions"
                        :key="'page-' + index"
                        class="permission-item"
                      >
                        <div class="permission-content">
                          <div class="permission-actions">
                            <el-checkbox
                              v-model="item.checked"
                              @change="handleCheckPermission(item)"
                            ></el-checkbox>
                          </div>
                          <div class="permission-icon">
                            <i
                              v-if="item.icon"
                              class="iconfont-sys"
                              v-html="item.icon"
                              :style="{ color: item.color || '#E6A23C' }"
                            ></i>
                            <i
                              v-if="!item.icon"
                              class="iconfont-sys"
                              v-html="'&#xe677;'"
                              :style="{ color: item.color || '#E6A23C' }"
                            ></i>
                          </div>
                          <div class="permission-info">
                            <span class="permission-name">{{ item.permissionName }}</span>
                            <div class="permission-extra">
                              <span class="page-url" v-if="item.page?.pageUrl">{{
                                item.page?.pageUrl
                              }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted, nextTick, onUnmounted, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Mouse, Link, Connection } from '@element-plus/icons-vue'
  import { useMenuStore } from '@/store/modules/menu'
  import { RoleService } from '@/api/roleApi'
  import { UserService } from '@/api/usersApi'
  import { formatMenuTitle, processMenuDetail } from '@/utils/menu'
  import { MenuListType } from '@/types/menu'
  import { menuService, MenuApiService } from '@/api/menuApi'
  import type { TagProps } from 'element-plus'

  // 定义自定义的权限项类型
  interface PermissionItem {
    id: number
    permissionName?: string
    permissionCode?: string
    permissionType?: number
    icon?: string
    color?: string
    sort?: number
    remarks?: string
    button?: {
      id: number
      buttonKey?: string
      buttonName?: string
    }
    api?: {
      id: number
      apiUrl?: string
      httpMethod?: string
      common: boolean
    }
    page?: {
      id: number
      pageUrl?: string
    }
    checked: boolean
  }

  // 权限类型定义
  const permissionTypes = [
    { value: 3, name: 'API权限', tagType: 'success' as const },
    { value: 2, name: '按钮权限', tagType: 'primary' as const },
    { value: 1, name: '路由权限', tagType: 'warning' as const }
  ]

  // 路由信息
  const route = useRoute()

  // 菜单和权限数据
  // 使用ref替代computed获取menuList
  const menuListData = ref<MenuListType[]>([])
  const menuTreeRef = ref()
  const loading = ref(false)
  const roleId = ref<number>(0)
  const roleName = ref<string>('')

  // 选中的菜单
  const selectedMenu = ref<MenuListType | null>(null)
  const defaultExpandedKeys = ref<string[]>([])

  // 是否显示复选框（根据type参数决定）
  const showCheckbox = ref<boolean>(false)
  const permissionType = ref<string>('')

  // 已选中的权限IDs
  const checkedPermissionIds = ref<number[]>([])
  const checkedMenuIds = ref<string[]>([]) // 修改为string类型，因为我们的node-key是字符串

  // 选中的权限列表
  const apiPermissions = ref<PermissionItem[]>([])
  const buttonPermissions = ref<PermissionItem[]>([])
  const pagePermissions = ref<PermissionItem[]>([])
  const activeCollapses = ref<string[]>(['api']) // 默认只展开API权限

  // 进度条颜色
  const progressColor = [
    { color: '#909399', percentage: 20 },
    { color: '#E6A23C', percentage: 40 },
    { color: '#409EFF', percentage: 60 },
    { color: '#67C23A', percentage: 100 }
  ]

  // 是否有任何选中的权限
  const hasAnySelectedPermissions = computed(() => {
    return (
      getSelectedApiPermissions.value.length > 0 ||
      getSelectedButtonPermissions.value.length > 0 ||
      getSelectedPagePermissions.value.length > 0
    )
  })

  // 获取菜单元数据
  const fetchMenuData = async () => {
    loading.value = true
    try {
      const response = await MenuApiService.getCurrentMenuDetail()
      if (response.success && response.data) {
        // 处理菜单数据为需要的格式
        if (Array.isArray(response.data)) {
          menuListData.value = response.data.map((menu) => processMenuDetail(menu))
        } else {
          // 如果返回单个对象，也包装成数组
          menuListData.value = [processMenuDetail(response.data)]
        }
      } else {
        ElMessage.error(response.message || '获取菜单数据失败')
      }
    } catch (error) {
      console.error('获取菜单数据失败:', error)
      ElMessage.error('获取菜单数据失败')
    } finally {
      loading.value = false
    }
  }

  // 仅包含菜单的树数据
  const menuTreeData = computed(() => {
    const processMenuData = (menus: MenuListType[]): any[] => {
      if (!menus || !menus.length) return []

      return menus.map((menu) => {
        // 为每个菜单节点创建一个新对象，保留我们需要的属性
        const menuNode = {
          ...menu,
          id: `m_${menu.id}`, // 使用带前缀的ID，以便识别这是菜单节点
          rawId: menu.id,
          children: menu.children ? processMenuData(menu.children) : []
        }

        return menuNode
      })
    }
    return processMenuData(menuListData.value)
  })

  // 方法标签类型
  const getMethodTagType = (method?: string): TagProps['type'] => {
    switch (method?.toUpperCase()) {
      case 'GET':
        return 'success'
      case 'POST':
        return 'primary'
      case 'PUT':
        return 'warning'
      case 'DELETE':
        return 'danger'
      default:
        return 'info'
    }
  }

  // 格式化菜单标题
  const formatTitle = (data: any) => {
    return data.meta?.title ? formatMenuTitle(data.meta.title) : data.name || '未命名菜单'
  }

  // 树节点属性
  const defaultProps = {
    children: 'children',
    label: formatTitle
  }

  // 用户ID (用于用户权限分配)
  const userId = ref<number>(0)
  const userName = ref<string>('')

  // 初始化数据
  const initData = async () => {
    loading.value = true
    try {
      // 获取菜单数据
      await fetchMenuData()

      // 获取路由参数中的type，决定是否显示复选框
      const routeType = route.params.type || route.query.type
      if (routeType) {
        if (typeof routeType === 'string') {
          permissionType.value = routeType
        } else if (Array.isArray(routeType)) {
          permissionType.value = routeType[0] || ''
        }

        // 如果type为role，则显示复选框
        showCheckbox.value = permissionType.value.toLowerCase() === 'role'
      }

      // 根据type类型处理不同的权限分配场景
      if (permissionType.value.toLowerCase() === 'user') {
        // 用户权限分配场景
        const routeUserId = route.params.userId || route.query.userId
        if (routeUserId) {
          userId.value = Number(routeUserId)
          const routeUserName = route.params.userName || route.query.userName
          if (routeUserName) {
            if (typeof routeUserName === 'string') {
              userName.value = routeUserName
            } else if (Array.isArray(routeUserName)) {
              userName.value = routeUserName[0] || ''
            }
          }

          await loadUserPermissions(userId.value)

          // 设置默认展开的第一级菜单
          setDefaultExpandedKeys()

          // 如果初始没有选中菜单，默认选中第一个菜单
          if (menuTreeData.value && menuTreeData.value.length > 0) {
            nextTick(() => {
              const firstMenu = menuTreeData.value[0]
              handleNodeClick(firstMenu)
              // 高亮第一个菜单节点
              if (menuTreeRef.value) {
                menuTreeRef.value.setCurrentKey(firstMenu.id)
              }
            })
          }
        } else {
          ElMessage.warning('未提供用户ID，无法加载权限数据')
        }
      } else {
        // 角色权限分配场景
        const routeRoleId = route.params.roleId || route.query.roleId
        if (routeRoleId) {
          roleId.value = Number(routeRoleId)
          const routeRoleName = route.params.roleName || route.query.roleName
          if (routeRoleName) {
            if (typeof routeRoleName === 'string') {
              roleName.value = routeRoleName
            } else if (Array.isArray(routeRoleName)) {
              roleName.value = routeRoleName[0] || ''
            }
          }

          await loadRolePermissions(roleId.value)

          // 设置默认展开的第一级菜单
          setDefaultExpandedKeys()

          // 如果初始没有选中菜单，默认选中第一个菜单
          if (menuTreeData.value && menuTreeData.value.length > 0) {
            nextTick(() => {
              const firstMenu = menuTreeData.value[0]
              handleNodeClick(firstMenu)
              // 高亮第一个菜单节点
              if (menuTreeRef.value) {
                menuTreeRef.value.setCurrentKey(firstMenu.id)
              }
            })
          }
        } else {
          ElMessage.warning('未提供角色ID，无法加载权限数据')
        }
      }
    } catch (error) {
      console.error('初始化数据失败:', error)
      ElMessage.error('加载权限数据失败')
    } finally {
      loading.value = false
    }
  }

  // 设置默认展开的菜单节点
  const setDefaultExpandedKeys = () => {
    // 默认展开第一级菜单
    const keys: string[] = []
    if (menuTreeData.value && menuTreeData.value.length > 0) {
      menuTreeData.value.forEach((menu) => {
        keys.push(menu.id)
      })
    }
    defaultExpandedKeys.value = keys
  }

  // 加载角色权限
  const loadRolePermissions = async (rId: number) => {
    try {
      const response = await RoleService.getRolePermissions(rId)

      if (response.success) {
        // 保存权限ID和菜单ID
        const { menuIds, permissionIds } = response.data || { menuIds: [], permissionIds: [] }
        // 将菜单ID转换为字符串格式，并添加前缀 'm_'
        checkedMenuIds.value = (menuIds || []).map((id) => `m_${id}`)
        checkedPermissionIds.value = permissionIds || []
      } else {
        ElMessage.warning(response.message || '获取角色权限失败')
      }
    } catch (error) {
      console.error('获取角色权限失败:', error)
      ElMessage.error('获取角色权限失败，请重试')
    }
  }

  // 处理菜单节点点击
  const handleNodeClick = (data: any) => {
    selectedMenu.value = data
    // 当选中一个菜单时，加载该菜单下的权限
    loadMenuPermissions(data)
    // 当菜单变化时重新计算高度
    nextTick(() => {
      calculateMaxHeight()
    })
  }

  // 处理菜单节点展开
  const handleNodeExpand = () => {
    // 当节点展开时，多次检查滚动条状态，确保DOM完全更新
    for (let delay of [50, 150, 300, 500]) {
      setTimeout(() => {
        checkTreeScroll()
      }, delay)
    }
  }

  // 处理菜单节点折叠
  const handleNodeCollapse = () => {
    // 当节点折叠时，多次检查滚动条状态，确保DOM完全更新
    for (let delay of [50, 150, 300, 500]) {
      setTimeout(() => {
        checkTreeScroll()
      }, delay)
    }
  }

  // 加载指定菜单下的权限
  const loadMenuPermissions = (menu: MenuListType) => {
    // 重置权限列表
    apiPermissions.value = []
    buttonPermissions.value = []
    pagePermissions.value = []

    if (!menu || !menu.meta || !menu.meta.authList) return

    // 将菜单下的权限添加到各自的列表中
    menu.meta.authList.forEach((auth) => {
      if (!auth) return

      // 创建权限对象的副本并添加checked属性
      const permCopy: PermissionItem = {
        ...auth,
        checked: checkedPermissionIds.value.includes(auth.id)
      }

      // 根据权限类型分类 - 处理数字或字符串类型
      const permType = Number(auth.permissionType)

      if (permType === 3 || auth.permissionType === 3) {
        // API权限
        apiPermissions.value.push(permCopy)
      } else if (permType === 2 || auth.permissionType === 2) {
        // 按钮权限
        buttonPermissions.value.push(permCopy)
      } else if (permType === 1 || auth.permissionType === 1) {
        // 路由权限
        pagePermissions.value.push(permCopy)
      }
    })

    // 权限加载后，延迟重新计算高度以确保DOM已更新
    setTimeout(() => {
      calculateMaxHeight()
    }, 100)
  }

  // 处理权限节点选中/取消选中
  const handleCheckPermission = (permission: PermissionItem) => {
    if (permission.checked) {
      // 添加到选中权限ID列表
      if (!checkedPermissionIds.value.includes(permission.id)) {
        checkedPermissionIds.value.push(permission.id)
      }
    } else {
      // 从选中权限ID列表移除
      const index = checkedPermissionIds.value.indexOf(permission.id)
      if (index > -1) {
        checkedPermissionIds.value.splice(index, 1)
      }
    }
  }

  // 移除已选权限
  const handleRemovePermission = (permission: PermissionItem) => {
    permission.checked = false
    handleCheckPermission(permission)
  }

  // 清空所有选中的权限
  const clearAllSelectedPermissions = () => {
    ElMessageBox.confirm('确定要清空当前菜单下所有已选择的权限吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        // 将所有权限的选中状态设为false
        apiPermissions.value.forEach((perm) => {
          perm.checked = false
        })
        buttonPermissions.value.forEach((perm) => {
          perm.checked = false
        })
        pagePermissions.value.forEach((perm) => {
          perm.checked = false
        })

        // 清空当前菜单下的权限ID
        if (selectedMenu.value && selectedMenu.value.meta && selectedMenu.value.meta.authList) {
          const permIds = selectedMenu.value.meta.authList.map((auth) => auth.id)
          checkedPermissionIds.value = checkedPermissionIds.value.filter(
            (id) => !permIds.includes(id)
          )
        }

        ElMessage.success('已清空当前菜单下所有已选择的权限')
      })
      .catch(() => {
        // 用户取消操作
      })
  }

  // 加载用户已分配的权限
  const loadUserPermissions = async (userIdValue: number) => {
    loading.value = true
    try {
      const response = await UserService.getAssignedPermissionIds(userIdValue)
      if (response.success) {
        // 获取权限ID并设置到checkedPermissionIds
        checkedPermissionIds.value = response.data || []
      } else {
        ElMessage.warning(response.message || '获取用户权限失败')
      }
    } catch (error) {
      console.error('获取用户权限失败:', error)
      ElMessage.error('获取用户权限失败，请重试')
    } finally {
      loading.value = false
    }
  }

  // 保存权限设置
  const savePermissions = async () => {
    // 根据权限类型决定保存方式
    if (permissionType.value.toLowerCase() === 'user') {
      // 用户权限保存
      if (!userId.value) {
        ElMessage.warning('请先选择一个用户')
        return
      }

      // 检查权限ID集合是否为空，如果为空则弹出确认框
      if (checkedPermissionIds.value.length === 0) {
        try {
          await ElMessageBox.confirm('您当前正在清空用户单独权限，是否确认？', '清空确认', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
          })
          // 用户确认清空权限，继续执行
        } catch {
          // 用户取消清空权限，中止操作
          return
        }
      }

      loading.value = true
      try {
        // 调用保存用户权限的API
        const response = await UserService.assignUserPermission({
          userId: userId.value,
          permissionIds: checkedPermissionIds.value
        })

        if (response.success) {
          ElMessage.success('用户权限设置保存成功')
        } else {
          ElMessage.error(response.message || '保存失败')
        }
      } catch (error) {
        console.error('保存用户权限失败:', error)
        ElMessage.error('保存用户权限失败，请重试')
      } finally {
        loading.value = false
      }
    } else {
      // 角色权限保存
      if (!roleId.value) {
        ElMessage.warning('请先选择一个角色')
        return
      }

      loading.value = true
      try {
        // 获取当前选中的菜单ID（去掉前缀'm_'）
        const selectedMenuIds = showCheckbox.value
          ? menuTreeRef.value?.getCheckedKeys().map((id: string) => Number(id.replace('m_', '')))
          : checkedMenuIds.value.map((id) => Number(id.replace('m_', '')))

        // 调用保存角色权限的API
        const response = await RoleService.saveRolePermissions({
          roleId: roleId.value,
          permissionIds: checkedPermissionIds.value,
          menuIds: selectedMenuIds
        })

        if (response.success) {
          ElMessage.success('角色权限设置保存成功')
        } else {
          ElMessage.error(response.message || '保存失败')
        }
        // 刷新列表数据
        const menuStore = useMenuStore()
        const { menuList: newMenuList, closeLoading } = await menuService.getMenuList(0) // 立即获取最新数据
        menuStore.setMenuList(newMenuList) // 更新 store
        closeLoading() // 关闭加载动画
      } catch (error) {
        console.error('保存角色权限失败:', error)
        ElMessage.error('保存角色权限失败，请重试')
      } finally {
        loading.value = false
      }
    }
  }

  // 获取已选择的API权限
  const getSelectedApiPermissions = computed(() => {
    return apiPermissions.value.filter((perm) => perm.checked)
  })

  // 获取已选择的按钮权限
  const getSelectedButtonPermissions = computed(() => {
    return buttonPermissions.value.filter((perm) => perm.checked)
  })

  // 获取已选择的路由权限
  const getSelectedPagePermissions = computed(() => {
    return pagePermissions.value.filter((perm) => perm.checked)
  })

  // 根据权限类型获取已选权限
  const getSelectedPermissionsByType = (type: number) => {
    switch (type) {
      case 3:
        return getSelectedApiPermissions.value
      case 2:
        return getSelectedButtonPermissions.value
      case 1:
        return getSelectedPagePermissions.value
      default:
        return []
    }
  }

  // 获取权限显示名称
  const getPermissionDisplayName = (perm: PermissionItem): string => {
    if (perm.permissionType === 2 && perm.button?.buttonName) {
      return perm.button.buttonName
    }
    return perm.permissionName || ''
  }

  // 计算所有已选择的权限总数
  const getSelectedPermissionsCount = computed(() => {
    return (
      getSelectedApiPermissions.value.length +
      getSelectedButtonPermissions.value.length +
      getSelectedPagePermissions.value.length
    )
  })

  // 权限列表容器参考
  const permissionDetailsRef = ref<HTMLElement | null>(null)

  // 动态计算权限列表的最大高度
  const listMaxHeight = ref(0) // 初始值为0，表示不限制高度
  const apiListHeight = ref(0)
  const buttonListHeight = ref(0)
  const pageListHeight = ref(0)

  // 计算最大高度的函数
  const calculateMaxHeight = () => {
    nextTick(() => {
      if (!permissionDetailsRef.value) return

      try {
        // 得到整个卡片容器的高度（父元素是el-card）
        const cardEl = permissionDetailsRef.value.closest('.box-card')
        if (!cardEl) return

        // 计算整个卡片的可用高度
        const cardHeight = cardEl.clientHeight

        // 卡片标题高度（包含padding）
        const cardHeaderHeight = 60

        // 顶部已选权限区域高度(如果存在)
        const summaryEl = permissionDetailsRef.value.querySelector('.permission-summary')
        const summaryHeight = summaryEl ? summaryEl.clientHeight + 20 : 0 // 加上margin-bottom 20px

        // 折叠面板标题高度(所有标题)
        const panelTitles = permissionDetailsRef.value.querySelectorAll('.el-collapse-item__header')
        let allHeadersHeight = 0
        panelTitles.forEach((title) => {
          allHeadersHeight += title.clientHeight
        })

        // 底部额外边距
        const bottomMargin = 20

        // 计算剩余的可用高度
        const availableHeight =
          cardHeight - cardHeaderHeight - summaryHeight - allHeadersHeight - bottomMargin

        // 获取当前展开的折叠面板
        const activePanel = activeCollapses.value[0]

        // 获取各个权限列表的实际内容高度
        const getContentHeight = (selector: string): number => {
          if (!permissionDetailsRef.value) return 0

          const container = permissionDetailsRef.value.querySelector(selector) as HTMLElement | null
          if (!container) return 0

          // 计算所有子元素的总高度
          let totalHeight = 0
          const items = container.querySelectorAll('.permission-item')
          items.forEach((item) => {
            const itemEl = item as HTMLElement
            totalHeight += itemEl.offsetHeight + 10 // 10px是每个item的margin-bottom
          })

          return totalHeight
        }

        // 计算各个权限列表的内容高度
        const apiContentHeight =
          apiPermissions.value.length > 0
            ? getContentHeight('.el-collapse-item[name="api"] .permission-scrollable-container')
            : 0
        const buttonContentHeight =
          buttonPermissions.value.length > 0
            ? getContentHeight('.el-collapse-item[name="button"] .permission-scrollable-container')
            : 0
        const pageContentHeight =
          pagePermissions.value.length > 0
            ? getContentHeight('.el-collapse-item[name="page"] .permission-scrollable-container')
            : 0

        // 计算所有内容的总高度
        const totalContentHeight = apiContentHeight + buttonContentHeight + pageContentHeight

        // 如果总内容高度小于可用高度，则不需要滚动条
        if (totalContentHeight <= availableHeight) {
          // 所有内容都可以显示，不需要滚动条
          apiListHeight.value = 0
          buttonListHeight.value = 0
          pageListHeight.value = 0
          listMaxHeight.value = 0

          // 添加无滚动条的类
          nextTick(() => {
            if (!permissionDetailsRef.value) return

            const lists = permissionDetailsRef.value.querySelectorAll('.permission-list')
            lists.forEach((list) => {
              const listEl = list as HTMLElement
              listEl.classList.add('no-scroll-list')
            })
          })
        } else {
          // 移除无滚动条的类
          nextTick(() => {
            if (!permissionDetailsRef.value) return

            const lists = permissionDetailsRef.value.querySelectorAll('.permission-list')
            lists.forEach((list) => {
              const listEl = list as HTMLElement
              listEl.classList.remove('no-scroll-list')
            })
          })
          // 需要滚动条，根据当前展开的面板分配高度
          const avgHeight = Math.max(150, Math.floor(availableHeight / 3))

          if (activePanel === 'api') {
            // 如果API面板是当前展开的，给它更多空间
            apiListHeight.value = Math.min(apiContentHeight, availableHeight * 0.6)
            buttonListHeight.value = avgHeight
            pageListHeight.value = avgHeight
          } else if (activePanel === 'button') {
            // 如果按钮面板是当前展开的，给它更多空间
            apiListHeight.value = avgHeight
            buttonListHeight.value = Math.min(buttonContentHeight, availableHeight * 0.6)
            pageListHeight.value = avgHeight
          } else if (activePanel === 'page') {
            // 如果路由面板是当前展开的，给它更多空间
            apiListHeight.value = avgHeight
            buttonListHeight.value = avgHeight
            pageListHeight.value = Math.min(pageContentHeight, availableHeight * 0.6)
          } else {
            // 没有展开的面板或者多个面板展开，平均分配
            apiListHeight.value = avgHeight
            buttonListHeight.value = avgHeight
            pageListHeight.value = avgHeight
          }

          // 设置通用最大高度（用于兼容现有代码）
          listMaxHeight.value = avgHeight
        }
      } catch (error) {
        console.error('计算高度时出错:', error)
        // 发生错误时使用一个合理的默认值
        listMaxHeight.value = 0
        apiListHeight.value = 0
        buttonListHeight.value = 0
        pageListHeight.value = 0
      }
    })
  }

  // 监听窗口大小变化和已选权限变化，重新计算高度
  watch(hasAnySelectedPermissions, calculateMaxHeight)

  // 监听分类展开状态变化
  watch(activeCollapses, calculateMaxHeight)

  // 窗口大小变化时重新计算
  const handleResize = () => {
    calculateMaxHeight()
    checkTreeScroll()
  }

  // 检查树容器是否需要滚动条 - 使用无滚动条但可滚动的策略
  const checkTreeScroll = () => {
    nextTick(() => {
      const treeContainer = document.querySelector('.tree-container') as HTMLElement | null
      if (treeContainer) {
        const treeEl = treeContainer.querySelector('.el-tree') as HTMLElement | null
        if (treeEl) {
          // 获取实际内容高度（包括所有子元素）
          const allNodes = treeEl.querySelectorAll('.el-tree-node')
          let totalContentHeight = 0

          // 只计算可见节点的高度
          allNodes.forEach((node) => {
            const nodeEl = node as HTMLElement
            if (window.getComputedStyle(nodeEl).display !== 'none') {
              totalContentHeight += nodeEl.offsetHeight
            }
          })

          // 判断是否需要滚动
          const needsScroll = totalContentHeight > treeContainer.clientHeight

          // 根据是否需要滚动添加或移除类
          if (needsScroll) {
            treeContainer.classList.add('needs-scroll')
          } else {
            treeContainer.classList.remove('needs-scroll')
          }
        }
      }
    })
  }

  // 直接添加样式到head，确保菜单树滚动条隐藏
  const addGlobalStyle = () => {
    const styleEl = document.createElement('style')
    styleEl.innerHTML = `
      .menu-permission-wrapper .tree-container {
        overflow: hidden !important;
      }
      /* 完全隐藏滚动条，但保持可滚动性 */
      .menu-permission-wrapper .tree-container.needs-scroll {
        overflow-y: scroll !important;
        scrollbar-width: none !important; /* Firefox */
        -ms-overflow-style: none !important; /* IE and Edge */
      }
      .menu-permission-wrapper .tree-container.needs-scroll::-webkit-scrollbar {
        display: none !important; /* Chrome, Safari, Opera */
        width: 0 !important;
      }
    `
    document.head.appendChild(styleEl)

    return () => {
      document.head.removeChild(styleEl)
    }
  }

  // 组件挂载后计算高度
  onMounted(() => {
    initData() // 加载初始数据
    window.addEventListener('resize', handleResize)
    calculateMaxHeight()

    // 添加全局样式
    const removeStyle = addGlobalStyle()

    // 初始化后多次检查树是否需要滚动，确保在各种情况下都能正确处理
    for (let delay of [100, 300, 500, 1000, 2000]) {
      setTimeout(() => {
        checkTreeScroll()
      }, delay)
    }

    // 组件卸载前清理
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      removeStyle()
    })
  })

  // 监听菜单树数据变化，重新检查是否需要滚动
  watch(menuTreeData, () => {
    // 多次延迟检查，确保DOM已完全更新
    for (let delay of [100, 300, 500, 800]) {
      setTimeout(checkTreeScroll, delay)
    }
  })

  // 监听默认展开的节点变化
  watch(defaultExpandedKeys, () => {
    // 多次延迟检查，确保DOM已完全更新
    for (let delay of [100, 300, 500, 800]) {
      setTimeout(checkTreeScroll, delay)
    }
  })
</script>

<style lang="scss" scoped>
  .menu-permission-wrapper {
    position: relative;
    height: 100%;
    overflow: hidden;
  }

  .menu-permission-container {
    position: relative;
    box-sizing: border-box;
    height: 100%;
    padding: 20px;
    overflow: hidden;
  }

  .full-height-row {
    height: 100%;
  }

  .full-height-col {
    height: 100%;
  }

  .box-card {
    display: flex;
    flex-direction: column;
    height: 100%;
    margin-bottom: 0;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .selected-node-info {
        font-size: 14px;
        color: #606266;
      }

      .card-header-actions {
        display: flex;
        gap: 10px;
      }
    }

    // 优化内容区自动撑满
    :deep(.el-card__body) {
      display: flex;
      flex: 1;
      flex-direction: column;
      overflow: hidden;
    }
  }

  .tree-container {
    height: 100%;
    overflow: hidden; /* 默认隐藏滚动条 */

    /* 滚动条基本样式 - 完全隐藏 */
    &::-webkit-scrollbar {
      display: none;
      width: 0;
    }

    /* 确保Firefox也隐藏滚动条 */
    scrollbar-width: none;

    /* 确保IE和Edge也隐藏滚动条 */
    -ms-overflow-style: none;

    /* 当需要滚动时，允许滚动但不显示滚动条 */
    &.needs-scroll {
      overflow-y: scroll;
    }
  }

  .custom-tree-node {
    display: flex;
    align-items: center;
    padding: 6px 0;
    font-size: 14px;

    .node-icon {
      margin-right: 8px;
      font-size: 16px;
    }

    .node-title {
      flex-grow: 1;
    }
  }

  .empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }

  .permission-details {
    height: 100%;
    padding-bottom: 20px;
    overflow-y: auto;

    // 只在内容超出时显示滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: #dcdfe6;
      border-radius: 3px;
    }

    // 当内容不足以滚动时，隐藏滚动条
    &:not(:hover)::-webkit-scrollbar-thumb {
      background-color: transparent;
    }

    // 顶部汇总区域样式
    .permission-summary {
      padding: 12px 16px;
      margin-bottom: 20px;
      background-color: #f8f9fa;
      border: 1px solid #ebeef5;
      border-radius: 4px;

      .summary-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 10px;

        .summary-title {
          font-size: 15px;
          font-weight: 500;
          color: #303133;
        }
      }

      .summary-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        &.overflow-tags {
          max-height: 120px;
          padding-right: 8px;
          overflow-y: auto;
        }

        .summary-tag {
          margin-right: 2px;
        }
      }
    }

    .collapse-title {
      width: 100%;

      .title-wrapper {
        display: flex;
        flex-wrap: nowrap;
        align-items: center;
        width: 100%;

        .title-icon {
          flex-shrink: 0;
          margin-right: 8px;
          font-size: 18px;
        }

        .title-text {
          flex-shrink: 0;
          margin-right: 12px;
          font-weight: 500;
          white-space: nowrap;
        }

        .selected-progress {
          flex: 1;
          margin-right: 10px;
        }
      }
    }

    .permission-list {
      max-height: none; // 移除最大高度限制，确保所有内容都显示
      padding: 10px 0;
      margin-bottom: 20px; // 增加底部间距
    }

    // 添加一个无滚动条的类，通过JS动态添加
    .no-scroll-list {
      overflow: visible !important;
    }

    .permission-scrollable-container {
      padding-right: 5px; // 为滚动条留出空间
      overflow-y: auto; // 添加垂直滚动，但只在需要时显示
      transition: max-height 0.3s ease; // 添加过渡效果

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-thumb {
        background-color: #dcdfe6;
        border-radius: 3px;
      }

      // 当没有设置最大高度或最大高度为0时，确保内容完全展示
      &[style*='max-height: none'] {
        overflow-y: visible;
      }
    }

    .permission-item {
      padding: 8px 12px;
      margin-bottom: 10px;
      background-color: #f9f9f9;
      border-radius: 4px;

      &:hover {
        background-color: #f0f9ff;
      }

      .permission-content {
        display: flex;
        align-items: center;

        .permission-actions {
          margin-right: 12px;
        }

        .permission-icon {
          margin-right: 12px;

          .iconfont-sys {
            display: inline-block;
            width: 24px;
            height: 24px;
            font-size: 20px;
            text-align: center;
          }
        }

        .permission-info {
          display: flex;
          flex: 1;
          align-items: center;
          justify-content: space-between;
          overflow: visible;

          .permission-name {
            max-width: 200px;
            padding-right: 10px;
            overflow: hidden;
            font-weight: 500;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .permission-extra {
            display: flex;
            flex: 1;
            flex-wrap: wrap;
            align-items: center;
            justify-content: flex-end;

            .method-tag {
              margin-right: 6px;
            }

            .api-url,
            .page-url,
            .button-key {
              margin-right: 6px;
              color: #909399;
              word-break: break-all;
              white-space: normal;
            }

            .common-tag {
              margin-left: 4px;
            }
          }
        }
      }
    }
  }

  // 优化collapse组件
  :deep(.el-collapse-item__content) {
    padding-bottom: 20px;
  }

  :deep(.el-collapse-item:last-child) {
    margin-bottom: 40px;
  }

  :deep(.el-collapse-item__header) {
    padding-right: 16px;
  }

  // 自定义进度条样式
  :deep(.el-progress-bar__inner) {
    transition: all 0.3s ease;
  }

  // 添加全局修复样式
  :deep(body),
  :deep(html) {
    overflow: hidden;
  }
</style>
