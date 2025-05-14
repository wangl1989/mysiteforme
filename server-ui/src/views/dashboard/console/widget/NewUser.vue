<template>
  <div class="region new-user art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">新用户</h4>
      </div>
      <el-button class="more-btn" v-menu-auth="'/system/user/Account'" @click="handleMore" v-ripple
        >查看更多</el-button
      >
    </div>
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
    <art-table v-else :data="tableData" :pagination="false" size="large">
      <template #default>
        <el-table-column label="用户" prop="avatar" width="180px">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <img class="avatar" :src="scope.row.icon" />
              <span class="user-name">{{ scope.row.nickName || scope.row.loginName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="邮箱" prop="email" />
        <el-table-column label="地区" prop="location">
          <template #default="scope">
            <span style="margin-left: 10px">{{ scope.row.location || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="资料完整进度" width="240">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.percent || 0"
              :color="getProgressColor(scope.row.percent)"
              :stroke-width="4"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue'
  import { AnalyticsService } from '@/api/analyticsApi'
  import { IndexUserCollectionResponse } from '@/api/model/analyticsModel'

  const tableData = ref<IndexUserCollectionResponse[]>([])
  const loading = ref(false)
  const router = useRouter()

  const fetchNewUsers = async () => {
    loading.value = true
    try {
      const response = await AnalyticsService.getIndexUserCollection(6)
      if (response.success && response.data) {
        tableData.value = response.data.map((user) => ({
          ...user,
          icon: getImgUrl(user.icon, user.id),
          pro: 0
        }))
        addAnimation()
      }
    } catch (error) {
      console.error('获取新用户数据失败:', error)
      tableData.value = []
    } finally {
      loading.value = false
    }
  }

  // 获取图片url
  const getImgUrl = (url: string | undefined, userId: string | number) => {
    if (!url) {
      return `https://api.dicebear.com/9.x/adventurer/svg?seed=${userId}`
    }

    if (url.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + url
    }

    return url
  }

  const handleMore = () => {
    router.push('/system/user/account')
  }

  onMounted(() => {
    fetchNewUsers()
  })

  const getProgressColor = (percentage: number | undefined) => {
    if (!percentage) return '#D9D9D9'
    if (percentage < 30) return 'rgb(var(--art-error))'
    if (percentage < 70) return 'rgb(var(--art-warning))'
    return 'rgb(var(--art-success))'
  }

  const addAnimation = () => {
    setTimeout(() => {
      tableData.value = tableData.value.map((item) => ({
        ...item,
        percent: item.percent || 0
      }))
    }, 100)
  }
</script>

<style lang="scss">
  .region {
    .el-progress-bar__inner {
      transition: all 1s !important;
    }

    .el-radio-button__original-radio:checked + .el-radio-button__inner {
      color: var(--el-color-primary) !important;
      background: transparent !important;
    }

    .any-table {
      .el-table {
        thead {
          tr {
            height: 55px !important;
          }
        }

        tr {
          height: 55px !important;
        }
      }
    }
  }
</style>

<style lang="scss" scoped>
  .region {
    width: 100%;

    .any-table {
      box-shadow: none;
    }

    .card-header {
      padding-left: 25px !important;
    }

    .avatar {
      width: 36px;
      height: 36px;
      margin-right: 10px;
      border-radius: 6px;
    }

    .user-name {
      margin-left: 0;
    }

    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 200px;

      .loading-spinner {
        width: 40px;
        height: 40px;
        border: 3px solid var(--el-color-primary-light-8);
        border-top: 3px solid var(--el-color-primary);
        border-radius: 50%;
        animation: spin 1s linear infinite;
      }

      p {
        margin-top: 10px;
        color: var(--art-text-gray-700);
      }
    }
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }

  @media screen and (max-width: $device-notebook) {
    .region {
      width: 100%;
    }
  }
</style>
