<template>
  <el-card class="user-card">
    <h2>用户管理</h2>

    <!-- 修改后的搜索栏 -->
    <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或邮箱"
          clearable
          style="width: 300px"
          @clear="fetchUsers"
      />
      <el-select
          v-model="selectedRole"
          placeholder="筛选角色"
          clearable
          style="width: 120px"
      >
        <el-option label="所有" value="" />
        <el-option label="用户" value="USER" />
        <el-option label="管理员" value="ADMIN" />
        <el-option label="超级管理员" value="SUPER_ADMIN" />
      </el-select>
      <el-button @click="() => fetchUsers(1)">搜索</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table :data="userList" stripe style="width: 100%">
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120" />
<!--      <el-table-column prop="totalPoints" label="总积分" width="100" />-->
<!--      <el-table-column prop="activityCount" label="活动数" width="100" />-->
      <el-table-column label="总积分" width="100">
        <template #default="{ row }">
          <span v-if="!['ADMIN', 'SUPER_ADMIN'].includes(row.role)">{{ row.totalPoints }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动数" width="100">
        <template #default="{ row }">
          <span v-if="!['ADMIN', 'SUPER_ADMIN'].includes(row.role)">{{ row.activityCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEditDialog(row)":disabled="row.role === 'SUPER_ADMIN'">编辑</el-button><el-button
            size="small"
            type="danger"
            @click="handleDelete(row.id)"
            :disabled="
              row.role === 'SUPER_ADMIN' ||
              (currentUserRole === 'ADMIN' && row.role !== 'USER') ||
              currentUserId === row.id"
        >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div style="margin-top: 20px; text-align: right">
      <el-pagination
          background
          layout="total, prev, pager, next"
          :page-size="pageSize"
          :current-page="currentPage"
          :total="total"
          @current-change="handlePageChange"
      />
    </div>

    <!-- 编辑用户弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑用户" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" :disabled="editForm.role === 'SUPER_ADMIN'" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
<!--        <el-form-item label="头像">-->
<!--          <el-input v-model="editForm.avatar" placeholder="头像 URL" />-->
<!--        </el-form-item>-->
        <el-form-item label="头像">
          <div style="display: flex; align-items: center; gap: 10px">
            <el-avatar :size="50" :src="editForm.avatar" />
            <el-button
                type="warning"
                @click="editForm.avatar = ''"
                :icon="Avatar"
            >
              重置为默认
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="总积分">
          <el-input-number v-model="editForm.totalPoints" :min="0" />
        </el-form-item>
        <el-form-item label="活动数">
          <el-input-number v-model="editForm.activityCount" :min="0" />
        </el-form-item>
        <el-form-item label="重置密码">
          <el-button
              type="warning"
              @click="handleResetPassword(editForm.id)"
              :disabled="editForm.role === 'SUPER_ADMIN'"
          >
            重置为12345678
          </el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-empty v-if="userList.length === 0" description="暂无用户数据" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const searchKeyword = ref('')
const dialogVisible = ref(false)
const editForm = ref({})
const currentUserRole = ref('')
const currentUserId = ref('') // 新增当前用户ID
import { Avatar } from '@element-plus/icons-vue'

// 修改后的fetchUsers函数
const fetchUsers = async (page = 1) => {
  currentPage.value = page
  try {
    const res = await axios.get('/api/users/page', {
      params: {
        pageNum: page,
        pageSize: pageSize,
        keyword: searchKeyword.value,
        ...(selectedRole.value ? { role: selectedRole.value } : {}) // 动态添加角色参数
      },
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    userList.value = res.data.content
    total.value = res.data.totalElements
  } catch (err) {
    ElMessage.error('加载用户失败')
    console.error(err)
  }
}


const handlePageChange = (page) => {
  fetchUsers(page)
}
const openEditDialog = (user) => {
  editForm.value = { ...user }
  dialogVisible.value = true
}

const submitEdit = async () => {
  try {
    // await axios.put(`/api/users/admin/${editForm.value.id}`, editForm.value, {
    //   headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    // })
    // 过滤掉不需要的字段，保持与后端接口一致
    const payload = {
      username: editForm.value.username, // 新增用户名字段
      email: editForm.value.email,
      avatar: editForm.value.avatar,
      role: editForm.value.role,
      totalPoints: editForm.value.totalPoints,
      activityCount: editForm.value.activityCount
    }

    await axios.put(`/api/users/admin/${editForm.value.id}`, payload, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })

    ElMessage.success('用户信息更新成功')
    dialogVisible.value = false
    fetchUsers()
  } catch (err) {
    ElMessage.error('更新失败：' + (err.response?.data?.message || '未知错误'))
  }
}

const fetchCurrentUser = async () => {
  try {
    const res = await axios.get('/api/users/me', {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    currentUserRole.value = res.data.role
    currentUserId.value = res.data.id
  } catch (err) {
    ElMessage.error('获取用户信息失败')
  }
}
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '警告', {
      type: 'warning'
    })
    await axios.delete(`/api/users/${id}`, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    ElMessage.success('用户已删除')
    fetchUsers()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
const selectedRole = ref('') // 新增角色筛选状态

const formatDate = (date) => dayjs(date).format('YYYY-MM-DD HH:mm:ss')

const handleResetPassword = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要重置密码为12345678吗？', '确认重置', {
      type: 'warning'
    })
    await axios.put(`/api/users/admin/${userId}/reset-password`, {}, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    ElMessage.success('密码已重置为12345678')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('重置失败：' + (err.response?.data?.message || '未知错误'))
    }
  }
}
onMounted(() => {
  fetchUsers()
  fetchCurrentUser() // 新增调用
})
</script>

<style scoped>
.user-card {
  max-width: 1100px;
  margin: 30px auto;
}
</style>

