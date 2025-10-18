<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">用户注册</h1>
      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名（3-20位字母数字下划线）"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（6-20位）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="手机号"
            size="large"
            prefix-icon="Phone"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="真实姓名（选填）"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goLogin">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { register } from '@/api/user';
import { validateUsername, validatePassword, validatePhone } from '@/utils/validate';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  realName: ''
});

// 自定义验证规则
const validateUsernameRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'));
  } else if (!validateUsername(value)) {
    callback(new Error('用户名为3-20位字母、数字或下划线'));
  } else {
    callback();
  }
};

const validatePasswordRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'));
  } else if (!validatePassword(value)) {
    callback(new Error('密码长度为6-20位'));
  } else {
    callback();
  }
};

const validateConfirmPasswordRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const validatePhoneRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'));
  } else if (!validatePhone(value)) {
    callback(new Error('请输入正确的手机号'));
  } else {
    callback();
  }
};

const rules = {
  username: [{ validator: validateUsernameRule, trigger: 'blur' }],
  password: [{ validator: validatePasswordRule, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPasswordRule, trigger: 'blur' }],
  phone: [{ validator: validatePhoneRule, trigger: 'blur' }]
};

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const { confirmPassword, ...registerData } = form;
        await register(registerData);
        ElMessage.success('注册成功，请登录');
        router.push('/login');
      } catch (error) {
        console.error('注册失败', error);
      } finally {
        loading.value = false;
      }
    }
  });
};

const goLogin = () => {
  router.push('/login');
};
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 100vh;
  padding: 20px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .register-box {
    width: 400px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    .title {
      margin-bottom: 30px;
      font-size: 24px;
      font-weight: bold;
      text-align: center;
      color: #303133;
    }

    .register-form {
      .register-btn {
        width: 100%;
      }

      .register-footer {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;
        font-size: 14px;
        color: #909399;

        span {
          margin-right: 5px;
        }
      }
    }
  }
}
</style>