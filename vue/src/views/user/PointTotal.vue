<template>
  <el-card class="chart-card">
    <h2>积分趋势图（近 30 天）</h2>
    <div ref="chartRef" class="chart-container"></div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const chartRef = ref(null)
let chartInstance = null

const renderChart = (xData, yData) => {
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: xData,
      boundaryGap: false
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '积分',
        type: 'line',
        data: yData,
        smooth: true,
        areaStyle: {
          color: '#409EFF20'
        },
        lineStyle: {
          color: '#409EFF'
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const loadChartData = async () => {
  try {
    const res = await axios.get('/api/points/my/recent', {
      params: { days: 30 },
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    const data = res.data || []

    const xData = data.map(item => item.createTime.split('T')[0]) // 日期
    const yData = data.map(item => item.points) // 每日积分

    await nextTick()
    renderChart(xData, yData)
  } catch (err) {
    ElMessage.error('加载积分图表失败')
  }
}

onMounted(() => {
  loadChartData()
})
</script>

<style scoped>
.chart-card {
  max-width: 1000px;
  margin: 30px auto;
}

.chart-container {
  width: 100%;
  height: 400px;
}
</style>