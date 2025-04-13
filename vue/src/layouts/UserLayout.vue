<template>
  <el-container class="nature-layout">
    <!-- 侧边菜单 -->
    <el-aside width="245px" style="background: linear-gradient(145deg, #f5ffec 0%, #e3f5d6 100%)">
<!--      //220px-->
      <div class="logo">环保积分激励系统</div>
      <el-menu
          :default-active="$route.path"
          router
          class="el-menu-vertical-demo"
          background-color="#f5ffec"
          text-color="#3a6351"
          active-text-color="#4a9d63">

        <el-menu-item index="/user/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>用户首页</span>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title><el-icon><Edit /></el-icon>活动</template>
          <el-menu-item index="/user/activity/submit">提交活动</el-menu-item>
          <el-menu-item index="/user/activity/my">我的活动</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title><el-icon><Coin /></el-icon>积分</template>
          <el-menu-item index="/user/points/records">积分明细</el-menu-item>
          <el-menu-item index="/user/points/total">我的积分</el-menu-item>
          <el-menu-item index="/user/points/leaderboard">排行榜</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title><el-icon><Present /></el-icon>奖励</template>
          <el-menu-item index="/user/rewards/list">奖励兑换</el-menu-item>
          <el-menu-item index="/user/rewards/my-records">我的兑换记录</el-menu-item>
        </el-sub-menu>

      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="greeting">
          <el-icon class="leaf-icon"><svg>...</svg></el-icon>
          <span>你好，{{ username }}！今天也要环保生活哦 🌱</span>
        </div>

        <el-dropdown>
    <span class="el-dropdown-link">
      <!-- 展示头像和名字-->
      <el-avatar
          :size="32"
          :src="avatarUrl"
          icon="User"
          style="margin-right: 8px"
      />
      {{ username }}
      <el-icon style="margin-left: 8px"><arrow-down /></el-icon>
    </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goProfile">个人资料</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 页面内容区域 -->
      <el-main style="background-color: #f5f7fa">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { HomeFilled, Edit, Coin,  Present, User } from '@element-plus/icons-vue'
import axios from "axios";

const router = useRouter()
const username = ref('用户')
const avatarUrl = ref('')  // 用户头像地址

onMounted(async () => {
  try {
    const res = await axios.get('/api/users/me', {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    })
    username.value = res.data.username
    avatarUrl.value = res.data.avatar || ''  // 没有头像时留空
    localStorage.setItem('username', res.data.username)
  } catch (error) {
    ElMessage.error('用户信息加载失败')
  }
})
const goProfile = () => {
  router.push('/user/profile')
}


const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.nature-layout {
  background: #f8fcf5;
  height: 100vh;
}

.logo {
  line-height: 24px; /* 新增行高设置 */
  color: #3a6351;
  font-size: 24px;
  font-family: 'Microsoft YaHei';
  padding: 24px;
  background: linear-gradient(145deg, #f5ffec 0%, #e3f5d6 100%);
  box-shadow: 0 2px 8px rgba(58,99,81,0.1);
}
.header {
  height: 72px; /* 24px(padding-top) + 24px(font-size) + 24px(padding-bottom) */
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(145deg, #e3f5d6 0%, #d0ebc0 100%);
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.greeting {
  color: #3a6351;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.leaf-icon {
  color: #6aaf73;
  font-size: 24px;
}
.header-left {
  font-size: 16px;
  font-weight: bold;
}
.el-menu-vertical-demo{
  border-right: none;
  padding: 12px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  font-weight: 500;
  color: #333;
}
</style>
