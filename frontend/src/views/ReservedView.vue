<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title || '页面')
</script>

<template>
  <main class="reserved-page">
    <router-link class="reserved-brand" to="/">
      <el-icon><Shop /></el-icon>
      <span>Shopping Platform</span>
    </router-link>

    <section class="reserved-panel">
      <el-empty :description="`${title}待开发`">
        <router-link to="/">
          <el-button type="primary">返回首页</el-button>
        </router-link>
      </el-empty>
    </section>
  </main>
</template>

<style scoped>
.reserved-page {
  min-height: 100vh;
  padding: 28px;
  background: #f5f7f6;
}

.reserved-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #16362f;
  font-size: 20px;
  font-weight: 800;
  text-decoration: none;
}

.reserved-panel {
  display: grid;
  min-height: calc(100vh - 120px);
  place-items: center;
}
</style>
