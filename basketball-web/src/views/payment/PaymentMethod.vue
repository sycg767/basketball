<template>
  <div class="payment-method-container">
    <el-card class="payment-card">
      <!-- 订单信息 -->
      <div class="order-info">
        <h2 class="section-title">订单信息</h2>
        <div class="info-item">
          <span class="label">订单编号:</span>
          <span class="value">{{ orderInfo.orderNo || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">商品名称:</span>
          <span class="value">{{ orderInfo.businessName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">订单金额:</span>
          <span class="value amount">¥{{ orderInfo.amount || '0.00' }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h2 class="section-title">选择支付方式</h2>

        <el-radio-group v-model="selectedMethod" class="method-group">
          <!-- 微信支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'wechat' }"
            @click="selectedMethod = 'wechat'"
          >
            <el-radio value="wechat" size="large">
              <div class="method-content">
                <div class="method-icon wechat">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-wechat"></use>
                  </svg>
                </div>
                <div class="method-info">
                  <h4>微信支付</h4>
                  <p>推荐使用微信扫码支付</p>
                </div>
              </div>
            </el-radio>
          </div>

          <!-- 支付宝支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'alipay' }"
            @click="selectedMethod = 'alipay'"
          >
            <el-radio value="alipay" size="large">
              <div class="method-content">
                <div class="method-icon alipay">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-alipay"></use>
                  </svg>
                </div>
                <div class="method-info">
                  <h4>支付宝支付</h4>
                  <p>支持支付宝扫码或跳转支付</p>
                </div>
              </div>
            </el-radio>
          </div>

          <!-- 余额支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'balance' }"
            @click="selectedMethod = 'balance'"
          >
            <el-radio value="balance" size="large">
              <div class="method-content">
                <div class="method-icon balance">
                  <el-icon :size="32"><Wallet /></el-icon>
                </div>
                <div class="method-info">
                  <h4>余额支付</h4>
                  <p>当前余额: ¥{{ userBalance }}</p>
                </div>
              </div>
            </el-radio>
          </div>
        </el-radio-group>
      </div>

      <!-- 支付按钮 -->
      <div class="payment-actions">
        <el-button size="large" @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          :disabled="!selectedMethod"
          @click="handleConfirmPay"
        >
          确认支付 ¥{{ orderInfo.amount || '0.00' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Wallet } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const selectedMethod = ref('wechat');
const userBalance = ref('0.00');

const orderInfo = reactive({
  orderNo: '',
  businessNo: '',
  businessName: '',
  businessType: '',
  amount: '0.00'
});

onMounted(() => {
  loadOrderInfo();
  loadUserBalance();
});

const loadOrderInfo = () => {
  // 从路由参数获取订单信息
  const { orderNo, businessNo, businessName, businessType, amount } = route.query;

  if (!orderNo || !amount) {
    ElMessage.error('订单信息不完整');
    router.back();
    return;
  }

  orderInfo.orderNo = orderNo;
  orderInfo.businessNo = businessNo || orderNo;
  orderInfo.businessName = businessName || '篮球馆服务';
  orderInfo.businessType = businessType || 'booking';
  orderInfo.amount = amount;
};

const loadUserBalance = () => {
  // 从用户信息获取余额
  const balance = userStore.userInfo.balance || 0;
  userBalance.value = balance.toFixed(2);
};

const handleConfirmPay = () => {
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式');
    return;
  }

  loading.value = true;

  // 根据选择的支付方式跳转到对应页面
  switch (selectedMethod.value) {
    case 'wechat':
      goToWechatPay();
      break;
    case 'alipay':
      goToAlipay();
      break;
    case 'balance':
      payWithBalance();
      break;
    default:
      ElMessage.error('不支持的支付方式');
      loading.value = false;
  }
};

const goToWechatPay = () => {
  router.push({
    path: '/payment/wechat',
    query: {
      businessNo: orderInfo.businessNo,
      businessType: orderInfo.businessType,
      businessName: orderInfo.businessName,
      amount: orderInfo.amount
    }
  });
};

const goToAlipay = () => {
  router.push({
    path: '/payment/alipay',
    query: {
      businessNo: orderInfo.businessNo,
      businessType: orderInfo.businessType,
      businessName: orderInfo.businessName,
      amount: orderInfo.amount
    }
  });
};

const payWithBalance = async () => {
  // TODO: 实现余额支付逻辑
  ElMessage.info('余额支付功能开发中');
  loading.value = false;
};

const handleCancel = () => {
  router.back();
};
</script>

<style lang="scss" scoped>
.payment-method-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;

  .payment-card {
    .section-title {
      margin: 0 0 20px 0;
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }

    .order-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .label {
          font-size: 14px;
          color: #606266;
        }

        .value {
          font-size: 14px;
          color: #303133;

          &.amount {
            font-size: 20px;
            font-weight: bold;
            color: #F56C6C;
          }
        }
      }
    }

    .payment-methods {
      .method-group {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .method-item {
          border: 2px solid #DCDFE6;
          border-radius: 8px;
          padding: 16px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #409EFF;
            background: #F5F7FA;
          }

          &.active {
            border-color: #409EFF;
            background: #ECF5FF;
          }

          :deep(.el-radio) {
            width: 100%;

            .el-radio__input {
              display: none;
            }

            .el-radio__label {
              padding: 0;
              width: 100%;
            }
          }

          .method-content {
            display: flex;
            align-items: center;
            gap: 16px;

            .method-icon {
              width: 48px;
              height: 48px;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 50%;
              flex-shrink: 0;

              &.wechat {
                background: #07C160;

                .icon {
                  width: 28px;
                  height: 28px;
                  fill: #fff;
                }
              }

              &.alipay {
                background: #1677FF;

                .icon {
                  width: 28px;
                  height: 28px;
                  fill: #fff;
                }
              }

              &.balance {
                background: #F56C6C;
                color: #fff;
              }
            }

            .method-info {
              flex: 1;

              h4 {
                margin: 0 0 4px 0;
                font-size: 16px;
                font-weight: 500;
                color: #303133;
              }

              p {
                margin: 0;
                font-size: 13px;
                color: #909399;
              }
            }
          }
        }
      }
    }

    .payment-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 32px;
      padding-top: 20px;
      border-top: 1px solid #EBEEF5;

      .el-button {
        min-width: 120px;
      }
    }
  }
}
</style>
