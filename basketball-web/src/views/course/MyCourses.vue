<template>
  <div class="my-courses-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="支付状态">
          <el-select v-model="searchForm.payStatus" placeholder="请选择状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部报名" name="all" />
      <el-tab-pane label="待支付" name="unpaid" />
      <el-tab-pane label="待上课" name="paid" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>

    <div v-if="enrollmentList.length > 0" class="enrollment-list">
      <el-card
        v-for="enrollment in enrollmentList"
        :key="enrollment.id"
        class="enrollment-card"
        shadow="hover"
      >
        <div class="enrollment-header">
          <div class="enrollment-no">
            <span class="label">报名编号：</span>
            <span class="value">{{ enrollment.enrollmentNo }}</span>
          </div>
          <div class="enrollment-status">
            <el-tag :type="getPayStatusColor(enrollment.payStatus)">
              {{ getPayStatusText(enrollment.payStatus) }}
            </el-tag>
            <el-tag :type="getAttendanceStatusColor(enrollment.attendanceStatus)" style="margin-left: 10px">
              {{ getAttendanceStatusText(enrollment.attendanceStatus) }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <div class="enrollment-content">
          <div class="course-info">
            <h3 class="course-name">{{ enrollment.courseName }}</h3>

            <div class="info-row">
              <span class="label">教练：</span>
              <span class="value">{{ enrollment.coachName }}</span>
            </div>

            <div class="info-row">
              <span class="label">上课时间：</span>
              <span class="value">{{ enrollment.scheduleDate }} {{ enrollment.timeSlot }}</span>
            </div>

            <div class="info-row">
              <span class="label">上课地点：</span>
              <span class="value">{{ enrollment.venueName }}</span>
            </div>

            <div class="info-row">
              <span class="label">课程费用：</span>
              <span class="price">¥{{ enrollment.price }}</span>
            </div>

            <div class="info-row">
              <span class="label">报名时间：</span>
              <span class="value">{{ formatDateTime(enrollment.enrollTime) }}</span>
            </div>

            <div v-if="enrollment.checkInTime" class="info-row">
              <span class="label">签到时间：</span>
              <span class="value">{{ formatDateTime(enrollment.checkInTime) }}</span>
            </div>

            <div v-if="enrollment.rating" class="info-row">
              <span class="label">我的评价：</span>
              <el-rate v-model="enrollment.rating" disabled />
            </div>

            <div v-if="enrollment.comment" class="info-row">
              <span class="label">评价内容：</span>
              <span class="value">{{ enrollment.comment }}</span>
            </div>
          </div>

          <div class="enrollment-actions">
            <!-- 待支付状态 -->
            <template v-if="enrollment.payStatus === 0">
              <el-button type="primary" @click="handlePay(enrollment)">
                立即支付
              </el-button>
              <el-button type="danger" plain @click="handleCancel(enrollment.id)">
                取消报名
              </el-button>
            </template>

            <!-- 已支付但未签到 -->
            <template v-else-if="enrollment.payStatus === 1 && enrollment.attendanceStatus === 0">
              <el-button type="success" disabled>
                已支付
              </el-button>
              <el-button type="info" plain>
                等待上课
              </el-button>
            </template>

            <!-- 已签到但未评价 -->
            <template v-else-if="enrollment.attendanceStatus === 1 && !enrollment.rating">
              <el-button type="warning" @click="handleRate(enrollment)">
                评价课程
              </el-button>
            </template>

            <!-- 已完成并已评价 -->
            <template v-else-if="enrollment.rating">
              <el-button type="success" disabled>
                已完成
              </el-button>
            </template>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.pageSize"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <el-empty v-else description="暂无报名记录" />

    <!-- 评价对话框 -->
    <el-dialog
      v-model="rateDialogVisible"
      title="课程评价"
      width="500px"
    >
      <el-form :model="rateForm" label-width="100px">
        <el-form-item label="课程名称">
          <span>{{ currentEnrollment?.courseName }}</span>
        </el-form-item>
        <el-form-item label="课程评分" required>
          <el-rate v-model="rateForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="rateForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getMyEnrollments, cancelEnrollment, rateCourse } from '@/api/course';
import { Search, Refresh } from '@element-plus/icons-vue';

const activeTab = ref('all');

const searchForm = reactive({
  payStatus: '',
  page: 1,
  pageSize: 10
});

const enrollmentList = ref([]);
const total = ref(0);

const rateDialogVisible = ref(false);
const currentEnrollment = ref(null);
const rateForm = reactive({
  rating: 5,
  comment: ''
});

// 加载报名列表
const loadEnrollmentList = async () => {
  try {
    const { data } = await getMyEnrollments(searchForm);
    enrollmentList.value = data.list || [];
    total.value = data.total || 0;
  } catch (error) {
    ElMessage.error('获取报名列表失败');
  }
};

// 搜索
const handleSearch = () => {
  searchForm.page = 1;
  loadEnrollmentList();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    payStatus: '',
    page: 1,
    pageSize: 10
  });
  loadEnrollmentList();
};

// 标签页切换
const handleTabChange = (tab) => {
  switch (tab) {
    case 'unpaid':
      searchForm.payStatus = 0;
      break;
    case 'paid':
      searchForm.payStatus = 1;
      break;
    default:
      searchForm.payStatus = '';
  }
  handleSearch();
};

// 支付
const handlePay = (enrollment) => {
  ElMessageBox.confirm('确认支付该课程费用?', '支付确认', {
    confirmButtonText: '确认支付',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用支付接口
    ElMessage.success('支付功能开发中');
  }).catch(() => {});
};

// 取消报名
const handleCancel = (id) => {
  ElMessageBox.confirm('确认取消该课程报名?', '取消确认', {
    confirmButtonText: '确认取消',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelEnrollment(id);
      ElMessage.success('取消报名成功');
      loadEnrollmentList();
    } catch (error) {
      ElMessage.error(error.message || '取消报名失败');
    }
  }).catch(() => {});
};

// 评价课程
const handleRate = (enrollment) => {
  currentEnrollment.value = enrollment;
  rateForm.rating = 5;
  rateForm.comment = '';
  rateDialogVisible.value = true;
};

// 提交评价
const confirmRate = async () => {
  if (!rateForm.rating) {
    ElMessage.warning('请选择评分');
    return;
  }

  try {
    await rateCourse(currentEnrollment.value.id, {
      rating: rateForm.rating,
      comment: rateForm.comment
    });
    ElMessage.success('评价成功');
    rateDialogVisible.value = false;
    loadEnrollmentList();
  } catch (error) {
    ElMessage.error(error.message || '评价失败');
  }
};

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  const date = new Date(dateTimeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 支付状态文本
const getPayStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '已支付'
  };
  return textMap[status] || '未知';
};

// 支付状态颜色
const getPayStatusColor = (status) => {
  const colorMap = {
    0: 'warning',
    1: 'success'
  };
  return colorMap[status] || 'info';
};

// 签到状态文本
const getAttendanceStatusText = (status) => {
  const textMap = {
    0: '未签到',
    1: '已签到'
  };
  return textMap[status] || '未知';
};

// 签到状态颜色
const getAttendanceStatusColor = (status) => {
  const colorMap = {
    0: 'info',
    1: 'success'
  };
  return colorMap[status] || 'info';
};

onMounted(() => {
  loadEnrollmentList();
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.my-courses-container {
  @include container;
  padding: $spacing-8 $spacing-6;
  min-height: 100vh;
  background: $bg-secondary;

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

  :deep(.el-tabs) {
    margin-bottom: $spacing-6;

    .el-tabs__header {
      margin-bottom: $spacing-6;
      border-bottom: 2px solid $border-color;
    }

    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-secondary;
      padding: $spacing-4 $spacing-6;
      transition: $transition-fast;

      &:hover {
        color: $primary;
      }

      &.is-active {
        color: $primary;
        font-weight: $font-weight-semibold;
      }
    }

    .el-tabs__active-bar {
      height: 3px;
      background: $primary;
      border-radius: $radius-full;
    }
  }

  .enrollment-list {
    display: grid;
    gap: $spacing-5;

    .enrollment-card {
      @include card-base;
      border: none;
      transition: all $duration-base $ease-out;

      &:hover {
        @include card-hover;
      }

      :deep(.el-card__body) {
        padding: $spacing-6;
      }

      .enrollment-header {
        @include flex-between;
        margin-bottom: $spacing-4;

        .enrollment-no {
          font-size: $font-size-sm;
          color: $text-secondary;

          .label {
            color: $text-tertiary;
          }

          .value {
            font-weight: $font-weight-semibold;
            color: $text-primary;
          }
        }

        .enrollment-status {
          display: flex;
          gap: $spacing-2;

          .el-tag {
            @include tag-base;
            font-weight: $font-weight-semibold;
          }
        }
      }

      :deep(.el-divider) {
        margin: $spacing-4 0;
        border-color: $border-color;
      }

      .enrollment-content {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: $spacing-6;

        .course-info {
          flex: 1;
          min-width: 0;

          .course-name {
            @include text-heading-3;
            font-size: $font-size-xl;
            margin: 0 0 $spacing-4 0;
          }

          .info-row {
            display: flex;
            align-items: center;
            margin-bottom: $spacing-3;
            font-size: $font-size-sm;

            &:last-child {
              margin-bottom: 0;
            }

            .label {
              color: $text-secondary;
              margin-right: $spacing-3;
              min-width: 90px;
              font-weight: $font-weight-medium;
            }

            .value {
              color: $text-primary;
            }

            .price {
              font-size: $font-size-xl;
              font-weight: $font-weight-bold;
              color: $error;
            }

            :deep(.el-rate) {
              .el-rate__icon {
                font-size: $font-size-lg;
              }
            }
          }
        }

        .enrollment-actions {
          display: flex;
          flex-direction: column;
          gap: $spacing-3;
          flex-shrink: 0;

          .el-button {
            @include button-base;
            min-width: 110px;

            &.el-button--primary {
              @include button-primary;
            }

            &.el-button--success {
              background: $success;
              color: $white;

              &:hover:not(:disabled) {
                background: color-mix(in srgb, $success 95%, black);
              }
            }

            &.el-button--warning {
              background: $warning;
              color: $white;

              &:hover:not(:disabled) {
                background: color-mix(in srgb, $warning 95%, black);
              }
            }

            &.el-button--danger {
              background: transparent;
              color: $error;
              border: 1px solid $error;

              &:hover:not(:disabled) {
                background: rgba($error, 0.1);
              }
            }

            &.el-button--info {
              @include button-secondary;
            }
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

  :deep(.el-dialog) {
    border-radius: $radius-lg;
    box-shadow: $shadow-modal;

    .el-dialog__header {
      padding: $spacing-6;
      border-bottom: 1px solid $border-color;

      .el-dialog__title {
        font-size: $font-size-xl;
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }
    }

    .el-dialog__body {
      padding: $spacing-6;

      .el-form-item__label {
        font-size: $font-size-sm;
        font-weight: $font-weight-medium;
        color: $text-secondary;
      }

      :deep(.el-rate) {
        .el-rate__icon {
          font-size: $font-size-3xl;
        }
      }

      :deep(.el-textarea__inner) {
        @include input-base;
        box-shadow: none;
      }
    }

    .el-dialog__footer {
      padding: $spacing-5 $spacing-6;
      border-top: 1px solid $border-color;

      .el-button {
        @include button-base;

        &.el-button--primary {
          @include button-primary;
        }

        &:not(.el-button--primary) {
          @include button-secondary;
        }
      }
    }
  }
}
</style>
