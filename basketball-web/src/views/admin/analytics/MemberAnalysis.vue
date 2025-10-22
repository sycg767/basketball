<template>
  <div class="member-analysis-container">
    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总会员数</p>
              <p class="stat-value">{{ totalMembers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">活跃会员</p>
              <p class="stat-value green">{{ activeMembers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">流失风险</p>
              <p class="stat-value red">{{ churnRiskMembers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活跃度趋势图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>会员活跃度趋势</span>
          <el-select v-model="trendDays" @change="loadActivityTrend" size="small" style="width: 120px">
            <el-option label="最近7天" :value="7" />
            <el-option label="最近15天" :value="15" />
            <el-option label="最近30天" :value="30" />
          </el-select>
        </div>
      </template>
      <LineChart
        v-if="activityTrendData.xAxisData"
        :data="activityTrendData"
        height="350px"
        y-axis-name="活跃度"
        :show-area="true"
      />
    </el-card>

    <!-- 会员列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员列表</span>
          <el-radio-group v-model="listType" @change="loadMemberList" size="small">
            <el-radio-button value="active">活跃用户</el-radio-button>
            <el-radio-button value="inactive">不活跃用户</el-radio-button>
            <el-radio-button value="churn">流失风险</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table v-loading="loading" :data="memberList" stripe>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="activityScore" label="活跃度" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.activityScore)">
              {{ row.activityScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastActiveTime" label="最后活跃" width="180" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadMemberList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import LineChart from '@/components/charts/LineChart.vue';
import {
  getMemberActivityTrend,
  getActiveUsers,
  getInactiveUsers,
  getChurnRiskUsers
} from '@/api/analytics';

const totalMembers = ref(0);
const activeMembers = ref(0);
const churnRiskMembers = ref(0);
const trendDays = ref(7);
const listType = ref('active');
const loading = ref(false);
const memberList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const activityTrendData = ref({
  xAxisData: [],
  series: []
});

onMounted(() => {
  loadActivityTrend();
  loadMemberList();
  loadStats();
});

const loadActivityTrend = async () => {
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - trendDays.value);

    console.log('👥 [会员活跃度趋势] 请求参数:', {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    const res = await getMemberActivityTrend({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });

    console.log('👥 [会员活跃度趋势] 响应数据:', res);
    console.log('👥 [会员活跃度趋势] 数据长度:', res.data?.length);
    console.log('👥 [会员活跃度趋势] 数据内容:', res.data);

    if (res.code === 200 && res.data && res.data.length > 0) {
      activityTrendData.value = {
        xAxisData: res.data.map(item => item.date || item.analysisDate),
        series: [
          {
            name: '活跃用户数',
            data: res.data.map(item => item.activeUserCount || item.totalLoginCount || item.loginCount || item.activeCount || 0)
          }
        ]
      };
      console.log('✅ [会员活跃度趋势] 图表数据已设置:', activityTrendData.value);
    } else {
      console.warn('⚠️ [会员活跃度趋势] 无数据或数据为空');
    }
  } catch (error) {
    console.error('❌ [会员活跃度趋势] 获取失败:', error);
    ElMessage.error('获取活跃度趋势失败');
  }
};

const loadMemberList = async () => {
  loading.value = true;
  try {
    let res;
    const params = { page: currentPage.value, size: pageSize.value };

    console.log('📋 [会员列表] 请求参数:', { listType: listType.value, ...params });

    if (listType.value === 'active') {
      res = await getActiveUsers(params);
    } else if (listType.value === 'inactive') {
      res = await getInactiveUsers(params);
    } else {
      res = await getChurnRiskUsers(params);
    }

    console.log('📋 [会员列表] 响应数据:', res);
    console.log('📋 [会员列表] 数据长度:', res.data?.records?.length || res.data?.length);

    if (res.code === 200) {
      memberList.value = res.data.records || res.data || [];
      total.value = res.data.total || res.data?.length || 0;
      console.log('✅ [会员列表] 列表数据已设置:', {
        count: memberList.value.length,
        total: total.value
      });
    }
  } catch (error) {
    ElMessage.error('获取会员列表失败');
  } finally {
    loading.value = false;
  }
};

const loadStats = async () => {
  try {
    console.log('📊 [会员统计] 开始获取统计数据...');

    const [activeRes, churnRes] = await Promise.all([
      getActiveUsers({ page: 1, size: 1 }),
      getChurnRiskUsers({ page: 1, size: 1 })
    ]);

    console.log('📊 [会员统计] 活跃用户响应:', activeRes);
    console.log('📊 [会员统计] 流失风险响应:', churnRes);

    totalMembers.value = activeRes.data?.total || activeRes.data?.length || 0;
    activeMembers.value = activeRes.data?.total || activeRes.data?.length || 0;
    churnRiskMembers.value = churnRes.data?.total || churnRes.data?.length || 0;

    console.log('✅ [会员统计] 统计数据已设置:', {
      totalMembers: totalMembers.value,
      activeMembers: activeMembers.value,
      churnRiskMembers: churnRiskMembers.value
    });
  } catch (error) {
    console.error('❌ [会员统计] 获取失败:', error);
  }
};

const getScoreType = (score) => {
  if (score >= 80) return 'success';
  if (score >= 60) return 'info';
  if (score >= 40) return 'warning';
  return 'danger';
};
</script>

<style lang="scss" scoped>
.member-analysis-container {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 24px 0;
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        padding: 20px;

        .stat-info {
          .stat-label {
            font-size: 14px;
            color: #909399;
            margin: 0 0 8px 0;
          }

          .stat-value {
            font-size: 32px;
            font-weight: bold;
            margin: 0;

            &.green {
              color: #67C23A;
            }

            &.red {
              color: #F56C6C;
            }
          }
        }
      }
    }
  }

  .chart-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}
</style>
