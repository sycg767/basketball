<template>
  <el-button
    :disabled="counting || disabled"
    :loading="loading"
    @click="handleSend"
    v-bind="$attrs"
  >
    {{ buttonText }}
  </el-button>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  // 倒计时秒数
  countdown: {
    type: Number,
    default: 60
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 按钮文本
  text: {
    type: String,
    default: '发送验证码'
  }
});

const emit = defineEmits(['send']);

const counting = ref(false);
const seconds = ref(0);
let timer = null;

const buttonText = computed(() => {
  if (counting.value) {
    return `${seconds.value}秒后重新发送`;
  }
  return props.text;
});

const handleSend = () => {
  emit('send');
};

const startCountdown = () => {
  counting.value = true;
  seconds.value = props.countdown;

  timer = setInterval(() => {
    seconds.value--;
    if (seconds.value <= 0) {
      stopCountdown();
    }
  }, 1000);
};

const stopCountdown = () => {
  counting.value = false;
  seconds.value = 0;
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
};

// 暴露方法供父组件调用
defineExpose({
  startCountdown,
  stopCountdown
});
</script>

<style scoped>
/* 按钮样式继承自Element Plus */
</style>
