<template>
  <div class="card-purchase-container">
    <el-page-header @back="handleBack" content="购买会员卡" />

    <div class="purchase-content">
      <!-- 会员卡类型列表 -->
      <el-row :gutter="20" class="card-types">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="cardType in cardTypes" :key="cardType.id">
          <el-card
            class="card-type"
            :class="{ 'card-selected': selectedCardType?.id === cardType.id }"
            @click="handleSelectCardType(cardType)"
          >
            <div class="card-type-header">
              <h3>{{ cardType.cardName }}</h3>
              <el-tag :type="getCardTypeTag(cardType.cardType)">
                {{ getCardTypeText(cardType.cardType) }}
              </el-tag>
            </div>

            <div class="card-type-body">
              <div class="price">
                <span class="label">价格:</span>
                <span class="value">¥{{ cardType.price }}</span>
              </div>

              <!-- 时间卡显示时长 -->
              <div v-if="cardType.cardType === 1" class="duration">
                <span class="label">有效期:</span>
                <span class="value">{{ cardType.duration }} 天</span>
              </div>

              <!-- 次卡显示次数 -->
              <div v-if="cardType.cardType === 2" class="times">
                <span class="label">次数:</span>
                <span class="value">{{ cardType.times }} 次</span>
              </div>

              <!-- 会员等级 -->
              <div class="level">
                <span class="label">会员等级:</span>
                <span class="value">{{ getMemberLevelText(cardType.memberLevel) }}</span>
              </div>

              <!-- 权益列表 -->
              <div v-if="cardType.benefitsList && cardType.benefitsList.length" class="benefits">
                <div class="benefits-title">权益:</div>
                <ul class="benefits-list">
                  <li v-for="(benefit, index) in cardType.benefitsList" :key="index">
                    <el-icon><Check /></el-icon>
                    {{ benefit }}
                  </li>
                </ul>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 购买表单 -->
      <el-card v-if="selectedCardType" class="purchase-form">
        <h3>确认购买信息</h3>
        <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef" label-width="100px">
          <el-form-item label="选择卡类型">
            <el-input :value="selectedCardType.cardName" disabled />
          </el-form-item>

          <el-form-item label="支付金额">
            <el-input :value="`¥${selectedCardType.price}`" disabled />
          </el-form-item>

          <el-form-item label="支付方式" prop="payMethod">
            <el-radio-group v-model="purchaseForm.payMethod">
              <el-radio :label="1">在线支付</el-radio>
              <el-radio :label="2">余额支付</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handlePurchase" :loading="purchaseLoading">
              立即购买
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 提示信息 -->
      <el-empty v-else description="请先选择要购买的会员卡类型" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getCardTypes, purchaseCard } from '@/api/member'

const router = useRouter()

// 会员卡类型列表
const cardTypes = ref([])
const selectedCardType = ref(null)

// 购买表单
const purchaseLoading = ref(false)
const purchaseForm = reactive({
  cardTypeId: null,
  payMethod: 1
})
const purchaseRules = {
  payMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}
const purchaseFormRef = ref(null)

// 获取会员卡类型列表
const fetchCardTypes = async () => {
  try {
    const res = await getCardTypes()
    if (res.code === 200) {
      cardTypes.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取会员卡类型失败')
  }
}

// 选择会员卡类型
const handleSelectCardType = (cardType) => {
  selectedCardType.value = cardType
  purchaseForm.cardTypeId = cardType.id
}

// 购买会员卡
const handlePurchase = async () => {
  await purchaseFormRef.value.validate()

  ElMessageBox.confirm(
    `确认购买【${selectedCardType.value.cardName}】吗?`,
    '确认购买',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    purchaseLoading.value = true
    try {
      const res = await purchaseCard(purchaseForm)
      if (res.code === 200) {
        ElMessage.success('购买成功')
        router.push('/member/card')
      }
    } catch (error) {
      ElMessage.error(error.message || '购买失败')
    } finally {
      purchaseLoading.value = false
    }
  }).catch(() => {
    // 取消购买
  })
}

// 取消
const handleCancel = () => {
  router.back()
}

// 返回
const handleBack = () => {
  router.back()
}

// 辅助方法
const getCardTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return tagMap[type] || 'info'
}

const getCardTypeText = (type) => {
  const textMap = {
    1: '时间卡',
    2: '次卡',
    3: '储值卡'
  }
  return textMap[type] || '未知'
}

const getMemberLevelText = (level) => {
  const levelMap = {
    1: '普通会员',
    2: '银卡会员',
    3: '金卡会员',
    4: 'VIP会员'
  }
  return levelMap[level] || '普通会员'
}

// 生命周期
onMounted(() => {
  fetchCardTypes()
})
</script>

<style lang="scss" scoped>
.card-purchase-container {
  padding: 20px;

  :deep(.el-page-header) {
    margin-bottom: 20px;
  }

  .purchase-content {
    .card-types {
      margin-bottom: 20px;

      .card-type {
        margin-bottom: 20px;
        cursor: pointer;
        transition: all 0.3s;
        height: 100%;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        &.card-selected {
          border-color: #409eff;
          box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
        }

        .card-type-header {
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

        .card-type-body {
          .price, .duration, .times, .level {
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
            }
          }

          .price .value {
            color: #f56c6c;
            font-size: 20px;
            font-weight: bold;
          }

          .benefits {
            margin-top: 15px;

            .benefits-title {
              font-size: 14px;
              color: #606266;
              margin-bottom: 10px;
            }

            .benefits-list {
              margin: 0;
              padding: 0;
              list-style: none;

              li {
                display: flex;
                align-items: center;
                gap: 5px;
                font-size: 13px;
                color: #606266;
                margin-bottom: 8px;

                .el-icon {
                  color: #67c23a;
                  font-size: 16px;
                }
              }
            }
          }
        }
      }
    }

    .purchase-form {
      max-width: 600px;
      margin: 0 auto;

      h3 {
        margin: 0 0 20px;
        font-size: 18px;
        color: #303133;
      }
    }
  }
}
</style>
