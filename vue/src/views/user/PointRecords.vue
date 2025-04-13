<template>
  <el-card class="point-card">
    <h2>我的积分记录</h2>

    <el-table :data="pointList" stripe style="width: 100%">
      <el-table-column prop="description" label="积分来源" />
      <el-table-column prop="points" label="积分" width="100" />
      <el-table-column label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import dayjs from "dayjs";

const pointList = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/points/my', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    pointList.value = res.data
  } catch (err) {
    ElMessage.error('加载积分记录失败')
  }
})

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
.point-card {
  max-width: 900px;
  margin: 40px auto;
}
</style>
