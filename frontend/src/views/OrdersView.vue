<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Goods, Tickets } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProfile, recharge } from '../api/auth'
import { cancelOrder, completeOrder, getOrders, payOrder } from '../api/orders'
import HomeBackLink from '../components/HomeBackLink.vue'
import { useAuthState } from '../stores/authState'

const router = useRouter()
const { user, setUser } = useAuthState()
const activeStatus = ref('all')
const orders = ref([])
const loading = ref(false)
const actionOrderId = ref(null)

const statusMap = {
  pendingPay: { label: '待支付', type: 'warning' },
  pendingShipment: { label: '待发货', type: 'danger' },
  shipped: { label: '已发货', type: 'primary' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'info' }
}

const totalAmount = computed(() => orders.value.reduce((sum, order) => sum + order.totalAmount, 0))
const balance = computed(() => Number(user.value?.balance || 0))

onMounted(() => {
  loadProfile()
  loadOrders()
})
watch(activeStatus, loadOrders)

async function loadProfile() {
  try {
    setUser(await getProfile())
  } catch (error) {
    ElMessage.error(error.message || '获取账户余额失败')
  }
}

async function loadOrders() {
  loading.value = true
  try {
    orders.value = await getOrders(activeStatus.value)
  } finally {
    loading.value = false
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

    setUser(await recharge(Number(value).toFixed(2)))
    ElMessage.success('充值成功')
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }

    ElMessage.error(error.message || '充值失败')
  }
}

async function pay(order) {
  actionOrderId.value = order.id
  try {
    const result = await payOrder(order.id)
    if (user.value) {
      setUser({ ...user.value, balance: result.balance })
    }
    ElMessage.success('支付成功，订单待发货')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  } finally {
    actionOrderId.value = null
  }
}

async function cancel(order) {
  try {
    await ElMessageBox.confirm('确定取消这个待支付订单吗？', '取消订单', {
      confirmButtonText: '取消订单',
      cancelButtonText: '返回',
      type: 'warning'
    })
  } catch (error) {
    return
  }

  actionOrderId.value = order.id
  try {
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message || '取消失败')
  } finally {
    actionOrderId.value = null
  }
}

async function complete(order) {
  actionOrderId.value = order.id
  try {
    await completeOrder(order.id)
    ElMessage.success('订单已完成')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    actionOrderId.value = null
  }
}

function statusInfo(status) {
  return statusMap[status] || { label: '未知状态', type: 'info' }
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<template>
  <main class="orders-page">
    <section class="page-header">
      <div>
        <HomeBackLink />
        <router-link class="brand" to="/">
          <el-icon><Document /></el-icon>
          <span>Shopping Platform</span>
        </router-link>
        <h1>我的订单</h1>
        <p>查看订单状态、商品明细和售后入口。</p>
      </div>

      <router-link to="/products">
        <el-button :icon="Goods">继续购物</el-button>
      </router-link>
    </section>

    <section class="stats-row">
      <div>
        <el-icon><Tickets /></el-icon>
        <span>当前订单</span>
        <strong>{{ orders.length }}</strong>
      </div>
      <div>
        <el-icon><Goods /></el-icon>
        <span>商品合计</span>
        <strong>￥{{ formatPrice(totalAmount) }}</strong>
      </div>
      <div>
        <el-icon><Tickets /></el-icon>
        <span>账户余额</span>
        <strong>￥{{ formatPrice(balance) }}</strong>
        <el-button size="small" type="primary" @click="openRecharge">充值</el-button>
      </div>
    </section>

    <section class="orders-panel" v-loading="loading">
      <el-tabs v-model="activeStatus">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="pendingPay" />
        <el-tab-pane label="待发货" name="pendingShipment" />
        <el-tab-pane label="已发货" name="shipped" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <div v-if="orders.length" class="order-list">
        <article v-for="order in orders" :key="order.id" class="order-card">
          <header class="order-head">
            <div>
              <strong>{{ order.orderNo }}</strong>
              <span>{{ order.createdAt }}</span>
            </div>
            <el-tag :type="statusInfo(order.status).type">
              {{ statusInfo(order.status).label }}
            </el-tag>
          </header>

          <div class="order-products">
            <button
              v-for="item in order.items"
              :key="item.productId"
              type="button"
              class="order-product"
              @click="router.push(`/products/${item.productId}`)"
            >
              <span class="order-image"></span>
              <span class="order-name">{{ item.name }}</span>
              <span>￥{{ formatPrice(item.price) }}</span>
              <span>x{{ item.quantity }}</span>
            </button>
          </div>

          <footer class="order-foot">
            <span>收货人：{{ order.receiver }}</span>
            <strong>实付 ￥{{ formatPrice(order.totalAmount) }}</strong>
            <div class="order-actions">
              <el-button
                v-if="order.status === 'pendingPay'"
                type="primary"
                :loading="actionOrderId === order.id"
                @click="pay(order)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 'pendingPay'"
                :loading="actionOrderId === order.id"
                @click="cancel(order)"
              >
                取消订单
              </el-button>
              <el-button
                v-if="order.status === 'shipped'"
                type="success"
                :loading="actionOrderId === order.id"
                @click="complete(order)"
              >
                确认完成
              </el-button>
              <el-button>订单详情</el-button>
            </div>
          </footer>
        </article>
      </div>

      <el-empty v-else description="当前状态下暂无订单">
        <el-button type="primary" @click="router.push('/products')">去购物</el-button>
      </el-empty>
    </section>
  </main>
</template>

<style scoped>
.orders-page {
  min-height: 100vh;
  padding: 28px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.page-header,
.stats-row,
.orders-panel {
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

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.stats-row div {
  display: flex;
  min-height: 88px;
  align-items: center;
  gap: 12px;
  padding: 18px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.stats-row .el-icon {
  width: 42px;
  height: 42px;
  color: #0f8f75;
  background: #eef7f3;
  border-radius: 8px;
}

.stats-row span {
  color: #6d7e78;
}

.stats-row strong {
  margin-left: auto;
  color: #183c35;
  font-size: 24px;
}

.stats-row .el-button {
  margin-left: auto;
}

.orders-panel {
  min-height: 420px;
  padding: 18px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.order-list {
  display: grid;
  gap: 14px;
}

.order-card {
  border: 1px solid #e3ebe6;
  border-radius: 8px;
}

.order-head,
.order-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
}

.order-head {
  background: #f8faf8;
  border-bottom: 1px solid #e3ebe6;
}

.order-head div {
  display: grid;
  gap: 4px;
}

.order-head span,
.order-foot span {
  color: #71827d;
  font-size: 13px;
}

.order-products {
  display: grid;
}

.order-product {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr) 100px 48px;
  gap: 12px;
  align-items: center;
  padding: 12px 16px;
  font: inherit;
  text-align: left;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.order-product + .order-product {
  border-top: 1px solid #edf2ef;
}

.order-product:hover {
  background: #f8faf8;
}

.order-image {
  display: block;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #eef3f0, #dbe7e1);
  border-radius: 8px;
}

.order-name {
  overflow: hidden;
  color: #183c35;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-foot strong {
  color: #d23f31;
  font-size: 18px;
}

.order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

@media (max-width: 760px) {
  .orders-page {
    padding: 18px 12px;
  }

  .page-header,
  .order-head,
  .order-foot {
    align-items: flex-start;
    flex-direction: column;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .order-product {
    grid-template-columns: 56px minmax(0, 1fr);
  }

  .order-product span:nth-child(3),
  .order-product span:nth-child(4) {
    grid-column: 2;
  }
}
</style>
