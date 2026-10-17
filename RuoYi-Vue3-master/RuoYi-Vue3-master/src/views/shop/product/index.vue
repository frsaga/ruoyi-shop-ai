<template>
  <div class="shop-page-container product-management">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">商品管理</h1>
        <p class="page-subtitle">管理您的商品库存和销售信息</p>
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
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
              <el-option label="待审核" value="0" />
              <el-option label="在售" value="1" />
              <el-option label="下架" value="2" />
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
          type="primary"
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['shop:product:add']"
          class="glow-button-primary"
        >新增商品</el-button>
        <el-button
          type="danger"
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['shop:product:remove']"
          class="glass-button danger"
        >批量删除</el-button>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table 
          v-loading="loading" 
          :data="productList" 
          @selection-change="handleSelectionChange"
          :row-class-name="tableRowClassName"
          stripe
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="商品名称" align="center" prop="productName" :show-overflow-tooltip="true" min-width="200" />
          <el-table-column label="价格" align="center" prop="price" width="120">
            <template #default="scope">
              <span class="price-text">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" align="center" prop="stock" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.stock > 50 ? 'success' : scope.row.stock > 10 ? 'warning' : 'danger'" effect="dark">
                {{ scope.row.stock }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="销量" align="center" prop="salesCount" width="100">
            <template #default="scope">
              <span class="sales-text">{{ scope.row.salesCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status" width="120">
            <template #default="scope">
              <el-tag 
                :type="scope.row.status === '1' ? 'success' : scope.row.status === '0' ? 'warning' : 'info'" 
                effect="dark"
                class="status-badge"
              >
                <dict-tag :options="statusOptions" :value="scope.row.status"/>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="店铺" align="center" prop="storeName" width="150" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280" fixed="right">
            <template #default="scope">
              <el-button 
                link 
                type="primary" 
                icon="Edit" 
                @click="handleUpdate(scope.row)" 
                v-hasPermi="['shop:product:edit']"
                class="action-btn"
              >修改</el-button>
              <el-button 
                link 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)" 
                v-hasPermi="['shop:product:remove']"
                class="action-btn"
              >删除</el-button>
              <el-button 
                link 
                type="success" 
                v-if="scope.row.status === '0'" 
                @click="handleAudit(scope.row.productId, '1')" 
                v-hasPermi="['shop:product:audit']"
                class="action-btn"
              >通过</el-button>
              <el-button 
                link 
                type="warning" 
                v-if="scope.row.status === '0'" 
                @click="handleAudit(scope.row.productId, '2')" 
                v-hasPermi="['shop:product:audit']"
                class="action-btn"
              >拒绝</el-button>
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

    <!-- 添加或修改商品对话框 -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="900px" 
      append-to-body
      class="product-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-content-glass">
        <el-form ref="productRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="所属店铺" prop="storeId">
            <el-select v-model="form.storeId" placeholder="请选择店铺" filterable style="width: 100%">
              <el-option
                v-for="store in storeOptions"
                :key="store.storeId"
                :label="store.storeName"
                :value="store.storeId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="商品名称" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="商品副标题" prop="productSubtitle">
            <el-input v-model="form.productSubtitle" placeholder="请输入商品副标题" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="库存" prop="stock">
                <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="商品详情">
            <editor v-model="form.detail" :min-height="192"/>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注内容" />
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
  </div>
</template>

<script setup name="Product">
import { listProduct, getProduct, delProduct, addProduct, updateProduct, auditProduct } from "@/api/shop/product";
import { listStore } from "@/api/shop/store";

const { proxy } = getCurrentInstance();

const productList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const storeOptions = ref([]);

const statusOptions = ref([
  { label: "待审核", value: "0" },
  { label: "在售", value: "1" },
  { label: "下架", value: "2" }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productName: null,
    status: null,
  },
  rules: {
    storeId: [
      { required: true, message: "请选择所属店铺", trigger: "change" }
    ],
    productName: [
      { required: true, message: "商品名称不能为空", trigger: "blur" },
      { min: 2, max: 100, message: "商品名称长度在 2 到 100 个字符", trigger: "blur" }
    ],
    price: [
      { required: true, message: "价格不能为空", trigger: "blur" },
      { type: "number", min: 0.01, message: "价格必须大于0", trigger: "blur" }
    ],
    stock: [
      { required: true, message: "库存不能为空", trigger: "blur" },
      { type: "number", min: 0, message: "库存不能小于0", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询商品列表 */
function getList() {
  loading.value = true;
  listProduct(queryParams.value).then(response => {
    productList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    productId: null,
    storeId: null,
    productName: null,
    productSubtitle: null,
    detail: null,
    price: null,
    stock: null,
    salesCount: null,
    status: "0",
    isRecommended: "0",
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  };
  // 如果表单ref存在，则重置表单验证状态
  if (proxy.$refs["productRef"]) {
    proxy.resetForm("productRef");
  }
}

/** 获取店铺列表用于下拉选择 */
function getStoreOptions() {
  return listStore({ pageNum: 1, pageSize: 1000 }).then(response => {
    storeOptions.value = response.rows || [];
    return response;
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

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.productId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  // 先打开对话框，然后异步加载数据
  open.value = true;
  title.value = "添加商品";
  // 异步加载店铺数据
  getStoreOptions().catch(err => {
    console.error("获取店铺列表失败:", err);
    proxy.$modal.msgError("获取店铺列表失败，请稍后重试");
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _productId = row.productId || ids.value;
  title.value = "修改商品";
  
  // 先加载店铺数据，然后加载商品详情，最后打开对话框
  Promise.all([
  getStoreOptions().catch(err => {
    console.error("获取店铺列表失败:", err);
    proxy.$modal.msgError("获取店铺列表失败，请稍后重试");
      return null;
    }),
    getProduct(_productId).catch(err => {
      console.error("获取商品详情失败:", err);
      proxy.$modal.msgError("获取商品详情失败，请稍后重试");
      throw err;
    })
  ]).then(([_, productResponse]) => {
    if (productResponse && productResponse.data) {
      form.value = productResponse.data;
      open.value = true;
    }
  }).catch(err => {
    // 如果加载失败，不打开对话框
    console.error("加载商品数据失败:", err);
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["productRef"].validate(valid => {
    if (valid) {
      if (form.value.productId != null) {
        updateProduct(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(err => {
          console.error("修改商品失败:", err);
          proxy.$modal.msgError("修改商品失败，请稍后重试");
        });
      } else {
        addProduct(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(err => {
          console.error("新增商品失败:", err);
          proxy.$modal.msgError("新增商品失败，请稍后重试");
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _productIds = row.productId || ids.value;
  proxy.$modal.confirm('是否确认删除商品编号为"' + _productIds + '"的数据项？').then(function() {
    return delProduct(_productIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 审核操作 */
function handleAudit(productId, status) {
  const text = status === "1" ? "通过" : "拒绝";
  proxy.$modal.confirm('是否确认' + text + '该商品？').then(function() {
    return auditProduct(productId, status);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess(text + "成功");
  }).catch(() => {});
}

/** 表格行类名 */
function tableRowClassName({ row, rowIndex }) {
  if (rowIndex % 2 === 1) {
    return 'table-row-even';
  }
  return '';
}

getList();
</script>

<style scoped lang="scss">
@import '@/styles/shop-theme.scss';

.product-management {
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
    
    &.danger {
      color: $danger-color;
      border-color: rgba(245, 108, 108, 0.3);
      
      &:hover {
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

  .price-text {
    font-size: 1.1rem;
    font-weight: 700;
    color: #f56c6c;
  }

  .sales-text {
    font-size: 0.95rem;
    color: #67c23a;
    font-weight: 600;
  }

  .status-badge {
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 12px;
  }

  .action-btn {
    transition: all 0.3s;
    margin: 0 5px;
    
    &:hover {
      transform: scale(1.1);
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .product-dialog {
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
}

// 响应式设计
@media (max-width: 768px) {
  .product-management {
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

