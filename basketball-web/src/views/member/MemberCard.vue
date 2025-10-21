<template>
  <div class="member-card-container">
    <!-- 页面头部 -->
    <BackButton text="返回" />

    <!-- 顶部操作栏 -->
    <div class="header-actions">
      <el-button type="primary" @click="handlePurchase" class="action-btn">
        <el-icon><Plus /></el-icon>
        购买会员卡
      </el-button>
      <el-button @click="handleViewRecords" class="action-btn">
        <el-icon><Document /></el-icon>
        使用记录
      </el-button>
    </div>

    <!-- 会员卡列表 - Apple Wallet风格 -->
    <div class="wallet-cards" v-if="cardList.length > 0">
      <div
        v-for="card in cardList"
        :key="card.id"
        class="wallet-card"
        :class="[getCardClass(card), `card-type-${card.cardType}`]"
        @click="handleViewDetail(card)"
      >
        <!-- 卡片背景装饰 -->
        <div class="card-decoration">
          <div class="decoration-circle circle-1"></div>
          <div class="decoration-circle circle-2"></div>
          <div class="decoration-circle circle-3"></div>
        </div>

        <!-- 卡片内容 -->
        <div class="card-content">
          <!-- 卡片头部 -->
          <div class="card-top">
            <div class="card-logo">
              <el-icon><CreditCard /></el-icon>
            </div>
            <el-tag :type="getStatusType(card.status)" class="status-tag">
              {{ getStatusText(card.status) }}
            </el-tag>
          </div>

          <!-- 卡片主体 -->
          <div class="card-main">
            <h3 class="card-name">{{ card.cardName }}</h3>
            <div class="card-no">{{ card.cardNo }}</div>

            <!-- 时间卡显示有效期 -->
            <div v-if="card.cardType === 1" class="card-value">
              <div class="value-item">
                <span class="value-label">有效期至</span>
                <span class="value-text">{{ formatDate(card.expireDate) }}</span>
              </div>
            </div>

            <!-- 次卡显示剩余次数 -->
            <div v-if="card.cardType === 2" class="card-value">
              <div class="value-item">
                <span class="value-label">剩余次数</span>
                <span class="value-text highlight">{{ card.remainingTimes }} 次</span>
              </div>
            </div>

            <!-- 储值卡显示余额 -->
            <div v-if="card.cardType === 3" class="card-value">
              <div class="value-item">
                <span class="value-label">卡内余额</span>
                <span class="value-text highlight">¥{{ card.balance }}</span>
              </div>
            </div>
          </div>

          <!-- 卡片底部操作 -->
          <div class="card-actions" @click.stop>
            <el-button
              v-if="card.cardType === 3 && card.status === 1"
              type="primary"
              size="small"
              @click="handleRecharge(card)"
              class="card-action-btn"
            >
              充值
            </el-button>
            <el-button
              size="small"
              @click="handleViewDetail(card)"
              class="card-action-btn"
            >
              详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="暂无会员卡">
        <el-button type="primary" @click="handlePurchase">立即购买会员卡</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      class="pagination"
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :page-sizes="[6, 12, 18, 24]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="会员卡充值"
      width="500px"
    >
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="支付方式" prop="payMethod">
          <el-radio-group v-model="rechargeForm.payMethod">
            <el-radio :label="1">在线支付</el-radio>
            <el-radio :label="2">余额支付</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmRecharge" :loading="rechargeLoading">
          确认充值
        </el-button>
      </template>
    </el-dialog>

    <!-- 使用记录对话框 -->
    <el-dialog
      v-model="recordDialogVisible"
      title="会员卡使用记录"
      width="800px"
    >
      <el-table :data="recordList" style="width: 100%">
        <el-table-column prop="cardName" label="会员卡" width="120" />
        <el-table-column prop="cardNo" label="卡号" width="180" />
        <el-table-column label="记录类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeText(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动金额" width="120">
          <template #default="{ row }">
            <span v-if="row.changeAmount" :class="row.changeAmount > 0 ? 'text-success' : 'text-danger'">
              {{ row.changeAmount > 0 ? '+' : '' }}¥{{ Math.abs(row.changeAmount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="变动次数" width="100">
          <template #default="{ row }">
            <span v-if="row.changeTimes" :class="row.changeTimes > 0 ? 'text-success' : 'text-danger'">
              {{ row.changeTimes > 0 ? '+' : '' }}{{ row.changeTimes }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      <el-pagination
        v-if="recordTotal > 0"
        class="pagination"
        v-model:current-page="recordQueryParams.page"
        v-model:page-size="recordQueryParams.pageSize"
        :total="recordTotal"
        layout="total, prev, pager, next"
        @current-change="handleRecordPageChange"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Document, CreditCard } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getMyCards, rechargeCard, getCardRecords } from '@/api/member'
import BackButton from '@/components/common/BackButton.vue'

const router = useRouter()

// 列表数据
const cardList = ref([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  pageSize: 6
})

// 充值对话框
const rechargeDialogVisible = ref(false)
const rechargeLoading = ref(false)
const currentCard = ref(null)
const rechargeForm = reactive({
  cardId: null,
  amount: null,
  payMethod: 1
})
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '充值金额必须大于0', trigger: 'blur' }
  ],
  payMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}
const rechargeFormRef = ref(null)

// 使用记录对话框
const recordDialogVisible = ref(false)
const recordList = ref([])
const recordTotal = ref(0)
const recordQueryParams = reactive({
  cardId: null,
  page: 1,
  pageSize: 10
})

// 获取会员卡列表
const fetchCardList = async () => {
  try {
    const res = await getMyCards(queryParams)
    if (res.code === 200) {
      cardList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取会员卡列表失败')
  }
}

// 获取使用记录
const fetchRecordList = async () => {
  try {
    const res = await getCardRecords(recordQueryParams)
    if (res.code === 200) {
      recordList.value = res.data.records
      recordTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取使用记录失败')
  }
}

// 处理购买

const handlePurchase = () => {
  router.push('/member/card-purchase')
}

// 处理查看记录
const handleViewRecords = () => {
  recordQueryParams.cardId = null
  recordQueryParams.page = 1
  recordDialogVisible.value = true
  fetchRecordList()
}

// 处理充值
const handleRecharge = (card) => {
  currentCard.value = card
  rechargeForm.cardId = card.id
  rechargeForm.amount = null
  rechargeForm.payMethod = 1
  rechargeDialogVisible.value = true
}

// 确认充值
const handleConfirmRecharge = async () => {
  await rechargeFormRef.value.validate()

  rechargeLoading.value = true
  try {
    const res = await rechargeCard(rechargeForm)
    if (res.code === 200) {
      ElMessage.success('充值成功')
      rechargeDialogVisible.value = false
      fetchCardList()
    }
  } catch (error) {
    ElMessage.error(error.message || '充值失败')
  } finally {
    rechargeLoading.value = false
  }
}

// 处理查看详情
const handleViewDetail = (card) => {
  recordQueryParams.cardId = card.id
  recordQueryParams.page = 1
  recordDialogVisible.value = true
  fetchRecordList()
}

// 分页
const handleSizeChange = () => {
  queryParams.page = 1
  fetchCardList()
}

const handlePageChange = () => {
  fetchCardList()
}

const handleRecordPageChange = () => {
  fetchRecordList()
}

// 辅助方法
const getCardClass = (card) => {
  return {
    'card-active': card.status === 1,
    'card-inactive': card.status === 0,
    'card-expired': card.status === 2
  }
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '未激活',
    1: '使用中',
    2: '已过期'
  }
  return textMap[status] || '未知'
}

const getRecordTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'primary'
  }
  return tagMap[type] || 'info'
}

const getRecordTypeText = (type) => {
  const textMap = {
    1: '充值',
    2: '消费',
    3: '退款',
    4: '赠送'
  }
  return textMap[type] || '未知'
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

// 生命周期
onMounted(() => {
  fetchCardList()
})
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.member-card-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;

  // 页面头部
  :deep(.el-page-header) {
    margin-bottom: 24px;
  }

  // 顶部操作栏
  .header-actions {
    display: flex;
    gap: 12px;
    margin-bottom: 32px;

    .action-btn {
      border-radius: 12px;
      padding: 12px 24px;
      font-weight: 500;
      transition: all 0.2s;

      &:hover {
        transform: translateY(-1px);
      }
    }
  }

  // Apple Wallet风格卡片容器
  .wallet-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  // 单个钱包卡片
  .wallet-card {
    position: relative;
    min-height: 220px;
    border-radius: 16px;
    padding: 24px;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

    &:hover {
      transform: translateY(-4px) scale(1.02);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
    }

    &:active {
      transform: translateY(-2px) scale(1.01);
    }

    // 不同类型卡片的渐变背景
    &.card-type-1 {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    &.card-type-2 {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }

    &.card-type-3 {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }

    // 状态样式
    &.card-inactive {
      opacity: 0.6;
      filter: grayscale(0.5);
    }

    &.card-expired {
      opacity: 0.5;
      filter: grayscale(0.8);
    }

    // 背景装饰圆圈
    .card-decoration {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;

      .decoration-circle {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);

        &.circle-1 {
          width: 120px;
          height: 120px;
          top: -40px;
          right: -20px;
        }

        &.circle-2 {
          width: 80px;
          height: 80px;
          bottom: -20px;
          left: -10px;
        }

        &.circle-3 {
          width: 60px;
          height: 60px;
          top: 50%;
          right: 20px;
          opacity: 0.5;
        }
      }
    }

    // 卡片内容
    .card-content {
      position: relative;
      z-index: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      color: white;
    }

    // 卡片头部
    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .card-logo {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(10px);
        display: flex;
        align-items: center;
        justify-content: center;

        .el-icon {
          font-size: 20px;
          color: white;
        }
      }

      .status-tag {
        background: rgba(255, 255, 255, 0.25);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        backdrop-filter: blur(10px);
        font-weight: 500;
      }
    }

    // 卡片主体
    .card-main {
      flex: 1;

      .card-name {
        font-size: 20px;
        font-weight: 600;
        margin: 0 0 8px 0;
        color: white;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .card-no {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 16px;
        font-family: 'SF Mono', 'Monaco', 'Courier New', monospace;
        letter-spacing: 1px;
      }

      .card-value {
        .value-item {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .value-label {
            font-size: 12px;
            color: rgba(255, 255, 255, 0.7);
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }

          .value-text {
            font-size: 24px;
            font-weight: 700;
            color: white;
            text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);

            &.highlight {
              font-size: 28px;
            }
          }
        }
      }
    }

    // 卡片底部操作
    .card-actions {
      display: flex;
      gap: 8px;
      margin-top: auto;

      .card-action-btn {
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        backdrop-filter: blur(10px);
        border-radius: 8px;
        font-weight: 500;
        transition: all 0.2s;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          border-color: rgba(255, 255, 255, 0.5);
          transform: translateY(-1px);
        }

        &.el-button--primary {
          background: rgba(255, 255, 255, 0.95);
          color: $primary-color;
          border: none;

          &:hover {
            background: white;
          }
        }
      }
    }
  }

  // 空状态
  .empty-state {
    padding: 60px 20px;
    text-align: center;
  }

  // 分页
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }

  // 文本颜色
  .text-success {
    color: #67c23a;
  }

  .text-danger {
    color: #f56c6c;
  }

  // 响应式
  @media (max-width: 768px) {
    padding: 16px;

    .wallet-cards {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .wallet-card {
      min-height: 200px;
    }
  }
}
</style>
