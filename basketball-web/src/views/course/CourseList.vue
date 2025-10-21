<template>
  <div class="course-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>课程列表</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>课程管理</el-breadcrumb-item>
        <el-breadcrumb-item>课程列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

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

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.course-list-container {
  @include container;
  padding: $spacing-8 $spacing-6;
  min-height: 100vh;
  background: $bg-secondary;

  .page-header {
    margin-bottom: $spacing-8;

    h2 {
      @include text-heading-2;
      margin-bottom: $spacing-3;
    }

    :deep(.el-breadcrumb) {
      font-size: $font-size-sm;

      .el-breadcrumb__item {
        .el-breadcrumb__inner {
          color: $text-secondary;
          font-weight: $font-weight-medium;
          transition: $transition-fast;

          &:hover {
            color: $primary;
          }
        }

        &:last-child .el-breadcrumb__inner {
          color: $text-primary;
        }
      }
    }
  }

  .filter-card {
    @include card-base;
    margin-bottom: $spacing-6;
    border: none;

    :deep(.el-card__body) {
      padding: $spacing-6;
    }

    .search-form {
      margin-bottom: 0;

      .el-form-item {
        margin-right: $spacing-4;

        :deep(.el-form-item__label) {
          font-size: $font-size-sm;
          font-weight: $font-weight-medium;
          color: $text-secondary;
        }

        :deep(.el-input__wrapper),
        :deep(.el-select .el-input__wrapper) {
          @include input-base;
          box-shadow: none;
        }
      }

      .el-button {
        @include button-base;

        &.el-button--primary {
          @include button-primary;
        }
      }
    }
  }

  .course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: $spacing-6;
    margin-bottom: $spacing-8;

    @include respond-to(md) {
      grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    }

    @include respond-to(lg) {
      gap: $spacing-8;
    }
  }

  .course-card {
    @include card-base;
    border: none;
    cursor: pointer;
    transition: all $duration-base $ease-out;
    overflow: hidden;

    &:hover {
      @include card-hover;
    }

    &:active {
      @include card-active;
    }

    :deep(.el-card__body) {
      padding: 0;
    }

    .course-cover {
      position: relative;
      height: 220px;
      overflow: hidden;
      background: $gray-100;

      .cover-image {
        width: 100%;
        height: 100%;

        :deep(.el-image__inner) {
          @include image-cover;
          transition: transform $duration-slow $ease-out;
        }
      }

      &:hover .cover-image :deep(.el-image__inner) {
        transform: scale(1.05);
      }

      .image-slot {
        @include flex-center;
        width: 100%;
        height: 100%;
        background: $gray-100;
        color: $text-tertiary;
        font-size: $font-size-4xl;
      }

      .course-type-tag {
        position: absolute;
        top: $spacing-3;
        right: $spacing-3;
        z-index: 1;

        .el-tag {
          @include tag-base;
          backdrop-filter: $backdrop-blur-sm;
          background: rgba($white, 0.95);
          font-weight: $font-weight-semibold;
        }
      }
    }

    .course-info {
      padding: $spacing-5;

      .course-name {
        @include text-heading-3;
        font-size: $font-size-lg;
        margin: 0 0 $spacing-4 0;
        @include text-ellipsis;
      }

      .course-meta {
        display: flex;
        gap: $spacing-5;
        margin-bottom: $spacing-4;
        color: $text-tertiary;
        font-size: $font-size-sm;

        .meta-item {
          display: flex;
          align-items: center;
          gap: $spacing-1;

          .el-icon {
            font-size: $font-size-base;
          }
        }
      }

      .course-detail {
        margin-bottom: $spacing-4;
        padding: $spacing-3;
        background: $gray-50;
        border-radius: $radius-sm;

        .detail-item {
          display: flex;
          align-items: center;
          margin-bottom: $spacing-2;
          font-size: $font-size-sm;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            color: $text-tertiary;
            margin-right: $spacing-1;
          }

          .el-tag {
            @include tag-base;
            font-size: $font-size-xs;
          }
        }
      }

      .course-footer {
        @include flex-between;
        padding-top: $spacing-4;
        border-top: 1px solid $border-color;

        .price-info {
          display: flex;
          flex-direction: column;
          gap: $spacing-1;

          .price {
            font-size: $font-size-2xl;
            font-weight: $font-weight-bold;
            color: $error;
          }

          .member-price {
            font-size: $font-size-xs;
            color: $text-tertiary;
          }
        }

        .el-button {
          @include button-base;
          height: 36px;
          font-size: $font-size-sm;

          &.el-button--primary {
            @include button-primary;
          }
        }
      }
    }
  }

  .pagination-container {
    @include flex-center;
    margin-top: $spacing-10;
    padding: $spacing-6 0;

    :deep(.el-pagination) {
      .el-pager li {
        min-width: 36px;
        height: 36px;
        line-height: 36px;
        border-radius: $radius-sm;
        font-weight: $font-weight-medium;
        transition: $transition-fast;

        &.is-active {
          background: $primary;
          color: $white;
        }

        &:hover:not(.is-active) {
          background: $gray-100;
        }
      }

      .btn-prev,
      .btn-next {
        min-width: 36px;
        height: 36px;
        border-radius: $radius-sm;
        transition: $transition-fast;

        &:hover:not(:disabled) {
          background: $gray-100;
        }
      }
    }
  }
}
</style>
