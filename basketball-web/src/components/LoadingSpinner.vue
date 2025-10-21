<template>
  <div class="loading-spinner-wrapper" :class="wrapperClass">
    <div class="loading-spinner" :class="spinnerClass" :style="spinnerStyle">
      <div class="spinner-circle"></div>
    </div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  },
  color: {
    type: String,
    default: 'primary'
  },
  text: {
    type: String,
    default: ''
  },
  fullscreen: {
    type: Boolean,
    default: false
  }
})

const spinnerClass = computed(() => ({
  [`loading-spinner--${props.size}`]: true,
  [`loading-spinner--${props.color}`]: true
}))

const wrapperClass = computed(() => ({
  'loading-spinner-wrapper--fullscreen': props.fullscreen
}))

const spinnerStyle = computed(() => {
  const sizes = {
    sm: '20px',
    md: '32px',
    lg: '48px',
    xl: '64px'
  }
  return {
    width: sizes[props.size],
    height: sizes[props.size]
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/index.scss';

.loading-spinner-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;

  &--fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    z-index: 9999;
  }
}

.loading-spinner {
  position: relative;
  display: inline-block;

  .spinner-circle {
    width: 100%;
    height: 100%;
    border: 3px solid rgba(0, 122, 255, 0.2);
    border-top-color: #007AFF;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    will-change: transform;
  }

  &--sm .spinner-circle {
    border-width: 2px;
  }

  &--lg .spinner-circle,
  &--xl .spinner-circle {
    border-width: 4px;
  }

  &--primary .spinner-circle {
    border-color: rgba(0, 122, 255, 0.2);
    border-top-color: #007AFF;
  }

  &--success .spinner-circle {
    border-color: rgba(52, 199, 89, 0.2);
    border-top-color: #34C759;
  }

  &--warning .spinner-circle {
    border-color: rgba(255, 149, 0, 0.2);
    border-top-color: #FF9500;
  }

  &--danger .spinner-circle {
    border-color: rgba(255, 59, 48, 0.2);
    border-top-color: #FF3B30;
  }
}

.loading-text {
  margin: 0;
  font-size: 14px;
  color: #86868b;
  font-weight: 500;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
