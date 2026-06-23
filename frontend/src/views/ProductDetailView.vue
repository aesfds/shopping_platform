<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Goods, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '../api/products'
import { addCartItem } from '../api/cart'
import HomeBackLink from '../components/HomeBackLink.vue'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)

const discount = computed(() => {
  if (!product.value?.originalPrice) return ''
  return `${Math.round((product.value.price / product.value.originalPrice) * 10)} 折`
})

onMounted(loadProduct)

async function loadProduct() {
  loading.value = true
  try {
    product.value = await getProductDetail(route.params.id)
  } catch (error) {
    ElMessage.error(error.message || '商品不存在')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

async function handleAddCart() {
  if (!ensureLogin()) {
    return false
  }

  try {
    await addCartItem(product.value, quantity.value)
    ElMessage.success('已加入购物车')
    return true
  } catch (error) {
    handleCartError(error)
    return false
  }
}

async function buyNow() {
  if (await handleAddCart()) {
    router.push('/cart')
  }
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function ensureLogin() {
  if (sessionStorage.getItem('token')) {
    return true
  }

  ElMessage.warning('请先登录后再购买')
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

  ElMessage.error(error.message || '操作失败')
}
</script>

<template>
  <main class="detail-page" v-loading="loading">
    <section class="detail-shell" v-if="product">
      <HomeBackLink />
      <div class="breadcrumb-row">
        <el-button text :icon="ArrowLeft" @click="router.push('/products')">返回商品列表</el-button>
        <span>/</span>
        <span>{{ product.name }}</span>
      </div>

      <section class="product-detail">
        <div class="product-gallery">
          <div
            class="main-image"
            :style="product.imageUrl ? { backgroundImage: `url(${product.imageUrl})` } : null"
          >
            <span v-if="product.tag">{{ product.tag }}</span>
          </div>
          <div class="thumb-row">
            <button type="button"></button>
            <button type="button"></button>
            <button type="button"></button>
          </div>
        </div>

        <div class="detail-info">
          <div class="category-line">
            <el-icon><Goods /></el-icon>
            <span>品质好物</span>
          </div>
          <h1>{{ product.name }}</h1>
          <p class="subtitle">{{ product.subtitle }}</p>

          <div class="price-panel">
            <div>
              <span>到手价</span>
              <strong>￥{{ formatPrice(product.price) }}</strong>
            </div>
            <del v-if="product.originalPrice">￥{{ formatPrice(product.originalPrice) }}</del>
            <em v-if="discount">{{ discount }}</em>
          </div>

          <div class="metric-row">
            <div>
              <strong>{{ product.sales }}</strong>
              <span>累计销量</span>
            </div>
            <div>
              <strong>{{ product.stock }}</strong>
              <span>库存</span>
            </div>
            <div>
              <strong>48h</strong>
              <span>发货时效</span>
            </div>
          </div>

          <div class="spec-block">
            <h2>商品特点</h2>
            <div class="spec-list">
              <span v-for="spec in product.specs" :key="spec">{{ spec }}</span>
            </div>
          </div>

          <div class="description-block">
            <h2>商品说明</h2>
            <p>{{ product.description }}</p>
          </div>

          <div class="purchase-row">
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock"
              size="large"
            />
            <el-button size="large" :icon="ShoppingCart" @click="handleAddCart">
              加入购物车
            </el-button>
            <el-button size="large" type="primary" @click="buyNow">立即购买</el-button>
          </div>
        </div>
      </section>

      <section v-if="product.related?.length" class="related-section">
        <div class="section-title">
          <h2>同类推荐</h2>
          <router-link to="/products">查看更多</router-link>
        </div>

        <div class="related-grid">
          <router-link
            v-for="item in product.related"
            :key="item.id"
            class="related-item"
            :to="`/products/${item.id}`"
          >
            <div class="related-image"></div>
            <strong>{{ item.name }}</strong>
            <span>￥{{ formatPrice(item.price) }}</span>
          </router-link>
        </div>
      </section>
    </section>
  </main>
</template>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 28px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.detail-shell {
  width: min(1180px, 100%);
  margin: 0 auto;
}

.breadcrumb-row {
  display: flex;
  min-height: 44px;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  color: #6d7e78;
}

.product-detail {
  display: grid;
  grid-template-columns: minmax(320px, 42%) minmax(0, 1fr);
  gap: 28px;
  align-items: start;
}

.product-gallery,
.detail-info,
.related-section {
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.product-gallery {
  padding: 16px;
}

.main-image {
  position: relative;
  aspect-ratio: 1 / 0.95;
  background:
    linear-gradient(135deg, #eef3f0, #dbe7e1),
    repeating-linear-gradient(45deg, transparent 0 18px, rgba(255, 255, 255, 0.4) 18px 36px);
  background-position: center;
  background-size: cover;
  border-radius: 8px;
}

.main-image span {
  position: absolute;
  top: 14px;
  left: 14px;
  padding: 5px 10px;
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  background: #d95d39;
  border-radius: 999px;
}

.thumb-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 12px;
}

.thumb-row button {
  aspect-ratio: 1 / 0.72;
  background: #eef4f1;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.detail-info {
  padding: 28px;
}

.category-line {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #0f8f75;
  font-weight: 800;
}

.detail-info h1 {
  margin: 14px 0 10px;
  color: #183c35;
  font-size: 34px;
  letter-spacing: 0;
}

.subtitle {
  margin: 0 0 18px;
  color: #6d7e78;
  font-size: 16px;
  line-height: 1.7;
}

.price-panel {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px;
  background: #fff5f1;
  border: 1px solid #f1d7cc;
  border-radius: 8px;
}

.price-panel div {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-panel span {
  color: #9a5544;
  font-size: 13px;
}

.price-panel strong {
  color: #d23f31;
  font-size: 34px;
}

.price-panel del {
  color: #a4aaa7;
}

.price-panel em {
  padding: 4px 8px;
  color: #fff;
  font-style: normal;
  font-weight: 800;
  background: #d95d39;
  border-radius: 999px;
}

.metric-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin: 18px 0;
  border: 1px solid #e3ebe6;
  border-radius: 8px;
}

.metric-row div {
  display: grid;
  min-height: 72px;
  place-items: center;
  gap: 4px;
}

.metric-row div + div {
  border-left: 1px solid #e3ebe6;
}

.metric-row strong {
  color: #183c35;
  font-size: 20px;
}

.metric-row span,
.description-block p {
  color: #71827d;
}

.spec-block h2,
.description-block h2,
.section-title h2 {
  margin: 0 0 12px;
  color: #183c35;
  font-size: 20px;
  letter-spacing: 0;
}

.spec-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.spec-list span {
  padding: 8px 12px;
  color: #24443d;
  background: #eef7f3;
  border-radius: 8px;
}

.description-block {
  margin-top: 18px;
}

.description-block p {
  margin: 0;
  line-height: 1.8;
}

.purchase-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.related-section {
  margin-top: 22px;
  padding: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title a {
  color: #0f8f75;
  font-weight: 800;
  text-decoration: none;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.related-item {
  display: grid;
  gap: 8px;
  color: #1f2d2a;
  text-decoration: none;
}

.related-image {
  aspect-ratio: 1 / 0.68;
  background: linear-gradient(135deg, #edf3f0, #dae6e0);
  border-radius: 8px;
}

.related-item span {
  color: #d23f31;
  font-weight: 900;
}

@media (max-width: 900px) {
  .product-detail {
    grid-template-columns: 1fr;
  }

  .related-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .detail-page {
    padding: 18px 12px;
  }

  .detail-info {
    padding: 20px;
  }

  .detail-info h1 {
    font-size: 28px;
  }

  .metric-row,
  .related-grid {
    grid-template-columns: 1fr;
  }

  .metric-row div + div {
    border-top: 1px solid #e3ebe6;
    border-left: 0;
  }
}
</style>
