<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Goods, Search, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCategories, getProductList } from '../api/products'
import { addCartItem } from '../api/cart'
import HomeBackLink from '../components/HomeBackLink.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const categories = ref([])
const products = ref([])
const total = ref(0)

const filters = reactive({
  keyword: '',
  categoryId: '',
  sort: 'recommend',
  page: 1,
  size: 8
})

const activeCategoryName = computed(() => {
  const active = categories.value.find((item) => item.id === Number(filters.categoryId))
  return active?.name || '全部商品'
})

watch(
  () => route.query,
  () => {
    filters.keyword = String(route.query.keyword || '')
    filters.categoryId = route.query.category ? Number(route.query.category) : ''
    filters.sort = String(route.query.sort || 'recommend')
    filters.page = Number(route.query.page || 1)
    loadProducts()
  },
  { immediate: true }
)

onMounted(async () => {
  categories.value = await getCategories()
})

async function loadProducts() {
  loading.value = true
  try {
    const result = await getProductList(filters)
    products.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function updateQuery(extra = {}) {
  const next = {
    keyword: filters.keyword || undefined,
    category: filters.categoryId || undefined,
    sort: filters.sort !== 'recommend' ? filters.sort : undefined,
    page: extra.page || 1
  }

  router.push({ path: '/products', query: next })
}

function selectCategory(categoryId) {
  filters.categoryId = categoryId
  updateQuery()
}

function changePage(page) {
  filters.page = page
  updateQuery({ page })
}

async function addToCart(product) {
  if (!ensureLogin()) {
    return
  }

  try {
    await addCartItem(product, 1)
    ElMessage.success('已加入购物车')
  } catch (error) {
    handleCartError(error)
  }
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function ensureLogin() {
  if (sessionStorage.getItem('token')) {
    return true
  }

  ElMessage.warning('请先登录后再加入购物车')
  router.push({
    path: '/login',
    query: { redirect: route.fullPath }
  })
  return false
}

function handleCartError(error) {
  if (error.message === '请先登录') {
    ensureLogin()
    return
  }

  ElMessage.error(error.message || '加入购物车失败')
}
</script>

<template>
  <main class="catalog-page">
    <section class="catalog-header">
      <div>
        <HomeBackLink />
        <router-link class="brand" to="/">
          <el-icon><Goods /></el-icon>
          <span>Shopping Platform</span>
        </router-link>
        <h1>{{ activeCategoryName }}</h1>
        <p>浏览全部商品，按分类、关键词和排序快速筛选。</p>
      </div>

      <div class="header-actions">
        <router-link to="/cart">
          <el-button type="primary" :icon="ShoppingCart">购物车</el-button>
        </router-link>
      </div>
    </section>

    <section class="catalog-toolbar">
      <el-input
        v-model="filters.keyword"
        :prefix-icon="Search"
        placeholder="搜索商品名称或卖点"
        size="large"
        @keyup.enter="updateQuery()"
      />

      <el-select v-model="filters.sort" size="large" @change="updateQuery()">
        <el-option label="推荐排序" value="recommend" />
        <el-option label="销量优先" value="sales" />
        <el-option label="价格从低到高" value="priceAsc" />
        <el-option label="价格从高到低" value="priceDesc" />
      </el-select>

      <el-button size="large" type="primary" @click="updateQuery()">筛选</el-button>
    </section>

    <section class="catalog-layout">
      <aside class="category-sidebar">
        <button
          type="button"
          class="category-button"
          :class="{ active: !filters.categoryId }"
          @click="selectCategory('')"
        >
          <span>全部商品</span>
          <el-icon><ArrowRight /></el-icon>
        </button>

        <button
          v-for="category in categories"
          :key="category.id"
          type="button"
          class="category-button"
          :class="{ active: filters.categoryId === category.id }"
          @click="selectCategory(category.id)"
        >
          <span>{{ category.name }}</span>
          <el-icon><ArrowRight /></el-icon>
        </button>
      </aside>

      <div class="product-area" v-loading="loading">
        <div v-if="products.length" class="product-grid">
          <article v-for="product in products" :key="product.id" class="product-card">
            <router-link
              class="product-media"
              :to="`/products/${product.id}`"
              :style="product.imageUrl ? { backgroundImage: `url(${product.imageUrl})` } : null"
            >
              <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
            </router-link>

            <div class="product-body">
              <router-link :to="`/products/${product.id}`">
                <h2>{{ product.name }}</h2>
              </router-link>
              <p>{{ product.subtitle }}</p>

              <div class="price-line">
                <strong>￥{{ formatPrice(product.price) }}</strong>
                <span v-if="product.originalPrice">￥{{ formatPrice(product.originalPrice) }}</span>
              </div>

              <div class="product-foot">
                <span>已售 {{ product.sales }}</span>
                <el-button text type="primary" @click="addToCart(product)">
                  加入购物车
                </el-button>
              </div>
            </div>
          </article>
        </div>

        <el-empty v-else description="没有找到匹配商品">
          <el-button type="primary" @click="selectCategory('')">查看全部</el-button>
        </el-empty>

        <div v-if="total > filters.size" class="pagination-row">
          <el-pagination
            background
            layout="prev, pager, next"
            :page-size="filters.size"
            :current-page="filters.page"
            :total="total"
            @current-change="changePage"
          />
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.catalog-page {
  min-height: 100vh;
  padding: 28px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.catalog-header,
.catalog-toolbar,
.catalog-layout {
  width: min(1180px, 100%);
  margin: 0 auto;
}

.catalog-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
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

.catalog-header h1 {
  margin: 0;
  color: #183c35;
  font-size: 36px;
  letter-spacing: 0;
}

.catalog-header p {
  margin: 10px 0 0;
  color: #6d7e78;
}

.catalog-toolbar {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) 190px auto;
  gap: 12px;
  margin-bottom: 18px;
}

.catalog-layout {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}

.category-sidebar {
  display: grid;
  gap: 8px;
  padding: 12px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.category-button {
  display: flex;
  min-height: 44px;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  color: #334743;
  font: inherit;
  background: transparent;
  border: 0;
  border-radius: 6px;
  cursor: pointer;
}

.category-button:hover,
.category-button.active {
  color: #0f7f6a;
  background: #eef7f3;
}

.product-area {
  min-height: 400px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.product-card {
  overflow: hidden;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.product-media {
  position: relative;
  display: block;
  aspect-ratio: 1 / 0.76;
  background:
    linear-gradient(135deg, #eef3f0, #dbe7e1),
    repeating-linear-gradient(45deg, transparent 0 16px, rgba(255, 255, 255, 0.36) 16px 32px);
  background-position: center;
  background-size: cover;
}

.product-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 9px;
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  background: #d95d39;
  border-radius: 999px;
}

.product-body {
  padding: 14px;
}

.product-body a {
  color: inherit;
  text-decoration: none;
}

.product-body h2 {
  margin: 0;
  overflow: hidden;
  color: #1d312d;
  font-size: 16px;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-body p {
  min-height: 40px;
  margin: 8px 0 12px;
  color: #71827d;
  font-size: 13px;
  line-height: 1.5;
}

.price-line,
.product-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.price-line strong {
  color: #d23f31;
  font-size: 20px;
}

.price-line span,
.product-foot {
  color: #8a9995;
  font-size: 13px;
}

.price-line span {
  text-decoration: line-through;
}

.product-foot {
  margin-top: 8px;
}

.pagination-row {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 1000px) {
  .catalog-layout {
    grid-template-columns: 1fr;
  }

  .category-sidebar {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .catalog-page {
    padding: 18px 12px;
  }

  .catalog-header,
  .catalog-toolbar {
    grid-template-columns: 1fr;
  }

  .catalog-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .category-sidebar,
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
