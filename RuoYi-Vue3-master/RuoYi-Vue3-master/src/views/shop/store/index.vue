<template>
  <div class="shop-page-container store-management">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">店铺管理</h1>
        <p class="page-subtitle">创建和管理您的店铺信息</p>
      </div>

      <!-- 搜索区域 -->
      <div class="search-card-glass">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
          <el-form-item label="店铺名称" prop="storeName">
            <el-input
              v-model="queryParams.storeName"
              placeholder="请输入店铺名称"
              clearable
              style="width: 240px"
              @keyup.enter="handleQuery"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
              <el-option label="待审核" value="0" />
              <el-option label="正常" value="1" />
              <el-option label="停用" value="2" />
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
          v-hasPermi="['shop:store:add']"
          class="glow-button-primary"
        >新增店铺</el-button>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table 
          v-loading="loading" 
          :data="storeList"
          :row-class-name="tableRowClassName"
          stripe
        >
          <el-table-column label="店铺名称" align="center" prop="storeName" :show-overflow-tooltip="true" min-width="200" />
          <el-table-column label="联系电话" align="center" prop="storePhone" width="140" />
          <el-table-column label="信用评分" align="center" prop="creditScore" width="120">
            <template #default="scope">
              <el-rate 
                :model-value="scope.row.creditScore || 5" 
                disabled 
                show-score 
                text-color="#ff9900"
                score-template="{value}"
              />
            </template>
          </el-table-column>
          <el-table-column label="销量" align="center" prop="salesVolume" width="120">
            <template #default="scope">
              <span class="sales-text">{{ scope.row.salesVolume || 0 }}</span>
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
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280" fixed="right">
            <template #default="scope">
              <el-button 
                link 
                type="primary" 
                icon="Edit" 
                @click="handleUpdate(scope.row)" 
                v-hasPermi="['shop:store:edit']"
                class="action-btn"
              >修改</el-button>
              <el-button 
                link 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)" 
                v-hasPermi="['shop:store:remove']"
                class="action-btn"
              >删除</el-button>
              <el-button 
                link 
                type="success" 
                v-if="scope.row.status === '0'" 
                @click="handleAudit(scope.row.storeId, '1')" 
                v-hasPermi="['shop:store:audit']"
                class="action-btn"
              >通过</el-button>
              <el-button 
                link 
                type="warning" 
                v-if="scope.row.status === '0'" 
                @click="handleAudit(scope.row.storeId, '2')" 
                v-hasPermi="['shop:store:audit']"
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

    <!-- 添加或修改店铺对话框 -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="700px" 
      append-to-body
      class="store-dialog"
    >
      <div class="dialog-content-glass">
        <el-form ref="storeRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="店铺名称" prop="storeName">
            <el-input v-model="form.storeName" placeholder="请输入店铺名称" />
          </el-form-item>
          <el-form-item label="店铺描述" prop="storeDescription">
            <el-input v-model="form.storeDescription" type="textarea" :rows="3" placeholder="请输入店铺描述" />
          </el-form-item>
          <el-form-item label="店铺地址" prop="storeAddress">
            <el-input v-model="form.storeAddress" placeholder="请输入店铺地址" />
          </el-form-item>
          <el-form-item label="联系电话" prop="storePhone">
            <el-input v-model="form.storePhone" placeholder="请输入联系电话" />
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

<script setup name="Store">
import { listStore, getStore, delStore, addStore, updateStore, auditStore } from "@/api/shop/store";

const { proxy } = getCurrentInstance();

const storeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref("");

const statusOptions = ref([
  { label: "待审核", value: "0" },
  { label: "正常", value: "1" },
  { label: "停用", value: "2" }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storeName: null,
    status: null,
  },
  rules: {
    storeName: [
      { required: true, message: "店铺名称不能为空", trigger: "blur" },
      { min: 2, max: 50, message: "店铺名称长度在 2 到 50 个字符", trigger: "blur" }
    ],
    storePhone: [
      { required: true, message: "联系电话不能为空", trigger: "blur" },
      { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: "联系电话格式不正确（手机号或固定电话）", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询店铺列表 */
function getList() {
  loading.value = true;
  listStore(queryParams.value).then(response => {
    storeList.value = response.rows;
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
    storeId: null,
    userId: null,
    storeName: null,
    storeDescription: null,
    storeAddress: null,
    storePhone: null,
    status: "0",
    creditScore: 5.0,
    salesVolume: 0,
    remark: null
  };
  proxy.resetForm("storeRef");
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
  title.value = "添加店铺";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _storeId = row.storeId;
  getStore(_storeId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改店铺";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["storeRef"].validate(valid => {
    if (valid) {
      if (form.value.storeId != null) {
        updateStore(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addStore(form.value).then(response => {
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
  const _storeId = row.storeId;
  proxy.$modal.confirm('是否确认删除店铺编号为"' + _storeId + '"的数据项？').then(function() {
    return delStore(_storeId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 审核操作 */
function handleAudit(storeId, status) {
  const text = status === "1" ? "通过" : "拒绝";
  proxy.$modal.confirm('是否确认' + text + '该店铺？').then(function() {
    return auditStore(storeId, status);
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

.store-management {
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

  .store-dialog {
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
  .store-management {
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

