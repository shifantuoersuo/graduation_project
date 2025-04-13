<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">环保积分激励系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button link @click="$router.push('/register')">去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formRef = ref()

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const res = await axios.post('/api/users/login', loginForm.value)
      const { token,role } = res.data

      // 保存token和角色到localStorage
      localStorage.setItem('token', token)
      localStorage.setItem('userRole', role)

      ElMessage.success('登录成功')

      if (role === 'USER') {
        router.push('/user/dashboard')
      } else {
        router.push('/admin/dashboard')
      }
    } catch (err) {
      ElMessage.error('登录失败，请检查用户名或密码')
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("@/assets/imgs/bg1.jpg") no-repeat right center fixed;
  background-size: cover;
  position: relative;
}
/* 添加半透明遮罩增强文字可读性 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.2);
  z-index: 0;
}
.login-card {
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
  color: #2c3e50; /* 加深标题颜色 */
}
</style>
<!--.login-card {-->
<!--width: 400px;-->
<!--padding: 30px;-->
<!--}-->

<!--.login-container {-->
<!--display: flex;-->
<!--justify-content: center;-->
<!--align-items: center;-->
<!--height: 100vh;-->
<!--background-color: #f5f7fa;-->
<!--}-->