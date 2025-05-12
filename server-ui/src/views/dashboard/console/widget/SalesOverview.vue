<template>
  <div class="region sales-overview art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">访问量</h4>
      </div>
    </div>
    <div class="chart" ref="chartRef">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
  import { storeToRefs } from 'pinia'
  import echarts from '@/plugins/echarts'
  import { useECharts } from '@/utils/echarts/useECharts'
  import { hexToRgba } from '@/utils/colors'
  import { useSettingStore } from '@/store/modules/setting'
  import { SystemThemeEnum } from '@/enums/appEnum'
  import { getCssVariable } from '@/utils/colors'
  import { AnalyticsService } from '@/api/analyticsApi'

  const chartRef = ref<HTMLDivElement>()
  const { setOptions, removeResize, resize } = useECharts(chartRef as Ref<HTMLDivElement>)

  const settingStore = useSettingStore()
  const { menuOpen, systemThemeType } = storeToRefs(settingStore)

  const isLight = computed(() => systemThemeType.value === SystemThemeEnum.LIGHT)

  // 加载状态
  const loading = ref(false)

  // 月度数据
  const monthlyData = ref({
    months: [] as string[],
    visits: [] as number[]
  })

  // 收缩菜单时，重新计算图表大小
  watch(menuOpen, () => {
    const delays = [100, 200, 300]
    delays.forEach((delay) => {
      setTimeout(resize, delay)
    })
  })

  // 获取月度访问数据
  const fetchMonthlyStats = async () => {
    loading.value = true
    try {
      const response = await AnalyticsService.getMonthlyStats()
      if (response.success && response.data) {
        monthlyData.value = response.data
        createChart()
      }
    } catch (error) {
      console.error('获取月度访问数据失败:', error)
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchMonthlyStats()
  })

  onUnmounted(() => {
    removeResize()
  })

  const createChart = () => {
    // 如果没有数据，使用默认数据
    if (!monthlyData.value.months.length) {
      monthlyData.value = {
        months: [
          '1月',
          '2月',
          '3月',
          '4月',
          '5月',
          '6月',
          '7月',
          '8月',
          '9月',
          '10月',
          '11月',
          '12月'
        ],
        visits: [50, 25, 40, 20, 70, 35, 65, 30, 35, 20, 40, 44]
      }
    }

    setOptions({
      grid: {
        left: '2.2%',
        right: '3%',
        bottom: '0%',
        top: '5px',
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: monthlyData.value.months,
        axisLabel: {
          show: true,
          color: '#999',
          margin: 20,
          interval: 0,
          fontSize: 13
        },
        axisLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        },
        axisLine: {
          show: isLight.value ? true : false,
          lineStyle: {
            color: '#E8E8E8',
            width: 1
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: isLight.value ? '#e8e8e8' : '#444',
            width: 1,
            type: 'dashed'
          }
        }
      },
      series: [
        {
          name: '访客',
          color: getCssVariable('--main-color'),
          type: 'line',
          stack: '总量',
          data: monthlyData.value.visits,
          smooth: true,
          symbol: 'none',
          lineStyle: {
            width: 2.6
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: hexToRgba(getCssVariable('--el-color-primary'), 0.2).rgba
              },
              {
                offset: 1,
                color: hexToRgba(getCssVariable('--el-color-primary'), 0.01).rgba
              }
            ])
          }
        }
      ]
    })
  }
</script>

<style lang="scss" scoped>
  .region {
    position: relative;
    box-sizing: border-box;
    width: calc(58% - var(--console-margin));
    height: 420px;
    padding: 20px 0 30px;

    .card-header {
      padding: 0 18px !important;
    }

    .chart {
      position: relative;
      width: 100%;
      height: calc(100% - 80px);
      margin-top: 30px;
    }

    .loading-container {
      position: absolute;
      inset: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: rgb(255 255 255 / 70%);

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
</style>
