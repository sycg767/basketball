<template>
  <el-card class="venue-card" :body-style="{ padding: '0px' }" shadow="hover">
    <div class="venue-image">
      <img :src="venue.imageUrl || defaultImage" :alt="venue.venueName" />
      <el-tag
        class="venue-status"
        :type="venue.status === 1 ? 'success' : 'danger'"
        size="small"
      >
        {{ venue.status === 1 ? '可用' : '维护中' }}
      </el-tag>
    </div>

    <div class="venue-info">
      <div class="venue-header">
        <h3 class="venue-name">{{ venue.venueName }}</h3>
        <span class="venue-code">{{ venue.venueCode }}</span>
      </div>

      <div class="venue-details">
        <el-row :gutter="10">
          <el-col :span="12">
            <div class="detail-item">
              <el-icon><Location /></el-icon>
              <span>{{ venue.location || '未设置' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <el-icon><Grid /></el-icon>
              <span>{{ venue.area }}㎡</span>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="10">
          <el-col :span="12">
            <div class="detail-item">
              <el-icon><User /></el-icon>
              <span>{{ venue.capacity }}人</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <el-icon><Basketball /></el-icon>
              <span>{{ venueTypeMap[venue.venueType] || venue.venueType }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <div v-if="venue.facilities" class="venue-facilities">
        <el-tag
          v-for="facility in displayFacilities"
          :key="facility"
          size="small"
          type="info"
        >
          {{ facility }}
        </el-tag>
        <el-tag
          v-if="hasMoreFacilities"
          size="small"
          type="info"
        >
          +{{ facilitiesList.length - maxDisplayFacilities }}
        </el-tag>
      </div>

      <div class="venue-footer">
        <el-button type="primary" @click="handleViewDetail">
          查看详情
        </el-button>
        <el-button v-if="showBooking && venue.status === 1" @click="handleBooking">
          立即预订
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { Location, Grid, User, Basketball } from '@element-plus/icons-vue';

const props = defineProps({
  venue: {
    type: Object,
    required: true
  },
  showBooking: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['booking']);

const router = useRouter();

// 默认图片
const defaultImage = 'https://via.placeholder.com/400x250?text=Basketball+Court';

// 场地类型映射
const venueTypeMap = {
  1: '室内全场',
  2: '室内半场',
  3: '室外全场',
  4: '室外半场'
};

// 设施列表
const facilitiesList = computed(() => {
  if (!props.venue.facilities) return [];
  return props.venue.facilities.split(',').filter(item => item.trim());
});

// 最大显示设施数量
const maxDisplayFacilities = 4;

// 显示的设施列表
const displayFacilities = computed(() => {
  return facilitiesList.value.slice(0, maxDisplayFacilities);
});

// 是否有更多设施
const hasMoreFacilities = computed(() => {
  return facilitiesList.value.length > maxDisplayFacilities;
});

// 查看详情
const handleViewDetail = () => {
  router.push(`/venue/${props.venue.id}`);
};

// 立即预订
const handleBooking = () => {
  router.push(`/booking/create/${props.venue.id}`);
};
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.venue-card {
  width: 100%;
  @include card-base;
  border: none;
  overflow: hidden;
  cursor: pointer;
  transition: all $duration-base $ease-out;

  &:hover {
    @include card-hover;
  }

  &:active {
    @include card-active;
  }

  :deep(.el-card__body) {
    padding: 0;
  }

  .venue-image {
    position: relative;
    width: 100%;
    height: 220px;
    overflow: hidden;
    background: $gray-100;

    img {
      @include image-cover;
      transition: transform $duration-slow $ease-out;
    }

    &:hover img {
      transform: scale(1.05);
    }

    // 图片遮罩渐变
    &::after {
      @include overlay-gradient;
      opacity: 0.3;
      transition: opacity $duration-base $ease-out;
    }

    &:hover::after {
      opacity: 0.5;
    }

    .venue-status {
      position: absolute;
      top: $spacing-3;
      right: $spacing-3;
      @include tag-base;
      backdrop-filter: $backdrop-blur-sm;
      background: rgba($white, 0.95);
      font-weight: $font-weight-semibold;
      z-index: 1;

      &.el-tag--success {
        color: $success;
        background: rgba($white, 0.95);
      }

      &.el-tag--danger {
        color: $error;
        background: rgba($white, 0.95);
      }
    }
  }

  .venue-info {
    padding: $spacing-5;

    .venue-header {
      @include flex-between;
      margin-bottom: $spacing-4;

      .venue-name {
        @include text-heading-3;
        font-size: $font-size-lg;
        margin: 0;
        @include text-ellipsis;
        flex: 1;
      }

      .venue-code {
        @include tag-base;
        font-size: $font-size-xs;
        color: $text-tertiary;
        background: $gray-100;
        margin-left: $spacing-2;
        flex-shrink: 0;
      }
    }

    .venue-details {
      margin-bottom: $spacing-4;

      .el-row {
        margin-bottom: $spacing-2;

        &:last-child {
          margin-bottom: 0;
        }
      }

      .detail-item {
        display: flex;
        align-items: center;
        gap: $spacing-2;
        font-size: $font-size-sm;
        color: $text-secondary;

        .el-icon {
          font-size: $font-size-base;
          color: $text-tertiary;
        }

        span {
          @include text-ellipsis;
        }
      }
    }

    .venue-facilities {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-2;
      margin-bottom: $spacing-4;
      min-height: 24px;
      max-height: 24px;
      overflow: hidden;

      .el-tag {
        @include tag-base;
        font-size: $font-size-xs;
        background: $gray-100;
        color: $text-secondary;
        border: none;
      }
    }

    .venue-footer {
      display: flex;
      gap: $spacing-3;
      padding-top: $spacing-4;
      border-top: 1px solid $border-color;

      .el-button {
        flex: 1;
        @include button-base;
        height: 40px;
        font-size: $font-size-sm;

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
