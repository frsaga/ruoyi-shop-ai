<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">网购平台用户注册</h3>
      <el-form-item prop="roleType">
        <el-radio-group v-model="registerForm.roleType" size="large" style="width: 100%;">
          <el-radio label="buyer" size="large" style="width: 48%;">
            <el-icon style="vertical-align: middle; margin-right: 5px;"><ShoppingCart /></el-icon>
            <span style="vertical-align: middle;">买家</span>
          </el-radio>
          <el-radio label="seller" size="large" style="width: 48%;">
            <el-icon style="vertical-align: middle; margin-right: 5px;"><Store /></el-icon>
            <span style="vertical-align: middle;">卖家</span>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item prop="username">
        <el-input 
          v-model="registerForm.username" 
          type="text" 
          size="large" 
          auto-complete="off" 
          placeholder="账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="nickName">
        <el-input 
          v-model="registerForm.nickName" 
          type="text" 
          size="large" 
          auto-complete="off" 
          :placeholder="registerForm.roleType === 'buyer' ? '昵称（用于订单显示）' : '昵称（用于店铺显示）'"
        >
          <template #prefix><el-icon class="el-input__icon"><User /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="phonenumber">
        <el-input 
          v-model="registerForm.phonenumber" 
          type="text" 
          size="large" 
          auto-complete="off" 
          placeholder="手机号"
          maxlength="11"
        >
          <template #prefix><el-icon class="el-input__icon"><Phone /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input 
          v-model="registerForm.email" 
          type="email" 
          size="large" 
          auto-complete="off" 
          placeholder="邮箱"
        >
          <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          size="large" 
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img"/>
        </div>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large" 
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right; margin-top: 10px;">
          <el-button type="primary" link @click="goToLogin">使用已有账户登录</el-button>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>Copyright © 2018-2023 ruoyi.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup name="Register">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from "element-plus";
import { register, getRegisterCodeImg } from "@/api/register";
import { ShoppingCart, Shop, User, Phone } from '@element-plus/icons-vue';

const router = useRouter();
const { proxy } = getCurrentInstance();

const registerForm = ref({
  roleType: "buyer",  // 默认选择买家
  username: "",
  nickName: "",
  phonenumber: "",
  email: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: ""
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 手机号验证
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入手机号"));
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error("请输入正确的手机号"));
  } else {
    callback();
  }
};

// 邮箱验证
const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入邮箱"));
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error("请输入正确的邮箱格式"));
  } else {
    callback();
  }
};

const registerRules = {
  roleType: [
    { required: true, message: "请选择注册角色", trigger: "change" }
  ],
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  nickName: [
    { required: true, trigger: "blur", message: "请输入昵称" },
    { min: 2, max: 30, message: "昵称长度必须介于 2 和 30 之间", trigger: "blur" }
  ],
  phonenumber: [
    { required: true, trigger: "blur", validator: validatePhone }
  ],
  email: [
    { required: true, trigger: "blur", validator: validateEmail }
  ],
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

/** 注册处理 - 独立注册逻辑 */
function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true;
      
      // 准备注册数据
      const registerData = {
        roleType: registerForm.value.roleType,
        username: registerForm.value.username,
        nickName: registerForm.value.nickName,
        phonenumber: registerForm.value.phonenumber,
        email: registerForm.value.email,
        password: "",
        code: registerForm.value.code,
        uuid: registerForm.value.uuid
      };
      
      // 调用注册API
      register(registerData).then(res => {
        loading.value = false;
        const username = registerForm.value.username;
        const roleText = registerForm.value.roleType === 'buyer' ? '买家' : '卖家';
        
        ElMessageBox.alert(
          "<font color='red'>恭喜你，您的" + roleText + "账号 " + username + " 注册成功！</font>", 
          "系统提示", 
          {
            dangerouslyUseHTMLString: true,
            type: "success",
          }
        ).then(() => {
          // 跳转到登录页面
          window.location.href = '/login';
        }).catch(() => {});
      }).catch(error => {
        loading.value = false;
        // 显示错误信息
        const errorMsg = error?.response?.data?.msg || error?.message || '注册失败，请稍后重试';
        ElMessage.error(errorMsg);
        
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode();
        }
      });
    }
  });
}

/** 获取验证码 - 独立逻辑 */
function getCode() {
  getRegisterCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      registerForm.value.uuid = res.uuid;
    }
  }).catch(error => {
    console.error('获取验证码失败:', error);
    ElMessage.error('获取验证码失败，请刷新页面重试');
  });
}

/** 跳转到登录页面 */
function goToLogin() {
  window.location.href = '/login';
}

// 页面加载时获取验证码
onMounted(() => {
  getCode();
});
</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 450px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
  :deep(.el-radio-group) {
    display: flex;
    justify-content: space-between;
    .el-radio {
      margin-right: 0;
      padding: 10px;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      transition: all 0.3s;
      &:hover {
        border-color: #409eff;
      }
      &.is-checked {
        border-color: #409eff;
        background-color: #f0f9ff;
      }
    }
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.register-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
