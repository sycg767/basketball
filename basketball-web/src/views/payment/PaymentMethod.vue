<template>
  <div class="payment-method-container">
    <BackButton text="返回" />
    <h2 class="page-title">支付管理</h2>

    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="payment-tabs" @tab-change="handleTabChange">
      <!-- 支付方式选择 -->
      <el-tab-pane label="支付方式" name="method" v-if="showPaymentForm">
        <el-card class="payment-card">
      <!-- 订单信息 -->
      <div class="order-info">
        <h2 class="section-title">订单信息</h2>
        <div class="info-item">
          <span class="label">订单编号:</span>
          <span class="value">{{ orderInfo.orderNo || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">商品名称:</span>
          <span class="value">{{ orderInfo.businessName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">订单金额:</span>
          <span class="value amount">¥{{ orderInfo.amount || '0.00' }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h2 class="section-title">选择支付方式</h2>

        <el-radio-group v-model="selectedMethod" class="method-group">
          <!-- 微信支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'wechat' }"
            @click="selectedMethod = 'wechat'"
          >
            <el-radio value="wechat" size="large">
              <div class="method-content">
                <div class="method-icon wechat">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-wechat"></use>
                  </svg>
                </div>
                <div class="method-info">
                  <h4>微信支付</h4>
                  <p>推荐使用微信扫码支付</p>
                </div>
              </div>
            </el-radio>
          </div>

          <!-- 支付宝支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'alipay' }"
            @click="selectedMethod = 'alipay'"
          >
            <el-radio value="alipay" size="large">
              <div class="method-content">
                <div class="method-icon alipay">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-alipay"></use>
                  </svg>
                </div>
                <div class="method-info">
                  <h4>支付宝支付</h4>
                  <p>支持支付宝扫码或跳转支付</p>
                </div>
              </div>
            </el-radio>
          </div>

          <!-- 余额支付 -->
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'balance' }"
            @click="selectedMethod = 'balance'"
          >
            <el-radio value="balance" size="large">
              <div class="method-content">
                <div class="method-icon balance">
                  <el-icon :size="32"><Wallet /></el-icon>
                </div>
                <div class="method-info">
                  <h4>余额支付</h4>
                  <p>当前余额: ¥{{ userBalance }}</p>
                </div>
              </div>
            </el-radio>
          </div>
        </el-radio-group>
      </div>

      <!-- 支付按钮 -->
      <div class="payment-actions">
        <el-button size="large" @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          :disabled="!selectedMethod"
          @click="handleConfirmPay"
        >
          确认支付 ¥{{ orderInfo.amount || '0.00' }}
        </el-button>
      </div>
        </el-card>
      </el-tab-pane>

      <!-- 支付记录 -->
      <el-tab-pane label="支付记录" name="records">
        <el-card class="records-card">
          <div class="records-header">
            <h2 class="section-title">支付记录</h2>
            <el-button type="primary" :icon="Refresh" @click="loadPaymentRecords">刷新</el-button>
          </div>

          <el-table
            v-loading="recordsLoading"
            :data="paymentRecords"
            style="width: 100%"
            stripe
          >
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column label="业务类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.businessType === 'balance_recharge' ? 'warning' : 'primary'">
                  {{ row.businessType === 'balance_recharge' ? '余额充值' : '场地预订' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="businessName" label="业务名称" width="150" />
            <el-table-column prop="amount" label="支付金额" width="120">
              <template #default="{ row }">
                <span class="amount-text">¥{{ row.amount?.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="payMethodName" label="支付方式" width="120" />
            <el-table-column prop="payTime" label="支付时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status, row.businessType)">
                  {{ getStatusText(row.status, row.businessType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="viewDetail(row)"
                  v-if="row.businessType !== 'balance_recharge'"
                >
                  查看详情
                </el-button>
                <span v-else style="color: #909399;">-</span>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="recordsTotal > 0"
            v-model:current-page="recordsPage"
            v-model:page-size="recordsPageSize"
            :total="recordsTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadPaymentRecords"
            @size-change="loadPaymentRecords"
            style="margin-top: 20px; justify-content: center"
          />

          <el-empty v-if="!recordsLoading && paymentRecords.length === 0" description="暂无支付记录" />
        </el-card>
      </el-tab-pane>

      <!-- 支付方式管理 -->
      <el-tab-pane label="支付方式管理" name="manage">
        <el-card class="manage-card">
          <div class="manage-header">
            <h2 class="section-title">我的支付方式</h2>
            <el-button type="primary" @click="showAddDialog = true">添加支付方式</el-button>
          </div>

          <!-- 支付方式列表 -->
          <div class="payment-methods-list">
            <!-- 余额支付 -->
            <div class="method-card balance-card">
              <div class="method-header">
                <div class="method-icon-wrapper">
                  <el-icon :size="32" color="#F56C6C"><Wallet /></el-icon>
                </div>
                <div class="method-info">
                  <h4>余额支付</h4>
                  <p>当前余额: ¥{{ userBalance }}</p>
                </div>
                <el-tag type="success">已启用</el-tag>
              </div>
              <div class="method-actions">
                <el-button link type="primary" @click="goToRecharge">充值</el-button>
              </div>
            </div>

            <!-- 微信支付 -->
            <div class="method-card" v-for="method in paymentMethods" :key="method.id">
              <div class="method-header">
                <div class="method-icon-wrapper" :class="method.type">
                  <el-icon :size="32" v-if="method.type === 'wechat'" color="#07C160">
                    <ChatDotRound />
                  </el-icon>
                  <el-icon :size="32" v-else-if="method.type === 'alipay'" color="#1677FF">
                    <Wallet />
                  </el-icon>
                  <el-icon :size="32" v-else color="#409EFF">
                    <CreditCard />
                  </el-icon>
                </div>
                <div class="method-info">
                  <h4>{{ method.name }}</h4>
                  <p>{{ method.account }}</p>
                </div>
                <el-tag v-if="method.isDefault" type="success">默认</el-tag>
                <el-tag v-else type="info">备用</el-tag>
              </div>
              <div class="method-actions">
                <el-button link type="primary" v-if="!method.isDefault" @click="setDefault(method.id)">
                  设为默认
                </el-button>
                <el-button link type="danger" @click="removeMethod(method.id)">解绑</el-button>
              </div>
            </div>

            <el-empty v-if="paymentMethods.length === 0" description="暂无绑定的支付方式" />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 账单管理 -->
      <el-tab-pane label="账单管理" name="bills">
        <el-card class="bills-card">
          <div class="bills-header">
            <h2 class="section-title">账单管理</h2>
            <div class="filter-group">
              <el-date-picker
                v-model="billDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="loadBills"
              />
              <el-button type="primary" @click="loadBills">查询</el-button>
            </div>
          </div>

          <!-- 账单统计 -->
          <div class="bill-summary">
            <div class="summary-item">
              <div class="summary-label">总支出</div>
              <div class="summary-value expense">¥{{ billSummary.totalExpense.toFixed(2) }}</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">总收入</div>
              <div class="summary-value income">¥{{ billSummary.totalIncome.toFixed(2) }}</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">交易笔数</div>
              <div class="summary-value">{{ billSummary.totalCount }}</div>
            </div>
          </div>

          <el-table
            v-loading="billsLoading"
            :data="bills"
            style="width: 100%; margin-top: 20px"
            stripe
          >
            <el-table-column prop="createTime" label="交易时间" width="180" />
            <el-table-column prop="description" label="交易说明" min-width="200" />
            <el-table-column prop="amount" label="金额" width="150">
              <template #default="{ row }">
                <span :class="['amount-text', row.type === 'expense' ? 'expense' : 'income']">
                  {{ row.type === 'expense' ? '-' : '+' }}¥{{ row.amount.toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="balance" label="余额" width="150">
              <template #default="{ row }">
                <span class="balance-text">¥{{ row.balance.toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="billsTotal > 0"
            v-model:current-page="billsPage"
            v-model:page-size="billsPageSize"
            :total="billsTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadBills"
            @size-change="loadBills"
            style="margin-top: 20px; justify-content: center"
          />

          <el-empty v-if="!billsLoading && bills.length === 0" description="暂无账单记录" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加支付方式对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加支付方式"
      width="500px"
    >
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="支付方式">
          <el-select v-model="addForm.type" placeholder="请选择支付方式">
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝支付" value="alipay" />
            <el-option label="银行卡" value="bankcard" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号信息">
          <el-input v-model="addForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addPaymentMethod">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Wallet, Refresh, ChatDotRound, CreditCard } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';
import { payBooking, getMyBookingList } from '@/api/booking';
import { getUserBalance, getBalanceRechargeRecords } from '@/api/member';
import BackButton from '@/components/common/BackButton.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 标签页
const activeTab = ref('records');
const showPaymentForm = ref(false);

// 支付方式选择
const loading = ref(false);
const selectedMethod = ref('wechat');
const userBalance = ref('0.00');

const orderInfo = reactive({
  orderNo: '',
  businessNo: '',
  businessName: '',
  businessType: '',
  amount: '0.00'
});

// 支付记录
const recordsLoading = ref(false);
const paymentRecords = ref([]);
const recordsPage = ref(1);
const recordsPageSize = ref(10);
const recordsTotal = ref(0);

// 支付方式管理
const paymentMethods = ref([]);
const showAddDialog = ref(false);
const addForm = reactive({
  type: '',
  account: '',
  isDefault: false
});

// 账单管理
const billsLoading = ref(false);
const bills = ref([]);
const billsPage = ref(1);
const billsPageSize = ref(10);
const billsTotal = ref(0);
const billDateRange = ref([]);
const billSummary = reactive({
  totalExpense: 0,
  totalIncome: 0,
  totalCount: 0
});

onMounted(() => {
  // 检查是否有订单信息，决定显示哪个标签页
  const { orderNo, amount } = route.query;
  if (orderNo && amount) {
    showPaymentForm.value = true;
    activeTab.value = 'method';
    loadOrderInfo();
    loadUserBalance();
  } else {
    // 没有订单信息，显示支付记录
    activeTab.value = 'records';
    loadPaymentRecords();
  }

  // 加载支付方式列表
  loadPaymentMethods();
});

const loadOrderInfo = () => {
  // 从路由参数获取订单信息
  const { orderNo, businessNo, businessName, businessType, amount } = route.query;

  if (!orderNo || !amount) {
    return;
  }

  orderInfo.orderNo = orderNo;
  orderInfo.businessNo = businessNo || orderNo;
  orderInfo.businessName = businessName || '篮球馆服务';
  orderInfo.businessType = businessType || 'booking';
  orderInfo.amount = amount;
};

// 加载支付记录
const loadPaymentRecords = async () => {
  recordsLoading.value = true;
  try {
    // 获取预订支付记录
    const bookingResponse = await getMyBookingList({
      page: recordsPage.value,
      pageSize: recordsPageSize.value,
      status: 1 // 只查询已支付的订单
    });

    // 尝试获取余额充值记录（如果API不存在则忽略）
    let rechargeResponse = null;
    try {
      rechargeResponse = await getBalanceRechargeRecords({
        page: recordsPage.value,
        pageSize: recordsPageSize.value
      });
    } catch (error) {
      console.warn('余额充值记录API暂未实现，跳过');
    }

    // 合并两种记录
    const bookingRecords = (bookingResponse.code === 200 ? bookingResponse.data.records || [] : []).map(record => ({
      id: record.id,
      orderNo: record.bookingNo,
      businessType: 'booking',
      businessName: record.venueName,
      amount: record.actualPrice,
      payMethodName: record.payMethodName || '未知',
      payTime: record.payTime,
      status: record.status
    }));

    const rechargeRecords = (rechargeResponse?.code === 200 ? rechargeResponse.data.records || [] : []).map(record => ({
      id: record.id,
      orderNo: record.paymentNo || record.orderNo,
      businessType: 'balance_recharge',
      businessName: '账户余额充值',
      amount: record.amount,
      payMethodName: record.payMethodName || '在线支付',
      payTime: record.payTime || record.createTime,
      status: record.status
    }));

    // 合并并按支付时间排序
    const allRecords = [...bookingRecords, ...rechargeRecords];
    allRecords.sort((a, b) => {
      const timeA = new Date(a.payTime || 0).getTime();
      const timeB = new Date(b.payTime || 0).getTime();
      return timeB - timeA; // 降序排列，最新的在前
    });

    paymentRecords.value = allRecords;
    recordsTotal.value = (bookingResponse.data?.total || 0) + (rechargeResponse?.data?.total || 0);
  } catch (error) {
    console.error('获取支付记录失败:', error);
    ElMessage.error('获取支付记录失败');
  } finally {
    recordsLoading.value = false;
  }
};

// 加载账单
const loadBills = async () => {
  billsLoading.value = true;
  try {
    // 获取预订支付记录
    const bookingResponse = await getMyBookingList({
      page: billsPage.value,
      pageSize: billsPageSize.value,
      status: 1 // 只查询已支付的订单
    });

    // 获取余额充值记录
    let rechargeResponse = null;
    try {
      rechargeResponse = await getBalanceRechargeRecords({
        page: billsPage.value,
        pageSize: billsPageSize.value
      });
    } catch (error) {
      console.warn('余额充值记录API暂未实现，跳过');
    }

    // 转换预订记录为账单格式
    const bookingBills = (bookingResponse.code === 200 ? bookingResponse.data.records || [] : []).map(record => ({
      createTime: record.payTime || record.createTime,
      description: `场地预订 - ${record.venueName}`,
      amount: record.actualPrice || 0,
      type: 'expense',
      balance: 0
    }));

    // 转换充值记录为账单格式
    const rechargeBills = (rechargeResponse?.code === 200 ? rechargeResponse.data.records || [] : [])
      .filter(record => record.status === 2) // 只显示支付成功的
      .map(record => ({
        createTime: record.payTime || record.createTime,
        description: '账户余额充值',
        amount: record.amount || 0,
        type: 'income',
        balance: 0
      }));

    // 合并并按时间排序
    const allBills = [...bookingBills, ...rechargeBills];
    allBills.sort((a, b) => {
      const timeA = new Date(a.createTime || 0).getTime();
      const timeB = new Date(b.createTime || 0).getTime();
      return timeB - timeA;
    });

    bills.value = allBills;
    billsTotal.value = (bookingResponse.data?.total || 0) + (rechargeResponse?.data?.total || 0);

    // 计算统计数据
    billSummary.totalExpense = bills.value.reduce((sum, bill) =>
      bill.type === 'expense' ? sum + bill.amount : sum, 0
    );
    billSummary.totalIncome = bills.value.reduce((sum, bill) =>
      bill.type === 'income' ? sum + bill.amount : sum, 0
    );
    billSummary.totalCount = bills.value.length;
  } catch (error) {
    console.error('获取账单失败:', error);
    ElMessage.error('获取账单失败');
  } finally {
    billsLoading.value = false;
  }
};

// 查看详情
const viewDetail = (row) => {
  if (row.businessType === 'booking') {
    router.push(`/booking/detail/${row.id}`);
  }
};

// 获取状态类型（根据业务类型区分）
const getStatusType = (status, businessType) => {
  // 场馆预订状态: 0-待支付, 1-已支付, 2-已取消, 3-已完成, 4-已退款
  // 支付订单状态: 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消, 5-已退款

  if (businessType === 'balance_recharge') {
    // 余额充值使用支付订单状态
    const typeMap = {
      0: 'warning',    // 待支付
      1: 'info',       // 支付中
      2: 'success',    // 支付成功
      3: 'danger',     // 支付失败
      4: 'info',       // 已取消
      5: 'warning'     // 已退款
    };
    return typeMap[status] || 'info';
  } else {
    // 场馆预订使用预订状态
    const typeMap = {
      0: 'warning',    // 待支付
      1: 'success',    // 已支付
      2: 'info',       // 已取消
      3: 'success',    // 已完成
      4: 'warning',    // 已退款
      5: 'info'        // 超时取消
    };
    return typeMap[status] || 'info';
  }
};

// 获取状态文本（根据业务类型区分）
const getStatusText = (status, businessType) => {
  if (businessType === 'balance_recharge') {
    // 余额充值使用支付订单状态
    const textMap = {
      0: '待支付',
      1: '支付中',
      2: '支付成功',
      3: '支付失败',
      4: '已取消',
      5: '已退款'
    };
    return textMap[status] || '未知';
  } else {
    // 场馆预订使用预订状态
    const textMap = {
      0: '待支付',
      1: '已支付',
      2: '已取消',
      3: '已完成',
      4: '已退款',
      5: '超时取消'
    };
    return textMap[status] || '未知';
  }
};

const loadUserBalance = async () => {
  try {
    const response = await getUserBalance();
    if (response.code === 200) {
      userBalance.value = (response.data || 0).toFixed(2);
    }
  } catch (error) {
    console.error('获取用户余额失败:', error);
    // 降级方案：从用户信息获取余额
    const balance = userStore.userInfo?.balance || 0;
    userBalance.value = balance.toFixed(2);
  }
};

const handleConfirmPay = () => {
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式');
    return;
  }

  loading.value = true;

  // 根据选择的支付方式跳转到对应页面
  switch (selectedMethod.value) {
    case 'wechat':
      goToWechatPay();
      break;
    case 'alipay':
      goToAlipay();
      break;
    case 'balance':
      payWithBalance();
      break;
    default:
      ElMessage.error('不支持的支付方式');
      loading.value = false;
  }
};

const goToWechatPay = () => {
  router.push({
    path: '/payment/wechat',
    query: {
      businessNo: orderInfo.businessNo,
      businessType: orderInfo.businessType,
      businessName: orderInfo.businessName,
      amount: orderInfo.amount
    }
  });
};

const goToAlipay = () => {
  router.push({
    path: '/payment/alipay',
    query: {
      businessNo: orderInfo.businessNo,
      businessType: orderInfo.businessType,
      businessName: orderInfo.businessName,
      amount: orderInfo.amount
    }
  });
};

const payWithBalance = async () => {
  try {
    // 检查余额是否足够
    const balance = parseFloat(userBalance.value);
    const amount = parseFloat(orderInfo.amount);

    if (balance < amount) {
      ElMessage.error('余额不足，请充值后再试');
      loading.value = false;
      return;
    }

    // 二次确认
    await ElMessageBox.confirm(
      `确认使用余额支付 ¥${orderInfo.amount} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    // 根据业务类型调用对应的支付接口
    if (orderInfo.businessType === 'booking') {
      // 预订订单支付 - 从订单号中提取ID
      const bookingId = extractBookingId(orderInfo.businessNo);

      const response = await payBooking(bookingId, {
        paymentMethod: 2, // 余额支付
        paymentType: ''
      });

      if (response.code === 200) {
        ElMessage.success('支付成功！');
        // 跳转到支付结果页面
        setTimeout(() => {
          router.push({
            path: '/payment/result',
            query: {
              status: 'success',
              orderNo: orderInfo.orderNo,
              amount: orderInfo.amount,
              paymentMethod: '余额支付'
            }
          });
        }, 500);
      } else {
        ElMessage.error(response.message || '支付失败');
        loading.value = false;
      }
    } else {
      // 其他业务类型的支付
      ElMessage.info('该业务类型暂不支持余额支付');
      loading.value = false;
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消支付
      loading.value = false;
      return;
    }
    console.error('余额支付失败:', error);
    ElMessage.error(error.message || '支付失败，请稍后重试');
    loading.value = false;
  }
};

// 从订单号中提取预订ID
const extractBookingId = (businessNo) => {
  // 如果订单号格式是 BK + 时间戳，需要查询获取真实ID
  // 这里简化处理，假设路由参数中有bookingId
  return route.query.bookingId || businessNo.replace(/\D/g, '');
};

// 加载支付方式列表
const loadPaymentMethods = () => {
  // 模拟数据（实际应该从后端API获取）
  paymentMethods.value = [
    {
      id: 1,
      type: 'wechat',
      name: '微信支付',
      account: '微信账号: wx***123',
      isDefault: true
    },
    {
      id: 2,
      type: 'alipay',
      name: '支付宝支付',
      account: '支付宝账号: 138****5678',
      isDefault: false
    }
  ];
};

// 添加支付方式
const addPaymentMethod = () => {
  if (!addForm.type || !addForm.account) {
    ElMessage.warning('请填写完整信息');
    return;
  }

  const typeNames = {
    wechat: '微信支付',
    alipay: '支付宝支付',
    bankcard: '银行卡'
  };

  const newMethod = {
    id: Date.now(),
    type: addForm.type,
    name: typeNames[addForm.type],
    account: addForm.account,
    isDefault: addForm.isDefault
  };

  // 如果设为默认，取消其他默认
  if (addForm.isDefault) {
    paymentMethods.value.forEach(m => m.isDefault = false);
  }

  paymentMethods.value.push(newMethod);
  ElMessage.success('添加成功');
  showAddDialog.value = false;

  // 重置表单
  addForm.type = '';
  addForm.account = '';
  addForm.isDefault = false;
};

// 设为默认
const setDefault = (id) => {
  paymentMethods.value.forEach(method => {
    method.isDefault = method.id === id;
  });
  ElMessage.success('已设为默认支付方式');
};

// 解绑支付方式
const removeMethod = async (id) => {
  try {
    await ElMessageBox.confirm('确定要解绑此支付方式吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const index = paymentMethods.value.findIndex(m => m.id === id);
    if (index > -1) {
      paymentMethods.value.splice(index, 1);
      ElMessage.success('解绑成功');
    }
  } catch {
    // 用户取消
  }
};

// 去充值
const goToRecharge = () => {
  router.push('/member/balance-recharge');
};

const goBack = () => {
  router.back();
};

// 标签页切换事件
const handleTabChange = (tabName) => {
  if (tabName === 'manage') {
    // 切换到支付方式管理时，刷新余额
    loadUserBalance();
  } else if (tabName === 'bills') {
    // 切换到账单管理时，加载账单
    loadBills();
  }
};
</script>

<style lang="scss" scoped>
.payment-method-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1a202c;
    margin: 20px 0;
  }

  .payment-tabs {
    margin-top: 20px;
  }

  .payment-card {
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
    .section-title {
      margin: 0 0 20px 0;
      font-size: 18px;
      font-weight: 500;
      color: #303133;
    }

    .order-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .label {
          font-size: 14px;
          color: #606266;
        }

        .value {
          font-size: 14px;
          color: #303133;

          &.amount {
            font-size: 20px;
            font-weight: bold;
            color: #F56C6C;
          }
        }
      }
    }

    .payment-methods {
      .method-group {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .method-item {
          border: 2px solid #DCDFE6;
          border-radius: 8px;
          padding: 16px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #409EFF;
            background: #F5F7FA;
          }

          &.active {
            border-color: #409EFF;
            background: #ECF5FF;
          }

          :deep(.el-radio) {
            width: 100%;

            .el-radio__input {
              display: none;
            }

            .el-radio__label {
              padding: 0;
              width: 100%;
            }
          }

          .method-content {
            display: flex;
            align-items: center;
            gap: 16px;

            .method-icon {
              width: 48px;
              height: 48px;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 50%;
              flex-shrink: 0;

              &.wechat {
                background: #07C160;

                .icon {
                  width: 28px;
                  height: 28px;
                  fill: #fff;
                }
              }

              &.alipay {
                background: #1677FF;

                .icon {
                  width: 28px;
                  height: 28px;
                  fill: #fff;
                }
              }

              &.balance {
                background: #F56C6C;
                color: #fff;
              }
            }

            .method-info {
              flex: 1;

              h4 {
                margin: 0 0 4px 0;
                font-size: 16px;
                font-weight: 500;
                color: #303133;
              }

              p {
                margin: 0;
                font-size: 13px;
                color: #909399;
              }
            }
          }
        }
      }
    }

    .payment-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 32px;
      padding-top: 20px;
      border-top: 1px solid #EBEEF5;

      .el-button {
        min-width: 120px;
      }
    }
  }

  // 支付记录样式
  .records-card {
    .records-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .section-title {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }
    }

    .amount-text {
      font-weight: 600;
      color: #F56C6C;
    }
  }

  // 支付方式管理样式
  .manage-card {
    .manage-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .section-title {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }
    }

    .payment-methods-list {
      display: flex;
      flex-direction: column;
      gap: 16px;

      .method-card {
        border: 1px solid #DCDFE6;
        border-radius: 8px;
        padding: 20px;
        transition: all 0.3s;

        &:hover {
          border-color: #409EFF;
          box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
        }

        .method-header {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 12px;

          .method-icon-wrapper {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #F5F7FA;
          }

          .method-info {
            flex: 1;

            h4 {
              margin: 0 0 4px 0;
              font-size: 16px;
              font-weight: 500;
              color: #303133;
            }

            p {
              margin: 0;
              font-size: 14px;
              color: #909399;
            }
          }
        }

        .method-actions {
          display: flex;
          gap: 12px;
          justify-content: flex-end;
        }
      }
    }
  }

  // 账单管理样式
  .bills-card {
    .bills-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 16px;

      .section-title {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }

      .filter-group {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .bill-summary {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
      margin-bottom: 20px;

      .summary-item {
        background: #F5F7FA;
        border-radius: 8px;
        padding: 20px;
        text-align: center;

        .summary-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }

        .summary-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;

          &.expense {
            color: #F56C6C;
          }

          &.income {
            color: #67C23A;
          }
        }
      }
    }

    .amount-text {
      font-weight: 600;

      &.expense {
        color: #F56C6C;
      }

      &.income {
        color: #67C23A;
      }
    }

    .balance-text {
      font-weight: 500;
      color: #409EFF;
    }
  }
}
</style>
