<template>
  <div class="shop-page-container cart-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">购物车</h1>
        <p class="page-subtitle">管理您的购物车商品</p>
      </div>

      <div v-if="cartList.length > 0">
        <!-- 购物车列表 -->
        <div class="table-container-glass">
          <el-table
            ref="cartTableRef"
            :data="cartList"
            @selection-change="handleSelectionChange"
            :row-class-name="tableRowClassName"
            stripe
          >
          <el-table-column type="selection" width="55" align="center" />
          
            <el-table-column label="商品信息" min-width="300">
              <template #default="scope">
                <div class="cart-product-info">
                  <div class="product-detail">
                    <div class="product-name">{{ scope.row.product?.productName }}</div>
                    <div class="product-subtitle" v-if="scope.row.product?.productSubtitle">
                      {{ scope.row.product?.productSubtitle }}
                    </div>
                    <div class="product-store" v-if="scope.row.product?.storeName">
                      <el-icon><Shop /></el-icon>
                      <span>{{ scope.row.product?.storeName }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="单价" align="center" width="150">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.product?.price }}</span>
              </template>
            </el-table-column>

            <el-table-column label="数量" align="center" width="180">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  :max="scope.row.product?.stock || 999"
                  @change="handleQuantityChange(scope.row)"
                  class="quantity-input"
                />
              </template>
            </el-table-column>

            <el-table-column label="小计" align="center" width="150">
              <template #default="scope">
                <span class="total-price-text">¥{{ calculateItemTotal(scope.row) }}</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" align="center" width="120">
              <template #default="scope">
                <el-button
                  type="danger"
                  link
                  icon="Delete"
                  @click="handleDelete(scope.row)"
                  class="action-btn"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 操作按钮和结算栏 -->
        <div class="cart-actions-bar">
          <div class="actions-left">
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="selectedItems.length === 0"
              @click="handleDeleteSelected"
              class="glass-button danger"
            >
              删除选中
            </el-button>
            <el-button
              type="info"
              plain
              @click="handleClearCart"
              class="glass-button"
            >
              清空购物车
            </el-button>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="cart-footer-glass">
          <div class="footer-left">
            <el-checkbox
              v-model="selectAll"
              @change="handleSelectAll"
              class="select-all-checkbox"
            >
              <span class="checkbox-label">全选</span>
            </el-checkbox>
            <span class="selected-count">已选商品 <span class="count-badge">{{ selectedItems.length }}</span> 件</span>
          </div>
          
          <div class="footer-right">
            <div class="total-info">
              <span class="total-label">合计：</span>
              <span class="total-amount">¥{{ totalAmount }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              :disabled="selectedItems.length === 0"
              @click="handleCheckout"
              class="checkout-btn glow-button-primary"
            >
              去结算 ({{ selectedItems.length }})
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空购物车 -->
      <div v-else class="empty-cart-glass">
        <el-empty
          description="购物车是空的，快去选购吧！"
          :image-size="150"
        >
          <el-button type="primary" @click="goToMarketplace" class="glow-button-primary">去购物</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup name="Cart">
import { listCart, updateCart, delCart, clearCart } from "@/api/shop/cart";
import { Shop } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const router = useRouter();

const cartList = ref([]);
const selectedItems = ref([]);
const selectAll = ref(false);
const cartTableRef = ref(null);

/** 查询购物车列表 */
function getList() {
  listCart().then(response => {
    cartList.value = response.data || [];
  });
}

/** 计算单个商品小计 */
function calculateItemTotal(item) {
  if (item.product && item.product.price) {
    return (item.product.price * item.quantity).toFixed(2);
  }
  return '0.00';
}

/** 计算总金额 */
const totalAmount = computed(() => {
  let total = 0;
  selectedItems.value.forEach(item => {
    if (item.product && item.product.price) {
      total += item.product.price * item.quantity;
    }
  });
  return total.toFixed(2);
});

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  selectedItems.value = selection;
  selectAll.value = selection.length === cartList.value.length && cartList.value.length > 0;
}

/** 全选操作 */
function handleSelectAll(val) {
  if (val) {
    cartTableRef.value?.toggleAllSelection();
  } else {
    cartTableRef.value?.clearSelection();
  }
}

/** 表格行类名 */
function tableRowClassName({ row, rowIndex }) {
  if (rowIndex % 2 === 1) {
    return 'table-row-even';
  }
  return '';
}

/** 修改商品数量 */
function handleQuantityChange(row) {
  updateCart(row.cartId, row.quantity).then(() => {
    proxy.$modal.msgSuccess("数量修改成功");
  });
}

/** 删除购物车商品 */
function handleDelete(row) {
  proxy.$modal.confirm('确认删除该商品吗？').then(() => {
    return delCart(row.cartId);
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功");
    getList();
  }).catch(() => {});
}

/** 删除选中商品 */
function handleDeleteSelected() {
  if (selectedItems.value.length === 0) {
    proxy.$modal.msgWarning("请选择要删除的商品");
    return;
  }
  
  const cartIds = selectedItems.value.map(item => item.cartId);
  proxy.$modal.confirm('确认删除选中的商品吗？').then(() => {
    return delCart(cartIds.join(','));
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功");
    getList();
  }).catch(() => {});
}

/** 清空购物车 */
function handleClearCart() {
  proxy.$modal.confirm('确认清空购物车吗？').then(() => {
    return clearCart();
  }).then(() => {
    proxy.$modal.msgSuccess("购物车已清空");
    getList();
  }).catch(() => {});
}

/** 去结算 */
function handleCheckout() {
  if (selectedItems.value.length === 0) {
    proxy.$modal.msgWarning("请选择要结算的商品");
    return;
  }
  
  // 将选中的商品传递到结算页面
  const checkoutData = selectedItems.value.map(item => ({
    cartId: item.cartId,
    productId: item.productId,
    productName: item.product?.productName,
    mainImage: item.product?.mainImage,
    price: item.product?.price,
    quantity: item.quantity,
    storeId: item.product?.storeId,
    storeName: item.product?.storeName
  }));
  
  router.push({
    path: '/shop/checkout',
    query: {
      items: JSON.stringify(checkoutData)
    }
  });
}

/** 去商城购物 */
function goToMarketplace() {
  router.push({ path: '/shop/marketplace' });
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.cart-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .table-container-glass {
    :deep(.el-table) {
      background: transparent;
      
      .el-table__header-wrapper {
        th {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
          color: #333;
          font-weight: 600;
          border-bottom: 2px solid rgba(102, 126, 234, 0.2);
        }
      }
      
      .el-table__body-wrapper {
        .table-row-even {
          background: rgba(102, 126, 234, 0.02);
        }
        
        .el-table__row {
          transition: all 0.3s;
          
          &:hover {
            background: rgba(102, 126, 234, 0.1) !important;
            transform: scale(1.01);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
          }
        }
      }
    }
  }

  .cart-product-info {
    display: flex;
    align-items: flex-start;
    gap: 15px;
  }

  .product-detail {
    flex: 1;
  }

  .product-name {
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 6px;
    color: #333;
    line-height: 1.4;
  }

  .product-subtitle {
    font-size: 0.85rem;
    color: #999;
    margin-bottom: 8px;
    line-height: 1.4;
  }

  .product-store {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 0.8rem;
    color: #909399;
    margin-top: 8px;
    
    .el-icon {
      font-size: 14px;
      color: #667eea;
    }
  }

  .price-text {
    font-size: 1.1rem;
    color: #f56c6c;
    font-weight: 600;
  }

  .total-price-text {
    font-size: 1.2rem;
    color: #f56c6c;
    font-weight: 700;
  }

  .quantity-input {
    :deep(.el-input-number__decrease),
    :deep(.el-input-number__increase) {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;
      
      &:hover {
        background: rgba(102, 126, 234, 0.2);
      }
    }
  }

  .action-btn {
    transition: all 0.3s;
    
    &:hover {
      transform: scale(1.1);
    }
  }

  .cart-actions-bar {
    margin-top: 20px;
    padding: 15px 20px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .actions-left {
    display: flex;
    gap: 10px;
  }

  .glass-button {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #333;
    transition: all 0.3s;
    
    &:hover:not(:disabled) {
      background: rgba(255, 255, 255, 1);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    &.danger {
      color: $danger-color;
      border-color: rgba(245, 108, 108, 0.3);
      
      &:hover:not(:disabled) {
        background: rgba(245, 108, 108, 0.1);
        border-color: $danger-color;
      }
    }
  }

  .cart-footer-glass {
    @extend .glass-card;
    margin-top: 20px;
    padding: 25px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: sticky;
    bottom: 20px;
    z-index: 10;
  }

  .footer-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .select-all-checkbox {
    :deep(.el-checkbox__label) {
      font-weight: 600;
      color: #333;
    }
  }

  .checkbox-label {
    font-size: 1rem;
    font-weight: 600;
  }

  .selected-count {
    font-size: 0.95rem;
    color: #666;
    
    .count-badge {
      display: inline-block;
      padding: 2px 8px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 10px;
      font-weight: 600;
      margin: 0 3px;
    }
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 25px;
  }

  .total-info {
    display: flex;
    align-items: baseline;
    gap: 10px;
    
    .total-label {
      font-size: 1.1rem;
      color: #666;
      font-weight: 500;
    }
    
    .total-amount {
      font-size: 2rem;
      color: #f56c6c;
      font-weight: 700;
    }
  }

  .checkout-btn {
    padding: 12px 40px;
    font-size: 1.1rem;
    font-weight: 600;
    min-width: 150px;
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .empty-cart-glass {
    @extend .glass-card;
    padding: 60px 20px;
    text-align: center;
    margin-top: 30px;
    
    :deep(.el-empty__description) {
      color: #666;
      font-size: 1rem;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .cart-page {
    .page-title {
      font-size: 2rem;
    }
    
    .cart-footer-glass {
      flex-direction: column;
      gap: 20px;
      padding: 20px;
    }
    
    .footer-left,
    .footer-right {
      width: 100%;
      justify-content: space-between;
    }
    
    .checkout-btn {
      width: 100%;
    }
  }
}
</style>

