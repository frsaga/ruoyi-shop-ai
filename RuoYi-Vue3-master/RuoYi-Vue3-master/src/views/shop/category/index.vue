<template>
  <div class="shop-page-container category-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">商品分类管理</h1>
        <p class="page-subtitle">管理商品分类信息</p>
      </div>

      <!-- 搜索区域 -->
      <div class="search-card-glass">
        <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
          <el-form-item label="分类名称" prop="categoryName">
            <el-input
              v-model="queryParams.categoryName"
              placeholder="请输入分类名称"
              clearable
              style="width: 240px"
              @keyup.enter="handleQuery"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
              <el-option label="正常" value="0" />
              <el-option label="停用" value="1" />
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
          v-hasPermi="['shop:category:add']"
          class="glow-button-primary"
        >新增分类</el-button>
        <el-button
          type="info"
          icon="Sort"
          @click="toggleExpandAll"
          class="glass-button"
        >展开/折叠</el-button>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </div>

      <!-- 数据表格 -->
      <div class="table-container-glass">
        <el-table
          v-if="refreshTable"
          v-loading="loading"
          :data="categoryList"
          row-key="categoryId"
          :default-expand-all="isExpandAll"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          :row-class-name="tableRowClassName"
        >
          <el-table-column prop="categoryName" label="分类名称" :show-overflow-tooltip="true" min-width="200">
            <template #default="scope">
              <span class="category-name-text">{{ scope.row.categoryName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="categoryIcon" label="分类图标" align="center" width="120">
            <template #default="scope">
              <svg-icon v-if="scope.row.categoryIcon" :icon-class="scope.row.categoryIcon" style="font-size: 24px; color: #667eea;" />
              <span v-else class="no-icon">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="categoryLevel" label="层级" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.categoryLevel === 1 ? 'success' : scope.row.categoryLevel === 2 ? 'warning' : 'info'" effect="dark">
                {{ scope.row.categoryLevel }}级
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="100" align="center">
            <template #default="scope">
              <span class="sort-order-text">{{ scope.row.sortOrder }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template #default="scope">
              <el-tag 
                :type="scope.row.status === '0' ? 'success' : 'danger'" 
                effect="dark"
                class="status-badge"
              >
                <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              <span class="time-text">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width" fixed="right">
            <template #default="scope">
              <el-button
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['shop:category:edit']"
                class="action-btn"
              >修改</el-button>
              <el-button
                link
                type="success"
                icon="Plus"
                @click="handleAdd(scope.row)"
                v-hasPermi="['shop:category:add']"
                class="action-btn"
              >新增</el-button>
              <el-button
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['shop:category:remove']"
                class="action-btn"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 添加或修改商品分类对话框 -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="700px" 
      append-to-body
      class="category-dialog"
    >
      <div class="dialog-content-glass">
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryOptions"
            :props="{ value: 'categoryId', label: 'categoryName', children: 'children' }"
            value-key="categoryId"
            placeholder="选择上级分类"
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类层级" prop="categoryLevel">
          <el-input-number v-model="form.categoryLevel" :min="1" :max="3" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="分类图标" prop="categoryIcon">
          <el-popover
            placement="bottom-start"
            :width="540"
            v-model:visible="showChooseIcon"
            trigger="click"
            @show="iconSelectSearch = ''"
          >
            <template #reference>
              <el-input v-model="form.categoryIcon" placeholder="点击选择图标" readonly>
                <template #prefix>
                  <svg-icon
                    v-if="form.categoryIcon"
                    :icon-class="form.categoryIcon"
                    style="height: 32px;width: 16px;"
                  />
                  <el-icon v-else style="height: 32px;width: 16px;">
                    <search />
                  </el-icon>
                </template>
              </el-input>
            </template>
            <icon-select ref="iconSelectRef" @selected="selected" :active-icon="form.categoryIcon" />
          </el-popover>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注"></el-input>
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

<script setup name="Category">
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from "@/api/shop/category";
import IconSelect from "@/components/IconSelect";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const categoryList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const title = ref("");
const categoryOptions = ref([]);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const showChooseIcon = ref(false);
const iconSelectSearch = ref("");

const data = reactive({
  form: {},
  queryParams: {
    categoryName: null,
    status: null
  },
  rules: {
    categoryName: [
      { required: true, message: "分类名称不能为空", trigger: "blur" },
      { min: 2, max: 50, message: "分类名称长度在 2 到 50 个字符", trigger: "blur" }
    ],
    categoryLevel: [
      { required: true, message: "分类层级不能为空", trigger: "change" },
      { type: "number", min: 1, max: 3, message: "分类层级必须在 1 到 3 之间", trigger: "change" }
    ],
    sortOrder: [
      { required: true, message: "排序不能为空", trigger: "blur" },
      { type: "number", min: 0, message: "排序必须大于等于 0", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询商品分类列表 */
function getList() {
  loading.value = true;
  listCategory(queryParams.value).then(response => {
    categoryList.value = handleTree(response.rows, "categoryId", "parentId");
    loading.value = false;
  });
}

/** 转换分类数据结构 */
function handleTree(data, id, parentId, children) {
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}

/** 查询分类下拉树结构 */
function getTreeselect() {
  listCategory().then(response => {
    categoryOptions.value = [];
    const category = { categoryId: 0, categoryName: '主类目', children: [] };
    category.children = handleTree(response.rows, "categoryId", "parentId");
    categoryOptions.value.push(category);
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
    categoryId: null,
    parentId: 0,
    categoryName: null,
    categoryLevel: 1,
    sortOrder: 0,
    categoryIcon: null,
    status: "0",
    remark: null
  };
  proxy.resetForm("categoryRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  getTreeselect();
  if (row != null && row.categoryId) {
    form.value.parentId = row.categoryId;
    form.value.categoryLevel = row.categoryLevel + 1;
  } else {
    form.value.parentId = 0;
    form.value.categoryLevel = 1;
  }
  open.value = true;
  title.value = "添加商品分类";
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getTreeselect();
  getCategory(row.categoryId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改商品分类";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["categoryRef"].validate(valid => {
    if (valid) {
      if (form.value.categoryId != null) {
        updateCategory(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addCategory(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除商品分类编号为"' + row.categoryId + '"的数据项？').then(function () {
    return delCategory(row.categoryId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 选择图标 */
function selected(name) {
  form.value.categoryIcon = name;
  showChooseIcon.value = false;
}

/** 表格行类名 */
function tableRowClassName({ row, rowIndex }) {
  return 'category-table-row';
}

getList();
</script>

<style scoped lang="scss">
@import '@/styles/shop-theme.scss';

.category-page {
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
        .category-table-row {
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

  .category-name-text {
    font-weight: 600;
    color: #333;
  }

  .no-icon {
    color: #999;
  }

  .sort-order-text {
    font-weight: 600;
    color: #667eea;
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
    margin: 0 5px;
    
    &:hover {
      transform: scale(1.1);
    }
  }

  .category-dialog {
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
  .category-page {
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

