<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Goods, Lock, Plus, Search, Shop, SwitchButton, Tickets, User } from '@element-plus/icons-vue'
import {
  adminLogout,
  createAdminProduct,
  getAdminOrders,
  getAdminProducts,
  getAdminUserOrders,
  getAdminUsers,
  resetUserPassword,
  shipAdminOrder,
  updateAdminProduct,
  updateAdminProductAvailability,
  uploadAdminProductImage
} from '../api/admin'
import { getCategories } from '../api/products'
import { useAdminState } from '../stores/adminState'

const router = useRouter()
const { adminUser, clearAdminAuth } = useAdminState()

const activeTab = ref('orders')
const users = ref([])
const orders = ref([])
const products = ref([])
const categories = ref([])
const userOrders = ref([])
const selectedUser = ref(null)
const usersLoading = ref(false)
const ordersLoading = ref(false)
const productsLoading = ref(false)
const productImageUploading = ref(false)
const userOrdersLoading = ref(false)
const actionOrderId = ref(null)
const actionProductId = ref(null)
const userKeyword = ref('')
const orderKeyword = ref('')
const orderStatus = ref('all')
const productKeyword = ref('')
const productCategoryId = ref('')
const productStatus = ref('all')
const userOrderDrawerVisible = ref(false)
const productDrawerVisible = ref(false)
const editingProductId = ref(null)
const productImageInputRef = ref()

const productForm = ref(createEmptyProductForm())

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 'pendingPay' },
  { label: '待发货', value: 'pendingShipment' },
  { label: '已发货', value: 'shipped' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const statusMap = {
  pendingPay: { label: '待支付', type: 'warning' },
  pendingShipment: { label: '待发货', type: 'danger' },
  shipped: { label: '已发货', type: 'primary' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'info' }
}

const productStatusOptions = [
  { label: '全部', value: 'all' },
  { label: '上架中', value: 'available' },
  { label: '已下架', value: 'offline' }
]

const stats = computed(() => ({
  users: users.value.length,
  orders: orders.value.length,
  products: products.value.length,
  pendingShipment: orders.value.filter((order) => order.status === 'pendingShipment').length
}))

onMounted(() => {
  loadCategories()
  loadUsers()
  loadOrders()
  loadProducts()
})

function createEmptyProductForm() {
  return {
    name: '',
    subtitle: '',
    description: '',
    price: 0,
    originalPrice: null,
    sales: 0,
    tag: '',
    imageUrl: '',
    stock: 99,
    specsText: '',
    categoryId: '',
    sortOrder: 0,
    available: true
  }
}

async function loadCategories() {
  try {
    categories.value = await getCategories()
  } catch (error) {
    ElMessage.error(error.message || '获取商品分类失败')
  }
}

async function loadUsers() {
  usersLoading.value = true
  try {
    users.value = await getAdminUsers(userKeyword.value.trim())
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    usersLoading.value = false
  }
}

async function loadOrders() {
  ordersLoading.value = true
  try {
    orders.value = await getAdminOrders(orderStatus.value, orderKeyword.value.trim())
  } catch (error) {
    ElMessage.error(error.message || '获取订单列表失败')
  } finally {
    ordersLoading.value = false
  }
}

async function loadProducts() {
  productsLoading.value = true
  try {
    products.value = await getAdminProducts({
      keyword: productKeyword.value.trim(),
      categoryId: productCategoryId.value,
      status: productStatus.value
    })
  } catch (error) {
    ElMessage.error(error.message || '获取商品列表失败')
  } finally {
    productsLoading.value = false
  }
}

async function openUserOrders(row) {
  selectedUser.value = row
  userOrderDrawerVisible.value = true
  userOrdersLoading.value = true
  try {
    userOrders.value = await getAdminUserOrders(row.id)
  } catch (error) {
    ElMessage.error(error.message || '获取用户订单失败')
  } finally {
    userOrdersLoading.value = false
  }
}

async function resetPassword(row) {
  try {
    const { value } = await ElMessageBox.prompt(
      `为用户 ${row.username} 设置新密码`,
      '重置密码',
      {
        confirmButtonText: '重置',
        cancelButtonText: '取消',
        inputValue: '123456',
        inputType: 'password',
        inputValidator(value) {
          return value && value.length >= 6 ? true : '密码至少 6 位'
        }
      }
    )

    await resetUserPassword(row.id, value)
    ElMessage.success('密码已重置')
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }

    ElMessage.error(error.message || '重置密码失败')
  }
}

async function shipOrder(order) {
  try {
    await ElMessageBox.confirm('确认将该订单标记为已发货吗？', '订单发货', {
      confirmButtonText: '确认发货',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (error) {
    return
  }

  actionOrderId.value = order.id
  try {
    await shipAdminOrder(order.id)
    ElMessage.success('订单已发货')
    await loadOrders()
    if (selectedUser.value) {
      userOrders.value = await getAdminUserOrders(selectedUser.value.id)
    }
  } catch (error) {
    ElMessage.error(error.message || '发货失败')
  } finally {
    actionOrderId.value = null
  }
}

function openCreateProduct() {
  editingProductId.value = null
  productForm.value = createEmptyProductForm()
  productDrawerVisible.value = true
}

function openEditProduct(row) {
  editingProductId.value = row.id
  productForm.value = {
    name: row.name || '',
    subtitle: row.subtitle || '',
    description: row.description || '',
    price: Number(row.price || 0),
    originalPrice: row.originalPrice ?? null,
    sales: Number(row.sales || 0),
    tag: row.tag || '',
    imageUrl: row.imageUrl || '',
    stock: Number(row.stock || 99),
    specsText: (row.specs || []).join('\n'),
    categoryId: row.categoryId || '',
    sortOrder: Number(row.sortOrder || row.id || 0),
    available: row.available
  }
  productDrawerVisible.value = true
}

async function submitProduct() {
  const payload = normalizeProductPayload()
  if (!payload) {
    return
  }

  productsLoading.value = true
  try {
    if (editingProductId.value) {
      await updateAdminProduct(editingProductId.value, payload)
      ElMessage.success('商品已保存')
    } else {
      await createAdminProduct(payload)
      ElMessage.success('商品已添加')
    }
    productDrawerVisible.value = false
    await loadProducts()
  } catch (error) {
    ElMessage.error(error.message || '保存商品失败')
  } finally {
    productsLoading.value = false
  }
}

async function toggleProduct(row) {
  actionProductId.value = row.id
  try {
    await updateAdminProductAvailability(row.id, !row.available)
    ElMessage.success(row.available ? '商品已下架' : '商品已上架')
    await loadProducts()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    actionProductId.value = null
  }
}

function openProductImagePicker() {
  productImageInputRef.value?.click()
}

async function uploadSelectedProductImage(event) {
  const file = event.target.files?.[0]
  event.target.value = ''

  if (!file) {
    return
  }
  if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)) {
    ElMessage.error('商品图片仅支持 JPG、PNG、GIF、WEBP 格式')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('商品图片不能超过 5MB')
    return
  }

  productImageUploading.value = true
  try {
    const result = await uploadAdminProductImage(file)
    productForm.value.imageUrl = result.imageUrl
    ElMessage.success('商品图片已上传')
  } catch (error) {
    ElMessage.error(error.message || '商品图片上传失败')
  } finally {
    productImageUploading.value = false
  }
}

function normalizeProductPayload() {
  const form = productForm.value
  if (!form.name.trim()) {
    ElMessage.warning('请输入商品名称')
    return null
  }
  if (!form.subtitle.trim()) {
    ElMessage.warning('请输入商品副标题')
    return null
  }
  if (!form.categoryId) {
    ElMessage.warning('请选择商品分类')
    return null
  }
  if (Number(form.price) <= 0) {
    ElMessage.warning('商品价格必须大于 0')
    return null
  }

  return {
    name: form.name.trim(),
    subtitle: form.subtitle.trim(),
    description: form.description.trim(),
    price: Number(form.price),
    originalPrice: form.originalPrice === null || form.originalPrice === '' ? null : Number(form.originalPrice),
    sales: Number(form.sales || 0),
    tag: form.tag.trim(),
    imageUrl: form.imageUrl.trim(),
    stock: Number(form.stock || 0),
    specs: form.specsText
      .split(/\n|,|，/)
      .map((item) => item.trim())
      .filter(Boolean),
    categoryId: Number(form.categoryId),
    sortOrder: Number(form.sortOrder || 0),
    available: Boolean(form.available)
  }
}

async function logout() {
  try {
    await adminLogout()
  } catch (error) {
    // 本地退出优先，服务端 token 失效时也会拒绝后续请求。
  }

  clearAdminAuth()
  router.push({ path: '/login', query: { mode: 'admin' } })
}

function statusInfo(status) {
  return statusMap[status] || { label: '未知状态', type: 'info' }
}

function itemSummary(items = []) {
  return items.map((item) => `${item.name} x${item.quantity}`).join('，')
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function categoryName(categoryId) {
  return categories.value.find((category) => category.id === categoryId)?.name || '-'
}
</script>

<template>
  <main class="admin-page">
    <header class="admin-header">
      <router-link class="brand" to="/">
        <el-icon><Shop /></el-icon>
        <span>Shopping Admin</span>
      </router-link>

      <div class="admin-account">
        <el-icon><User /></el-icon>
        <span>{{ adminUser?.username || 'admin' }}</span>
        <el-button :icon="SwitchButton" @click="logout">退出</el-button>
      </div>
    </header>

    <section class="stats-row">
      <div>
        <el-icon><User /></el-icon>
        <span>用户数</span>
        <strong>{{ stats.users }}</strong>
      </div>
      <div>
        <el-icon><Document /></el-icon>
        <span>订单数</span>
        <strong>{{ stats.orders }}</strong>
      </div>
      <div>
        <el-icon><Tickets /></el-icon>
        <span>待发货</span>
        <strong>{{ stats.pendingShipment }}</strong>
      </div>
      <div>
        <el-icon><Goods /></el-icon>
        <span>商品数</span>
        <strong>{{ stats.products }}</strong>
      </div>
    </section>

    <section class="admin-panel">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="订单管理" name="orders">
          <div class="toolbar">
            <el-segmented v-model="orderStatus" :options="statusOptions" @change="loadOrders" />
            <div class="toolbar-search">
              <el-input
                v-model="orderKeyword"
                :prefix-icon="Search"
                clearable
                placeholder="订单号、用户、手机号"
                @keyup.enter="loadOrders"
                @clear="loadOrders"
              />
              <el-button type="primary" @click="loadOrders">查询</el-button>
            </div>
          </div>

          <el-table :data="orders" v-loading="ordersLoading" class="admin-table">
            <el-table-column prop="orderNo" label="订单号" min-width="190" />
            <el-table-column label="用户" min-width="150">
              <template #default="{ row }">
                <div class="cell-stack">
                  <strong>{{ row.username }}</strong>
                  <span>{{ row.phone }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="商品" min-width="260">
              <template #default="{ row }">
                <span class="ellipsis">{{ itemSummary(row.items) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="金额" width="110">
              <template #default="{ row }">￥{{ formatPrice(row.totalAmount) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <el-tag :type="statusInfo(row.status).type">
                  {{ statusInfo(row.status).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" width="150" />
            <el-table-column label="操作" width="130" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'pendingShipment'"
                  size="small"
                  type="primary"
                  :loading="actionOrderId === row.id"
                  @click="shipOrder(row)"
                >
                  发货
                </el-button>
                <span v-else class="muted">-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="用户管理" name="users">
          <div class="toolbar">
            <div class="toolbar-search">
              <el-input
                v-model="userKeyword"
                :prefix-icon="Search"
                clearable
                placeholder="用户名、邮箱、手机号"
                @keyup.enter="loadUsers"
                @clear="loadUsers"
              />
              <el-button type="primary" @click="loadUsers">查询</el-button>
            </div>
          </div>

          <el-table :data="users" v-loading="usersLoading" class="admin-table">
            <el-table-column prop="id" label="ID" width="72" />
            <el-table-column prop="username" label="用户名" min-width="130" />
            <el-table-column prop="phone" label="手机号" min-width="130" />
            <el-table-column prop="email" label="邮箱" min-width="190" />
            <el-table-column label="余额" width="120">
              <template #default="{ row }">￥{{ formatPrice(row.balance) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="createdAt" label="注册时间" width="150" />
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="openUserOrders(row)">订单</el-button>
                <el-button size="small" type="warning" :icon="Lock" @click="resetPassword(row)">
                  重置密码
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="商品管理" name="products">
          <div class="toolbar">
            <el-segmented v-model="productStatus" :options="productStatusOptions" @change="loadProducts" />
            <div class="toolbar-search product-toolbar">
              <el-select
                v-model="productCategoryId"
                clearable
                placeholder="分类"
                @change="loadProducts"
                @clear="loadProducts"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
              <el-input
                v-model="productKeyword"
                :prefix-icon="Search"
                clearable
                placeholder="商品名称、卖点、标签"
                @keyup.enter="loadProducts"
                @clear="loadProducts"
              />
              <el-button type="primary" @click="loadProducts">查询</el-button>
              <el-button type="success" :icon="Plus" @click="openCreateProduct">添加商品</el-button>
            </div>
          </div>

          <el-table :data="products" v-loading="productsLoading" class="admin-table">
            <el-table-column prop="id" label="ID" width="72" />
            <el-table-column label="商品" min-width="210">
              <template #default="{ row }">
                <div class="cell-stack">
                  <strong>{{ row.name }}</strong>
                  <span>{{ row.subtitle }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="分类" width="110">
              <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
            </el-table-column>
            <el-table-column label="价格" width="110">
              <template #default="{ row }">￥{{ formatPrice(row.price) }}</template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="90" />
            <el-table-column prop="sales" label="销量" width="90" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.available ? 'success' : 'info'">
                  {{ row.available ? '上架中' : '已下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="190" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="openEditProduct(row)">编辑</el-button>
                <el-button
                  size="small"
                  :type="row.available ? 'warning' : 'success'"
                  :loading="actionProductId === row.id"
                  @click="toggleProduct(row)"
                >
                  {{ row.available ? '下架' : '上架' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </section>

    <el-drawer
      v-model="userOrderDrawerVisible"
      :title="selectedUser ? `${selectedUser.username} 的订单记录` : '订单记录'"
      size="64%"
    >
      <el-table :data="userOrders" v-loading="userOrdersLoading">
        <el-table-column prop="orderNo" label="订单号" min-width="190" />
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">
            <span class="ellipsis">{{ itemSummary(row.items) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="110">
          <template #default="{ row }">￥{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusInfo(row.status).type">
              {{ statusInfo(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="150" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pendingShipment'"
              size="small"
              type="primary"
              :loading="actionOrderId === row.id"
              @click="shipOrder(row)"
            >
              发货
            </el-button>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-drawer
      v-model="productDrawerVisible"
      :title="editingProductId ? '编辑商品' : '添加商品'"
      size="520px"
    >
      <el-form :model="productForm" label-position="top">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品副标题">
          <el-input v-model="productForm.subtitle" maxlength="160" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="productForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="价格">
            <el-input-number v-model="productForm.price" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="库存">
            <el-input-number v-model="productForm.stock" :min="0" />
          </el-form-item>
          <el-form-item label="销量">
            <el-input-number v-model="productForm.sales" :min="0" />
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="标签">
            <el-input v-model="productForm.tag" maxlength="32" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="productForm.sortOrder" :min="0" />
          </el-form-item>
        </div>
        <el-form-item label="商品图片">
          <button
            class="product-image-upload"
            type="button"
            :disabled="productImageUploading"
            @click="openProductImagePicker"
          >
            <span
              v-if="productForm.imageUrl"
              class="product-image-preview"
              :style="{ backgroundImage: `url(${productForm.imageUrl})` }"
            ></span>
            <span v-else class="product-image-empty">
              <el-icon><Plus /></el-icon>
              <strong>上传商品图片</strong>
              <em>JPG / PNG / GIF / WEBP，最大 5MB</em>
            </span>
            <span v-if="productImageUploading" class="image-upload-mask" v-loading="productImageUploading"></span>
          </button>
          <input
            ref="productImageInputRef"
            class="hidden-file-input"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            @change="uploadSelectedProductImage"
          />
        </el-form-item>
        <el-form-item label="商品特点">
          <el-input
            v-model="productForm.specsText"
            type="textarea"
            :rows="3"
            placeholder="每行一个特点，也可以用逗号分隔"
          />
        </el-form-item>
        <el-form-item label="商品说明">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            maxlength="800"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="productForm.available">上架商品</el-checkbox>
        </el-form-item>
        <div class="drawer-actions">
          <el-button @click="productDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="productsLoading" @click="submitProduct">保存</el-button>
        </div>
      </el-form>
    </el-drawer>
  </main>
</template>

<style scoped>
.admin-page {
  min-height: 100vh;
  padding: 24px;
  color: #1f2d2a;
  background: #f4f6f4;
}

.admin-header,
.stats-row,
.admin-panel {
  width: min(1280px, 100%);
  margin: 0 auto;
}

.admin-header {
  display: flex;
  min-height: 64px;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
}

.brand,
.admin-account {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.brand {
  color: #183c35;
  font-size: 22px;
  font-weight: 900;
  text-decoration: none;
}

.brand .el-icon {
  width: 40px;
  height: 40px;
  color: #fff;
  background: #1f8a70;
  border-radius: 8px;
}

.admin-account {
  color: #3c4c48;
  font-weight: 800;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.stats-row div {
  display: flex;
  min-height: 86px;
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

.admin-panel {
  min-height: 560px;
  padding: 18px;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-search {
  display: flex;
  width: min(420px, 100%);
  gap: 10px;
}

.product-toolbar {
  width: min(760px, 100%);
}

.admin-table {
  width: 100%;
}

.cell-stack {
  display: grid;
  gap: 4px;
}

.cell-stack span,
.muted {
  color: #7b8a85;
  font-size: 12px;
}

.ellipsis {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.drawer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

.product-image-upload {
  position: relative;
  display: block;
  width: 100%;
  overflow: hidden;
  padding: 0;
  background: #f8faf8;
  border: 1px dashed #b8c8c1;
  border-radius: 8px;
  cursor: pointer;
}

.product-image-upload:disabled {
  cursor: wait;
}

.product-image-upload:hover {
  border-color: #1f8a70;
}

.product-image-preview,
.product-image-empty {
  display: grid;
  width: 100%;
  min-height: 190px;
  place-items: center;
}

.product-image-preview {
  background-position: center;
  background-size: cover;
}

.product-image-empty {
  gap: 6px;
  color: #6d7e78;
}

.product-image-empty .el-icon {
  width: 42px;
  height: 42px;
  color: #0f8f75;
  background: #eef7f3;
  border-radius: 8px;
}

.product-image-empty strong {
  color: #183c35;
}

.product-image-empty em {
  font-size: 12px;
  font-style: normal;
}

.image-upload-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.66);
}

.hidden-file-input {
  display: none;
}

@media (max-width: 860px) {
  .admin-page {
    padding: 16px 10px;
  }

  .admin-header,
  .toolbar {
    align-items: flex-start;
    flex-direction: column;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
