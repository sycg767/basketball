<template>
  <div class="log-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="登录日志" name="login" />
      <el-tab-pane label="操作日志" name="operation" />
    </el-tabs>

    <!-- 登录日志 -->
    <el-card v-if="activeTab === 'login'" class="content-card">
      <template #header>
        <div class="header-content">
          <h3>登录日志管理</h3>
          <el-button type="primary" @click="exportLogs">导出日志</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>

        <el-form-item label="登录状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="登录时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchLogs">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="loginLogList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="loginType" label="登录类型" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ getLoginTypeText(row.loginType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="location" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示信息" width="150" />
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewLogDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 操作日志 -->
    <el-card v-if="activeTab === 'operation'" class="content-card">
      <template #header>
        <div class="header-content">
          <h3>操作日志管理</h3>
          <el-button type="primary" @click="exportLogs">导出日志</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>

        <el-form-item label="操作类型">
          <el-input v-model="searchForm.operation" placeholder="请输入操作类型" clearable />
        </el-form-item>

        <el-form-item label="操作状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchLogs">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="operationLogList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="operation" label="操作类型" width="150" />
        <el-table-column prop="method" label="请求方法" width="120" />
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="location" label="操作地点" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="执行时间(ms)" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewLogDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 日志统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">成功登录</div>
              <div class="stat-value">{{ loginStats.successfulLogins }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">失败登录</div>
              <div class="stat-value">{{ loginStats.failedLogins }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">成功操作</div>
              <div class="stat-value">{{ operationStats.successfulOperations }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">失败操作</div>
              <div class="stat-value">{{ operationStats.failedOperations }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="detailDialogTitle"
      width="800px"
    >
      <div v-if="logDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ logDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ logDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ logDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ logDetail.ip }}</el-descriptions-item>
          <el-descriptions-item label="操作地点">{{ logDetail.location }}</el-descriptions-item>
          <el-descriptions-item label="浏览器">{{ logDetail.browser }}</el-descriptions-item>
          <el-descriptions-item label="操作系统">{{ logDetail.os }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="logDetail.status === 1 ? 'success' : 'danger'">
              {{ logDetail.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(logDetail.createTime) }}</el-descriptions-item>

          <el-descriptions-item label="操作类型" v-if="activeTab === 'operation'">{{ logDetail.operation }}</el-descriptions-item>
          <el-descriptions-item label="请求方法" v-if="activeTab === 'operation'">{{ logDetail.method }}</el-descriptions-item>
          <el-descriptions-item label="执行时间" v-if="activeTab === 'operation'">{{ logDetail.executeTime }}ms</el-descriptions-item>
          <el-descriptions-item label="登录类型" v-if="activeTab === 'login'">{{ getLoginTypeText(logDetail.loginType) }}</el-descriptions-item>
          <el-descriptions-item label="提示信息">{{ logDetail.message }}</el-descriptions-item>

          <el-descriptions-item label="参数" v-if="logDetail.params" :span="2">
            <pre class="log-content">{{ logDetail.params }}</pre>
          </el-descriptions-item>

          <el-descriptions-item label="返回结果" v-if="logDetail.result" :span="2">
            <pre class="log-content">{{ logDetail.result }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { request } from '@/utils/request.js'

const activeTab = ref('login')
const loading = ref(false)
const detailDialogVisible = ref(false)
const detailDialogTitle = ref('日志详情')

const searchForm = reactive({
  userId: '',
  username: '',
  status: '',
  operation: '',
  dateRange: []
})

const loginLogList = ref([])
const operationLogList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const logDetail = ref(null)

const loginStats = reactive({
  successfulLogins: 0,
  failedLogins: 0
})

const operationStats = reactive({
  successfulOperations: 0,
  failedOperations: 0
})

const currentLogList = computed(() => {
  return activeTab.value === 'login' ? loginLogList.value : operationLogList.value
})

const getLogs = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }

    // 根据当前标签页获取对应的日志
    const endpoint = activeTab.value === 'login' ? '/api/login-log/page' : '/api/operation-log/page'
    const response = await request.get(endpoint, { params })

    if (response.code === 200) {
      const data = response.data
      if (activeTab.value === 'login') {
        loginLogList.value = data.records
      } else {
        operationLogList.value = data.records
      }
      total.value = data.total
    }
  } catch (error) {
    ElMessage.error('获取日志列表失败')
  }
  loading.value = false
}

const getLogStatistics = async () => {
  try {
    // 获取登录统计
    const loginResponse = await request.get('/api/login-log/statistics')
    if (loginResponse.code === 200) {
      loginStats.successfulLogins = loginResponse.data.successfulLogins
      loginStats.failedLogins = loginResponse.data.failedLogins
    }

    // 获取操作统计
    const operationResponse = await request.get('/api/operation-log/statistics')
    if (operationResponse.code === 200) {
      operationStats.successfulOperations = operationResponse.data.successfulOperations
      operationStats.failedOperations = operationResponse.data.failedOperations
    }
  } catch (error) {
    console.error('获取日志统计失败:', error)
  }
}

const handleTabClick = () => {
  currentPage.value = 1
  getLogs()
}

const searchLogs = () => {
  currentPage.value = 1
  getLogs()
}

const resetSearch = () => {
  searchForm.userId = ''
  searchForm.username = ''
  searchForm.status = ''
  searchForm.operation = ''
  searchForm.dateRange = []
  searchLogs()
}

const viewLogDetail = async (row) => {
  try {
    const endpoint = activeTab.value === 'login' ? '/api/login-log' : '/api/operation-log'
    const response = await request.get(`${endpoint}/${row.id}`)
    if (response.code === 200) {
      logDetail.value = response.data
      detailDialogTitle.value = activeTab.value === 'login' ? '登录日志详情' : '操作日志详情'
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取日志详情失败')
  }
}

const exportLogs = () => {
  // 这里可以实现导出功能
  ElMessage.success('导出功能开发中...')
}

const getLoginTypeText = (type) => {
  const map = {
    1: '账号密码',
    2: '手机验证码',
    3: '第三方登录'
  }
  return map[type] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getLogs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getLogs()
}

onMounted(() => {
  getLogs()
  getLogStatistics()
})
</script>

<style scoped lang="scss">
.log-container {
  padding: 20px;
}

.content-card {
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .stat-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: #fff;

      &.success {
        background: linear-gradient(135deg, #67c23a, #85ce61);
      }

      &.danger {
        background: linear-gradient(135deg, #f56c6c, #f78989);
      }
    }

    .stat-info {
      .stat-title {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 20px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
}

.log-content {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

:deep(.el-card) {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #e6e6e6;
}
</style>