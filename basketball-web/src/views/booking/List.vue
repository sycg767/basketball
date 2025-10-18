<template>
  <div class="booking-list-container">
    <h2 class="page-title">我的预订</h2>

    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-radio-group v-model="filterStatus" @change="handleFilterChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button :label="0">待支付</el-radio-button>
        <el-radio-button :label="1">已支付</el-radio-button>
        <el-radio-button :label="2">已取消</el-radio-button>
        <el-radio-button :label="3">已完成</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 预订列表 -->
    <div class="booking-list" v-loading="loading">
      <el-empty v-if="bookingList.length === 0" description="暂无预订记录" />

      <el-card v-for="booking in bookingList" :key="booking.id" class="booking-card">
        <div class="booking-header">
          <div class="order-info">
            <span class="order-no">订单号: {{ booking.bookingNo }}</span>
            <el-tag :type="getStatusType(booking.status)" size="small">
              {{ getStatusText(booking.status) }}
            </el-tag>
          </div>
          <div class="order-time">{{ booking.createTime }}</div>
        </div>

        <el-divider />

        <div class="booking-content">
          <div class="venue-info">
            <h3>{{ booking.venueName }}</h3>
            <p class="location">
              <el-icon><Location /></el-icon>
              {{ booking.venueLocation }}
            </p>
          </div>

          <div class="booking-details">
            <div class="detail-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ booking.bookingDate }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Clock /></el-icon>
              <span>{{ booking.timeSlot }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Timer /></el-icon>
              <span>{{ booking.duration }}小时</span>
            </div>
          </div>

          <div class="price-info">
            <div class="price-label">实付金额</div>
            <div class="price-value">¥{{ booking.actualPrice?.toFixed(2) || booking.totalPrice?.toFixed(2) }}</div>
          </div>
        </div>

        <el-divider />

        <div class="booking-actions">
          <el-button type="primary" link @click="viewDetail(booking.id)">
            查看详情
          </el-button>
          <el-button
            v-if="booking.status === 0"
            type="success"
            @click="handlePay(booking)"
          >
            立即支付
          </el-button>
          <el-button
            v-if="booking.status === 0 || booking.status === 1"
            type="danger"
            @click="handleCancel(booking)"
          >
            取消预订
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="pagination.total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchBookingList"
      @current-change="fetchBookingList"
      class="pagination"
    />

    <!-- 支付对话框 -->
    <el-dialog
      v-model="payDialogVisible"
      title="支付订单"
      width="400px"
    >
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="订单金额">
          <div class="pay-amount">¥{{ currentBooking?.actualPrice?.toFixed(2) || currentBooking?.totalPrice?.toFixed(2) }}</div>
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
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Location,
  Calendar,
  Clock,
  Timer
} from '@element-plus/icons-vue';
import {
  getMyBookingList,
  payBooking,
  cancelBooking
} from '@/api/booking';

const router = useRouter();
const loading = ref(false);
const filterStatus = ref('');
const bookingList = ref([]);

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

// 支付相关
const payDialogVisible = ref(false);
const paying = ref(false);
const currentBooking = ref(null);
const payForm = reactive({
  paymentMethod: 2 // 默认余额支付
});

// 取消相关
const cancelDialogVisible = ref(false);
const canceling = ref(false);
const cancelForm = reactive({
  cancelReason: ''
});

// 获取预订列表
const fetchBookingList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value !== '' ? filterStatus.value : undefined
    };

    const res = await getMyBookingList(params);
    bookingList.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error(error.message || '获取预订列表失败');
  } finally {
    loading.value = false;
  }
};

// 筛选状态变化
const handleFilterChange = () => {
  pagination.page = 1;
  fetchBookingList();
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'success',
    4: 'info',
    5: 'danger'
  };
  return typeMap[status] || 'info';
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

// 查看详情
const viewDetail = (id) => {
  router.push(`/booking/detail/${id}`);
};

// 支付
const handlePay = (booking) => {
  currentBooking.value = booking;
  payForm.paymentMethod = 2;
  payDialogVisible.value = true;
};

const confirmPay = async () => {
  paying.value = true;
  try {
    await payBooking(currentBooking.value.id, {
      paymentMethod: payForm.paymentMethod
    });
    ElMessage.success('支付成功');
    payDialogVisible.value = false;
    fetchBookingList();
  } catch (error) {
    ElMessage.error(error.message || '支付失败');
  } finally {
    paying.value = false;
  }
};

// 取消预订
const handleCancel = (booking) => {
  currentBooking.value = booking;
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
    await cancelBooking(currentBooking.value.id, {
      cancelReason: cancelForm.cancelReason
    });
    ElMessage.success('取消成功');
    cancelDialogVisible.value = false;
    fetchBookingList();
  } catch (error) {
    ElMessage.error(error.message || '取消失败');
  } finally {
    canceling.value = false;
  }
};

onMounted(() => {
  fetchBookingList();
});
</script>

<style lang="scss" scoped>
.booking-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1a202c;
    margin: 0 0 20px 0;
  }

  .filter-card {
    margin-bottom: 20px;
  }

  .booking-list {
    min-height: 400px;

    .booking-card {
      margin-bottom: 16px;

      .booking-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .order-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .order-no {
            font-size: 14px;
            color: #666;
          }
        }

        .order-time {
          font-size: 13px;
          color: #999;
        }
      }

      .booking-content {
        display: flex;
        gap: 24px;

        .venue-info {
          flex: 1;

          h3 {
            font-size: 18px;
            font-weight: 600;
            color: #1a202c;
            margin: 0 0 8px 0;
          }

          .location {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 14px;
            color: #666;
            margin: 0;
          }
        }

        .booking-details {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 8px;

          .detail-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #666;
          }
        }

        .price-info {
          text-align: right;

          .price-label {
            font-size: 13px;
            color: #999;
            margin-bottom: 4px;
          }

          .price-value {
            font-size: 24px;
            font-weight: 700;
            color: #f56c6c;
          }
        }
      }

      .booking-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
      }
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .pay-amount {
    font-size: 28px;
    font-weight: 700;
    color: #f56c6c;
  }
}
</style>
