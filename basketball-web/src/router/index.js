import { createRouter, createWebHistory } from 'vue-router';
import { getToken } from '@/utils/auth';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '登录', requireAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '注册', requireAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', requireAuth: true }
  },
  {
    path: '/venue',
    name: 'VenueList',
    component: () => import('@/views/venue/VenueList.vue'),
    meta: { title: '场地列表' }
  },
  {
    path: '/venue/:id',
    name: 'VenueDetail',
    component: () => import('@/views/venue/VenueDetail.vue'),
    meta: { title: '场地详情' }
  },
  // 预订相关路由
  {
    path: '/booking',
    name: 'BookingList',
    component: () => import('@/views/booking/List.vue'),
    meta: { title: '我的预订', requireAuth: true }
  },
  {
    path: '/booking/detail/:id',
    name: 'BookingDetail',
    component: () => import('@/views/booking/Detail.vue'),
    meta: { title: '预订详情', requireAuth: true }
  },
  {
    path: '/booking/create/:venueId',
    name: 'BookingCreate',
    component: () => import('@/views/booking/Create.vue'),
    meta: { title: '创建预订', requireAuth: true }
  },
  // 课程相关路由
  {
    path: '/course',
    name: 'CourseList',
    component: () => import('@/views/course/CourseList.vue'),
    meta: { title: '课程列表' }
  },
  {
    path: '/my-courses',
    name: 'MyCourses',
    component: () => import('@/views/course/MyCourses.vue'),
    meta: { title: '我的课程', requireAuth: true }
  },
  {
    path: '/course/:id',
    name: 'CourseDetail',
    component: () => import('@/views/course/CourseDetail.vue'),
    meta: { title: '课程详情' }
  },
  // 会员管理路由
  {
    path: '/member/card',
    name: 'MemberCard',
    component: () => import('@/views/member/MemberCard.vue'),
    meta: { title: '我的会员卡', requireAuth: true }
  },
  {
    path: '/member/card-purchase',
    name: 'MemberCardPurchase',
    component: () => import('@/views/member/MemberCardPurchase.vue'),
    meta: { title: '购买会员卡', requireAuth: true }
  },
  {
    path: '/member/points',
    name: 'Points',
    component: () => import('@/views/member/Points.vue'),
    meta: { title: '我的积分', requireAuth: true }
  },
  // 支付相关路由
  {
    path: '/payment/method',
    name: 'PaymentMethod',
    component: () => import('@/views/payment/PaymentMethod.vue'),
    meta: { title: '选择支付方式', requireAuth: true }
  },
  {
    path: '/payment/wechat',
    name: 'WechatPay',
    component: () => import('@/views/payment/WechatPay.vue'),
    meta: { title: '微信支付', requireAuth: true }
  },
  {
    path: '/payment/alipay',
    name: 'AlipayPage',
    component: () => import('@/views/payment/AlipayPage.vue'),
    meta: { title: '支付宝支付', requireAuth: true }
  },
  {
    path: '/payment/result',
    name: 'PaymentResult',
    component: () => import('@/views/payment/PaymentResult.vue'),
    meta: { title: '支付结果', requireAuth: true }
  },
  // 通知相关路由
  {
    path: '/notification/list',
    name: 'NotificationList',
    component: () => import('@/views/notification/NotificationList.vue'),
    meta: { title: '通知中心', requireAuth: true }
  },
  {
    path: '/notification/:id',
    name: 'NotificationDetail',
    component: () => import('@/views/notification/NotificationDetail.vue'),
    meta: { title: '通知详情', requireAuth: true }
  },
  // 管理员路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录', requireAuth: false }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { title: '管理后台', requireAuth: true, requireAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据统计', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'venues',
        name: 'AdminVenues',
        component: () => import('@/views/admin/VenueManage.vue'),
        meta: { title: '场地管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'bookings',
        name: 'AdminBookings',
        component: () => import('@/views/admin/BookingManage.vue'),
        meta: { title: '预订管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('@/views/admin/CourseManage.vue'),
        meta: { title: '课程管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'member',
        name: 'AdminMember',
        component: () => import('@/views/admin/MemberManage.vue'),
        meta: { title: '会员管理', requireAuth: true, requireAdmin: true }
      },
      // 数据分析路由
      {
        path: 'analytics/member',
        name: 'MemberAnalysis',
        component: () => import('@/views/admin/analytics/MemberAnalysis.vue'),
        meta: { title: '会员活跃度分析', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'analytics/course',
        name: 'CourseAnalysis',
        component: () => import('@/views/admin/analytics/CourseAnalysis.vue'),
        meta: { title: '课程热度分析', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'analytics/venue',
        name: 'VenueAnalysis',
        component: () => import('@/views/admin/analytics/VenueAnalysis.vue'),
        meta: { title: '场地使用分析', requireAuth: true, requireAdmin: true }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '篮球场馆预约系统';

  // 判断是否需要登录
  if (to.meta.requireAuth) {
    const token = getToken();
    if (!token) {
      next('/login');
      return;
    }
  }

  next();
});

export default router;