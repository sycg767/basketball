<template>
  <div class="venue-analysis-container">
    <!-- 场地使用率排行 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场地使用率排行</span>
          <el-select v-model="rankingDays" @change="loadRanking" size="small" style="width: 120px">
            <el-option label="最近7天" :value="7" />
            <el-option label="最近15天" :value="15" />
            <el-option label="最近30天" :value="30" />
          </el-select>
        </div>
      </template>
      <BarChart
        v-if="rankingData.xAxisData"
        :data="rankingData"
        height="400px"
        y-axis-name="使用率(%)"
        :horizontal="true"
      />
    </el-card>

    <!-- 收入分析 -->
    <el-card class="revenue-card">
      <template #header>
        <span>场地收入分析</span>
      </template>
      <LineChart
        v-if="revenueData.xAxisData"
        :data="revenueData"
        height="350px"
        y-axis-name="收入(元)"
        :show-area="true"
      />
    </el-card>

    <!-- 高峰时段分析 -->
    <el-card>
      <template #header>
        <span>高峰时段分布</span>
      </template>
      <el-table v-loading="loading" :data="peakPeriods" stripe>
        <el-table-column prop="periodName" label="时段" width="200" />
        <el-table-column prop="bookingCount" label="预订次数" width="120" />
        <el-table-column prop="percentage" label="占比" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.percentage" :color="getProgressColor(row.percentage)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import BarChart from '@/components/charts/BarChart.vue';
import LineChart from '@/components/charts/LineChart.vue';
import {
  getVenueUsageRanking,
  getVenueRevenueAnalysis,
  getPeakPeriodAnalysis
} from '@/api/analytics';

const rankingDays = ref(7);
const loading = ref(false);
const peakPeriods = ref([]);

const rankingData = ref({
  xAxisData: [],
  series: []
});

const revenueData = ref({
  xAxisData: [],
  series: []
});

onMounted(() => {
  loadRanking();
  loadRevenueAnalysis();
  loadPeakPeriods();
});

const loadRanking = async () => {
  loading.value = true;
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - rankingDays.value);

    console.log('🏀 [场地使用率] 请求参数:', {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      limit: 10
    });

    const res = await getVenueUsageRanking({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      limit: 10
    });

    console.log('🏀 [场地使用率] 响应数据:', res);
    console.log('🏀 [场地使用率] 数据长度:', res.data?.length);
    console.log('🏀 [场地使用率] 数据内容:', res.data);

    if (res.code === 200 && res.data && res.data.length > 0) {
      rankingData.value = {
        xAxisData: res.data.map(item => item.venueName),
        series: [
          {
            name: '使用率',
            data: res.data.map(item => item.avgUsageRate || item.usageRate || item.rate || 0)
          }
        ]
      };
      console.log('✅ [场地使用率] 图表数据已设置:', rankingData.value);
    } else {
      console.warn('⚠️ [场地使用率] 无数据或数据为空');
    }
  } catch (error) {
    console.error('❌ [场地使用率] 获取失败:', error);
    ElMessage.error('获取场地排行榜失败');
  } finally {
    loading.value = false;
  }
};

const loadRevenueAnalysis = async () => {
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30);

    console.log('💰 [场地收入] 请求参数:', {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    const res = await getVenueRevenueAnalysis({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    console.log('💰 [场地收入] 响应数据:', res);
    console.log('💰 [场地收入] 数据长度:', res.data?.length);
    console.log('💰 [场地收入] 数据内容:', res.data);

    if (res.code === 200 && res.data && res.data.length > 0) {
      revenueData.value = {
        xAxisData: res.data.map(item => item.venueName || item.analysisDate || item.date),
        series: [
          {
            name: '总收入',
            data: res.data.map(item => item.totalRevenue || item.revenue || 0)
          }
        ]
      };
      console.log('✅ [场地收入] 图表数据已设置:', revenueData.value);
    } else {
      console.warn('⚠️ [场地收入] 无数据或数据为空');
    }
  } catch (error) {
    console.error('获取收入分析失败:', error);
  }
};

const loadPeakPeriods = async () => {
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 7);

    console.log('⏰ [高峰时段] 请求参数:', {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    const res = await getPeakPeriodAnalysis({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    console.log('⏰ [高峰时段] 响应数据:', res);
    console.log('⏰ [高峰时段] 数据长度:', res.data?.length);
    console.log('⏰ [高峰时段] 数据内容:', res.data);

    if (res.code === 200 && res.data) {
      peakPeriods.value = res.data || [];
      console.log('✅ [高峰时段] 数据已设置:', peakPeriods.value);
    } else {
      console.warn('⚠️ [高峰时段] 无数据或数据为空');
    }
  } catch (error) {
    console.error('❌ [高峰时段] 获取失败:', error);
  }
};

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67C23A';
  if (percentage >= 60) return '#E6A23C';
  return '#F56C6C';
};
</script>

<style lang="scss" scoped>
.venue-analysis-container {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 24px 0;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .revenue-card {
    margin: 20px 0;
  }
}
</style>
