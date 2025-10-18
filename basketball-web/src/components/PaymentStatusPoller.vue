<template>
  <div class="payment-status-poller">
    <slot :status="status" :polling="polling"></slot>
  </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue';

const props = defineProps({
  // 支付订单号
  paymentNo: {
    type: String,
    required: true
  },
  // 查询函数
  queryFn: {
    type: Function,
    required: true
  },
  // 轮询间隔(毫秒)
  interval: {
    type: Number,
    default: 2000
  },
  // 最大轮询次数
  maxAttempts: {
    type: Number,
    default: 30
  },
  // 是否自动开始轮询
  autoStart: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['success', 'failed', 'timeout', 'error']);

const polling = ref(false);
const status = ref('pending'); // pending | success | failed | timeout
const attempts = ref(0);
let timer = null;

watch(() => props.paymentNo, (newVal) => {
  if (newVal && props.autoStart) {
    start();
  }
});

const start = () => {
  if (polling.value) {
    return;
  }

  polling.value = true;
  attempts.value = 0;
  status.value = 'pending';

  poll();
};

const poll = async () => {
  if (!polling.value || attempts.value >= props.maxAttempts) {
    if (attempts.value >= props.maxAttempts) {
      handleTimeout();
    }
    return;
  }

  attempts.value++;

  try {
    const res = await props.queryFn(props.paymentNo);

    if (res.code === 200) {
      const paymentStatus = res.data.status;

      // 根据后端返回的状态判断
      // 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消, 5-已关闭
      if (paymentStatus === 2) {
        handleSuccess(res.data);
        return;
      } else if (paymentStatus === 3 || paymentStatus === 4 || paymentStatus === 5) {
        handleFailed(res.data);
        return;
      }
    }

    // 继续轮询
    scheduleNextPoll();
  } catch (error) {
    console.error('查询支付状态失败:', error);
    emit('error', error);

    // 出错后继续轮询
    scheduleNextPoll();
  }
};

const scheduleNextPoll = () => {
  if (!polling.value) {
    return;
  }

  timer = setTimeout(() => {
    poll();
  }, props.interval);
};

const handleSuccess = (data) => {
  status.value = 'success';
  polling.value = false;
  stop();
  emit('success', data);
};

const handleFailed = (data) => {
  status.value = 'failed';
  polling.value = false;
  stop();
  emit('failed', data);
};

const handleTimeout = () => {
  status.value = 'timeout';
  polling.value = false;
  stop();
  emit('timeout');
};

const stop = () => {
  polling.value = false;
  if (timer) {
    clearTimeout(timer);
    timer = null;
  }
};

const reset = () => {
  stop();
  status.value = 'pending';
  attempts.value = 0;
};

onBeforeUnmount(() => {
  stop();
});

// 如果设置了autoStart,在组件挂载时开始轮询
if (props.autoStart && props.paymentNo) {
  start();
}

defineExpose({
  start,
  stop,
  reset,
  polling,
  status,
  attempts
});
</script>

<style scoped>
.payment-status-poller {
  width: 100%;
}
</style>
