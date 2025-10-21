<template>
  <div class="booking-detail-container">
    <BackButton text="返回" />

    <div v-loading="loading" class="detail-content">
      <el-card class="status-card">
        <div class="status-content">
          <el-icon :size="48" :color="getStatusColor(bookingDetail.status)">
            <component :is="getStatusIcon(bookingDetail.status)" />
          </el-icon>
          <div class="status-info">
            <h2>{{ getStatusText(bookingDetail.status) }}</h2>
            <p class="order-no">订单号: {{ bookingDetail.bookingNo }}</p>
          </div>
        </div>
      </el-card>

      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>场地信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="场地名称">
            {{ bookingDetail.venueName }}
          </el-descriptions-item>
          <el-descriptions-item label="场地位置">
            {{ bookingDetail.venueLocation }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>预订信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预订日期">
            {{ bookingDetail.bookingDate }}
          </el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ bookingDetail.timeSlot }}
          </el-descriptions-item>
          <el-descriptions-item label="时长">
            {{ bookingDetail.duration }} 小时
          </el-descriptions-item>
          <el-descriptions-item label="预订类型">
            {{ bookingDetail.bookingType === 1 ? '按时段' : '包场' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系人" v-if="bookingDetail.contactName">
            {{ bookingDetail.contactName }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话" v-if="bookingDetail.contactPhone">
            {{ bookingDetail.contactPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="人数" v-if="bookingDetail.peopleCount">
            {{ bookingDetail.peopleCount }} 人
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="bookingDetail.remark">
            {{ bookingDetail.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>费用信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="单价">
            ¥{{ bookingDetail.price?.toFixed(2) }}/小时
          </el-descriptions-item>
          <el-descriptions-item label="总价">
            ¥{{ bookingDetail.totalPrice?.toFixed(2) }}
          </el-descriptions-item>
          <el-descriptions-item label="优惠金额">
            ¥{{ bookingDetail.discountAmount?.toFixed(2) || '0.00' }}
          </el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="price-highlight">¥{{ bookingDetail.actualPrice?.toFixed(2) || bookingDetail.totalPrice?.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式" v-if="bookingDetail.payMethodName">
            {{ bookingDetail.payMethodName }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>时间信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="创建时间">
            {{ bookingDetail.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="bookingDetail.payTime">
            {{ bookingDetail.payTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间" v-if="bookingDetail.cancelTime">
            {{ bookingDetail.cancelTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2" v-if="bookingDetail.cancelReason">
            {{ bookingDetail.cancelReason }}
          </el-descriptions-item>
          <el-descriptions-item label="过期时间" v-if="bookingDetail.expireTime && bookingDetail.status === 0">
            {{ bookingDetail.expireTime }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <div class="action-buttons">
        <el-button
          v-if="bookingDetail.status === 0"
          type="success"
          size="large"
          @click="handlePay"
        >
          立即支付
        </el-button>
        <el-button
          v-if="bookingDetail.status === 0 || bookingDetail.status === 1"
          type="danger"
          size="large"
          @click="handleCancel"
        >
          取消预订
        </el-button>
      </div>
    </div>

    <!-- 支付对话框 -->
    <el-dialog
      v-model="payDialogVisible"
      title="选择支付方式"
      width="600px"
      :close-on-click-modal="false"
    >
      <!-- 订单金额显示 -->
      <div class="payment-amount-section">
        <div class="amount-label">订单金额</div>
        <div class="amount-value">¥{{ bookingDetail.actualPrice?.toFixed(2) || bookingDetail.totalPrice?.toFixed(2) }}</div>
      </div>

      <el-divider />

      <!-- 支付方式选择 -->
      <div class="payment-methods-section">
        <div class="section-title">请选择支付方式</div>

        <!-- 微信支付 -->
        <div
          class="payment-method-card"
          :class="{ active: payForm.paymentType === 'wechat_native' }"
          @click="selectPaymentMethod('wechat_native')"
        >
          <div class="method-icon wechat-icon">
            <svg viewBox="0 0 1024 1024" width="32" height="32">
              <path d="M664.250054 368.541681c10.015098 0 19.892049 0.732687 29.67281 1.795902-26.647917-122.810047-159.358451-214.077703-310.826188-214.077703-169.353083 0-308.085774 114.232694-308.085774 259.274068 0 83.708494 46.165436 152.460344 123.281791 205.78483l-30.80868 91.730191 107.688651-53.455469c38.558178 7.53665 69.459978 15.308661 107.924012 15.308661 9.66308 0 19.230993-0.470721 28.752858-1.225921-6.025227-20.36584-9.521864-41.723264-9.521864-63.862493C402.328693 476.632491 517.908058 368.541681 664.250054 368.541681zM498.62897 285.87389c23.200398 0 38.557154 15.120372 38.557154 38.061874 0 22.846334-15.356756 38.156018-38.557154 38.156018-23.107277 0-46.260603-15.309684-46.260603-38.156018C452.368366 300.994262 475.522716 285.87389 498.62897 285.87389zM283.016307 362.090758c-23.107277 0-46.402843-15.309684-46.402843-38.156018 0-22.941502 23.295566-38.061874 46.402843-38.061874 23.081695 0 38.46301 15.120372 38.46301 38.061874C321.479317 346.782098 306.098002 362.090758 283.016307 362.090758zM945.448458 606.151333c0-121.888048-123.258255-221.236753-261.683954-221.236753-146.57838 0-262.015505 99.348706-262.015505 221.236753 0 122.06508 115.437126 221.200938 262.015505 221.200938 30.66644 0 61.617359-7.609305 92.423993-15.262612l84.513836 45.786813-23.178909-76.17082C899.379213 735.776599 945.448458 674.90216 945.448458 606.151333zM598.803483 567.994292c-15.332197 0-30.807656-15.096836-30.807656-30.501688 0-15.190981 15.47546-30.477129 30.807656-30.477129 23.295566 0 38.558178 15.286148 38.558178 30.477129C637.361661 552.897456 622.099049 567.994292 598.803483 567.994292zM768.25071 567.994292c-15.213493 0-30.594809-15.096836-30.594809-30.501688 0-15.190981 15.381315-30.477129 30.594809-30.477129 23.107277 0 38.558178 15.286148 38.558178 30.477129C806.808888 552.897456 791.357987 567.994292 768.25071 567.994292z" fill="#00C800"></path>
            </svg>
          </div>
          <div class="method-info">
            <div class="method-name">微信支付</div>
            <div class="method-desc">推荐使用微信扫码支付</div>
          </div>
          <div class="method-check">
            <el-icon v-if="payForm.paymentType === 'wechat_native'" color="#07C160" :size="24">
              <SuccessFilled />
            </el-icon>
          </div>
        </div>

        <!-- 支付宝支付 -->
        <div
          class="payment-method-card"
          :class="{ active: payForm.paymentType === 'alipay_page' }"
          @click="selectPaymentMethod('alipay_page')"
        >
          <div class="method-icon alipay-icon">
            <svg viewBox="0 0 1024 1024" width="32" height="32">
              <path d="M1024 701.9v162.8c0 88.5-71.7 160.2-160.2 160.2H160.2C71.7 1024.9 0 953.2 0 864.7V159.9C0 71.4 71.7-0.3 160.2-0.3h703.6c88.5 0 160.2 71.7 160.2 160.2v390.8c-51.8-23.8-155.9-67.8-289.8-67.8-228.9 0-378.1 112.1-430.5 210.5l-0.5 0.9 490.4 199.3c47.6-63.3 118.8-127.1 230.4-151.6z m-514.4-68.9c-0.3 0-0.5 0-0.8 0.1-0.3 0-0.5 0.1-0.8 0.1-44.2 8.2-89.3 22.6-134.4 42.8-9.5 4.3-19 8.9-28.4 13.8-52.2-88.7-152.9-148.3-267.3-148.3-15.7 0-31.1 1.2-46.2 3.4V232.3h585.6v401.6c-35.6-0.3-71.7 0-107.7 0.1z m-278.8-236c-51.7 0-93.6 41.9-93.6 93.6s41.9 93.6 93.6 93.6 93.6-41.9 93.6-93.6-41.9-93.6-93.6-93.6z" fill="#1296DB"></path>
            </svg>
          </div>
          <div class="method-info">
            <div class="method-name">支付宝支付</div>
            <div class="method-desc">支持支付宝扫码或跳转支付</div>
          </div>
          <div class="method-check">
            <el-icon v-if="payForm.paymentType === 'alipay_page'" color="#1677FF" :size="24">
              <SuccessFilled />
            </el-icon>
          </div>
        </div>

        <!-- 余额支付 -->
        <div
          class="payment-method-card"
          :class="{ active: payForm.paymentMethod === 2 }"
          @click="selectPaymentMethod('balance')"
        >
          <div class="method-icon balance-icon">
            <el-icon :size="32"><Wallet /></el-icon>
          </div>
          <div class="method-info">
            <div class="method-name">余额支付</div>
            <div class="method-desc">当前余额: ¥0.00</div>
          </div>
          <div class="method-check">
            <el-icon v-if="payForm.paymentMethod === 2" color="#F56C6C" :size="24">
              <SuccessFilled />
            </el-icon>
          </div>
        </div>

        <!-- 现场支付 -->
        <div
          class="payment-method-card"
          :class="{ active: payForm.paymentMethod === 4 }"
          @click="selectPaymentMethod('onsite')"
        >
          <div class="method-icon onsite-icon">
            <el-icon :size="32"><Money /></el-icon>
          </div>
          <div class="method-info">
            <div class="method-name">现场支付</div>
            <div class="method-desc">到场后现金或刷卡支付</div>
          </div>
          <div class="method-check">
            <el-icon v-if="payForm.paymentMethod === 4" color="#909399" :size="24">
              <SuccessFilled />
            </el-icon>
          </div>
        </div>
      </div>

      <!-- 二维码显示区域 -->
      <div v-if="showQRCode" class="qr-code-container">
        <div class="qr-code-header">
          <span>请使用{{ payForm.paymentType === 'wechat_native' ? '微信' : '支付宝' }}扫码支付</span>
          <el-button type="text" @click="refreshQRCode" :loading="refreshing">
            刷新二维码
          </el-button>
        </div>
        <div class="qr-code-content">
          <img v-if="qrCodeUrl" :src="qrCodeUrl" alt="支付二维码" class="qr-code-img" />
          <div v-else class="qr-code-placeholder">
            <el-empty description="生成二维码中..." />
          </div>
        </div>
        <div class="qr-code-status">
          <p>支付单号: {{ paymentResult?.paymentNo }}</p>
          <p>订单号: {{ bookingDetail.bookingNo }}</p>
          <p v-if="paymentResult?.expireTime" class="expire-time">
            二维码有效期至: {{ formatExpireTime(paymentResult.expireTime) }}
          </p>
        </div>

        <!-- 模拟扫码区域（仅开发环境显示） -->
        <div class="mock-scan-area">
          <el-divider>开发环境模拟功能</el-divider>
          <div class="mock-scan-buttons">
            <el-button type="success" @click="mockPaymentSuccess" :disabled="paymentStatus === 'success'">
              模拟扫码支付成功
            </el-button>
            <el-button type="danger" @click="mockPaymentFail" :disabled="paymentStatus === 'failed'">
              模拟扫码支付失败
            </el-button>
          </div>
          <p class="mock-tip">点击按钮模拟用户扫码支付结果</p>
        </div>
      </div>

      <!-- 支付状态显示 -->
      <div v-if="showPaymentStatus" class="payment-status">
        <div v-if="paymentStatus === 'paying'" class="status-paying">
          <el-icon class="status-icon is-loading"><loading /></el-icon>
          <span>等待支付完成...</span>
        </div>
        <div v-else-if="paymentStatus === 'success'" class="status-success">
          <el-icon class="status-icon"><success-filled /></el-icon>
          <span>支付成功！</span>
        </div>
        <div v-else-if="paymentStatus === 'failed'" class="status-failed">
          <el-icon class="status-icon"><circle-close-filled /></el-icon>
          <span>支付失败，请重试</span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="payDialogVisible = false">取消</el-button>
          <el-button
            v-if="!showQRCode"
            type="primary"
            size="large"
            @click="handleConfirmPay"
            :loading="paying"
            :disabled="!payForm.paymentType && payForm.paymentMethod !== 2 && payForm.paymentMethod !== 4"
          >
            确认支付
          </el-button>
          <el-button
            v-if="showQRCode"
            type="danger"
            size="large"
            @click="cancelPayment"
            :loading="canceling"
          >
            取消支付
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 取消对话框 -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="取消预订"
      width="400px"
    >
      <el-form :model="cancelForm" label-width="100px">
        <el-form-item label="取消原因" required>
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            :rows="4"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel" :loading="canceling">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Clock,
  Loading,
  Wallet,
  Money
} from '@element-plus/icons-vue';
import {
  getBookingDetail,
  payBooking,
  cancelBooking
} from '@/api/booking';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const bookingDetail = ref({});

// 支付相关
const payDialogVisible = ref(false);
const paying = ref(false);
const payForm = reactive({
  paymentMethod: 2,
  paymentType: 'wechat_native'
});

// 二维码相关
const showQRCode = ref(false);
const qrCodeUrl = ref('');
const refreshing = ref(false);
const paymentResult = ref(null);

// 支付状态轮询
const paymentPolling = ref(null);
const paymentStatus = ref(''); // paying, success, failed

// 计算属性
const isOnlinePayment = () => payForm.paymentMethod === 1;

const showPaymentStatus = () => {
  return paymentStatus.value && paymentStatus.value !== '';
};

// 取消相关
const cancelDialogVisible = ref(false);
const canceling = ref(false);
const cancelForm = reactive({
  cancelReason: ''
});

// 获取预订详情
const fetchBookingDetail = async () => {
  loading.value = true;
  try {
    const res = await getBookingDetail(route.params.id);
    bookingDetail.value = res.data;
  } catch (error) {
    ElMessage.error(error.message || '获取预订详情失败');
    router.back();
  } finally {
    loading.value = false;
  }
};


// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    0: Clock,
    1: SuccessFilled,
    2: CircleCloseFilled,
    3: SuccessFilled,
    4: CircleCloseFilled,
    5: WarningFilled
  };
  return iconMap[status] || Clock;
};

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: '#E6A23C',
    1: '#67C23A',
    2: '#909399',
    3: '#67C23A',
    4: '#909399',
    5: '#F56C6C'
  };
  return colorMap[status] || '#909399';
};

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已完成',
    4: '已退款',
    5: '超时取消'
  };
  return textMap[status] || '未知';
};

// 支付
const handlePay = () => {
  payForm.paymentMethod = 1;

  payForm.paymentType = 'wechat_native';
  showQRCode.value = false;
  qrCodeUrl.value = '';
  paymentResult.value = null;
  paymentStatus.value = '';
  payDialogVisible.value = true;
};

// 选择支付方式
const selectPaymentMethod = (method) => {
  showQRCode.value = false;
  qrCodeUrl.value = '';
  paymentResult.value = null;
  paymentStatus.value = '';

  if (method === 'wechat_native') {
    payForm.paymentMethod = 1;
    payForm.paymentType = 'wechat_native';
  } else if (method === 'alipay_page') {
    payForm.paymentMethod = 1;
    payForm.paymentType = 'alipay_page';
  } else if (method === 'balance') {
    payForm.paymentMethod = 2;
    payForm.paymentType = '';
  } else if (method === 'onsite') {
    payForm.paymentMethod = 4;
    payForm.paymentType = '';
  }
};

// 确认支付
const handleConfirmPay = () => {
  if (payForm.paymentMethod === 1) {
    // 在线支付，生成二维码
    generateQRCode();
  } else {
    // 其他支付方式，直接确认
    confirmPay();
  }
};

// 支付方式改变
const handlePaymentMethodChange = (value) => {
  if (value === 1) {
    showQRCode.value = false;
    qrCodeUrl.value = '';
    paymentResult.value = null;
    paymentStatus.value = '';
  }
};

// 格式化过期时间
const formatExpireTime = (expireTime) => {
  if (!expireTime) return '';
  const date = new Date(expireTime);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 轮询支付状态
const startPaymentPolling = () => {
  if (paymentPolling.value) {
    clearInterval(paymentPolling.value);
  }

  paymentStatus.value = 'paying';
  paymentPolling.value = setInterval(async () => {
    try {
      const res = await getBookingDetail(bookingDetail.value.id);
      if (res.data.status === 1) { // 已支付
        paymentStatus.value = 'success';
        clearInterval(paymentPolling.value);
        payDialogVisible.value = false;
        fetchBookingDetail();
        ElMessage.success('支付成功！');
      } else if (res.data.status === 5) { // 超时取消
        paymentStatus.value = 'failed';
        clearInterval(paymentPolling.value);
        ElMessage.error('支付超时，请重新尝试');
      }
    } catch (error) {
      // 忽略轮询中的错误
    }
  }, 3000);
};

// 生成二维码
const generateQRCode = async () => {
  refreshing.value = true;
  try {
    const res = await payBooking(bookingDetail.value.id, {
      paymentMethod: payForm.paymentMethod,
      paymentType: payForm.paymentType
    });

    paymentResult.value = res.data;

    // 如果是在线支付，显示二维码
    if (payForm.paymentMethod === 1 && res.data.qrCodeUrl) {
      // 生成模拟二维码（实际项目中应该是后端返回的真实二维码）
      if (res.data.qrCodeUrl.startsWith('data:')) {
        qrCodeUrl.value = res.data.qrCodeUrl;
      } else {
        // 模拟二维码，生成一个占位图
        qrCodeUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${res.data.paymentNo}`;
      }

      showQRCode.value = true;
      startPaymentPolling();
    } else {
      // 非在线支付，直接处理
      if (payForm.paymentMethod === 2) {
        paymentStatus.value = 'success';
        payDialogVisible.value = false;
        fetchBookingDetail();
      } else if (payForm.paymentMethod === 3) {
        paymentStatus.value = 'success';
        payDialogVisible.value = false;
        fetchBookingDetail();
      } else if (payForm.paymentMethod === 4) {
        ElMessage.success('现场支付订单已确认');
        payDialogVisible.value = false;
        fetchBookingDetail();
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '生成支付二维码失败');
    showQRCode.value = false;
  } finally {
    refreshing.value = false;
  }
};

// 刷新二维码
const refreshQRCode = async () => {
  await generateQRCode();
};

// 取消支付
const cancelPayment = () => {
  if (paymentPolling.value) {
    clearInterval(paymentPolling.value);
  }
  payDialogVisible.value = false;
  showQRCode.value = false;
  paymentStatus.value = '';
};

// 模拟支付成功（调用mock接口）
const mockPaymentSuccess = async () => {
  try {
    // 调用mock支付成功接口
    await fetch(`http://localhost:8080/api/mock/payment/scan-success/${paymentResult.value.paymentNo}`, {
      method: 'POST'
    });
    ElMessage.success('模拟支付成功！支付状态已更新');
  } catch (error) {
    ElMessage.error('模拟支付失败: ' + error.message);
  }
};

// 模拟支付失败
const mockPaymentFail = async () => {
  try {
    // 调用mock支付失败接口
    await fetch(`http://localhost:8080/api/mock/payment/scan-fail/${paymentResult.value.paymentNo}`, {
      method: 'POST'
    });
    ElMessage.success('模拟支付失败！支付状态已更新');
  } catch (error) {
    ElMessage.error('模拟支付失败: ' + error.message);
  }
};

const confirmPay = async () => {
  if (isOnlinePayment()) {
    await generateQRCode();
  } else {
    paying.value = true;
    try {
      await payBooking(bookingDetail.value.id, {
        paymentMethod: payForm.paymentMethod
      });
      ElMessage.success('支付成功');
      payDialogVisible.value = false;
      fetchBookingDetail();
    } catch (error) {
      ElMessage.error(error.message || '支付失败');
    } finally {
      paying.value = false;
    }
  }
};

// 取消预订
const handleCancel = () => {
  cancelForm.cancelReason = '';
  cancelDialogVisible.value = true;
};

const confirmCancel = async () => {
  if (!cancelForm.cancelReason.trim()) {
    ElMessage.warning('请输入取消原因');
    return;
  }

  canceling.value = true;
  try {
    await cancelBooking(bookingDetail.value.id, {
      cancelReason: cancelForm.cancelReason
    });
    ElMessage.success('取消成功');
    cancelDialogVisible.value = false;
    fetchBookingDetail();
  } catch (error) {
    ElMessage.error(error.message || '取消失败');
  } finally {
    canceling.value = false;
  }
};

onMounted(() => {
  fetchBookingDetail();
});

// 组件销毁时清理轮询
onUnmounted(() => {
  if (paymentPolling.value) {
    clearInterval(paymentPolling.value);
  }
});
</script>

<style lang="scss" scoped>
.booking-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;

  .detail-content {
    margin-top: 20px;

    .status-card {
      margin-bottom: 20px;

      .status-content {
        display: flex;
        align-items: center;
        gap: 20px;
        padding: 20px;

        .status-info {
          h2 {
            font-size: 24px;
            font-weight: 600;
            margin: 0 0 8px 0;
          }

          .order-no {
            font-size: 14px;
            color: #666;
            margin: 0;
          }
        }
      }
    }

    .info-card {
      margin-bottom: 20px;

      .card-header {
        font-size: 16px;
        font-weight: 600;
      }

      .price-highlight {
        font-size: 18px;
        font-weight: 700;
        color: #f56c6c;
      }
    }

    .action-buttons {
      display: flex;
      justify-content: center;
      gap: 16px;
      margin-top: 30px;
    }
  }

  .pay-amount {
    font-size: 28px;
    font-weight: 700;
    color: #f56c6c;
  }

  // 二维码样式
  .qr-code-container {
    margin-top: 20px;
    text-align: center;

    .qr-code-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      font-size: 14px;
      font-weight: 500;
    }

    .qr-code-content {
      display: flex;
      justify-content: center;
      margin: 20px 0;

      .qr-code-img {
        width: 200px;
        height: 200px;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .qr-code-placeholder {
        width: 200px;
        height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px dashed #d9d9d9;
        border-radius: 8px;
        background-color: #fafafa;
      }
    }

    .qr-code-status {
      text-align: left;
      font-size: 12px;
      color: #666;

      p {
        margin: 5px 0;
        line-height: 1.5;
      }

      .expire-time {
        color: #f56c6c;
        font-weight: 500;
      }
    }

    // 模拟扫码区域样式
    .mock-scan-area {
      margin-top: 20px;
      padding: 15px;
      background-color: #f8f9fa;
      border-radius: 8px;

      .mock-scan-buttons {
        display: flex;
        gap: 10px;
        justify-content: center;
        margin-bottom: 10px;
      }

      .mock-tip {
        text-align: center;
        font-size: 12px;
        color: #999;
        margin: 0;
      }
    }
  }

  // 支付状态样式
  .payment-status {
    text-align: center;
    padding: 20px 0;

    .status-paying,
    .status-success,
    .status-failed {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;

      .status-icon {
        font-size: 48px;

        &.is-loading {
          animation: rotating 2s linear infinite;
        }

        &.status-success {
          color: #67c23a;
        }

        &.status-failed {
          color: #f56c6c;
        }
      }

      span {
        font-size: 16px;
        font-weight: 500;
      }
    }
  }

  // 新支付对话框样式
  .payment-amount-section {
    text-align: center;
    padding: 20px 0;

    .amount-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .amount-value {
      font-size: 32px;
      font-weight: bold;
      color: #F56C6C;
    }
  }

  .payment-methods-section {
    .section-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 16px;
    }

    .payment-method-card {
      display: flex;
      align-items: center;
      padding: 16px;
      margin-bottom: 12px;
      border: 2px solid #DCDFE6;
      border-radius: 8px;
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

      .method-icon {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        flex-shrink: 0;
        margin-right: 16px;

        &.wechat-icon {
          background: #E8F5E9;
        }

        &.alipay-icon {
          background: #E3F2FD;
        }

        &.balance-icon {
          background: #FFEBEE;
          color: #F56C6C;
        }

        &.onsite-icon {
          background: #F5F5F5;
          color: #909399;
        }
      }

      .method-info {
        flex: 1;

        .method-name {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }

        .method-desc {
          font-size: 13px;
          color: #909399;
        }
      }

      .method-check {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style>
