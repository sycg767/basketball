<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-section">
        <div class="logo-icon">
          <el-icon :size="48"><Basketball /></el-icon>
        </div>
        <h1 class="title">篮球馆场地预约</h1>
        <p class="subtitle">欢迎回来</p>
      </div>

      <!-- Tab切换 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 账号登录 -->
        <el-tab-pane label="账号登录" name="account">
          <el-form ref="accountFormRef" :model="accountForm" :rules="accountRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="accountForm.username"
                placeholder="用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="accountForm.password"
                type="password"
                placeholder="密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleAccountLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleAccountLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 手机登录 -->
        <el-tab-pane label="手机登录" name="phone">
          <el-form ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" class="login-form">
            <el-form-item prop="phone">
              <el-input
                v-model="phoneForm.phone"
                placeholder="手机号"
                size="large"
                prefix-icon="Iphone"
                maxlength="11"
              />
            </el-form-item>

            <el-form-item prop="code">
              <div class="code-input-wrapper">
                <el-input
                  v-model="phoneForm.code"
                  placeholder="验证码"
                  size="large"
                  prefix-icon="Message"
                  maxlength="6"
                  @keyup.enter="handlePhoneLogin"
                />
                <SmsCountdown
                  ref="smsCountdownRef"
                  :disabled="!phoneForm.phone || !validatePhone(phoneForm.phone)"
                  :loading="sendingCode"
                  text="获取验证码"
                  class="send-code-btn"
                  @send="handleSendCode"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handlePhoneLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <!-- 第三方登录 -->
      <div class="oauth-login">
        <div class="divider">
          <span>其他方式</span>
        </div>
        <div class="oauth-buttons">
          <button class="wechat-btn" @click="showWechatLogin">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-wechat"></use>
            </svg>
          </button>
        </div>
      </div>

      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="goRegister">立即注册</el-link>
      </div>
    </div>

    <!-- 微信登录对话框 -->
    <WechatLoginDialog
      v-model="wechatDialogVisible"
      @success="handleWechatLoginSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Basketball } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';
import { validateUsername, validatePassword, validatePhone } from '@/utils/validate';
import { sendSmsCode, loginByPhone } from '@/api/auth';
import SmsCountdown from '@/components/SmsCountdown.vue';
import WechatLoginDialog from '@/components/WechatLoginDialog.vue';

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref('account');
const loading = ref(false);
const sendingCode = ref(false);
const wechatDialogVisible = ref(false);

// 账号登录表单
const accountFormRef = ref(null);
const accountForm = reactive({
  username: '',
  password: ''
});

// 手机登录表单
const phoneFormRef = ref(null);
const phoneForm = reactive({
  phone: '',
  code: ''
});

// 验证码倒计时组件引用
const smsCountdownRef = ref(null);

// 账号登录验证规则
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

const accountRules = {
  username: [{ validator: validateUsernameRule, trigger: 'blur' }],
  password: [{ validator: validatePasswordRule, trigger: 'blur' }]
};

// 手机登录验证规则
const validatePhoneRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'));
  } else if (!validatePhone(value)) {
    callback(new Error('请输入正确的手机号'));
  } else {
    callback();
  }
};

const validateCodeRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'));
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('请输入6位数字验证码'));
  } else {
    callback();
  }
};

const phoneRules = {
  phone: [{ validator: validatePhoneRule, trigger: 'blur' }],
  code: [{ validator: validateCodeRule, trigger: 'blur' }]
};

// 账号登录
const handleAccountLogin = () => {
  accountFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await userStore.login(accountForm);
        ElMessage.success('登录成功');

        // 使用角色字段判断用户类型
        if (userStore.isAdmin) {
          router.push('/admin/dashboard');
        } else {
          router.push('/user-center');
        }
      } catch (error) {
        console.error('登录失败', error);
      } finally {
        loading.value = false;
      }
    }
  });
};

// 发送验证码
const handleSendCode = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号');
    return;
  }

  if (!validatePhone(phoneForm.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }

  sendingCode.value = true;
  try {
    const res = await sendSmsCode({
      phone: phoneForm.phone,
      type: 'login'
    });

    if (res.code === 200) {
      ElMessage.success('验证码已发送');
      smsCountdownRef.value?.startCountdown();
    } else {
      ElMessage.error(res.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    ElMessage.error('发送验证码失败');
  } finally {
    sendingCode.value = false;
  }
};

// 手机验证码登录
const handlePhoneLogin = () => {
  phoneFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await loginByPhone({
          phone: phoneForm.phone,
          code: phoneForm.code
        });

        if (res.code === 200) {
          const { token, user } = res.data;
          userStore.setToken(token);
          userStore.setUserInfo(user);

          ElMessage.success('登录成功');

          if (userStore.isAdmin) {
            router.push('/admin/dashboard');
          } else {
            router.push('/user-center');
          }
        } else {
          ElMessage.error(res.message || '登录失败');
        }
      } catch (error) {
        console.error('手机登录失败:', error);
        ElMessage.error('登录失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

// 显示微信登录
const showWechatLogin = () => {
  wechatDialogVisible.value = true;
};

// 微信登录成功
const handleWechatLoginSuccess = (data) => {
  const { token, user } = data;
  userStore.setToken(token);
  userStore.setUserInfo(user);

  ElMessage.success('微信登录成功');

  if (userStore.isAdmin) {
    router.push('/admin/dashboard');
  } else {
    router.push('/user-center');
  }
};

const goRegister = () => {
  router.push('/register');
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: $bg-secondary;

  .login-box {
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

    .login-tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 24px;
      }

      :deep(.el-tabs__nav-wrap::after) {
        height: 1px;
        background: $border-color;
      }

      :deep(.el-tabs__item) {
        font-size: 15px;
        font-weight: 500;
        color: $text-secondary;
        padding: 0 20px;
        height: 44px;
        line-height: 44px;

        &.is-active {
          color: $primary;
        }

        &:hover {
          color: $primary;
        }
      }

      :deep(.el-tabs__active-bar) {
        height: 2px;
        background: $primary;
      }

      .login-form {
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

        .code-input-wrapper {
          display: flex;
          gap: 12px;

          :deep(.el-input) {
            flex: 1;
          }

          .send-code-btn {
            flex-shrink: 0;
          }
        }

        .login-btn {
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
      }
    }

    .oauth-login {
      margin-top: 32px;

      .divider {
        position: relative;
        text-align: center;
        margin-bottom: 24px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          width: 100%;
          height: 1px;
          background: $border-color;
        }

        span {
          position: relative;
          padding: 0 16px;
          background: $white;
          color: $text-tertiary;
          font-size: 13px;
          font-weight: 500;
          z-index: 1;
        }
      }

      .oauth-buttons {
        display: flex;
        justify-content: center;
        gap: 16px;

        .wechat-btn {
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #07C160;
          border: none;
          border-radius: $radius-full;
          cursor: pointer;
          transition: all $duration-fast $ease-out;
          box-shadow: $shadow-sm;

          &:hover {
            background: #06AD56;
            transform: translateY(-2px);
            box-shadow: $shadow-md;
          }

          &:active {
            transform: translateY(0);
          }

          .icon {
            width: 24px;
            height: 24px;
            fill: $white;
          }
        }
      }
    }

    .login-footer {
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
</style>
