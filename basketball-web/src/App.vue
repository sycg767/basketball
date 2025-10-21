<template>
  <router-view />
</template>

<script setup>
</script>

<style lang="scss">
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

#app {
  width: 100%;
  min-height: 100vh;
  background-color: $bg-secondary;
  color: $text-primary;
  font-family: $font-family;

  // 平滑滚动
  scroll-behavior: smooth;

  // 优化文本渲染
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}
</style>