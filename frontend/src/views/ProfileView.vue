<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Camera, Shop, Tickets, User } from '@element-plus/icons-vue'
import { getProfile, recharge, updateProfile, uploadAvatar } from '../api/auth'
import HomeBackLink from '../components/HomeBackLink.vue'
import { useAuthState } from '../stores/authState'

const { setUser } = useAuthState()
const formRef = ref()
const avatarInputRef = ref()
const loading = ref(false)
const saving = ref(false)
const uploadingAvatar = ref(false)
const profile = ref(null)

const form = reactive({
  username: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 40, message: '用户名长度为 2-40 个字符', trigger: 'blur' }
  ]
}

const accountText = computed(() => {
  if (!profile.value) {
    return ''
  }

  return profile.value.email || profile.value.phone || `用户 ID：${profile.value.id}`
})
const balanceText = computed(() => formatPrice(profile.value?.balance))

onMounted(loadProfile)

async function loadProfile() {
  loading.value = true
  try {
    const data = await getProfile()
    profile.value = data
    form.username = data.username || ''
    setUser(data)
  } catch (error) {
    ElMessage.error(error.message || '获取用户资料失败')
  } finally {
    loading.value = false
  }
}

async function submit() {
  await formRef.value.validate()

  saving.value = true
  try {
    const data = await updateProfile({
      username: form.username
    })
    profile.value = data
    setUser(data)
    ElMessage.success('资料已保存')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function openAvatarPicker() {
  avatarInputRef.value?.click()
}

async function uploadSelectedAvatar(event) {
  const file = event.target.files?.[0]
  event.target.value = ''

  if (!file) {
    return
  }
  if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)) {
    ElMessage.error('头像仅支持 JPG、PNG、GIF、WEBP 格式')
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('头像不能超过 2MB')
    return
  }

  uploadingAvatar.value = true
  try {
    const data = await uploadAvatar(file)
    profile.value = data
    setUser(data)
    ElMessage.success('头像已更新')
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    uploadingAvatar.value = false
  }
}

async function openRecharge() {
  try {
    const { value } = await ElMessageBox.prompt('请输入充值金额', '账户充值', {
      confirmButtonText: '充值',
      cancelButtonText: '取消',
      inputType: 'number',
      inputPlaceholder: '例如 100',
      inputValidator(value) {
        const amount = Number(value)
        return Number.isFinite(amount) && amount > 0 ? true : '请输入大于 0 的金额'
      }
    })

    const data = await recharge(Number(value).toFixed(2))
    profile.value = data
    setUser(data)
    ElMessage.success('充值成功')
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }

    ElMessage.error(error.message || '充值失败')
  }
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<template>
  <main class="profile-page">
    <HomeBackLink />

    <section class="page-header">
      <router-link class="brand" to="/">
        <el-icon><Shop /></el-icon>
        <span>Shopping Platform</span>
      </router-link>
      <h1>个人主页</h1>
      <p>管理账号展示信息。</p>
    </section>

    <section class="profile-layout" v-loading="loading">
      <aside class="profile-summary">
        <button
          class="avatar-button"
          type="button"
          aria-label="上传头像"
          :disabled="uploadingAvatar"
          @click="openAvatarPicker"
        >
          <el-avatar :size="96" :src="profile?.avatarUrl">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="avatar-overlay" v-loading="uploadingAvatar">
            <el-icon><Camera /></el-icon>
          </span>
        </button>
        <input
          ref="avatarInputRef"
          class="avatar-input"
          type="file"
          accept="image/jpeg,image/png,image/gif,image/webp"
          @change="uploadSelectedAvatar"
        />
        <strong>{{ form.username || '我的账号' }}</strong>
        <span>{{ accountText }}</span>
        <div class="balance-box">
          <el-icon><Tickets /></el-icon>
          <div>
            <span>账户余额</span>
            <strong>￥{{ balanceText }}</strong>
          </div>
        </div>
        <el-button type="primary" @click="openRecharge">充值</el-button>
      </aside>

      <section class="profile-panel">
        <div class="panel-heading">
          <div>
            <h2>基础资料</h2>
            <p>保持头像和昵称与你想展示的身份一致。</p>
          </div>
          <el-icon><Camera /></el-icon>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              :prefix-icon="User"
              maxlength="40"
              show-word-limit
              size="large"
              placeholder="请输入用户名"
            />
          </el-form-item>

          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="submit">保存资料</el-button>
          </div>
        </el-form>
      </section>
    </section>
  </main>
</template>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 28px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.page-header,
.profile-layout {
  width: min(980px, 100%);
  margin: 0 auto;
}

.page-header {
  margin-bottom: 22px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
  color: #183c35;
  font-size: 20px;
  font-weight: 800;
  text-decoration: none;
}

.brand .el-icon {
  width: 38px;
  height: 38px;
  color: #fff;
  background: #1f8a70;
  border-radius: 8px;
}

.page-header h1 {
  margin: 0;
  color: #183c35;
  font-size: 36px;
  letter-spacing: 0;
}

.page-header p {
  margin: 10px 0 0;
  color: #6d7e78;
}

.profile-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}

.profile-summary,
.profile-panel {
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.profile-summary {
  display: grid;
  min-height: 330px;
  place-items: center;
  gap: 10px;
  padding: 24px;
  text-align: center;
}

.profile-summary strong {
  max-width: 100%;
  overflow: hidden;
  color: #183c35;
  font-size: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-summary span {
  max-width: 100%;
  overflow: hidden;
  color: #71827d;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-button {
  position: relative;
  display: inline-grid;
  width: 104px;
  height: 104px;
  place-items: center;
  padding: 0;
  color: inherit;
  background: transparent;
  border: 0;
  border-radius: 50%;
  cursor: pointer;
}

.avatar-button:disabled {
  cursor: wait;
}

.avatar-button:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  inset: 4px;
  display: grid;
  place-items: center;
  color: #fff;
  background: rgba(15, 38, 33, 0.48);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.18s ease;
}

.avatar-overlay .el-icon {
  font-size: 24px;
}

.avatar-input {
  display: none;
}

.balance-box {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  padding: 14px;
  text-align: left;
  background: #f8faf8;
  border: 1px solid #e4ede8;
  border-radius: 8px;
}

.balance-box .el-icon {
  width: 36px;
  height: 36px;
  flex: 0 0 auto;
  color: #0f8f75;
  background: #eef7f3;
  border-radius: 8px;
}

.balance-box div {
  display: grid;
  min-width: 0;
  gap: 4px;
}

.balance-box span {
  font-size: 13px;
}

.balance-box strong {
  color: #d23f31;
  font-size: 20px;
}

.profile-panel {
  padding: 24px;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 22px;
}

.panel-heading h2 {
  margin: 0;
  color: #183c35;
  font-size: 24px;
}

.panel-heading p {
  margin: 8px 0 0;
  color: #71827d;
  line-height: 1.6;
}

.panel-heading > .el-icon {
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  color: #0f8f75;
  background: #eef7f3;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

@media (max-width: 760px) {
  .profile-page {
    padding: 18px 12px;
  }

  .profile-layout {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
