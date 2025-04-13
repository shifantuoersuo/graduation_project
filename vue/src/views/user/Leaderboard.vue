<template>
  <el-card class="chart-card">
    <h2>积分排行榜</h2>
    <div ref="chartRef" class="chart-container"></div>
    <div v-if="currentUserRank" class="user-rank">
      <span class="trophy">🏆</span>
      您的当前排名: <strong>{{ currentUserRank }}</strong>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const chartRef = ref(null)
const currentUserRank = ref(null)
let chartInstance = null

const renderChart = (xData, yData) => {
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const option = {
    title: {
      // text: '用户积分排行榜',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '积分',
        type: 'bar',
        data: yData,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const loadChartData = async () => {
  try {
    const res = await axios.get('/api/points/top', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    const data = res.data || []

    if (data.length === 0) {
      ElMessage.warning('暂无排行榜数据');
      return;
    }

    // 从API响应中查找当前用户
    const currentUser = data.find(item => item.isCurrent)
    if (currentUser) {
      currentUserRank.value = currentUser.rank
    }

    await nextTick()
    renderChart(data.map(item => item.username), data.map(item => item.totalPoints))
  } catch (err) {
    ElMessage.error('加载排行榜失败')
  }
}
onMounted(() => {
  loadChartData()
})
</script>

<style scoped>
.user-rank {
  margin-top: 20px;
  padding: 15px 25px;
  background: #f0f9eb;
  border-radius: 25px;
  font-size: 18px;
  color: #67C23A;
  text-align: center;
  display: inline-block;
  box-shadow: 0 2px 12px rgba(103, 194, 58, 0.1);
  border: 1px solid #e1f3d8;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
}

.user-rank strong {
  font-size: 24px;
  margin: 0 8px;
  color: #5daf34;
}

.trophy {
  margin-right: 10px;
  vertical-align: -2px;
}

.chart-card {
  max-width: 1000px;
  margin: 30px auto;
}

.chart-container {
  width: 100%;
  height: 500px;
}
</style>
