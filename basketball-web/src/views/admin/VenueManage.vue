<template>
  <div class="venue-manage-container">
    <h2 class="page-title">场地管理</h2>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="场地名称">
          <el-input v-model="searchForm.name" placeholder="请输入场地名称" clearable />
        </el-form-item>
        <el-form-item label="场地类型">
          <el-select v-model="searchForm.type" placeholder="请选择场地类型" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="室内全场" :value="1" />
            <el-option label="室内半场" :value="2" />
            <el-option label="室外全场" :value="3" />
            <el-option label="室外半场" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增场地
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 场地列表 -->
    <el-card class="table-card">
      <el-table :data="venueList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="场地名称" width="150" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type <= 2 ? 'success' : 'warning'">
              {{ getVenueTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="price" label="价格/小时" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 价格配置对话框 -->
    <el-dialog
      v-model="priceDialogVisible"
      title="配置场地价格"
      width="700px"
      @close="handlePriceDialogClose"
    >
      <el-form ref="priceFormRef" :model="priceForm" label-width="120px">
        <el-form-item label="场地名称">
          <el-input :value="venueForm.name" disabled />
        </el-form-item>
        <el-form-item label="时间类型" prop="timeType">
          <el-radio-group v-model="priceForm.timeType">
            <el-radio :value="1">工作日</el-radio>
            <el-radio :value="2">周末</el-radio>
            <el-radio :value="3">节假日</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="时段" prop="timePeriod">
          <el-select v-model="priceForm.timePeriod" placeholder="请选择时段" @change="handleTimePeriodChange">
            <el-option label="早场 (08:00-12:00)" value="08:00-12:00" />
            <el-option label="午场 (12:00-18:00)" value="12:00-18:00" />
            <el-option label="晚场 (18:00-22:00)" value="18:00-22:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="标准价格(元/小时)" prop="price">
          <el-input-number v-model="priceForm.price" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="会员价格(元/小时)" prop="memberPrice">
          <el-input-number v-model="priceForm.memberPrice" :min="0" :precision="2" :step="10" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="priceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePriceSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="venueFormRef"
        :model="venueForm"
        :rules="venueRules"
        label-width="100px"
      >
        <el-form-item label="场地名称" prop="name">
          <el-input v-model="venueForm.name" placeholder="请输入场地名称" />
        </el-form-item>
        <el-form-item label="场地编码" prop="code">
          <el-input v-model="venueForm.code" placeholder="请输入场地编码" :disabled="!!venueForm.id" />
        </el-form-item>
        <el-form-item label="场地类型" prop="type">
          <el-radio-group v-model="venueForm.type">
            <el-radio :value="1">室内全场</el-radio>
            <el-radio :value="2">室内半场</el-radio>
            <el-radio :value="3">室外全场</el-radio>
            <el-radio :value="4">室外半场</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="venueForm.location" placeholder="请输入场地位置" />
        </el-form-item>
        <el-form-item label="基础价格" v-if="venueForm.id">
          <el-input :value="`¥${venueForm.price || 0}/小时`" disabled>
            <template #append>
              <el-button @click="handleEditPrice">修改价格</el-button>
            </template>
          </el-input>
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            注: 价格需单独配置，请点击"修改价格"按钮进行设置
          </div>
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="venueForm.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="设施" prop="facilities">
          <el-input
            v-model="venueForm.facilities"
            type="textarea"
            :rows="3"
            placeholder="请输入场地设施，多个设施用逗号分隔"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="venueForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete
} from '@element-plus/icons-vue';
import {
  getAdminVenueList,
  createVenue,
  updateVenue,
  deleteVenue,
  setVenuePrice
} from '@/api/admin';

const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('新增场地');
const venueFormRef = ref(null);
const priceDialogVisible = ref(false);
const priceFormRef = ref(null);

const searchForm = reactive({
  name: '',
  type: null
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

const venueList = ref([]);

// 场地类型映射
const venueTypeMap = {
  1: '室内全场',
  2: '室内半场',
  3: '室外全场',
  4: '室外半场'
};

const getVenueTypeText = (type) => {
  return venueTypeMap[type] || '未知';
};

const venueForm = reactive({
  id: null,
  name: '',
  code: '',
  type: 1,
  location: '',
  price: 0,
  capacity: 10,
  facilities: '',
  status: 1
});

const venueRules = {
  name: [{ required: true, message: '请输入场地名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入场地编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择场地类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入场地位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
};

const priceForm = reactive({
  venueId: null,
  timeType: 1,
  timePeriod: '08:00-12:00',
  startTime: '08:00:00',
  endTime: '12:00:00',
  price: 100.00,
  memberPrice: 90.00
});

// 获取场地列表
const fetchVenueList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      venueName: searchForm.name || undefined,
      venueType: searchForm.type || undefined
    };

    const res = await getAdminVenueList(params);

    // 转换数据格式
    venueList.value = res.data.records.map(item => ({
      id: item.id,
      name: item.venueName,
      code: item.venueCode,
      type: item.venueType,
      location: item.location || '-',
      price: item.basePrice || 0,
      capacity: item.capacity || 0,
      facilities: item.facilities || '',
      status: item.status
    }));

    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error(error.message || '获取场地列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.page = 1;
  fetchVenueList();
};

// 重置
const handleReset = () => {
  searchForm.name = '';
  searchForm.type = null;
  handleSearch();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增场地';
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑场地';
  // 清空表单并重新赋值,确保所有字段正确同步
  Object.assign(venueForm, {
    id: row.id,
    name: row.name,
    code: row.code,
    type: row.type,
    location: row.location,
    price: row.price,
    capacity: row.capacity,
    facilities: row.facilities,
    status: row.status
  });
  dialogVisible.value = true;
};

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除场地"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await deleteVenue(row.id);
    ElMessage.success('删除成功');
    fetchVenueList();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败');
    }
  }
};

// 修改价格
const handleEditPrice = () => {
  if (!venueForm.id) {
    ElMessage.warning('请先选择场地');
    return;
  }
  priceForm.venueId = venueForm.id;
  priceDialogVisible.value = true;
};

// 时段改变
const handleTimePeriodChange = (value) => {
  const timeMap = {
    '08:00-12:00': { start: '08:00:00', end: '12:00:00' },
    '12:00-18:00': { start: '12:00:00', end: '18:00:00' },
    '18:00-22:00': { start: '18:00:00', end: '22:00:00' }
  };
  if (timeMap[value]) {
    priceForm.startTime = timeMap[value].start;
    priceForm.endTime = timeMap[value].end;
  }
};

// 提交价格配置
const handlePriceSubmit = async () => {
  try {
    const data = {
      venueId: priceForm.venueId,
      timeType: priceForm.timeType,
      timePeriod: priceForm.timePeriod,
      startTime: priceForm.startTime,
      endTime: priceForm.endTime,
      price: priceForm.price,
      memberPrice: priceForm.memberPrice
    };

    await setVenuePrice(data);
    ElMessage.success('价格配置成功');
    priceDialogVisible.value = false;
    fetchVenueList();
  } catch (error) {
    ElMessage.error(error.message || '价格配置失败');
  }
};

// 关闭价格对话框
const handlePriceDialogClose = () => {
  Object.assign(priceForm, {
    venueId: null,
    timeType: 1,
    timePeriod: '08:00-12:00',
    startTime: '08:00:00',
    endTime: '12:00:00',
    price: 100.00,
    memberPrice: 90.00
  });
};

// 提交表单
const handleSubmit = async () => {
  if (!venueFormRef.value) return;

  try {
    await venueFormRef.value.validate();

    const data = {
      venueName: venueForm.name,
      venueCode: venueForm.code,
      venueType: venueForm.type,
      location: venueForm.location,
      capacity: venueForm.capacity,
      facilities: venueForm.facilities,
      status: venueForm.status,
      area: 100 // 默认面积，可以后续添加输入字段
    };

    if (venueForm.id) {
      await updateVenue(venueForm.id, data);
      ElMessage.success('修改成功');
    } else {
      await createVenue(data);
      ElMessage.success('新增成功');
    }

    dialogVisible.value = false;
    fetchVenueList();
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message || '操作失败');
    }
  }
};

// 对话框关闭
const handleDialogClose = () => {
  venueFormRef.value?.resetFields();
  Object.assign(venueForm, {
    id: null,
    name: '',
    code: '',
    type: 1,
    location: '',
    price: 0,
    capacity: 10,
    facilities: '',
    status: 1
  });
};

// 分页
const handleSizeChange = () => {
  fetchVenueList();
};

const handlePageChange = () => {
  fetchVenueList();
};

onMounted(() => {
  fetchVenueList();
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.venue-manage-container {
  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 24px 0;
    letter-spacing: -0.02em;
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-form) {
      .el-form-item {
        margin-bottom: 0;
      }

      .el-button {
        border-radius: $radius-md;
        font-weight: 500;
        transition: all $duration-fast $ease-in-out;

        &:hover {
          transform: translateY(-1px);
        }
      }
    }
  }

  .table-card {
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-table) {
      font-size: 14px;

      th {
        background-color: $bg-secondary;
        color: $text-secondary;
        font-weight: 500;
      }

      td {
        color: $text-primary;
      }

      .el-button {
        font-weight: 500;
        transition: all $duration-fast $ease-in-out;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .pagination {
      margin-top: 24px;
      justify-content: flex-end;
    }
  }

  // 对话框样式优化
  :deep(.el-dialog) {
    border-radius: $radius-xl;

    .el-dialog__header {
      padding: 24px 24px 16px;
      border-bottom: 1px solid $border-color;

      .el-dialog__title {
        font-size: 17px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid $border-color;

      .el-button {
        border-radius: $radius-md;
        font-weight: 500;
        padding: 10px 20px;
      }
    }
  }
}
</style>
