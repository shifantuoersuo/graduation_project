<template>
  <el-container style="height: 100vh">
    <!-- 左侧菜单 -->
    <el-aside width="245px" class="aside">
      <div class="logo">管理员中心</div>
      <el-menu
          :default-active="$route.path"
          router
          class="el-menu-vertical-demo"
          background-color="#2d3a4b"
          text-color="#fff"
          active-text-color="#409EFF"
          style="padding: 12px"
      >
        <el-menu-item index="/admin/dashboard">系统总览</el-menu-item>
        <el-menu-item index="/admin/activity-review">活动审核</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/rewards-manage">奖励管理</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体内容 -->
    <el-container>
      <el-header class="header" style="height: 72px">
        <div class="admin-greeting">欢迎你，管理员</div>
        <el-button type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main style="background-color: #f5f7fa">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const logout = () => {
  localStorage.clear()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.aside {
  background-color: #1f2d3d;
}
.logo {
  color: white;
  font-size: 24px;
  font-weight: bold;
  padding: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #1f2d3d;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #1f2d3d;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.el-menu-vertical-demo {
  border-right: none;     /* 移除边框保持统一 */
}
.admin-greeting {
  color: white;
}
</style>
