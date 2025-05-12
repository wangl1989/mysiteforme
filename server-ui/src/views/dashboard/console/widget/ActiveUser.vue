<template>
  <div class="region active-user art-custom-card">
    <div class="chart" ref="chartRef"></div>
    <div class="text">
      <h3 class="box-title">访问量概述</h3>
      <p class="subtitle">上方是当前系统最近9天的访问数据图标</p>
    </div>
    <div class="list">
      <div v-for="(item, index) in displayList" :key="index">
        <p>{{ item.num }}</p>
        <p class="subtitle">{{ item.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, computed } from 'vue'
  import * as echarts from 'echarts'
  import { useECharts } from '@/utils/echarts/useECharts'
  import { useSettingStore } from '@/store/modules/setting'
  import { getCssVariable } from '@/utils/colors'
  import type { PropType } from 'vue'

  // 定义接收的props类型
  interface ChartItem {
    dates: number[] | string[]
    values: number[]
  }

  interface ListItem {
    name: string
    num: string
  }

  // 定义组件的props
  const props = defineProps({
    chartData: {
      type: Object as PropType<ChartItem>,
      default: () => ({
        dates: [1, 2, 3, 4, 5, 6, 7, 8, 9],
        values: [160, 100, 150, 80, 190, 100, 175, 120, 160]
      })
    },
    listData: {
      type: Array as PropType<ListItem[]>,
      default: () => [
        { name: '总用户量', num: '32k' },
        { name: '总访问量', num: '128k' },
        { name: '总点击量', num: '1.2k' },
        { name: '平均访问时长', num: '20S' }
      ]
    }
  })

  const chartRef = ref<HTMLDivElement | null>(null)
  const { setOptions, removeResize, resize } = useECharts(chartRef as Ref<HTMLDivElement>)
  const settingStore = useSettingStore()
  const { menuOpen, isDark } = storeToRefs(settingStore)

  // 收缩菜单时，重新计算图表大小
  watch(menuOpen, () => {
    const delays = [100, 200, 300]
    delays.forEach((delay) => {
      setTimeout(resize, delay)
    })
  })

  // 使用计算属性处理显示的列表数据
  const displayList = computed(() => {
    return props.listData
  })

  const createChart = () => {
    setOptions({
      grid: {
        left: '0',
        right: '4%',
        bottom: '0%',
        top: '5px',
        containLabel: true
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1,
            type: 'dashed'
          }
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1
          }
        }
      },
      xAxis: {
        type: 'category',
        data: props.chartData.dates,
        boundaryGap: [0, 0.01],
        splitLine: {
          show: false
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1
          }
        },
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        }
      },
      series: [
        {
          data: props.chartData.values,
          type: 'bar',
          barMaxWidth: 36,
          itemStyle: {
            borderRadius: [6, 6, 6, 6],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: getCssVariable('--el-color-primary-light-4')
              },
              {
                offset: 1,
                color: getCssVariable('--el-color-primary')
              }
            ])
          }
        }
      ]
    })
  }

  // 监听chartData的变化，重新渲染图表
  watch(
    () => props.chartData,
    () => {
      createChart()
    },
    { deep: true }
  )

  onMounted(() => {
    createChart()
  })

  onUnmounted(() => {
    removeResize()
  })
</script>

<style lang="scss" scoped>
  .dark {
    .region {
      .chart {
        background: none;
      }
    }
  }

  .region {
    box-sizing: border-box;
    width: 42%;
    height: 420px;
    padding: 16px;

    .chart {
      box-sizing: border-box;
      width: 100%;
      height: 220px;
      padding: 20px 0 20px 20px;
      // 跟随系统主色
      // background-image: linear-gradient(
      //   90deg,
      //   var(--el-color-primary-light-1),
      //   var(--el-color-primary-light-3),
      //   var(--el-color-primary-light-1)
      // );
      border-radius: calc(var(--custom-radius) / 2 + 4px) !important;
    }

    .text {
      margin-left: 3px;

      h3 {
        margin-top: 20px;
        font-size: 18px;
        font-weight: 500;
      }

      p {
        margin-top: 5px;
        font-size: 14px;

        &:last-of-type {
          height: 42px;
          margin-top: 5px;
        }
      }
    }

    .list {
      display: flex;
      justify-content: space-between;
      margin-left: 3px;

      > div {
        flex: 1;

        p {
          font-weight: 400;

          &:first-of-type {
            font-size: 24px;
            color: var(--art-gray-900);
          }

          &:last-of-type {
            font-size: 13px;
          }
        }
      }
    }
  }

  @media screen and (max-width: $device-phone) {
    .dark {
      .active-user {
        .chart {
          padding: 15px 0 0 !important;
        }
      }
    }
  }
</style>
