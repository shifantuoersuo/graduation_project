<template>
  <el-card class="reward-card">
    <h2>积分奖励商城</h2>
    <el-row :gutter="20">
      <el-col v-for="item in rewardList" :key="item.id" :span="6">
        <el-card shadow="hover" class="reward-item">
          <el-image
              style="width: 100%; height: 160px"
              :src="item.imageUrl || '/default.png'"
              fit="cover"
          />
          <div class="reward-info">
            <h3>{{ item.name }}</h3>
            <p class="desc">{{ item.description }}</p>
            <p>所需积分：<strong style="color: #f56c6c">{{ item.cost }}</strong></p>
            <p>剩余库存：{{ item.stock }}</p>
          </div>
          <el-button
              type="primary"
              :disabled="item.stock <= 0"
              @click="redeem(item.id)"
          >
            立即兑换
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const rewardList = ref([])

const loadRewards = async () => {
  try {
    const res = await axios.get('/api/rewards/all', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    rewardList.value = res.data
  } catch (err) {
    ElMessage.error('加载奖励列表失败')
  }
}

const redeem = async (id) => {
  try {
    await axios.post(`/api/rewards/redeem/${id}`, {}, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    ElMessage.success('兑换成功！可前往兑换记录查看')
    loadRewards() // 重新加载库存
  } catch (err) {
    const msg = err.response?.data?.message || '兑换失败'
    ElMessage.error(msg)
  }
}

onMounted(() => {
  loadRewards()
})
</script>

<style scoped>
.reward-card {
  max-width: 1100px;
  margin: 30px auto;
}

.reward-item {
  margin-bottom: 20px;
}

.reward-info {
  margin: 12px 0;
}

.desc {
  font-size: 14px;
  color: #666;
  margin: 4px 0;
}
</style>
