<template>
  <div class="course-detail-container">
    <el-card v-if="course" class="course-card">
      <div class="course-header">
        <div class="course-cover">
          <el-image
            :src="course.coverImage || '/images/default-course.jpg'"
            fit="cover"
            class="cover-image"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>

        <div class="course-basic-info">
          <div class="course-header-top">
            <h1 class="course-name">{{ course.courseName }}</h1>
            <el-tag :type="getCourseTypeColor(course.courseType)" size="large">
              {{ getCourseTypeText(course.courseType) }}
            </el-tag>
          </div>

          <div class="course-stats">
            <div class="stat-item">
              <el-icon><User /></el-icon>
              <span>教练：{{ course.coachName }}</span>
            </div>
            <div class="stat-item">
              <el-icon><Star /></el-icon>
              <span>评分：{{ course.rating || '5.0' }}</span>
            </div>
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>浏览：{{ course.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon><UserFilled /></el-icon>
              <span>报名：{{ course.enrollCount || 0 }}人</span>
            </div>
          </div>

          <div class="course-attributes">
            <div class="attr-row">
              <span class="attr-label">难度等级：</span>
              <el-tag :type="getDifficultyColor(course.difficultyLevel)">
                {{ getDifficultyText(course.difficultyLevel) }}
              </el-tag>
            </div>
            <div class="attr-row">
              <span class="attr-label">适合年龄：</span>
              <span>{{ course.ageRange }}</span>
            </div>
            <div class="attr-row">
              <span class="attr-label">课程人数：</span>
              <span>{{ course.minStudents }}-{{ course.maxStudents }}人</span>
            </div>
            <div class="attr-row">
              <span class="attr-label">课程时长：</span>
              <span>{{ course.duration }}分钟/节</span>
            </div>
            <div class="attr-row">
              <span class="attr-label">总课时：</span>
              <span>{{ course.totalSessions }}节课</span>
            </div>
          </div>

          <div class="course-price">
            <div class="price-item">
              <span class="price-label">普通价格：</span>
              <span class="price-value">¥{{ course.price }}</span>
            </div>
            <div v-if="course.memberPrice" class="price-item">
              <span class="price-label">会员价格：</span>
              <span class="price-value member">¥{{ course.memberPrice }}</span>
            </div>
          </div>
        </div>
      </div>

      <el-divider />

      <div class="course-description">
        <h3>课程介绍</h3>
        <p>{{ course.description }}</p>
      </div>

      <el-divider />

      <div v-if="course.syllabusList && course.syllabusList.length > 0" class="course-syllabus">
        <h3>课程大纲</h3>
        <ul>
          <li v-for="(item, index) in course.syllabusList" :key="index">
            {{ item }}
          </li>
        </ul>
      </div>

      <el-divider v-if="course.videoUrl" />

      <div v-if="course.videoUrl" class="course-video">
        <h3>课程视频</h3>
        <video :src="course.videoUrl" controls class="video-player" />
      </div>
    </el-card>

    <el-card v-if="course" class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>课程排期</span>
          <div class="date-filter">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="loadSchedules"
            />
          </div>
        </div>
      </template>

      <div v-if="scheduleList.length > 0" class="schedule-list">
        <el-card
          v-for="schedule in scheduleList"
          :key="schedule.id"
          class="schedule-item"
          shadow="hover"
        >
          <div class="schedule-info">
            <div class="schedule-date">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(schedule.scheduleDate) }}</span>
            </div>
            <div class="schedule-time">
              <el-icon><Clock /></el-icon>
              <span>{{ schedule.timeSlot }}</span>
            </div>
            <div class="schedule-venue">
              <el-icon><Location /></el-icon>
              <span>{{ schedule.venueName }}</span>
            </div>
            <div class="schedule-seats">
              <el-icon><User /></el-icon>
              <span>剩余名额：{{ schedule.remainingSeats }}/{{ schedule.maxStudents }}</span>
            </div>
          </div>

          <div class="schedule-action">
            <el-tag
              v-if="schedule.status === 2"
              type="danger"
              effect="dark"
            >
              已满员
            </el-tag>
            <el-tag
              v-else-if="schedule.status === 5"
              type="info"
              effect="dark"
            >
              已取消
            </el-tag>
            <el-button
              v-else
              type="primary"
              :disabled="schedule.status !== 1"
              @click="handleEnroll(schedule)"
            >
              立即报名
            </el-button>
          </div>
        </el-card>
      </div>

      <el-empty v-else description="暂无排期数据" />
    </el-card>

    <!-- 报名对话框 -->
    <el-dialog
      v-model="enrollDialogVisible"
      title="课程报名"
      width="500px"
    >
      <el-form :model="enrollForm" label-width="100px">
        <el-form-item label="课程名称">
          <span>{{ course?.courseName }}</span>
        </el-form-item>
        <el-form-item label="上课时间">
          <span>{{ currentSchedule?.scheduleDate }} {{ currentSchedule?.timeSlot }}</span>
        </el-form-item>
        <el-form-item label="上课地点">
          <span>{{ currentSchedule?.venueName }}</span>
        </el-form-item>
        <el-form-item label="课程费用">
          <span class="price-text">¥{{ enrollPrice }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="enrollDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEnroll">确认报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCourseDetail, getCourseSchedules, enrollCourse } from '@/api/course';
import {
  Picture,
  User,
  Star,
  View,
  UserFilled,
  Calendar,
  Clock,
  Location
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

const course = ref(null);
const scheduleList = ref([]);
const dateRange = ref([]);

const enrollDialogVisible = ref(false);
const currentSchedule = ref(null);
const enrollForm = reactive({
  scheduleId: null
});

// 计算报名价格
const enrollPrice = computed(() => {
  if (!course.value) return 0;
  // 这里可以根据用户会员等级判断显示普通价格还是会员价格
  return course.value.memberPrice || course.value.price;
});

// 加载课程详情
const loadCourseDetail = async () => {
  try {
    const { data } = await getCourseDetail(route.params.id);
    course.value = data;
  } catch (error) {
    ElMessage.error('获取课程详情失败');
  }
};

// 加载课程排期
const loadSchedules = async () => {
  try {
    const params = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    const { data } = await getCourseSchedules(route.params.id, params);
    scheduleList.value = data || [];
  } catch (error) {
    ElMessage.error('获取课程排期失败');
  }
};

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return `${dateStr} ${weekDays[date.getDay()]}`;
};

// 处理报名
const handleEnroll = (schedule) => {
  currentSchedule.value = schedule;
  enrollForm.scheduleId = schedule.id;
  enrollDialogVisible.value = true;
};

// 确认报名
const confirmEnroll = async () => {
  try {
    await enrollCourse(enrollForm);
    ElMessage.success('报名成功');
    enrollDialogVisible.value = false;

    // 提示跳转到我的课程
    ElMessageBox.confirm('报名成功，是否前往查看我的课程?', '提示', {
      confirmButtonText: '前往查看',
      cancelButtonText: '继续浏览',
      type: 'success'
    }).then(() => {
      router.push('/my-courses');
    }).catch(() => {
      // 刷新排期列表
      loadSchedules();
    });
  } catch (error) {
    ElMessage.error(error.message || '报名失败');
  }
};

// 课程��型文本
const getCourseTypeText = (type) => {
  const typeMap = {
    1: '基础班',
    2: '提高班',
    3: '专业班',
    4: '私教课'
  };
  return typeMap[type] || '未知';
};

// 课程类型颜色
const getCourseTypeColor = (type) => {
  const colorMap = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger'
  };
  return colorMap[type] || 'info';
};

// 难度等级文本
const getDifficultyText = (level) => {
  const levelMap = {
    1: '入门',
    2: '初级',
    3: '中级',
    4: '高级'
  };
  return levelMap[level] || '未知';
};

// 难度等级颜色
const getDifficultyColor = (level) => {
  const colorMap = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger'
  };
  return colorMap[level] || 'info';
};

onMounted(() => {
  loadCourseDetail();
  loadSchedules();
});
</script>

<style scoped>
.course-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.course-card {
  margin-bottom: 20px;
}

.course-header {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.course-cover {
  flex-shrink: 0;
  width: 400px;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 50px;
}

.course-basic-info {
  flex: 1;
}

.course-header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.course-name {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: #303133;
}

.course-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
}

.course-attributes {
  margin-bottom: 20px;
}

.attr-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
}

.attr-label {
  color: #909399;
  margin-right: 10px;
  min-width: 80px;
}

.course-price {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.price-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.price-item:last-child {
  margin-bottom: 0;
}

.price-label {
  font-size: 14px;
  margin-right: 10px;
}

.price-value {
  font-size: 28px;
  font-weight: bold;
}

.price-value.member {
  color: #ffd700;
}

.course-description h3,
.course-syllabus h3,
.course-video h3 {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 15px 0;
  color: #303133;
}

.course-description p {
  line-height: 1.8;
  color: #606266;
  margin: 0;
}

.course-syllabus ul {
  margin: 0;
  padding-left: 20px;
}

.course-syllabus li {
  line-height: 2;
  color: #606266;
  margin-bottom: 8px;
}

.video-player {
  width: 100%;
  max-height: 500px;
  border-radius: 8px;
}

.schedule-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-list {
  display: grid;
  gap: 15px;
}

.schedule-item {
  cursor: pointer;
}

.schedule-item :deep(.el-card__body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.schedule-info {
  display: flex;
  gap: 30px;
  flex: 1;
}

.schedule-date,
.schedule-time,
.schedule-venue,
.schedule-seats {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.schedule-action {
  margin-left: 20px;
}

.price-text {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
