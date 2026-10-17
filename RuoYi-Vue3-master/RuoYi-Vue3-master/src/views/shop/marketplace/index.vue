<template>
  <div class="shop-page-container marketplace-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">商城首页</h1>
        <p class="page-subtitle">发现优质商品，享受购物乐趣</p>
      </div>

    <!-- 搜索区域 -->
      <div class="search-card-glass">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
        <el-form-item label="商品分类">
          <el-cascader
            v-model="selectedCategory"
            :options="categoryOptions"
            :props="{ value: 'categoryId', label: 'categoryName', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="请选择商品分类"
            clearable
            style="width: 280px"
            @change="handleCategoryChange"
            class="category-cascader"
          />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input
            v-model="queryParams.productName"
            placeholder="请输入商品名称"
            clearable
              style="width: 280px"
            @keyup.enter="handleQuery"
              class="search-input"
          />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery" class="glow-button-primary">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery" class="glass-button">重置</el-button>
        </el-form-item>
      </el-form>
      </div>

    <!-- 商品列表 -->
      <div class="product-grid-container" v-loading="loading">
        <div 
          v-for="product in productList" 
          :key="product.productId"
          class="product-card-glass"
          @click="handleProductDetail(product)"
        >
          <div class="product-card-inner">
            <!-- 商品状态标签 -->
            <div class="product-status-badge" v-if="product.status !== '1' || product.stock <= 0">
              <el-tag 
                :type="product.status !== '1' ? 'warning' : 'danger'" 
                effect="dark"
                size="small"
              >
                {{ product.status !== '1' ? '已下架' : '已售罄' }}
              </el-tag>
            </div>
            
          <!-- 商品信息 -->
          <div class="product-info">
              <h3 class="product-name" :title="product.productName" @click.stop="handleProductDetail(product)">
                {{ product.productName }}
              </h3>
            <p class="product-subtitle" v-if="product.productSubtitle" :title="product.productSubtitle">
              {{ product.productSubtitle }}
            </p>
            
            <div class="product-meta">
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              <div class="product-stats">
                <span class="sales">已售 {{ product.salesCount || 0 }}</span>
                  <span class="stock" :class="{ 'low-stock': product.stock < 10 }">库存 {{ product.stock }}</span>
              </div>
            </div>
            
            <div class="product-store">
              <el-icon><Shop /></el-icon>
              <span>{{ product.storeName || '官方店铺' }}</span>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="product-actions">
            <el-button
                v-if="isBuyer"
              type="primary"
              size="small"
              icon="ShoppingCart"
              :disabled="product.status !== '1' || product.stock <= 0"
              @click.stop="handleAddToCart(product)"
                class="cart-btn"
            >
              {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
            </el-button>
            <el-button
              :type="product.isFavorite ? 'danger' : 'default'"
              size="small"
              :icon="product.isFavorite ? 'StarFilled' : 'Star'"
              @click.stop="handleToggleFavorite(product)"
              circle
                class="favorite-btn"
            />
            </div>
          </div>
        </div>
        <!-- 空状态 -->
        <div v-if="!loading && productList.length === 0" class="empty-product-glass" style="grid-column: 1 / -1; width: 100%;">
          <el-empty description="暂无商品" />
        </div>
      </div>

    <!-- 分页 -->
      <div class="pagination-wrapper">
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
      </div>
    </div>

    <!-- 购物车悬浮按钮（仅买家显示） -->
    <el-badge v-if="isBuyer" :value="cartCount" :max="99" class="cart-badge">
      <el-button
        type="primary"
        circle
        size="large"
        class="cart-float-btn"
        @click="goToCart"
      >
        <el-icon :size="24"><ShoppingCart /></el-icon>
      </el-button>
    </el-badge>
  </div>
</template>

<script setup name="Marketplace">
import { listPublicProduct } from "@/api/shop/product";
import { addToCart, listCart } from "@/api/shop/cart";
import { checkFavorite, addFavorite, cancelFavorite } from "@/api/shop/favorite";
import { getCategoryTree } from "@/api/shop/category";
import { Shop, ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue';
import useUserStore from '@/store/modules/user';

const { proxy } = getCurrentInstance();
const userStore = useUserStore();

const productList = ref([]);
const loading = ref(true);
const total = ref(0);
const cartCount = ref(0);
const isBuyer = ref(true);
const categoryOptions = ref([]);
const selectedCategory = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    productName: null,
    categoryId: null
    // 注意：公开接口会自动设置为status=1，这里不需要设置
  }
});

const { queryParams } = toRefs(data);

/** 查询商品列表 */
function getList() {
  loading.value = true;
  listPublicProduct(queryParams.value).then(response => {
    productList.value = response.rows;
    total.value = response.total;
    
    // 检查每个商品的收藏状态
    productList.value.forEach(product => {
      checkFavoriteStatus(product);
    });
    
    loading.value = false;
  }).catch(error => {
    console.error('获取商品列表失败:', error);
    loading.value = false;
  });
}

/** 检查商品收藏状态 */
function checkFavoriteStatus(product) {
  checkFavorite(product.productId).then(response => {
    product.isFavorite = response.data;
  }).catch(() => {
    product.isFavorite = false;
  });
}

/** 切换收藏状态 */
function handleToggleFavorite(product) {
  if (product.isFavorite) {
    // 取消收藏
    cancelFavorite(product.productId).then(() => {
      product.isFavorite = false;
      proxy.$modal.msgSuccess("已取消收藏");
    });
  } else {
    // 添加收藏
    addFavorite({ productId: product.productId }).then(() => {
      product.isFavorite = true;
      proxy.$modal.msgSuccess("收藏成功");
    }).catch((error) => {
      if (error.response?.data?.msg) {
        proxy.$modal.msgWarning(error.response.data.msg);
      }
    });
  }
}


/** 获取购物车数量 */
function getCartCount() {
  // 只有买家才需要获取购物车数量
  if (!isBuyer.value) {
    return;
  }
  listCart().then(response => {
    if (response.data) {
      cartCount.value = response.data.length;
    }
  }).catch(() => {
    cartCount.value = 0;
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

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  selectedCategory.value = null;
  queryParams.value.categoryId = null;
  queryParams.value.productName = null;
  handleQuery();
}

/** 分类变化处理 */
function handleCategoryChange(value) {
  queryParams.value.categoryId = value || null;
  handleQuery();
}

/** 获取分类列表 */
function getCategoryList() {
  getCategoryTree().then(response => {
    // 处理响应数据：可能是 response.data 或直接是 response
    const data = response.data || response || [];
    categoryOptions.value = Array.isArray(data) ? data : [];
  }).catch(err => {
    console.error("获取分类列表失败:", err);
    // 分类加载失败不影响页面显示，只是分类选择器为空
    categoryOptions.value = [];
  });
}

/** 获取商品状态文本 */
function getStatusText(status) {
  const statusMap = {
    '0': '待审核',
    '1': '在售',
    '2': '已下架'
  };
  return statusMap[status] || '未知';
}

/** 商品详情 */
function handleProductDetail(product) {
  // 添加时间戳参数，确保即使路由相同也能刷新
  proxy.$router.push({ 
    path: '/shop/product/detail/' + product.productId,
    query: {
      _t: Date.now() // 添加时间戳强制刷新
    }
  });
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

  addToCart(product.productId, 1).then(response => {
    proxy.$modal.msgSuccess("已加入购物车");
    getCartCount();  // 刷新购物车数量
  });
}

/** 跳转到购物车 */
function goToCart() {
  proxy.$router.push({ path: '/shop/cart' });
}

// 初始化
onMounted(() => {
  checkUserType();
  // 先加载商品列表，分类列表加载失败不影响页面显示
  getList();
  // 异步加载分类列表
  getCategoryList();
  // 只有买家才获取购物车数量
  if (isBuyer.value) {
    getCartCount();
  }
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.marketplace-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .search-input {
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

  .category-cascader {
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

  .glass-button {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #333;
    transition: all 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 1);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }

  .product-grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 25px;
    margin-top: 30px;
    padding: 0 10px;
  }

  .product-card-glass {
    @extend .glass-card;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    animation: fadeInUp 0.6s ease-out;
    animation-fill-mode: both;
    
    @for $i from 1 through 12 {
      &:nth-child(#{$i}) {
        animation-delay: #{$i * 0.05}s;
      }
    }
    
    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
    }
  }

  .product-card-inner {
    position: relative;
    padding: 20px;
  }
  
  .product-status-badge {
    position: absolute;
    top: 15px;
    right: 15px;
    z-index: 2;
  }

  .product-info {
    margin-bottom: 15px;
  }

  .product-name {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0 0 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #333;
    cursor: pointer;
    transition: color 0.3s;
    line-height: 1.4;
    
    &:hover {
      color: #667eea;
    }
  }

  .product-subtitle {
    font-size: 0.85rem;
    color: #999;
    margin: 0 0 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.4;
  }

  .product-meta {
    margin-bottom: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .product-price {
    display: flex;
    align-items: baseline;
    
    .price-symbol {
      font-size: 0.9rem;
      color: #f56c6c;
      font-weight: 600;
    }
    
    .price-value {
      font-size: 1.8rem;
      color: #f56c6c;
      font-weight: 700;
      margin-left: 2px;
    }
  }

  .product-stats {
    display: flex;
    flex-direction: column;
    gap: 5px;
    font-size: 0.85rem;
    text-align: right;
    
    .sales {
      color: #67c23a;
      font-weight: 600;
    }
    
    .stock {
      color: #909399;
      
      &.low-stock {
        color: #f56c6c;
        font-weight: 600;
      }
    }
  }

  .product-store {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.85rem;
    color: #909399;
    margin-bottom: 15px;
    padding: 8px 12px;
    background: rgba(102, 126, 234, 0.05);
    border-radius: 8px;
    
    .el-icon {
      font-size: 16px;
      color: #667eea;
    }
  }

  .product-actions {
    display: flex;
    gap: 10px;
    padding-top: 15px;
    border-top: 1px solid rgba(102, 126, 234, 0.1);
    
    .cart-btn {
      flex: 1;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      transition: all 0.3s;
      
      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
      }
    }
    
    .favorite-btn {
      transition: all 0.3s;
      
      &:hover {
        transform: scale(1.1) rotate(15deg);
      }
    }
  }

  .pagination-wrapper {
    margin-top: 40px;
    display: flex;
    justify-content: center;
    padding: 20px 0;
  }

  .empty-product-glass {
    @extend .glass-card;
    padding: 60px 20px;
    text-align: center;
    margin: 40px auto;
    max-width: 500px;
  }
}

.cart-badge {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 1000;
  animation: bounceIn 0.6s ease-out;
}

.cart-float-btn {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  
  &:hover {
    transform: scale(1.15) translateY(-5px);
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.6);
  }
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3) translateY(50px);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    transform: scale(1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .marketplace-page {
    .page-title {
      font-size: 2rem;
    }
    
    .product-grid-container {
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      gap: 15px;
    }
    
    .cart-badge {
      bottom: 20px;
      right: 20px;
    }
  }
}

:deep(.el-badge__content) {
  border: 2px solid #fff;
  font-weight: 600;
}
</style>

