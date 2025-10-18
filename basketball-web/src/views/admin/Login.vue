<template>
  <div class="admin-login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon :size="48" color="#409eff"><Basketball /></el-icon>
        <h2>管理员登录</h2>
        <p>篮球场馆预约系统后台管理</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入管理员账号"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>

        <div class="login-tips">
          <el-link type="primary" @click="goHome">返回用户端</el-link>
        </div>
      </el-form>
    </div>

    <div class="login-footer">
      <p>© 2025 篮球场馆预约系统 版权所有</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Basketball } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = reactive({
  username: '',
  password: ''
});

const loginRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
};

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await userStore.login(loginForm);
        ElMessage.success('登录成功');
        router.push('/admin/dashboard');
      } catch (error) {
        ElMessage.error(error.message || '登录失败，请检查账号密码');
      } finally {
        loading.value = false;
      }
    }
  });
};

// 返回用户端
const goHome = () => {
  router.push('/');
};
</script>

<style lang="scss" scoped>
.admin-login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;

  .login-box {
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
    padding: 40px;
    width: 100%;
    max-width: 400px;

    .login-header {
      text-align: center;
      margin-bottom: 32px;

      .el-icon {
        margin-bottom: 16px;
      }

      h2 {
        font-size: 28px;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 8px 0;
      }

      p {
        font-size: 14px;
        color: #718096;
        margin: 0;
      }
    }

    .login-form {
      .login-button {
        width: 100%;
        font-size: 16px;
        font-weight: 600;
      }

      .login-tips {
        text-align: center;
        margin-top: 16px;
      }
    }
  }

  .login-footer {
    margin-top: 32px;
    text-align: center;

    p {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
      margin: 0;
    }
  }
}
</style>
