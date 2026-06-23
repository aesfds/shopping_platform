<script setup>
import { House } from '@element-plus/icons-vue'
</script>

<template>
  <router-link class="home-back-link" to="/">
    <el-icon><House /></el-icon>
    <span>返回首页</span>
  </router-link>
</template>

<style scoped>
.home-back-link {
  position: fixed;
  top: 16px;
  left: 16px;
  z-index: 50;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  padding: 8px 12px;
  color: #0f7f6a;
  font-size: 14px;
  font-weight: 800;
  text-decoration: none;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #d7e5df;
  border-radius: 8px;
  box-shadow: 0 8px 22px rgba(21, 51, 44, 0.08);
  backdrop-filter: blur(8px);
}

.home-back-link:hover {
  color: #fff;
  background: #1f8a70;
  border-color: #1f8a70;
}
</style>
