<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User, Shop } from '@element-plus/icons-vue'
import { login } from '../api/auth'
import { adminLogin } from '../api/admin'
import { useAuthState } from '../stores/authState'
import { useAdminState } from '../stores/adminState'

const router = useRouter()
const route = useRoute()
const { setAuth, clearAuth } = useAuthState()
const { setAdminAuth, clearAdminAuth } = useAdminState()
const loading = ref(false)
const loginMode = ref(route.query.mode === 'admin' ? 'admin' : 'user')

const form = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ]
}

const formRef = ref()
const modeOptions = [
  { label: '用户登录', value: 'user' },
  { label: '管理员登录', value: 'admin' }
]
const title = computed(() => (loginMode.value === 'admin' ? '管理员登录' : '账号登录'))
const subtitle = computed(() => (
  loginMode.value === 'admin' ? '使用管理员账号进入后台' : '使用手机号、邮箱或用户名登录'
))
const accountPlaceholder = computed(() => (
  loginMode.value === 'admin' ? '管理员账号 admin' : '手机号 / 邮箱 / 用户名'
))

async function submit() {
  await formRef.value.validate()

  loading.value = true
  try {
    if (loginMode.value === 'admin') {
      const result = await adminLogin({
        username: form.account,
        password: form.password
      })

      clearAuth()
      setAdminAuth(result.token, result.admin || {})
      ElMessage.success('管理员登录成功')
      router.push(route.query.redirect || '/admin')
      return
    }

    const result = await login(form)

    if (result?.token) {
      clearAdminAuth()
      setAuth(result.token, result.user || {})
    }

    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-visual">
      <router-link class="brand" to="/">
        <el-icon><Shop /></el-icon>
        <span>Shopping Platform</span>
      </router-link>

      <div class="visual-copy">
        <h1>{{ loginMode === 'admin' ? '后台管理' : '欢迎回来' }}</h1>
        <p>{{ loginMode === 'admin' ? '查询用户、订单并处理发货。' : '登录后可继续查看购物车、订单和会员权益。' }}</p>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-box">
        <el-radio-group v-model="loginMode" class="login-mode">
          <el-radio-button
            v-for="option in modeOptions"
            :key="option.value"
            :label="option.value"
          >
            {{ option.label }}
          </el-radio-button>
        </el-radio-group>

        <h2>{{ title }}</h2>
        <p>{{ subtitle }}</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="账号" prop="account">
            <el-input
                v-model="form.account"
                :prefix-icon="User"
                size="large"
                :placeholder="accountPlaceholder"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
                v-model="form.password"
                :prefix-icon="Lock"
                size="large"
                type="password"
                show-password
                placeholder="请输入密码"
                @keyup.enter="submit"
            />
          </el-form-item>

          <el-button
              class="submit-button"
              type="primary"
              size="large"
              :loading="loading"
              @click="submit"
          >
            登录
          </el-button>
        </el-form>

        <div v-if="loginMode === 'user'" class="auth-switch">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
        <div v-else class="admin-tip">
          管理员账号由系统预设，不开放网页注册。
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  display: grid;
  min-height: 100vh;
  grid-template-columns: minmax(360px, 42%) 1fr;
  background: #f5f7f6;
}

.auth-visual {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 36px;
  color: #fff;
  background: linear-gradient(145deg, #1f8a70, #123c35);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 22px;
  font-weight: 800;
  text-decoration: none;
}

.visual-copy h1 {
  margin: 0;
  font-size: 48px;
  letter-spacing: 0;
}

.visual-copy p {
  max-width: 360px;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.8;
}

.auth-panel {
  display: grid;
  place-items: center;
  padding: 32px;
}

.auth-box {
  width: min(420px, 100%);
  padding: 36px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.auth-box h2 {
  margin: 0;
  color: #183c35;
  font-size: 28px;
}

.login-mode {
  width: 100%;
  margin-bottom: 22px;
}

.login-mode :deep(.el-radio-button) {
  flex: 1;
}

.login-mode :deep(.el-radio-button__inner) {
  width: 100%;
}

.auth-box > p {
  margin: 8px 0 28px;
  color: #70817b;
}

.submit-button {
  width: 100%;
  margin-top: 8px;
}

.auth-switch {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 22px;
  color: #70817b;
}

.auth-switch a {
  color: #0f8f75;
  font-weight: 800;
  text-decoration: none;
}

.admin-tip {
  margin-top: 22px;
  color: #70817b;
  font-size: 14px;
  text-align: center;
}

@media (max-width: 760px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-visual {
    min-height: 220px;
  }

  .visual-copy h1 {
    font-size: 36px;
  }
}
</style>
