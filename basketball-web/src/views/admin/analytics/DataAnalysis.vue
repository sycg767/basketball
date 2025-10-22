<template>
  <div class="data-analysis-container">
    <h2 class="page-title">数据分析</h2>

    <el-tabs v-model="activeTab" type="card" class="analysis-tabs">
      <el-tab-pane label="会员活跃度" name="member">
        <MemberAnalysis />
      </el-tab-pane>

      <el-tab-pane label="课程热度" name="course">
        <CourseAnalysis />
      </el-tab-pane>

      <el-tab-pane label="场地使用" name="venue">
        <VenueAnalysis />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import MemberAnalysis from './MemberAnalysis.vue';
import CourseAnalysis from './CourseAnalysis.vue';
import VenueAnalysis from './VenueAnalysis.vue';

const activeTab = ref('member');
</script>

<style lang="scss" scoped>
.data-analysis-container {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 24px 0;
  }

  .analysis-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }

    :deep(.el-tabs__item) {
      font-size: 15px;
      padding: 0 24px;
      height: 44px;
      line-height: 44px;
    }

    :deep(.el-tabs__content) {
      padding: 0;
    }
  }
}
</style>
