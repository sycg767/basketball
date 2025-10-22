<template>
  <div class="announcement-section">
    <div class="section-header">
      <h3 class="section-title">
        <el-icon color="#f56c6c"><Bell /></el-icon>
        系统公告
        <el-tag type="info" size="small" v-if="announcements.length > 0">
          {{ announcements.length }} 条
        </el-tag>
      </h3>
      <el-button type="primary" link size="small" @click="viewAllAnnouncements">
        查看全部
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="announcement-list">
      <div
        v-if="announcements.length === 0"
        class="empty-announcements"
      >
        <el-empty description="暂无公告" :image-size="80">
          <template #description>
            <span class="empty-text">暂无系统公告</span>
          </template>
        </el-empty>
      </div>

      <div
        v-else
        class="announcement-item"
        v-for="announcement in announcements"
        :key="announcement.id"
        @click="viewAnnouncement(announcement)"
      >
        <div class="announcement-content">
          <div class="announcement-type">
            <el-tag
              :type="getTypeTag(announcement.type)"
              size="small"
              effect="plain"
            >
              {{ getTypeText(announcement.type) }}
            </el-tag>
          </div>

          <h4 class="announcement-title" :title="announcement.title">
            {{ announcement.title }}
          </h4>

          <p class="announcement-desc" :title="announcement.content">
            {{ truncateContent(announcement.content) }}
          </p>

          <div class="announcement-meta">
            <span class="publish-time">
              <el-icon><Clock /></el-icon>
              {{ formatTime(announcement.publishTime) }}
            </span>
            <span class="view-count">
              <el-icon><View /></el-icon>
              {{ announcement.viewCount || 0 }} 阅读
            </span>
          </div>
        </div>

        <div class="announcement-actions">
          <el-button
            type="primary"
            link
            size="small"
            @click.stop="markAsRead(announcement)"
            v-if="!announcement.isRead"
          >
            标记已读
          </el-button>
        </div>
      </div>
    </div>

    <!-- 查看公告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedAnnouncement?.title"
      width="600px"
      destroy-on-close
    >
      <div v-if="selectedAnnouncement" class="announcement-detail">
        <div class="announcement-header">
          <div class="announcement-type-tag">
            <el-tag :type="getTypeTag(selectedAnnouncement.type)" size="small">
              {{ getTypeText(selectedAnnouncement.type) }}
            </el-tag>
          </div>
          <div class="announcement-time">
            <span>发布时间：{{ formatTime(selectedAnnouncement.publishTime) }}</span>
          </div>
        </div>

        <div class="announcement-content-detail">
          <div class="content-text" v-html="formatContent(selectedAnnouncement.content)"></div>
        </div>

        <div class="announcement-footer">
          <span class="view-count">阅读量：{{ selectedAnnouncement.viewCount || 0 }}</span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            @click="markAsRead(selectedAnnouncement); dialogVisible = false"
            v-if="!selectedAnnouncement?.isRead"
          >
            标记已读
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, ArrowRight, Clock, View } from '@element-plus/icons-vue'
import { request } from '@/utils/request.js'

const router = useRouter()

// 响应式数据
const announcements = ref([])
const dialogVisible = ref(false)
const selectedAnnouncement = ref(null)
const loading = ref(false)
const refreshTimer = ref(null)

// 公告类型映射
const typeMap = {
  1: { text: '系统通知', tag: 'danger' },
  2: { text: '维护通知', tag: 'warning' },
  3: { text: '节日通知', tag: 'success' },
  4: { text: '活动通知', tag: 'primary' }
}

// 获取公告列表
const getAnnouncements = async () => {
  try {
    loading.value = true
    const response = await request.get('/api/announcement/page', {
      params: {
        current: 1,
        size: 5,
        status: 1 // 只获取启用的公告
      }
    })

    if (response.code === 200) {
      // 获取已读状态
      const announcementIds = response.data.records.map(a => a.id)
      const readStatusMap = await getReadStatus(announcementIds)

      announcements.value = response.data.records.map(announcement => ({
        ...announcement,
        isRead: readStatusMap[announcement.id] || false
      }))
    }
  } catch (error) {
    console.error('获取公告失败:', error)
    ElMessage.error('获取公告失败')
  } finally {
    loading.value = false
  }
}

// 获取公告已读状态
const getReadStatus = async (announcementIds) => {
  try {
    const response = await request.post('/api/announcement/read-status', {
      announcementIds
    })
    if (response.code === 200) {
      // 返回格式: { announcementId: isRead }
      return response.data || {}
    }
  } catch (error) {
    console.error('获取已读状态失败:', error)
  }
  return {}
}

// 获取公告类型标签类型
const getTypeTag = (type) => {
  return typeMap[type]?.tag || 'info'
}

// 获取公告类型文本
const getTypeText = (type) => {
  return typeMap[type]?.text || '其他'
}

// 截断公告内容
const truncateContent = (content, maxLength = 60) => {
  if (!content) return ''
  return content.length > maxLength ? content.substring(0, maxLength) + '...' : content
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date

  // 1小时内显示"刚刚"
  if (diff < 60 * 60 * 1000) {
    return '刚刚'
  }
  // 24小时内显示"X小时前"
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }
  // 7天内显示"X天前"
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  }
  // 超过7天显示具体日期
  return date.toLocaleDateString()
}

// 格式化公告内容（支持换行）
const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 查看公告详情
const viewAnnouncement = (announcement) => {
  selectedAnnouncement.value = announcement
  dialogVisible.value = true

  // 增加阅读量
  if (announcement.viewCount !== undefined) {
    announcement.viewCount++
  }
}

// 标记公告为已读
const markAsRead = async (announcement) => {
  try {
    const response = await request.post(`/api/announcement/read/${announcement.id}`)
    if (response.code === 200) {
      announcement.isRead = true
      ElMessage.success('已标记为已读')
      // 刷新公告列表以更新已读状态
      await getAnnouncements()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

// 查看全部公告
const viewAllAnnouncements = () => {
  router.push('/announcement/list')
}

// 自动刷新公告
const startAutoRefresh = () => {
  refreshTimer.value = setInterval(() => {
    getAnnouncements()
  }, 5 * 60 * 1000) // 每5分钟刷新一次
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
}

// 生命周期钩子
onMounted(() => {
  getAnnouncements()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.announcement-section {
  margin-bottom: 32px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 20px;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
      letter-spacing: -0.02em;
    }

    :deep(.el-button) {
      font-weight: 500;
      transition: all $duration-fast $ease-in-out;

      &:hover {
        transform: translateX(2px);
      }
    }
  }

  .announcement-list {
    .empty-announcements {
      padding: 60px 20px;
      text-align: center;
      background: $white;
      border-radius: $radius-lg;
      border: 1px solid $border-color;

      .empty-text {
        color: $text-tertiary;
        font-size: 14px;
      }
    }

    .announcement-item {
      background: $white;
      border-radius: $radius-lg;
      padding: 24px;
      margin-bottom: 16px;
      border: 1px solid $border-color;
      cursor: pointer;
      transition: all $duration-base $ease-in-out;
      box-shadow: $shadow-sm;

      &:hover {
        box-shadow: $shadow-md;
        transform: translateY(-2px);
        border-color: $primary;
      }

      .announcement-content {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .announcement-title {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
          margin: 0 0 8px 0;
          line-height: 1.4;
          letter-spacing: -0.01em;

          &:hover {
            color: $primary;
          }
        }

        .announcement-desc {
          font-size: 14px;
          color: $text-secondary;
          line-height: 1.6;
          margin: 0;
        }

        .announcement-meta {
          display: flex;
          gap: 20px;
          align-items: center;
          margin-top: 8px;

          .publish-time,
          .view-count {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: $text-tertiary;
            font-weight: 500;
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

.announcement-detail {
  .announcement-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid $border-color;

    .announcement-time {
      font-size: 14px;
      color: $text-secondary;
      font-weight: 500;
    }
  }

  .announcement-content-detail {
    margin-bottom: 24px;
    color: $text-primary;
    line-height: 1.8;

    .content-text {
      font-size: 15px;
      line-height: 1.6;
      color: #4a5568;
      white-space: pre-wrap;
    }
  }

  .announcement-footer {
    display: flex;
    justify-content: flex-end;
    padding-top: 16px;
    border-top: 1px solid #e2e8f0;

    .view-count {
      font-size: 14px;
      color: #718096;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>