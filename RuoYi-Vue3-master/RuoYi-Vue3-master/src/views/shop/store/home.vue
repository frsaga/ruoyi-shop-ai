<template>
  <div class="shop-page-container store-home-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">店铺主页</h1>
        <p class="page-subtitle">浏览店铺商品和店铺信息</p>
      </div>

      <!-- 店铺信息卡片 -->
      <div class="store-header-glass" v-loading="storeLoading">
        <div class="store-header">
          <div class="store-info">
            <h1 class="store-name">{{ store.storeName || '店铺名称' }}</h1>
            <p class="store-description" v-if="store.storeDescription">
              {{ store.storeDescription }}
            </p>
            <div class="store-meta">
              <span class="meta-item" v-if="store.creditScore">
                <el-icon><StarFilled /></el-icon>
                信用评分: {{ store.creditScore }}分
              </span>
              <span class="meta-item" v-if="store.salesVolume">
                <el-icon><TrendCharts /></el-icon>
                总销量: {{ store.salesVolume }}
              </span>
              <span class="meta-item" v-if="store.storePhone">
                <el-icon><Phone /></el-icon>
                联系电话: {{ store.storePhone }}
              </span>
            </div>
          </div>
          <div class="store-actions">
            <el-button
              type="primary"
              size="large"
              class="glow-button-primary"
              @click="handleStartChat"
            >
              <el-icon><Message /></el-icon>
              发起客服沟通
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品列表卡片 -->
      <div class="store-products-glass">
        <div class="products-header">
          <h2 class="section-title">在售商品</h2>
          <div class="search-box">
            <el-input
              v-model="queryParams.productName"
              placeholder="搜索商品"
              class="search-input"
              clearable
              @keyup.enter="handleQuery"
            >
              <template #append>
                <el-button class="search-btn" @click="handleQuery">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>

        <div v-loading="productsLoading" class="products-list">
          <el-empty v-if="productList.length === 0 && !productsLoading" description="暂无在售商品" />
          
          <div class="product-grid-container" v-else>
            <div
              v-for="product in productList" 
              :key="product.productId"
              class="product-card-glass"
              @click="handleProductDetail(product)"
            >
              <div class="product-info">
                <h3 class="product-name" @click.stop="handleProductDetail(product)">
                  {{ product.productName }}
                </h3>
                <p class="product-subtitle" v-if="product.productSubtitle">
                  {{ product.productSubtitle }}
                </p>
                <div class="product-price">
                  <span class="price-label">价格：</span>
                  <span class="price-value">¥{{ product.price }}</span>
                </div>
                <div class="product-meta">
                  <span class="meta-stat">
                    <el-icon><TrendCharts /></el-icon>
                    销量: {{ product.salesCount || 0 }}
                  </span>
                  <span class="meta-stat">
                    <el-icon><Box /></el-icon>
                    库存: {{ product.stock }}
                  </span>
                </div>
                <div class="product-status">
                  <el-tag v-if="product.status === '1'" type="success" class="status-badge">在售</el-tag>
                  <el-tag v-else-if="product.status === '0'" type="warning" class="status-badge">待审核</el-tag>
                  <el-tag v-else type="info" class="status-badge">已下架</el-tag>
                </div>
              </div>
              <div class="product-actions">
                <el-button
                  v-if="isBuyer"
                  type="primary"
                  size="small"
                  class="product-action-btn"
                  :disabled="product.status !== '1' || product.stock <= 0"
                  @click.stop="handleAddToCart(product)"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
                </el-button>
                <el-button
                  v-else
                  type="primary"
                  size="small"
                  class="product-action-btn"
                  :disabled="product.status !== '1' || product.stock <= 0"
                  @click.stop="handleProductDetail(product)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </div>

          <div class="pagination-wrapper" v-show="total > 0">
            <pagination
              :total="total"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              @pagination="getProductList"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="StoreHome">
import { getStorePublic } from "@/api/shop/store";
import { listProductByStore } from "@/api/shop/product";
import { addToCart } from "@/api/shop/cart";
import { ShoppingCart, Message, StarFilled, TrendCharts, Phone, Search, Box } from '@element-plus/icons-vue';
import useUserStore from '@/store/modules/user';
import { watch, onActivated } from 'vue';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const storeLoading = ref(false);
const productsLoading = ref(false);
const store = ref({});
const productList = ref([]);
const total = ref(0);
const canChat = ref(false);
const isBuyer = ref(true);

const queryParams = ref({
  pageNum: 1,
  pageSize: 12,
  productName: null
});

/** 获取店铺信息 */
function getStoreInfo() {
  storeLoading.value = true;
  const storeId = route.params.storeId;
  
  if (!storeId) {
    storeLoading.value = false;
    proxy.$modal.msgError("店铺ID不能为空");
    return;
  }
  
  getStorePublic(storeId).then(response => {
    if (response.code === 200 && response.data) {
      store.value = response.data;
      storeLoading.value = false;
      
      // 检查是否可以发起客服沟通（需要登录且不是店铺主人）
      checkCanChat();
    } else {
      storeLoading.value = false;
      proxy.$modal.msgError(response.msg || "获取店铺信息失败");
    }
  }).catch((error) => {
    storeLoading.value = false;
    console.error('获取店铺信息失败:', error);
    proxy.$modal.msgError(error.response?.data?.msg || error.message || "获取店铺信息失败，请检查店铺ID是否正确");
  });
}

/** 检查是否可以发起客服沟通 */
function checkCanChat() {
  try {
    // 获取当前用户ID（使用Pinia store）
    const currentUserId = userStore.id;
    
    // 获取店铺主人ID
    const storeUserId = store.value.userId;
    
    // 调试信息
    console.log('当前用户ID:', currentUserId, '店铺主人ID:', storeUserId);
    
    // 如果用户已登录且不是店铺主人，可以发起客服沟通
    // 如果用户未登录，按钮仍然可用，点击时再提示登录
    if (!currentUserId) {
      // 未登录用户，按钮可用，但点击时会提示登录
      canChat.value = true;
    } else if (!storeUserId) {
      // 店铺没有关联用户，允许发起沟通
      canChat.value = true;
    } else {
      // 已登录用户，检查是否是店铺主人（字符串和数字都要比较）
      const currentUserIdStr = String(currentUserId);
      const storeUserIdStr = String(storeUserId);
      canChat.value = currentUserIdStr !== storeUserIdStr;
    }
  } catch (e) {
    console.error('检查客服沟通权限失败:', e);
    // 出错时允许点击，点击时再判断
    canChat.value = true;
  }
}

/** 获取商品列表 */
function getProductList() {
  productsLoading.value = true;
  const storeId = route.params.storeId;
  
  listProductByStore(storeId, queryParams.value).then(response => {
    productList.value = response.rows || [];
    total.value = response.total || 0;
    productsLoading.value = false;
  }).catch(() => {
    productsLoading.value = false;
  });
}

/** 搜索商品 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getProductList();
}

/** 商品详情 */
function handleProductDetail(product) {
  // 添加时间戳参数，确保即使路由相同也能刷新
  router.push({
    path: '/shop/product/detail/' + product.productId,
    query: {
      _t: Date.now() // 添加时间戳强制刷新
    }
  });
}

/** 检查用户类型 */
function checkUserType() {
  // 通过检查用户角色来判断：如果用户有seller角色或店铺管理权限，则是卖家
  try {
    const roles = userStore.roles || [];
    const permissions = userStore.permissions || [];
    
    // 检查是否有seller角色
    const hasSellerRole = roles.some(role => role.toLowerCase().includes('seller'));
    // 检查是否有店铺管理权限
    const hasStorePermission = permissions.includes('shop:store:list');
    // 检查是否是管理员
    const isAdmin = roles.some(role => role.toLowerCase().includes('admin')) || 
                    permissions.some(perm => perm.includes('admin'));
    
    // 如果有seller角色、店铺管理权限或是管理员，则不是买家
    isBuyer.value = !hasSellerRole && !hasStorePermission && !isAdmin;
    
    // 如果无法判断，默认设为买家
    if (roles.length === 0 && permissions.length === 0) {
      isBuyer.value = true;
    }
  } catch (e) {
    // 默认设为买家
    isBuyer.value = true;
  }
}

/** 加入购物车 */
function handleAddToCart(product) {
  // 只有买家才能加入购物车
  if (!isBuyer.value) {
    proxy.$modal.msgWarning("卖家不能使用购物车功能");
    return;
  }
  
  if (product.status !== '1') {
    proxy.$modal.msgWarning("商品已下架");
    return;
  }
  if (product.stock <= 0) {
    proxy.$modal.msgWarning("商品已售罄");
    return;
  }

  addToCart(product.productId, 1).then(() => {
    proxy.$modal.msgSuccess("已加入购物车");
  });
}

/** 发起客服沟通 */
function handleStartChat() {
  // 获取当前用户ID（使用Pinia store）
  const currentUserId = userStore.id;
  
  // 检查是否已登录
  if (!currentUserId) {
    proxy.$modal.msgWarning("请先登录");
    router.push('/login');
    return;
  }
  
  // 检查是否是店铺主人
  const storeUserId = store.value.userId;
  if (storeUserId) {
    const currentUserIdStr = String(currentUserId);
    const storeUserIdStr = String(storeUserId);
    if (currentUserIdStr === storeUserIdStr) {
      proxy.$modal.msgWarning("这是您的店铺，无法与自己沟通");
      return;
    }
  }
  
  // 跳转到客服沟通页面，并传递店铺信息
  if (!store.value.userId) {
    proxy.$modal.msgError("店铺信息不完整，无法发起客服沟通");
    return;
  }
  
  router.push({
    path: '/shop/chat',
    query: {
      sellerId: store.value.userId,
      storeId: store.value.storeId
    }
  });
}

// 加载店铺数据
function loadStoreData() {
  checkUserType();
  getStoreInfo();
  getProductList();
}

// 监听路由参数变化（当路由参数变化时刷新数据）
watch(() => route.params.storeId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadStoreData();
  }
});

// 组件激活时刷新数据（用于keep-alive场景，确保从其他页面返回时数据是最新的）
onActivated(() => {
  loadStoreData();
});

onMounted(() => {
  loadStoreData();
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.store-home-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .store-header-glass {
    @extend .glass-card;
    padding: 30px;
    margin-bottom: 20px;
    animation: slideDown 0.6s ease-out;
  }

  .store-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 30px;
    
    @media (max-width: 768px) {
      flex-direction: column;
    }
  }

  .store-info {
    flex: 1;
  }

  .store-name {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 15px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .store-description {
    font-size: 1rem;
    color: rgba(0, 0, 0, 0.6);
    margin: 0 0 20px;
    line-height: 1.6;
  }

  .store-meta {
    display: flex;
    gap: 30px;
    font-size: 0.95rem;
    color: rgba(0, 0, 0, 0.7);
    flex-wrap: wrap;
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 15px;
      background: rgba(102, 126, 234, 0.05);
      border-radius: 8px;
      
      .el-icon {
        color: #667eea;
      }
    }
  }

  .store-actions {
    flex-shrink: 0;
  }

  .store-products-glass {
    @extend .glass-card;
    padding: 30px;
    animation: fadeInUp 0.6s ease-out;
  }

  .products-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    flex-wrap: wrap;
    gap: 15px;
    
    .section-title {
      font-size: 1.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0;
    }
    
    .search-box {
      display: flex;
      gap: 10px;
      
      .search-input {
        width: 300px;
        
        :deep(.el-input__wrapper) {
          background: rgba(255, 255, 255, 0.9);
          border-radius: 10px;
          transition: all 0.3s;
          
          &:hover {
            background: rgba(255, 255, 255, 1);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
          }
        }
      }
      
      .search-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }

  .products-list {
    min-height: 400px;
  }

  .product-grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }

  .product-card-glass {
    @extend .glass-card;
    padding: 0;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 12px 40px rgba(102, 126, 234, 0.25);
    }
  }

  .product-info {
    padding: 20px;
  }

  .product-name {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0 0 10px;
    color: rgba(0, 0, 0, 0.8);
    cursor: pointer;
    transition: color 0.3s;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    
    &:hover {
      color: #667eea;
    }
  }

  .product-subtitle {
    font-size: 0.9rem;
    color: rgba(0, 0, 0, 0.5);
    margin: 0 0 15px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .product-price {
    margin: 15px 0;
    
    .price-label {
      font-size: 0.9rem;
      color: rgba(0, 0, 0, 0.6);
    }
    
    .price-value {
      font-size: 1.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-left: 5px;
    }
  }

  .product-meta {
    display: flex;
    justify-content: space-between;
    font-size: 0.85rem;
    color: rgba(0, 0, 0, 0.6);
    margin: 15px 0;
    gap: 10px;
    
    .meta-stat {
      display: flex;
      align-items: center;
      gap: 5px;
      
      .el-icon {
        color: #667eea;
        font-size: 0.9rem;
      }
    }
  }

  .product-status {
    margin-top: 15px;
    
    .status-badge {
      font-weight: 600;
      padding: 4px 12px;
      border-radius: 12px;
    }
  }

  .product-actions {
    padding: 15px 20px;
    border-top: 1px solid rgba(102, 126, 234, 0.1);
    background: rgba(102, 126, 234, 0.02);
    
    .product-action-btn {
      width: 100%;
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      }
      
      .el-icon {
        margin-right: 5px;
      }
    }
  }

  .pagination-wrapper {
    margin-top: 30px;
    display: flex;
    justify-content: center;
  }

  @media (max-width: 768px) {
    .products-header {
      flex-direction: column;
      align-items: stretch;
      
      .search-box {
        width: 100%;
        
        .search-input {
          width: 100%;
        }
      }
    }
    
    .product-grid-container {
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 15px;
    }
    
    .store-meta {
      flex-direction: column;
      gap: 10px;
    }
  }
}
</style>

