<template>
  <div class="shop-page-container product-detail-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">商品详情</h1>
        <p class="page-subtitle">查看商品详细信息、评价和购买选项</p>
      </div>

      <!-- 商品信息卡片 -->
      <div class="product-info-glass" v-loading="loading">
        <div class="product-header">
            <h1 class="product-name">{{ product.productName }}</h1>
            <p class="product-subtitle">{{ product.productSubtitle }}</p>

            <div class="product-price-box">
              <div class="price-main">
                <span class="price-label">价格</span>
                <span class="price-value">¥{{ product.price }}</span>
              </div>
              <div class="product-meta">
              <span class="meta-item">
                <el-icon><TrendCharts /></el-icon>
                销量: {{ product.salesCount || 0 }}
              </span>
              <span class="meta-item">
                <el-icon><Box /></el-icon>
                库存: {{ product.stock }}
              </span>
            </div>
          </div>

            <div class="product-store" @click="handleGoToStore" v-if="product.storeId">
              <el-icon><Shop /></el-icon>
            <span class="store-name">{{ product.storeName || '官方店铺' }}</span>
            <el-icon class="store-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="product-store" v-else>
              <el-icon><Shop /></el-icon>
              <span class="store-name">{{ product.storeName || '官方店铺' }}</span>
            </div>

          <!-- 数量选择（仅买家显示） -->
          <div v-if="isBuyer" class="quantity-selector">
              <span class="label">数量：</span>
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="product.stock || 999"
              class="quantity-input"
              />
              <span class="stock-tip">库存{{ product.stock }}件</span>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button
              v-if="isBuyer"
                type="primary"
                size="large"
              class="glow-button-primary"
                :disabled="product.status !== '1' || product.stock <= 0"
                @click="handleAddToCart"
              >
              <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button
              v-if="isBuyer"
                type="danger"
                size="large"
              class="glass-button danger-button"
                :disabled="product.status !== '1' || product.stock <= 0"
                @click="handleBuyNow"
              >
                立即购买
              </el-button>
              <el-button
                :type="isFavorite ? 'danger' : 'default'"
                size="large"
              class="glass-button"
                @click="handleToggleFavorite"
              >
              <el-icon><StarFilled v-if="isFavorite" /><Star v-else /></el-icon>
                {{ isFavorite ? '已收藏' : '收藏' }}
              </el-button>
              <el-button
                type="success"
                size="large"
              class="glass-button success-button"
                @click="handleContactService"
              >
              <el-icon><ChatDotRound /></el-icon>
                联系客服
              </el-button>
            </div>
          </div>
      </div>

      <!-- 商品详情卡片 -->
      <div class="product-detail-glass">
        <div class="card-header-title">商品详情</div>
        <div class="product-detail-content" v-html="product.detail || '暂无详细描述'">
        </div>
      </div>

      <!-- 商品评价卡片 -->
      <div class="review-card-glass">
          <div class="review-header">
            <span class="card-header-title">商品评价</span>
          <span class="review-count">({{ reviewList.length }})</span>
          </div>
        
        <el-empty v-if="!reviewList || reviewList.length === 0" description="暂无评价" />
        
        <div v-else class="review-list">
          <div 
            v-for="review in reviewList" 
            :key="review.reviewId" 
            class="review-item"
          >
            <div class="review-header-info">
              <div class="user-info">
                <el-avatar :size="40" class="user-avatar">{{ review.userName ? review.userName.charAt(0) : 'U' }}</el-avatar>
                <div class="user-details">
                  <div class="user-name">{{ review.userName || '匿名用户' }}</div>
                  <el-rate 
                    v-model="review.rating" 
                    disabled 
                    show-score 
                    text-color="#ff9900" 
                  />
                </div>
              </div>
              <div class="review-time">{{ parseTime(review.createTime, '{y}-{m}-{d}') }}</div>
            </div>
            
            <div class="review-content">
              {{ review.content }}
            </div>
            
            <el-divider v-if="reviewList.indexOf(review) < reviewList.length - 1" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ProductDetail">
import { getPublicProduct } from "@/api/shop/product";
import { addToCart } from "@/api/shop/cart";
import { checkFavorite, addFavorite, cancelFavorite } from "@/api/shop/favorite";
import { getReviewsByProductId } from "@/api/shop/review";
import { getStorePublic } from "@/api/shop/store";
import { Shop, ShoppingCart, Star, StarFilled, ArrowRight, ChatDotRound, TrendCharts, Box } from '@element-plus/icons-vue';
import useUserStore from '@/store/modules/user';
import { watch, onActivated } from 'vue';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(true);
const product = ref({});
const quantity = ref(1);
const isFavorite = ref(false);
const reviewList = ref([]);
const isBuyer = ref(true);

/** 获取商品详情 */
function getDetail() {
  loading.value = true;
  const productId = route.params.productId;
  
  getPublicProduct(productId).then(response => {
    product.value = response.data;
    
    // 检查收藏状态
    checkFavorite(productId).then(res => {
      isFavorite.value = res.data;
    }).catch(() => {
      isFavorite.value = false;
    });
    
    loading.value = false;
  }).catch(error => {
    console.error('获取商品详情失败:', error);
    loading.value = false;
    proxy.$modal.msgError(error.msg || '获取商品详情失败');
  });
}

/** 切换收藏状态 */
function handleToggleFavorite() {
  if (isFavorite.value) {
    // 取消收藏
    cancelFavorite(product.value.productId).then(() => {
      isFavorite.value = false;
      proxy.$modal.msgSuccess("已取消收藏");
    });
  } else {
    // 添加收藏
    addFavorite({ productId: product.value.productId }).then(() => {
      isFavorite.value = true;
      proxy.$modal.msgSuccess("收藏成功");
    }).catch((error) => {
      if (error.response?.data?.msg) {
        proxy.$modal.msgWarning(error.response.data.msg);
      }
    });
  }
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
function handleAddToCart() {
  // 只有买家才能加入购物车
  if (!isBuyer.value) {
    proxy.$modal.msgWarning("卖家不能使用购物车功能");
    return;
  }
  
  if (product.value.status !== '1') {
    proxy.$modal.msgWarning("商品已下架");
    return;
  }
  if (product.value.stock <= 0) {
    proxy.$modal.msgWarning("商品已售罄");
    return;
  }

  addToCart(product.value.productId, quantity.value).then(() => {
    proxy.$modal.msgSuccess("已加入购物车");
  });
}

/** 立即购买 */
function handleBuyNow() {
  // 只有买家才能立即购买
  if (!isBuyer.value) {
    proxy.$modal.msgWarning("卖家不能使用购买功能");
    return;
  }
  
  if (product.value.status !== '1') {
    proxy.$modal.msgWarning("商品已下架");
    return;
  }
  if (product.value.stock <= 0) {
    proxy.$modal.msgWarning("商品已售罄");
    return;
  }

  // 先加入购物车，再跳转到结算页面
  addToCart(product.value.productId, quantity.value).then(() => {
    // 构造结算数据
    const checkoutData = [{
      productId: product.value.productId,
      productName: product.value.productName,
      price: product.value.price,
      quantity: quantity.value,
      storeId: product.value.storeId,
      storeName: product.value.storeName
    }];
    
    router.push({
      path: '/shop/checkout',
      query: {
        items: JSON.stringify(checkoutData)
      }
    });
  });
}

/** 跳转到店铺主页 */
function handleGoToStore() {
  if (!product.value.storeId) {
    proxy.$modal.msgWarning("该商品没有关联店铺");
    return;
  }
  
  console.log('跳转到店铺主页，storeId:', product.value.storeId);
  // 添加时间戳参数，确保即使路由相同也能刷新
  router.push({
    path: '/shop/store/' + product.value.storeId,
    query: {
      _t: Date.now() // 添加时间戳强制刷新
    }
  }).catch(err => {
    console.error('路由跳转失败:', err);
    proxy.$modal.msgError("跳转失败，请稍后重试");
  });
}

/** 获取评价列表 */
function getReviewList() {
  const productId = route.params.productId;
  getReviewsByProductId(productId).then(response => {
    reviewList.value = response.data || [];
  }).catch(err => {
    console.error('获取评价列表失败:', err);
    reviewList.value = [];
  });
}

/** 联系客服 */
function handleContactService() {
  // 获取当前用户ID
  const currentUserId = userStore.id;
  
  // 检查是否已登录
  if (!currentUserId) {
    proxy.$modal.msgWarning("请先登录");
    router.push('/login');
    return;
  }
  
  // 检查商品是否有店铺信息
  if (!product.value.storeId) {
    proxy.$modal.msgError("商品信息不完整，无法联系客服");
    return;
  }
  
  // 获取店铺主人用户ID（优先使用商品中的userId，如果没有则查询店铺信息）
  let sellerUserId = product.value.userId;
  
  if (!sellerUserId && product.value.storeId) {
    // 如果商品中没有userId，通过storeId查询店铺信息获取userId
    getStorePublic(product.value.storeId).then(response => {
      if (response.data && response.data.userId) {
        sellerUserId = response.data.userId;
        proceedToChat(sellerUserId, product.value.storeId, currentUserId);
      } else {
        proxy.$modal.msgError("无法获取店铺信息，请联系管理员");
      }
    }).catch(() => {
      proxy.$modal.msgError("获取店铺信息失败，请稍后重试");
    });
    return;
  }
  
  proceedToChat(sellerUserId, product.value.storeId, currentUserId);
}

/** 执行跳转到聊天页面 */
function proceedToChat(sellerUserId, storeId, currentUserId) {
  // 检查是否是店铺主人（不能与自己沟通）
  const currentUserIdStr = String(currentUserId);
  const storeUserIdStr = String(sellerUserId);
  if (currentUserIdStr === storeUserIdStr) {
    proxy.$modal.msgWarning("这是您的商品，无法与自己沟通");
    return;
  }
  
  // 跳转到客服沟通页面，传递店铺信息
  router.push({
    path: '/shop/chat',
    query: {
      sellerId: sellerUserId,  // 卖家/店主ID
      storeId: storeId         // 店铺ID
    }
  });
}

// 加载商品详情和评价
function loadProductData() {
  checkUserType();
  getDetail();
  getReviewList();
}

// 监听路由参数变化（当路由参数变化时刷新数据）
watch(() => route.params.productId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadProductData();
  }
});

// 组件激活时刷新数据（用于keep-alive场景，确保从其他页面返回时数据是最新的）
onActivated(() => {
  loadProductData();
});

onMounted(() => {
  loadProductData();
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.product-detail-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
}

  .product-info-glass {
    @extend .glass-card;
    padding: 30px;
    margin-bottom: 20px;
    animation: slideDown 0.6s ease-out;
}

  .product-header {
.product-name {
      font-size: 2rem;
      font-weight: 700;
  margin: 0 0 10px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
}

.product-subtitle {
      font-size: 1rem;
      color: rgba(0, 0, 0, 0.6);
      margin: 0 0 25px;
    }
}

.product-price-box {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    padding: 25px;
    border-radius: 15px;
    margin-bottom: 25px;
    border: 1px solid rgba(102, 126, 234, 0.2);
}

.price-main {
  display: flex;
  align-items: baseline;
    margin-bottom: 15px;
  
  .price-label {
      font-size: 1rem;
      color: rgba(0, 0, 0, 0.6);
      margin-right: 15px;
  }
  
  .price-value {
      font-size: 2.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
  }
}

.product-meta {
  display: flex;
  gap: 30px;
    font-size: 0.95rem;
    color: rgba(0, 0, 0, 0.7);
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: 5px;
      
      .el-icon {
        color: #667eea;
      }
    }
}

.product-store {
  display: flex;
  align-items: center;
    gap: 8px;
    font-size: 1rem;
  cursor: pointer;
    padding: 12px 20px;
    border-radius: 10px;
    margin-bottom: 20px;
    background: rgba(102, 126, 234, 0.05);
    border: 1px solid rgba(102, 126, 234, 0.2);
    transition: all 0.3s;
  
  &:hover {
      background: rgba(102, 126, 234, 0.1);
      transform: translateX(5px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
  }
  
  .el-icon {
      color: #667eea;
      font-size: 1.2rem;
  }
  
  .store-name {
      color: #667eea;
      font-weight: 600;
      flex: 1;
    }
    
    .store-arrow {
      transition: transform 0.3s;
    }
    
    &:hover .store-arrow {
      transform: translateX(5px);
  }
}

.quantity-selector {
  display: flex;
  align-items: center;
    margin: 25px 0;
    padding: 15px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 10px;
  
  .label {
      font-size: 1rem;
      color: rgba(0, 0, 0, 0.7);
    margin-right: 15px;
      font-weight: 500;
    }
    
    .quantity-input {
      :deep(.el-input__wrapper) {
        background: rgba(255, 255, 255, 0.9);
        border-radius: 8px;
      }
  }
  
  .stock-tip {
    margin-left: 15px;
      font-size: 0.9rem;
      color: rgba(0, 0, 0, 0.5);
  }
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  flex-wrap: wrap;
  
  .el-button {
    flex: 1;
      min-width: 140px;
      height: 50px;
      font-size: 1rem;
      font-weight: 600;
      border-radius: 12px;
      transition: all 0.3s;
      
      .el-icon {
        margin-right: 5px;
  }
}

    .glass-button {
      background: rgba(255, 255, 255, 0.9);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #333;
      
      &:hover {
        background: rgba(255, 255, 255, 1);
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      }
    }
    
    .danger-button {
      background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
      color: white;
      border: none;
      
      &:hover {
        box-shadow: 0 6px 20px rgba(255, 77, 79, 0.4);
      }
    }
    
    .success-button {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      color: white;
      border: none;
      
      &:hover {
        box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4);
      }
    }
  }

  .product-detail-glass {
    @extend .glass-card;
    padding: 30px;
    margin-bottom: 20px;
    animation: fadeInUp 0.6s ease-out;
  }

  .review-card-glass {
    @extend .glass-card;
    padding: 30px;
    animation: fadeInUp 0.8s ease-out;
}

.card-header-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .review-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    
    .review-count {
      margin-left: 10px;
      color: rgba(0, 0, 0, 0.5);
      font-size: 1rem;
    }
}

.product-detail-content {
  min-height: 200px;
  line-height: 1.8;
    color: rgba(0, 0, 0, 0.7);
    font-size: 1rem;
  
  :deep(img) {
    max-width: 100%;
    height: auto;
      border-radius: 8px;
      margin: 10px 0;
    }
    
    :deep(p) {
      margin: 10px 0;
    }
  }

  .review-list {
    .review-item {
      padding: 20px 0;
      transition: all 0.3s;
      
      &:hover {
        background: rgba(102, 126, 234, 0.03);
        border-radius: 10px;
        padding: 20px;
        margin: 0 -20px;
      }
    }
  }

  .review-header-info {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .user-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      font-weight: 600;
    }
    
    .user-details {
      .user-name {
        font-weight: 600;
        color: rgba(0, 0, 0, 0.8);
        margin-bottom: 5px;
      }
    }
  }

  .review-time {
    font-size: 0.9rem;
    color: rgba(0, 0, 0, 0.5);
  }

  .review-content {
    color: rgba(0, 0, 0, 0.7);
    line-height: 1.6;
    font-size: 0.95rem;
    padding-left: 55px;
  }

  @media (max-width: 768px) {
    .action-buttons {
      .el-button {
        flex: 1 1 100%;
      }
    }
    
    .product-meta {
      flex-direction: column;
      gap: 10px;
    }
    
    .review-content {
      padding-left: 0;
      margin-top: 10px;
    }
  }
}
</style>

