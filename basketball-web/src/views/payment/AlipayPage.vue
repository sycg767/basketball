<template>
  <div class="alipay-page-container">
    <el-card class="payment-card">
      <div class="payment-header">
        <h2>支付宝支付</h2>
        <div class="amount">
          <span class="label">支付金额:</span>
          <span class="value">¥{{ paymentInfo.amount }}</span>
        </div>
      </div>

      <el-divider />

      <div v-if="loading" class="loading-box">
        <el-icon class="is-loading" :size="48"><Loading /></el-icon>
        <p>正在生成支付二维码...</p>
      </div>

      <div v-else-if="paymentUrl" class="payment-content">
        <div class="qrcode-box">
          <div class="qrcode-image">
            <img :src="paymentUrl" alt="支付宝支付二维码" />
          </div>
          <h3>请使用支付宝扫码支付</h3>
          <p>打开支付宝APP，扫描二维码完成支付</p>

          <el-alert type="info" :closable="false" show-icon class="tips">
            <template #title>
              <div class="tips-content">
                <p>• 请在5分钟内完成支付</p>
                <p>• 支付完成后会自动跳转</p>
              </div>
            </template>
          </el-alert>
        </div>

        <!-- 支付状态轮询 -->
        <PaymentStatusPoller
          v-if="paymentNo"
          :payment-no="paymentNo"
          :query-fn="queryPayment"
          :interval="3000"
          :max-attempts="100"
          :auto-start="true"
          @success="handlePaymentSuccess"
          @failed="handlePaymentFailed"
        />

        <!-- 操作按钮 -->
        <div class="payment-actions">
          <el-button size="large" @click="handleCancel">取消支付</el-button>
          <el-button type="primary" size="large" @click="confirmPayment">
            支付遇到问题?
          </el-button>
          <el-button type="success" size="large" @click="handleMockPayment" v-if="paymentNo">
            模拟支付成功
          </el-button>
        </div>
      </div>

      <div v-else class="error-box">
        <el-icon :size="64" color="#F56C6C"><CircleCloseFilled /></el-icon>
        <p>创建支付失败</p>
        <el-button type="primary" @click="createPayment">重试</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Loading, InfoFilled, CircleCloseFilled } from '@element-plus/icons-vue';
import { createPayment as createPaymentApi, queryPayment, mockPaymentSuccess } from '@/api/payment';
import PaymentStatusPoller from '@/components/PaymentStatusPoller.vue';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const paymentUrl = ref('');
const paymentNo = ref('');

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
  paymentUrl.value = '';
  paymentNo.value = '';

  try {
    const res = await createPaymentApi({
      businessNo: paymentInfo.businessNo,
      businessType: paymentInfo.businessType,
      paymentType: 'alipay_page',
      amount: parseFloat(paymentInfo.amount),
      description: paymentInfo.businessName
    });

    if (res.code === 200) {
      paymentUrl.value = res.data.qrCodeUrl || res.data.paymentUrl || res.data.formHtml;
      paymentNo.value = res.data.paymentNo;

      ElMessage.success('支付订单已创建');

      // 如果是二维码URL，不自动跳转
      if (res.data.qrCodeUrl) {
        ElMessage.info('请扫描二维码完成支付');
      } else {
        // 自动跳转
        setTimeout(() => {
          jumpToAlipay();
        }, 2000);
      }
    } else {
      ElMessage.error(res.message || '创建支付失败');
    }
  } catch (error) {
    console.error('创建支付宝支付失败:', error);
    ElMessage.error('创建支付失败,请重试');
  } finally {
    loading.value = false;
  }
};

const jumpToAlipay = () => {
  if (!paymentUrl.value) {
    ElMessage.warning('支付地址无效');
    return;
  }

  // 如果是HTML表单,需要渲染并提交
  if (paymentUrl.value.includes('<form')) {
    const div = document.createElement('div');
    div.innerHTML = paymentUrl.value;
    document.body.appendChild(div);
    const form = div.querySelector('form');
    if (form) {
      form.submit();
    }
  } else {
    // 如果是URL,直接跳转
    window.open(paymentUrl.value, '_blank');
  }
};

const handlePaymentSuccess = (data) => {
  ElMessage.success('支付成功');

  router.replace({
    path: '/payment/result',
    query: {
      status: 'success',
      paymentNo: paymentNo.value,
      amount: paymentInfo.amount,
      businessName: paymentInfo.businessName
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
      amount: paymentInfo.amount
    }
  });
};

const confirmPayment = async () => {
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
      } else {
        ElMessage.warning('未检测到支付成功,请稍后再试');
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
        confirmPayment();
      }, 500);
    } else {
      ElMessage.error(res.message || '模拟支付失败');
    }
  } catch (error) {
    console.error('模拟支付失败:', error);
    ElMessage.error('模拟支付失败');
  }
};
</script>

<style lang="scss" scoped>
.alipay-page-container {
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

    .loading-box,
    .error-box {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 16px;
      min-height: 300px;

      p {
        font-size: 14px;
        color: #606266;
        margin: 0;
      }

      .el-icon {
        color: #409EFF;
      }
    }

    .payment-content {
      .qrcode-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 16px;
        padding: 40px 20px;

        .qrcode-image {
          width: 260px;
          height: 260px;
          padding: 20px;
          background: #fff;
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

          img {
            width: 100%;
            height: 100%;
            display: block;
          }
        }

        h3 {
          margin: 0;
          font-size: 18px;
          color: #303133;
        }

        p {
          margin: 0;
          font-size: 14px;
          color: #909399;
        }

        .tips {
          width: 100%;
          margin-top: 20px;
        }
      }

      .info-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 16px;
        padding: 40px 20px;

        h3 {
          margin: 0;
          font-size: 18px;
          color: #303133;
        }

        > p {
          margin: 0;
          font-size: 14px;
          color: #606266;
        }

        .manual-jump {
          margin-top: 20px;
          text-align: center;

          p {
            margin: 0 0 12px 0;
            font-size: 13px;
            color: #909399;
          }
        }

        .tips {
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
}
</style>
