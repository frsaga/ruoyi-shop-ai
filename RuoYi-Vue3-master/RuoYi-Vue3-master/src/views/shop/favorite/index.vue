<template>
  <div class="shop-page-container favorite-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">我的收藏</h1>
        <p class="page-subtitle">管理您收藏的商品</p>
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
          v-hasPermi="['shop:favorite:remove']"
          class="glass-button danger"
        >批量删除</el-button>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table 
          v-loading="loading" 
          :data="favoriteList" 
          @selection-change="handleSelectionChange"
          :row-class-name="tableRowClassName"
          stripe
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="商品名称" align="center" prop="product.productName" :show-overflow-tooltip="true" min-width="200" />
          <el-table-column label="商品副标题" align="center" prop="product.productSubtitle" :show-overflow-tooltip="true" min-width="180" />
          <el-table-column label="价格" align="center" prop="product.price" width="120">
            <template #default="scope">
              <span class="price-text">¥{{ scope.row.product?.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" align="center" prop="product.stock" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.product?.stock > 50 ? 'success' : scope.row.product?.stock > 10 ? 'warning' : 'danger'" effect="dark">
                {{ scope.row.product?.stock || 0 }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="销量" align="center" prop="product.salesCount" width="100">
            <template #default="scope">
              <span class="sales-text">{{ scope.row.product?.salesCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="店铺" align="center" prop="product.storeName" width="150" />
          <el-table-column label="收藏时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              <span class="time-text">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200" fixed="right">
            <template #default="scope">
              <el-button 
                link 
                type="primary" 
                icon="View" 
                @click="handleViewProduct(scope.row.product.productId)"
                class="action-btn"
              >查看商品</el-button>
              <el-button 
                link 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)" 
                v-hasPermi="['shop:favorite:remove']"
                class="action-btn"
              >取消收藏</el-button>
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
  </div>
</template>

<script setup name="Favorite">
import { listFavorite, delFavorite } from "@/api/shop/favorite";

const { proxy } = getCurrentInstance();
const router = useRouter();

const favoriteList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productName: null,
  }
});

const { queryParams } = toRefs(data);

/** 查询收藏列表 */
function getList() {
  loading.value = true;
  listFavorite(queryParams.value).then(response => {
    favoriteList.value = response.rows;
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

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.favoriteId);
  multiple.value = !selection.length;
}

/** 查看商品 */
function handleViewProduct(productId) {
  router.push({ path: '/shop/product/detail/' + productId });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _favoriteIds = row.favoriteId || ids.value;
  proxy.$modal.confirm('是否确认取消收藏编号为"' + _favoriteIds + '"的数据项？').then(function() {
    // 如果是单个删除，传递数组；如果是批量删除，直接传递数组
    const favoriteIdArray = Array.isArray(_favoriteIds) ? _favoriteIds : [_favoriteIds];
    return delFavorite(favoriteIdArray);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("取消收藏成功");
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

.favorite-page {
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

  .time-text {
    color: #666;
    font-size: 0.9rem;
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
}

// 响应式设计
@media (max-width: 768px) {
  .favorite-page {
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

