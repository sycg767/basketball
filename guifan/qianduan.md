# 篮球场馆管理系统 - 前端开发规范

## 一、项目结构

```
basketball-web/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── auth.js       # 认证接口
│   │   ├── venue.js      # 场地接口
│   │   ├── booking.js    # 预订接口
│   │   └── admin.js      # 管理端接口
│   ├── assets/           # 资源文件
│   │   ├── images/       # 图片
│   │   └── styles/       # 样式
│   ├── components/       # 公共组件
│   │   ├── common/       # 通用组件
│   │   └── layout/       # 布局组件
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── store/            # Pinia状态管理
│   │   ├── modules/      # 模块
│   │   │   ├── user.js   # 用户状态
│   │   │   └── booking.js # 预订状态
│   │   └── index.js
│   ├── utils/            # 工具类
│   │   ├── request.js    # axios封装
│   │   ├── auth.js       # 认证工具
│   │   └── validate.js   # 验证规则
│   ├── views/            # 页面视图
│   │   ├── auth/         # 认证页面
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   ├── venue/        # 场地页面
│   │   │   ├── VenueList.vue
│   │   │   └── VenueDetail.vue
│   │   ├── booking/      # 预订页面
│   │   │   ├── BookingList.vue
│   │   │   ├── BookingDetail.vue
│   │   │   └── CreateBooking.vue
│   │   └── admin/        # 管理端页面
│   │       ├── venue/
│   │       ├── booking/
│   │       └── user/
│   ├── App.vue
│   └── main.js
├── package.json
└── vite.config.js
```

## 二、命名规范

### 1. 文件命名

| 类型 | 规则 | 示例 |
|------|------|------|
| 页面组件 | 大驼峰 | `VenueList.vue`, `BookingDetail.vue` |
| 公共组件 | 大驼峰 | `UserAvatar.vue`, `StatusTag.vue` |
| API文件 | 小驼峰 | `venue.js`, `booking.js` |
| Store模块 | 小驼峰 | `user.js`, `booking.js` |
| 工具类 | 小驼峰 | `request.js`, `validate.js` |

### 2. 变量命名

| 类型 | 规则 | 示例 |
|------|------|------|
| 响应式数据 | 小驼峰 | `venueList`, `bookingDetail` |
| 常量 | 大写下划线 | `BASE_URL`, `TOKEN_KEY` |
| 方法 | 小驼峰 | `getVenueList()`, `handleSubmit()` |
| 事件处理 | handle+动作 | `handleClick()`, `handleSearch()` |
| API方法 | 动词+名词 | `getVenueList()`, `createBooking()` |

## 三、API接口规范

### 1. 接口文件结构

```javascript
// src/api/venue.js
import request from '@/utils/request'

/**
 * 获取场地列表
 */
export function getVenueList(params) {
  return request({
    url: '/api/venue',
    method: 'get',
    params
  })
}

/**
 * 获取场地详情
 */
export function getVenueDetail(id) {
  return request({
    url: `/api/venue/${id}`,
    method: 'get'
  })
}

/**
 * 获取场地价格列表
 */
export function getVenuePrices(id) {
  return request({
    url: `/api/venue/prices/${id}`,
    method: 'get'
  })
}
```

### 2. API路径对照表

#### 用户端接口

```javascript
// 认证
POST   /api/auth/register          // 注册
POST   /api/auth/login             // 登录
GET    /api/auth/info              // 获取用户信息

// 场地
GET    /api/venue                  // 场地列表(分页)
GET    /api/venue/{id}             // 场地详情
GET    /api/venue/prices/{id}      // 价格列表

// 预订
POST   /api/booking                // 创建预订
GET    /api/booking/my             // 我的预订
GET    /api/booking/{id}           // 预订详情
PUT    /api/booking/{id}/pay       // 支付预订
PUT    /api/booking/{id}/cancel    // 取消预订
GET    /api/booking/check-available // 检查可用性
```

#### 管理端接口

```javascript
// 场地管理
POST   /api/admin/venue            // 创建场地
PUT    /api/admin/venue/{id}       // 更新场地
DELETE /api/admin/venue/{id}       // 删除场地
GET    /api/admin/venue            // 场地列表
POST   /api/admin/venue/price      // 设置价格

// 预订管理
GET    /api/admin/booking          // 预订列表
PUT    /api/admin/booking/{id}/cancel // 取消预订

// 用户管理
GET    /api/admin/user             // 用户列表
PUT    /api/admin/user/{id}/status // 更新用户状态
```

## 四、组件开发规范

### 1. 基础模板

```vue
<template>
  <div class="venue-list">
    <!-- 内容 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVenueList } from '@/api/venue'

// 响应式数据
const venueList = ref([])
const loading = ref(false)

// 方法
const loadVenues = async () => {
  loading.value = true
  try {
    const res = await getVenueList()
    venueList.value = res.data
  } catch (error) {
    console.error('加载场地列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadVenues()
})
</script>

<style scoped>
.venue-list {
  /* 样式 */
}
</style>
```

### 2. 表单组件规范

```vue
<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="100px"
  >
    <el-form-item label="场地名称" prop="venueName">
      <el-input v-model="formData.venueName" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
      <el-button @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref()
const formData = reactive({
  venueName: '',
  venueType: '',
  // ...
})

const formRules = {
  venueName: [
    { required: true, message: '请输入场地名称', trigger: 'blur' }
  ],
  venueType: [
    { required: true, message: '请选择场地类型', trigger: 'change' }
  ]
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    // API调用
    ElMessage.success('提交成功')
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>
```

### 3. 列表组件规范

```vue
<template>
  <div class="booking-list">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="状态">
        <el-select v-model="queryParams.status">
          <el-option label="全部" :value="null" />
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="id" label="预订ID" />
      <el-table-column prop="venueName" label="场地名称" />
      <el-table-column prop="bookingDate" label="预订日期" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link @click="handleView(row.id)">查看</el-button>
          <el-button link v-if="row.status === 0" @click="handlePay(row.id)">支付</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      @current-change="loadBookings"
      @size-change="loadBookings"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyBookingList } from '@/api/booking'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  status: null
})

const loadBookings = async () => {
  loading.value = true
  try {
    const res = await getMyBookingList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  loadBookings()
}

onMounted(() => {
  loadBookings()
})
</script>
```

## 五、状态码映射

### 1. 预订状态

```javascript
// 状态映射
export const bookingStatusMap = {
  0: '待支付',
  1: '已支付',
  2: '已取消',
  3: '已完成',
  4: '已退款',
  5: '超时取消'
}

// 状态标签类型
export const bookingStatusType = {
  0: 'warning',
  1: 'success',
  2: 'info',
  3: 'success',
  4: 'warning',
  5: 'danger'
}

// 使用示例
const getStatusText = (status) => bookingStatusMap[status] || '未知'
const getStatusType = (status) => bookingStatusType[status] || 'info'
```

### 2. 场地类型

```javascript
export const venueTypeMap = {
  1: '室内全场',
  2: '室内半场',
  3: '室外全场',
  4: '室外半场'
}
```

### 3. 时段类型

```javascript
export const timeTypeMap = {
  1: '工作日',
  2: '周末',
  3: '节假日'
}
```

### 4. 支付方式

```javascript
export const paymentMethodMap = {
  1: '在线支付',
  2: '余额支付',
  3: '会员卡',
  4: '现场支付'
}
```

### 5. 通用状态

```javascript
export const statusMap = {
  0: '禁用',
  1: '启用',
  2: '维护中'
}
```

## 六、请求封装

### 1. axios配置

```javascript
// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 统一响应格式: { code, message, data }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }

    return res
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权,请重新登录')
          // 跳转登录页
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
      }
    }
    return Promise.reject(error)
  }
)

export default service
```

### 2. Token管理

```javascript
// src/utils/auth.js
const TOKEN_KEY = 'basketball_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  return localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  return localStorage.removeItem(TOKEN_KEY)
}
```

## 七、路由配置

```javascript
// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/venue'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/venue',
    name: 'VenueList',
    component: () => import('@/views/venue/VenueList.vue')
  },
  {
    path: '/venue/:id',
    name: 'VenueDetail',
    component: () => import('@/views/venue/VenueDetail.vue')
  },
  {
    path: '/booking',
    name: 'BookingList',
    component: () => import('@/views/booking/BookingList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/booking/create',
    name: 'CreateBooking',
    component: () => import('@/views/booking/CreateBooking.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/booking/:id',
    name: 'BookingDetail',
    component: () => import('@/views/booking/BookingDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'venue',
        name: 'AdminVenue',
        component: () => import('@/views/admin/venue/VenueManage.vue')
      },
      {
        path: 'booking',
        name: 'AdminBooking',
        component: () => import('@/views/admin/booking/BookingManage.vue')
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/UserManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

## 八、Pinia状态管理

```javascript
// src/store/modules/user.js
import { defineStore } from 'pinia'
import { login, getUserInfo } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null
  }),

  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      setToken(res.data.token)
    },

    async getUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
    },

    logout() {
      this.token = ''
      this.userInfo = null
      removeToken()
    }
  }
})
```

## 九、表单验证规则

```javascript
// src/utils/validate.js

// 手机号验证
export const validatePhone = (rule, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneReg.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

// 密码验证
export const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码至少6位'))
  } else {
    callback()
  }
}

// 邮箱验证
export const validateEmail = (rule, value, callback) => {
  const emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (value && !emailReg.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}
```

## 十、日期时间格式化

```javascript
// src/utils/date.js
import dayjs from 'dayjs'

// 日期格式化
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  return date ? dayjs(date).format(format) : ''
}

// 日期时间格式化
export const formatDateTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return date ? dayjs(date).format(format) : ''
}

// 时间格式化
export const formatTime = (time, format = 'HH:mm') => {
  return time ? dayjs(time, 'HH:mm:ss').format(format) : ''
}

// 使用示例
import { formatDate, formatDateTime } from '@/utils/date'

const formattedDate = formatDate(booking.bookingDate)          // 2025-10-02
const formattedDateTime = formatDateTime(booking.createTime)   // 2025-10-02 14:30:00
```

## 十一、开发注意事项

### 1. 组件通信

```javascript
// Props传值
defineProps({
  venueId: {
    type: Number,
    required: true
  }
})

// Emits事件
const emit = defineEmits(['submit', 'cancel'])
emit('submit', formData)

// provide/inject
// 父组件
provide('venueId', venueId)
// 子组件
const venueId = inject('venueId')
```

### 2. 响应式数据

```javascript
import { ref, reactive, computed, watch } from 'vue'

// ref - 基本类型
const count = ref(0)
count.value++

// reactive - 对象类型
const state = reactive({
  name: '',
  age: 0
})

// computed - 计算属性
const fullName = computed(() => {
  return `${state.firstName} ${state.lastName}`
})

// watch - 监听
watch(() => state.name, (newVal, oldVal) => {
  console.log('name changed:', newVal)
})
```

### 3. 环境变量

```javascript
// .env.development
VITE_API_BASE_URL=http://localhost:8080

// .env.production
VITE_API_BASE_URL=https://api.basketball.com

// 使用
const baseURL = import.meta.env.VITE_API_BASE_URL
```

### 4. Element Plus按需引入

```javascript
// vite.config.js
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default {
  plugins: [
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ]
}
```

### 5. 错误处理

```javascript
try {
  const res = await createBooking(formData)
  ElMessage.success('预订成功')
  router.push('/booking')
} catch (error) {
  ElMessage.error(error.message || '预订失败')
}
```

## 十二、数据展示最佳实践

### 1. 金额显示

```javascript
// 保留两位小数
const formatPrice = (price) => {
  return price ? `¥${Number(price).toFixed(2)}` : '¥0.00'
}
```

### 2. 图片列表

```vue
<template>
  <el-image
    v-for="(img, index) in venue.imageList"
    :key="index"
    :src="img"
    fit="cover"
  />
</template>

<script setup>
// imageList从后端VO获取，已经是List<String>格式
const venue = ref({
  imageList: []  // ["url1", "url2", "url3"]
})
</script>
```

### 3. 空数据处理

```vue
<el-empty v-if="!tableData.length && !loading" description="暂无数据" />
```

### 4. 加载状态

```vue
<el-table :data="tableData" v-loading="loading">
  <!-- 表格内容 -->
</el-table>
```
