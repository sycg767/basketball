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
            <NotificationCenter />

            <el-avatar :size="36" style="margin-right: 10px">
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
import { getMyPoints, getUserBalance } from '@/api/member';
import NotificationCenter from '@/components/NotificationCenter.vue';
import AnnouncementSection from '@/components/AnnouncementSection.vue';
import {
  House,
  Calendar,
  Reading,
  User,
  Basketball,
  Check
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 用户统计数据
const pointsBalance = ref(0);
const accountBalance = ref(0.00);

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
    1: '银卡会员',
    2: '金卡会员',
    3: '钻石会员'
  };
  return levels[userStore.memberLevel] || '普通会员';
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
.home-container {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f0f4f8 0%, #ffffff 100%);

  .home-header {
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
          font-size: 28px;
          font-weight: 700;
          color: #ffffff;
          margin: 0;
          letter-spacing: 2px;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

          &:not(.is-plain) {
            background-color: #ffffff;
            color: #667eea;

            &:hover {
              background-color: #e0e7ff;
              color: #5568d3;
            }
          }
        }
      }
    }
  }

  .home-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 40px 60px;

    // Hero区域
    .hero-section {
      display: flex;
      align-items: center;
      gap: 60px;
      padding: 60px 0;

      .hero-content {
        flex: 1;

        .hero-title {
          font-size: 42px;
          font-weight: 700;
          color: #1a202c;
          margin: 0 0 16px 0;
          line-height: 1.2;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .hero-subtitle {
          font-size: 18px;
          color: #4a5568;
          margin: 0 0 32px 0;
          line-height: 1.6;
        }

        .hero-actions {
          display: flex;
          gap: 16px;
          margin-bottom: 32px;

          :deep(.el-button) {
            padding: 12px 32px;
            font-size: 16px;
            border-radius: 8px;
          }
        }

        .features {
          display: flex;
          gap: 24px;
          flex-wrap: wrap;

          .feature-tag {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            background: #ffffff;
            border-radius: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

            span {
              font-size: 14px;
              color: #4a5568;
              font-weight: 500;
            }
          }
        }
      }

      .hero-image {
        flex: 0 0 400px;

        .image-placeholder {
          width: 100%;
          height: 400px;
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
          border-radius: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 10px 40px rgba(102, 126, 234, 0.15);
        }
      }
    }

    // 公共标题样式
    .section-title {
      font-size: 28px;
      font-weight: 700;
      color: #1a202c;
      margin: 0 0 32px 0;
      text-align: center;
      position: relative;
      padding-bottom: 16px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60px;
        height: 4px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
      }
    }

    // 快捷功能
    .quick-actions {
      margin: 60px 0;

      .actions-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 24px;

        @media (max-width: 992px) {
          grid-template-columns: repeat(2, 1fr);
        }

        @media (max-width: 576px) {
          grid-template-columns: 1fr;
        }

        .action-card {
          background: #ffffff;
          border-radius: 16px;
          padding: 32px 24px;
          text-align: center;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

          &:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);

            .action-icon {
              transform: scale(1.1);
            }
          }

          .action-icon {
            width: 80px;
            height: 80px;
            margin: 0 auto 20px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;

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

            &.red {
              background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
              color: #ffffff;
            }
          }

          h4 {
            font-size: 18px;
            font-weight: 600;
            color: #1a202c;
            margin: 0 0 12px 0;
          }

          p {
            font-size: 14px;
            color: #718096;
            margin: 0;
            line-height: 1.6;
          }
        }
      }
    }

    // 系统简介
    .intro-section {
      margin: 60px 0;

      .intro-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 32px;

        @media (max-width: 768px) {
          grid-template-columns: 1fr;
        }

        .intro-item {
          background: #ffffff;
          border-radius: 16px;
          padding: 32px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
          }

          .intro-icon {
            width: 64px;
            height: 64px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;

            &.blue {
              background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
              color: #667eea;
            }

            &.green {
              background: linear-gradient(135deg, rgba(132, 250, 176, 0.1) 0%, rgba(143, 211, 244, 0.1) 100%);
              color: #48bb78;
            }

            &.orange {
              background: linear-gradient(135deg, rgba(255, 236, 210, 0.3) 0%, rgba(252, 182, 159, 0.3) 100%);
              color: #ed8936;
            }

            &.red {
              background: linear-gradient(135deg, rgba(255, 154, 158, 0.1) 0%, rgba(254, 207, 239, 0.1) 100%);
              color: #f56565;
            }
          }

          h4 {
            font-size: 20px;
            font-weight: 600;
            color: #1a202c;
            margin: 0 0 12px 0;
          }

          p {
            font-size: 15px;
            color: #718096;
            line-height: 1.7;
            margin: 0;
          }
        }
      }
    }

    // 公告区域样式
    .announcement-section {
      margin: 60px 0;

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
    }

    // 用户欢迎区域样式
    .user-welcome-section {
      margin: 40px 0;

      .welcome-content {
        background: #ffffff;
        border-radius: 16px;
        padding: 40px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 40px;

        @media (max-width: 768px) {
          flex-direction: column;
          gap: 24px;
          padding: 32px;
        }

        .welcome-text {
          flex: 1;

          h2 {
            font-size: 32px;
            font-weight: 700;
            color: #1a202c;
            margin: 0 0 12px 0;
            line-height: 1.2;
          }

          p {
            font-size: 16px;
            color: #718096;
            margin: 0;
            line-height: 1.6;
          }
        }

        .user-stats {
          display: flex;
          gap: 40px;
          align-items: center;

          @media (max-width: 768px) {
            gap: 24px;
          }

          .stat-item {
            text-align: center;

            .stat-value {
              font-size: 32px;
              font-weight: 700;
              color: #667eea;
              margin-bottom: 8px;
              line-height: 1;
            }

            .stat-label {
              font-size: 14px;
              color: #718096;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}
</style>