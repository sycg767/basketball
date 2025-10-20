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
          <el-button type="info" link @click="refreshData">
            <el-icon :size="14"><Refresh /></el-icon>
            刷新
          </el-button>
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
            <!-- 会员权益说明 -->
            <div class="member-benefits" v-if="userStore.memberLevel > 0">
              <p>✨ 会员专享权益：场地预订折扣、优先选场、积分加倍</p>
            </div>
          </div>
        </div>
        <!-- 账户信息面板 -->
        <div class="account-panel">
          <div class="account-item">
            <div class="account-label">积分余额</div>
            <div class="account-value">{{ userPoints }}</div>
          </div>
          <div class="account-item">
            <div class="account-label">账户余额</div>
            <div class="account-value">¥{{ userBalance.toFixed(2) }}</div>
          </div>
          <div class="account-item">
            <div class="account-label">会员到期</div>
            <div class="account-value">{{ memberExpireDate || '永久有效' }}</div>
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
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/store/modules/user';
import { getMyPoints, getUserBalance, getMyCards } from '@/api/member';
import AnnouncementSection from '@/components/AnnouncementSection.vue';
import {
  Basketball,
  House,
  Calendar,
  Reading,
  Star,
  Money,
  Bell,
  ArrowRight,
  Refresh
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 用户数据响应式变量
const userPoints = ref(0);
const userBalance = ref(0.00);
const memberExpireDate = ref('');
const userCards = ref([]);

// 页面加载时获取用户数据
onMounted(async () => {
  await loadUserData();
});

// 加载用户数据
const loadUserData = async () => {
  try {
    // 获取积分和余额
    const [pointsRes, balanceRes, cardsRes] = await Promise.all([
      getMyPoints(),
      getUserBalance(),
      getMyCards({ current: 1, size: 10 })
    ]);

    if (pointsRes.code === 200) {
      userPoints.value = pointsRes.data || 0;
    }
    if (balanceRes.code === 200) {
      userBalance.value = balanceRes.data || 0;
    }
    if (cardsRes.code === 200) {
      userCards.value = cardsRes.data?.records || [];
      // 设置会员到期时间（取最早的有效会员卡）
      const validCard = userCards.value.find(card => card.status === 1);
      if (validCard && validCard.expireTime) {
        memberExpireDate.value = new Date(validCard.expireTime).toLocaleDateString('zh-CN');
      }
    }
  } catch (error) {
    console.error('获取用户数据失败:', error);
    ElMessage.error('获取用户数据失败');
  }
};

// 刷新数据
const refreshData = async () => {
  ElMessage.info('正在刷新数据...');
  await loadUserData();
  ElMessage.success('数据已更新');
};

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    userStore.logout();
    router.push('/login');
    ElMessage.success('已退出登录');
  } catch {
    // 用户取消操作
  }
};

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
      max-width: 100%;
      margin: 0;
      padding: 12px 16px;
      display: flex;
      flex-direction: column;
      gap: 12px;

      @media (min-width: 768px) {
        max-width: 1200px;
        margin: 0 auto;
        padding: 16px 40px;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
      }

      .logo-section {
        display: flex;
        align-items: center;
        gap: 8px;

        @media (min-width: 768px) {
          gap: 12px;
        }

        h1 {
          font-size: 18px;
          font-weight: 700;
          color: #ffffff;
          margin: 0;
          letter-spacing: 1px;

          @media (min-width: 768px) {
            font-size: 24px;
          }
        }
      }

      .user-info {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
        gap: 8px;

        @media (min-width: 768px) {
          flex-wrap: nowrap;
          gap: 12px;
        }

        .username {
          color: #ffffff;
          font-weight: 500;
          font-size: 14px;
          margin-right: 4px;

          @media (min-width: 768px) {
            font-size: 16px;
            margin-right: 8px;
          }
        }

        :deep(.el-button) {
          color: #ffffff;
          font-size: 12px;

          @media (min-width: 768px) {
            font-size: 14px;
          }

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
    padding: 20px;

    // 用户信息卡片
    .user-info-card {
      background: #ffffff;
      border-radius: 16px;
      padding: 24px;
      margin-bottom: 24px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);

      .card-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 16px;

        .user-avatar {
          flex-shrink: 0;
        }

        .user-details {
          flex: 1;
          text-align: center;

          h2 {
            font-size: 24px;
            font-weight: 700;
            color: #1a202c;
            margin: 0 0 12px 0;
          }

          .user-meta {
            display: flex;
            flex-direction: column;
            gap: 8px;
            align-items: center;
            margin-bottom: 12px;

            .member-level {
              background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
              color: #8b4513;
              padding: 4px 12px;
              border-radius: 20px;
              font-size: 12px;
              font-weight: 600;
            }

            .user-id {
              color: #718096;
              font-size: 12px;
            }
          }

          .member-benefits {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
            border-radius: 12px;
            padding: 12px;
            margin-top: 12px;

            p {
              margin: 0;
              color: #4a5568;
              font-size: 12px;
              line-height: 1.4;
            }
          }
        }
      }

      // 账户信息面板
      .account-panel {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        padding: 16px;
        color: #ffffff;
        text-align: center;

        .account-item {
          margin-bottom: 16px;

          &:last-child {
            margin-bottom: 0;
          }

          .account-label {
            font-size: 12px;
            opacity: 0.9;
            margin-bottom: 6px;
          }

          .account-value {
            font-size: 20px;
            font-weight: 700;
            line-height: 1;
          }
        }
      }

      // 账户信息面板桌面端横排布局
      @media (min-width: 768px) {
        .account-panel {
          text-align: left;
          min-width: 280px;

          .account-item {
            margin-bottom: 20px;

            &:last-child {
              margin-bottom: 0;
            }

            .account-label {
              font-size: 14px;
              margin-bottom: 8px;
            }

            .account-value {
              font-size: 24px;
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
      margin-bottom: 24px;

      .section-title {
        font-size: 20px;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 16px 0;
        position: relative;
        padding-bottom: 8px;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 36px;
          height: 2px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 2px;
        }
      }

      .actions-grid {
        display: grid;
        grid-template-columns: 1fr;
        gap: 16px;

        @media (min-width: 640px) {
          grid-template-columns: repeat(2, 1fr);
        }

        @media (min-width: 1024px) {
          grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
        }

        .action-card {
          background: #ffffff;
          border-radius: 12px;
          padding: 16px;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 12px;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);

          @media (min-width: 640px) {
            flex-direction: row;
            align-items: center;
            padding: 20px;
          }

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);

            .arrow-icon {
              transform: translateX(4px);
            }
          }

          .action-icon {
            width: 48px;
            height: 48px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;

            @media (min-width: 640px) {
              width: 56px;
              height: 56px;
            }

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
              font-size: 16px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 4px 0;
            }

            p {
              font-size: 12px;
              color: #718096;
              margin: 0;
            }

            @media (min-width: 640px) {
              p {
                font-size: 14px;
              }
            }
          }

          .arrow-icon {
            font-size: 14px;
            color: #a0aec0;
            transition: all 0.3s ease;
            display: none;

            @media (min-width: 640px) {
              display: block;
            }
          }
        }
      }
    }

    // 公告区域样式
    .announcement-section {
      margin-bottom: 24px;

      @media (min-width: 768px) {
        margin-bottom: 40px;
      }

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        @media (min-width: 768px) {
          margin-bottom: 20px;
        }

        .section-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: #1a202c;
          margin: 0;

          @media (min-width: 768px) {
            font-size: 18px;
          }
        }
      }

      .announcement-list {
        .empty-announcements {
          padding: 32px 16px;
          text-align: center;

          @media (min-width: 768px) {
            padding: 40px 20px;
          }

          .empty-text {
            color: #a0aec0;
            font-size: 12px;

            @media (min-width: 768px) {
              font-size: 14px;
            }
          }
        }

        .announcement-item {
          background: #ffffff;
          border-radius: 10px;
          padding: 16px;
          margin-bottom: 10px;
          border: 1px solid #e2e8f0;
          cursor: pointer;
          transition: all 0.3s ease;

          @media (min-width: 768px) {
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 12px;
          }

          &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
            border-color: #667eea;
          }

          .announcement-content {
            display: flex;
            flex-direction: column;
            gap: 6px;

            @media (min-width: 768px) {
              gap: 8px;
            }

            .announcement-type {
              margin-bottom: 2px;

              @media (min-width: 768px) {
                margin-bottom: 4px;
              }
            }

            .announcement-title {
              font-size: 14px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 6px 0;
              line-height: 1.4;

              @media (min-width: 768px) {
                font-size: 16px;
                margin: 0 0 8px 0;
              }

              &:hover {
                color: #667eea;
              }
            }

            .announcement-desc {
              font-size: 12px;
              color: #718096;
              line-height: 1.4;
              margin: 0;

              @media (min-width: 768px) {
                font-size: 14px;
                line-height: 1.5;
              }
            }

            .announcement-meta {
              display: flex;
              flex-direction: column;
              gap: 8px;
              align-items: flex-start;
              margin-top: 6px;

              @media (min-width: 768px) {
                flex-direction: row;
                gap: 16px;
                margin-top: 8px;
              }

              .publish-time,
              .view-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 10px;
                color: #a0aec0;

                @media (min-width: 768px) {
                  font-size: 12px;
                }
              }
            }
          }

          .announcement-actions {
            margin-top: 8px;
            display: flex;
            justify-content: flex-end;

            @media (min-width: 768px) {
              margin-top: 12px;
            }
          }
        }
      }
    }

    // 最近活动
    .recent-activities {
      .section-title {
        font-size: 16px;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 16px 0;
        position: relative;
        padding-bottom: 8px;

        @media (min-width: 768px) {
          font-size: 18px;
        }

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 24px;
          height: 2px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 2px;
        }
      }

      .activity-list {
        display: flex;
        flex-direction: column;
        gap: 12px;

        @media (min-width: 768px) {
          gap: 16px;
        }

        .activity-item {
          background: #ffffff;
          border-radius: 10px;
          padding: 16px;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

          @media (min-width: 768px) {
            border-radius: 12px;
            padding: 20px;
            flex-direction: row;
            align-items: center;
            gap: 16px;
          }

          .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;

            @media (min-width: 768px) {
              width: 48px;
              height: 48px;
              border-radius: 10px;
            }

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
            text-align: center;

            @media (min-width: 768px) {
              text-align: left;
            }

            h4 {
              font-size: 14px;
              font-weight: 600;
              color: #1a202c;
              margin: 0 0 4px 0;

              @media (min-width: 768px) {
                font-size: 16px;
              }
            }

            .activity-time {
              font-size: 10px;
              color: #a0aec0;
              margin: 0;

              @media (min-width: 768px) {
                font-size: 14px;
              }
            }
          }

          .activity-status {
            flex-shrink: 0;
            display: none;

            @media (min-width: 768px) {
              display: block;
            }
          }
        }
      }
    }
  }
}
</style>