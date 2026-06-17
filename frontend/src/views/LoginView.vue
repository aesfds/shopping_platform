<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User, Shop } from '@element-plus/icons-vue'
import { login } from '../api/auth'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ]
}

const formRef = ref()

async function submit() {
  await formRef.value.validate()

  loading.value = true
  try {
    const result = await login(form)

    if (result?.token) {
      localStorage.setItem('token', result.token)
      localStorage.setItem('user', JSON.stringify(result.user || {}))
    }

    ElMessage.success('登录成功')
    router.push('/')
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
        <h1>欢迎回来</h1>
        <p>登录后可继续查看购物车、订单和会员权益。</p>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-box">
        <h2>账号登录</h2>
        <p>使用手机号或邮箱登录</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="账号" prop="account">
            <el-input
                v-model="form.account"
                :prefix-icon="User"
                size="large"
                placeholder="手机号 / 邮箱"
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

        <div class="auth-switch">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
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