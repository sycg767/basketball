<template>
  <el-dialog
    v-model="visible"
    title="微信扫码登录"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="wechat-login-content">
      <div v-if="loading" class="loading-box">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在加载二维码...</p>
      </div>

      <div v-else-if="qrCodeUrl" class="qrcode-box">
        <img :src="qrCodeUrl" alt="微信扫码登录" class="qrcode-image" />
        <p class="qrcode-tip">请使用微信扫描二维码登录</p>

        <div v-if="scanStatus === 'scanned'" class="scan-status">
          <el-icon color="#67C23A"><SuccessFilled /></el-icon>
          <p>扫码成功,请在手机上确认登录</p>
        </div>
      </div>

      <div v-else class="error-box">
        <el-icon color="#F56C6C"><CircleCloseFilled /></el-icon>
        <p>二维码加载失败</p>
        <el-button type="primary" @click="loadQrCode">重新加载</el-button>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
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
      // 开始轮询扫码状态
      startPolling();
    } else {
      ElMessage.error(res.message || '获取二维码失败');
      qrCodeUrl.value = '';
    }
  } catch (error) {
    console.error('获取微信授权URL失败:', error);
    ElMessage.error('获取二维码失败,请重试');
    qrCodeUrl.value = '';
  } finally {
    loading.value = false;
  }
};

const startPolling = () => {
  // 模拟轮询扫码状态 (实际项目中需要调用后端轮询接口)
  // 这里只是演示,真实场景需要后端支持

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

<style scoped>
.wechat-login-content {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-box,
.error-box {
  text-align: center;
}

.loading-box .el-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 16px;
}

.error-box .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-box p {
  margin-bottom: 16px;
  color: #606266;
}

.qrcode-box {
  text-align: center;
}

.qrcode-image {
  width: 200px;
  height: 200px;
  border: 1px solid #DCDFE6;
  border-radius: 8px;
  margin-bottom: 16px;
}

.qrcode-tip {
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}

.scan-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #67C23A;
  font-size: 14px;
}

.scan-status .el-icon {
  font-size: 20px;
}
</style>
