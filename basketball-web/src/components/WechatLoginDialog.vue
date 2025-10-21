<template>
  <el-dialog
    v-model="visible"
    title="微信扫码登录"
    width="420px"
    :close-on-click-modal="false"
    class="wechat-login-dialog"
    @close="handleClose"
  >
    <div class="wechat-login-content">
      <div v-if="loading" class="loading-box">
        <el-icon class="is-loading" :size="48"><Loading /></el-icon>
        <p>正在加载二维码...</p>
      </div>

      <div v-else-if="qrCodeUrl" class="qrcode-box">
        <div class="qrcode-wrapper">
          <img :src="qrCodeUrl" alt="微信扫码登录" class="qrcode-image" />
        </div>
        <p class="qrcode-tip">请使用微信扫描二维码登录</p>

        <div v-if="scanStatus === 'scanned'" class="scan-status">
          <el-icon color="#34C759" :size="20"><SuccessFilled /></el-icon>
          <p>扫码成功，请在手机上确认登录</p>
        </div>
      </div>

      <div v-else class="error-box">
        <el-icon color="#FF3B30" :size="48"><CircleCloseFilled /></el-icon>
        <p>二维码加载失败</p>
        <el-button type="primary" @click="loadQrCode" class="reload-btn">重新加载</el-button>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Loading, SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue';
import { getWechatAuthUrl } from '@/api/auth';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const visible = ref(false);
const loading = ref(false);
const qrCodeUrl = ref('');
const scanStatus = ref(''); // 'scanned' | 'confirmed' | ''
let pollTimer = null;

watch(() => props.modelValue, (val) => {
  visible.value = val;
  if (val) {
    loadQrCode();
  } else {
    stopPolling();
  }
});

watch(visible, (val) => {
  emit('update:modelValue', val);
});

const loadQrCode = async () => {
  loading.value = true;
  scanStatus.value = '';

  try {
    const res = await getWechatAuthUrl();
    if (res.code === 200) {
      qrCodeUrl.value = res.data.qrCodeUrl;
      startPolling();
    } else {
      ElMessage.error(res.message || '获取二维码失败');
      qrCodeUrl.value = '';
    }
  } catch (error) {
    console.error('获取微信授权URL失败:', error);
    ElMessage.error('获取二维码失败');
    qrCodeUrl.value = '';
  } finally {
    loading.value = false;
  }
};

const startPolling = () => {
  // 模拟轮询扫码状态 (实际项目中需要调用后端轮询接口)
  // pollTimer = setInterval(async () => {
  //   try {
  //     const res = await checkScanStatus();
  //     if (res.data.status === 'scanned') {
  //       scanStatus.value = 'scanned';
  //     } else if (res.data.status === 'confirmed') {
  //       scanStatus.value = 'confirmed';
  //       stopPolling();
  //       handleLoginSuccess(res.data);
  //     }
  //   } catch (error) {
  //     console.error('轮询扫码状态失败:', error);
  //   }
  // }, 2000);
};

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
};

const handleLoginSuccess = (data) => {
  ElMessage.success('登录成功');
  emit('success', data);
  visible.value = false;
};

const handleClose = () => {
  stopPolling();
  qrCodeUrl.value = '';
  scanStatus.value = '';
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.wechat-login-dialog {
  :deep(.el-dialog) {
    border-radius: $radius-2xl;
    box-shadow: $shadow-2xl;
  }

  :deep(.el-dialog__header) {
    padding: 24px 24px 16px;
    border-bottom: 1px solid $border-color;
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
  }

  :deep(.el-dialog__body) {
    padding: 32px 24px;
  }

  :deep(.el-dialog__footer) {
    padding: 16px 24px 24px;
    border-top: 1px solid $border-color;
  }

  .wechat-login-content {
    min-height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .loading-box,
  .error-box {
    text-align: center;

    .el-icon {
      margin-bottom: 16px;
    }

    p {
      margin: 0 0 16px 0;
      color: $text-secondary;
      font-size: 15px;
    }
  }

  .error-box {
    .reload-btn {
      height: 40px;
      padding: 0 24px;
      font-size: 14px;
      font-weight: 500;
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

  .qrcode-box {
    text-align: center;

    .qrcode-wrapper {
      display: inline-block;
      padding: 16px;
      background: $white;
      border-radius: $radius-xl;
      box-shadow: $shadow-md;
      border: 1px solid $border-color;
      margin-bottom: 20px;
    }

    .qrcode-image {
      width: 200px;
      height: 200px;
      display: block;
      border-radius: $radius-lg;
    }

    .qrcode-tip {
      color: $text-secondary;
      font-size: 14px;
      margin: 0 0 16px 0;
    }

    .scan-status {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      padding: 12px 20px;
      background: rgba($success, 0.1);
      border-radius: $radius-lg;
      color: $success;
      font-size: 14px;
      font-weight: 500;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;

    .cancel-btn {
      height: 40px;
      padding: 0 32px;
      font-size: 14px;
      font-weight: 500;
      border-radius: $radius-lg;
      background: $bg-secondary;
      border: 1px solid $border-color;
      color: $text-secondary;
      transition: all $duration-fast $ease-out;

      &:hover {
        background: $gray-200;
        border-color: $gray-300;
      }
    }
  }
}
</style>
