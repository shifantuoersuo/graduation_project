<template>
  <el-card class="record-card">
    <h2>我的兑换记录</h2>

    <el-table :data="records" stripe style="width: 100%">
      <el-table-column prop="rewardName" label="奖励名称" />
      <el-table-column prop="cost" label="消耗积分" width="120" />
      <el-table-column prop="redeemTime" label="兑换时间" width="220" />
    </el-table>

    <el-empty v-if="records.length === 0" description="暂无兑换记录" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const records = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/rewards/records', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    records.value = res.data
  } catch (err) {
    ElMessage.error('加载兑换记录失败')
  }
})
</script>

<style scoped>
.record-card {
  max-width: 900px;
  margin: 40px auto;
}
</style>
