<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <el-icon :size="24" color="#409eff"><Basketball /></el-icon>
          <h2 v-show="!isCollapse">管理后台</h2>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :router="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>

          <el-sub-menu index="analytics">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>数据分析</span>
            </template>
            <el-menu-item index="/admin/analytics/member">会员活跃度</el-menu-item>
            <el-menu-item index="/admin/analytics/course">课程热度</el-menu-item>
            <el-menu-item index="/admin/analytics/venue">场地使用</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/venues">
            <el-icon><House /></el-icon>
            <template #title>场地管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/bookings">
            <el-icon><Calendar /></el-icon>
            <template #title>预订管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/courses">
            <el-icon><Reading /></el-icon>
            <template #title>课程管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/member">
            <el-icon><CreditCard /></el-icon>
            <template #title>会员管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/financial">
            <el-icon><Money /></el-icon>
            <template #title>财务管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>日志管理</template>
          </el-menu-item>

          <el-menu-item index="/admin/settings">
            <el-icon><Setting /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleCollapse">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32">{{ getUserInitial() }}</el-avatar>
                <span class="username">{{ userStore.username || '管理员' }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Basketball,
  DataAnalysis,
  TrendCharts,
  House,
  Calendar,
  Reading,
  User,
  CreditCard,
  Bell,
  Money,
  Document,
  Setting,
  Fold,
  Expand,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isCollapse = ref(false);

// 当前激活菜单
const activeMenu = computed(() => route.path);

// 面包屑
const breadcrumb = computed(() => {
  const titleMap = {
    '/admin/dashboard': '数据统计',
    '/admin/analytics/member': '会员活跃度分析',
    '/admin/analytics/course': '课程热度分析',
    '/admin/analytics/venue': '场地使用分析',
    '/admin/venues': '场地管理',
    '/admin/bookings': '预订管理',
    '/admin/courses': '课程管理',
    '/admin/users': '用户管理',
    '/admin/member': '会员管理',
    '/admin/announcements': '公告管理',
    '/admin/financial': '财务管理',
    '/admin/logs': '日志管理',
    '/admin/settings': '系统设置'
  };
  return titleMap[route.path];
});

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 获取用户名首字母
const getUserInitial = () => {
  const name = userStore.username || '管理员';
  return name.charAt(0).toUpperCase();
};

// 下拉菜单命令处理
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/admin/profile');
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout();
      ElMessage.success('已退出登录');
      router.push('/admin/login');
    }).catch(() => {});
  }
};
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;

  .el-container {
    height: 100%;
  }

  .sidebar {
    background-color: #304156;
    transition: width 0.3s;

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      background-color: #2b3a4b;

      h2 {
        color: #ffffff;
        font-size: 18px;
        font-weight: 600;
        margin: 0;
      }
    }

    .el-menu {
      border-right: none;
    }
  }

  .header {
    background-color: #ffffff;
    border-bottom: 1px solid #e6e6e6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;

      .collapse-icon {
        font-size: 20px;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }
    }

    .header-right {
      .user-dropdown {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;

        .username {
          font-size: 14px;
          color: #606266;
        }

        &:hover {
          color: #409eff;
        }
      }
    }
  }

  .main-content {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style>
