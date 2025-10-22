<template>
  <div class="wechat-pay-container">
    <el-card class="payment-card">
      <div class="payment-header">
        <h2>微信扫码支付</h2>
        <div class="amount">
          <span class="label">支付金额:</span>
          <span class="value">¥{{ paymentInfo.amount }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 二维码展示 -->
      <QrCodeDisplay
        ref="qrCodeRef"
        :qr-code-url="qrCodeUrl"
        :loading="loading"
        :show-timer="true"
        :timeout="300"
        title="请使用微信扫码支付"
        description="打开微信，扫描二维码完成支付"
        loading-text="正在生成支付二维码..."
        @timeout="handleTimeout"
        @retry="createPayment"
      >
        <template #footer>
          <div class="payment-tips">
            <el-alert type="info" :closable="false" show-icon>
              <template #title>
                <div class="tips-content">
                  <p>• 请在5分钟内完成支付</p>
                  <p>• 支付完成后会自动跳转</p>
                </div>
              </template>
            </el-alert>
          </div>
        </template>
      </QrCodeDisplay>

      <!-- 支付状态轮询 -->
      <PaymentStatusPoller
        v-if="paymentNo"
        :payment-no="paymentNo"
        :query-fn="queryPayment"
        :interval="2000"
        :max-attempts="150"
        :auto-start="true"
        @success="handlePaymentSuccess"
        @failed="handlePaymentFailed"
        @timeout="handlePollingTimeout"
      />

      <!-- 操作按钮 -->
      <div class="payment-actions">
        <el-button size="large" @click="handleCancel">取消支付</el-button>
        <el-button type="primary" size="large" @click="checkPaymentStatus">
          支付遇到问题?
        </el-button>
        <el-button type="success" size="large" @click="handleMockPayment" v-if="paymentNo">
          模拟支付成功
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createPayment as createPaymentApi, queryPayment, mockPaymentSuccess } from '@/api/payment';
import QrCodeDisplay from '@/components/QrCodeDisplay.vue';
import PaymentStatusPoller from '@/components/PaymentStatusPoller.vue';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const qrCodeUrl = ref('');
const paymentNo = ref('');
const qrCodeRef = ref(null);

const paymentInfo = reactive({
  businessNo: '',
  businessType: '',
  businessName: '',
  amount: '0.00'
});

onMounted(() => {
  loadPaymentInfo();
  createPayment();
});

const loadPaymentInfo = () => {
  const { businessNo, businessType, businessName, amount } = route.query;

  if (!businessNo || !amount) {
    ElMessage.error('支付信息不完整');
    router.back();
    return;
  }

  paymentInfo.businessNo = businessNo;
  paymentInfo.businessType = businessType || 'booking';
  paymentInfo.businessName = businessName || '篮球馆服务';
  paymentInfo.amount = amount;
};

const createPayment = async () => {
  loading.value = true;
  qrCodeUrl.value = '';
  paymentNo.value = '';

  try {
    const res = await createPaymentApi({
      businessNo: paymentInfo.businessNo,
      businessType: paymentInfo.businessType,
      paymentType: 'wechat_native',
      amount: parseFloat(paymentInfo.amount),
      description: paymentInfo.businessName
    });

    if (res.code === 200) {
      qrCodeUrl.value = res.data.qrCodeUrl || res.data.codeUrl;
      paymentNo.value = res.data.paymentNo;

      ElMessage.success('支付二维码已生成');
    } else {
      ElMessage.error(res.message || '创建支付失败');
    }
  } catch (error) {
    console.error('创建微信支付失败:', error);
    ElMessage.error('创建支付失败,请重试');
  } finally {
    loading.value = false;
  }
};

const handlePaymentSuccess = (data) => {
  ElMessage.success('支付成功');

  // 跳转到支付结果页面
  router.replace({
    path: '/payment/result',
    query: {
      status: 'success',
      paymentNo: paymentNo.value,
      amount: paymentInfo.amount,
      businessName: paymentInfo.businessName,
      type: paymentInfo.businessType
    }
  });
};

const handlePaymentFailed = (data) => {
  ElMessage.error('支付失败');

  router.replace({
    path: '/payment/result',
    query: {
      status: 'failed',
      paymentNo: paymentNo.value,
      amount: paymentInfo.amount,
      type: paymentInfo.businessType
    }
  });
};

const handleTimeout = () => {
  ElMessageBox.confirm(
    '二维码已过期,是否重新生成?',
    '提示',
    {
      confirmButtonText: '重新生成',
      cancelButtonText: '取消支付',
      type: 'warning'
    }
  ).then(() => {
    createPayment();
  }).catch(() => {
    handleCancel();
  });
};

const handlePollingTimeout = () => {
  ElMessageBox.confirm(
    '支付超时,请确认是否已完成支付',
    '支付超时',
    {
      confirmButtonText: '已完成支付',
      cancelButtonText: '取消支付',
      type: 'warning'
    }
  ).then(() => {
    checkPaymentStatus();
  }).catch(() => {
    handleCancel();
  });
};

const checkPaymentStatus = async () => {
  if (!paymentNo.value) {
    ElMessage.warning('支付订单不存在');
    return;
  }

  try {
    const res = await queryPayment(paymentNo.value);

    if (res.code === 200) {
      const status = res.data.status;

      if (status === 2) {
        handlePaymentSuccess(res.data);
      } else if (status === 3 || status === 4 || status === 5) {
        handlePaymentFailed(res.data);
      } else {
        ElMessage.info('支付尚未完成,请继续扫码支付');
      }
    }
  } catch (error) {
    console.error('查询支付状态失败:', error);
    ElMessage.error('查询失败,请稍后重试');
  }
};

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消支付吗?',
      '取消支付',
      {
        confirmButtonText: '确定',
        cancelButtonText: '继续支付',
        type: 'warning'
      }
    );

    router.back();
  } catch (error) {
    // 用户选择继续支付
  }
};

const handleMockPayment = async () => {
  if (!paymentNo.value) {
    ElMessage.warning('支付订单不存在');
    return;
  }

  try {
    const res = await mockPaymentSuccess(paymentNo.value);

    if (res.code === 200) {
      ElMessage.success('模拟支付成功');
      // 等待一下让后端处理完成
      setTimeout(() => {
        checkPaymentStatus();
      }, 500);
    } else {
      ElMessage.error(res.message || '模拟支付失败');
    }
  } catch (error) {
    console.error('模拟支付失败:', error);
    ElMessage.error('模拟支付失败');
  }
};

onBeforeUnmount(() => {
  qrCodeRef.value?.stopCountdown();
});
</script>

<style lang="scss" scoped>
.wechat-pay-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;

  .payment-card {
    .payment-header {
      text-align: center;

      h2 {
        margin: 0 0 16px 0;
        font-size: 20px;
        font-weight: 500;
        color: #303133;
      }

      .amount {
        .label {
          font-size: 14px;
          color: #606266;
          margin-right: 8px;
        }

        .value {
          font-size: 28px;
          font-weight: bold;
          color: #F56C6C;
        }
      }
    }

    .payment-tips {
      width: 100%;
      margin-top: 20px;

      .tips-content {
        p {
          margin: 4px 0;
          font-size: 13px;
          color: #606266;
        }
      }
    }

    .payment-actions {
      display: flex;
      justify-content: center;
      gap: 12px;
      margin-top: 32px;
      padding-top: 20px;
      border-top: 1px solid #EBEEF5;

      .el-button {
        min-width: 140px;
      }
    }
  }
}
</style>
