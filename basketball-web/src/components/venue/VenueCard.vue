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
.venue-card {
  width: 100%;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
  }

  .venue-image {
    position: relative;
    width: 100%;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }

    &:hover img {
      transform: scale(1.1);
    }

    .venue-status {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }

  .venue-info {
    padding: 15px;

    .venue-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .venue-name {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }

      .venue-code {
        font-size: 12px;
        color: #909399;
        background-color: #f5f7fa;
        padding: 4px 8px;
        border-radius: 4px;
      }
    }

    .venue-details {
      margin-bottom: 15px;

      .el-row {
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }
      }

      .detail-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: #606266;

        .el-icon {
          font-size: 16px;
          color: #909399;
        }
      }
    }

    .venue-facilities {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 15px;
      min-height: 28px;
      max-height: 28px;
      overflow: hidden;

      .el-tag {
        font-size: 12px;
      }
    }

    .venue-footer {
      display: flex;
      justify-content: space-between;
      gap: 10px;

      .el-button {
        flex: 1;
      }
    }
  }
}
</style>
