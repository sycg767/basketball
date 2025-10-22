<template>
  <div class="member-manage-container">
    <el-tabs v-model="activeTab" type="card">
      <!-- 会员卡类型管理 -->
      <el-tab-pane label="会员卡类型" name="cardType">
        <el-card class="filter-card">
          <el-button type="primary" icon="Plus" @click="handleAddCardType">添加会员卡类型</el-button>
        </el-card>

        <el-card class="table-card">
          <el-table :data="cardTypeList" stripe border v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="cardName" label="卡名称" min-width="120" />
            <el-table-column prop="cardCode" label="卡编号" width="140" />
            <el-table-column label="卡类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getCardTypeColor(row.cardType)">
                  {{ getCardTypeText(row.cardType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="时长/次数" width="100">
              <template #default="{ row }">
                <span v-if="row.cardType === 1">{{ row.duration }}天</span>
                <span v-else-if="row.cardType === 2">{{ row.times }}次</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="memberLevel" label="会员等级" width="100">
              <template #default="{ row }">
                {{ getMemberLevelText(row.memberLevel) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link icon="Edit" @click="handleEditCardType(row)">
                  编辑
                </el-button>
                <el-button type="danger" link icon="Delete" @click="handleDeleteCardType(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="cardTypePage"
              v-model:page-size="cardTypePageSize"
              :total="cardTypeTotal"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadCardTypeList"
              @current-change="loadCardTypeList"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 会员卡管理 -->
      <el-tab-pane label="会员卡管理" name="memberCard">
        <el-card class="filter-card">
          <el-form :inline="true" :model="cardSearchForm">
            <el-form-item label="关键词">
              <el-input
                v-model="cardSearchForm.keyword"
                placeholder="卡号/用户名/手机号"
                clearable
                @clear="handleCardSearch"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="cardSearchForm.status" placeholder="请选择" clearable style="width: 180px">
                <el-option label="全部" value="" />
                <el-option label="未激活" :value="0" />
                <el-option label="有效" :value="1" />
                <el-option label="已过期" :value="2" />
                <el-option label="已冻结" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleCardSearch">搜索</el-button>
              <el-button icon="Refresh" @click="handleCardReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card">
          <el-table :data="cardList" stripe border v-loading="cardLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="cardNo" label="卡号" width="220" />
            <el-table-column prop="cardName" label="卡类型" width="120" />
            <el-table-column prop="realName" label="持卡人" width="100">
              <template #default="{ row }">
                {{ row.realName || row.username }}
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column label="卡信息" min-width="150">
              <template #default="{ row }">
                <div v-if="row.cardType === 1">
                  有效期: {{ row.startDate }} ~ {{ row.expireDate }}
                </div>
                <div v-else-if="row.cardType === 2">
                  剩余次数: {{ row.remainingTimes }}次
                </div>
                <div v-else-if="row.cardType === 3">
                  余额: ¥{{ row.balance }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getCardStatusColor(row.status)">
                  {{ getCardStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 0"
                  type="success"
                  link
                  @click="handleActivateCard(row)"
                >
                  激活
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="warning"
                  link
                  @click="handleFreezeCard(row)"
                >
                  冻结
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="cardPage"
              v-model:page-size="cardPageSize"
              :total="cardTotal"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleCardSearch"
              @current-change="handleCardSearch"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加/编辑会员卡类型对话框 -->
    <el-dialog
      v-model="cardTypeDialogVisible"
      :title="cardTypeDialogTitle"
      width="700px"
      @close="handleCardTypeDialogClose"
    >
      <el-form
        ref="cardTypeFormRef"
        :model="cardTypeForm"
        :rules="cardTypeRules"
        label-width="120px"
      >
        <el-form-item label="卡名称" prop="cardName">
          <el-input v-model="cardTypeForm.cardName" placeholder="请输入卡名称" />
        </el-form-item>
        <el-form-item label="卡编号" prop="cardCode">
          <el-input v-model="cardTypeForm.cardCode" placeholder="请输入卡编号" />
        </el-form-item>
        <el-form-item label="卡类型" prop="cardType">
          <el-select v-model="cardTypeForm.cardType" placeholder="请选择" style="width: 100%">
            <el-option label="时间卡" :value="1" />
            <el-option label="次卡" :value="2" />
            <el-option label="储值卡" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="cardTypeForm.cardType === 1" label="有效期(天)" prop="duration">
          <el-input-number v-model="cardTypeForm.duration" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="cardTypeForm.cardType === 2" label="次数" prop="times">
          <el-input-number v-model="cardTypeForm.times" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="cardTypeForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="cardTypeForm.originalPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="折扣">
          <el-input-number v-model="cardTypeForm.discount" :min="0" :max="1" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="cardTypeForm.memberLevel" placeholder="请选择" style="width: 100%">
            <el-option label="银卡会员" :value="1" />
            <el-option label="金卡会员" :value="2" />
            <el-option label="铂金会员" :value="3" />
            <el-option label="VIP会员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="权益列表">
          <el-input
            v-model="benefitsText"
            type="textarea"
            :rows="4"
            placeholder="每行一个权益"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="cardTypeForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="cardTypeForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="cardTypeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCardTypeSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCardTypes,
  createCardType,
  updateCardType,
  deleteCardType,
  updateCardTypeStatus,
  getAllCards,
  activateCard,
  freezeCard
} from '@/api/admin'

const activeTab = ref('cardType')
const loading = ref(false)
const cardLoading = ref(false)

// 会员卡类型管理
const cardTypeList = ref([])
const cardTypePage = ref(1)
const cardTypePageSize = ref(10)
const cardTypeTotal = ref(0)

// 会员卡管理
const cardList = ref([])
const cardPage = ref(1)
const cardPageSize = ref(10)
const cardTotal = ref(0)
const cardSearchForm = reactive({
  keyword: '',
  status: ''
})

// 会员卡类型对话框
const cardTypeDialogVisible = ref(false)
const cardTypeDialogTitle = ref('')
const cardTypeFormRef = ref(null)
const submitting = ref(false)
const benefitsText = ref('')

const cardTypeForm = reactive({
  id: null,
  cardName: '',
  cardCode: '',
  cardType: null,
  duration: null,
  times: null,
  price: null,
  originalPrice: null,
  discount: null,
  memberLevel: null,
  description: '',
  sortOrder: 0,
  status: 1
})

const cardTypeRules = {
  cardName: [{ required: true, message: '请输入卡名称', trigger: 'blur' }],
  cardCode: [{ required: true, message: '请输入卡编号', trigger: 'blur' }],
  cardType: [{ required: true, message: '请选择卡类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  memberLevel: [{ required: true, message: '请选择会员等级', trigger: 'change' }]
}

// 获取会员卡类型列表
const loadCardTypeList = async () => {
  try {
    loading.value = true
    const { data } = await getCardTypes({ page: cardTypePage.value, pageSize: cardTypePageSize.value })
    cardTypeList.value = data.records || []
    cardTypeTotal.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取会员卡类型列表失败')
  } finally {
    loading.value = false
  }
}

// 添加会员卡类型
const handleAddCardType = () => {
  cardTypeDialogTitle.value = '添加会员卡类型'
  resetCardTypeForm()
  cardTypeDialogVisible.value = true
}

// 编辑会员卡类型
const handleEditCardType = (row) => {
  cardTypeDialogTitle.value = '编辑会员卡类型'
  Object.assign(cardTypeForm, row)
  if (row.benefitsList && row.benefitsList.length) {
    benefitsText.value = row.benefitsList.join('\n')
  }
  cardTypeDialogVisible.value = true
}

// 删除会员卡类型
const handleDeleteCardType = (row) => {
  ElMessageBox.confirm(`确定要删除会员卡类型"${row.cardName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCardType(row.id)
      ElMessage.success('删除成功')
      loadCardTypeList()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await updateCardTypeStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 提交会员卡类型表单
const handleCardTypeSubmit = async () => {
  await cardTypeFormRef.value.validate()

  try {
    submitting.value = true
    const formData = { ...cardTypeForm }

    // 处理权益列表
    if (benefitsText.value) {
      formData.benefits = benefitsText.value.split('\n').filter(line => line.trim())
    }

    if (formData.id) {
      await updateCardType(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createCardType(formData)
      ElMessage.success('添加成功')
    }

    cardTypeDialogVisible.value = false
    loadCardTypeList()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// 获取会员卡列表
const loadCardList = async () => {
  try {
    cardLoading.value = true
    const params = {
      keyword: cardSearchForm.keyword,
      status: cardSearchForm.status,
      page: cardPage.value,
      pageSize: cardPageSize.value
    }
    const { data } = await getAllCards(params)
    cardList.value = data.records || []
    cardTotal.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取会员卡列表失败')
  } finally {
    cardLoading.value = false
  }
}

// 搜索会员卡
const handleCardSearch = () => {
  cardPage.value = 1
  loadCardList()
}

// 重置会员卡搜索
const handleCardReset = () => {
  cardSearchForm.keyword = ''
  cardSearchForm.status = ''
  handleCardSearch()
}

// 激活会员卡
const handleActivateCard = (row) => {
  ElMessageBox.confirm('确定要激活该会员卡吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await activateCard(row.id)
      ElMessage.success('激活成功')
      loadCardList()
    } catch (error) {
      ElMessage.error('激活失败')
    }
  }).catch(() => {})
}

// 冻结会员卡
const handleFreezeCard = (row) => {
  ElMessageBox.confirm('确定要冻结该会员卡吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await freezeCard(row.id)
      ElMessage.success('冻结成功')
      loadCardList()
    } catch (error) {
      ElMessage.error('冻结失败')
    }
  }).catch(() => {})
}

// 重置会员卡类型表单
const resetCardTypeForm = () => {
  Object.assign(cardTypeForm, {
    id: null,
    cardName: '',
    cardCode: '',
    cardType: null,
    duration: null,
    times: null,
    price: null,
    originalPrice: null,
    discount: null,
    memberLevel: null,
    description: '',
    sortOrder: 0,
    status: 1
  })
  benefitsText.value = ''
  cardTypeFormRef.value?.clearValidate()
}

// 关闭对话框
const handleCardTypeDialogClose = () => {
  resetCardTypeForm()
}

// 辅助函数
const getCardTypeText = (type) => {
  const map = { 1: '时间卡', 2: '次卡', 3: '储值卡' }
  return map[type] || '未知'
}

const getCardTypeColor = (type) => {
  const map = { 1: 'primary', 2: 'success', 3: 'warning' }
  return map[type] || 'info'
}

const getMemberLevelText = (level) => {
  const map = { 0: '普通用户', 1: '银卡会员', 2: '金卡会员', 3: '铂金会员', 4: 'VIP会员' }
  return map[level] || '未知'
}

const getCardStatusText = (status) => {
  const map = { 0: '未激活', 1: '有效', 2: '已过期', 3: '已冻结', 4: '已注销' }
  return map[status] || '未知'
}

const getCardStatusColor = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning', 4: 'info' }
  return map[status] || 'info'
}

onMounted(() => {
  loadCardTypeList()
  loadCardList()
})
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.member-manage-container {
  .filter-card, .table-card {
    margin-bottom: 24px;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-button) {
      border-radius: $radius-md;
      font-weight: 500;
      transition: all $duration-fast $ease-in-out;

      &:hover {
        transform: translateY(-1px);
      }
    }

    :deep(.el-table) {
      font-size: 14px;

      th {
        background-color: $bg-secondary;
        color: $text-secondary;
        font-weight: 500;
      }

      td {
        color: $text-primary;
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }
}
</style>
