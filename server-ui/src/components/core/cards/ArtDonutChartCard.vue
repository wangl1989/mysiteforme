<template>
  <div class="donut-chart-card art-custom-card" :style="{ height: `${height}rem` }">
    <div class="card-body">
      <div class="card-content">
        <div class="data-section">
          <p class="title">{{ title }}</p>
          <div>
            <p class="value">{{ formatNumber(value) }}</p>
            <div class="percentage" :class="{ 'is-increase': percentage > 0 }">
              占总{{ title }} {{ percentage }}%
            </div>
          </div>
        </div>
        <div class="chart-section">
          <div ref="chartRef" class="chart-container"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useChart, useChartOps } from '@/composables/useChart'
  import { EChartsOption } from 'echarts'
  const { chartRef, isDark, initChart, updateChart } = useChart()

  interface Props {
    value: number
    title: string
    percentage: number
    currentYear?: string
    previousYear?: string
    height?: number
    color?: string | [string, string]
    radius?: [string, string]
    data: [number, number]
  }

  const props = withDefaults(defineProps<Props>(), {
    value: 0,
    title: '',
    percentage: 0,
    currentYear: 'Active',
    previousYear: 'Inactive',
    height: 9,
    color: '',
    radius: () => ['70%', '90%'],
    data: () => [0, 100]
  })

  function getCssVariableValue(cssVar: string): string {
    if (typeof window === 'undefined' || !cssVar) return '#000000'
    if (cssVar.startsWith('var(')) {
      const varName = cssVar.match(/--[\w-]+/)?.[0]
      if (varName) {
        try {
          return getComputedStyle(document.documentElement).getPropertyValue(varName).trim()
        } catch (e) {
          console.warn(`Failed to parse CSS variable: ${cssVar}`, e)
          return '#000000'
        }
      }
    }
    return cssVar
  }

  const formatNumber = (num: number) => {
    return num.toLocaleString()
  }

  const options: () => EChartsOption = () => {
    let activeColorValue: string
    let inactiveColorValue: string

    if (Array.isArray(props.color) && props.color.length === 2) {
      activeColorValue = props.color[0]
      inactiveColorValue = props.color[1]
    } else if (typeof props.color === 'string' && props.color) {
      activeColorValue = getCssVariableValue(props.color)
      inactiveColorValue = getCssVariableValue('var(--el-fill-color-lighter)')
    } else {
      activeColorValue = getCssVariableValue(useChartOps().themeColor)
      inactiveColorValue = getCssVariableValue('var(--el-fill-color-lighter)')
    }

    activeColorValue =
      activeColorValue && activeColorValue !== '#000000' ? activeColorValue : '#409EFF'
    inactiveColorValue =
      inactiveColorValue && inactiveColorValue !== '#000000' ? inactiveColorValue : '#f5f7fa'

    if (Array.isArray(props.color) && props.color.length === 2) {
      if (props.color[0] === '#000000') activeColorValue = '#000000'
      if (props.color[1] === '#000000') inactiveColorValue = '#000000'
    } else if (typeof props.color === 'string' && props.color === '#000000') {
      activeColorValue = '#000000'
    }

    return {
      series: [
        {
          type: 'pie',
          radius: props.radius,
          avoidLabelOverlap: false,
          label: {
            show: false
          },
          data: [
            {
              value: props.data[0],
              name: props.currentYear,
              itemStyle: { color: activeColorValue }
            },
            {
              value: props.data[1],
              name: props.previousYear,
              itemStyle: { color: inactiveColorValue }
            }
          ]
        }
      ]
    }
  }

  watch(isDark, () => {
    return initChart(options())
  })

  watch(
    [() => props.data, () => props.color],
    () => {
      updateChart(options())
    },
    { deep: true }
  )

  onMounted(() => {
    return initChart(options())
  })
</script>

<style lang="scss" scoped>
  .donut-chart-card {
    overflow: hidden;
    color: #303133;
    background-color: var(--art-main-bg-color);
    border-radius: var(--custom-radius);
    transition: 0.3s;

    .card-body {
      box-sizing: border-box;
      display: flex;
      height: 100%;
      padding: 20px;
    }

    .card-content {
      display: flex;
      gap: 20px;
      align-items: flex-start;
      width: 100%;
    }

    .data-section {
      display: flex;
      flex: 1;
      flex-direction: column;
      justify-content: space-between;
      height: 85%;
    }

    .chart-section {
      display: flex;
      flex: 1;
      align-items: center;
      max-width: 200px;
      height: 100%;
    }

    .title {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
      line-height: 1.2;
      color: var(--art-text-gray-900);
    }

    .value {
      margin: 0;
      margin-top: 10px;
      font-size: 20px;
      font-weight: 500;
      line-height: 1.2;
      color: var(--art-text-gray-900);
    }

    .percentage {
      margin-top: 5px;
      font-size: 12px;
      font-weight: 500;
      color: #f56c6c;

      &.is-increase {
        color: #67c23a;
      }
    }

    .chart-container {
      width: 100%;
      height: 120px;
    }

    .chart-legend {
      display: flex;
      gap: 16px;
      margin-top: 8px;
      font-size: 12px;
      color: #909399;

      .legend-item {
        position: relative;
        padding-left: 16px;

        &::before {
          position: absolute;
          top: 50%;
          left: 0;
          width: 8px;
          height: 8px;
          content: '';
          border-radius: 50%;
          transform: translateY(-50%);
        }

        &.current::before {
          background-color: var(--main-color);
        }

        &.previous::before {
          background-color: #e6e8f7;
        }
      }
    }
  }
</style>
