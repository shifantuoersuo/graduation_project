<template>
  <el-card class="profile-card">
    <h2 style="margin-bottom: 20px;">个人资料设置</h2>
    <el-tabs type="border-card">
      <!-- 基本信息修改 -->
      <el-tab-pane label="基本信息">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" label-position="top">
          <el-form-item label="头像" prop="avatar">
            <el-upload
                class="avatar-uploader"
                action="/api/upload/image"
                :show-file-list="false"
                :headers="headers"
                name="file"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
            >
              <img v-if="form.avatar" :src="form.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username"/>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 密码修改 -->
      <el-tab-pane label="修改密码">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" label-position="top">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitPassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const originalUsername = ref('')
const formRef = ref()
const passwordFormRef = ref()

const form = ref({
  username: '',
  email: '',
  avatar: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const headers = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

const rules = {
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_, value) => {
        if (value !== passwordForm.value.newPassword) {
          return Promise.reject('两次密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await axios.get('/api/users/me', { headers })
    form.value = res.data
    originalUsername.value = res.data.username // 保存原始用户名
    localStorage.setItem('username', res.data.username)
  } catch {
    ElMessage.error('加载用户信息失败')
  }
}

// 上传头像成功
const handleAvatarSuccess = (res) => {
  form.value.avatar = res.url
  ElMessage.success('头像上传成功')
}

const handleAvatarError = (err) => {
  ElMessage.error(`上传失败: ${err.message}`)
}

// 提交基本信息修改
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await axios.put('/api/users/update', form.value, { headers })

      // 检查用户名是否修改
      if (form.value.username !== originalUsername.value) {
        ElMessage.success('用户名已修改，请重新登录')
        localStorage.clear()
        location.href = '/login'
      } else {
        ElMessage.success('信息已更新')
      }
    }catch {
      ElMessage.error(err.response?.data?.message || '修改失败')// 后端返回的错误信息
    }
  })
}

// 提交密码修改
const submitPassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await axios.post('/api/users/update-password', passwordForm.value, { headers })
      ElMessage.success('密码修改成功，请重新登录')
      localStorage.clear()
      location.href = '/login'
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '修改失败')
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>


<style scoped>
.profile-card {
  max-width: 700px;
  margin: 40px auto;
  padding: 20px;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
}
</style>
