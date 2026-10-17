<template>
  <div class="shop-page-container order-management">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">订单管理</h1>
        <p class="page-subtitle">查看和管理您的订单信息</p>
      </div>

      <!-- 搜索区域 -->
      <div class="search-card-glass">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
          <el-form-item label="订单号" prop="orderNo">
            <el-input
              v-model="queryParams.orderNo"
              placeholder="请输入订单号"
              clearable
              style="width: 240px"
              @keyup.enter="handleQuery"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="订单状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable style="width: 200px">
              <el-option label="待支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已发货" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已关闭" :value="4" />
              <el-option label="已取消" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery" class="glow-button-primary">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery" class="glass-button">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table 
          v-loading="loading" 
          :data="orderList"
          :row-class-name="tableRowClassName"
          stripe
        >
          <el-table-column label="订单号" align="center" prop="orderNo" width="200">
            <template #default="scope">
              <span class="order-no-text">{{ scope.row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="收货人" align="center" prop="receiverName" width="120" />
          <el-table-column label="联系电话" align="center" prop="receiverPhone" width="130" />
          <el-table-column label="订单金额" align="center" prop="totalAmount" width="120">
            <template #default="scope">
              <span class="amount-text">¥{{ scope.row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="实付金额" align="center" prop="paymentAmount" width="120">
            <template #default="scope">
              <span class="amount-text primary">¥{{ scope.row.paymentAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="订单状态" align="center" prop="status" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)" 
                effect="dark"
                class="status-badge animated"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              <span class="time-text">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="320" fixed="right">
            <template #default="scope">
              <el-button 
                link 
                type="primary" 
                icon="View" 
                @click="handleDetail(scope.row)" 
                v-hasPermi="['shop:order:detail']"
                class="action-btn"
              >详情</el-button>
              
              <!-- 买家操作 -->
              <el-button 
                link 
                type="success" 
                v-if="scope.row.status === 0" 
                @click="handlePay(scope.row)" 
                v-hasPermi="['shop:order:pay']"
                class="action-btn pay-btn"
              >支付</el-button>
              <el-button 
                link 
                type="success" 
                v-if="scope.row.status === 2" 
                @click="handleConfirm(scope.row)" 
                v-hasPermi="['shop:order:confirm']"
                class="action-btn"
              >确认收货</el-button>
              <el-button 
                link 
                type="warning" 
                v-if="scope.row.status === 3" 
                @click="handleReview(scope.row)" 
                v-hasPermi="['shop:review:add']"
                :disabled="scope.row.allReviewed"
                class="action-btn review-btn"
                :class="{ 'disabled': scope.row.allReviewed }"
              >
                {{ scope.row.allReviewed ? '已评价' : '评价' }}
              </el-button>
              <el-button 
                link 
                type="danger" 
                v-if="scope.row.status === 0" 
                @click="handleCancel(scope.row)" 
                v-hasPermi="['shop:order:cancel']"
                class="action-btn"
              >取消</el-button>
              
              <!-- 卖家操作 -->
              <el-button 
                link 
                type="warning" 
                v-if="scope.row.status === 1" 
                @click="handleSend(scope.row)" 
                v-hasPermi="['shop:order:send']"
                class="action-btn"
              >发货</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
          class="pagination-container"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog 
      title="订单详情" 
      v-model="open" 
      width="900px" 
      append-to-body
      class="order-dialog"
    >
      <div class="dialog-content-glass">
        <el-descriptions :column="2" border class="order-descriptions">
          <el-descriptions-item label="订单号">
            <span class="order-no-text">{{ orderDetail.orderNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(orderDetail.status)" effect="dark">
              {{ getStatusText(orderDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ orderDetail.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ orderDetail.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            <span class="address-text">
              {{ orderDetail.receiverProvince }} {{ orderDetail.receiverCity }} {{ orderDetail.receiverDistrict }} {{ orderDetail.receiverAddress }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span class="amount-text">¥{{ orderDetail.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="amount-text primary">¥{{ orderDetail.paymentAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="运费">
            <span class="amount-text">¥{{ orderDetail.freight || 0 }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">
            <el-tag :type="orderDetail.paymentType === 1 ? 'success' : 'info'" effect="dark">
              {{ orderDetail.paymentType === 1 ? '在线支付' : '货到付款' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            <span class="time-text">{{ parseTime(orderDetail.createTime) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" :span="2" v-if="orderDetail.paymentTime">
            <span class="time-text">{{ parseTime(orderDetail.paymentTime) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="2" v-if="orderDetail.remark">
            <span class="remark-text">{{ orderDetail.remark }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>
          <span class="divider-text">订单商品</span>
        </el-divider>
        <div class="order-items-table">
          <el-table :data="orderDetail.orderItems" style="width: 100%">
            <el-table-column label="商品名称" prop="productName" min-width="200" />
            <el-table-column label="单价" prop="currentPrice" width="120">
              <template #default="scope">
                <span class="amount-text">¥{{ scope.row.currentPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="数量" prop="quantity" width="100" align="center">
              <template #default="scope">
                <el-tag type="info" effect="dark">{{ scope.row.quantity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="小计" prop="totalPrice" width="120" align="right">
              <template #default="scope">
                <span class="amount-text primary">¥{{ scope.row.totalPrice }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false" class="glass-button">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog 
      title="商品评价" 
      v-model="reviewOpen" 
      width="700px" 
      append-to-body
      class="review-dialog"
    >
      <div class="dialog-content-glass">
        <el-form :model="reviewForm" :rules="reviewRules" ref="reviewRef" label-width="100px">
          <el-form-item label="选择商品" prop="productId">
            <el-select 
              v-model="reviewForm.productId" 
              placeholder="请选择要评价的商品"
              style="width: 100%"
              @change="handleProductChange"
              class="review-select"
            >
              <el-option
                v-for="item in currentOrderItems"
                :key="item.productId"
                :label="item.productName"
                :value="item.productId"
                :disabled="item.reviewed"
              >
                <span>{{ item.productName }}</span>
                <el-tag v-if="item.reviewed" type="success" size="small" style="float: right; margin-left: 10px;">已评价</el-tag>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="评分" prop="rating">
            <el-rate 
              v-model="reviewForm.rating" 
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              show-text
              size="large"
            />
          </el-form-item>
          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入评价内容（至少10个字）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="reviewOpen = false" class="glass-button">取 消</el-button>
          <el-button type="primary" @click="submitReview" class="glow-button-primary">提 交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Order">
import { listOrder, getOrder, sendOrder, confirmOrder, payOrder, cancelOrder } from "@/api/shop/order";
import { getOrderItemsWithReviewStatus, addReview } from "@/api/shop/review";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

const orderList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const orderDetail = ref({});
const reviewOpen = ref(false);
const reviewRef = ref();
const currentOrderItems = ref([]);
const currentOrder = ref({});

const statusOptions = ref([
  { label: "待支付", value: 0 },
  { label: "已支付", value: 1 },
  { label: "已发货", value: 2 },
  { label: "已完成", value: 3 },
  { label: "已关闭", value: 4 },
  { label: "已取消", value: 5 }
]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    status: null,
  },
  reviewForm: {
    productId: null,
    orderId: null,
    rating: 5,
    content: ''
  },
  reviewRules: {
    productId: [
      { required: true, message: "请选择要评价的商品", trigger: "change" }
    ],
    rating: [
      { required: true, message: "请选择评分", trigger: "change" }
    ],
    content: [
      { required: true, message: "请输入评价内容", trigger: "blur" },
      { min: 10, message: "评价内容至少10个字", trigger: "blur" }
    ]
  }
});

const { queryParams, reviewForm, reviewRules } = toRefs(data);

/** 查询订单列表 */
function getList() {
  loading.value = true;
  listOrder(queryParams.value).then(response => {
    orderList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 查看详情 */
function handleDetail(row) {
  getOrder(row.orderId).then(response => {
    orderDetail.value = response.data;
    open.value = true;
  });
}

/** 支付订单 */
function handlePay(row) {
  proxy.$modal.confirm('确认支付该订单吗？').then(function() {
    return payOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("支付成功");
  }).catch(() => {});
}

/** 取消订单 */
function handleCancel(row) {
  proxy.$modal.confirm('确认取消该订单吗？').then(function() {
    return cancelOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("订单已取消");
  }).catch(() => {});
}

/** 发货操作（卖家） */
function handleSend(row) {
  proxy.$modal.confirm('确认发货该订单吗？').then(function() {
    return sendOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("发货成功");
  }).catch(() => {});
}

/** 确认收货操作（买家） */
function handleConfirm(row) {
  proxy.$modal.confirm('确认收货该订单吗？').then(function() {
    return confirmOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("确认收货成功");
  }).catch(() => {});
}

/** 评价订单 */
function handleReview(row) {
  // 如果订单已全部评价，提示用户
  if (row.allReviewed) {
    proxy.$modal.msgWarning("该订单的所有商品已评价完毕");
    return;
  }
  
  currentOrder.value = row;
  reviewForm.value.orderId = row.orderId;
  reviewForm.value.productId = null;
  reviewForm.value.rating = 5;
  reviewForm.value.content = '';
  
  // 获取订单商品及评价状态
  getOrderItemsWithReviewStatus(row.orderId).then(response => {
    currentOrderItems.value = response.data || [];
    
    // 检查是否还有未评价的商品
    const unreviewed = currentOrderItems.value.find(item => !item.reviewed);
    if (!unreviewed) {
      proxy.$modal.msgWarning("该订单的所有商品已评价完毕");
      // 刷新订单列表以更新评价状态
      getList();
      return;
    }
    
    reviewOpen.value = true;
    
    // 自动选择第一个未评价的商品
    reviewForm.value.productId = unreviewed.productId;
  }).catch(err => {
    proxy.$modal.msgError("获取订单商品失败");
  });
}

/** 商品选择变化 */
function handleProductChange(productId) {
  // 可以在这里添加额外逻辑
}

/** 提交评价 */
function submitReview() {
  reviewRef.value.validate(valid => {
    if (valid) {
      addReview(reviewForm.value).then(response => {
        proxy.$modal.msgSuccess("评价成功");
        reviewOpen.value = false;
        
        // 刷新订单列表以更新评价状态
        getList();
      }).catch(err => {
        if (err.response?.data?.msg) {
          proxy.$modal.msgError(err.response.data.msg);
        }
      });
    }
  });
}

// 初始化
onMounted(() => {
  // 检查是否有刷新参数
  if (route.query.refresh === 'true') {
    // 刷新订单列表
    getList();
    
    // 如果有支付成功标记，显示支付成功提示
    if (route.query.paid === 'true' && route.query.orderNo) {
      nextTick(() => {
        proxy.$modal.msgSuccess("支付成功！订单号：" + route.query.orderNo);
      });
    }
    
    // 清除查询参数，避免刷新后重复触发
    const newQuery = { ...route.query };
    delete newQuery.refresh;
    delete newQuery.paid;
    delete newQuery.orderNo;
    router.replace({ 
      path: route.path,
      query: newQuery
    });
  } else {
    getList();
  }
});

/** 获取订单状态类型 */
function getStatusType(status) {
  const statusMap = {
    0: 'warning',  // 待支付
    1: 'success',  // 已支付
    2: 'info',     // 已发货
    3: 'success',  // 已完成
    4: 'info',     // 已关闭
    5: 'danger'    // 已取消
  };
  return statusMap[status] || 'info';
}

/** 获取订单状态文字 */
function getStatusText(status) {
  const statusTextMap = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已关闭',
    5: '已取消'
  };
  return statusTextMap[status] || '未知状态';
}

/** 表格行类名 */
function tableRowClassName({ row, rowIndex }) {
  if (rowIndex % 2 === 1) {
    return 'table-row-even';
  }
  return '';
}
</script>

<style scoped lang="scss">
@import '@/styles/shop-theme.scss';

.order-management {
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

  .order-no-text {
    font-family: 'Courier New', monospace;
    font-weight: 600;
    color: #667eea;
    letter-spacing: 1px;
  }

  .amount-text {
    font-size: 1.1rem;
    font-weight: 700;
    color: #f56c6c;
    
    &.primary {
      color: #67c23a;
    }
  }

  .time-text {
    color: #666;
    font-size: 0.9rem;
  }

  .status-badge {
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 12px;
    
    &.animated {
      animation: pulse 2s infinite;
    }
  }

  .action-btn {
    transition: all 0.3s;
    margin: 0 3px;
    
    &:hover:not(.disabled) {
      transform: scale(1.1);
    }
    
    &.pay-btn {
      font-weight: 600;
      
      &:hover {
        color: #67c23a;
      }
    }
    
    &.review-btn.disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .order-dialog,
  .review-dialog {
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
      
      .el-dialog__title {
        font-size: 1.3rem;
        font-weight: 600;
        color: #333;
      }
    }
    
    :deep(.el-dialog__body) {
      padding: 30px;
    }
  }

  .dialog-content-glass {
    background: rgba(255, 255, 255, 0.5);
    border-radius: 15px;
    padding: 20px;
  }

  .order-descriptions {
    :deep(.el-descriptions__label) {
      font-weight: 600;
      color: #333;
      background: rgba(102, 126, 234, 0.05);
    }
    
    :deep(.el-descriptions__content) {
      color: #666;
    }
  }

  .address-text {
    line-height: 1.6;
    color: #666;
  }

  .remark-text {
    line-height: 1.6;
    color: #666;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .divider-text {
    font-size: 1.1rem;
    font-weight: 600;
    color: #667eea;
  }

  .order-items-table {
    :deep(.el-table) {
      background: rgba(255, 255, 255, 0.8);
      border-radius: 10px;
      
      .el-table__header-wrapper {
        th {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        }
      }
    }
  }

  .review-select {
    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.9);
      border-radius: 10px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .order-management {
    .page-title {
      font-size: 2rem;
    }
  }
}
</style>

