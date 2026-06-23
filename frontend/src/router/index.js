import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProductListView from '../views/ProductListView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import CartView from '../views/CartView.vue'
import OrdersView from '../views/OrdersView.vue'
import ProfileView from '../views/ProfileView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: '首页' }
  },
  {
    path: '/products',
    name: 'products',
    component: ProductListView,
    meta: { title: '全部商品' }
  },
  {
    path: '/products/:id',
    name: 'product-detail',
    component: ProductDetailView,
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartView,
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'orders',
    component: OrdersView,
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { title: '个人主页', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { title: '注册' }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: { title: '管理后台', requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin && !sessionStorage.getItem('adminToken')) {
    return {
      path: '/login',
      query: { mode: 'admin', redirect: to.fullPath }
    }
  }

  if (to.meta.requiresAuth && !sessionStorage.getItem('token')) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }
})

export default router
