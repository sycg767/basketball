<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="home-header">
      <div class="header-content">
        <div class="logo-section">
          <el-icon :size="32" color="#ffffff"><Basketball /></el-icon>
          <h1>篮球场馆预约</h1>
        </div>
        <div class="user-info">
          <template v-if="userStore.isLogin">
            <!-- 通知中心 -->
            <div class="notification-wrapper">
              <NotificationCenter />
            </div>

            <el-avatar :size="36">
              {{ getUserInitial() }}
            </el-avatar>
            <span class="username">{{ userStore.realName || userStore.username || '用户' }}</span>
            <el-button type="primary" link @click="goProfile">个人中心</el-button>
            <el-button type="danger" link @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" plain @click="goLogin">登录</el-button>
            <el-button type="primary" @click="goRegister">注册</el-button>
          </template>
        </div>
      </div>
    </div>

    <div class="home-content">
      <!-- Hero区域 -->
      <div class="hero-section" v-if="!userStore.isLogin">
        <div class="hero-content">
          <h2 class="hero-title">欢迎来到篮球场馆预约系统</h2>
          <p class="hero-subtitle">在这里轻松预订心仪的篮球场地，享受专业的运动体验</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="goToVenue">
              <el-icon><House /></el-icon>
              <span>浏览场地</span>
            </el-button>
            <el-button type="success" size="large" plain @click="goToLogin">
              <el-icon><User /></el-icon>
              <span>立即登录</span>
            </el-button>
          </div>
          <div class="features">
            <div class="feature-tag">
              <el-icon color="#409eff"><Check /></el-icon>
              <span>在线预订</span>
            </div>
            <div class="feature-tag">
              <el-icon color="#67c23a"><Check /></el-icon>
              <span>专业场地</span>
            </div>
            <div class="feature-tag">
              <el-icon color="#e6a23c"><Check /></el-icon>
              <span>优惠会员</span>
            </div>
            <div class="feature-tag">
              <el-icon color="#f56c6c"><Check /></el-icon>
              <span>便捷支付</span>
            </div>
          </div>
        </div>
        <div class="hero-image">
          <div class="image-placeholder">
            <el-icon :size="120" color="#409eff"><Basketball /></el-icon>
          </div>
        </div>
      </div>

      <!-- 已登录用户的个性化欢迎区域 -->
      <div class="user-welcome-section" v-else>
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userStore.realName || userStore.username || '用户' }}！</h2>
            <p>今天是美好的一天，开始您的运动之旅吧！</p>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ getMemberLevelText() }}</div>
              <div class="stat-label">会员等级</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ pointsBalance }}</div>
              <div class="stat-label">积分余额</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ accountBalance.toFixed(2) }}</div>
              <div class="stat-label">账户余额</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ cardBalance.toFixed(2) }}</div>
              <div class="stat-label">会员卡余额</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统公告 (仅已登录用户显示) -->
      <div class="announcement-section" v-if="userStore.isLogin">
        <AnnouncementSection />
      </div>

      <!-- 快捷功能入口 -->
      <div class="quick-actions">
        <h3 class="section-title">快捷功能</h3>
        <div class="actions-grid">
          <div class="action-card" @click="goToVenue">
            <div class="action-icon blue">
              <el-icon :size="40"><House /></el-icon>
            </div>
            <h4>浏览场地</h4>
            <p>查看所有可用的篮球场地</p>
          </div>

          <div class="action-card" @click="goToBooking">
            <div class="action-icon green">
              <el-icon :size="40"><Calendar /></el-icon>
            </div>
            <h4>我的预订</h4>
            <p>查看和管理您的场地预订</p>
          </div>

          <div class="action-card" @click="goToCourse">
            <div class="action-icon orange">
              <el-icon :size="40"><Reading /></el-icon>
            </div>
            <h4>课程报名</h4>
            <p>参加专业的篮球培训课程</p>
          </div>

          <div class="action-card" @click="goToMyCourses">
            <div class="action-icon purple">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <h4>我的课程</h4>
            <p>查看我的课程报名记录</p>
          </div>

          <div class="action-card" @click="goToMember">
            <div class="action-icon red">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <h4>会员中心</h4>
            <p>查看会员权益和积分</p>
          </div>
        </div>
      </div>

      <!-- 系统简介 -->
      <div class="intro-section">
        <h3 class="section-title">系统功能介绍</h3>
        <div class="intro-grid">
          <div class="intro-item">
            <div class="intro-icon blue">
              <el-icon :size="32"><Basketball /></el-icon>
            </div>
            <h4>场地预订</h4>
            <p>支持多种场地类型，实时查看场地状态，方便快捷的预订流程</p>
          </div>

          <div class="intro-item">
            <div class="intro-icon green">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <h4>灵活时段</h4>
            <p>灵活的预订时段选择，支持在线支付，预订记录随时查询</p>
          </div>

          <div class="intro-item">
            <div class="intro-icon orange">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <h4>课程培训</h4>
            <p>专业教练团队，多样化课程选择，线上报名便捷高效</p>
          </div>

          <div class="intro-item">
            <div class="intro-icon red">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <h4>会员体系</h4>
            <p>会员等级制度，积分奖励机制，专属优惠折扣</p>
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
import NotificationCenter from '@/components/NotificationCenter.vue';
import AnnouncementSection from '@/components/AnnouncementSection.vue';
import {
  House,
  Calendar,
  Reading,
  User,
  Basketball,
  Check,
  Document
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 用户统计数据
const pointsBalance = ref(0);
const accountBalance = ref(0.00);
const cardBalance = ref(0.00);

// 获取用户统计数据
const loadUserStats = async () => {
  if (userStore.isLogin) {
    try {
      // 获取积分余额
      const pointsResponse = await getMyPoints();
      if (pointsResponse.code === 200) {
        pointsBalance.value = pointsResponse.data || 0;
      }

      // 获取账户余额
      const balanceResponse = await getUserBalance();
      if (balanceResponse.code === 200) {
        accountBalance.value = balanceResponse.data || 0;
      }

      // 获取会员卡余额
      const cardsResponse = await getMyCards({ page: 1, pageSize: 100 });
      if (cardsResponse.code === 200 && cardsResponse.data && cardsResponse.data.records) {
        // 计算所有储值卡的总余额
        cardBalance.value = cardsResponse.data.records
          .filter(card => card.cardType === 2 && card.status === 1) // 2-储值卡, 1-已激活
          .reduce((sum, card) => sum + (card.balance || 0), 0);
      }
    } catch (error) {
      console.error('获取用户统计数据失败:', error);
    }
  }
};

// 页面加载时获取统计数据
onMounted(() => {
  loadUserStats();
});

// 获取用户名首字母
const getUserInitial = () => {
  const name = userStore.realName || userStore.username || '用户';
  return name.charAt(0).toUpperCase();
};

const goLogin = () => {
  router.push('/login');
};

// 获取会员等级文本
const getMemberLevelText = () => {
  const levels = {
    0: '普通用户',
    1: '银卡会员',
    2: '金卡会员',
    3: '铂金会员',
    4: 'VIP会员'
  };
  return levels[userStore.memberLevel] || '普通用户';
};

const goRegister = () => {
  router.push('/register');
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
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  router.push('/user-center');
};

const goToCourse = () => {
  router.push('/course');
};

const goToMyCourses = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  router.push('/my-courses');
};

const goToMember = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  router.push('/member/card');
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.home-container {
  min-height: 100vh;
  background: $bg-secondary;

  .home-header {
    // Apple风格毛玻璃导航栏
    @include frosted-glass(blur(20px), 0.85);
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow: $shadow-sm;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: $z-index-sticky;

    .header-content {
      @include container;
      padding: $spacing-4 $spacing-8;
      @include flex-between;

      .logo-section {
        display: flex;
        align-items: center;
        gap: $spacing-3;

        h1 {
          font-size: $font-size-2xl;
          font-weight: $font-weight-bold;
          color: $text-primary;
          margin: 0;
          letter-spacing: $letter-spacing-tight;
        }

        :deep(.el-icon) {
          color: $primary;
        }
      }

      .user-info {
        display: flex;
        align-items: center;
        gap: $spacing-3;

        .notification-wrapper {
          display: flex;
          align-items: center;
          margin-right: $spacing-4;
        }

        .username {
          color: $text-primary;
          font-weight: $font-weight-medium;
          font-size: $font-size-base;
        }

        :deep(.el-avatar) {
          background: $primary-gradient;
          font-weight: $font-weight-semibold;
        }

        :deep(.el-button) {
          @include button-base;
          padding: $spacing-2 $spacing-4;
          font-size: $font-size-sm;
          border-radius: $radius-full;

          &.el-button--primary {
            @include button-primary;

            &.is-plain {
              background: transparent;
              color: $primary;
              border: 1px solid $primary;

              &:hover {
                background: rgba($primary, 0.1);
              }
            }
          }

          &.el-button--danger {
            background: transparent;
            color: $error;
            border: none;

            &:hover {
              background: rgba($error, 0.1);
            }
          }

          &.is-link {
            background: transparent;
            color: $primary;
            border: none;
            padding: $spacing-2 $spacing-3;

            &:hover {
              background: rgba($primary, 0.08);
            }
          }
        }
      }
    }
  }

  .home-content {
    @include container;
    padding-bottom: $spacing-16;

    // Hero区域 - 简化设计
    .hero-section {
      display: flex;
      align-items: center;
      gap: $spacing-16;
      padding: $spacing-16 0;

      @media (max-width: $breakpoint-md) {
        flex-direction: column;
        gap: $spacing-8;
        padding: $spacing-12 0;
      }

      .hero-content {
        flex: 1;

        .hero-title {
          @include text-heading-1;
          font-size: $font-size-5xl;
          margin: 0 0 $spacing-4 0;
          color: $text-primary;

          @media (max-width: $breakpoint-md) {
            font-size: $font-size-4xl;
          }
        }

        .hero-subtitle {
          @include text-body;
          font-size: $font-size-lg;
          color: $text-secondary;
          margin: 0 0 $spacing-8 0;
        }

        .hero-actions {
          display: flex;
          gap: $spacing-4;
          margin-bottom: $spacing-8;

          :deep(.el-button) {
            @include button-base;
            padding: $spacing-4 $spacing-8;
            font-size: $font-size-lg;
            border-radius: $radius-lg;

            &.el-button--primary {
              @include button-primary;
            }

            &.el-button--success {
              background: transparent;
              color: $success;
              border: 2px solid $success;

              &:hover {
                background: rgba($success, 0.1);
              }
            }
          }
        }

        .features {
          display: flex;
          gap: $spacing-4;
          flex-wrap: wrap;

          .feature-tag {
            @include tag-base;
            @include tag-primary;
            padding: $spacing-2 $spacing-4;
            background: $bg-primary;
            box-shadow: $shadow-sm;

            span {
              font-size: $font-size-sm;
              color: $text-secondary;
              font-weight: $font-weight-medium;
            }

            :deep(.el-icon) {
              color: $primary;
            }
          }
        }
      }

      .hero-image {
        flex: 0 0 400px;

        @media (max-width: $breakpoint-md) {
          flex: 1;
          width: 100%;
        }

        .image-placeholder {
          width: 100%;
          height: 400px;
          background: $bg-primary;
          border-radius: $radius-2xl;
          @include flex-center;
          box-shadow: $shadow-card;

          :deep(.el-icon) {
            color: $primary;
            opacity: 0.3;
          }
        }
      }
    }

    // 公共标题样式
    .section-title {
      @include text-heading-2;
      margin: 0 0 $spacing-8 0;
      text-align: center;
      position: relative;
      padding-bottom: $spacing-4;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 48px;
        height: 4px;
        background: $primary;
        border-radius: $radius-full;
      }
    }

    // 快捷功能
    .quick-actions {
      margin: $spacing-16 0;

      .actions-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: $spacing-6;

        @media (max-width: $breakpoint-lg) {
          grid-template-columns: repeat(2, 1fr);
        }

        @media (max-width: $breakpoint-sm) {
          grid-template-columns: 1fr;
        }

        .action-card {
          @include card-base;
          padding: $spacing-8 $spacing-6;
          text-align: center;
          cursor: pointer;
          border-radius: $radius-xl;

          &:hover {
            @include card-hover;

            .action-icon {
              transform: scale(1.05);
            }
          }

          &:active {
            @include card-active;
          }

          .action-icon {
            width: 72px;
            height: 72px;
            margin: 0 auto $spacing-5;
            border-radius: $radius-full;
            @include flex-center;
            transition: $transition-transform;

            &.blue {
              background: rgba($primary, 0.1);
              color: $primary;
            }

            &.green {
              background: rgba($success, 0.1);
              color: $success;
            }

            &.orange {
              background: rgba($warning, 0.1);
              color: $warning;
            }

            &.purple {
              background: rgba(#9333ea, 0.1);
              color: #9333ea;
            }

            &.red {
              background: rgba($error, 0.1);
              color: $error;
            }
          }

          h4 {
            font-size: $font-size-lg;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            margin: 0 0 $spacing-2 0;
          }

          p {
            @include text-caption;
            margin: 0;
          }
        }
      }
    }

    // 系统简介
    .intro-section {
      margin: $spacing-16 0;

      .intro-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: $spacing-6;

        @media (max-width: $breakpoint-md) {
          grid-template-columns: 1fr;
        }

        .intro-item {
          @include card-base;
          padding: $spacing-8;
          border-radius: $radius-xl;

          &:hover {
            @include card-hover;
          }

          .intro-icon {
            width: 56px;
            height: 56px;
            border-radius: $radius-lg;
            @include flex-center;
            margin-bottom: $spacing-5;

            &.blue {
              background: rgba($primary, 0.1);
              color: $primary;
            }

            &.green {
              background: rgba($success, 0.1);
              color: $success;
            }

            &.orange {
              background: rgba($warning, 0.1);
              color: $warning;
            }

            &.red {
              background: rgba($error, 0.1);
              color: $error;
            }
          }

          h4 {
            font-size: $font-size-xl;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            margin: 0 0 $spacing-3 0;
          }

          p {
            @include text-body;
            margin: 0;
          }
        }
      }
    }

    // 公告区域样式
    .announcement-section {
      margin: $spacing-16 0;

      .section-title {
        @include text-heading-3;
        margin: 0 0 $spacing-6 0;
        text-align: left;
        position: relative;
        padding-bottom: $spacing-3;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 40px;
          height: 3px;
          background: $primary;
          border-radius: $radius-full;
        }
      }
    }

    // 用户欢迎区域样式
    .user-welcome-section {
      margin: $spacing-10 0;

      .welcome-content {
        background: $white;
        padding: $spacing-10;
        border-radius: $radius-xl;
        box-shadow: $shadow-sm;
        border: 1px solid $border-color;
        @include flex-between;
        gap: $spacing-10;
        transition: all $duration-base $ease-in-out;

        &:hover {
          box-shadow: $shadow-md;
        }

        @media (max-width: $breakpoint-md) {
          flex-direction: column;
          gap: $spacing-6;
          padding: $spacing-8;
        }

        .welcome-text {
          flex: 1;

          h2 {
            font-size: $font-size-3xl;
            font-weight: 600;
            color: $text-primary;
            margin: 0 0 $spacing-2 0;
            letter-spacing: -0.02em;

            @media (max-width: $breakpoint-md) {
              font-size: $font-size-2xl;
            }
          }

          p {
            font-size: $font-size-base;
            color: $text-secondary;
            margin: 0;
            font-weight: 500;
          }
        }

        .user-stats {
          display: flex;
          gap: $spacing-8;
          align-items: center;

          @media (max-width: $breakpoint-md) {
            gap: $spacing-6;
            width: 100%;
            justify-content: space-around;
          }

          .stat-item {
            text-align: center;
            padding: $spacing-4 $spacing-6;
            background: $bg-secondary;
            border-radius: $radius-lg;
            min-width: 100px;

            .stat-value {
              font-size: $font-size-2xl;
              font-weight: 600;
              color: $primary;
              margin-bottom: $spacing-1;
              line-height: 1.2;
            }

            .stat-label {
              font-size: $font-size-sm;
              color: $text-secondary;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}
</style>