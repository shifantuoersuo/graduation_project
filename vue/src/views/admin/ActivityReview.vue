<template>
  <el-card class="review-card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2>环保活动审核</h2>
      <el-button icon="Clock" @click="$router.push('/admin/activity-history')">查看历史记录</el-button>
    </div>
    <el-table :data="pendingActivities" stripe style="width: 100%">
      <el-table-column prop="title" label="活动标题" width="180" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 80px; height: 80px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="提交人" width="120" />

      <el-table-column prop="createTime" label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>

      <!-- 新增积分输入列 -->
      <el-table-column label="积分" width="150">
        <template #default="{ row }">
          <el-input
              v-model.number="row.points"
              type="number"
              :min="0"
              placeholder="输入积分"
              style="width: 100px"
              :rules="[{ required: true, message: '积分不能为空' }]"
          />
        </template>
      </el-table-column>
      <el-table-column label="审核操作" width="200">
        <template #default="{ row }">
          <el-button type="success" @click="approveActivity(row.id, row.points)">通过</el-button>
          <el-button type="danger" @click="rejectActivity(row.id)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="pendingActivities.length === 0" description="暂无待审核活动" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from "dayjs";
import { useRouter } from 'vue-router'

const router = useRouter()


const pendingActivities = ref([])

// 获取待审核的活动列表
const loadPendingActivities = async () => {
  try {
    const res = await axios.get('/api/activities/pending', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    pendingActivities.value = res.data
  } catch (err) {
    ElMessage.error('加载待审核活动失败')
  }
}

// 通过活动
const approveActivity = async (id, points) => {
  try {
    await axios.post(`/api/activities/review/${id}`, {
      status: 'APPROVED',
      points: points || 0,
      reason: ''
    }, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    ElMessage.success('已通过该活动')
    loadPendingActivities()
  } catch (err) {
    ElMessage.error('审核失败')
  }
}

// 拒绝活动
const rejectActivity = async (id) => {
  ElMessageBox.prompt('请输入拒绝理由', '拒绝审核', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /^.{3,}$/,
    inputErrorMessage: '拒绝理由至少3个字符'
  }).then(({ value }) => {
    return axios.post(`/api/activities/review/${id}`, {
      status: 'REJECTED',
      points: 0,
      reason: value  // ✅ 添加拒绝理由
    }, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
  }).then(() => {
    ElMessage.success('已拒绝该活动')
    loadPendingActivities()
  }).catch(() => {
    ElMessage.info('取消操作')
  })
}

const formatDate = (date) => {
  try {  // 如果只需要显示日期，可以使用 'YYYY-MM-DD'
    return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
  }catch (err) {
    console.error(err)
    return ''
  }

}

onMounted(() => {
  loadPendingActivities()
})
</script>


<style scoped>
.review-card {
  max-width: 1100px;
  margin: 40px auto;
}
</style>
