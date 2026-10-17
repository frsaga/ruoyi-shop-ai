<template>
  <div class="shop-page-container checkout-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">确认订单</h1>
        <p class="page-subtitle">确认订单信息并完成支付</p>
      </div>

      <!-- 收货地址选择 -->
      <div class="address-section-glass">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Location /></el-icon>
            收货地址
          </h2>
          <el-button type="primary" link class="add-address-btn" @click="handleAddAddress">
            <el-icon><Plus /></el-icon>
            新增收货地址
          </el-button>
        </div>

        <div v-if="addressList.length > 0" class="address-list">
          <div
            v-for="address in addressList"
            :key="address.addressId"
            class="address-card-glass"
            :class="{ active: selectedAddressId === address.addressId }"
            @click="selectedAddressId = address.addressId"
          >
            <el-radio v-model="selectedAddressId" :label="address.addressId" class="address-radio">
              <div class="address-info">
                <div class="address-user">
                  <span class="name">{{ address.receiverName }}</span>
                  <span class="phone">{{ address.receiverPhone }}</span>
                  <el-tag v-if="address.isDefault === '1'" type="success" size="small" class="default-tag">默认</el-tag>
                </div>
                <div class="address-detail">
                  <el-icon><Location /></el-icon>
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.address }}
                </div>
              </div>
            </el-radio>
          </div>
        </div>
        <div v-else class="empty-address-glass">
          <el-empty description="暂无收货地址，请先添加" :image-size="100">
            <el-button type="primary" class="glow-button-primary" @click="handleAddAddress">添加收货地址</el-button>
          </el-empty>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="products-section-glass">
          <h2 class="section-title">
            <el-icon><ShoppingCart /></el-icon>
            商品信息
          </h2>
        <div class="table-container-glass">
          <el-table :data="orderItems" style="width: 100%" class="checkout-table">
            <el-table-column label="商品" min-width="300">
              <template #default="scope">
                <div class="product-info">
                  <span class="product-name">{{ scope.row.productName }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="单价" align="center" width="150">
              <template #default="scope">
                <span class="price-text">¥{{ scope.row.price }}</span>
              </template>
            </el-table-column>

            <el-table-column label="数量" align="center" width="100">
              <template #default="scope">
                <span class="quantity-text">×{{ scope.row.quantity }}</span>
              </template>
            </el-table-column>

            <el-table-column label="小计" align="center" width="150">
              <template #default="scope">
                <span class="total-price-text">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="remark-section-glass">
        <h2 class="section-title">
          <el-icon><Edit /></el-icon>
          订单备注
        </h2>
        <el-input
          v-model="orderForm.remark"
          type="textarea"
          :rows="3"
          placeholder="选填，可以告诉卖家您的特殊需求"
          maxlength="200"
          show-word-limit
          class="remark-input"
        />
      </div>

      <!-- 费用明细 -->
      <div class="fee-section-glass">
        <h2 class="section-title">
          <el-icon><Money /></el-icon>
          费用明细
        </h2>
        <div class="fee-detail">
          <div class="fee-item">
            <span class="fee-label">商品总额：</span>
            <span class="fee-amount">¥{{ totalAmount }}</span>
          </div>
          <div class="fee-item">
            <span class="fee-label">运费：</span>
            <span class="fee-amount">¥{{ freight }}</span>
          </div>
          <el-divider />
          <div class="fee-item total-fee">
            <span class="fee-label">应付总额：</span>
            <span class="total-amount">¥{{ paymentAmount }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-bar-glass">
        <el-button class="glass-button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回购物车
        </el-button>
        <el-button
          type="primary"
          size="large"
          class="glow-button-primary submit-btn"
          :disabled="!selectedAddressId"
          @click="handleSubmitOrder"
        >
          <el-icon><Check /></el-icon>
          提交订单
        </el-button>
      </div>
    </div>

    <!-- 新增/编辑收货地址对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      title="收货地址"
      width="600px"
      :close-on-click-modal="false"
      class="address-dialog"
    >
      <div class="dialog-content-glass">
        <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="100px">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="receiverPhone">
            <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="所在地区" prop="region">
            <el-col :span="8">
              <el-input v-model="addressForm.province" placeholder="省份" />
            </el-col>
            <el-col :span="8">
              <el-input v-model="addressForm.city" placeholder="城市" />
            </el-col>
            <el-col :span="8">
              <el-input v-model="addressForm.district" placeholder="区县" />
            </el-col>
          </el-form-item>
          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="addressForm.address"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址"
            />
          </el-form-item>
          <el-form-item label="默认地址">
            <el-switch v-model="addressForm.isDefault" active-value="1" inactive-value="0" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="glass-button" @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" class="glow-button-primary" @click="handleSaveAddress">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 支付确认对话框 -->
    <el-dialog
      v-model="paymentDialogVisible"
      title="确认支付"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="payment-dialog"
    >
      <div class="dialog-content-glass">
        <div class="payment-confirm-content">
          <el-alert
            type="success"
            :closable="false"
            show-icon
            class="success-alert"
          >
            <template #title>
              <div class="alert-content">
                <div class="alert-title">订单提交成功</div>
                <div class="alert-subtitle" v-if="createdOrders.length === 1">
                  订单号：{{ currentOrderNos[0] }}
                </div>
                <div class="alert-subtitle" v-else>
                  共创建 {{ createdOrders.length }} 个订单（按店铺分组）
                </div>
              </div>
            </template>
          </el-alert>
          
          <!-- 多个订单时显示订单列表 -->
          <div v-if="createdOrders.length > 1" class="orders-list">
            <div v-for="(order, index) in createdOrders" :key="order.orderId" class="order-item-glass">
              <div class="order-item-header">订单{{ index + 1 }}：{{ order.orderNo }}</div>
              <div class="order-item-store">店铺：{{ order.storeName || '未知店铺' }}</div>
              <div class="order-item-detail">
                <span>商品总额：¥{{ order.totalAmount }}</span>
                <span>运费：¥{{ order.freight }}</span>
                <span class="order-total">合计：¥{{ order.paymentAmount }}</span>
              </div>
            </div>
          </div>
          
          <div class="payment-detail">
            <div class="payment-row">
              <span class="payment-label">商品总额：</span>
              <span class="payment-value">¥{{ totalAmount }}</span>
            </div>
            <div class="payment-row">
              <span class="payment-label">运费：</span>
              <span class="payment-value">¥{{ freight }}</span>
            </div>
            <el-divider />
            <div class="payment-row total-payment-row">
              <span class="payment-label">应付总额：</span>
              <span class="payment-total-amount">¥{{ paymentAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button class="glass-button" @click="handleCancelPayment">稍后支付</el-button>
          <el-button type="primary" class="glow-button-primary" :loading="paymentLoading" @click="handleConfirmPayment">
            确认支付
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Checkout">
import { createOrder, createOrdersBatch, payOrder, payOrdersBatch } from "@/api/shop/order";
import { delCart } from "@/api/shop/cart";
import { listAddress, addAddress } from "@/api/shop/address";
import { getPublicProduct } from "@/api/shop/product";
import { Location, Plus, ShoppingCart, Edit, Money, ArrowLeft, Check } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();

const orderItems = ref([]);
const addressList = ref([]);
const selectedAddressId = ref(null);
const currentOrderIds = ref([]);
const currentOrderNos = ref([]);
const createdOrders = ref([]);

const addressDialogVisible = ref(false);
const paymentDialogVisible = ref(false);
const paymentLoading = ref(false);

const data = reactive({
  orderForm: {
    remark: ''
  },
  addressForm: {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    address: '',
    isDefault: '0'
  },
  addressRules: {
    receiverName: [
      { required: true, message: "收货人不能为空", trigger: "blur" }
    ],
    receiverPhone: [
      { required: true, message: "手机号不能为空", trigger: "blur" },
      { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
    ],
    address: [
      { required: true, message: "详细地址不能为空", trigger: "blur" }
    ]
  }
});

const { orderForm, addressForm, addressRules } = toRefs(data);

/** 计算商品总额 */
const totalAmount = computed(() => {
  let total = 0;
  orderItems.value.forEach(item => {
    total += item.price * item.quantity;
  });
  return total.toFixed(2);
});

/** 运费 */
const freight = computed(() => {
  // 简单示例：满100免运费，否则10元
  return totalAmount.value >= 100 ? '0.00' : '10.00';
});

/** 应付总额 */
const paymentAmount = computed(() => {
  return (parseFloat(totalAmount.value) + parseFloat(freight.value)).toFixed(2);
});

/** 获取收货地址列表 */
function getAddressList() {
  listAddress().then(response => {
    addressList.value = response.data || [];
    // 设置默认地址
    const defaultAddr = addressList.value.find(addr => addr.isDefault === '1');
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.addressId;
    } else if (addressList.value.length > 0) {
      selectedAddressId.value = addressList.value[0].addressId;
    }
  });
}

/** 新增收货地址 */
function handleAddAddress() {
  addressDialogVisible.value = true;
  addressForm.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    address: '',
    isDefault: '0'
  };
}

/** 保存收货地址 */
function handleSaveAddress() {
  proxy.$refs.addressFormRef.validate((valid) => {
    if (valid) {
      addAddress(addressForm.value).then(() => {
        proxy.$modal.msgSuccess("地址保存成功");
        addressDialogVisible.value = false;
        getAddressList();
      });
    }
  });
}

/** 提交订单 */
async function handleSubmitOrder() {
  if (!selectedAddressId.value) {
    proxy.$modal.msgWarning("请选择收货地址");
    return;
  }

  const selectedAddress = addressList.value.find(addr => addr.addressId === selectedAddressId.value);
  if (!selectedAddress) {
    proxy.$modal.msgWarning("收货地址信息有误");
    return;
  }

  // 检查并补充商品信息（如果缺少storeId）
  const itemsNeedingProductInfo = [];
  for (let i = 0; i < orderItems.value.length; i++) {
    const item = orderItems.value[i];
    const storeId = item.storeId || item.product?.storeId;
    if (!storeId && item.productId) {
      itemsNeedingProductInfo.push({ index: i, productId: item.productId });
    }
  }

  // 如果有商品缺少storeId，先获取商品信息
  if (itemsNeedingProductInfo.length > 0) {
    try {
      const productInfoPromises = itemsNeedingProductInfo.map(({ index, productId }) =>
        getPublicProduct(productId).then(response => ({
          index,
          product: response.data
        })).catch(error => {
          console.error(`获取商品信息失败: ${productId}`, error);
          return { index, product: null };
        })
      );
      
      const productInfos = await Promise.all(productInfoPromises);
      
      // 补充商品信息
      productInfos.forEach(({ index, product }) => {
        if (product) {
          const item = orderItems.value[index];
          if (!item.storeId && product.storeId) {
            item.storeId = product.storeId;
            item.storeName = product.storeName;
          }
          if (!item.product) {
            item.product = product;
          } else {
            // 合并商品信息
            item.product.storeId = product.storeId;
            item.product.storeName = product.storeName;
          }
        }
      });
    } catch (error) {
      console.error('获取商品信息失败:', error);
      proxy.$modal.msgError("获取商品信息失败，请重试");
      return;
    }
  }

  // 按店铺分组商品
  const ordersByStore = {};
  let hasError = false;
  let errorMessage = '';
  
  for (const item of orderItems.value) {
    // 获取店铺ID，优先使用item.storeId，其次使用product.storeId
    const storeId = item.storeId || item.product?.storeId;
    if (!storeId) {
      hasError = true;
      errorMessage = "商品信息不完整，缺少店铺信息，商品：" + (item.productName || item.product?.productName || '未知商品');
      break;
    }
    
    // 获取店铺名称
    const storeName = item.storeName || item.product?.storeName || '未知店铺';
    
    if (!ordersByStore[storeId]) {
      ordersByStore[storeId] = {
        storeId: storeId,
        storeName: storeName,
        items: [],
        totalAmount: 0,
        freight: 0,
        paymentAmount: 0
      };
    }
    
    ordersByStore[storeId].items.push({
      productId: item.productId,
      productName: item.productName || item.product?.productName,
      productImage: item.productImage || item.product?.mainImage || item.mainImage || '',
      currentPrice: item.price || item.product?.price,
      quantity: item.quantity
    });
    
    const itemTotal = (item.price || item.product?.price || 0) * item.quantity;
    ordersByStore[storeId].totalAmount += itemTotal;
  }

  if (hasError) {
    proxy.$modal.msgError(errorMessage);
    return;
  }

  // 计算每个店铺的运费和总金额
  const orderList = Object.values(ordersByStore).map(storeOrder => {
    // 每个店铺单独计算运费：满100免运费，否则10元
    storeOrder.freight = parseFloat(storeOrder.totalAmount) >= 100 ? 0 : 10;
    storeOrder.paymentAmount = parseFloat(storeOrder.totalAmount) + storeOrder.freight;
    
    return {
      storeId: storeOrder.storeId,
      receiverName: selectedAddress.receiverName,
      receiverPhone: selectedAddress.receiverPhone,
      receiverProvince: selectedAddress.province,
      receiverCity: selectedAddress.city,
      receiverDistrict: selectedAddress.district,
      receiverAddress: selectedAddress.address,
      totalAmount: storeOrder.totalAmount.toFixed(2),
      freight: storeOrder.freight.toFixed(2),
      paymentAmount: storeOrder.paymentAmount.toFixed(2),
      remark: orderForm.value.remark,
      orderItems: storeOrder.items
    };
  });

  // 批量创建订单
  createOrdersBatch(orderList).then(response => {
    createdOrders.value = response.data || [];
    currentOrderIds.value = createdOrders.value.map(order => order.orderId);
    currentOrderNos.value = createdOrders.value.map(order => order.orderNo);
    
    // 删除购物车中已下单的商品
    const cartIds = orderItems.value.map(item => item.cartId).filter(id => id);
    if (cartIds.length > 0) {
      delCart(cartIds.join(','));
    }
    
    // 弹出支付确认对话框
    paymentDialogVisible.value = true;
  }).catch(error => {
    console.error('订单提交失败:', error);
    proxy.$modal.msgError(error.msg || error.message || "订单提交失败，请重试");
  });
}

/** 确认支付 */
function handleConfirmPayment() {
  if (!currentOrderIds.value || currentOrderIds.value.length === 0) {
    proxy.$modal.msgError("订单信息有误");
    return;
  }

  paymentLoading.value = true;
  
  // 批量支付订单
  if (currentOrderIds.value.length === 1) {
    // 单个订单，使用单订单支付接口
    payOrder(currentOrderIds.value[0]).then(() => {
      paymentLoading.value = false;
      paymentDialogVisible.value = false;
      
      // 跳转到订单页面
      router.push({ 
        path: '/shop/order',
        query: { 
          refresh: 'true',
          paid: 'true',
          orderNo: currentOrderNos.value[0] 
        }
      });
    }).catch(error => {
      paymentLoading.value = false;
      proxy.$modal.msgError(error.msg || "支付失败，请重试");
    });
  } else {
    // 多个订单，使用批量支付接口
    payOrdersBatch(currentOrderIds.value).then(() => {
      paymentLoading.value = false;
      paymentDialogVisible.value = false;
      proxy.$modal.msgSuccess("支付成功！共支付 " + currentOrderIds.value.length + " 个订单");
      
      // 跳转到订单页面
      router.push({ 
        path: '/shop/order',
        query: { 
          refresh: 'true',
          paid: 'true'
        }
      });
    }).catch(error => {
      paymentLoading.value = false;
      proxy.$modal.msgError(error.msg || "支付失败，请重试");
    });
  }
}

/** 取消支付（稍后支付） */
function handleCancelPayment() {
  paymentDialogVisible.value = false;
  // 跳转到订单页面，用户可以在订单页面稍后支付
  router.push({ 
    path: '/shop/order',
    query: { 
      refresh: 'true'
    }
  });
}

/** 返回购物车 */
function goBack() {
  router.back();
}

onMounted(() => {
  // 从路由参数获取商品列表
  const items = route.query.items;
  if (items) {
    try {
      orderItems.value = JSON.parse(items);
    } catch (e) {
      proxy.$modal.msgError("订单数据有误");
      router.push({ path: '/shop/cart' });
      return;
    }
  }
  
  if (orderItems.value.length === 0) {
    proxy.$modal.msgWarning("没有选择商品");
    router.push({ path: '/shop/cart' });
    return;
  }

  getAddressList();
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.checkout-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .address-section-glass,
  .products-section-glass,
  .remark-section-glass,
  .fee-section-glass {
    @extend .glass-card;
    padding: 25px;
    margin-bottom: 20px;
    animation: slideDown 0.6s ease-out;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .section-title {
    font-size: 1.3rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 10px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0 0 20px;
    
    .el-icon {
      color: #667eea;
    }
  }

  .add-address-btn {
    font-weight: 600;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-2px);
    }
  }

  .address-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .address-card-glass {
    @extend .glass-card;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;
    
    &:hover {
      border-color: rgba(102, 126, 234, 0.3);
      transform: translateY(-3px);
    }
    
    &.active {
      border-color: #667eea;
      background: rgba(102, 126, 234, 0.05);
    }
    
    .address-radio {
      width: 100%;
      
      :deep(.el-radio__label) {
        width: 100%;
      }
    }
  }

  .address-info {
    width: 100%;
  }

  .address-user {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 10px;
    
    .name {
      font-weight: 600;
      font-size: 1rem;
      color: rgba(0, 0, 0, 0.8);
    }
    
    .phone {
      color: rgba(0, 0, 0, 0.6);
      font-size: 0.95rem;
    }
    
    .default-tag {
      font-weight: 600;
    }
  }

  .address-detail {
    color: rgba(0, 0, 0, 0.6);
    font-size: 0.9rem;
    line-height: 1.6;
    display: flex;
    align-items: center;
    gap: 5px;
    
    .el-icon {
      color: #667eea;
    }
  }

  .empty-address-glass {
    @extend .glass-card;
    padding: 40px;
    text-align: center;
  }

  .table-container-glass {
    background: transparent;
    padding: 0;
    
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
        .el-table__row {
          transition: all 0.3s;
          
          &:hover {
            background: rgba(102, 126, 234, 0.05) !important;
          }
        }
      }
    }
  }

  .product-info {
    display: flex;
    align-items: center;
    
    .product-name {
      font-size: 0.95rem;
      color: rgba(0, 0, 0, 0.8);
      font-weight: 500;
    }
  }

  .price-text {
    color: #667eea;
    font-size: 1rem;
    font-weight: 600;
  }

  .quantity-text {
    color: rgba(0, 0, 0, 0.7);
    font-size: 0.95rem;
  }

  .total-price-text {
    color: #ff4d4f;
    font-weight: 600;
    font-size: 1rem;
  }

  .remark-input {
    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.9);
      border-radius: 10px;
      border: 1px solid rgba(102, 126, 234, 0.2);
    }
  }

  .fee-detail {
    padding: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border-radius: 15px;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }

  .fee-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    font-size: 1rem;
    color: rgba(0, 0, 0, 0.7);
    
    &.total-fee {
      font-size: 1.2rem;
      font-weight: 600;
      margin-top: 10px;
    }
  }

  .fee-label {
    color: rgba(0, 0, 0, 0.7);
  }

  .fee-amount {
    color: rgba(0, 0, 0, 0.8);
    font-weight: 500;
  }

  .total-amount {
    background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-size: 1.8rem;
    font-weight: 700;
  }

  .action-bar-glass {
    @extend .glass-card;
    padding: 20px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30px;
    animation: fadeInUp 0.6s ease-out;
    
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
    
    .submit-btn {
      font-size: 1.1rem;
      padding: 15px 40px;
      height: auto;
      
      .el-icon {
        margin-right: 5px;
      }
    }
  }

  .address-dialog,
  .payment-dialog {
    :deep(.el-dialog) {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    
    :deep(.el-dialog__header) {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      padding: 20px;
      border-radius: 20px 20px 0 0;
    }
    
    :deep(.el-dialog__title) {
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .dialog-content-glass {
    padding: 20px 0;
  }

  .payment-confirm-content {
    padding: 10px 0;
  }

  .success-alert {
    margin-bottom: 20px;
    
    .alert-content {
      .alert-title {
        font-size: 1.1rem;
        font-weight: 600;
        margin-bottom: 8px;
        color: #67c23a;
      }
      
      .alert-subtitle {
        font-size: 0.95rem;
        color: rgba(0, 0, 0, 0.6);
      }
    }
  }

  .orders-list {
    margin-bottom: 20px;
  }

  .order-item-glass {
    @extend .glass-card;
    padding: 15px;
    margin-bottom: 15px;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }

  .order-item-header {
    font-weight: 600;
    margin-bottom: 8px;
    color: rgba(0, 0, 0, 0.8);
  }

  .order-item-store {
    font-size: 0.9rem;
    color: rgba(0, 0, 0, 0.6);
    margin-bottom: 8px;
  }

  .order-item-detail {
    display: flex;
    justify-content: space-between;
    font-size: 0.95rem;
    gap: 10px;
    
    .order-total {
      color: #ff4d4f;
      font-weight: 600;
    }
  }

  .payment-detail {
    padding: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border-radius: 15px;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }

  .payment-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    font-size: 1rem;
    
    &.total-payment-row {
      font-size: 1.2rem;
      padding-top: 15px;
      border-top: 2px solid rgba(102, 126, 234, 0.2);
      margin-top: 10px;
    }
  }

  .payment-label {
    color: rgba(0, 0, 0, 0.7);
    font-weight: 500;
  }

  .payment-value {
    color: rgba(0, 0, 0, 0.8);
    font-weight: 600;
  }

  .payment-total-amount {
    background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-size: 1.8rem;
    font-weight: 700;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  @media (max-width: 768px) {
    .action-bar-glass {
      flex-direction: column;
      gap: 15px;
      
      .submit-btn {
        width: 100%;
      }
    }
    
    .address-card-glass {
      padding: 15px;
    }
    
    .order-item-detail {
      flex-direction: column;
      gap: 5px;
    }
  }
}
</style>

