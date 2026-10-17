<template>
  <div class="shop-page-container review-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">评价管理</h1>
        <p class="page-subtitle">管理商品评价信息</p>
      </div>

      <!-- 搜索区域 -->
      <div class="search-card-glass">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
          <el-form-item label="商品名称" prop="productName">
            <el-input
              v-model="queryParams.productName"
              placeholder="请输入商品名称"
              clearable
              style="width: 240px"
              @keyup.enter="handleQuery"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="评价状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择评价状态" clearable style="width: 200px">
              <el-option label="待审核" value="0" />
              <el-option label="已发布" value="1" />
              <el-option label="已屏蔽" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="评分" prop="rating">
            <el-select v-model="queryParams.rating" placeholder="请选择评分" clearable style="width: 150px">
              <el-option label="1星" :value="1" />
              <el-option label="2星" :value="2" />
              <el-option label="3星" :value="3" />
              <el-option label="4星" :value="4" />
              <el-option label="5星" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery" class="glow-button-primary">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery" class="glass-button">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
          type="danger"
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['shop:review:remove']"
          class="glass-button danger"
        >批量删除</el-button>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table 
          v-loading="loading" 
          :data="reviewList" 
          @selection-change="handleSelectionChange"
          :row-class-name="tableRowClassName"
          stripe
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="商品名称" align="center" prop="productName" :show-overflow-tooltip="true" min-width="200" />
          <el-table-column label="评分" align="center" prop="rating" width="150">
            <template #default="scope">
              <div class="rating-display">
                <el-rate 
                  :model-value="scope.row.rating" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                  score-template="{value}分"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="评价内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="250" />
          <el-table-column label="用户" align="center" prop="userName" width="120">
            <template #default="scope">
              <span class="user-name-text">{{ scope.row.userName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="评价状态" align="center" prop="status" width="120">
            <template #default="scope">
              <el-tag 
                :type="scope.row.status === '0' ? 'warning' : scope.row.status === '1' ? 'success' : 'danger'" 
                effect="dark"
                class="status-badge"
              >
                <span v-if="scope.row.status === '0'">待审核</span>
                <span v-else-if="scope.row.status === '1'">已发布</span>
                <span v-else-if="scope.row.status === '2'">已屏蔽</span>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评价时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              <span class="time-text">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280" fixed="right">
            <template #default="scope">
              <el-button 
                link 
                type="primary" 
                icon="View" 
                @click="handleDetail(scope.row)" 
                v-hasPermi="['shop:review:query']"
                class="action-btn"
              >详情</el-button>
              
              <!-- 买家操作：只能修改自己的评价 -->
              <el-button 
                link 
                type="primary" 
                icon="Edit" 
                @click="handleUpdate(scope.row)" 
                v-hasPermi="['shop:review:edit']" 
                v-if="isBuyer"
                class="action-btn"
              >修改</el-button>
              
              <!-- 卖家操作：审核评价 -->
              <el-button 
                link 
                v-if="!isBuyer && scope.row.status === '0'" 
                type="success" 
                icon="Check" 
                @click="handleAudit(scope.row, '1')" 
                v-hasPermi="['shop:review:audit']"
                class="action-btn"
              >通过</el-button>
              <el-button 
                link 
                v-if="!isBuyer && scope.row.status === '0'" 
                type="warning" 
                icon="Close" 
                @click="handleAudit(scope.row, '2')" 
                v-hasPermi="['shop:review:audit']"
                class="action-btn"
              >屏蔽</el-button>
              
              <el-button 
                link 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)" 
                v-hasPermi="['shop:review:remove']"
                class="action-btn"
              >删除</el-button>
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

    <!-- 添加/修改评价对话框 -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="700px" 
      append-to-body
      class="review-dialog"
    >
      <div class="dialog-content-glass">
        <el-form ref="reviewRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="订单" prop="orderId">
            <el-select v-model="form.orderId" placeholder="请选择订单" @change="handleOrderChange" style="width: 100%">
              <el-option
                v-for="order in orderList"
                :key="order.orderId"
                :label="order.orderNo"
                :value="order.orderId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="商品" prop="productId">
            <el-select v-model="form.productId" placeholder="请选择商品" :disabled="!form.orderId" style="width: 100%">
              <el-option
                v-for="item in orderItems"
                :key="item.productId"
                :label="item.productName"
                :value="item.productId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="评分" prop="rating">
            <el-rate 
              v-model="form.rating" 
              show-text 
              :texts="['很差', '差', '一般', '好', '很好']" 
              size="large"
            />
          </el-form-item>
          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="5"
              placeholder="请输入评价内容"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel" class="glass-button">取 消</el-button>
          <el-button type="primary" @click="submitForm" class="glow-button-primary">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价详情对话框 -->
    <el-dialog 
      title="评价详情" 
      v-model="detailOpen" 
      width="700px" 
      append-to-body
      class="review-detail-dialog"
    >
      <div class="dialog-content-glass">
        <el-descriptions :column="2" border class="review-descriptions">
          <el-descriptions-item label="商品名称">
            <span class="product-name-text">{{ reviewDetail.productName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            <span class="user-name-text">{{ reviewDetail.userName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="评分" :span="2">
            <div class="rating-display">
              <el-rate 
                :model-value="reviewDetail.rating" 
                disabled 
                show-score 
                text-color="#ff9900"
                score-template="{value}分"
                size="large"
              />
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="评价内容" :span="2">
            <div class="review-content-text">{{ reviewDetail.content }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="评价状态">
            <el-tag 
              :type="reviewDetail.status === '0' ? 'warning' : reviewDetail.status === '1' ? 'success' : 'danger'" 
              effect="dark"
              class="status-badge"
            >
              <span v-if="reviewDetail.status === '0'">待审核</span>
              <span v-else-if="reviewDetail.status === '1'">已发布</span>
              <span v-else-if="reviewDetail.status === '2'">已屏蔽</span>
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评价时间">
            <span class="time-text">{{ parseTime(reviewDetail.createTime) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false" class="glass-button">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Review">
import { listReview, getReview, addReview, updateReview, delReview, auditReview } from "@/api/shop/review";
import { listOrder } from "@/api/shop/order";
import useUserStore from '@/store/modules/user';

const { proxy } = getCurrentInstance();
const userStore = useUserStore();

const reviewList = ref([]);
const orderList = ref([]);
const orderItems = ref([]);
const open = ref(false);
const detailOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const isBuyer = ref(true);

const statusOptions = ref([
  { label: "待审核", value: "0" },
  { label: "已发布", value: "1" },
  { label: "已屏蔽", value: "2" }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productName: null,
    status: null,
    rating: null,
  },
  rules: {
    productId: [
      { required: true, message: "商品不能为空", trigger: "change" }
    ],
    rating: [
      { required: true, message: "评分不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "评价内容不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);
const reviewDetail = ref({});

/** 检查用户类型 */
function checkUserType() {
  try {
    const roles = userStore.roles || [];
    const permissions = userStore.permissions || [];
    const hasSellerRole = roles.some(role => role.toLowerCase().includes('seller'));
    const hasStorePermission = permissions.includes('shop:store:list');
    isBuyer.value = !hasSellerRole && !hasStorePermission;
  } catch (e) {
    isBuyer.value = true;
  }
}

/** 查询评价列表 */
function getList() {
  loading.value = true;
  listReview(queryParams.value).then(response => {
    reviewList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 获取订单列表（用于新增评价时选择订单） */
function getOrderList() {
  listOrder({ status: 3 }).then(response => {
    orderList.value = response.rows || [];
  });
}

/** 订单选择变化 */
function handleOrderChange(orderId) {
  // 根据订单ID获取订单详情，获取订单商品列表
  const order = orderList.value.find(o => o.orderId === orderId);
  if (order && order.orderItems) {
    orderItems.value = order.orderItems;
  } else {
    orderItems.value = [];
  }
  form.value.productId = null;
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

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加评价";
  getOrderList();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const reviewId = row.reviewId;
  getReview(reviewId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改评价";
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  const reviewId = row.reviewId;
  getReview(reviewId).then(response => {
    reviewDetail.value = response.data;
    detailOpen.value = true;
  });
}

/** 审核评价 */
function handleAudit(row, status) {
  const statusText = status === '1' ? '通过' : '屏蔽';
  proxy.$modal.confirm('确认要' + statusText + '该评价吗？').then(() => {
    return auditReview(row.reviewId, status);
  }).then(() => {
    proxy.$modal.msgSuccess(statusText + '成功');
    getList();
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["reviewRef"].validate(valid => {
    if (valid) {
      if (form.value.reviewId != null) {
        updateReview(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addReview(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const reviewIds = row.reviewId || ids.value;
  proxy.$modal.confirm('是否确认删除评价编号为"' + reviewIds + '"的数据项？').then(function() {
    return delReview(reviewIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.reviewId);
  multiple.value = !selection.length;
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

// 取消按钮
function reset() {
  form.value = {
    reviewId: null,
    productId: null,
    orderId: null,
    rating: 5,
    content: null,
    images: null
  };
  orderItems.value = [];
  proxy.resetForm("reviewRef");
}

/** 表格行类名 */
function tableRowClassName({ row, rowIndex }) {
  if (rowIndex % 2 === 1) {
    return 'table-row-even';
  }
  return '';
}

onMounted(() => {
  checkUserType();
  getList();
});
</script>

<style scoped lang="scss">
@import '@/styles/shop-theme.scss';

.review-page {
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

  .rating-display {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .user-name-text {
    font-weight: 600;
    color: #333;
  }

  .product-name-text {
    font-weight: 600;
    color: #333;
  }

  .status-badge {
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 12px;
  }

  .time-text {
    color: #666;
    font-size: 0.9rem;
  }

  .action-btn {
    transition: all 0.3s;
    margin: 0 3px;
    
    &:hover {
      transform: scale(1.1);
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .review-dialog,
  .review-detail-dialog {
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

  .review-descriptions {
    :deep(.el-descriptions__label) {
      font-weight: 600;
      color: #333;
      background: rgba(102, 126, 234, 0.05);
    }
    
    :deep(.el-descriptions__content) {
      color: #666;
    }
  }

  .review-content-text {
    line-height: 1.8;
    color: #666;
    white-space: pre-wrap;
    word-break: break-word;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .review-page {
    .page-title {
      font-size: 2rem;
    }
    
    .action-buttons {
      flex-direction: column;
      
      .el-button {
        width: 100%;
        margin-bottom: 10px;
      }
    }
  }
}
</style>

