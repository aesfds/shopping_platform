<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Delete, Goods, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getCartItems,
  removeCartItem,
  removeSelectedCartItems,
  updateCartItem
} from '../api/cart'
import { createOrder } from '../api/orders'
import HomeBackLink from '../components/HomeBackLink.vue'

const router = useRouter()
const loading = ref(false)
const items = ref([])

const selectedItems = computed(() => items.value.filter((item) => item.selected))
const selectedCount = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalAmount = computed(() => selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const allSelected = computed({
  get() {
    return items.value.length > 0 && items.value.every((item) => item.selected)
  },
  set(value) {
    items.value.forEach((item) => {
      item.selected = value
      updateCartItem(item.productId, { selected: value })
    })
  }
})

onMounted(loadCart)

async function loadCart() {
  loading.value = true
  try {
    items.value = await getCartItems()
  } finally {
    loading.value = false
  }
}

async function saveItem(item) {
  items.value = await updateCartItem(item.productId, {
    quantity: item.quantity,
    selected: item.selected
  })
}

async function removeItem(item) {
  items.value = await removeCartItem(item.productId)
  ElMessage.success('已移除商品')
}

async function clearSelected() {
  if (!selectedItems.value.length) {
    ElMessage.warning('请选择要删除的商品')
    return
  }

  items.value = await removeSelectedCartItems()
  ElMessage.success('已删除选中商品')
}

async function checkout() {
  if (!selectedItems.value.length) {
    ElMessage.warning('请先选择商品')
    return
  }

  await createOrder()
  ElMessage.success('订单已创建')
  router.push('/orders')
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<template>
  <main class="cart-page">
    <section class="page-header">
      <div>
        <HomeBackLink />
        <router-link class="brand" to="/">
          <el-icon><ShoppingCart /></el-icon>
          <span>Shopping Platform</span>
        </router-link>
        <h1>购物车</h1>
        <p>管理已选商品，调整数量并准备结算。</p>
      </div>

      <router-link to="/products">
        <el-button :icon="Goods">继续购物</el-button>
      </router-link>
    </section>

    <section class="cart-layout" v-loading="loading">
      <div class="cart-main">
        <div v-if="items.length" class="cart-toolbar">
          <el-checkbox v-model="allSelected">全选</el-checkbox>
          <el-button text type="danger" :icon="Delete" @click="clearSelected">
            删除选中
          </el-button>
        </div>

        <div v-if="items.length" class="cart-list">
          <article v-for="item in items" :key="item.productId" class="cart-item">
            <el-checkbox v-model="item.selected" @change="saveItem(item)" />

            <router-link class="item-image" :to="`/products/${item.productId}`">
              <span v-if="item.tag">{{ item.tag }}</span>
            </router-link>

            <div class="item-info">
              <router-link :to="`/products/${item.productId}`">
                <h2>{{ item.name }}</h2>
              </router-link>
              <p>{{ item.subtitle }}</p>
              <span>库存 {{ item.stock }}</span>
            </div>

            <div class="item-price">
              <strong>￥{{ formatPrice(item.price) }}</strong>
              <del v-if="item.originalPrice">￥{{ formatPrice(item.originalPrice) }}</del>
            </div>

            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="item.stock"
              @change="saveItem(item)"
            />

            <div class="item-total">￥{{ formatPrice(item.price * item.quantity) }}</div>

            <el-button text type="danger" :icon="Delete" @click="removeItem(item)" />
          </article>
        </div>

        <el-empty v-else description="购物车还是空的">
          <el-button type="primary" @click="router.push('/products')">去逛逛</el-button>
        </el-empty>
      </div>

      <aside class="summary-panel">
        <h2>结算信息</h2>
        <div class="summary-row">
          <span>已选数量</span>
          <strong>{{ selectedCount }}</strong>
        </div>
        <div class="summary-row">
          <span>商品合计</span>
          <strong>￥{{ formatPrice(totalAmount) }}</strong>
        </div>
        <el-button type="primary" size="large" @click="checkout">去结算</el-button>
      </aside>
    </section>
  </main>
</template>

<style scoped>
.cart-page {
  min-height: 100vh;
  padding: 28px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.page-header,
.cart-layout {
  width: min(1180px, 100%);
  margin: 0 auto;
}

.page-header {
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

.cart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 18px;
  align-items: start;
}

.cart-main,
.summary-panel {
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.cart-main {
  min-height: 420px;
}

.cart-toolbar {
  display: flex;
  min-height: 56px;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px;
  border-bottom: 1px solid #e6ece8;
}

.cart-list {
  display: grid;
}

.cart-item {
  display: grid;
  grid-template-columns: 32px 96px minmax(0, 1fr) 110px 130px 100px 42px;
  gap: 14px;
  align-items: center;
  padding: 18px;
}

.cart-item + .cart-item {
  border-top: 1px solid #e6ece8;
}

.item-image {
  position: relative;
  display: block;
  aspect-ratio: 1 / 1;
  background: linear-gradient(135deg, #eef3f0, #dbe7e1);
  border-radius: 8px;
}

.item-image span {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 3px 7px;
  color: #fff;
  font-size: 12px;
  background: #d95d39;
  border-radius: 999px;
}

.item-info a {
  color: inherit;
  text-decoration: none;
}

.item-info h2 {
  margin: 0;
  color: #183c35;
  font-size: 17px;
}

.item-info p {
  margin: 8px 0;
  color: #71827d;
}

.item-info span {
  color: #8a9995;
  font-size: 13px;
}

.item-price {
  display: grid;
  gap: 4px;
}

.item-price strong,
.item-total {
  color: #d23f31;
  font-weight: 900;
}

.item-price del {
  color: #a4aaa7;
  font-size: 13px;
}

.summary-panel {
  position: sticky;
  top: 24px;
  display: grid;
  gap: 16px;
  padding: 20px;
}

.summary-panel h2 {
  margin: 0;
  color: #183c35;
  font-size: 22px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  color: #61736d;
}

.summary-row strong {
  color: #183c35;
}

@media (max-width: 1000px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }

  .summary-panel {
    position: static;
  }
}

@media (max-width: 760px) {
  .cart-page {
    padding: 18px 12px;
  }

  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .cart-item {
    grid-template-columns: 30px 84px minmax(0, 1fr);
  }

  .item-price,
  .cart-item .el-input-number,
  .item-total,
  .cart-item > .el-button {
    grid-column: 3;
  }
}
</style>
