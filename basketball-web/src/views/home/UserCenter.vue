<template>
  <div class="user-center-container">
    <!-- 顶部导航栏 -->
    <div class="user-header">
      <div class="header-content">
        <div class="logo-section">
          <el-icon :size="28" color="#ffffff"><Basketball /></el-icon>
          <h1>用户中心</h1>
        </div>
        <div class="user-info">
          <el-avatar :size="32" style="margin-right: 12px">
            {{ getUserInitial() }}
          </el-avatar>
          <span class="username">{{ userStore.realName || userStore.username || '用户' }}</span>
          <el-button type="primary" link @click="goProfile">个人中心</el-button>
          <el-button type="danger" link @click="handleLogout">退出</el-button>
        </div>
      </div>
    </div>

    <div class="user-content">
      <!-- 用户信息卡片 -->
      <div class="user-info-card">
        <div class="card-content">
          <div class="user-avatar">
            <el-avatar :size="64" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              {{ getUserInitial() }}
            </el-avatar>
          </div>
          <div class="user-details">
            <h2>{{ userStore.realName || userStore.username || '用户' }}</h2>
            <div class="user-meta">
              <span class="member-level" v-if="userStore.memberLevel > 0">
                {{ getMemberLevelText() }}
              </span>
              <span class="user-id">ID: {{ userStore.userId }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷功能入口 -->
      <div class="quick-actions">
        <h3 class="section-title">快捷功能</h3>
        <div class="actions-grid">
          <div class="action-card" @click="goToVenue">
            <div class="action-icon blue">
              <el-icon :size="32"><House /></el-icon>
            </div>
            <div class="action-content">
              <h4>场地预订</h4>
              <p>查看和预订篮球场地</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="goToBooking">
            <div class="action-icon green">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="action-content">
              <h4>我的预订</h4>
              <p>查看预订记录和状态</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="goToCourse">
            <div class="action-icon orange">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <div class="action-content">
              <h4>课程报名</h4>
              <p>查看和报名篮球课程</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="goToMember">
            <div class="action-icon purple">
              <el-icon :size="32"><Star /></el-icon>
            </div>
            <div class="action-content">
              <h4>会员中心</h4>
              <p>查看会员权益和积分</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="goToPayment">
            <div class="action-icon red">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="action-content">
              <h4>支付管理</h4>
              <p>管理支付方式和账单</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="goToNotifications">
            <div class="action-icon cyan">
              <el-icon :size="32"><Bell /></el-icon>
            </div>
            <div class="action-content">
              <h4>通知中心</h4>
              <p>查看系统通知和消息</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 系统公告 -->
      <div class="announcement-section">
        <AnnouncementSection />
      </div>

      <!-- 最近活动 -->
      <div class="recent-activities">
        <h3 class="section-title">最近活动</h3>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-icon blue">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="activity-content">
              <h4>场地预订</h4>
              <p class="activity-time">2小时前</p>
            </div>
            <div class="activity-status">
              <el-tag type="success" size="small">已确认</el-tag>
            </div>
          </div>

          <div class="activity-item">
            <div class="activity-icon orange">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="activity-content">
              <h4>课程报名</h4>
              <p class="activity-time">1天前</p>
            </div>
            <div class="activity-status">
              <el-tag type="primary" size="small">进行中</el-tag>
            </div>
          </div>

          <div class="activity-item">
            <div class="activity-icon green">
              <el-icon><Money /></el-icon>
            </div>
            <div class="activity-content">
              <h4>积分兑换</h4>
              <p class="activity-time">3天前</p>
            </div>
            <div class="activity-status">
              <el-tag type="info" size="small">已完成</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/store/modules/user';
import AnnouncementSection from '@/components/AnnouncementSection.vue';
import {
  Basketball,
  House,
  Calendar,
  Reading,
  Star,
  Money,
  Bell,
  ArrowRight
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 获取用户名首字母
const getUserInitial = () => {
  const name = userStore.realName || userStore.username || '用户';
  return name.charAt(0).toUpperCase();
};

// 获取会员等级文本
const getMemberLevelText = () => {
  const levels = {
    1: '银卡会员',
    2: '金卡会员',
    3: '钻石会员'
  };
  return levels[userStore.memberLevel] || '普通会员';
};

const goProfile = () => {
  router.push('/profile');
};

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout();
    ElMessage.success('已退出登录');
    router.push('/login');
  }).catch(() => {});
};

// 快捷功能导航
const goToVenue = () => {
  router.push('/venue');
};

const goToBooking = () => {
  router.push('/booking');
};

const goToCourse = () => {
  router.push('/course');
};

const goToMember = () => {
  router.push('/member/card');
};

const goToPayment = () => {
  router.push('/payment/method');
};

const goToNotifications = () => {
  router.push('/notification/list');
};
</script>

<style lang="scss" scoped>
.user-center-container {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f0f4f8 0%, #ffffff 100%);

  .user-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
    position: sticky;
    top: 0;
    z-index: 100;

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 16px 40px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .logo-section {
        display: flex;
        align-items: center;
        gap: 12px;

        h1 {
          font-size: 24px;
          font-weight: 700;
          color: #ffffff;
          margin: 0;
          letter-spacing: 1px;
        }
      }

      .user-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .username {
          color: #ffffff;
          font-weight: 500;
          margin-right: 8px;
        }

        :deep(.el-button) {
          color: #ffffff;

          &:hover {
            color: #e0e7ff;
          }
        }

        :deep(.el-button--primary) {
          background-color: rgba(255, 255, 255, 0.2);
          border-color: rgba(255, 255, 255, 0.3);
          color: #ffffff;

          &:hover {
            background-color: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.4);
          }

          &.is-link {
            background-color: transparent;
            border-color: transparent;
          }
        }
      }
    }
  }

  .user-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px;

    // 用户信息卡片
    .user-info-card {
      background: #ffffff;
      border-radius: 16px;
      padding: 32px;
      margin-bottom: 40px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);

      .card-content {
        display: flex;
        align-items: center;
        gap: 24px;

        .user-avatar {
          flex-shrink: 0;
        }

        .user-details {
          flex: 1;

          h2 {
            font-size: 28px;
            font-weight: 700;
            color: #1a202c;
            margin: 0 0 8px 0;
          }

          .user-meta {
            display: flex;
            gap: 16px;
            align-items: center;

            .member-level {
              padding: 4px 12px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: #ffffff;
              border-radius: 20px;
              font-size: 14px;
              font-weight: 500;
            }

            .user-id {
              color: #718096;
              font-size: 14px;
            }
          }
        }
      }
    }

    // 公共标题样式
    .section-title {
      font-size: 24px;
      font-weight: 700;
      color: #1a202c;
      margin: 0 0 24px 0;
      position: relative;
      padding-bottom: 12px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 48px;
        height: 3px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
      }
    }

    // 快捷功能
    .quick-actions {
      margin-bottom: 40px;

      .actions-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
        gap: 24px;

        .action-card {
          background: #ffffff;
          border-radius: 16px;
          padding: 24px;
          display: flex;
          align-items: center;
          gap: 16px;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);

          &:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);

            .arrow-icon {
              transform: translateX(4px);
            }
          }

          .action-icon {
            width: 56px;
            height: 56px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;

            &.blue {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: #ffffff;
            }

            &.green {
              background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
              color: #ffffff;
            }

            &.orange {
              background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
              color: #ffffff;
            }

            &.purple {
              background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
              color: #ffffff;
            }

            &.red {
              background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
              color: #ffffff;
            }

            &.cyan {
              background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
              color: #ffffff;
            }
          }

          .action-content {
            flex: 1;

            h4 {
              font-size: 18px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 4px 0;
            }

            p {
              font-size: 14px;
              color: #718096;
              margin: 0;
            }
          }

          .arrow-icon {
            font-size: 16px;
            color: #a0aec0;
            transition: all 0.3s ease;
          }
        }
      }
    }

    // 公告区域样式
    .announcement-section {
      margin-bottom: 40px;

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .section-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 18px;
          font-weight: 600;
          color: #1a202c;
          margin: 0;
        }
      }

      .announcement-list {
        .empty-announcements {
          padding: 40px 20px;
          text-align: center;

          .empty-text {
            color: #a0aec0;
            font-size: 14px;
          }
        }

        .announcement-item {
          background: #ffffff;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 12px;
          border: 1px solid #e2e8f0;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
            border-color: #667eea;
          }

          .announcement-content {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .announcement-type {
              margin-bottom: 4px;
            }

            .announcement-title {
              font-size: 16px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 8px 0;
              line-height: 1.4;

              &:hover {
                color: #667eea;
              }
            }

            .announcement-desc {
              font-size: 14px;
              color: #718096;
              line-height: 1.5;
              margin: 0;
            }

            .announcement-meta {
              display: flex;
              gap: 16px;
              align-items: center;
              margin-top: 8px;

              .publish-time,
              .view-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: #a0aec0;
              }
            }
          }

          .announcement-actions {
            margin-top: 12px;
            display: flex;
            justify-content: flex-end;
          }
        }
      }
    }

    // 最近活动
    .recent-activities {
      .activity-list {
        display: flex;
        flex-direction: column;
        gap: 16px;

        .activity-item {
          background: #ffffff;
          border-radius: 12px;
          padding: 20px;
          display: flex;
          align-items: center;
          gap: 16px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

          .activity-icon {
            width: 48px;
            height: 48px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;

            &.blue {
              background: rgba(102, 126, 234, 0.1);
              color: #667eea;
            }

            &.orange {
              background: rgba(251, 146, 60, 0.1);
              color: #fb923c;
            }

            &.green {
              background: rgba(74, 222, 128, 0.1);
              color: #4ade80;
            }
          }

          .activity-content {
            flex: 1;

            h4 {
              font-size: 16px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 4px 0;
            }

            .activity-time {
              font-size: 14px;
              color: #a0aec0;
              margin: 0;
            }
          }

          .activity-status {
            flex-shrink: 0;
          }
        }
      }
    }
  }
}
</style>