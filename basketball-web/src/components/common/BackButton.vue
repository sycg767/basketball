<template>
  <div class="back-button-wrapper">
    <el-button
      class="back-button"
      :type="type"
      :size="size"
      @click="handleBack"
    >
      <el-icon><ArrowLeft /></el-icon>
      <span>{{ text }}</span>
    </el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';

const props = defineProps({
  text: {
    type: String,
    default: '返回'
  },
  type: {
    type: String,
    default: 'default'
  },
  size: {
    type: String,
    default: 'default'
  },
  to: {
    type: [String, Object],
    default: null
  }
});

const emit = defineEmits(['back']);
const router = useRouter();

const handleBack = () => {
  // 如果有自定义事件监听器，先触发事件
  if (emit('back')) {
    return;
  }

  // 如果指定了跳转路径，使用 push
  if (props.to) {
    router.push(props.to);
  } else {
    // 否则返回上一页
    router.back();
  }
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.back-button-wrapper {
  margin-bottom: $spacing-6;

  .back-button {
    @include button-base;
    display: inline-flex;
    align-items: center;
    gap: $spacing-2;
    padding: $spacing-2 $spacing-4;
    font-weight: $font-weight-medium;
    transition: all $duration-fast $ease-out;

    &:hover {
      transform: translateX(-2px);
    }

    .el-icon {
      font-size: $font-size-base;
    }

    span {
      font-size: $font-size-sm;
    }
  }
}
</style>
