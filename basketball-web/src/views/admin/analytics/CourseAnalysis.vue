<template>
  <div class="course-analysis-container">
    <h2 class="page-title">课程热度分析</h2>

    <!-- 热度排行榜 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程热度排行榜</span>
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
        y-axis-name="热度分数"
        :horizontal="true"
      />
    </el-card>

    <!-- 热门和冷门课程 -->
    <el-row :gutter="20" class="course-lists">
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>热门课程</span>
          </template>
          <el-table v-loading="loading" :data="hotCourses" stripe>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="popularityScore" label="热度" width="100">
              <template #default="{ row }">
                <el-tag type="success">{{ row.popularityScore }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enrollCount" label="报名数" width="100" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>冷门课程</span>
          </template>
          <el-table v-loading="loading" :data="coldCourses" stripe>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="popularityScore" label="热度" width="100">
              <template #default="{ row }">
                <el-tag type="warning">{{ row.popularityScore }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enrollCount" label="报名数" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import BarChart from '@/components/charts/BarChart.vue';
import {
  getCoursePopularityRanking,
  getHotCourses,
  getColdCourses
} from '@/api/analytics';

const rankingDays = ref(7);
const loading = ref(false);
const hotCourses = ref([]);
const coldCourses = ref([]);

const rankingData = ref({
  xAxisData: [],
  series: []
});

onMounted(() => {
  loadRanking();
  loadHotColdCourses();
});

const loadRanking = async () => {
  loading.value = true;
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - rankingDays.value);

    const res = await getCoursePopularityRanking({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      limit: 10
    });
    if (res.code === 200 && res.data && res.data.length > 0) {
      rankingData.value = {
        xAxisData: res.data.map(item => item.courseName),
        series: [
          {
            name: '热度分数',
            data: res.data.map(item => item.popularityScore || item.score || 0)
          }
        ]
      };
    }
  } catch (error) {
    console.error('获取课程排行榜失败:', error);
    ElMessage.error('获取课程排行榜失败');
  } finally {
    loading.value = false;
  }
};

const loadHotColdCourses = async () => {
  try {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30);

    const params = {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      limit: 5
    };

    const [hotRes, coldRes] = await Promise.all([
      getHotCourses(params),
      getColdCourses(params)
    ]);

    if (hotRes.code === 200) {
      hotCourses.value = hotRes.data || [];
    }

    if (coldRes.code === 200) {
      coldCourses.value = coldRes.data || [];
    }
  } catch (error) {
    console.error('获取热门/冷门课程失败:', error);
  }
};
</script>

<style lang="scss" scoped>
.course-analysis-container {
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

  .course-lists {
    margin-top: 20px;
  }
}
</style>
