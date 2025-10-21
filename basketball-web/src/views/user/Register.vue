<template>
  <div class="register-container">
    <div class="register-box">
      <div class="logo-section">
        <div class="logo-icon">
          <el-icon :size="48"><Basketball /></el-icon>
        </div>
        <h1 class="title">创建账号</h1>
        <p class="subtitle">加入篮球馆预约平台</p>
      </div>

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
import { Basketball } from '@element-plus/icons-vue';
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
@use '@/styles/design-system/variables' as *;

.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: $bg-secondary;

  .register-box {
    width: 100%;
    max-width: 420px;
    padding: 48px 32px;
    background: $white;
    border-radius: $radius-2xl;
    box-shadow: $shadow-lg;
    border: 1px solid $border-color;

    @media (max-width: 640px) {
      padding: 32px 24px;
    }

    .logo-section {
      text-align: center;
      margin-bottom: 40px;

      .logo-icon {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 80px;
        height: 80px;
        margin-bottom: 16px;
        background: $primary-gradient;
        border-radius: $radius-full;
        box-shadow: $shadow-md;

        :deep(.el-icon) {
          color: $white;
        }
      }

      .title {
        font-size: 24px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 8px 0;
        letter-spacing: -0.02em;
      }

      .subtitle {
        font-size: 15px;
        color: $text-secondary;
        margin: 0;
      }
    }

    .register-form {
      :deep(.el-form-item) {
        margin-bottom: 20px;
      }

      :deep(.el-input__wrapper) {
        padding: 12px 16px;
        border-radius: $radius-lg;
        box-shadow: none;
        border: 1px solid $border-color;
        transition: all $duration-fast $ease-in-out;

        &:hover {
          border-color: $primary-light;
        }

        &.is-focus {
          border-color: $primary;
          box-shadow: 0 0 0 3px rgba($primary, 0.1);
        }
      }

      :deep(.el-input__inner) {
        font-size: 15px;
        color: $text-primary;

        &::placeholder {
          color: $text-tertiary;
        }
      }

      .register-btn {
        width: 100%;
        height: 44px;
        font-size: 15px;
        font-weight: 600;
        border-radius: $radius-lg;
        background: $primary;
        border: none;
        transition: all $duration-fast $ease-out;

        &:hover {
          background: $primary-dark;
          transform: translateY(-1px);
          box-shadow: $shadow-md;
        }

        &:active {
          transform: translateY(0);
        }
      }

      .register-footer {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 24px;
        font-size: 14px;
        color: $text-secondary;

        span {
          margin-right: 8px;
        }

        :deep(.el-link) {
          font-size: 14px;
          font-weight: 500;
        }
      }
    }
  }
}
</style>
