<template>
  <div class="points-container">
    <!-- 积分卡片 -->
    <el-card class="points-card">
      <div class="points-header">
        <div class="points-info">
          <div class="points-icon">
            <el-icon><TrophyBase /></el-icon>
          </div>
          <div class="points-detail">
            <div class="points-label">我的积分</div>
            <div class="points-value">{{ myPoints }}</div>
          </div>
        </div>
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <div class="points-tips">
        <el-alert
          title="积分说明"
          type="info"
          :closable="false"
        >
          <template #default>
            <ul>
              <li>消费满1元即可获得1积分</li>
              <li>积分可用于兑换礼品或抵扣消费</li>
              <li>积分有效期为1年,请及时使用</li>
            </ul>
          </template>
        </el-alert>
      </div>
    </el-card>

    <!-- 积分明细 -->
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>积分明细</span>
          <el-radio-group v-model="filterType" size="small" @change="handleFilterChange">
            <el-radio-button :label="0">全部</el-radio-button>
            <el-radio-button :label="1">获得</el-radio-button>
            <el-radio-button :label="2">消费</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="recordList" style="width: 100%">
        <el-table-column label="积分变动" width="120">
          <template #default="{ row }">
            <span :class="row.points > 0 ? 'text-success' : 'text-danger'">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="说明" />

        <el-table-column label="积分余额" width="120">
          <template #default="{ row }">
            <span class="points-balance">{{ row.pointsAfter }}</span>
          </template>
        </el-table-column>

        <el-table-column label="过期时间" width="120">
          <template #default="{ row }">
            <span :class="getExpireClass(row.expireDate)">
              {{ formatDate(row.expireDate) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>

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

      <el-empty v-else description="暂无积分记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { TrophyBase, Refresh } from '@element-plus/icons-vue'
import { getMyPoints, getPointsRecords } from '@/api/member'

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
.points-container {
  padding: 20px;

  .points-card {
    margin-bottom: 20px;

    .points-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .points-info {
        display: flex;
        align-items: center;
        gap: 20px;

        .points-icon {
          width: 80px;
          height: 80px;
          border-radius: 50%;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;

          .el-icon {
            font-size: 40px;
            color: #fff;
          }
        }

        .points-detail {
          .points-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 5px;
          }

          .points-value {
            font-size: 36px;
            font-weight: bold;
            color: #303133;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }
        }
      }
    }

    .points-tips {
      :deep(.el-alert__content) {
        ul {
          margin: 0;
          padding-left: 20px;

          li {
            margin-bottom: 5px;
            font-size: 13px;
            color: #606266;

            &:last-child {
              margin-bottom: 0;
            }
          }
        }
      }
    }
  }

  .records-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .text-success {
      color: #67c23a;
      font-weight: bold;
    }

    .text-danger {
      color: #f56c6c;
      font-weight: bold;
    }

    .text-warning {
      color: #e6a23c;
    }

    .text-expired {
      color: #f56c6c;
      text-decoration: line-through;
    }

    .points-balance {
      font-weight: 500;
      color: #409eff;
    }

    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}
</style>
