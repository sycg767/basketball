<template>
  <div class="dashboard-container">
    <h2 class="page-title">数据统计</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card blue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><House /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalVenues }}</p>
              <p class="stat-label">场地总数</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card green">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalUsers }}</p>
              <p class="stat-label">注册用户</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card orange">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalBookings }}</p>
              <p class="stat-label">预订总数</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card red">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">¥{{ stats.totalRevenue.toLocaleString() }}</p>
              <p class="stat-label">总收入</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>最近预订趋势</span>
              <el-select v-model="trendDays" @change="handleTrendDaysChange" size="small" style="width: 120px">
                <el-option label="最近7天" :value="7" />
                <el-option label="最近15天" :value="15" />
                <el-option label="最近30天" :value="30" />
              </el-select>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>场地使用率</span>
          </template>
          <div ref="usageChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近预订列表 -->
    <el-card class="recent-bookings">
      <template #header>
        <span>最近预订</span>
      </template>
      <el-table :data="recentBookings" stripe>
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="venueName" label="场地名称" />
        <el-table-column prop="userName" label="用户" />
        <el-table-column prop="date" label="预订日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="120" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import {
  House,
  User,
  Calendar,
  Money
} from '@element-plus/icons-vue';
import {
  getStatistics,
  getRecentBookings,
  getBookingTrend,
  getVenueUsageRate
} from '@/api/admin';

const stats = ref({
  totalVenues: 0,
  totalUsers: 0,
  totalBookings: 0,
  totalRevenue: 0
});

const recentBookings = ref([]);
const trendDays = ref(7);
const trendChartRef = ref(null);
const usageChartRef = ref(null);
let trendChart = null;
let usageChart = null;

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getStatistics();
    stats.value = {
      totalVenues: res.data.totalVenues || 0,
      totalUsers: res.data.totalUsers || 0,
      totalBookings: res.data.totalBookings || 0,
      totalRevenue: res.data.totalRevenue || 0
    };
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败');
  }
};

// 获取最近预订
const fetchRecentBookings = async () => {
  try {
    const res = await getRecentBookings(10);
    recentBookings.value = res.data.map(item => ({
      id: item.bookingNo,
      venueName: item.venueName,
      userName: item.realName || item.username,
      date: item.bookingDate,
      timeSlot: item.timeSlot,
      amount: item.actualPrice || item.totalPrice,
      status: item.status
    }));
  } catch (error) {
    ElMessage.error(error.message || '获取最近预订失败');
  }
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',   // 待支付
    1: 'success',   // 已支付
    2: 'danger',    // 已取消
    3: 'success',   // 已完成
    4: 'info',      // 已退款
    5: 'danger'     // 超时取消
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

// 初始化预订趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return;

  trendChart = echarts.init(trendChartRef.value);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '预订数量',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#409EFF',
          width: 3
        },
        itemStyle: {
          color: '#409EFF'
        },
        data: []
      }
    ]
  };

  trendChart.setOption(option);
};

// 初始化场地使用率图表
const initUsageChart = () => {
  if (!usageChartRef.value) return;

  usageChart = echarts.init(usageChartRef.value);

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}% ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center',
      formatter: (name) => {
        return name.length > 8 ? name.substring(0, 8) + '...' : name;
      }
    },
    series: [
      {
        name: '使用率',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold',
            formatter: '{b}\n{c}%'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  };

  usageChart.setOption(option);
};

// 获取预订趋势数据
const fetchBookingTrend = async () => {
  try {
    const res = await getBookingTrend({ days: trendDays.value });
    const dates = res.data.map(item => item.date);
    const counts = res.data.map(item => item.count);

    if (trendChart) {
      trendChart.setOption({
        xAxis: { data: dates },
        series: [{ data: counts }]
      });
    }
  } catch (error) {
    ElMessage.error(error.message || '获取预订趋势失败');
  }
};

// 获取场地使用率数据
const fetchVenueUsage = async () => {
  try {
    const res = await getVenueUsageRate();
    const data = res.data
      .filter(item => item.usageRate > 0) // 只显示有使用率的场地
      .map(item => ({
        name: item.venueName,
        value: item.usageRate
      }));

    if (usageChart) {
      usageChart.setOption({
        series: [{ data }]
      });
    }
  } catch (error) {
    ElMessage.error(error.message || '获取场地使用率失败');
  }
};

// 切换趋势天数
const handleTrendDaysChange = () => {
  fetchBookingTrend();
};

// 窗口大小改变时调整图表
const handleResize = () => {
  trendChart?.resize();
  usageChart?.resize();
};

onMounted(() => {
  fetchStats();
  fetchRecentBookings();

  // 初始化图表
  initTrendChart();
  initUsageChart();

  // 获取图表数据
  fetchBookingTrend();
  fetchVenueUsage();

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  // 销毁图表实例
  trendChart?.dispose();
  usageChart?.dispose();

  // 移除事件监听
  window.removeEventListener('resize', handleResize);
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.dashboard-container {
  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 24px 0;
    letter-spacing: -0.02em;
  }

  .stats-row {
    margin-bottom: 24px;

    .stat-card {
      margin-bottom: 20px;
      border-radius: $radius-lg;
      border: 1px solid $border-color;
      transition: all $duration-fast $ease-in-out;

      &:hover {
        transform: translateY(-2px);
        box-shadow: $shadow-md;
      }

      :deep(.el-card__body) {
        padding: 24px;
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 20px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: $radius-lg;
          display: flex;
          align-items: center;
          justify-content: center;
          color: $white;
          flex-shrink: 0;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 32px;
            font-weight: 600;
            color: $text-primary;
            margin: 0 0 4px 0;
            line-height: 1.2;
          }

          .stat-label {
            font-size: 13px;
            color: $text-secondary;
            margin: 0;
            font-weight: 500;
          }
        }
      }

      &.blue .stat-icon {
        background: $primary;
      }

      &.green .stat-icon {
        background: $success;
      }

      &.orange .stat-icon {
        background: $warning;
      }

      &.red .stat-icon {
        background: $error;
      }
    }
  }

  .charts-row {
    margin-bottom: 24px;

    :deep(.el-card) {
      border-radius: $radius-lg;
      border: 1px solid $border-color;
      box-shadow: $shadow-sm;

      .el-card__header {
        border-bottom: 1px solid $border-color;
        padding: 20px 24px;
        font-weight: 600;
        color: $text-primary;
      }

      .el-card__body {
        padding: 24px;
      }
    }

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .chart-container {
      height: 320px;
      width: 100%;
    }
  }

  .recent-bookings {
    margin-bottom: 24px;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__header) {
      border-bottom: 1px solid $border-color;
      padding: 20px 24px;
      font-weight: 600;
      color: $text-primary;
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
    }
  }
}
</style>
