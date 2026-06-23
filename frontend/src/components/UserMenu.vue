<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Bell,
  ShoppingCart,
  SwitchButton,
  Tickets,
  User
} from '@element-plus/icons-vue'
import { getProfile, logout as logoutApi } from '../api/auth'
import { useAuthState } from '../stores/authState'

const router = useRouter()
const route = useRoute()
const { user, isLoggedIn, setUser, clearAuth, syncAuth } = useAuthState()

const displayName = computed(() => user.value?.username || '我的账号')
const avatarUrl = computed(() => user.value?.avatarUrl || '')

onMounted(refreshProfile)

async function refreshProfile() {
  syncAuth()

  if (!isLoggedIn.value) {
    return
  }

  try {
    setUser(await getProfile())
  } catch (error) {
    // 请求拦截器会处理 401，这里只保持菜单不打断页面渲染。
  }
}

function goLogin() {
  router.push({
    path: '/login',
    query: route.fullPath === '/' ? {} : { redirect: route.fullPath }
  })
}

async function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
    return
  }

  if (command === 'orders') {
    router.push('/orders')
    return
  }

  if (command === 'cart') {
    router.push('/cart')
    return
  }

  if (command === 'messages') {
    ElMessage.info('消息中心后续接入')
    return
  }

  if (command === 'logout') {
    try {
      await logoutApi()
    } catch (error) {
      // 本地退出优先，服务端会在 token 失效时自动拒绝后续请求。
    }

    clearAuth()
    ElMessage.success('已退出登录')

    if (route.meta.requiresAuth) {
      router.push('/login')
    }
  }
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<template>
  <el-button
    v-if="!isLoggedIn"
    class="login-button"
    :icon="User"
    @click="goLogin"
  >
    登录
  </el-button>

  <el-dropdown
    v-else
    trigger="click"
    popper-class="user-menu-popper"
    @command="handleCommand"
  >
    <button class="user-trigger" type="button">
      <el-avatar :size="30" :src="avatarUrl">
        <el-icon><User /></el-icon>
      </el-avatar>
      <span>{{ displayName }}</span>
    </button>

    <template #dropdown>
      <el-dropdown-menu>
        <div class="dropdown-profile">
          <el-avatar :size="44" :src="avatarUrl">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div>
            <strong>{{ displayName }}</strong>
            <span>{{ user?.email || user?.phone || '已登录' }}</span>
            <span>余额 ￥{{ formatPrice(user?.balance) }}</span>
          </div>
        </div>
        <el-dropdown-item command="profile" :icon="User">个人主页</el-dropdown-item>
        <el-dropdown-item command="orders" :icon="Tickets">订单</el-dropdown-item>
        <el-dropdown-item command="cart" :icon="ShoppingCart">购物车</el-dropdown-item>
        <el-dropdown-item command="messages" :icon="Bell">消息</el-dropdown-item>
        <el-dropdown-item divided command="logout" :icon="SwitchButton">
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
.login-button,
.user-trigger {
  height: 42px;
  border-radius: 8px;
  font-weight: 800;
}

.login-button {
  color: #20332f;
  background: #eef4f1;
  border-color: #d9e5df;
}

.user-trigger {
  display: inline-flex;
  max-width: 172px;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  color: #20332f;
  font: inherit;
  background: #eef4f1;
  border: 1px solid #d9e5df;
  cursor: pointer;
}

.user-trigger:hover,
.login-button:hover {
  color: #fff;
  background: #1f8a70;
  border-color: #1f8a70;
}

.user-trigger span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

<style>
.user-menu-popper .el-dropdown-menu {
  min-width: 210px;
}

.user-menu-popper .dropdown-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px 12px;
  border-bottom: 1px solid #edf2ef;
}

.user-menu-popper .dropdown-profile div {
  display: grid;
  min-width: 0;
  gap: 3px;
}

.user-menu-popper .dropdown-profile strong,
.user-menu-popper .dropdown-profile span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-menu-popper .dropdown-profile strong {
  color: #183c35;
}

.user-menu-popper .dropdown-profile span {
  color: #71827d;
  font-size: 12px;
}
</style>
