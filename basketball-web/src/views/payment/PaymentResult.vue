<template>
  <div class="payment-result-container">
    <el-card class="result-card">
      <!-- 支付结果展示 -->
      <div class="result-content">
        <!-- 成功状态 -->
        <div v-if="isSuccess" class="result-success">
          <el-icon :size="80" color="#67C23A"><SuccessFilled /></el-icon>
          <h2>支付成功</h2>
          <p class="result-message">您的订单已支付成功，感谢您的使用！</p>
        </div>

        <!-- 失败状态 -->
        <div v-else class="result-failed">
          <el-icon :size="80" color="#F56C6C"><CircleCloseFilled /></el-icon>
          <h2>支付失败</h2>
          <p class="result-message">很抱歉，支付未能完成，请重试或联系客服。</p>
        </div>

        <el-divider />

        <!-- 订单信息 -->
        <div class="order-info">
          <h3 class="section-title">订单信息</h3>

          <div class="info-item">
            <span class="label">支付单号:</span>
            <span class="value">{{ paymentNo || '-' }}</span>
          </div>

          <div class="info-item">
            <span class="label">商品名称:</span>
            <span class="value">{{ businessName || '-' }}</span>
          </div>

          <div class="info-item">
            <span class="label">支付金额:</span>
            <span class="value amount">¥{{ amount || '0.00' }}</span>
          </div>

          <div class="info-item">
            <span class="label">支付时间:</span>
            <span class="value">{{ paymentTime }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button size="large" @click="handleBackHome">返回首页</el-button>
          <el-button
            v-if="isSuccess"
            type="primary"
            size="large"
            @click="handleViewOrder"
          >
            查看订单
          </el-button>
          <el-button
            v-else
            type="primary"
            size="large"
            @click="handleRetry"
          >
            重新支付
          </el-button>
        </div>

        <!-- 温馨提示 -->
        <div class="tips-section">
          <el-alert
            :type="isSuccess ? 'success' : 'warning'"
            :closable="false"
            show-icon
          >
            <template #title>
              <div class="tips-content">
                <p v-if="isSuccess">• 您可以在"我的订单"中查看订单详情</p>
                <p v-if="isSuccess">• 如有疑问，请联系客服</p>
                <p v-if="!isSuccess">• 如果已完成支付但显示失败，请联系客服</p>
                <p v-if="!isSuccess">• 您也可以稍后在"我的订单"中继续支付</p>
              </div>
            </template>
          </el-alert>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

const paymentNo = ref('');
const amount = ref('0.00');
const businessName = ref('');
const status = ref('');

const isSuccess = computed(() => status.value === 'success');

const paymentTime = computed(() => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
});

onMounted(() => {
  loadPaymentResult();
});

const loadPaymentResult = () => {
  // 从路由参数获取支付结果信息
  const { status: paymentStatus, paymentNo: no, amount: amt, businessName: name } = route.query;

  if (!paymentStatus) {
    // 如果没有状态参数，返回首页
    router.replace('/');
    return;
  }

  status.value = paymentStatus;
  paymentNo.value = no || '';
  amount.value = amt || '0.00';
  businessName.value = name || '篮球馆服务';
};

const handleBackHome = () => {
  router.push('/');
};

const handleViewOrder = () => {
  // 跳转到订单列表页面
  router.push('/user/orders');
};

const handleRetry = () => {
  // 返回上一页重新选择支付方式
  router.back();
};
</script>

<style lang="scss" scoped>
.payment-result-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;

  .result-card {
    .result-content {
      .result-success,
      .result-failed {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 40px 20px;

        h2 {
          margin: 20px 0 12px 0;
          font-size: 24px;
          font-weight: 500;
        }

        .result-message {
          margin: 0;
          font-size: 14px;
          color: #606266;
          text-align: center;
        }
      }

      .result-success h2 {
        color: #67C23A;
      }

      .result-failed h2 {
        color: #F56C6C;
      }

      .order-info {
        padding: 20px 0;

        .section-title {
          margin: 0 0 20px 0;
          font-size: 18px;
          font-weight: 500;
          color: #303133;
        }

        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;
          padding: 12px;
          background: #F5F7FA;
          border-radius: 4px;

          .label {
            font-size: 14px;
            color: #606266;
          }

          .value {
            font-size: 14px;
            color: #303133;
            font-weight: 500;

            &.amount {
              font-size: 20px;
              font-weight: bold;
              color: #F56C6C;
            }
          }
        }
      }

      .action-buttons {
        display: flex;
        justify-content: center;
        gap: 16px;
        margin: 32px 0;

        .el-button {
          min-width: 140px;
        }
      }

      .tips-section {
        margin-top: 20px;

        .tips-content {
          p {
            margin: 6px 0;
            font-size: 13px;
            line-height: 1.6;
          }
        }
      }
    }
  }
}
</style>
