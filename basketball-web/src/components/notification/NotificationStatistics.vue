<template>
  <el-card class="statistics-card">
    <template #header>
      <h4>通知统计</h4>
    </template>
    <div v-loading="loading" class="chart-container">
      <div ref="chartRef" style="width: 100%; height: 300px;"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { getNotificationStatistics } from '@/api/notification';
import { ElMessage } from 'element-plus';

const chartRef = ref(null);
const loading = ref(false);
let chartInstance = null;

const typeNameMap = {
  'BOOKING_SUCCESS': '预订成功',
  'BOOKING_CANCEL': '预订取消',
  'BOOKING_START_REMINDER': '预订提醒',
  'COURSE_ENROLL_SUCCESS': '课程报名',
  'COURSE_ENROLL_CANCEL': '课程取消',
  'COURSE_START_REMINDER': '课程提醒',
  'PAYMENT_SUCCESS': '支付成功',
  'REFUND_SUCCESS': '退款成功',
  'MEMBER_CARD_EXPIRE_SOON': '会员到期'
};

onMounted(() => {
  loadStatistics();
});

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose();
  }
});

const loadStatistics = async () => {
  loading.value = true;
  try {
    const res = await getNotificationStatistics();
    if (res.code === 200) {
      renderChart(res.data);
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    ElMessage.error('获取统计数据失败');
  } finally {
    loading.value = false;
  }
};

const renderChart = (data) => {
  if (!chartRef.value) return;

  if (chartInstance) {
    chartInstance.dispose();
  }

  chartInstance = echarts.init(chartRef.value);

  const chartData = Object.entries(data).map(([key, value]) => ({
    name: typeNameMap[key] || key,
    value
  }));

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: chartData
      }
    ]
  };

  chartInstance.setOption(option);
};
</script>

<style lang="scss" scoped>
.statistics-card {
  margin-bottom: 20px;

  h4 {
    margin: 0;
    font-size: 16px;
    font-weight: 500;
  }

  .chart-container {
    min-height: 300px;
  }
}
</style>
