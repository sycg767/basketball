<template>
  <el-dialog
    v-model="visible"
    title="课程支付"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="payment-dialog-content">
      <!-- 订单信息 -->
      <div class="order-info">
        <h3 class="section-title">订单信息</h3>
        <div class="info-item">
          <span class="label">课程名称:</span>
          <span class="value">{{ orderInfo.courseName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">订单编号:</span>
          <span class="value">{{ orderInfo.enrollmentNo || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">订单金额:</span>
          <span class="value amount">¥{{ orderInfo.amount || '0.00' }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h3 class="section-title">选择支付方式</h3>

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
                  <el-icon :size="28"><ChatDotRound /></el-icon>
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
                  <el-icon :size="28"><Wallet /></el-icon>
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
                  <el-icon :size="28"><Wallet /></el-icon>
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
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!selectedMethod"
          @click="handleConfirmPay"
        >
          确认支付 ¥{{ orderInfo.amount || '0.00' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ChatDotRound, Wallet } from '@element-plus/icons-vue';
import { payEnrollment } from '@/api/course';
import { getUserBalance } from '@/api/member';
import { useUserStore } from '@/store/modules/user';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  enrollmentId: {
    type: [Number, String],
    required: true
  },
  orderInfo: {
    type: Object,
    default: () => ({
      courseName: '',
      enrollmentNo: '',
      amount: '0.00'
    })
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const router = useRouter();
const userStore = useUserStore();

const visible = ref(false);
const loading = ref(false);
const selectedMethod = ref('wechat');
const userBalance = ref('0.00');

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  visible.value = val;
  if (val) {
    loadUserBalance();
  }
});

// 监听 visible 变化
watch(visible, (val) => {
  emit('update:modelValue', val);
});

// 加载用户余额
const loadUserBalance = async () => {
  try {
    const response = await getUserBalance();
    if (response.code === 200) {
      userBalance.value = (response.data || 0).toFixed(2);
    }
  } catch (error) {
    console.error('获取用户余额失败:', error);
    const balance = userStore.userInfo?.balance || 0;
    userBalance.value = balance.toFixed(2);
  }
};

// 确认支付
const handleConfirmPay = async () => {
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式');
    return;
  }

  loading.value = true;

  try {
    // 根据选择的支付方式处理
    switch (selectedMethod.value) {
      case 'wechat':
        await payWithWechat();
        break;
      case 'alipay':
        await payWithAlipay();
        break;
      case 'balance':
        await payWithBalance();
        break;
      default:
        ElMessage.error('不支持的支付方式');
        loading.value = false;
    }
  } catch (error) {
    console.error('支付失败:', error);
    ElMessage.error(error.message || '支付失败');
    loading.value = false;
  }
};

// 微信支付 - 跳转到微信支付页面
const payWithWechat = async () => {
  visible.value = false;
  router.push({
    path: '/payment/wechat',
    query: {
      businessNo: props.enrollmentId,
      businessType: 'course',
      businessName: props.orderInfo.courseName,
      amount: props.orderInfo.amount
    }
  });
};

// 支付宝支付 - 跳转到支付宝支付页面
const payWithAlipay = async () => {
  visible.value = false;
  router.push({
    path: '/payment/alipay',
    query: {
      businessNo: props.enrollmentId,
      businessType: 'course',
      businessName: props.orderInfo.courseName,
      amount: props.orderInfo.amount
    }
  });
};

// 余额支付
const payWithBalance = async () => {
  try {
    // 检查余额是否足够
    const balance = parseFloat(userBalance.value);
    const amount = parseFloat(props.orderInfo.amount);

    if (balance < amount) {
      ElMessage.error('余额不足，请充值后再试');
      loading.value = false;
      return;
    }

    // 二次确认
    await ElMessageBox.confirm(
      `确认使用余额支付 ¥${props.orderInfo.amount} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const response = await payEnrollment(props.enrollmentId, {
      paymentMethod: 2,
      paymentType: ''
    });

    if (response.code === 200) {
      ElMessage.success('支付成功！');
      visible.value = false;

      // 触发成功事件
      emit('success');

      // 跳转到支付结果页
      setTimeout(() => {
        router.push({
          path: '/payment/result',
          query: {
            status: 'success',
            orderNo: props.orderInfo.enrollmentNo,
            amount: props.orderInfo.amount,
            paymentMethod: '余额支付',
            type: 'course'
          }
        });
      }, 500);
    } else {
      ElMessage.error(response.message || '支付失败');
      loading.value = false;
    }
  } catch (error) {
    if (error === 'cancel') {
      loading.value = false;
      return;
    }
    throw error;
  }
};

// 关闭弹窗
const handleClose = () => {
  visible.value = false;
  loading.value = false;
  selectedMethod.value = 'wechat';
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.payment-dialog-content {
  .section-title {
    margin: 0 0 $spacing-5 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  .order-info {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-4;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        font-size: $font-size-sm;
        color: $text-secondary;
      }

      .value {
        font-size: $font-size-base;
        color: $text-primary;

        &.amount {
          font-size: $font-size-2xl;
          font-weight: $font-weight-bold;
          color: $error;
        }
      }
    }
  }

  :deep(.el-divider) {
    margin: $spacing-6 0;
  }

  .payment-methods {
    .method-group {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: $spacing-3;

      .method-item {
        border: 2px solid $border-color;
        border-radius: $radius-md;
        padding: $spacing-4;
        cursor: pointer;
        transition: all $duration-fast $ease-out;

        &:hover {
          border-color: $primary;
          background: $gray-50;
        }

        &.active {
          border-color: $primary;
          background: rgba($primary, 0.05);
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
          gap: $spacing-4;

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
              color: $white;
            }

            &.alipay {
              background: #1677FF;
              color: $white;
            }

            &.balance {
              background: $error;
              color: $white;
            }
          }

          .method-info {
            flex: 1;

            h4 {
              margin: 0 0 $spacing-1 0;
              font-size: $font-size-base;
              font-weight: $font-weight-semibold;
              color: $text-primary;
            }

            p {
              margin: 0;
              font-size: $font-size-sm;
              color: $text-secondary;
            }
          }
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-3;

  .el-button {
    @include button-base;
    min-width: 100px;

    &.el-button--primary {
      @include button-primary;
    }
  }
}

:deep(.el-dialog) {
  border-radius: $radius-lg;
  box-shadow: $shadow-modal;

  .el-dialog__header {
    padding: $spacing-6;
    border-bottom: 1px solid $border-color;

    .el-dialog__title {
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }
  }

  .el-dialog__body {
    padding: $spacing-6;
  }

  .el-dialog__footer {
    padding: $spacing-5 $spacing-6;
    border-top: 1px solid $border-color;
  }
}
</style>
