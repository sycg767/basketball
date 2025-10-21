<template>
  <div class="skeleton-loader" :class="loaderClass">
    <!-- 卡片骨架屏 -->
    <template v-if="type === 'card'">
      <div class="skeleton-card">
        <div class="skeleton-image"></div>
        <div class="skeleton-content">
          <div class="skeleton-title"></div>
          <div class="skeleton-text"></div>
          <div class="skeleton-text short"></div>
        </div>
      </div>
    </template>

    <!-- 列表骨架屏 -->
    <template v-else-if="type === 'list'">
      <div v-for="i in rows" :key="i" class="skeleton-list-item">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-list-content">
          <div class="skeleton-title"></div>
          <div class="skeleton-text"></div>
        </div>
      </div>
    </template>

    <!-- 表格骨架屏 -->
    <template v-else-if="type === 'table'">
      <div class="skeleton-table">
        <div class="skeleton-table-header">
          <div v-for="i in columns" :key="i" class="skeleton-table-cell"></div>
        </div>
        <div v-for="i in rows" :key="i" class="skeleton-table-row">
          <div v-for="j in columns" :key="j" class="skeleton-table-cell"></div>
        </div>
      </div>
    </template>

    <!-- 文本骨架屏 -->
    <template v-else-if="type === 'text'">
      <div v-for="i in rows" :key="i" class="skeleton-text" :class="{ short: i === rows }"></div>
    </template>

    <!-- 自定义骨架屏 -->
    <template v-else>
      <slot></slot>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['card', 'list', 'table', 'text', 'custom'].includes(value)
  },
  rows: {
    type: Number,
    default: 3
  },
  columns: {
    type: Number,
    default: 4
  },
  animated: {
    type: Boolean,
    default: true
  }
})

const loaderClass = computed(() => ({
  'skeleton-loader--animated': props.animated
}))
</script>

<style lang="scss" scoped>
@import '@/assets/styles/index.scss';

.skeleton-loader {
  width: 100%;

  &--animated {
    .skeleton-card,
    .skeleton-image,
    .skeleton-avatar,
    .skeleton-title,
    .skeleton-text,
    .skeleton-table-cell {
      @extend .skeleton;
    }
  }
}

// 卡片骨架屏
.skeleton-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.skeleton-image {
  width: 100%;
  height: 200px;
  background: #f0f0f0;
}

.skeleton-content {
  padding: 20px;
}

.skeleton-title {
  height: 24px;
  background: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  width: 70%;
}

.skeleton-text {
  height: 16px;
  background: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 8px;

  &.short {
    width: 50%;
  }

  &:last-child {
    margin-bottom: 0;
  }
}

// 列表骨架屏
.skeleton-list-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #ffffff;
  border-radius: 8px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f0f0f0;
  flex-shrink: 0;
}

.skeleton-list-content {
  flex: 1;
}

// 表格骨架屏
.skeleton-table {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}

.skeleton-table-header {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f5f5f7;
  border-bottom: 1px solid #e5e5e5;
}

.skeleton-table-row {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #e5e5e5;

  &:last-child {
    border-bottom: none;
  }
}

.skeleton-table-cell {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 4px;
}

// 骨架屏动画
.skeleton {
  background: linear-gradient(
    90deg,
    #f0f0f0 0%,
    #f8f8f8 50%,
    #f0f0f0 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
