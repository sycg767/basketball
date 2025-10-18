<template>
  <div class="user-manage-container">
    <h2 class="page-title">用户管理</h2>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="searchForm.memberLevel" placeholder="请选择会员等级" clearable>
            <el-option label="全部" value="" />
            <el-option label="普通会员" :value="0" />
            <el-option label="银卡会员" :value="1" />
            <el-option label="金卡会员" :value="2" />
            <el-option label="钻石会员" :value="3" />
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table :data="userList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="memberLevel" label="会员等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getMemberLevelType(row.memberLevel)">
              {{ getMemberLevelText(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="账户余额" width="120">
          <template #default="{ row }">
            ¥{{ row.balance.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              <el-icon><Lock v-if="row.status === 1" /><Unlock v-else /></el-icon>
              {{ row.status === 1 ? '禁用' : '启用' }}
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

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="userForm.memberLevel" placeholder="请选择会员等级">
            <el-option label="普通会员" :value="0" />
            <el-option label="银卡会员" :value="1" />
            <el-option label="金卡会员" :value="2" />
            <el-option label="钻石会员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户余额" prop="balance">
          <el-input-number v-model="userForm.balance" :min="0" :precision="2" />
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
  Edit,
  Lock,
  Unlock
} from '@element-plus/icons-vue';
import {
  getUserList,
  updateUser,
  toggleUserStatus
} from '@/api/admin';

const loading = ref(false);
const dialogVisible = ref(false);
const userFormRef = ref(null);

const searchForm = reactive({
  username: '',
  memberLevel: ''
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
});

const userList = ref([]);

const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  memberLevel: 0,
  balance: 0
});

const userRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  memberLevel: [{ required: true, message: '请选择会员等级', trigger: 'change' }]
};

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      username: searchForm.username || undefined,
      memberLevel: searchForm.memberLevel !== '' ? searchForm.memberLevel : undefined
    };

    const res = await getUserList(params);

    userList.value = res.data.records.map(item => ({
      id: item.id,
      username: item.username,
      realName: item.realName || '-',
      phone: item.phone,
      memberLevel: item.memberLevel || 0,
      balance: item.balance || 0,
      registerTime: item.createTime,
      status: item.status
    }));

    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 会员等级类型
const getMemberLevelType = (level) => {
  const typeMap = {
    0: 'info',
    1: '',
    2: 'warning',
    3: 'danger'
  };
  return typeMap[level] || 'info';
};

// 会员等级文本
const getMemberLevelText = (level) => {
  const textMap = {
    0: '普通会员',
    1: '银卡会员',
    2: '金卡会员',
    3: '钻石会员'
  };
  return textMap[level] || '未知';
};

// 搜索
const handleSearch = () => {
  pagination.page = 1;
  fetchUserList();
};

// 重置
const handleReset = () => {
  searchForm.username = '';
  searchForm.memberLevel = '';
  handleSearch();
};

// 编辑
const handleEdit = (row) => {
  Object.assign(userForm, row);
  dialogVisible.value = true;
};

// 切换状态
const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用';
  const newStatus = row.status === 1 ? 0 : 1;

  try {
    await ElMessageBox.confirm(`确定要${action}用户"${row.username}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await toggleUserStatus(row.id, newStatus);
    ElMessage.success(`${action}成功`);
    fetchUserList();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || `${action}失败`);
    }
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return;

  try {
    const valid = await userFormRef.value.validate();
    if (!valid) return;

    const updateData = {
      realName: userForm.realName,
      phone: userForm.phone,
      memberLevel: userForm.memberLevel,
      balance: userForm.balance
    };

    await updateUser(userForm.id, updateData);
    ElMessage.success('修改成功');
    dialogVisible.value = false;
    fetchUserList();
  } catch (error) {
    ElMessage.error(error.message || '修改失败');
  }
};

// 对话框关闭
const handleDialogClose = () => {
  userFormRef.value?.resetFields();
  Object.assign(userForm, {
    id: null,
    username: '',
    realName: '',
    phone: '',
    memberLevel: 0,
    balance: 0
  });
};

// 分页
const handleSizeChange = () => {
  fetchUserList();
};

const handlePageChange = () => {
  fetchUserList();
};

onMounted(() => {
  fetchUserList();
});
</script>

<style lang="scss" scoped>
.user-manage-container {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1a202c;
    margin: 0 0 24px 0;
  }

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}
</style>
