<template>
  <div class="card-purchase-container">
    <BackButton text="返回" />

    <div class="purchase-content">
      <!-- 会员卡类型列表 - Apple风格 -->
      <div class="card-types-grid">
        <div
          v-for="cardType in cardTypes"
          :key="cardType.id"
          class="card-type-item"
          :class="[
            { 'card-selected': selectedCardType?.id === cardType.id },
            `card-style-${cardType.cardType}`
          ]"
          @click="handleSelectCardType(cardType)"
        >
          <!-- 卡片装饰背景 -->
          <div class="card-decoration">
            <div class="deco-circle circle-1"></div>
            <div class="deco-circle circle-2"></div>
          </div>

          <!-- 卡片内容 -->
          <div class="card-type-content">
            <!-- 头部 -->
            <div class="card-type-header">
              <div class="card-icon">
                <el-icon><CreditCard /></el-icon>
              </div>
              <el-tag :type="getCardTypeTag(cardType.cardType)" class="card-tag">
                {{ getCardTypeText(cardType.cardType) }}
              </el-tag>
            </div>

            <!-- 卡片名称 -->
            <h3 class="card-name">{{ cardType.cardName }}</h3>

            <!-- 价格 -->
            <div class="card-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ cardType.price }}</span>
            </div>

            <!-- 卡片详情 -->
            <div class="card-details">
              <!-- 时间卡显示时长 -->
              <div v-if="cardType.cardType === 1" class="detail-item">
                <el-icon><Clock /></el-icon>
                <span>有效期 {{ cardType.duration }} 天</span>
              </div>

              <!-- 次卡显示次数 -->
              <div v-if="cardType.cardType === 2" class="detail-item">
                <el-icon><Tickets /></el-icon>
                <span>{{ cardType.times }} 次使用</span>
              </div>

              <!-- 会员等级 -->
              <div class="detail-item">
                <el-icon><User /></el-icon>
                <span>{{ getMemberLevelText(cardType.memberLevel) }}</span>
              </div>
            </div>

            <!-- 权益列表 -->
            <div v-if="cardType.benefitsList && cardType.benefitsList.length" class="card-benefits">
              <div class="benefits-title">会员权益</div>
              <ul class="benefits-list">
                <li v-for="(benefit, index) in cardType.benefitsList" :key="index">
                  <el-icon><Check /></el-icon>
                  <span>{{ benefit }}</span>
                </li>
              </ul>
            </div>

            <!-- 选中标识 -->
            <div v-if="selectedCardType?.id === cardType.id" class="selected-badge">
              <el-icon><Select /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 购买确认区域 -->
      <div v-if="selectedCardType" class="purchase-confirm">
        <div class="confirm-card">
          <h3 class="confirm-title">确认购买信息</h3>

          <div class="confirm-details">
            <div class="detail-row">
              <span class="detail-label">会员卡类型</span>
              <span class="detail-value">{{ selectedCardType.cardName }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">支付金额</span>
              <span class="detail-value price">¥{{ selectedCardType.price }}</span>
            </div>
          </div>

          <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef">
            <el-form-item label="支付方式" prop="payMethod" class="payment-method">
              <el-radio-group v-model="purchaseForm.payMethod" class="payment-options">
                <el-radio :label="1" class="payment-option">
                  <div class="option-content">
                    <el-icon><CreditCard /></el-icon>
                    <span>在线支付</span>
                  </div>
                </el-radio>
                <el-radio :label="2" class="payment-option">
                  <div class="option-content">
                    <el-icon><Wallet /></el-icon>
                    <span>余额支付</span>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <div class="confirm-actions">
              <el-button @click="handleCancel" class="cancel-btn">取消</el-button>
              <el-button type="primary" @click="handlePurchase" :loading="purchaseLoading" class="purchase-btn">
                立即购买
              </el-button>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 提示信息 -->
      <div v-else class="empty-selection">
        <el-empty description="请先选择要购买的会员卡类型" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, CreditCard, Clock, Tickets, User, Select, Wallet } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getCardTypes, purchaseCard } from '@/api/member'
import BackButton from '@/components/common/BackButton.vue'

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
    0: '普通用户',
    1: '银卡会员',
    2: '金卡会员',
    3: '铂金会员',
    4: 'VIP会员'
  }
  return levelMap[level] || '普通用户'
}

// 生命周期
onMounted(() => {
  fetchCardTypes()
})
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.card-purchase-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;

  .purchase-content {
    display: flex;
    flex-direction: column;
    gap: 32px;

    // 卡片类型网格
    .card-types-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 24px;
    }

    // 单个卡片类型
    .card-type-item {
      position: relative;
      border-radius: 16px;
      padding: 24px;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      background: white;
      overflow: hidden;
      min-height: 380px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      &.card-selected {
        border: 2px solid $primary-color;
        box-shadow: 0 4px 20px rgba($primary-color, 0.25);
      }

      // 不同类型卡片的装饰色
      &.card-style-1 .card-decoration {
        background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
      }

      &.card-style-2 .card-decoration {
        background: linear-gradient(135deg, #f093fb15 0%, #f5576c15 100%);
      }

      &.card-style-3 .card-decoration {
        background: linear-gradient(135deg, #4facfe15 0%, #00f2fe15 100%);
      }

      // 装饰背景
      .card-decoration {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none;

        .deco-circle {
          position: absolute;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.5);

          &.circle-1 {
            width: 100px;
            height: 100px;
            top: -30px;
            right: -20px;
          }

          &.circle-2 {
            width: 60px;
            height: 60px;
            bottom: -15px;
            left: -10px;
          }
        }
      }

      // 卡片内容
      .card-type-content {
        position: relative;
        z-index: 1;
      }

      // 卡片头部
      .card-type-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .card-icon {
          width: 44px;
          height: 44px;
          border-radius: 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;

          .el-icon {
            font-size: 20px;
            color: white;
          }
        }

        .card-tag {
          font-weight: 500;
        }
      }

      // 卡片名称
      .card-name {
        font-size: 22px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 16px 0;
      }

      // 价格
      .card-price {
        display: flex;
        align-items: baseline;
        margin-bottom: 20px;

        .price-symbol {
          font-size: 20px;
          font-weight: 600;
          color: $primary-color;
          margin-right: 4px;
        }

        .price-value {
          font-size: 36px;
          font-weight: 700;
          color: $primary-color;
          line-height: 1;
        }
      }

      // 卡片详情
      .card-details {
        display: flex;
        flex-direction: column;
        gap: 12px;
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 1px solid $border-color;

        .detail-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: $text-secondary;

          .el-icon {
            font-size: 16px;
            color: $primary-color;
          }
        }
      }

      // 权益列表
      .card-benefits {
        .benefits-title {
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 12px;
        }

        .benefits-list {
          margin: 0;
          padding: 0;
          list-style: none;

          li {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            font-size: 13px;
            color: $text-secondary;
            margin-bottom: 8px;
            line-height: 1.5;

            .el-icon {
              color: #67c23a;
              font-size: 16px;
              flex-shrink: 0;
              margin-top: 2px;
            }

            span {
              flex: 1;
            }
          }
        }
      }

      // 选中标识
      .selected-badge {
        position: absolute;
        top: 16px;
        right: 16px;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: $primary-color;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2px 8px rgba($primary-color, 0.4);

        .el-icon {
          font-size: 18px;
          color: white;
        }
      }
    }

    // 购买确认区域
    .purchase-confirm {
      max-width: 600px;
      margin: 0 auto;

      .confirm-card {
        background: white;
        border-radius: 16px;
        padding: 32px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

        .confirm-title {
          font-size: 20px;
          font-weight: 600;
          color: $text-primary;
          margin: 0 0 24px 0;
        }

        .confirm-details {
          background: $bg-light;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 24px;

          .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;

            &:not(:last-child) {
              border-bottom: 1px solid $border-color;
            }

            .detail-label {
              font-size: 14px;
              color: $text-secondary;
            }

            .detail-value {
              font-size: 15px;
              font-weight: 600;
              color: $text-primary;

              &.price {
                font-size: 24px;
                color: $primary-color;
              }
            }
          }
        }

        .payment-method {
          margin-bottom: 24px;

          :deep(.el-form-item__label) {
            font-weight: 600;
            color: $text-primary;
          }

          .payment-options {
            display: flex;
            gap: 16px;
            width: 100%;

            .payment-option {
              flex: 1;

              :deep(.el-radio__input) {
                display: none;
              }

              :deep(.el-radio__label) {
                padding: 0;
                width: 100%;
              }

              .option-content {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 6px;
                padding: 6px;
                border: 1px solid $border-color;
                border-radius: 10px;
                transition: all 0.2s;
                cursor: pointer;

                .el-icon {
                  font-size: 24px;
                  color: $text-secondary;
                }

                span {
                  font-size: 13px;
                  font-weight: 500;
                  color: $text-primary;
                }
              }

              &:hover .option-content {
                border-color: rgba($primary-color, 0.5);
                background: rgba($primary-color, 0.03);
              }

              :deep(.el-radio__input.is-checked) + .el-radio__label .option-content {
                border-color: $primary-color;
                background: rgba($primary-color, 0.08);

                .el-icon {
                  color: $primary-color;
                }
              }
            }
          }
        }

        .confirm-actions {
          display: flex;
          gap: 12px;
          justify-content: flex-end;

          .cancel-btn,
          .purchase-btn {
            padding: 12px 32px;
            font-weight: 500;
            border-radius: 10px;
            transition: all 0.2s;

            &:hover {
              transform: translateY(-1px);
            }
          }

          .purchase-btn {
            min-width: 140px;
          }
        }
      }
    }

    // 空状态
    .empty-selection {
      padding: 60px 20px;
      text-align: center;
    }
  }

  // 响应式
  @media (max-width: 768px) {
    padding: 16px;

    .purchase-content {
      .card-types-grid {
        grid-template-columns: 1fr;
        gap: 16px;
      }

      .card-type-item {
        min-height: 340px;
      }

      .purchase-confirm .confirm-card {
        padding: 24px 20px;

        .payment-options {
          flex-direction: column;
        }

        .confirm-actions {
          flex-direction: column-reverse;

          .cancel-btn,
          .purchase-btn {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>
