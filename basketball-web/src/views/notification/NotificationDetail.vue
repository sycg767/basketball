<template>
  <div class="notification-detail-container">
    <BackButton text="返回列表" />

    <el-card v-loading="loading">
      <template v-if="notification">
        <!-- 通知头部 -->
        <div class="notification-header">
          <div class="notification-icon" :style="{ backgroundColor: getTypeBgColor(notification.type) }">
            <el-icon :size="48" :color="getTypeColor(notification.type)">
              <component :is="getTypeIcon(notification.type)" />
            </el-icon>
          </div>

          <div class="notification-info">
            <h2 class="notification-title">{{ notification.title }}</h2>

            <div class="notification-meta">
              <el-tag :type="notification.isRead === 0 ? 'danger' : 'info'" size="small">
                {{ notification.isRead === 0 ? '未读' : '已读' }}
              </el-tag>

              <span class="notification-time">
                <el-icon><Clock /></el-icon>
                {{ formatFullTime(notification.sendTime) }}
              </span>

              <el-tag v-if="notification.relatedType" size="small" type="info">
                {{ getRelatedTypeName(notification.relatedType) }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 通知内容 -->
        <div class="notification-content">
          <h3>消息详情</h3>
          <div class="content-text">{{ notification.content }}</div>
        </div>

        <!-- 相关业务信息 -->
        <div v-if="notification.relatedType && notification.relatedId" class="related-info">
          <el-divider />
          <h3>相关信息</h3>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="关联类型">
              {{ getRelatedTypeName(notification.relatedType) }}
            </el-descriptions-item>
            <el-descriptions-item label="关联ID">
              {{ notification.relatedId }}
            </el-descriptions-item>
          </el-descriptions>

          <!-- 根据类型显示操作按钮 -->
          <div class="related-actions">
            <el-button
              v-if="notification.relatedType === 'BOOKING'"
              type="primary"
              @click="handleViewBooking"
            >
              查看预订详情
            </el-button>

            <el-button
              v-if="notification.relatedType === 'COURSE'"
              type="primary"
              @click="handleViewCourse"
            >
              查看课程详情
            </el-button>

            <el-button
              v-if="notification.relatedType === 'PAYMENT'"
              type="primary"
              @click="handleViewPayment"
            >
              查看支付详情
            </el-button>

            <el-button
              v-if="notification.relatedType === 'MEMBER_CARD'"
              type="primary"
              @click="handleViewMemberCard"
            >
              查看会员卡
            </el-button>
          </div>
        </div>

        <!-- 操作按钮 -->
        <el-divider />
        <div class="notification-actions">
          <el-button
            v-if="notification.isRead === 0"
            type="primary"
            @click="handleMarkRead"
          >
            标记已读
          </el-button>
          <el-button @click="handleDelete">删除通知</el-button>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Clock,
  SuccessFilled,
  InfoFilled,
  WarningFilled,
  MessageBox
} from '@element-plus/icons-vue';
import {
  getNotificationList,
  markAsRead,
  deleteNotification
} from '@/api/notification';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();

const notification = ref(null);
const loading = ref(false);

onMounted(() => {
  loadNotificationDetail();
});

const loadNotificationDetail = async () => {
  loading.value = true;
  try {
    const notificationId = Number(route.params.id);

    // 通过列表接口查询单个通知
    const res = await getNotificationList({ page: 1, size: 1 });

    if (res.code === 200 && res.data.records && res.data.records.length > 0) {
      // 实际应该有单独的详情接口,这里简化处理
      // 在生产环境建议后端提供 GET /api/notification/{id} 接口
      notification.value = res.data.records.find(item => item.id === notificationId) || res.data.records[0];

      // 如果未读,自动标记为已读
      if (notification.value && notification.value.isRead === 0) {
        await markAsRead(notification.value.id);
        notification.value.isRead = 1;
      }
    } else {
      ElMessage.error('通知不存在');
      router.replace('/notification/list');
    }
  } catch (error) {
    console.error('获取通知详情失败:', error);
    ElMessage.error('获取通知详情失败');
  } finally {
    loading.value = false;
  }
};


const handleMarkRead = async () => {
  try {
    await markAsRead(notification.value.id);
    notification.value.isRead = 1;
    ElMessage.success('已标记为已读');
  } catch (error) {
    console.error('标记已读失败:', error);
    ElMessage.error('操作失败');
  }
};

const handleDelete = async () => {
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

    await deleteNotification(notification.value.id);
    ElMessage.success('删除成功');
    router.replace('/notification/list');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

const handleViewBooking = () => {
  router.push(`/booking/detail/${notification.value.relatedId}`);
};

const handleViewCourse = () => {
  router.push(`/course/${notification.value.relatedId}`);
};

const handleViewPayment = () => {
  // 跳转到订单或支付记录页面
  ElMessage.info('支付详情功能开发中');
};

const handleViewMemberCard = () => {
  router.push('/member/card');
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

const formatFullTime = (time) => {
  if (!time) return '';

  const date = new Date(time);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
</script>

<style lang="scss" scoped>
.notification-detail-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;

  .notification-header {
    display: flex;
    gap: 20px;
    padding: 20px 0;

    .notification-icon {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      border-radius: 50%;
    }

    .notification-info {
      flex: 1;

      .notification-title {
        margin: 0 0 12px 0;
        font-size: 24px;
        font-weight: 500;
        color: #303133;
      }

      .notification-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

        .notification-time {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 14px;
          color: #606266;

          .el-icon {
            font-size: 16px;
          }
        }
      }
    }
  }

  .notification-content {
    padding: 20px 0;

    h3 {
      margin: 0 0 16px 0;
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }

    .content-text {
      font-size: 15px;
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  }

  .related-info {
    padding: 20px 0;

    h3 {
      margin: 0 0 16px 0;
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }

    .related-actions {
      margin-top: 16px;
      display: flex;
      gap: 12px;
    }
  }

  .notification-actions {
    padding: 20px 0;
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}
</style>
