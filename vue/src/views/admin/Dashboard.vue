<template>
  <el-card class="dashboard-card">
    <h2>系统数据总览</h2>

    <!-- 统计数据 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-statistic title="用户总数" :value="stats.userCount" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="总活动数" :value="stats.activityCount" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="待审核活动" :value="stats.pendingActivities" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="积分总发放量" :value="stats.totalPoints" />
      </el-col>
    </el-row>

    <el-divider />

    <!-- ECharts 图表 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <h3>活动提交趋势</h3>
          <div ref="activityChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h3>积分增耗趋势</h3>
          <div ref="pointsChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const stats = ref({
  userCount: 0,
  activityCount: 0,
  pendingActivities: 0,
  totalPoints: 0
})

const activityChart = ref(null)
const pointsChart = ref(null)
let activityChartInstance = null
let pointsChartInstance = null

const loadDashboardData = async () => {
  try {
    const res = await axios.get('/api/users/dashboard', {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    stats.value = res.data
    console.log('赋值后的 stats：', stats.value)
    await nextTick()
    renderCharts(res.data.activityTrends, res.data.pointsTrends)
  } catch (err) {
    ElMessage.error('加载统计数据失败')
  }
}

const renderCharts = (activityData, pointsData) => {

  // 提取横轴日期 & 数值
  const activityDates = activityData.map(item => item.date)
  const activityCounts = activityData.map(item => item.count)

  const pointsDates = pointsData.map(item => item.date)
  const pointsValues = pointsData.map(item => item.points)

  // 活动提交趋势图
  if (!activityChartInstance) {
    activityChartInstance = echarts.init(activityChart.value)
  }
  activityChartInstance.setOption({
    // title: { text: '活动提交趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: activityDates },
    yAxis: { type: 'value' },
    series: [{ name: '活动数', data: activityCounts, type: 'line', smooth: true }]
  })

  // 积分发放趋势图
  if (!pointsChartInstance) {
    pointsChartInstance = echarts.init(pointsChart.value)
  }
  pointsChartInstance.setOption({
    // title: { text: '积分发放趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: pointsDates },
    yAxis: { type: 'value' },
    series: [{ name: '积分', data: pointsValues, type: 'bar', color: '#409EFF' }]
  })
}


onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-card {
  max-width: 1100px;
  margin: 30px auto;
}
.chart-container {
  width: 100%;
  height: 300px;
}
</style>
