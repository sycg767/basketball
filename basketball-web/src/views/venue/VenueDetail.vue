<template>
  <div class="venue-detail-container">
    <BackButton text="返回列表" />

    <div v-loading="loading" class="detail-content">
      <template v-if="venueDetail">
        <!-- 场地基本信息 -->
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>场地信息</span>
              <el-tag :type="venueDetail.status === 1 ? 'success' : 'danger'">
                {{ venueDetail.status === 1 ? '可用' : '维护中' }}
              </el-tag>
            </div>
          </template>

          <el-row :gutter="20">
            <!-- 场地图片 -->
            <el-col :span="12">
              <div class="venue-images">
                <el-image
                  :src="currentImage"
                  :preview-src-list="imageList"
                  fit="cover"
                  class="main-image"
                />
                <div v-if="imageList.length > 1" class="thumbnail-list">
                  <div
                    v-for="(img, index) in imageList"
                    :key="index"
                    class="thumbnail-item"
                    :class="{ active: currentImage === img }"
                    @click="currentImage = img"
                  >
                    <el-image :src="img" fit="cover" />
                  </div>
                </div>
              </div>
            </el-col>

            <!-- 场地详情 -->
            <el-col :span="12">
              <div class="venue-info-detail">
                <h2 class="venue-name">{{ venueDetail.venueName }}</h2>
                <p class="venue-code">场地编码：{{ venueDetail.venueCode }}</p>

                <el-divider />

                <div class="info-row">
                  <label>场地类型：</label>
                  <span>{{ venueTypeMap[venueDetail.venueType] || venueDetail.venueType }}</span>
                </div>

                <div class="info-row">
                  <label>场地位置：</label>
                  <span>{{ venueDetail.location || '未设置' }}</span>
                </div>

                <div class="info-row">
                  <label>场地面积：</label>
                  <span>{{ venueDetail.area }}㎡</span>
                </div>

                <div class="info-row">
                  <label>可容纳人数：</label>
                  <span>{{ venueDetail.capacity }}人</span>
                </div>

                <div class="info-row">
                  <label>地面类型：</label>
                  <span>{{ venueDetail.floorType || '未设置' }}</span>
                </div>

                <div v-if="venueDetail.facilities" class="info-row">
                  <label>场地设施：</label>
                  <div class="facilities">
                    <el-tag
                      v-for="facility in facilitiesList"
                      :key="facility"
                      size="small"
                      type="info"
                    >
                      {{ facility }}
                    </el-tag>
                  </div>
                </div>

                <div v-if="venueDetail.description" class="info-row">
                  <label>场地描述：</label>
                  <p class="description">{{ venueDetail.description }}</p>
                </div>

                <el-divider />

                <div class="action-buttons">
                  <el-button
                    v-if="venueDetail.status === 1"
                    type="primary"
                    size="large"
                    @click="handleBooking"
                  >
                    立即预订
                  </el-button>
                  <el-button size="large" @click="handleBack">
                    返回列表
                  </el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 价格信息 -->
        <el-card v-loading="priceLoading" class="price-card" shadow="never">
          <template #header>
            <span>价格信息</span>
          </template>

          <div v-if="priceList.length > 0" class="price-list">
            <el-table :data="priceList" border stripe>
              <el-table-column label="时段类型" width="120">
                <template #default="{ row }">
                  {{ timeTypeMap[row.timeType] || row.timeType }}
                </template>
              </el-table-column>
              <el-table-column prop="timePeriod" label="时段名称" width="150" />
              <el-table-column label="时间段" width="200">
                <template #default="{ row }">
                  {{ row.startTime }} - {{ row.endTime }}
                </template>
              </el-table-column>
              <el-table-column label="标准价格" width="120">
                <template #default="{ row }">
                  <span class="price">¥{{ row.price }}/小时</span>
                </template>
              </el-table-column>
              <el-table-column label="会员价格" width="120">
                <template #default="{ row }">
                  <span class="member-price">¥{{ row.memberPrice }}/小时</span>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                    {{ row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <el-empty v-else description="暂无价格配置" />
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getVenueDetail, getVenuePrices } from '@/api/venue';
import { useUserStore } from '@/store/modules/user';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 数据
const loading = ref(false);
const priceLoading = ref(false);
const venueDetail = ref(null);
const priceList = ref([]);
const currentImage = ref('');

// 默认图片
const defaultImage = 'https://via.placeholder.com/600x400?text=Basketball+Court';

// 场地类型映射
const venueTypeMap = {
  1: '室内全场',
  2: '室内半场',
  3: '室外全场',
  4: '室外半场'
};

// 时段类型映射
const timeTypeMap = {
  1: '工作日',
  2: '周末',
  3: '节假日'
};

// 图片列表
const imageList = computed(() => {
  if (!venueDetail.value) return [defaultImage];
  if (venueDetail.value.imageList && venueDetail.value.imageList.length > 0) {
    return venueDetail.value.imageList;
  }
  if (venueDetail.value.imageUrl) {
    return [venueDetail.value.imageUrl];
  }
  return [defaultImage];
});

// 设施列表
const facilitiesList = computed(() => {
  if (!venueDetail.value || !venueDetail.value.facilities) return [];
  return venueDetail.value.facilities.split(',').filter(item => item.trim());
});

// 获取场地详情
const fetchVenueDetail = async () => {
  const venueId = route.params.id;
  if (!venueId) {
    ElMessage.error('场地ID不存在');
    router.back();
    return;
  }

  loading.value = true;
  try {
    const res = await getVenueDetail(venueId);
    if (res.code === 200) {
      venueDetail.value = res.data;
      currentImage.value = imageList.value[0];

      // 获取价格信息
      fetchVenuePrices(venueId);
    }
  } catch (error) {
    console.error('获取场地详情失败：', error);
    ElMessage.error('获取场地详情失败');
  } finally {
    loading.value = false;
  }
};

// 获取价格信息
const fetchVenuePrices = async (venueId) => {
  priceLoading.value = true;
  try {
    const res = await getVenuePrices(venueId);
    if (res.code === 200) {
      priceList.value = res.data || [];
    }
  } catch (error) {
    console.error('获取价格信息失败：', error);
  } finally {
    priceLoading.value = false;
  }
};


// 预订
const handleBooking = () => {
  router.push(`/booking/create/${venueDetail.value.id}`);
};

// 初始化
onMounted(() => {
  fetchVenueDetail();
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.venue-detail-container {
  @include container;
  padding: $spacing-8 $spacing-6;
  min-height: 100vh;
  background: $bg-secondary;

  .detail-content {
    .info-card,
    .price-card {
      @include card-base;
      margin-bottom: $spacing-6;
      border: none;

      :deep(.el-card__header) {
        padding: $spacing-5 $spacing-6;
        border-bottom: 1px solid $border-color;
        background: $bg-primary;

        .card-header {
          @include flex-between;
          font-size: $font-size-lg;
          font-weight: $font-weight-semibold;
          color: $text-primary;
        }
      }

      :deep(.el-card__body) {
        padding: $spacing-6;
      }
    }

    .venue-images {
      .main-image {
        width: 100%;
        height: 420px;
        border-radius: $radius-lg;
        overflow: hidden;
        background: $gray-100;
        box-shadow: $shadow-sm;

        :deep(.el-image__inner) {
          @include image-cover;
        }
      }

      .thumbnail-list {
        display: flex;
        gap: $spacing-3;
        margin-top: $spacing-4;

        .thumbnail-item {
          width: 88px;
          height: 88px;
          border-radius: $radius-md;
          overflow: hidden;
          cursor: pointer;
          border: 2px solid transparent;
          transition: all $duration-fast $ease-out;
          background: $gray-100;

          &:hover {
            border-color: $primary;
            transform: translateY(-2px);
            box-shadow: $shadow-sm;
          }

          &.active {
            border-color: $primary;
            box-shadow: 0 0 0 4px rgba($primary, 0.1);
          }

          .el-image {
            width: 100%;
            height: 100%;

            :deep(.el-image__inner) {
              @include image-cover;
            }
          }
        }
      }
    }

    .venue-info-detail {
      .venue-name {
        @include text-heading-1;
        font-size: $font-size-3xl;
        margin-bottom: $spacing-3;
      }

      .venue-code {
        @include text-caption;
        @include tag-base;
        display: inline-block;
        background: $gray-100;
        margin-bottom: $spacing-5;
      }

      :deep(.el-divider) {
        margin: $spacing-6 0;
        border-color: $border-color;
      }

      .info-row {
        display: flex;
        margin-bottom: $spacing-4;

        label {
          min-width: 110px;
          font-weight: $font-weight-semibold;
          color: $text-secondary;
          font-size: $font-size-sm;
        }

        span {
          color: $text-primary;
          font-size: $font-size-base;
        }

        .facilities {
          display: flex;
          flex-wrap: wrap;
          gap: $spacing-2;

          .el-tag {
            @include tag-base;
            background: $gray-100;
            color: $text-secondary;
            border: none;
          }
        }

        .description {
          flex: 1;
          @include text-body;
          line-height: $line-height-relaxed;
          margin: 0;
        }
      }

      .action-buttons {
        display: flex;
        gap: $spacing-4;
        margin-top: $spacing-6;

        .el-button {
          flex: 1;
          @include button-base;
          height: 48px;
          font-size: $font-size-base;

          &.el-button--primary {
            @include button-primary;
          }

          &:not(.el-button--primary) {
            @include button-secondary;
          }
        }
      }
    }

    .price-list {
      :deep(.el-table) {
        border-radius: $radius-md;
        overflow: hidden;

        th {
          background: $gray-50;
          color: $text-secondary;
          font-weight: $font-weight-semibold;
          font-size: $font-size-sm;
        }

        td {
          color: $text-primary;
          font-size: $font-size-sm;
        }

        .el-table__row {
          transition: $transition-fast;

          &:hover {
            background: $gray-50;
          }
        }
      }

      .price {
        color: $error;
        font-weight: $font-weight-bold;
        font-size: $font-size-lg;
      }

      .member-price {
        color: $success;
        font-weight: $font-weight-bold;
        font-size: $font-size-lg;
      }
    }
  }
}
</style>
