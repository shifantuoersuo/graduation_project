<template>
  <el-card class="dashboard">
    <h2>欢迎回来，{{ username }} 👋</h2>
    <p>当前积分：<strong style="font-size: 20px; color: #67C23A;">{{ totalPoints }}</strong></p>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <h3>积分趋势（最近7天）</h3>
<!--          <div ref="chartRef" class="chart" />-->
          <div v-if="hasChartData" ref="chartRef" class="chart" />
          <el-empty v-else description="快去获取积分叭！" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <h3>快捷操作</h3>
          <el-space wrap>
            <el-button type="primary" @click="go('/user/activity/submit')">提交活动</el-button>
            <el-button @click="go('/user/points/records')">查看积分</el-button>
            <el-button type="success" @click="go('/user/rewards/list')">兑换奖励</el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')
const totalPoints = ref(0)
const chartRef = ref(null)

//新增
const hasChartData = ref(true) // 新增状态标识

const loadDashboardData = async () => {
  try {
    const res = await axios.get('/api/users/me', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    username.value = res.data.username
    totalPoints.value = res.data.totalPoints || 0
  } catch (err) {
    console.error('获取用户信息失败')
  }

  try {
    const res = await axios.get('/api/points/my/recent', {
      params: { days: 7 },
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    renderChart(res.data)
    hasChartData.value = res.data.length > 0
  } catch (err) {
    console.error('加载积分趋势失败')
  }
}

const renderChart = (data) => {
  const chart = echarts.init(chartRef.value)
  const option = {
    xAxis: {
      type: 'category',
      data: data.map(item => item.createTime.split('T')[0])//格式化日期
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: data.map(item => item.points),
      type: 'line',
      smooth: true,
      areaStyle: {}
    }]
  }
  chart.setOption(option)
}

const go = (path) => {
  router.push(path)
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  max-width: 1000px;
  margin: 30px auto;
}
.chart {
  width: 100%;
  height: 300px;
}
</style>
