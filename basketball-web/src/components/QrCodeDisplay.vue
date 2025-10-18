<template>
  <div class="qrcode-container">
    <div v-if="loading" class="qrcode-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>{{ loadingText }}</p>
    </div>

    <div v-else-if="qrCodeUrl" class="qrcode-content">
      <div class="qrcode-box">
        <img :src="qrCodeUrl" :alt="title" class="qrcode-image" />
      </div>

      <div class="qrcode-info">
        <h3 v-if="title" class="qrcode-title">{{ title }}</h3>
        <p v-if="description" class="qrcode-description">{{ description }}</p>

        <div v-if="showTimer && countdown > 0" class="qrcode-timer">
          <el-icon><Timer /></el-icon>
          <span>剩余时间: {{ formatTime(countdown) }}</span>
        </div>
      </div>

      <div v-if="$slots.footer" class="qrcode-footer">
        <slot name="footer"></slot>
      </div>
    </div>

    <div v-else class="qrcode-error">
      <el-icon color="#F56C6C"><CircleCloseFilled /></el-icon>
      <p>{{ errorText }}</p>
      <el-button v-if="showRetry" type="primary" @click="handleRetry">
        重新加载
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue';
import { Loading, Timer, CircleCloseFilled } from '@element-plus/icons-vue';

const props = defineProps({
  // 二维码URL
  qrCodeUrl: {
    type: String,
    default: ''
  },
  // 标题
  title: {
    type: String,
    default: '扫码支付'
  },
  // 描述文字
  description: {
    type: String,
    default: ''
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 加载文字
  loadingText: {
    type: String,
    default: '正在生成二维码...'
  },
  // 错误文字
  errorText: {
    type: String,
    default: '二维码加载失败'
  },
  // 是否显示倒计时
  showTimer: {
    type: Boolean,
    default: false
  },
  // 倒计时秒数
  timeout: {
    type: Number,
    default: 300 // 5分钟
  },
  // 是否显示重试按钮
  showRetry: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['timeout', 'retry']);

const countdown = ref(0);
let timer = null;

watch(() => props.qrCodeUrl, (newVal) => {
  if (newVal && props.showTimer) {
    startCountdown();
  } else {
    stopCountdown();
  }
});

watch(() => props.showTimer, (newVal) => {
  if (newVal && props.qrCodeUrl) {
    startCountdown();
  } else {
    stopCountdown();
  }
});

const startCountdown = () => {
  countdown.value = props.timeout;

  timer = setInterval(() => {
    countdown.value--;

    if (countdown.value <= 0) {
      stopCountdown();
      emit('timeout');
    }
  }, 1000);
};

const stopCountdown = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  countdown.value = 0;
};

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

const handleRetry = () => {
  emit('retry');
};

onBeforeUnmount(() => {
  stopCountdown();
});

defineExpose({
  startCountdown,
  stopCountdown
});
</script>

<style scoped>
.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  min-height: 400px;
}

.qrcode-loading,
.qrcode-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.qrcode-loading .el-icon {
  font-size: 48px;
  color: #409EFF;
}

.qrcode-loading p,
.qrcode-error p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.qrcode-error .el-icon {
  font-size: 48px;
}

.qrcode-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
}

.qrcode-box {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #fff;
  border: 1px solid #DCDFE6;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.qrcode-image {
  width: 200px;
  height: 200px;
  display: block;
}

.qrcode-info {
  text-align: center;
  width: 100%;
}

.qrcode-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.qrcode-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.qrcode-timer {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #F5F7FA;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

.qrcode-timer .el-icon {
  font-size: 16px;
}

.qrcode-footer {
  width: 100%;
  text-align: center;
  padding-top: 12px;
  border-top: 1px solid #EBEEF5;
}
</style>
