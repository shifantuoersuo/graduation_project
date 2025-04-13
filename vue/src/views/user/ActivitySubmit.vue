<template>
  <el-card class="form-card">
    <h2>提交环保活动</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入活动标题" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入活动内容或说明"
            rows="4"
        />
      </el-form-item>

      <el-form-item label="上传图片" prop="imageUrl">
        <el-upload
            class="upload-demo"
            action="/api/upload/image"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
        >
          <el-button type="primary">点击上传</el-button>
          <span v-if="form.imageUrl" class="image-text">已上传 ✔</span>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm">提交活动</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  title: '',
  description: '',
  imageUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }]
}

const formRef = ref()

// 设置 JWT 令牌作为上传请求头
const headers = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

// 上传成功回调
const handleUploadSuccess = (res) => {
  form.value.imageUrl = res.url
  ElMessage.success('图片上传成功')
}

// 表单提交
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await axios.post('/api/activities/submit', form.value, {
        headers: {
          Authorization: 'Bearer ' + localStorage.getItem('token')
        }
      })
      ElMessage.success('提交成功，等待审核')
      router.push('/user/activity/my') // 跳转到“我的活动”页面
    } catch (err) {
      ElMessage.error('提交失败')
    }
  })
}

// 重置表单
const resetForm = () => {
  form.value = {
    title: '',
    description: '',
    imageUrl: ''
  }
  formRef.value.clearValidate()
}
</script>

<style scoped>
.form-card {
  max-width: 600px;
  margin: 40px auto;
}

.image-text {
  margin-left: 12px;
  color: #67c23a;
}
</style>
