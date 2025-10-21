<template>
  <div class="points-container">
    <!-- 页面头部 -->
    <el-page-header @back="handleBack" content="我的积分" />

    <!-- 积分展示卡片 - Apple风格 -->
    <div class="points-showcase">
      <div class="points-card-main">
        <div class="points-background">
          <div class="bg-circle circle-1"></div>
          <div class="bg-circle circle-2"></div>
          <div class="bg-circle circle-3"></div>
        </div>

        <div class="points-content">
          <div class="points-header">
            <div class="points-icon">
              <el-icon><TrophyBase /></el-icon>
            </div>
            <el-button type="primary" @click="handleRefresh" class="refresh-btn">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>

          <div class="points-main">
            <div class="points-label">我的积分</div>
            <div class="points-value">{{ myPoints.toLocaleString() }}</div>
            <div class="points-subtitle">Points Balance</div>
          </div>
        </div>
      </div>

      <!-- 积分说明卡片 -->
      <div class="points-tips-card">
        <h3 class="tips-title">积分规则</h3>
        <div class="tips-list">
          <div class="tip-item">
            <div class="tip-icon">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="tip-content">
              <div class="tip-title">消费赚积分</div>
              <div class="tip-desc">每消费1元获得1积分</div>
            </div>
          </div>
          <div class="tip-item">
            <div class="tip-icon">
              <el-icon><Present /></el-icon>
            </div>
            <div class="tip-content">
              <div class="tip-title">积分可兑换</div>
              <div class="tip-desc">兑换礼品或抵扣消费</div>
            </div>
          </div>
          <div class="tip-item">
            <div class="tip-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="tip-content">
              <div class="tip-title">有效期1年</div>
              <div class="tip-desc">请及时使用积分</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 积分明细 -->
    <div class="records-section">
      <div class="section-header">
        <h2 class="section-title">积分明细</h2>
        <el-radio-group v-model="filterType" size="default" @change="handleFilterChange" class="filter-tabs">
          <el-radio-button :label="0">全部</el-radio-button>
          <el-radio-button :label="1">获得</el-radio-button>
          <el-radio-button :label="2">消费</el-radio-button>
        </el-radio-group>
      </div>

      <div class="records-list" v-if="recordList.length > 0">
        <div v-for="record in recordList" :key="record.id" class="record-item">
          <div class="record-icon" :class="record.points > 0 ? 'icon-gain' : 'icon-cost'">
            <el-icon v-if="record.points > 0"><Plus /></el-icon>
            <el-icon v-else><Minus /></el-icon>
          </div>

          <div class="record-info">
            <div class="record-main">
              <span class="record-type">{{ getTypeText(record.type) }}</span>
              <span class="record-desc">{{ record.remark }}</span>
            </div>
            <div class="record-meta">
              <span class="record-time">{{ formatDate(record.createTime) }}</span>
              <span class="record-expire" :class="getExpireClass(record.expireDate)">
                {{ record.expireDate ? `有效期至 ${formatDate(record.expireDate)}` : '' }}
              </span>
            </div>
          </div>

          <div class="record-amount">
            <div class="amount-change" :class="record.points > 0 ? 'text-success' : 'text-danger'">
              {{ record.points > 0 ? '+' : '' }}{{ record.points }}
            </div>
            <div class="amount-balance">余额 {{ record.pointsAfter }}</div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无积分记录" class="empty-records" />

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { TrophyBase, Refresh, Coin, Present, Clock, Plus, Minus } from '@element-plus/icons-vue'
import { getMyPoints, getPointsRecords } from '@/api/member'

const router = useRouter()

// 返回上一页
const handleBack = () => {
  router.back()
}

// 我的积分
const myPoints = ref(0)

// 筛选类型
const filterType = ref(0)

// 积分记录列表
const recordList = ref([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 获取我的积分
const fetchMyPoints = async () => {
  try {
    const res = await getMyPoints()
    if (res.code === 200) {
      myPoints.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取积分失败')
  }
}

// 获取积分记录
const fetchPointsRecords = async () => {
  try {
    const res = await getPointsRecords(queryParams)
    if (res.code === 200) {
      // 根据筛选类型过滤数据
      let records = res.data.records
      if (filterType.value === 1) {
        records = records.filter(record => record.points > 0)
      } else if (filterType.value === 2) {
        records = records.filter(record => record.points < 0)
      }
      recordList.value = records
      total.value = records.length
    }
  } catch (error) {
    ElMessage.error('获取积分记录失败')
  }
}

// 刷新
const handleRefresh = () => {
  fetchMyPoints()
  fetchPointsRecords()
  ElMessage.success('刷新成功')
}

// 筛选类型变化
const handleFilterChange = () => {
  queryParams.page = 1
  fetchPointsRecords()
}

// 分页
const handleSizeChange = () => {
  queryParams.page = 1
  fetchPointsRecords()
}

const handlePageChange = () => {
  fetchPointsRecords()
}

// 辅助方法
const getTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'primary'
  }
  return tagMap[type] || 'info'
}

const getTypeText = (type) => {
  const textMap = {
    1: '消费赠送',
    2: '积分兑换',
    3: '积分过期',
    4: '手动调整'
  }
  return textMap[type] || '未知'
}

const getExpireClass = (expireDate) => {
  if (!expireDate) return ''

  const now = new Date()
  const expire = new Date(expireDate)
  const diffDays = Math.floor((expire - now) / (1000 * 60 * 60 * 24))

  if (diffDays < 0) {
    return 'text-expired'
  } else if (diffDays < 30) {
    return 'text-warning'
  }
  return ''
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

// 生命周期
onMounted(() => {
  fetchMyPoints()
  fetchPointsRecords()
})
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.points-container {
  min-height: 100vh;
  background: $bg-secondary;
  padding: $spacing-6;
  max-width: 1400px;
  margin: 0 auto;

  // 页面头部
  :deep(.el-page-header) {
    margin-bottom: $spacing-6;
    background: $white;
    padding: $spacing-4;
    border-radius: $radius-lg;
    box-shadow: $shadow-sm;
  }

  // 积分展示区域
  .points-showcase {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: $spacing-6;
    margin-bottom: $spacing-8;

    @media (max-width: 968px) {
      grid-template-columns: 1fr;
    }
  }

  // 主积分卡片 - Apple Wallet风格
  .points-card-main {
    position: relative;
    height: 280px;
    border-radius: $radius-xl;
    padding: $spacing-8;
    background: linear-gradient(135deg, #FF9500 0%, #FF6B00 100%);
    overflow: hidden;
    box-shadow: $shadow-lg;
    transition: $transition-base;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-xl;
    }

    .points-background {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      opacity: 0.3;

      .bg-circle {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.15);
        backdrop-filter: $backdrop-blur-sm;

        &.circle-1 {
          width: 220px;
          height: 220px;
          top: -90px;
          right: -70px;
        }

        &.circle-2 {
          width: 160px;
          height: 160px;
          bottom: -60px;
          left: -50px;
        }

        &.circle-3 {
          width: 120px;
          height: 120px;
          top: 35%;
          right: 12%;
          opacity: 0.6;
        }
      }
    }

    .points-content {
      position: relative;
      z-index: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      color: $white;
    }

    .points-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-10;

      .points-icon {
        width: 60px;
        height: 60px;
        border-radius: $radius-lg;
        background: rgba(255, 255, 255, 0.25);
        backdrop-filter: $backdrop-blur;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

        .el-icon {
          font-size: 32px;
          color: $white;
        }
      }

      .refresh-btn {
        background: rgba(255, 255, 255, 0.25);
        border: 1px solid rgba(255, 255, 255, 0.4);
        color: $white;
        backdrop-filter: $backdrop-blur;
        border-radius: $radius-md;
        transition: $transition-fast;

        &:hover {
          background: rgba(255, 255, 255, 0.35);
          transform: scale(1.05);
        }
      }
    }

    .points-main {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .points-label {
        font-size: $font-size-lg;
        color: rgba(255, 255, 255, 0.95);
        margin-bottom: $spacing-3;
        font-weight: $font-weight-semibold;
        letter-spacing: $letter-spacing-tight;
      }

      .points-value {
        font-size: 64px;
        font-weight: $font-weight-bold;
        color: $white;
        line-height: 1;
        margin-bottom: $spacing-2;
        text-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
        letter-spacing: -0.03em;
      }

      .points-subtitle {
        font-size: $font-size-sm;
        color: rgba(255, 255, 255, 0.8);
        text-transform: uppercase;
        letter-spacing: 1.5px;
        font-weight: $font-weight-medium;
      }
    }
  }

  // 积分说明卡片 - iOS风格
  .points-tips-card {
    background: $white;
    border-radius: $radius-lg;
    padding: $spacing-6;
    box-shadow: $shadow-sm;
    border: 1px solid $border-color;
    transition: $transition-base;

    &:hover {
      box-shadow: $shadow-md;
    }

    .tips-title {
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin: 0 0 $spacing-5 0;
      letter-spacing: $letter-spacing-tight;
    }

    .tips-list {
      display: flex;
      flex-direction: column;
      gap: $spacing-4;
    }

    .tip-item {
      display: flex;
      align-items: flex-start;
      gap: $spacing-3;
      padding: $spacing-3;
      border-radius: $radius-md;
      transition: $transition-fast;

      &:hover {
        background: $bg-secondary;
      }

      .tip-icon {
        width: 44px;
        height: 44px;
        border-radius: $radius-md;
        background: rgba($warning, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        .el-icon {
          font-size: 22px;
          color: $warning;
        }
      }

      .tip-content {
        flex: 1;

        .tip-title {
          font-size: $font-size-base;
          font-weight: $font-weight-semibold;
          color: $text-primary;
          margin-bottom: $spacing-1;
          letter-spacing: $letter-spacing-tight;
        }

        .tip-desc {
          font-size: $font-size-sm;
          color: $text-secondary;
          line-height: $line-height-normal;
        }
      }
    }
  }

  // 积分明细区域 - iOS风格
  .records-section {
    background: $white;
    border-radius: $radius-lg;
    padding: $spacing-6;
    box-shadow: $shadow-sm;
    border: 1px solid $border-color;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-6;
      flex-wrap: wrap;
      gap: $spacing-4;

      .section-title {
        font-size: $font-size-xl;
        font-weight: $font-weight-semibold;
        color: $text-primary;
        margin: 0;
        letter-spacing: $letter-spacing-tight;
      }

      .filter-tabs {
        :deep(.el-radio-button__inner) {
          border-radius: $radius-sm;
          padding: $spacing-2 $spacing-5;
          border: 1px solid $border-color;
          transition: $transition-fast;
          font-weight: $font-weight-medium;
          font-size: $font-size-sm;

          &:hover {
            color: $primary;
            border-color: $primary;
            background: rgba($primary, 0.05);
          }
        }

        :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
          background: $primary;
          border-color: $primary;
          color: $white;
          box-shadow: 0 2px 8px rgba($primary, 0.25);
        }
      }
    }

    .records-list {
      display: flex;
      flex-direction: column;
      gap: $spacing-3;
    }

    .record-item {
      display: flex;
      align-items: center;
      gap: $spacing-4;
      padding: $spacing-4;
      background: $bg-secondary;
      border-radius: $radius-md;
      transition: $transition-fast;
      border: 1px solid transparent;

      &:hover {
        background: $white;
        border-color: $border-color;
        box-shadow: $shadow-sm;
        transform: translateX(2px);
      }

      .record-icon {
        width: 48px;
        height: 48px;
        border-radius: $radius-md;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        .el-icon {
          font-size: 22px;
        }

        &.icon-gain {
          background: rgba($success, 0.12);
          color: $success;
        }

        &.icon-cost {
          background: rgba($error, 0.12);
          color: $error;
        }
      }

      .record-info {
        flex: 1;
        min-width: 0;

        .record-main {
          display: flex;
          align-items: center;
          gap: $spacing-3;
          margin-bottom: $spacing-1;

          .record-type {
            font-size: $font-size-base;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            letter-spacing: $letter-spacing-tight;
          }

          .record-desc {
            font-size: $font-size-sm;
            color: $text-secondary;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .record-meta {
          display: flex;
          align-items: center;
          gap: $spacing-4;
          font-size: $font-size-xs;
          color: $text-tertiary;

          .record-expire {
            &.text-warning {
              color: $warning;
              font-weight: $font-weight-medium;
            }

            &.text-expired {
              color: $error;
              text-decoration: line-through;
            }
          }
        }
      }

      .record-amount {
        text-align: right;
        flex-shrink: 0;

        .amount-change {
          font-size: $font-size-xl;
          font-weight: $font-weight-bold;
          margin-bottom: $spacing-1;
          letter-spacing: $letter-spacing-tight;

          &.text-success {
            color: $success;
          }

          &.text-danger {
            color: $error;
          }
        }

        .amount-balance {
          font-size: $font-size-xs;
          color: $text-tertiary;
          font-weight: $font-weight-medium;
        }
      }
    }

    .empty-records {
      padding: $spacing-12 $spacing-5;
    }

    .pagination {
      display: flex;
      justify-content: center;
      margin-top: $spacing-6;
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    padding: $spacing-4;

    :deep(.el-page-header) {
      padding: $spacing-3;
    }

    .points-showcase {
      gap: $spacing-4;
      margin-bottom: $spacing-6;
    }

    .points-card-main {
      height: 240px;
      padding: $spacing-6;

      .points-header {
        margin-bottom: $spacing-6;

        .points-icon {
          width: 52px;
          height: 52px;

          .el-icon {
            font-size: 28px;
          }
        }
      }

      .points-main .points-value {
        font-size: 48px;
      }
    }

    .points-tips-card {
      padding: $spacing-4;

      .tips-title {
        font-size: $font-size-lg;
        margin-bottom: $spacing-4;
      }

      .tip-item {
        padding: $spacing-2;

        .tip-icon {
          width: 40px;
          height: 40px;

          .el-icon {
            font-size: 20px;
          }
        }
      }
    }

    .records-section {
      padding: $spacing-4;

      .section-header {
        gap: $spacing-3;

        .section-title {
          font-size: $font-size-lg;
        }
      }

      .record-item {
        flex-wrap: wrap;
        padding: $spacing-3;

        .record-icon {
          width: 44px;
          height: 44px;

          .el-icon {
            font-size: 20px;
          }
        }

        .record-amount {
          width: 100%;
          text-align: left;
          margin-top: $spacing-2;
          padding-top: $spacing-2;
          border-top: 1px solid $border-color;

          .amount-change {
            font-size: $font-size-lg;
          }
        }
      }
    }
  }
}
</style>
