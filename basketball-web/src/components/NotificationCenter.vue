<template>
  <el-popover
    placement="bottom-end"
    :width="380"
    trigger="click"
    @show="handlePopoverShow"
  >
    <template #reference>
      <div class="notification-trigger">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-icon :size="24"><Bell /></el-icon>
        </el-badge>
      </div>
    </template>

    <div class="notification-popover">
      <!-- 头部 -->
      <div class="notification-header">
        <h4>通知消息</h4>
        <el-button
          v-if="notifications.length > 0"
          type="text"
          size="small"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
      </div>

      <el-divider style="margin: 8px 0" />

      <!-- 通知列表 -->
      <div class="notification-list">
        <template v-if="loading">
          <div class="notification-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            <p>加载中...</p>
          </div>
        </template>

        <template v-else-if="notifications.length === 0">
          <el-empty description="暂无通知" :image-size="80" />
        </template>

        <template v-else>
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-item"
            :class="{ 'is-unread': item.isRead === 0 }"
            @click="handleNotificationClick(item)"
          >
            <div class="notification-icon">
              <el-icon :size="20" :color="getTypeColor(item.type)">
                <component :is="getTypeIcon(item.type)" />
              </el-icon>
            </div>

            <div class="notification-content">
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-body">{{ item.content }}</div>
              <div class="notification-time">{{ formatTime(item.sendTime) }}</div>
            </div>

            <div v-if="item.isRead === 0" class="notification-badge"></div>
          </div>
        </template>
      </div>

      <!-- 底部 -->
      <el-divider style="margin: 8px 0" />
      <div class="notification-footer">
        <el-button type="text" @click="handleViewAll">查看全部</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Bell,
  Loading,
  SuccessFilled,
  InfoFilled,
  WarningFilled,
  CircleCloseFilled,
  MessageBox
} from '@element-plus/icons-vue';
import {
  getUnreadNotifications,
  getUnreadCount,
  markAsRead,
  markAllAsRead
} from '@/api/notification';

const router = useRouter();

const notifications = ref([]);
const unreadCount = ref(0);
const loading = ref(false);
let pollTimer = null;

onMounted(() => {
  loadUnreadCount();
  startPolling();
});

onBeforeUnmount(() => {
  stopPolling();
});

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount();
    if (res.code === 200) {
      unreadCount.value = res.data || 0;
    }
  } catch (error) {
    console.error('获取未读数量失败:', error);
  }
};

const loadNotifications = async () => {
  loading.value = true;
  try {
    const res = await getUnreadNotifications({ page: 1, size: 10 });
    if (res.code === 200) {
      notifications.value = res.data.records || [];
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知列表失败');
  } finally {
    loading.value = false;
  }
};

const handlePopoverShow = () => {
  loadNotifications();
};

const handleNotificationClick = async (item) => {
  // 标记为已读
  if (item.isRead === 0) {
    try {
      await markAsRead(item.id);
      item.isRead = 1;
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    } catch (error) {
      console.error('标记已读失败:', error);
    }
  }

  // 跳转到通知详情或相关页面
  router.push(`/notification/${item.id}`);
};

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead();
    notifications.value.forEach(item => {
      item.isRead = 1;
    });
    unreadCount.value = 0;
    ElMessage.success('已全部标记为已读');
  } catch (error) {
    console.error('全部已读失败:', error);
    ElMessage.error('操作失败');
  }
};

const handleViewAll = () => {
  router.push('/notification/list');
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

const formatTime = (time) => {
  if (!time) return '';

  const now = new Date();
  const notifyTime = new Date(time);
  const diff = now - notifyTime;

  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;

  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`;
  } else {
    const year = notifyTime.getFullYear();
    const month = String(notifyTime.getMonth() + 1).padStart(2, '0');
    const date = String(notifyTime.getDate()).padStart(2, '0');
    return `${year}-${month}-${date}`;
  }
};

const startPolling = () => {
  // 每30秒轮询一次未读数量
  pollTimer = setInterval(() => {
    loadUnreadCount();
  }, 30000);
};

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
};
</script>

<style lang="scss" scoped>
.notification-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;

  &:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }

  .el-icon {
    color: #606266;
  }
}

.notification-popover {
  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4px;

    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }

  .notification-list {
    max-height: 400px;
    overflow-y: auto;

    .notification-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px 0;
      color: #909399;

      .el-icon {
        font-size: 32px;
        margin-bottom: 8px;
      }

      p {
        margin: 0;
        font-size: 14px;
      }
    }

    .notification-item {
      position: relative;
      display: flex;
      gap: 12px;
      padding: 12px;
      cursor: pointer;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background-color: #F5F7FA;
      }

      &.is-unread {
        background-color: #EFF6FF;

        &:hover {
          background-color: #E1EDFF;
        }
      }

      .notification-icon {
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: #F5F7FA;
      }

      .notification-content {
        flex: 1;
        overflow: hidden;

        .notification-title {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .notification-body {
          font-size: 13px;
          color: #606266;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          line-height: 1.4;
        }

        .notification-time {
          font-size: 12px;
          color: #909399;
        }
      }

      .notification-badge {
        position: absolute;
        top: 18px;
        right: 12px;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: #F56C6C;
      }
    }
  }

  .notification-footer {
    text-align: center;
    padding: 4px 0;
  }
}
</style>
