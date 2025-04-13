<template>
  <el-card class="history-card">
    <div class="header">
      <h2>已审核活动记录</h2>
      <el-button icon="ArrowLeft" @click="goBack">返回审核页面</el-button>
    </div>

    <el-table :data="historyActivities" stripe>
      <el-table-column prop="title" label="活动标题" width="180" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 80px; height: 80px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="提交人" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : 'danger'">
            {{ row.status === 'APPROVED' ? '已通过' : '已拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="80" />
      <el-table-column prop="updateTime" label="审核时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="拒绝理由" width="200">
        <template #default="{ row }">
          <span v-if="row.status === 'REJECTED'">{{ row.rejectReason || '无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="warning" size="small" @click="undoReview(row.id)">撤销</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="historyActivities.length === 0" description="暂无审核记录" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const historyActivities = ref([])
const router = useRouter()

const loadHistoryActivities = async () => {
  try {
    const res = await axios.get('/api/activities/history', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    historyActivities.value = res.data
  } catch (err) {
    ElMessage.error('加载历史活动失败')
  }
}

const undoReview = async (id) => {
  ElMessageBox.confirm('确定要将该活动撤销为“待审核”状态吗？', '确认撤销', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.post(`/api/activities/review/${id}`, {
          status: 'PENDING',
          points: 0,
          reason: ''
        },{
        headers: {
          Authorization: 'Bearer ' + localStorage.getItem('token')
        }
      })
      ElMessage.success('已撤销该活动')
      console.log(status)
      loadHistoryActivities()
    } catch (err) {
      ElMessage.error('撤销失败')
    }
  })
}

const goBack = () => {
  router.push('/admin/activity-review')
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadHistoryActivities()
})
</script>

<style scoped>
.history-card {
  max-width: 1400px;
  margin: 40px auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
