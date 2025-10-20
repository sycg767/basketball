<template>
  <div class="notification-list-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="page-title">通知中心</span>
      </template>
    </el-page-header>

    <el-card class="notification-card">
      <!-- 头部 -->
      <template #header>
        <div class="card-header">
          <h3>通知中心</h3>
          <div class="header-actions">
            <el-button
              v-if="hasUnread"
              type="primary"
              size="small"
              @click="handleMarkAllRead"
            >
              全部已读
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选器 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="unread">未读</el-radio-button>
          <el-radio-button value="BOOKING_SUCCESS">预订通知</el-radio-button>
          <el-radio-button value="COURSE_ENROLL_SUCCESS">课程通知</el-radio-button>
          <el-radio-button value="PAYMENT_SUCCESS">支付通知</el-radio-button>
          <el-radio-button value="MEMBER_CARD_EXPIRE_SOON">会员提醒</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 通知列表 -->
      <div v-loading="loading" class="notification-content">
        <template v-if="notifications.length === 0 && !loading">
          <el-empty description="暂无通知" />
        </template>

        <template v-else>
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-card"
            :class="{ 'is-unread': item.isRead === 0 }"
            @click="handleNotificationClick(item)"
          >
            <div class="notification-left">
              <div class="notification-icon" :style="{ backgroundColor: getTypeBgColor(item.type) }">
                <el-icon :size="24" :color="getTypeColor(item.type)">
                  <component :is="getTypeIcon(item.type)" />
                </el-icon>
              </div>
            </div>

            <div class="notification-main">
              <div class="notification-header-row">
                <div class="notification-title">
                  {{ item.title }}
                  <el-tag v-if="item.isRead === 0" type="danger" size="small" effect="dark">
                    未读
                  </el-tag>
                </div>
                <div class="notification-time">{{ formatTime(item.sendTime) }}</div>
              </div>

              <div class="notification-body">{{ item.content }}</div>

              <div v-if="item.relatedType" class="notification-meta">
                <el-tag size="small" type="info">{{ getRelatedTypeName(item.relatedType) }}</el-tag>
                <span v-if="item.relatedId" class="related-id">ID: {{ item.relatedId }}</span>
              </div>
            </div>

            <div class="notification-actions">
              <el-button
                v-if="item.isRead === 0"
                type="text"
                size="small"
                @click.stop="handleMarkRead(item)"
              >
                标记已读
              </el-button>
              <el-button
                type="text"
                size="small"
                @click.stop="handleDelete(item)"
              >
                删除
              </el-button>
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
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  SuccessFilled,
  InfoFilled,
  WarningFilled,
  MessageBox
} from '@element-plus/icons-vue';
import {
  getNotificationList,
  getUnreadNotifications,
  markAsRead,
  markAllAsRead,
  deleteNotification
} from '@/api/notification';

const router = useRouter();

const notifications = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const filterType = ref('');

const hasUnread = computed(() => notifications.value.some(item => item.isRead === 0));

onMounted(() => {
  loadNotifications();
});

const loadNotifications = async () => {
  loading.value = true;
  try {
    let res;
    if (filterType.value === 'unread') {
      res = await getUnreadNotifications({
        page: currentPage.value,
        size: pageSize.value
      });
    } else {
      res = await getNotificationList({
        page: currentPage.value,
        size: pageSize.value,
        type: filterType.value || undefined
      });
    }

    if (res.code === 200) {
      notifications.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知列表失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1;
  loadNotifications();
};

const handlePageChange = () => {
  loadNotifications();
};

const handleSizeChange = () => {
  currentPage.value = 1;
  loadNotifications();
};

const goBack = () => {
  router.back();
};

const handleNotificationClick = (item) => {
  router.push(`/notification/${item.id}`);
};

const handleMarkRead = async (item) => {
  try {
    await markAsRead(item.id);
    item.isRead = 1;
    ElMessage.success('已标记为已读');
  } catch (error) {
    console.error('标记已读失败:', error);
    ElMessage.error('操作失败');
  }
};

const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要将所有未读消息标记为已读吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await markAllAsRead();
    notifications.value.forEach(item => {
      item.isRead = 1;
    });
    ElMessage.success('已全部标记为已读');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('全部已读失败:', error);
      ElMessage.error('操作失败');
    }
  }
};

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条通知吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await deleteNotification(item.id);
    ElMessage.success('删除成功');
    loadNotifications();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

const getTypeIcon = (type) => {
  const iconMap = {
    BOOKING_SUCCESS: SuccessFilled,
    BOOKING_CANCEL: WarningFilled,
    COURSE_ENROLL_SUCCESS: SuccessFilled,
    COURSE_ENROLL_CANCEL: WarningFilled,
    PAYMENT_SUCCESS: SuccessFilled,
    REFUND_SUCCESS: InfoFilled,
    MEMBER_CARD_EXPIRE_SOON: WarningFilled,
    COURSE_START_REMINDER: InfoFilled,
    BOOKING_START_REMINDER: InfoFilled
  };

  return iconMap[type] || MessageBox;
};

const getTypeColor = (type) => {
  const colorMap = {
    BOOKING_SUCCESS: '#67C23A',
    BOOKING_CANCEL: '#E6A23C',
    COURSE_ENROLL_SUCCESS: '#67C23A',
    COURSE_ENROLL_CANCEL: '#E6A23C',
    PAYMENT_SUCCESS: '#67C23A',
    REFUND_SUCCESS: '#409EFF',
    MEMBER_CARD_EXPIRE_SOON: '#E6A23C',
    COURSE_START_REMINDER: '#409EFF',
    BOOKING_START_REMINDER: '#409EFF'
  };

  return colorMap[type] || '#909399';
};

const getTypeBgColor = (type) => {
  const colorMap = {
    BOOKING_SUCCESS: '#F0F9FF',
    BOOKING_CANCEL: '#FEF6EC',
    COURSE_ENROLL_SUCCESS: '#F0F9FF',
    COURSE_ENROLL_CANCEL: '#FEF6EC',
    PAYMENT_SUCCESS: '#F0F9FF',
    REFUND_SUCCESS: '#EFF6FF',
    MEMBER_CARD_EXPIRE_SOON: '#FEF6EC',
    COURSE_START_REMINDER: '#EFF6FF',
    BOOKING_START_REMINDER: '#EFF6FF'
  };

  return colorMap[type] || '#F5F7FA';
};

const getRelatedTypeName = (type) => {
  const nameMap = {
    BOOKING: '预订',
    COURSE: '课程',
    PAYMENT: '支付',
    MEMBER_CARD: '会员卡'
  };

  return nameMap[type] || type;
};

const formatTime = (time) => {
  if (!time) return '';

  const date = new Date(time);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}`;
};
</script>

<style lang="scss" scoped>
.notification-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .page-title {
    font-size: 18px;
    font-weight: 500;
  }

  .notification-card {
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

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .filter-bar {
    margin-bottom: 20px;
  }

  .notification-content {
    min-height: 400px;

    .notification-card {
      display: flex;
      gap: 16px;
      padding: 16px;
      margin-bottom: 12px;
      background-color: #FFFFFF;
      border: 1px solid #EBEEF5;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }

      &.is-unread {
        border-left: 4px solid #409EFF;
        background-color: #FFF;
      }

      .notification-left {
        flex-shrink: 0;

        .notification-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 50px;
          height: 50px;
          border-radius: 50%;
        }
      }

      .notification-main {
        flex: 1;
        overflow: hidden;

        .notification-header-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .notification-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 500;
            color: #303133;
          }

          .notification-time {
            font-size: 13px;
            color: #909399;
          }
        }

        .notification-body {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin-bottom: 8px;
        }

        .notification-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .related-id {
            font-size: 13px;
            color: #909399;
          }
        }
      }

      .notification-actions {
        flex-shrink: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 8px;
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
