<template>
  <div class="console">
    <CardList :dataList="cardListData"></CardList>

    <div class="column column2">
      <ActiveUser :chartData="activeUserChartData" :listData="activeUserListData"></ActiveUser>
      <SalesOverview></SalesOverview>
    </div>

    <div class="column column3">
      <NewUser class="new-user-card"></NewUser>
      <ArtDataListCard
        :maxCount="5"
        :list="activityListData"
        title="最近活动"
        subtitle="用户最新操作"
        :showMoreButton="true"
        @more="handleMore"
        class="activity-card"
      />
      <div class="donut-charts">
        <ArtDonutChartCard
          :value="systemData.roleCount"
          :title="'角色数'"
          :percentage="systemData.rolePercent"
          :height="9.6"
          :data="[systemData.rolePercent, 100 - systemData.rolePercent]"
          :radius="['50%', '70%']"
          :color="['#409EFF', '#f5f7fa']"
          class="donut-card"
        />
        <ArtDonutChartCard
          :value="systemData.menuCount"
          :title="'菜单数'"
          :percentage="systemData.menuPercent"
          :height="9.6"
          :data="[systemData.menuPercent, 100 - systemData.menuPercent]"
          :radius="['50%', '70%']"
          :color="['#67C23A', '#f5f7fa']"
          class="donut-card"
        />
        <ArtDonutChartCard
          :value="systemData.permissionCount"
          :title="'权限数'"
          :percentage="systemData.permissionPercent"
          :height="9.7"
          :data="[systemData.permissionPercent, 100 - systemData.permissionPercent]"
          :radius="['50%', '70%']"
          :color="['#E6A23C', '#f5f7fa']"
          class="donut-card"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import CardList from './widget/CardList.vue'
  import ActiveUser from './widget/ActiveUser.vue'
  import SalesOverview from './widget/SalesOverview.vue'
  import NewUser from './widget/NewUser.vue'
  import ArtDataListCard from '@/components/core/cards/ArtDataListCard.vue'
  import ArtDonutChartCard from '@/components/core/cards/ArtDonutChartCard.vue'
  import { useSettingStore } from '@/store/modules/setting'
  import { useCommon } from '@/composables/useCommon'
  import { computed, watch, ref, onMounted } from 'vue'
  import { AnalyticsService } from '@/api/analyticsApi'
  import {
    IndexUserOperationLogResponse,
    IndexSystemDataResponse
  } from '@/api/model/analyticsModel'

  const router = useRouter()
  const settingStore = useSettingStore()
  const currentGlopTheme = computed(() => settingStore.systemThemeType)

  watch(currentGlopTheme, () => {
    settingStore.reload()
  })

  useCommon().scrollToTop()

  const cardListData = ref([
    { des: '总访问次数', icon: '&#xe721;', startVal: 0, duration: 1000, num: 0, change: '0%' },
    { des: '独立访客数', icon: '&#xe724;', startVal: 0, duration: 1000, num: 0, change: '0%' },
    { des: '点击量', icon: '&#xe7aa;', startVal: 0, duration: 1000, num: 0, change: '0%' },
    { des: '新用户', icon: '&#xe82a;', startVal: 0, duration: 1000, num: 0, change: '0%' }
  ])

  interface ChartData {
    dates: string[]
    values: number[]
  }
  const activeUserChartData = ref<ChartData>({ dates: [], values: [] })
  const activeUserListData = ref([
    { name: '总用户量', num: '0' },
    { name: '总访问量', num: '0' },
    { name: '总点击量', num: '0' },
    { name: '平均访问时长', num: '0S' }
  ])

  const loading = ref(false)

  interface ActivityListItem {
    title: string
    status: string
    time: string
    class: string
    icon: string
  }
  const activityListData = ref<ActivityListItem[]>([])

  const systemData = ref<IndexSystemDataResponse>({
    id: 0,
    roleCount: 0,
    menuCount: 0,
    permissionCount: 0,
    rolePercent: 0,
    menuPercent: 0,
    permissionPercent: 0
  })

  const fetchTodayStats = async () => {
    try {
      const data = await AnalyticsService.getTodayStats()
      if (data.success) {
        const statsData = data.data
        cardListData.value = [
          {
            des: '总访问次数',
            icon: '&#xe721;',
            startVal: 0,
            duration: 1000,
            num: statsData.totalVisits,
            change: formatPercentage(statsData.visitsPercent)
          },
          {
            des: '独立访客数',
            icon: '&#xe724;',
            startVal: 0,
            duration: 1000,
            num: statsData.uniqueVisitors,
            change: formatPercentage(statsData.visitorsPercent)
          },
          {
            des: '点击量',
            icon: '&#xe7aa;',
            startVal: 0,
            duration: 1000,
            num: statsData.totalClicks,
            change: formatPercentage(statsData.clickPercent)
          },
          {
            des: '新用户',
            icon: '&#xe82a;',
            startVal: 0,
            duration: 1000,
            num: statsData.newUsers,
            change: formatPercentage(statsData.newUserPercent)
          }
        ]
        activeUserListData.value = [
          { name: '总用户量', num: formatNumber(statsData.allTotalUser) },
          { name: '总访问量', num: formatNumber(statsData.allTotalVisits) },
          { name: '总点击量', num: formatNumber(statsData.allTotalClicks) },
          { name: '平均访问时长', num: `${statsData.avgVisitDuration}S` }
        ]
      }
    } catch (error) {
      console.error('获取今日统计数据失败:', error)
    }
  }

  const fetchTrendStats = async () => {
    try {
      const data = await AnalyticsService.getTrendStats(9)
      if (data && data.success) {
        const trendData = data.data
        activeUserChartData.value = {
          dates: trendData.days.map((day) => String(day)),
          values: trendData.visits
        }
      }
    } catch (error) {
      console.error('获取趋势数据失败:', error)
    }
  }

  const fetchActivityLog = async () => {
    try {
      const response = await AnalyticsService.getIndexUserOperationLog(5)
      if (response.success && response.data) {
        activityListData.value = response.data.map((log: IndexUserOperationLogResponse) => {
          let icon = log.icon
          let itemClass = ''

          if (!icon) {
            switch (log.method?.toUpperCase()) {
              case 'POST':
                icon = '&#xe6f2;'
                itemClass = 'bg-primary'
                break
              case 'PUT':
                icon = '&#xe806;'
                itemClass = 'bg-secondary'
                break
              case 'DELETE':
                icon = '&#xe813;'
                itemClass = 'bg-danger'
                break
              default:
                icon = '&#xe61e;'
                itemClass = 'bg-info'
            }
          } else {
            switch (log.method?.toUpperCase()) {
              case 'POST':
                itemClass = 'bg-primary'
                break
              case 'PUT':
                itemClass = 'bg-secondary'
                break
              case 'DELETE':
                itemClass = 'bg-danger'
                break
              default:
                itemClass = 'bg-info'
            }
          }

          return {
            title: log.title,
            status: log.userName,
            time: log.createTime,
            icon: icon || '',
            class: itemClass
          }
        })
      }
    } catch (error) {
      console.error('获取活动日志失败:', error)
      activityListData.value = []
    }
  }

  const fetchSystemData = async () => {
    try {
      const response = await AnalyticsService.getIndexSystemData()
      if (response.success && response.data) {
        systemData.value = response.data
      }
    } catch (error) {
      console.error('获取系统数据失败:', error)
    }
  }

  const formatPercentage = (value: number) => {
    if (value > 0) {
      return `+${value}%`
    } else {
      return `${value}%`
    }
  }
  const formatNumber = (num: number) => {
    if (num >= 1000) {
      return `${Math.floor(num / 1000)}k`
    }
    return num.toString()
  }

  onMounted(async () => {
    loading.value = true
    await Promise.all([fetchTodayStats(), fetchTrendStats(), fetchActivityLog(), fetchSystemData()])
    loading.value = false
  })

  const handleMore = () => {
    router.push('/system/log/log')
  }
</script>

<style lang="scss" scoped>
  .console {
    :deep(.card-header) {
      display: flex;
      justify-content: space-between;
      padding: 20px 25px 5px 0;

      .title {
        h4 {
          font-size: 18px;
          font-weight: 500;
          color: var(--art-text-gray-800);
        }

        p {
          margin-top: 3px;
          font-size: 13px;

          span {
            margin-left: 10px;
            color: #52c41a;
          }
        }
      }
    }

    :deep(.box-title) {
      color: var(--art-gray-900) !important;
    }

    :deep(.subtitle) {
      color: var(--art-gray-600) !important;
    }

    :deep(.card-list li),
    .region,
    .dynamic,
    .bottom-wrap {
      background: var(--art-main-bg-color);
      border-radius: calc(var(--custom-radius) + 4px) !important;
    }

    .column {
      display: flex;
      justify-content: space-between;
      margin-top: var(--console-margin);
      background-color: transparent !important;
    }

    .column3 {
      .new-user-card {
        width: 55%;
      }

      .activity-card {
        width: 40%;
        margin-left: 15px;
      }

      .donut-charts {
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        width: calc(25% - 15px);
        margin-left: 15px;

        :deep(.art-donut-chart-card) {
          height: auto;
          padding: 10px 0;
        }

        .donut-card {
          margin-bottom: 18px;
          background: var(--art-main-bg-color);
          border-radius: calc(var(--custom-radius) + 4px) !important;

          &:first-child {
            margin-top: 0;
          }

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }

    .bottom-wrap {
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      height: 300px;
      padding: 20px;
      margin-top: var(--console-margin);
      background: var(--art-main-bg-color);

      h2 {
        margin-top: 10px;
        font-size: 20px;
        font-weight: 500;
      }

      p {
        margin-top: 5px;
        font-size: 14px;
        color: var(--art-gray-600);
      }

      .button-wrap {
        display: flex;
        flex-wrap: wrap;
        width: 600px;
        margin-top: 35px;

        .btn {
          display: flex;
          justify-content: space-between;
          width: 240px;
          height: 50px;
          padding: 0 15px;
          margin: 0 15px 15px 0;
          font-size: 14px;
          line-height: 50px;
          color: var(--art-gray-800);
          text-align: center;
          cursor: pointer;
          background: var(--art-bg-color);
          border-radius: calc(var(--custom-radius) / 2 + 2px) !important;
          transition: all 0.3s;

          &:hover {
            box-shadow: 0 5px 10px rgb(0 0 0 / 5%);
            transform: translateY(-4px);
          }
        }
      }
    }
  }
</style>

<style lang="scss" scoped>
  .console {
    @media screen and (max-width: $device-ipad-pro) {
      .column2 {
        margin-top: 15px;

        :deep(.active-user) {
          width: 50%;
        }

        :deep(.sales-overview) {
          width: calc(50% - 15px);
        }
      }

      .column3 {
        display: flex;
        flex-wrap: wrap;
        margin-top: 15px;

        .new-user-card {
          width: 35%;
        }

        .activity-card {
          width: 40%;
          margin-left: 15px;
        }

        .donut-charts {
          width: calc(25% - 15px);
          margin-left: 15px;
        }
      }

      .bottom-wrap {
        height: auto;
        margin-top: 15px;

        .button-wrap {
          width: 470px;
          margin-top: 20px;

          .btn {
            width: 180px;
          }
        }

        .right-img {
          width: 300px;
          height: 230px;
        }
      }
    }

    @media screen and (max-width: $device-ipad-vertical) {
      :deep(.card-list) {
        width: calc(100% + 15px);
        margin-left: -15px;

        li {
          width: calc(50% - 15px);
          margin: 0 0 15px 15px;
        }
      }

      .column2 {
        display: block;
        margin-top: 0;

        :deep(.active-user) {
          width: 100%;
        }

        :deep(.sales-overview) {
          width: 100%;
          margin-top: 15px;
        }
      }

      .column3 {
        display: block;
        margin-top: 15px;

        .new-user-card {
          width: 100%;
        }

        .activity-card {
          width: 100%;
          margin: 15px 0 0;
          margin-left: 0;
        }

        .donut-charts {
          display: flex;
          flex-flow: row wrap;
          width: 100%;
          margin: 15px 0 0;
          margin-left: 0;

          .donut-card {
            width: calc(33.33% - 10px);
            margin-right: 15px;
            margin-bottom: 0;

            &:last-child {
              margin-right: 0;
            }
          }
        }
      }

      .bottom-wrap {
        height: auto;
        margin-top: 15px;

        .button-wrap {
          width: 100%;
          margin-top: 20px;

          .btn {
            width: 190px;
            height: 50px;
            line-height: 50px;
          }
        }

        .right-img {
          display: none;
        }
      }
    }

    @media screen and (max-width: $device-phone) {
      :deep(.card-list) {
        width: 100%;
        margin: 0;

        li {
          width: 100%;
          margin: 0 0 15px;
        }
      }

      :deep(.active-user) {
        .chart {
          padding: 10px;
        }
      }

      .sales-overview {
        height: 300px;
        padding: 20px 15px;

        :deep(.card-header) {
          padding: 0 0 0 5px !important;
        }
      }

      .column3 {
        .donut-charts {
          flex-direction: column;

          .donut-card {
            width: 100%;
            margin-right: 0;
            margin-bottom: 15px;

            &:last-child {
              margin-bottom: 0;
            }
          }
        }
      }

      .bottom-wrap {
        padding: 0 15px;

        .button-wrap {
          width: 100%;
          margin-top: 20px;

          .btn {
            width: 100%;
            margin-right: 0;
          }
        }
      }
    }
  }
</style>
