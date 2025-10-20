<template>
  <div class="venue-detail-container">
    <el-page-header @back="handleBack" title="返回">
      <template #content>
        <span class="page-title">场地详情</span>
      </template>
    </el-page-header>

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

                <!-- 预订面板 -->
                <div v-if="userStore.isLogin && venueDetail.status === 1" class="booking-panel">
                  <h3 class="panel-title">立即预订</h3>

                  <!-- 日期选择 -->
                  <div class="booking-form">
                    <el-form :model="bookingForm" label-width="80px">
                      <el-form-item label="选择日期">
                        <el-date-picker
                          v-model="bookingForm.date"
                          type="date"
                          placeholder="请选择日期"
                          :disabled-date="disabledDate"
                          style="width: 100%"
                        />
                      </el-form-item>

                      <el-form-item label="时间段">
                        <el-time-picker
                          v-model="bookingForm.startTime"
                          placeholder="开始时间"
                          format="HH:mm"
                          value-format="HH:mm"
                          style="width: 48%; margin-right: 4%"
                        />
                        <span style="margin: 0 4px">至</span>
                        <el-time-picker
                          v-model="bookingForm.endTime"
                          placeholder="结束时间"
                          format="HH:mm"
                          value-format="HH:mm"
                          style="width: 48%"
                          :disabled="!bookingForm.startTime"
                        />
                      </el-form-item>

                      <el-form-item label="预订备注">
                        <el-input
                          v-model="bookingForm.remark"
                          type="textarea"
                          :rows="2"
                          placeholder="请输入特殊要求（选填）"
                        />
                      </el-form-item>

                      <el-form-item>
                        <el-button type="primary" @click="confirmBooking" :loading="bookingLoading">
                          确认预订
                        </el-button>
                        <el-button @click="resetBookingForm">重置</el-button>
                      </el-form-item>
                    </el-form>
                  </div>
                </div>

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
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getVenueDetail, getVenuePrices } from '@/api/venue';
import { useUserStore } from '@/store/modules/user';
import request from '@/utils/request';
import { getMyBookingList, checkVenueAvailable } from '@/api/booking';

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

// 预订表单
const bookingForm = reactive({
  date: '',
  startTime: '',
  endTime: '',
  remark: ''
});

const bookingLoading = ref(false);

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000;
};

// 重置预订表单
const resetBookingForm = () => {
  bookingForm.date = '';
  bookingForm.startTime = '';
  bookingForm.endTime = '';
  bookingForm.remark = '';
};

// 确认预订
const confirmBooking = async () => {
  if (!bookingForm.date || !bookingForm.startTime || !bookingForm.endTime) {
    ElMessage.warning('请选择预订日期和时间段');
    return;
  }

  if (bookingForm.startTime >= bookingForm.endTime) {
    ElMessage.warning('结束时间必须晚于开始时间');
    return;
  }

  bookingLoading.value = true;
  try {
    const bookingData = {
      venueId: venueDetail.value.id,
      venueName: venueDetail.value.venueName,
      date: bookingForm.date,
      startTime: bookingForm.startTime,
      endTime: bookingForm.endTime,
      remark: bookingForm.remark || '',
      totalPrice: calculateTotalPrice(bookingForm.startTime, bookingForm.endTime, priceList.value)
    };

    const res = await request.post('/api/booking', bookingData);
    if (res.code === 200) {
      ElMessage.success('预订成功');
      resetBookingForm();
      router.push(`/booking/list`);
    } else {
      ElMessage.error(res.msg || '预订失败');
    }
  } catch (error) {
    console.error('预订失败：', error);
    ElMessage.error('预订失败');
  } finally {
    bookingLoading.value = false;
  }
};

// 计算总价格
const calculateTotalPrice = (startTime, endTime, priceList) => {
  if (!priceList || priceList.length === 0) return 0;

  // 简化价格计算逻辑：根据时间段匹配价格
  const hourStart = parseInt(startTime.split(':')[0]);
  const hourEnd = parseInt(endTime.split(':')[0]);

  let total = 0;
  for (let hour = hourStart; hour < hourEnd; hour++) {
    // 查找对应时段的价格（简化处理）
    const priceItem = priceList.find(item => {
      const itemHourStart = parseInt(item.startTime.split(':')[0]);
      return itemHourStart === hour;
    });
    if (priceItem) {
      // 判断是否为会员
      const memberPrice = userStore.isMember ? priceItem.memberPrice : priceItem.price;
      total += memberPrice;
    }
  }
  return total;
};

// 返回
const handleBack = () => {
  router.back();
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
.venue-detail-container {
  padding: 20px;

  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  .detail-content {
    margin-top: 20px;

    .info-card,
    .price-card {
      margin-bottom: 20px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }

    .venue-images {
      .main-image {
        width: 100%;
        height: 400px;
        border-radius: 8px;
        overflow: hidden;
      }

      .thumbnail-list {
        display: flex;
        gap: 10px;
        margin-top: 10px;

        .thumbnail-item {
          width: 80px;
          height: 80px;
          border-radius: 4px;
          overflow: hidden;
          cursor: pointer;
          border: 2px solid transparent;
          transition: all 0.3s;

          &:hover,
          &.active {
            border-color: #409eff;
          }

          .el-image {
            width: 100%;
            height: 100%;
          }
        }
      }
    }

    .venue-info-detail {
      .venue-name {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 10px;
      }

      .venue-code {
        font-size: 14px;
        color: #909399;
        margin-bottom: 0;
      }

      .info-row {
        display: flex;
        margin-bottom: 15px;

        label {
          min-width: 100px;
          font-weight: 600;
          color: #606266;
        }

        span {
          color: #303133;
        }

        .facilities {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
        }

        .description {
          flex: 1;
          line-height: 1.6;
          color: #606266;
          margin: 0;
        }
      }

      .action-buttons {
        display: flex;
        gap: 10px;

        .el-button {
          flex: 1;
        }
      }
    }

    .price-list {
      .price {
        color: #f56c6c;
        font-weight: 600;
        font-size: 16px;
      }

      .member-price {
        color: #67c23a;
        font-weight: 600;
        font-size: 16px;
      }
    }
  }
}
</style>
