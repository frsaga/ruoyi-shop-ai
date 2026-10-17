<template>
  <div class="shop-page-container address-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">收货地址管理</h1>
        <p class="page-subtitle">管理您的收货地址信息</p>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button 
          type="primary" 
          icon="Plus" 
          @click="handleAdd"
          class="glow-button-primary"
        >新增地址</el-button>
      </div>

      <!-- 地址列表 -->
      <div v-if="addressList.length > 0" class="address-grid-container">
        <div
          v-for="address in addressList"
          :key="address.addressId"
          class="address-card-glass"
          :class="{ 'is-default': address.isDefault === '1' }"
        >
          <div class="address-card-inner">
            <div class="address-header">
              <div class="address-user">
                <strong class="receiver-name">{{ address.receiverName }}</strong>
                <span class="phone">{{ address.receiverPhone }}</span>
              </div>
              <el-tag v-if="address.isDefault === '1'" type="success" effect="dark" class="default-tag">
                默认地址
              </el-tag>
            </div>

            <div class="address-content">
              <div class="address-detail">
                <el-icon><Location /></el-icon>
                <span>{{ address.province }} {{ address.city }} {{ address.district }}</span>
              </div>
              <div class="address-detail">
                <span>{{ address.address }}</span>
              </div>
            </div>

            <div class="address-actions">
              <el-button 
                type="primary" 
                link 
                icon="Edit" 
                @click="handleEdit(address)"
                class="action-btn"
              >编辑</el-button>
              <el-button
                v-if="address.isDefault !== '1'"
                type="success"
                link
                icon="Check"
                @click="handleSetDefault(address)"
                class="action-btn"
              >设为默认</el-button>
              <el-button
                type="danger"
                link
                icon="Delete"
                @click="handleDelete(address)"
                :disabled="address.isDefault === '1'"
                class="action-btn"
              >删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-address-glass">
        <el-empty
          description="暂无收货地址，快去添加吧！"
          :image-size="150"
        >
          <el-button type="primary" @click="handleAdd" class="glow-button-primary">添加收货地址</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 新增/编辑地址对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="title"
      width="700px"
      :close-on-click-modal="false"
      class="address-dialog"
    >
      <div class="dialog-content-glass">
        <el-form
          ref="addressFormRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="收货人" prop="receiverName">
            <el-input
              v-model="form.receiverName"
              placeholder="请输入收货人姓名"
              maxlength="50"
            />
          </el-form-item>

          <el-form-item label="手机号" prop="receiverPhone">
            <el-input
              v-model="form.receiverPhone"
              placeholder="请输入手机号"
              maxlength="11"
            />
          </el-form-item>

          <el-form-item label="所在地区" required>
            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item prop="province">
                  <el-input
                    v-model="form.province"
                    placeholder="省份"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item prop="city">
                  <el-input
                    v-model="form.city"
                    placeholder="城市"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item prop="district">
                  <el-input
                    v-model="form.district"
                    placeholder="区县"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="form.address"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="设为默认">
            <el-switch
              v-model="form.isDefault"
              active-value="1"
              inactive-value="0"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="glass-button">取消</el-button>
          <el-button type="primary" @click="handleSubmit" class="glow-button-primary">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Address">
import { listAddress, getAddress, addAddress, updateAddress, delAddress, setDefaultAddress } from "@/api/shop/address";
import { Location } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();

const addressList = ref([]);
const dialogVisible = ref(false);
const title = ref('');

const data = reactive({
  form: {},
  rules: {
    receiverName: [
      { required: true, message: "收货人不能为空", trigger: "blur" },
      { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" }
    ],
    receiverPhone: [
      { required: true, message: "手机号不能为空", trigger: "blur" },
      { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
    ],
    province: [
      { required: true, message: "省份不能为空", trigger: "blur" }
    ],
    city: [
      { required: true, message: "城市不能为空", trigger: "blur" }
    ],
    district: [
      { required: true, message: "区县不能为空", trigger: "blur" }
    ],
    address: [
      { required: true, message: "详细地址不能为空", trigger: "blur" },
      { min: 5, max: 200, message: "长度在 5 到 200 个字符", trigger: "blur" }
    ]
  }
});

const { form, rules } = toRefs(data);

/** 查询地址列表 */
function getList() {
  listAddress().then(response => {
    addressList.value = response.data || [];
  });
}

/** 表单重置 */
function reset() {
  form.value = {
    addressId: null,
    receiverName: null,
    receiverPhone: null,
    province: null,
    city: null,
    district: null,
    address: null,
    isDefault: "0"
  };
  proxy.resetForm("addressFormRef");
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  dialogVisible.value = true;
  title.value = "新增收货地址";
}

/** 编辑按钮操作 */
function handleEdit(row) {
  reset();
  const addressId = row.addressId;
  
  getAddress(addressId).then(response => {
    form.value = response.data;
    dialogVisible.value = true;
    title.value = "修改收货地址";
  });
}

/** 设为默认地址 */
function handleSetDefault(row) {
  proxy.$modal.confirm('确认设置为默认地址吗？').then(() => {
    return setDefaultAddress(row.addressId);
  }).then(() => {
    proxy.$modal.msgSuccess("设置成功");
    getList();
  }).catch(() => {});
}

/** 删除按钮操作 */
function handleDelete(row) {
  if (row.isDefault === "1") {
    proxy.$modal.msgWarning("默认地址不能删除");
    return;
  }
  
  proxy.$modal.confirm('确认删除该收货地址吗？').then(() => {
    return delAddress(row.addressId);
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功");
    getList();
  }).catch(() => {});
}

/** 提交按钮 */
function handleSubmit() {
  proxy.$refs.addressFormRef.validate(valid => {
    if (valid) {
      if (form.value.addressId != null) {
        updateAddress(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          dialogVisible.value = false;
          getList();
        });
      } else {
        addAddress(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          dialogVisible.value = false;
          getList();
        });
      }
    }
  });
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.address-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .address-grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 25px;
    margin-top: 20px;
  }

  .address-card-glass {
    @extend .glass-card;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    animation: fadeInUp 0.6s ease-out;
    animation-fill-mode: both;
    
    @for $i from 1 through 10 {
      &:nth-child(#{$i}) {
        animation-delay: #{$i * 0.1}s;
      }
    }
    
    &.is-default {
      border: 2px solid #67c23a;
      box-shadow: 0 8px 32px rgba(103, 194, 58, 0.3);
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #67c23a 0%, #85ce61 100%);
      }
    }
    
    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
    }
  }

  .address-card-inner {
    padding: 25px;
  }

  .address-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  }

  .address-user {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .receiver-name {
      font-size: 1.2rem;
      font-weight: 700;
      color: #333;
    }
    
    .phone {
      color: #666;
      font-size: 0.95rem;
      padding: 4px 12px;
      background: rgba(102, 126, 234, 0.1);
      border-radius: 12px;
    }
  }

  .default-tag {
    font-weight: 600;
    padding: 4px 12px;
    animation: pulse 2s infinite;
  }

  .address-content {
    margin-bottom: 20px;
    min-height: 80px;
  }

  .address-detail {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    color: #666;
    font-size: 0.95rem;
    line-height: 1.8;
    margin: 10px 0;
    
    .el-icon {
      color: #667eea;
      font-size: 18px;
      margin-top: 3px;
      flex-shrink: 0;
    }
    
    span {
      flex: 1;
    }
  }

  .address-actions {
    display: flex;
    justify-content: flex-start;
    gap: 10px;
    padding-top: 15px;
    border-top: 1px solid rgba(102, 126, 234, 0.1);
  }

  .action-btn {
    transition: all 0.3s;
    
    &:hover:not(:disabled) {
      transform: scale(1.1);
    }
  }

  .empty-address-glass {
    @extend .glass-card;
    padding: 60px 20px;
    text-align: center;
    margin-top: 30px;
    
    :deep(.el-empty__description) {
      color: #666;
      font-size: 1rem;
    }
  }

  .address-dialog {
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
}

// 响应式设计
@media (max-width: 768px) {
  .address-page {
    .page-title {
      font-size: 2rem;
    }
    
    .address-grid-container {
      grid-template-columns: 1fr;
      gap: 15px;
    }
  }
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}
</style>

