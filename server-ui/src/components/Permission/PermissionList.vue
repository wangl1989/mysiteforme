<!-- eslint-disable prettier/prettier -->
<template>
  <div class="permission-list">
    <!-- 页面权限分组 - 只在有数据时显示 -->
    <div class="permission-group" v-if="hasPermissionType('1')">
      <div class="permission-group-header" @click="toggleGroup('1')">
        <el-icon class="arrow-icon" :class="{ 'is-expanded': groupVisible['1'] }">
          <arrow-right />
        </el-icon>
        <span>路由权限</span>
      </div>
      <div v-show="groupVisible['1']">
        <el-table :data="getPermissionsByType('1')" style="width: 100%">
          <el-table-column prop="permissionName" label="权限名称" min-width="180">
            <template #default="scope">
              <div class="permission-name-cell">
                <i
                  v-if="scope.row.icon"
                  class="iconfont-sys"
                  v-html="scope.row.icon"
                  :style="{ color: scope.row.color }"
                ></i>
                <span>
                  {{ scope.row.permissionName }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="page.pageUrl" label="路由地址" min-width="180" />
          <el-table-column prop="sort" label="排序值" width="80" align="center" />
          <el-table-column prop="updateDate" label="更新时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.updateDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="right">
            <template #default="scope">
              <span
                class="action-btn edit-btn"
                v-auth="'permission_edit'"
                @click="handleEdit(scope.row)"
                >编辑</span
              >
              <span
                class="action-btn delete-btn"
                v-auth="'permission_delete'"
                @click="handleDelete(scope.row)"
                >删除</span
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 按钮权限分组 - 只在有数据时显示 -->
    <div class="permission-group" v-if="hasPermissionType('2')">
      <div class="permission-group-header" @click="toggleGroup('2')">
        <el-icon class="arrow-icon" :class="{ 'is-expanded': groupVisible['2'] }">
          <arrow-right />
        </el-icon>
        <span>按钮权限</span>
      </div>
      <div v-show="groupVisible['2']">
        <el-table :data="getPermissionsByType('2')" style="width: 100%">
          <el-table-column prop="permissionName" label="权限名称" min-width="150">
            <template #default="scope">
              <div class="permission-name-cell">
                <i
                  v-if="scope.row.icon"
                  class="iconfont-sys"
                  v-html="scope.row.icon"
                  :style="{ color: scope.row.color }"
                ></i>
                <span>
                  {{ scope.row.permissionName }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="button.buttonKey" label="按钮关键词" min-width="150" />
          <el-table-column prop="sort" label="排序值" width="80" align="center" />
          <el-table-column prop="updateDate" label="更新时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.updateDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="right">
            <template #default="scope">
              <span
                class="action-btn edit-btn"
                v-auth="'permission_edit'"
                @click="handleEdit(scope.row)"
                >编辑</span
              >
              <span
                class="action-btn delete-btn"
                v-auth="'permission_delete'"
                @click="handleDelete(scope.row)"
                >删除</span
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- API权限分组 - 只在有数据时显示 -->
    <div class="permission-group" v-if="hasPermissionType('3')">
      <div class="permission-group-header" @click="toggleGroup('3')">
        <el-icon class="arrow-icon" :class="{ 'is-expanded': groupVisible['3'] }">
          <arrow-right />
        </el-icon>
        <span>API权限</span>
      </div>
      <div v-show="groupVisible['3']">
        <el-table :data="getPermissionsByType('3')" style="width: 100%">
          <el-table-column prop="permissionName" label="权限名称" min-width="120">
            <template #default="scope">
              <div class="permission-name-cell">
                <i
                  v-if="scope.row.icon"
                  class="iconfont-sys"
                  v-html="scope.row.icon"
                  :style="{ color: scope.row.color }"
                ></i>
                <span>
                  {{ scope.row.permissionName }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="api.apiUrl" label="API地址" min-width="150" />
          <el-table-column prop="api.httpMethod" label="请求方法" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getMethodTagType(scope.row.api?.httpMethod)" size="small">
                {{ scope.row.api?.httpMethod || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="api.common" label="公共接口" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.api?.common ? 'success' : 'info'" size="small">
                {{ scope.row.api?.common ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序值" width="80" align="center" />
          <el-table-column prop="updateDate" label="更新时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.updateDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="right">
            <template #default="scope">
              <span class="action-btn edit-btn" @click="handleEdit(scope.row)">编辑</span>
              <span class="action-btn delete-btn" @click="handleDelete(scope.row)">删除</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 当没有任何权限数据时显示提示 -->
    <div class="no-permission-tip" v-if="!hasAnyPermissions">该菜单下暂无权限数据</div>
  </div>
</template>

<script setup lang="ts">
  import { reactive, computed } from 'vue'
  import { ArrowRight } from '@element-plus/icons-vue'
  import { formatDate } from '@/utils/date'

  const props = defineProps({
    permissions: {
      type: Array,
      default: () => []
    }
  })

  const emit = defineEmits(['edit', 'delete'])

  // 分组显示状态
  const groupVisible = reactive<Record<string, boolean>>({
    '1': true, // 页面权限默认展开
    '2': true, // 按钮权限默认展开
    '3': true // API权限默认展开
  })

  // 切换分组显示/隐藏
  const toggleGroup = (type: string) => {
    groupVisible[type] = !groupVisible[type]
  }

  // 根据权限类型获取权限列表
  const getPermissionsByType = (type: string) => {
    if (!props.permissions) return []
    return props.permissions.filter((item: any) => String(item.permissionType) === type)
  }

  // 检查是否有某种权限类型的数据
  const hasPermissionType = (type: string) => {
    return getPermissionsByType(type).length > 0
  }

  // 检查是否有任何权限数据
  const hasAnyPermissions = computed(() => props.permissions && props.permissions.length > 0)

  // 根据HTTP方法获取标签类型
  const getMethodTagType = (method: string) => {
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

  // 编辑权限
  const handleEdit = (row: any) => {
    emit('edit', row)
  }

  // 删除权限
  const handleDelete = (row: any) => {
    emit('delete', row)
  }
</script>

<style lang="scss" scoped>
  .permission-list {
    .permission-group {
      margin-bottom: 10px;

      .permission-group-header {
        display: flex;
        align-items: center;
        padding: 8px 0;
        margin-bottom: 5px;
        font-weight: 500;
        color: #303133;
        cursor: pointer;
        border-bottom: 1px solid #ebeef5;

        .arrow-icon {
          margin-right: 5px;
          font-size: 14px;
          transition: transform 0.2s;

          &.is-expanded {
            transform: rotate(90deg);
          }
        }
      }

      .empty-tip {
        padding: 10px 0;
        font-size: 14px;
        color: #909399;
        text-align: center;
      }

      :deep(.el-table) {
        .el-table__row {
          td {
            border-bottom: 1px solid #ebeef5;
          }
        }
      }

      .action-btn {
        margin-left: 10px;
        font-size: 12px;
        cursor: pointer;
      }

      .edit-btn {
        color: #409eff;
      }

      .delete-btn {
        color: #f56c6c;
      }

      .permission-name-cell {
        display: flex;
        align-items: center;

        .el-icon {
          margin-right: 5px;
          font-size: 16px;
        }

        .iconfont-sys {
          margin-right: 10px;
          font-size: 16px;
        }
      }
    }

    .no-permission-tip {
      padding: 20px 0;
      font-size: 14px;
      color: #909399;
      text-align: center;
    }
  }
</style>
