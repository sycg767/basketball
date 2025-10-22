<template>
  <div class="financial-container">
    <el-card class="page-header">
      <template #header>
        <div class="header-content">
          <h2>财务管理</h2>
          <el-button type="primary" @click="showCreateDialog">创建记录</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="记录类型">
          <el-select v-model="searchForm.recordType" placeholder="请选择类型" clearable style="width: 180px">
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
            <el-option label="退款" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择业务类型" clearable style="width: 180px">
            <el-option label="场地" :value="1" />
            <el-option label="课程" :value="2" />
            <el-option label="会员卡" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon income">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">总收入</div>
              <div class="stat-value">¥{{ formatMoney(totalIncome) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon expense">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">总支出</div>
              <div class="stat-value">¥{{ formatMoney(totalExpense) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon balance">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">当前余额</div>
              <div class="stat-value">¥{{ formatMoney(balance) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon count">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">记录总数</div>
              <div class="stat-value">{{ total }} 条</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="content-card">
      <el-table :data="financialList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="recordNo" label="流水号" width="160" />
        <el-table-column prop="recordType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeText(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ getBusinessTypeText(row.businessType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="['amount', row.recordType === 1 ? 'income' : 'expense']">
              {{ row.recordType === 1 ? '+' : '-' }}¥{{ formatMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balanceBefore" label="变动前余额" width="120">
          <template #default="{ row }">
            ¥{{ formatMoney(row.balanceBefore) }}
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="变动后余额" width="120">
          <template #default="{ row }">
            <span class="current-balance">¥{{ formatMoney(row.balanceAfter) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单号" width="140" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewRecord(row)">查看</el-button>
            <el-button type="danger" size="small" @click="deleteRecord(row)" v-if="row.recordType === 1">删除</el-button>
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

    <!-- 创建记录对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建财务记录' : '编辑财务记录'"
      width="600px"
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordRules"
        label-width="100px"
      >
        <el-form-item label="记录类型" prop="recordType">
          <el-radio-group v-model="recordForm.recordType">
            <el-radio :value="1">收入</el-radio>
            <el-radio :value="2">支出</el-radio>
            <el-radio :value="3">退款</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="recordForm.businessType" placeholder="请选择业务类型">
            <el-option label="场地" :value="1" />
            <el-option label="课程" :value="2" />
            <el-option label="会员卡" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="recordForm.amount"
            :precision="2"
            :min="0.01"
            :step="0.01"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="关联订单号" prop="orderNo">
          <el-input v-model="recordForm.orderNo" placeholder="请输入关联订单号" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="recordForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看记录对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="财务记录详情"
      width="600px"
    >
      <div v-if="viewRecordDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="流水号">{{ viewRecordDetail.recordNo }}</el-descriptions-item>
          <el-descriptions-item label="记录类型">
            <el-tag :type="getRecordTypeTag(viewRecordDetail.recordType)">
              {{ getRecordTypeText(viewRecordDetail.recordType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业务类型">{{ getBusinessTypeText(viewRecordDetail.businessType) }}</el-descriptions-item>
          <el-descriptions-item label="金额">
            <span :class="['amount', viewRecordDetail.recordType === 1 ? 'income' : 'expense']">
              {{ viewRecordDetail.recordType === 1 ? '+' : '-' }}¥{{ formatMoney(viewRecordDetail.amount) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="变动前余额">¥{{ formatMoney(viewRecordDetail.balanceBefore) }}</el-descriptions-item>
          <el-descriptions-item label="变动后余额">
            <span class="current-balance">¥{{ formatMoney(viewRecordDetail.balanceAfter) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="关联订单号">{{ viewRecordDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(viewRecordDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ viewRecordDetail.description }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { request } from '@/utils/request.js'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const recordFormRef = ref<FormInstance>()

const searchForm = reactive({
  recordType: '',
  businessType: '',
  orderNo: ''
})

const recordForm = reactive({
  id: null,
  recordType: 1,
  businessType: 1,
  orderNo: '',
  amount: 0,
  description: ''
})

const recordRules: FormRules = {
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const financialList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const totalIncome = ref(0)
const totalExpense = ref(0)
const balance = ref(0)
const viewRecordDetail = ref(null)

const getFinancialRecords = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const response = await request.get('/api/financial/page', { params })
    if (response.code === 200) {
      financialList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取财务记录失败')
  }
  loading.value = false
}

const getFinancialStatistics = async () => {
  try {
    const response = await request.get('/api/financial/statistics')
    if (response.code === 200) {
      totalIncome.value = response.data.totalIncome
      totalExpense.value = response.data.totalExpense
      balance.value = response.data.balance
    }
  } catch (error) {
    console.error('获取财务统计失败:', error)
  }
}

const searchRecords = () => {
  currentPage.value = 1
  getFinancialRecords()
}

const resetSearch = () => {
  searchForm.recordType = ''
  searchForm.businessType = ''
  searchForm.orderNo = ''
  searchRecords()
}

const showCreateDialog = () => {
  dialogType.value = 'create'
  recordForm.id = null
  Object.assign(recordForm, {
    recordType: 1,
    businessType: 1,
    orderNo: '',
    amount: 0,
    description: ''
  })
  dialogVisible.value = true
}

const saveRecord = async () => {
  if (!recordFormRef.value) return

  await recordFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const url = dialogType.value === 'create'
          ? '/api/financial/create'
          : '/api/financial/update'

        const response = await request.post(url, recordForm)
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'create' ? '创建成功' : '更新成功')
          dialogVisible.value = false
          getFinancialRecords()
          getFinancialStatistics()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      }
      saving.value = false
    }
  })
}

const viewRecord = async (row) => {
  try {
    const response = await request.get(`/api/financial/${row.id}`)
    if (response.code === 200) {
      viewRecordDetail.value = response.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取财务记录详情失败')
  }
}

const deleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此财务记录吗？', '提示', {
      type: 'warning'
    })

    const response = await request.delete(`/api/financial/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getFinancialRecords()
      getFinancialStatistics()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getRecordTypeText = (type) => {
  const map = {
    1: '收入',
    2: '支出',
    3: '退款'
  }
  return map[type] || '未知'
}

const getRecordTypeTag = (type) => {
  const map = {
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return map[type] || 'info'
}

const getBusinessTypeText = (type) => {
  const map = {
    1: '场地',
    2: '课程',
    3: '会员卡',
    4: '其他'
  }
  return map[type] || '未知'
}

const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getFinancialRecords()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getFinancialRecords()
}

onMounted(() => {
  getFinancialRecords()
  getFinancialStatistics()
})
</script>

<style scoped lang="scss">
.financial-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
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

      &.income {
        background: linear-gradient(135deg, #67c23a, #85ce61);
      }

      &.expense {
        background: linear-gradient(135deg, #f56c6c, #f78989);
      }

      &.balance {
        background: linear-gradient(135deg, #409eff, #66b1ff);
      }

      &.count {
        background: linear-gradient(135deg, #909399, #a6a9ad);
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

.content-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.amount {
  font-weight: bold;

  &.income {
    color: #67c23a;
  }

  &.expense {
    color: #f56c6c;
  }
}

.current-balance {
  font-weight: bold;
  color: #409eff;
}

:deep(.el-card) {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>