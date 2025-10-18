<template>
  <div class="booking-detail-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="page-title">预订详情</span>
      </template>
    </el-page-header>

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
      title="支付订单"
      width="400px"
    >
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="订单金额">
          <div class="pay-amount">¥{{ bookingDetail.actualPrice?.toFixed(2) || bookingDetail.totalPrice?.toFixed(2) }}</div>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="payForm.paymentMethod">
            <el-radio :label="1">在线支付</el-radio>
            <el-radio :label="2">余额支付</el-radio>
            <el-radio :label="3">会员卡</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">确认支付</el-button>
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
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Clock
} from '@element-plus/icons-vue';
import {
  getBookingDetail,
  payBooking,
  cancelBooking
} from '@/api/booking';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const bookingDetail = ref({});

// 支付相关
const payDialogVisible = ref(false);
const paying = ref(false);
const payForm = reactive({
  paymentMethod: 2
});

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

// 返回
const goBack = () => {
  router.back();
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
  payForm.paymentMethod = 2;
  payDialogVisible.value = true;
};

const confirmPay = async () => {
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
</script>

<style lang="scss" scoped>
.booking-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;

  .page-title {
    font-size: 18px;
    font-weight: 600;
  }

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
}
</style>
