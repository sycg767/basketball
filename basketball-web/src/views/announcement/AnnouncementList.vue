<template>
  <div class="announcement-list-container">
    <BackButton text="返回" />

    <el-card class="announcement-card">
      <!-- 头部 -->
      <template #header>
        <div class="card-header">
          <h3>系统公告</h3>
        </div>
      </template>

      <!-- 筛选器 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="1">系统通知</el-radio-button>
          <el-radio-button value="2">维护通知</el-radio-button>
          <el-radio-button value="3">节日通知</el-radio-button>
          <el-radio-button value="4">活动通知</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 公告列表 -->
      <div v-loading="loading" class="announcement-content">
        <template v-if="announcements.length === 0 && !loading">
          <el-empty description="暂无公告" />
        </template>

        <template v-else>
          <div
            v-for="item in announcements"
            :key="item.id"
            class="announcement-card-item"
            @click="handleAnnouncementClick(item)"
          >
            <div class="announcement-left">
              <div class="announcement-icon" :style="{ backgroundColor: getTypeBgColor(item.type) }">
                <el-icon :size="24" :color="getTypeColor(item.type)">
                  <Bell />
                </el-icon>
              </div>
            </div>

            <div class="announcement-main">
              <div class="announcement-header-row">
                <div class="announcement-title">
                  {{ item.title }}
                  <el-tag :type="getTypeTag(item.type)" size="small" effect="plain">
                    {{ getTypeText(item.type) }}
                  </el-tag>
                </div>
                <div class="announcement-time">{{ formatTime(item.publishTime) }}</div>
              </div>

              <div class="announcement-body">{{ truncateContent(item.content) }}</div>

              <div class="announcement-meta">
                <span class="view-count">
                  <el-icon><View /></el-icon>
                  {{ item.viewCount || 0 }} 阅读
                </span>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Bell, View } from '@element-plus/icons-vue';
import { request } from '@/utils/request.js';
import BackButton from '@/components/common/BackButton.vue';

const router = useRouter();

const announcements = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const filterType = ref('');

const typeMap = {
  1: { text: '系统通知', tag: 'danger' },
  2: { text: '维护通知', tag: 'warning' },
  3: { text: '节日通知', tag: 'success' },
  4: { text: '活动通知', tag: 'primary' }
};

onMounted(() => {
  loadAnnouncements();
});

const loadAnnouncements = async () => {
  loading.value = true;
  try {
    const response = await request.get('/api/announcement/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        status: 1,
        type: filterType.value || undefined
      }
    });

    if (response.code === 200) {
      announcements.value = response.data.records || [];
      total.value = response.data.total || 0;
    }
  } catch (error) {
    console.error('获取公告列表失败:', error);
    ElMessage.error('获取公告列表失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1;
  loadAnnouncements();
};

const handlePageChange = () => {
  loadAnnouncements();
};

const handleSizeChange = () => {
  currentPage.value = 1;
  loadAnnouncements();
};

const handleAnnouncementClick = (item) => {
  // 打开公告详情对话框或跳转到详情页
  ElMessage.info('公告详情功能开发中');
};

const getTypeTag = (type) => {
  return typeMap[type]?.tag || 'info';
};

const getTypeText = (type) => {
  return typeMap[type]?.text || '其他';
};

const getTypeColor = (type) => {
  const colorMap = {
    1: '#f56c6c',
    2: '#e6a23c',
    3: '#67c23a',
    4: '#409eff'
  };
  return colorMap[type] || '#909399';
};

const getTypeBgColor = (type) => {
  const colorMap = {
    1: '#fef0f0',
    2: '#fdf6ec',
    3: '#f0f9ff',
    4: '#ecf5ff'
  };
  return colorMap[type] || '#f5f7fa';
};

const truncateContent = (content, maxLength = 100) => {
  if (!content) return '';
  return content.length > maxLength ? content.substring(0, maxLength) + '...' : content;
};

const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}`;
};
</script>

<style lang="scss" scoped>
.announcement-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .announcement-card {
    margin-top: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }
  }

  .filter-bar {
    margin-bottom: 20px;
  }

  .announcement-content {
    min-height: 400px;

    .announcement-card-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      margin-bottom: 12px;
      background-color: #ffffff;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }

      .announcement-left {
        flex-shrink: 0;

        .announcement-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 50px;
          height: 50px;
          border-radius: 50%;
        }
      }

      .announcement-main {
        flex: 1;
        overflow: hidden;

        .announcement-header-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .announcement-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 500;
            color: #303133;
          }

          .announcement-time {
            font-size: 13px;
            color: #909399;
          }
        }

        .announcement-body {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin-bottom: 8px;
        }

        .announcement-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .view-count {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
            color: #909399;
          }
        }
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}
</style>
