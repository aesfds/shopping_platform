<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Bicycle,
  Goods,
  Handbag,
  House,
  MagicStick,
  Monitor,
  Pear,
  Search,
  ShoppingCart,
  Shop,
  Tickets,
  User
} from '@element-plus/icons-vue'
import { getHomePage } from '../api/home'
import UserMenu from '../components/UserMenu.vue'
import { useAuthState } from '../stores/authState'

const router = useRouter()
const { isLoggedIn } = useAuthState()
const keyword = ref('')
const loading = ref(false)

const fallbackHome = {
  banners: [
    {
      id: 1,
      title: '春季好物上新',
      subtitle: '精选家居、数码与日用百货，新季清单一次逛齐',
      ctaText: '立即查看',
      backgroundColor: '#1f8a70'
    }
  ],
  categories: [
    { id: 1, name: '数码电器', icon: 'Monitor', targetRoute: '/products?category=1' },
    { id: 2, name: '服饰鞋包', icon: 'Handbag', targetRoute: '/products?category=2' },
    { id: 3, name: '家居生活', icon: 'House', targetRoute: '/products?category=3' },
    { id: 4, name: '美妆护理', icon: 'MagicStick', targetRoute: '/products?category=4' },
    { id: 5, name: '食品生鲜', icon: 'Pear', targetRoute: '/products?category=5' },
    { id: 6, name: '运动户外', icon: 'Bicycle', targetRoute: '/products?category=6' }
  ],
  productSections: [
    {
      title: '热门推荐',
      subtitle: '大家都在买',
      products: [
        { id: 1, name: '无线降噪耳机', subtitle: '轻量佩戴，通勤场景推荐', price: 299, originalPrice: 399, sales: 3180, tag: '热卖' },
        { id: 2, name: '便携咖啡杯', subtitle: '密封防漏，办公室常备', price: 59.9, originalPrice: 89, sales: 2206, tag: '精选' },
        { id: 3, name: '基础款短袖', subtitle: '舒适棉感，多色可扩展', price: 79, originalPrice: 119, sales: 1860, tag: '新品' },
        { id: 4, name: '桌面收纳盒', subtitle: '模块组合，保持桌面清爽', price: 39.9, originalPrice: 59.9, sales: 1420, tag: '好评' }
      ]
    }
  ],
  navLinks: [
    { label: '首页', route: '/', reserved: false },
    { label: '全部商品', route: '/products', reserved: true },
    { label: '购物车', route: '/cart', reserved: true },
    { label: '我的订单', route: '/orders', reserved: true },
    { label: '登录', route: '/login', reserved: true }
  ]
}

const home = ref(fallbackHome)
const activeBanner = computed(() => home.value.banners?.[0] || fallbackHome.banners[0])
const visibleNavLinks = computed(() => {
  return (home.value.navLinks || []).map((link) => {
    if (link.route === '/login' && isLoggedIn.value) {
      return { ...link, label: '个人主页', route: '/profile' }
    }

    return link
  })
})
const categoryIconMap = { Monitor, Handbag, House, MagicStick, Pear, Bicycle }

onMounted(async () => {
  loading.value = true
  try {
    home.value = await getHomePage()
  } catch (error) {
    home.value = fallbackHome
  } finally {
    loading.value = false
  }
})

function goSearch() {
  const query = keyword.value.trim()
  router.push(query ? `/products?keyword=${encodeURIComponent(query)}` : '/products')
}

function resolveCategoryIcon(iconName) {
  return categoryIconMap[iconName] || Goods
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<template>
  <div class="home-page" v-loading="loading">
    <header class="site-header">
      <div class="header-strip">
        <span>全国配送</span>
        <nav class="quick-links" aria-label="快捷入口">
          <router-link :to="isLoggedIn ? '/profile' : '/login'">
            {{ isLoggedIn ? '个人主页' : '登录' }}
          </router-link>
          <router-link to="/orders">订单</router-link>
          <router-link to="/cart">购物车</router-link>
        </nav>
      </div>

      <div class="header-main">
        <router-link class="brand" to="/">
          <span class="brand-mark">
            <el-icon><Shop /></el-icon>
          </span>
          <span>Shopping Platform</span>
        </router-link>

        <div class="search-bar">
          <el-input
            v-model="keyword"
            :prefix-icon="Search"
            placeholder="搜索商品、品牌、店铺"
            size="large"
            @keyup.enter="goSearch"
          />
          <el-button type="primary" size="large" @click="goSearch">搜索</el-button>
        </div>

        <div class="header-actions">
          <UserMenu />
          <el-tooltip content="购物车" placement="bottom">
            <router-link class="icon-action" to="/cart" aria-label="购物车">
              <el-icon><ShoppingCart /></el-icon>
            </router-link>
          </el-tooltip>
        </div>
      </div>

      <nav class="main-nav" aria-label="主导航">
        <router-link
          v-for="link in visibleNavLinks"
          :key="link.route"
          :to="link.route"
          :class="{ active: link.route === '/' }"
        >
          {{ link.label }}
        </router-link>
      </nav>
    </header>

    <main>
      <section class="hero-band">
        <div class="category-panel">
          <div class="panel-title">商品分类</div>
          <button
            v-for="category in home.categories"
            :key="category.id"
            class="category-row"
            type="button"
            @click="router.push(category.targetRoute)"
          >
            <span class="category-icon">
              <el-icon><component :is="resolveCategoryIcon(category.icon)" /></el-icon>
            </span>
            <span>{{ category.name }}</span>
            <el-icon class="category-arrow"><ArrowRight /></el-icon>
          </button>
        </div>

        <article class="hero-card" :style="{ '--hero-color': activeBanner.backgroundColor }">
          <div class="hero-copy">
            <span class="eyebrow">精选频道</span>
            <h1>{{ activeBanner.title }}</h1>
            <p>{{ activeBanner.subtitle }}</p>
            <el-button type="primary" size="large" @click="router.push('/products')">
              {{ activeBanner.ctaText }}
            </el-button>
          </div>
          <div class="hero-placeholder" aria-hidden="true"></div>
        </article>

        <aside class="promo-stack">
          <router-link class="promo-block promo-warm" to="/products">
            <span>限时优惠</span>
            <strong>今日精选</strong>
          </router-link>
          <router-link class="promo-block promo-cool" :to="isLoggedIn ? '/profile' : '/login'">
            <span>会员专区</span>
            <strong>会员权益</strong>
          </router-link>
        </aside>
      </section>

      <section class="service-band" aria-label="服务承诺">
        <div>
          <el-icon><Tickets /></el-icon>
          <span>优惠券</span>
        </div>
        <div>
          <el-icon><Goods /></el-icon>
          <span>品质商品</span>
        </div>
        <div>
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
        <div>
          <el-icon><User /></el-icon>
          <span>会员中心</span>
        </div>
      </section>

      <section class="category-strip">
        <div class="section-heading">
          <div>
            <h2>热门分类</h2>
            <p>快速进入你常逛的频道</p>
          </div>
          <router-link to="/products">全部商品</router-link>
        </div>
        <div class="category-grid">
          <button
            v-for="category in home.categories"
            :key="category.id"
            class="category-tile"
            type="button"
            @click="router.push(category.targetRoute)"
          >
            <span>
              <el-icon><component :is="resolveCategoryIcon(category.icon)" /></el-icon>
            </span>
            <strong>{{ category.name }}</strong>
          </button>
        </div>
      </section>

      <section
        v-for="section in home.productSections"
        :key="section.title"
        class="product-section"
      >
        <div class="section-heading">
          <div>
            <h2>{{ section.title }}</h2>
            <p>{{ section.subtitle }}</p>
          </div>
          <router-link to="/products">查看更多</router-link>
        </div>

        <div class="product-grid">
          <article v-for="product in section.products" :key="product.id" class="product-card">
            <div
              class="product-media"
              :style="product.imageUrl ? { backgroundImage: `url(${product.imageUrl})` } : null"
            >
              <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p>{{ product.subtitle }}</p>
              <div class="product-meta">
                <span class="price">￥{{ formatPrice(product.price) }}</span>
                <span v-if="product.originalPrice" class="origin">
                  ￥{{ formatPrice(product.originalPrice) }}
                </span>
              </div>
              <div class="product-foot">
                <span>已售 {{ product.sales || 0 }}</span>
                <el-button text type="primary" @click="router.push(`/products/${product.id}`)">
                  查看
                </el-button>
              </div>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  color: #1f2d2a;
  background: #f4f6f4;
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.96);
  border-bottom: 1px solid #e1e7e3;
  backdrop-filter: blur(10px);
}

.header-strip,
.header-main,
.main-nav,
main {
  width: min(1180px, calc(100% - 32px));
  margin: 0 auto;
}

.header-strip {
  display: flex;
  min-height: 36px;
  align-items: center;
  justify-content: space-between;
  color: #64736e;
  font-size: 13px;
}

.quick-links,
.main-nav {
  display: flex;
  align-items: center;
  gap: 22px;
}

a {
  color: inherit;
  text-decoration: none;
}

.quick-links a:hover,
.main-nav a:hover,
.section-heading a:hover {
  color: #0f8f75;
}

.header-main {
  display: grid;
  grid-template-columns: auto minmax(280px, 1fr) auto;
  gap: 28px;
  align-items: center;
  padding: 18px 0;
}

.brand {
  display: inline-flex;
  min-width: 230px;
  align-items: center;
  gap: 12px;
  color: #183c35;
  font-size: 22px;
  font-weight: 800;
}

.brand-mark,
.category-icon,
.category-tile span {
  display: inline-grid;
  place-items: center;
}

.brand-mark {
  width: 40px;
  height: 40px;
  color: #fff;
  background: #1f8a70;
  border-radius: 8px;
}

.search-bar {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-action {
  display: inline-grid;
  width: 42px;
  height: 42px;
  place-items: center;
  color: #20332f;
  background: #eef4f1;
  border: 1px solid #d9e5df;
  border-radius: 8px;
}

.icon-action:hover {
  color: #fff;
  background: #1f8a70;
  border-color: #1f8a70;
}

.main-nav {
  min-height: 42px;
  color: #3e4d49;
  font-size: 15px;
  font-weight: 700;
}

.main-nav a {
  display: inline-flex;
  height: 42px;
  align-items: center;
  border-bottom: 2px solid transparent;
}

.main-nav a.active {
  color: #0f8f75;
  border-color: #0f8f75;
}

main {
  padding: 28px 0 56px;
}

.hero-band {
  display: grid;
  grid-template-columns: 230px minmax(0, 1fr) 210px;
  gap: 18px;
  align-items: stretch;
}

.category-panel,
.hero-card,
.promo-block,
.product-card {
  border: 1px solid #dfe8e3;
  border-radius: 8px;
  background: #fff;
}

.category-panel {
  padding: 14px;
}

.panel-title {
  margin-bottom: 12px;
  color: #193a34;
  font-size: 17px;
  font-weight: 800;
}

.category-row {
  display: grid;
  width: 100%;
  grid-template-columns: 32px 1fr 18px;
  min-height: 44px;
  align-items: center;
  gap: 10px;
  padding: 0 8px;
  color: #334743;
  font: inherit;
  text-align: left;
  background: transparent;
  border: 0;
  border-radius: 6px;
  cursor: pointer;
}

.category-row:hover {
  color: #0f7f6a;
  background: #eef7f3;
}

.category-icon {
  width: 30px;
  height: 30px;
  color: #0f8f75;
  background: #e4f3ee;
  border-radius: 8px;
}

.category-arrow {
  color: #9aa8a4;
}

.hero-card {
  --hero-color: #1f8a70;
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(260px, 38%);
  min-height: 360px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--hero-color), #123c35);
}

.hero-copy {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 540px;
  padding: 48px;
  color: #fff;
}

.eyebrow {
  width: fit-content;
  margin-bottom: 16px;
  padding: 6px 12px;
  color: #16362f;
  font-size: 13px;
  font-weight: 800;
  background: #f8d76f;
  border-radius: 999px;
}

.hero-copy h1 {
  margin: 0;
  font-size: 52px;
  line-height: 1.08;
  letter-spacing: 0;
}

.hero-copy p {
  max-width: 440px;
  margin: 18px 0 28px;
  color: rgba(255, 255, 255, 0.86);
  font-size: 17px;
  line-height: 1.7;
}

.hero-placeholder {
  align-self: stretch;
  margin: 34px 34px 34px 0;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.18)),
    repeating-linear-gradient(45deg, rgba(255, 255, 255, 0.12) 0 14px, rgba(255, 255, 255, 0.2) 14px 28px);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 8px;
}

.promo-stack {
  display: grid;
  gap: 18px;
}

.promo-block {
  display: flex;
  min-height: 171px;
  flex-direction: column;
  justify-content: flex-end;
  gap: 8px;
  padding: 22px;
  color: #fff;
}

.promo-block span {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.promo-block strong {
  font-size: 24px;
}

.promo-warm {
  background: linear-gradient(145deg, #d95d39, #8f2f23);
}

.promo-cool {
  background: linear-gradient(145deg, #2d6cdf, #173c7c);
}

.service-band {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin: 22px 0;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
  background: #fff;
}

.service-band div {
  display: flex;
  min-height: 76px;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #2f4641;
  font-weight: 800;
}

.service-band div + div {
  border-left: 1px solid #e6ece8;
}

.service-band .el-icon {
  color: #d95d39;
  font-size: 22px;
}

.category-strip,
.product-section {
  margin-top: 30px;
}

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 16px;
}

.section-heading h2 {
  margin: 0;
  color: #183c35;
  font-size: 25px;
  letter-spacing: 0;
}

.section-heading p {
  margin: 8px 0 0;
  color: #70817b;
  font-size: 14px;
}

.section-heading a {
  flex: 0 0 auto;
  color: #49625b;
  font-weight: 800;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 14px;
}

.category-tile {
  display: grid;
  min-height: 120px;
  place-items: center;
  gap: 12px;
  color: #243d37;
  font: inherit;
  background: #fff;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
  cursor: pointer;
}

.category-tile:hover {
  color: #0f8f75;
  border-color: #91d6c1;
  transform: translateY(-2px);
}

.category-tile span {
  width: 44px;
  height: 44px;
  color: #fff;
  background: #1f8a70;
  border-radius: 8px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.product-card {
  min-width: 0;
  overflow: hidden;
}

.product-media {
  position: relative;
  aspect-ratio: 1 / 0.78;
  background:
    linear-gradient(135deg, #eef3f0, #dbe7e1),
    repeating-linear-gradient(45deg, transparent 0 16px, rgba(255, 255, 255, 0.35) 16px 32px);
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

.product-info {
  padding: 14px;
}

.product-info h3 {
  margin: 0;
  overflow: hidden;
  color: #1d312d;
  font-size: 16px;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info p {
  min-height: 40px;
  margin: 8px 0 12px;
  color: #71827d;
  font-size: 13px;
  line-height: 1.5;
}

.product-meta,
.product-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.price {
  color: #d23f31;
  font-size: 20px;
  font-weight: 900;
}

.origin {
  color: #9aa8a4;
  font-size: 13px;
  text-decoration: line-through;
}

.product-foot {
  min-height: 34px;
  margin-top: 8px;
  color: #8a9995;
  font-size: 13px;
}

@media (max-width: 1024px) {
  .header-main {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .brand {
    min-width: 0;
  }

  .header-actions {
    position: absolute;
    right: 16px;
    top: 48px;
  }

  .hero-band {
    grid-template-columns: 1fr;
  }

  .category-panel {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .panel-title {
    grid-column: 1 / -1;
  }

  .promo-stack,
  .service-band,
  .category-grid,
  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .header-strip,
  .header-main,
  .main-nav,
  main {
    width: min(100% - 20px, 1180px);
  }

  .header-strip {
    align-items: flex-start;
    flex-direction: column;
    justify-content: center;
    gap: 6px;
    padding: 8px 0;
  }

  .quick-links,
  .main-nav {
    flex-wrap: wrap;
    gap: 14px;
  }

  .brand {
    padding-right: 104px;
    font-size: 18px;
  }

  .search-bar {
    grid-template-columns: 1fr;
  }

  .hero-card {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-copy {
    padding: 30px 24px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-placeholder {
    min-height: 180px;
    margin: 0 24px 24px;
  }

  .category-panel,
  .promo-stack,
  .service-band,
  .category-grid,
  .product-grid {
    grid-template-columns: 1fr;
  }

  .service-band div + div {
    border-top: 1px solid #e6ece8;
    border-left: 0;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
