<template>
  <el-card class="reward-card">
    <h2>奖励管理</h2>
    <el-button type="primary" @click="openAddDialog" style="margin-bottom: 15px;">添加奖励</el-button>

    <el-table :data="rewards" stripe style="width: 100%">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="cost" label="所需积分" width="120" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 80px; height: 80px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteReward(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑奖励对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑奖励' : '添加奖励'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="所需积分" prop="cost">
          <el-input v-model.number="form.cost" type="number" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input v-model.number="form.stock" type="number" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
              action="/api/upload/image"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleImageUpload"
          >
            <el-button>上传图片</el-button>
            <span v-if="form.imageUrl" style="margin-left: 10px; color: green;">✔ 已上传</span>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const rewards = ref([])
const dialogVisible = ref(false)
const isEditing = ref(false)
const formRef = ref()

const form = ref({
  id: null,
  name: '',
  description: '',
  cost: 0,
  stock: 0,
  imageUrl: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  cost: [{ required: true, type: 'number', message: '请输入所需积分', trigger: 'blur' }],
  stock: [{ required: true, type: 'number', message: '请输入库存', trigger: 'blur' }]
}

const headers = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

const loadRewards = async () => {
  try {
    const res = await axios.get('/api/rewards/all', { headers })
    rewards.value = res.data
  } catch (err) {
    ElMessage.error('加载奖励列表失败')
  }
}

const openAddDialog = () => {
  isEditing.value = false
  form.value = { id: null, name: '', description: '', cost: 0, stock: 0, imageUrl: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEditing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleImageUpload = (res) => {
  form.value.imageUrl = res.url
  ElMessage.success('图片上传成功')
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (isEditing.value) {
        await axios.put(`/api/rewards/update/${form.value.id}`, form.value, { headers })
        ElMessage.success('修改成功')
      } else {
        await axios.post('/api/rewards/create', form.value, { headers })
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadRewards()
    } catch (err) {
      ElMessage.error('保存失败')
    }
  })
}

const deleteReward = (id) => {
  ElMessageBox.confirm('确认要删除该奖励吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await axios.delete(`/api/rewards/delete/${id}`, { headers })
    ElMessage.success('删除成功')
    loadRewards()
  }).catch(() => {})
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
</style>
