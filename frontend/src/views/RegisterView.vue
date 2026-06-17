<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Message, Phone, User, Shop } from '@element-plus/icons-vue'
import { register } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

function validateConfirmPassword(rule, value, callback) {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }

  callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2 到 20 位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function submit() {
  await formRef.value.validate()

  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel">
      <div class="auth-box">
        <router-link class="brand" to="/">
          <el-icon><Shop /></el-icon>
          <span>Shopping Platform</span>
        </router-link>

        <h2>创建账号</h2>
        <p>注册后即可使用购物车和订单功能</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :prefix-icon="User" size="large" />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" :prefix-icon="Message" size="large" />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" :prefix-icon="Phone" size="large" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" :prefix-icon="Lock" size="large" type="password" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
                v-model="form.confirmPassword"
                :prefix-icon="Lock"
                size="large"
                type="password"
                show-password
                @keyup.enter="submit"
            />
          </el-form-item>

          <el-button class="submit-button" type="primary" size="large" :loading="loading" @click="submit">
            注册
          </el-button>
        </el-form>

        <div class="auth-switch">
          <span>已有账号？</span>
          <router-link to="/login">去登录</router-link>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: #f5f7f6;
}

.auth-panel {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 32px;
}

.auth-box {
  width: min(460px, 100%);
  padding: 34px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 26px;
  color: #183c35;
  font-size: 22px;
  font-weight: 800;
  text-decoration: none;
}

.auth-box h2 {
  margin: 0;
  color: #183c35;
  font-size: 28px;
}

.auth-box > p {
  margin: 8px 0 24px;
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
</style>