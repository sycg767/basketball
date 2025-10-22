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
            <p v-if="standardPrice > 0" class="price">
              <span class="price-label">价格:</span>
              <span class="price-value">¥{{ standardPrice.toFixed(2) }}/小时</span>
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


          <el-form-item label="使用积分">
            <div class="points-section">
              <el-checkbox
                v-model="usePoints"
                @change="handlePointsChange"
                :disabled="!canUsePointsNow"
              >
                使用积分抵扣（可用积分：{{ userPoints }}）
              </el-checkbox>

              <!-- 积分不足提示 -->
              <el-alert
                v-if="!canUsePointsNow"
                type="warning"
                :closable="false"
                show-icon
                style="margin-top: 8px;"
              >
                <template #title>
                  <span style="font-size: 13px;">
                    积分不足，最少需要 100 积分才能使用抵扣功能
                  </span>
                </template>
              </el-alert>

              <div v-if="usePoints && canUsePointsNow && maxPointsCanUse >= 100" class="points-input">
                <el-slider
                  v-model="pointsToUse"
                  :min="100"
                  :max="Math.max(100, maxPointsCanUse)"
                  :step="100"
                  :disabled="!totalPrice || totalPrice === 0"
                  @change="calculatePointsDeduct"
                />
                <div class="points-info">
                  <span>使用 {{ pointsToUse }} 积分</span>
                  <span class="deduct-amount">抵扣 ¥{{ pointsDeductAmount.toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="预计费用">
            <div class="price-summary">
              <div class="price-item">
                <span>时长:</span>
                <span>{{ duration }} 小时</span>
              </div>
              <div class="price-item">
                <span>单价:</span>
                <span>¥{{ memberPrice.toFixed(2) }}/小时</span>
              </div>
              <div class="price-item" v-if="priceInfo.hasCard">
                <span>会员卡:</span>
                <span class="member-price">{{ priceInfo.cardName }} ({{ (memberDiscount * 10).toFixed(1) }}折)</span>
              </div>
              <div class="price-item" v-if="priceInfo.discountAmount > 0">
                <span>优惠金额:</span>
                <span class="discount-price">-¥{{ priceInfo.discountAmount.toFixed(2) }}</span>
              </div>
              <div class="price-item" v-if="usePoints && pointsDeductAmount > 0">
                <span>积分抵扣:</span>
                <span class="points-deduct">-¥{{ pointsDeductAmount.toFixed(2) }}</span>
              </div>
              <div class="price-item total">
                <span>总计:</span>
                <span class="total-price">¥{{ finalPayAmount.toFixed(2) }}</span>
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Location } from '@element-plus/icons-vue';
import { getVenueDetail } from '@/api/venue';
import { createBooking, checkVenueAvailable, calculateBookingPrice } from '@/api/booking';
import { useUserStore } from '@/store/modules/user';
import { getMemberLevel } from '@/utils/constants';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);
const submitting = ref(false);
const venueInfo = ref(null);

// 可用性检查
const availabilityChecked = ref(false);
const isAvailable = ref(false);
const checkingAvailability = ref(false);

// 价格信息
const priceInfo = ref({
  duration: 0,
  pricePerHour: 0,
  totalPrice: 0,
  actualPrice: 0,
  discountAmount: 0,
  hasCard: false,
  cardName: '',
  discount: 1
});

// 积分相关
const usePoints = ref(false);
const userPoints = ref(0);
const pointsToUse = ref(100);
const pointsDeductAmount = ref(0);

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
  return priceInfo.value.duration || 0;
});

// 计算标准价格（未折扣的原价单价）
const standardPrice = computed(() => {
  const price = priceInfo.value.standardPricePerHour || 0;
  console.log('[前端计算] 标准价格 standardPrice:', price);
  return price;
});

// 计算会员折扣
const memberDiscount = computed(() => {
  const discount = priceInfo.value.discount || 1;
  console.log('[前端计算] 会员折扣 memberDiscount:', discount);
  return discount;
});

// 计算会员折扣价（会员等级折扣后的单价）
const memberPrice = computed(() => {
  const price = priceInfo.value.pricePerHour || 0;
  console.log('[前端计算] 会员单价 memberPrice:', price);
  return price;
});

// 计算总价（使用实际价格）
const totalPrice = computed(() => {
  return priceInfo.value.actualPrice || 0;
});

// 是否可以使用积分（至少需要100积分）
const canUsePointsNow = computed(() => {
  return userPoints.value >= 100;
});

// 计算最多可用积分
const maxPointsCanUse = computed(() => {
  if (!totalPrice.value || totalPrice.value === 0) return 0;

  // 最多抵扣50%
  const maxDeduct = totalPrice.value * 0.5;
  const maxPoints = Math.floor(maxDeduct * 100);

  return Math.min(maxPoints, userPoints.value);
});

// 计算最终支付金额
const finalPayAmount = computed(() => {
  if (usePoints.value && pointsDeductAmount.value > 0) {
    return Math.max(0, totalPrice.value - pointsDeductAmount.value);
  }
  return totalPrice.value;
});

// 禁用过去的日期
const disabledDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0));
};

// 检查时段可用性并计算价格
const checkAvailability = async () => {
  if (!bookingForm.bookingDate || !bookingForm.startTime || !bookingForm.endTime) {
    availabilityChecked.value = false;
    priceInfo.value = {
      duration: 0,
      standardPricePerHour: 0,
      pricePerHour: 0,
      totalPrice: 0,
      actualPrice: 0,
      discountAmount: 0,
      hasCard: false,
      cardName: '',
      discount: 1
    };
    return;
  }

  checkingAvailability.value = true;
  try {
    // 并行调用可用性检查和价格计算
    const [availableRes, priceRes] = await Promise.all([
      checkVenueAvailable({
        venueId: bookingForm.venueId,
        bookingDate: bookingForm.bookingDate,
        startTime: bookingForm.startTime,
        endTime: bookingForm.endTime
      }),
      calculateBookingPrice({
        venueId: bookingForm.venueId,
        bookingDate: bookingForm.bookingDate,
        startTime: bookingForm.startTime,
        endTime: bookingForm.endTime
      })
    ]);

    isAvailable.value = availableRes.data;
    availabilityChecked.value = true;

    // 更新价格信息
    if (priceRes.data) {
      console.log('=== 价格计算结果 ===');
      console.log('后端返回完整数据:', priceRes.data);
      console.log('标准单价 (standardPricePerHour):', priceRes.data.standardPricePerHour);
      console.log('会员单价 (pricePerHour):', priceRes.data.pricePerHour);
      console.log('时长 (duration):', priceRes.data.duration);
      console.log('总价 (totalPrice):', priceRes.data.totalPrice);
      console.log('实付价格 (actualPrice):', priceRes.data.actualPrice);
      console.log('会员等级 (memberLevel):', priceRes.data.memberLevel);
      console.log('是否有会员卡 (hasCard):', priceRes.data.hasCard);
      if (priceRes.data.hasCard) {
        console.log('会员卡名称 (cardName):', priceRes.data.cardName);
        console.log('会员卡折扣 (discount):', priceRes.data.discount);
        console.log('折扣金额 (discountAmount):', priceRes.data.discountAmount);
      }
      console.log('==================');

      priceInfo.value = priceRes.data;
    }
  } catch (error) {
    ElMessage.error(error.message || '检查可用性失败');
    isAvailable.value = false;
    availabilityChecked.value = false;
  } finally {
    checkingAvailability.value = false;
  }
};

// 处理积分选择变化
const handlePointsChange = (checked) => {
  if (!checked) {
    pointsDeductAmount.value = 0;
    pointsToUse.value = 100;
  } else {
    // 检查是否满足最低使用门槛
    if (!canUsePointsNow.value) {
      usePoints.value = false;
      ElMessage.warning('积分不足，最少需要 100 积分才能使用抵扣功能');
      return;
    }
    calculatePointsDeduct();
  }
};

// 计算积分抵扣金额
const calculatePointsDeduct = () => {
  if (!usePoints.value || pointsToUse.value < 100) {
    pointsDeductAmount.value = 0;
    return;
  }

  // 100积分 = 1元
  let deductAmount = pointsToUse.value / 100;

  // 最多抵扣50%
  const maxDeduct = totalPrice.value * 0.5;
  if (deductAmount > maxDeduct) {
    deductAmount = maxDeduct;
  }

  pointsDeductAmount.value = deductAmount;
};

// 获取用户积分
const fetchUserPoints = async () => {
  try {
    // 每次都重新获取最新的用户信息，确保积分是最新的
    await userStore.getUserInfo();
    userPoints.value = userStore.points || 0;

    console.log('用户积分:', userPoints.value);
  } catch (error) {
    console.error('获取用户积分失败:', error);
    userPoints.value = 0;
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

    // 跳转到预订详情页，携带积分信息
    const query = {};
    if (usePoints.value && pointsToUse.value > 0) {
      query.pointsToUse = pointsToUse.value;
    }
    router.push({
      path: `/booking/detail/${res.data}`,
      query
    });
  } catch (error) {
    ElMessage.error(error.message || '预订失败');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  fetchVenueDetail();
  fetchUserPoints();
});

// 监听页面可见性，当页面重新可见时刷新积分
onMounted(() => {
  const handleVisibilityChange = () => {
    if (document.visibilityState === 'visible') {
      fetchUserPoints();
    }
  };
  document.addEventListener('visibilitychange', handleVisibilityChange);

  // 组件卸载时移除监听
  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange);
  });
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

      .points-section {
        .points-input {
          margin-top: 16px;
          padding: 16px;
          background: #f5f7fa;
          border-radius: 8px;

          .points-info {
            display: flex;
            justify-content: space-between;
            margin-top: 12px;
            font-size: 14px;

            .deduct-amount {
              color: #67c23a;
              font-weight: 600;
            }
          }
        }
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

          .member-price {
            color: #67c23a;
            font-weight: 600;
          }

          .discount-price {
            color: #f56c6c;
            font-weight: 600;
          }

          .points-deduct {
            color: #67c23a;
            font-weight: 600;
          }

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
