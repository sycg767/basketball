<template>
  <div class="booking-manage-container">
    <h2 class="page-title">预订管理</h2>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="预订状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="全部" value="" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已退款" :value="4" />
            <el-option label="超时取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预订列表 -->
    <el-card class="table-card">
      <el-table :data="bookingList" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="140" />
        <el-table-column prop="venueName" label="场地名称" width="150" />
        <el-table-column prop="username" label="预订用户" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="bookingDate" label="预订日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="150" />
        <el-table-column prop="duration" label="时长(小时)" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="预订详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">
          {{ bookingDetail.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(bookingDetail.status)">
            {{ getStatusText(bookingDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="场地名称">
          {{ bookingDetail.venueName }}
        </el-descriptions-item>
        <el-descriptions-item label="场地位置">
          {{ bookingDetail.venueLocation }}
        </el-descriptions-item>
        <el-descriptions-item label="预订用户">
          {{ bookingDetail.username }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ bookingDetail.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="预订日期">
          {{ bookingDetail.bookingDate }}
        </el-descriptions-item>
        <el-descriptions-item label="时间段">
          {{ bookingDetail.timeSlot }}
        </el-descriptions-item>
        <el-descriptions-item label="时长">
          {{ bookingDetail.duration }} 小时
        </el-descriptions-item>
        <el-descriptions-item label="单价">
          ¥{{ bookingDetail.price }}/小时
        </el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span style="color: #f56c6c; font-weight: bold;">
            ¥{{ bookingDetail.amount?.toFixed(2) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">
          {{ bookingDetail.payMethod }}
        </el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">
          {{ bookingDetail.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ bookingDetail.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Search,
  Refresh,
  View,
  Close
} from '@element-plus/icons-vue';
import {
  getBookingList,
  getBookingDetail,
  cancelBooking
} from '@/api/admin';

const loading = ref(false);
const dialogVisible = ref(false);

const searchForm = reactive({
  orderNo: '',
  username: '',
  status: ''
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

const bookingList = ref([]);
const bookingDetail = ref({});

// 获取预订列表
const fetchBookingList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      orderNo: searchForm.orderNo || undefined,
      username: searchForm.username || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    };

    const res = await getBookingList(params);

    bookingList.value = res.data.records.map(item => ({
      id: item.id,
      orderNo: item.bookingNo,
      venueName: item.venueName,
      venueLocation: item.venueLocation || '-',
      username: item.username,
      phone: item.phone,
      bookingDate: item.bookingDate,
      timeSlot: item.timeSlot,
      duration: item.duration,
      price: item.price,
      amount: item.actualPrice || item.totalPrice,
      payMethod: item.payMethodName,
      status: item.status,
      createTime: item.createTime,
      remark: item.remark || ''
    }));

    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error(error.message || '获取预订列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',   // 待支付
    1: 'primary',   // 已支付
    2: 'info',      // 已取消
    3: 'success',   // 已完成
    4: 'danger',    // 已退款
    5: 'info'       // 超时取消
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

// 搜索
const handleSearch = () => {
  pagination.page = 1;
  fetchBookingList();
};

// 重置
const handleReset = () => {
  searchForm.orderNo = '';
  searchForm.username = '';
  searchForm.status = '';
  handleSearch();
};

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getBookingDetail(row.id);
    bookingDetail.value = {
      id: res.data.id,
      orderNo: res.data.bookingNo,
      venueName: res.data.venueName,
      venueLocation: res.data.venueLocation || '-',
      username: res.data.username,
      phone: res.data.phone,
      bookingDate: res.data.bookingDate,
      timeSlot: res.data.timeSlot,
      duration: res.data.duration,
      price: res.data.price,
      amount: res.data.actualPrice || res.data.totalPrice,
      payMethod: res.data.payMethodName,
      status: res.data.status,
      createTime: res.data.createTime,
      remark: res.data.remark || ''
    };
    dialogVisible.value = true;
  } catch (error) {
    ElMessage.error(error.message || '获取预订详情失败');
  }
};

// 取消预订
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消订单"${row.orderNo}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await cancelBooking(row.id);
    ElMessage.success('取消成功');
    fetchBookingList();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消失败');
    }
  }
};

// 分页
const handleSizeChange = () => {
  fetchBookingList();
};

const handlePageChange = () => {
  fetchBookingList();
};

onMounted(() => {
  fetchBookingList();
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.booking-manage-container {
  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 24px 0;
    letter-spacing: -0.02em;
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-button) {
      border-radius: $radius-md;
      font-weight: 500;
      transition: all $duration-fast $ease-in-out;

      &:hover {
        transform: translateY(-1px);
      }
    }
  }

  .table-card {
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-table) {
      font-size: 14px;

      th {
        background-color: $bg-secondary;
        color: $text-secondary;
        font-weight: 500;
      }

      td {
        color: $text-primary;
      }

      .el-button {
        font-weight: 500;
        transition: all $duration-fast $ease-in-out;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .pagination {
      margin-top: 24px;
      justify-content: flex-end;
    }
  }
}
</style>
