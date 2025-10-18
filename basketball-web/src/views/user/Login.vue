<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">篮球馆管理系统</h1>

      <!-- Tab切换 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 账号登录 -->
        <el-tab-pane label="账号登录" name="account">
          <el-form ref="accountFormRef" :model="accountForm" :rules="accountRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="accountForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="accountForm.password"
                type="password"
                placeholder="请输入密码"
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
                placeholder="请输入手机号"
                size="large"
                prefix-icon="Iphone"
                maxlength="11"
              />
            </el-form-item>

            <el-form-item prop="code">
              <div class="code-input-wrapper">
                <el-input
                  v-model="phoneForm.code"
                  placeholder="请输入验证码"
                  size="large"
                  prefix-icon="Message"
                  maxlength="6"
                  @keyup.enter="handlePhoneLogin"
                />
                <SmsCountdown
                  ref="smsCountdownRef"
                  :disabled="!phoneForm.phone || !validatePhone(phoneForm.phone)"
                  :loading="sendingCode"
                  text="发送验证码"
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
          <span>其他登录方式</span>
        </div>
        <div class="oauth-buttons">
          <el-button
            circle
            size="large"
            class="wechat-btn"
            @click="showWechatLogin"
          >
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-wechat"></use>
            </svg>
          </el-button>
        </div>
      </div>

      <div class="login-footer">
        <span>还没有账号?</span>
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
        router.push('/');
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
      ElMessage.success('验证码已发送,请注意查收');
      // 启动倒计时
      smsCountdownRef.value?.startCountdown();
    } else {
      ElMessage.error(res.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    ElMessage.error('发送验证码失败,请稍后重试');
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
          // 保存token和用户信息
          const { token, user } = res.data;
          userStore.setToken(token);
          userStore.setUserInfo(user);

          ElMessage.success('登录成功');
          router.push('/');
        } else {
          ElMessage.error(res.message || '登录失败');
        }
      } catch (error) {
        console.error('手机登录失败:', error);
        ElMessage.error('登录失败,请检查验证码是否正确');
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
  router.push('/');
};

const goRegister = () => {
  router.push('/register');
};
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-box {
    width: 450px;
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

    .login-tabs {
      :deep(.el-tabs__nav-wrap::after) {
        height: 1px;
      }

      .login-form {
        margin-top: 20px;

        .code-input-wrapper {
          display: flex;
          gap: 10px;

          .el-input {
            flex: 1;
          }

          .send-code-btn {
            width: 120px;
            flex-shrink: 0;
          }
        }

        .login-btn {
          width: 100%;
        }
      }
    }

    .oauth-login {
      margin-top: 30px;

      .divider {
        position: relative;
        text-align: center;
        margin-bottom: 20px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          width: 100%;
          height: 1px;
          background: #DCDFE6;
        }

        span {
          position: relative;
          padding: 0 16px;
          background: #fff;
          color: #909399;
          font-size: 14px;
          z-index: 1;
        }
      }

      .oauth-buttons {
        display: flex;
        justify-content: center;
        gap: 20px;

        .wechat-btn {
          width: 48px;
          height: 48px;
          background: #07C160;
          border-color: #07C160;
          color: #fff;

          &:hover {
            background: #06AD56;
            border-color: #06AD56;
          }

          .icon {
            width: 24px;
            height: 24px;
            fill: currentColor;
          }
        }
      }
    }

    .login-footer {
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
</style>
