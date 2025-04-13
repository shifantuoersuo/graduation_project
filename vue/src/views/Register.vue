<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱（可选）" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button type="text" @click="$router.push('/login')">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

const formRef = ref()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, value) => {
        return value === form.value.password
      },
      message: '两次输入的密码不一致',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur',
      required: false
    }
  ]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await axios.post('/api/users/register', {
        username: form.value.username,
        password: form.value.password,
        email: form.value.email
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '注册失败')
      console.error(err)
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("@/assets/imgs/bg.jpg") no-repeat right center fixed;
  background-size: cover;
  position: relative;
}
/* 添加半透明遮罩增强文字可读性 */
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.2);
  z-index: 0;
}
.register-card {
  width: 400px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.9); /* 添加半透明白色背景 */
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37); /* 增强阴影 */
  border-radius: 10px;
  position: relative;
  z-index: 1;
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
</style>
<!--.register-container {-->
<!--display: flex;-->
<!--justify-content: center;-->
<!--align-items: center;-->
<!--height: 100vh;-->
<!--background-color: #f5f7fa;-->
<!--}-->

<!--.register-card {-->
<!--width: 450px;-->
<!--padding: 30px;-->
<!--}-->

