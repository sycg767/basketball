<template>
  <div class="balance-recharge-container">
    <div class="page-header">
      <BackButton text="返回" />
    </div>

    <el-card class="recharge-card">
      <template #header>
        <div class="card-header">
          <h2>账户余额充值</h2>
          <div class="current-balance">
            <div class="balance-label">当前余额</div>
            <div class="balance-amount">¥{{ currentBalance }}</div>
          </div>
        </div>
      </template>

      <el-divider style="margin: 0 0 24px 0;" />

      <!-- 充值表单 -->
      <el-form :model="rechargeForm" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="amount">
          <div class="form-section">
            <div class="section-label">充值金额</div>
            <!-- 快捷金额选择 -->
            <div class="quick-amounts">
              <el-button
                v-for="amount in quickAmounts"
                :key="amount"
                :type="rechargeForm.amount === amount ? 'primary' : ''"
                @click="rechargeForm.amount = amount"
                class="amount-btn"
              >
                ¥{{ amount }}
              </el-button>
            </div>
            <!-- 自定义金额 -->
            <el-input-number
              v-model="rechargeForm.amount"
              :min="1"
              :max="10000"
              :precision="2"
              controls-position="right"
              placeholder="或输入自定义金额"
              style="width: 100%; margin-top: 12px;"
            />
          </div>
        </el-form-item>

        <el-form-item prop="paymentType">
          <div class="form-section">
            <div class="section-label">支付方式</div>
            <el-radio-group v-model="rechargeForm.paymentType" class="payment-methods">
              <el-radio value="wechat_native" class="payment-radio">
                <div class="payment-option">
                  <div class="payment-icon wechat">
                    <el-icon :size="24"><ChatDotRound /></el-icon>
                  </div>
                  <div class="payment-info">
                    <div class="payment-name">微信支付</div>
                    <div class="payment-desc">扫码支付</div>
                  </div>
                </div>
              </el-radio>
              <el-radio value="alipay_page" class="payment-radio">
                <div class="payment-option">
                  <div class="payment-icon alipay">
                    <el-icon :size="24"><Wallet /></el-icon>
                  </div>
                  <div class="payment-info">
                    <div class="payment-name">支付宝支付</div>
                    <div class="payment-desc">扫码支付</div>
                  </div>
                </div>
              </el-radio>
            </el-radio-group>
          </div>
        </el-form-item>

        <!-- 充值说明 -->
        <div class="recharge-tips">
          <div class="tip-item">
            <el-icon color="#007AFF"><CircleCheck /></el-icon>
            <span>充值金额将实时到账</span>
          </div>
          <div class="tip-item">
            <el-icon color="#007AFF"><CircleCheck /></el-icon>
            <span>余额可用于支付预订订单</span>
          </div>
          <div class="tip-item">
            <el-icon color="#007AFF"><CircleCheck /></el-icon>
            <span>余额永久有效，不会过期</span>
          </div>
        </div>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button size="large" @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleSubmit"
        >
          立即充值 ¥{{ rechargeForm.amount || 0 }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Wallet, CircleCheck } from '@element-plus/icons-vue'
import { getUserBalance, rechargeBalance } from '@/api/member'
import BackButton from '@/components/common/BackButton.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const currentBalance = ref(0)

// 快捷金额选项
const quickAmounts = [50, 100, 200, 500]

// 充值表单
const rechargeForm = reactive({
  amount: null,
  paymentType: 'wechat_native',
  clientIp: ''
})

// 表单验证规则
const rules = {
  amount: [
    { required: true, message: '请选择或输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '充值金额必须大于0', trigger: 'blur' }
  ],
  paymentType: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 获取当前余额
const fetchBalance = async () => {
  try {
    const res = await getUserBalance()
    if (res.code === 200) {
      currentBalance.value = res.data
    }
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

// 提交充值
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    loading.value = true

    const res = await rechargeBalance({
      amount: rechargeForm.amount,
      paymentType: rechargeForm.paymentType,
      clientIp: '127.0.0.1' // 实际应用中应该获取真实IP
    })

    if (res.code === 200) {
      const paymentResult = res.data

      // 根据支付方式跳转
      if (rechargeForm.paymentType.startsWith('wechat')) {
        router.push({
          path: '/payment/wechat',
          query: {
            businessNo: paymentResult.businessNo || paymentResult.paymentNo,
            businessType: 'balance_recharge',
            businessName: '账户余额充值',
            amount: rechargeForm.amount
          }
        })
      } else if (rechargeForm.paymentType.startsWith('alipay')) {
        router.push({
          path: '/payment/alipay',
          query: {
            businessNo: paymentResult.businessNo || paymentResult.paymentNo,
            businessType: 'balance_recharge',
            businessName: '账户余额充值',
            amount: rechargeForm.amount
          }
        })
      }
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchBalance()
})
</script>

<style lang="scss" scoped>
.balance-recharge-container {
  min-height: 100vh;
  background: #f5f5f7;
  padding: 0;
  position: relative;

  .page-header {
    position: absolute;
    top: 20px;
    left: 20px;
    z-index: 10;

    @media (min-width: 640px) {
      top: 24px;
      left: 24px;
    }
  }

  .recharge-card {
    background: #ffffff;
    border-radius: 0;
    box-shadow: none;
    border: none;
    max-width: 600px;
    width: 100%;
    margin: 0 auto;
    min-height: 100vh;

    @media (min-width: 640px) {
      border-radius: 0 0 24px 24px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      margin-top: 0;
    }

    :deep(.el-card__header) {
      background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
      border-bottom: none;
      padding: 80px 32px 40px;

      @media (min-width: 640px) {
        padding: 60px 32px 40px;
      }
    }

    :deep(.el-card__body) {
      padding: 32px;
    }

    .card-header {
      text-align: center;

      h2 {
        font-size: 28px;
        font-weight: 700;
        color: #ffffff;
        margin: 0 0 24px 0;
        letter-spacing: -0.5px;
      }

      .current-balance {
        text-align: center;
        padding: 32px 24px;
        background: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border-radius: 20px;
        color: white;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          top: -50%;
          right: -50%;
          width: 200%;
          height: 200%;
          background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
          animation: shimmer 3s infinite;
        }

        @keyframes shimmer {
          0%, 100% {
            transform: translate(0, 0);
          }
          50% {
            transform: translate(-10%, -10%);
          }
        }

        .balance-label {
          font-size: 15px;
          opacity: 0.95;
          margin-bottom: 12px;
          font-weight: 500;
          letter-spacing: 0.5px;
          position: relative;
          z-index: 1;
        }

        .balance-amount {
          font-size: 52px;
          font-weight: 800;
          text-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          letter-spacing: -1px;
          position: relative;
          z-index: 1;
        }
      }
    }

    :deep(.el-form) {
      .el-form-item {
        margin-bottom: 24px;
      }
    }

    .form-section {
      width: 100%;

      .section-label {
        font-size: 15px;
        font-weight: 600;
        color: #1a202c;
        margin-bottom: 12px;
      }
    }

    .quick-amounts {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 10px;

      .amount-btn {
        height: 48px;
        font-size: 16px;
        font-weight: 700;
        border-radius: 12px;
        border: 2px solid #e2e8f0;
        background: #ffffff;
        color: #1a202c;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

        &:hover {
          border-color: #007AFF;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 122, 255, 0.2);
        }

        &.el-button--primary {
          background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
          border-color: transparent;
          color: white;
          box-shadow: 0 6px 16px rgba(0, 122, 255, 0.3);

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 122, 255, 0.4);
          }
        }
      }
    }

    :deep(.el-input-number) {
      width: 100%;

      .el-input__wrapper {
        border-radius: 14px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border: 2px solid #e2e8f0;
        transition: all 0.3s;
        padding: 12px 16px;

        &:hover {
          border-color: #007AFF;
        }

        &.is-focus {
          border-color: #007AFF;
          box-shadow: 0 4px 16px rgba(0, 122, 255, 0.2);
        }
      }

      .el-input__inner {
        text-align: left;
        font-size: 17px;
        font-weight: 600;
        color: #1a202c;
      }
    }

    .payment-methods {
      width: 100%;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;

      :deep(.el-radio) {
        width: 100%;
        margin: 0;
        height: auto;
        display: block;
      }

      .payment-radio {
        width: 100%;
        padding: 16px;
        border: 2px solid #e2e8f0;
        border-radius: 14px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        background: #ffffff;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 12px;

        &:hover {
          border-color: #007AFF;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 122, 255, 0.15);
        }

        :deep(.el-radio__input) {
          .el-radio__inner {
            width: 18px;
            height: 18px;
            border-width: 2px;
          }

          &.is-checked {
            .el-radio__inner {
              background: #007AFF;
              border-color: #007AFF;

              &::after {
                width: 6px;
                height: 6px;
              }
            }
          }
        }

        :deep(.el-radio__label) {
          padding-left: 10px;
          flex: 1;
        }

        &:has(.el-radio__input.is-checked) {
          border-color: #007AFF;
          background: rgba(0, 122, 255, 0.05);
          box-shadow: 0 4px 12px rgba(0, 122, 255, 0.2);

          .payment-icon {
            transform: scale(1.05);
          }
        }

        .payment-option {
          display: flex;
          align-items: center;
          gap: 12px;
          width: 100%;

          .payment-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            transition: transform 0.3s;
            box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);

            &.wechat {
              background: linear-gradient(135deg, #07c160 0%, #05a850 100%);
              color: white;
            }

            &.alipay {
              background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
              color: white;
            }
          }

          .payment-info {
            flex: 1;

            .payment-name {
              font-size: 15px;
              font-weight: 700;
              color: #1a202c;
              margin-bottom: 2px;
              letter-spacing: -0.2px;
            }

            .payment-desc {
              font-size: 12px;
              color: #718096;
              font-weight: 500;
            }
          }
        }
      }
    }

    .recharge-tips {
      background: rgba(0, 122, 255, 0.05);
      border-radius: 12px;
      padding: 16px 20px;
      margin-top: 8px;

      .tip-item {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 8px;
        font-size: 14px;
        color: #4a5568;
        font-weight: 500;

        &:last-child {
          margin-bottom: 0;
        }

        .el-icon {
          flex-shrink: 0;
        }
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: row;
      justify-content: center;
      gap: 12px;
      margin-top: 24px;

      .el-button {
        min-width: 140px;
        height: 48px;
        font-size: 16px;
        font-weight: 700;
        border-radius: 12px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

        &:not(.el-button--primary) {
          border: 2px solid #e2e8f0;
          background: #ffffff;
          color: #4a5568;

          &:hover {
            border-color: #cbd5e0;
            background: #f7fafc;
          }
        }

        &.el-button--primary {
          background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
          border: none;
          box-shadow: 0 6px 16px rgba(0, 122, 255, 0.3);
          letter-spacing: 0.3px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 122, 255, 0.4);
          }

          &:active {
            transform: translateY(0);
          }
        }
      }
    }
  }
}
</style>
