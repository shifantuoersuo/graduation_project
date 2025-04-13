<template>
  <el-card class="activity-card">
    <h2>我的环保活动记录</h2>

    <el-table :data="activities" stripe style="width: 100%">
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 80px; height: 80px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="120">
        <template #default="{ row }">
          <el-tag
              :type="statusColor(row.status)"
              effect="plain"
          >
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拒绝理由" width="200">
        <template #default="{ row }">
          <el-tooltip v-if="row.status === 'REJECTED'" :content="row.rejectReason" placement="top">
            <span>{{ row.rejectReason?.slice(0, 10) || '无' }}...</span>
          </el-tooltip>
        </template>
      </el-table-column>

    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const activities = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/activities/my', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    activities.value = res.data
  } catch (err) {
    ElMessage.error('获取活动记录失败')
  }
})

// 状态文字
const statusText = (status) => {
  switch (status) {
    case 'PENDING': return '审核中'
    case 'APPROVED': return '已通过'
    case 'REJECTED': return '已拒绝'
    default: return '未知状态'
  }
}

// 状态颜色
const statusColor = (status) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'APPROVED': return 'success'
    case 'REJECTED': return 'danger'
    default: return ''
  }
}
// 日期格式化函数
const formatDate = (date) => {
  try {  // 如果只需要显示日期，可以使用 'YYYY-MM-DD'
    return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
  }catch (err) {
    console.error(err)
    return ''
  }
}

</script>

<style scoped>
.activity-card {
  max-width: 1000px;
  margin: 40px auto;
}
</style>
