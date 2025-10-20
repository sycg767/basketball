<template>
  <div class="settings-container">
    <el-card class="page-header">
      <template #header>
        <div class="header-content">
          <h2>系统设置</h2>
          <span class="subtitle">管理系统配置和字典数据</span>
        </div>
      </template>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="menu-card">
          <div class="menu-item"
               v-for="item in menuItems"
               :key="item.key"
               :class="{ active: activeMenu === item.key }"
               @click="switchMenu(item.key)">
            <el-icon :size="20">
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.title }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="content-card">
          <!-- 系统配置 -->
          <div v-show="activeMenu === 'config'">
            <div class="section-header">
              <h3>系统配置</h3>
              <p>管理系统全局配置参数，如系统名称、联系方式等</p>
            </div>
            <SystemConfig />
          </div>

          <!-- 字典管理 -->
          <div v-show="activeMenu === 'dictionary'">
            <div class="section-header">
              <h3>字典管理</h3>
              <p>管理系统字典数据，用于下拉选择、状态标识等场景</p>
            </div>
            <DictionaryManage />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Setting, List } from '@element-plus/icons-vue'
import DictionaryManage from './DictionaryManage.vue'
import SystemConfig from './SystemConfig.vue'

const activeMenu = ref('dictionary')

const menuItems = [
  { key: 'dictionary', title: '字典管理', icon: List },
  { key: 'config', title: '系统配置', icon: Setting }
]

const switchMenu = (key: string) => {
  activeMenu.value = key
}

onMounted(() => {
  // 可以在这里加载初始化数据
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.subtitle {
  color: #666;
  font-size: 14px;
}

.menu-card {
  height: 100%;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  margin: 8px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.menu-item:hover {
  background-color: #f5f7fa;
  border-color: #dcdfe6;
}

.menu-item.active {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.menu-item .el-icon {
  flex-shrink: 0;
}

.content-card {
  min-height: 600px;
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.section-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

:deep(.el-card) {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>