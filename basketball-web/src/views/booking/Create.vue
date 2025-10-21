<template>
  <div class="booking-create-container">
    <BackButton text="返回" />

    <div class="create-content">
      <el-card class="venue-card">
        <template #header>
          <div class="card-header">
            <span>场地信息</span>
          </div>
        </template>
        <div class="venue-info" v-if="venueInfo">
          <img :src="venueInfo.imageUrl || '/default-venue.jpg'" alt="场地图片" class="venue-image" />
          <div class="venue-details">
            <h2>{{ venueInfo.venueName }}</h2>
            <p class="location">
              <el-icon><Location /></el-icon>
              {{ venueInfo.location }}
            </p>
            <p class="price">
              <span class="price-label">价格:</span>
              <span class="price-value">¥{{ venueInfo.basePrice }}/小时</span>
            </p>
          </div>
        </div>
      </el-card>

      <el-card class="form-card">
        <template #header>
          <div class="card-header">
            <span>预订信息</span>
          </div>
        </template>
        <el-form
          ref="formRef"
          :model="bookingForm"
          :rules="formRules"
          label-width="100px"
        >
          <el-form-item label="预订日期" prop="bookingDate" required>
            <el-date-picker
              v-model="bookingForm.bookingDate"
              type="date"
              placeholder="选择预订日期"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              @change="checkAvailability"
            />
          </el-form-item>

          <el-form-item label="预订时段" required>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item prop="startTime">
                  <el-time-select
                    v-model="bookingForm.startTime"
                    placeholder="开始时间"
                    start="06:00"
                    end="22:00"
                    step="01:00"
                    @change="checkAvailability"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="endTime">
                  <el-time-select
                    v-model="bookingForm.endTime"
                    placeholder="结束时间"
                    start="07:00"
                    end="23:00"
                    step="01:00"
                    :min-time="bookingForm.startTime"
                    @change="checkAvailability"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <div v-if="availabilityChecked" class="availability-tip">
              <el-alert
                v-if="isAvailable"
                title="该时段可预订"
                type="success"
                :closable="false"
                show-icon
              />
              <el-alert
                v-else
                title="该时段已被预订,请选择其他时间"
                type="error"
                :closable="false"
                show-icon
              />
            </div>
          </el-form-item>

          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
          </el-form-item>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>

          <el-form-item label="人数" prop="peopleCount">
            <el-input-number
              v-model="bookingForm.peopleCount"
              :min="1"
              :max="venueInfo?.capacity || 100"
              placeholder="请输入人数"
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="bookingForm.remark"
              type="textarea"
              :rows="4"
              placeholder="请输入备注信息"
            />
          </el-form-item>


          <el-form-item label="预计费用">
            <div class="price-summary">
              <div class="price-item">
                <span>时长:</span>
                <span>{{ duration }} 小时</span>
              </div>
              <div class="price-item">
                <span>单价:</span>
                <span>¥{{ venueInfo?.basePrice || 0 }}/小时</span>
              </div>
              <div class="price-item total">
                <span>总计:</span>
                <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="action-buttons">
        <el-button @click="goBack" size="large">取消</el-button>
        <el-button
          type="primary"
          size="large"
          @click="handleSubmit"
          :loading="submitting"
          :disabled="!isAvailable"
        >
          确认预订
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Location } from '@element-plus/icons-vue';
import { getVenueDetail } from '@/api/venue';
import { createBooking, checkVenueAvailable } from '@/api/booking';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();
const formRef = ref(null);
const submitting = ref(false);
const venueInfo = ref(null);

// 可用性检查
const availabilityChecked = ref(false);
const isAvailable = ref(false);
const checkingAvailability = ref(false);

const bookingForm = reactive({
  venueId: route.params.venueId,
  bookingDate: '',
  startTime: '',
  endTime: '',
  bookingType: 1,
  contactName: '',
  contactPhone: '',
  peopleCount: 1,
  remark: ''
});

const formRules = {
  bookingDate: [
    { required: true, message: '请选择预订日期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  contactPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
};

// 计算时长
const duration = computed(() => {
  if (!bookingForm.startTime || !bookingForm.endTime) return 0;

  const start = new Date(`2000-01-01 ${bookingForm.startTime}`);
  const end = new Date(`2000-01-01 ${bookingForm.endTime}`);
  const hours = (end - start) / (1000 * 60 * 60);

  return hours > 0 ? hours : 0;
});

// 计算总价
const totalPrice = computed(() => {
  if (!venueInfo.value?.basePrice) return 0;
  return duration.value * venueInfo.value.basePrice;
});

// 禁用过去的日期
const disabledDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0));
};

// 检查时段可用性
const checkAvailability = async () => {
  if (!bookingForm.bookingDate || !bookingForm.startTime || !bookingForm.endTime) {
    availabilityChecked.value = false;
    return;
  }

  checkingAvailability.value = true;
  try {
    const res = await checkVenueAvailable({
      venueId: bookingForm.venueId,
      bookingDate: bookingForm.bookingDate,
      startTime: bookingForm.startTime,
      endTime: bookingForm.endTime
    });
    isAvailable.value = res.data;
    availabilityChecked.value = true;
  } catch (error) {
    ElMessage.error(error.message || '检查可用性失败');
    isAvailable.value = false;
    availabilityChecked.value = false;
  } finally {
    checkingAvailability.value = false;
  }
};

// 获取场地详情
const fetchVenueDetail = async () => {
  try {
    const res = await getVenueDetail(route.params.venueId);
    venueInfo.value = res.data;
  } catch (error) {
    ElMessage.error(error.message || '获取场地信息失败');
    router.back();
  }
};




// 提交预订
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    if (!isAvailable.value) {
      ElMessage.warning('该时段不可预订,请重新选择');
      return;
    }

    submitting.value = true;

    const res = await createBooking({
      venueId: parseInt(bookingForm.venueId),
      bookingDate: bookingForm.bookingDate,
      startTime: bookingForm.startTime,
      endTime: bookingForm.endTime,
      bookingType: bookingForm.bookingType,
      contactName: bookingForm.contactName,
      contactPhone: bookingForm.contactPhone,
      peopleCount: bookingForm.peopleCount,
      remark: bookingForm.remark
    });

    ElMessage.success('预订成功');

    // 跳转到预订详情页
    router.push(`/booking/detail/${res.data}`);
  } catch (error) {
    ElMessage.error(error.message || '预订失败');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  fetchVenueDetail();
});
</script>

<style lang="scss" scoped>
.booking-create-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;

  .page-title {
    font-size: 18px;
    font-weight: 600;
  }

  .create-content {
    margin-top: 20px;

    .venue-card {
      margin-bottom: 20px;

      .venue-info {
        display: flex;
        gap: 20px;

        .venue-image {
          width: 200px;
          height: 150px;
          object-fit: cover;
          border-radius: 8px;
        }

        .venue-details {
          flex: 1;

          h2 {
            font-size: 20px;
            font-weight: 600;
            margin: 0 0 12px 0;
          }

          .location {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 14px;
            color: #666;
            margin: 0 0 12px 0;
          }

          .price {
            font-size: 14px;
            margin: 0;

            .price-label {
              color: #666;
            }

            .price-value {
              font-size: 20px;
              font-weight: 700;
              color: #f56c6c;
              margin-left: 8px;
            }
          }
        }
      }
    }

    .form-card {
      margin-bottom: 20px;

      .card-header {
        font-size: 16px;
        font-weight: 600;
      }

      .availability-tip {
        margin-top: 8px;
      }

      .price-summary {
        background: #f5f7fa;
        padding: 16px;
        border-radius: 8px;

        .price-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          font-size: 14px;

          &.total {
            margin-top: 8px;
            padding-top: 8px;
            border-top: 1px dashed #dcdfe6;
            font-size: 16px;
            font-weight: 600;

            .total-price {
              font-size: 24px;
              color: #f56c6c;
            }
          }
        }
      }
    }

    .action-buttons {
      display: flex;
      justify-content: center;
      gap: 16px;
      margin-top: 30px;
    }
  }
}
</style>
