<template>
  <div class="member-card-container">
    <!-- 顶部操作栏 -->
    <el-card class="header-card">
      <div class="header-actions">
        <el-button type="primary" @click="handlePurchase">
          <el-icon><Plus /></el-icon>
          购买会员卡
        </el-button>
        <el-button @click="handleViewRecords">
          <el-icon><Document /></el-icon>
          使用记录
        </el-button>
      </div>
    </el-card>

    <!-- 会员卡列表 -->
    <el-card class="table-card">
      <el-row :gutter="20" class="card-list" v-if="cardList.length > 0">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="card in cardList" :key="card.id">
          <el-card class="member-card" :class="getCardClass(card)">
            <div class="card-header">
              <h3>{{ card.cardName }}</h3>
              <el-tag :type="getStatusType(card.status)">
                {{ getStatusText(card.status) }}
              </el-tag>
            </div>

            <div class="card-body">
              <div class="card-no">卡号: {{ card.cardNo }}</div>

              <!-- 时间卡显示有效期 -->
              <div v-if="card.cardType === 1" class="card-info">
                <div class="info-item">
                  <span class="label">开始日期:</span>
                  <span class="value">{{ formatDate(card.startDate) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">到期日期:</span>
                  <span class="value">{{ formatDate(card.expireDate) }}</span>
                </div>
              </div>

              <!-- 次卡显示剩余次数 -->
              <div v-if="card.cardType === 2" class="card-info">
                <div class="info-item">
                  <span class="label">剩余次数:</span>
                  <span class="value highlight">{{ card.remainingTimes }} 次</span>
                </div>
              </div>

              <!-- 储值卡显示余额 -->
              <div v-if="card.cardType === 3" class="card-info">
                <div class="info-item">
                  <span class="label">卡内余额:</span>
                  <span class="value highlight">¥{{ card.balance }}</span>
                </div>
              </div>
            </div>

            <div class="card-footer">
              <el-button
                v-if="card.cardType === 3 && card.status === 1"
                type="primary"
                size="small"
                @click="handleRecharge(card)"
              >
                充值
              </el-button>
              <el-button
                size="small"
                @click="handleViewDetail(card)"
              >
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-else description="暂无会员卡">
        <el-button type="primary" @click="handlePurchase">立即购买会员卡</el-button>
      </el-empty>
    </el-card>

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
import { Plus, Document } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getMyCards, rechargeCard, getCardRecords } from '@/api/member'

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
.member-card-container {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .card-list {
    margin-bottom: 20px;

    .member-card {
      margin-bottom: 20px;
      height: 280px;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.card-active {
        border-color: #67c23a;
      }

      &.card-inactive {
        border-color: #909399;
        opacity: 0.7;
      }

      &.card-expired {
        border-color: #f56c6c;
        opacity: 0.6;
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid #ebeef5;

        h3 {
          margin: 0;
          font-size: 18px;
          color: #303133;
        }
      }

      .card-body {
        .card-no {
          font-size: 14px;
          color: #909399;
          margin-bottom: 15px;
        }

        .card-info {
          .info-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 14px;

            .label {
              color: #606266;
            }

            .value {
              color: #303133;
              font-weight: 500;

              &.highlight {
                color: #409eff;
                font-size: 16px;
                font-weight: bold;
              }
            }
          }
        }
      }

      .card-footer {
        display: flex;
        gap: 10px;
        margin-top: 20px;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .text-success {
    color: #67c23a;
  }

  .text-danger {
    color: #f56c6c;
  }
}
</style>
