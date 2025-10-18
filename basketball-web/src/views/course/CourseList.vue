<template>
  <div class="course-list-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入课程名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.courseType" placeholder="请选择类型" clearable>
            <el-option label="全部" value="" />
            <el-option label="基础班" :value="1" />
            <el-option label="提高班" :value="2" />
            <el-option label="专业班" :value="3" />
            <el-option label="私教课" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级">
          <el-select v-model="searchForm.difficultyLevel" placeholder="请选择难度" clearable>
            <el-option label="全部" value="" />
            <el-option label="入门" :value="1" />
            <el-option label="初级" :value="2" />
            <el-option label="中级" :value="3" />
            <el-option label="高级" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="course-grid">
      <el-card
        v-for="course in courseList"
        :key="course.id"
        class="course-card"
        shadow="hover"
        @click="goToDetail(course.id)"
      >
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
          <div class="course-type-tag">
            <el-tag :type="getCourseTypeColor(course.courseType)" size="small">
              {{ getCourseTypeText(course.courseType) }}
            </el-tag>
          </div>
        </div>

        <div class="course-info">
          <h3 class="course-name">{{ course.courseName }}</h3>

          <div class="course-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>{{ course.coachName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Star /></el-icon>
              <span>{{ course.rating || '5.0' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ course.viewCount || 0 }}</span>
            </div>
          </div>

          <div class="course-detail">
            <div class="detail-item">
              <span class="label">难度：</span>
              <el-tag :type="getDifficultyColor(course.difficultyLevel)" size="small">
                {{ getDifficultyText(course.difficultyLevel) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">人数：</span>
              <span>{{ course.minStudents }}-{{ course.maxStudents }}人</span>
            </div>
            <div class="detail-item">
              <span class="label">时长：</span>
              <span>{{ course.duration }}分钟/节</span>
            </div>
          </div>

          <div class="course-footer">
            <div class="price-info">
              <span class="price">¥{{ course.price }}</span>
              <span v-if="course.memberPrice" class="member-price">
                会员价 ¥{{ course.memberPrice }}
              </span>
            </div>
            <el-button type="primary" size="small" @click.stop="goToDetail(course.id)">
              查看详情
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[6, 12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <el-empty v-else description="暂无课程数据" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getCourseList } from '@/api/course';
import { Picture, User, Star, View, Search, Refresh } from '@element-plus/icons-vue';

const router = useRouter();

const searchForm = reactive({
  keyword: '',
  courseType: '',
  difficultyLevel: '',
  page: 1,
  size: 12
});

const courseList = ref([]);
const total = ref(0);

// 获取课程列表
const loadCourseList = async () => {
  try {
    const { data } = await getCourseList(searchForm);
    // 后端返回的是 records，不是 list
    courseList.value = data.records || [];
    total.value = data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败:', error);
    ElMessage.error('获取课程列表失败');
  }
};

// 搜索
const handleSearch = () => {
  searchForm.page = 1;
  loadCourseList();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    courseType: '',
    difficultyLevel: '',
    page: 1,
    size: 12
  });
  loadCourseList();
};

// 跳转详情
const goToDetail = (id) => {
  router.push(`/course/${id}`);
};

// 课程类型文本
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
  loadCourseList();
});
</script>

<style scoped>
.course-list-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 15px;
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
  font-size: 30px;
}

.course-type-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.course-info {
  padding: 0 10px;
}

.course-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 15px 0;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #909399;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.course-detail {
  margin-bottom: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item .label {
  color: #909399;
  margin-right: 5px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.member-price {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
