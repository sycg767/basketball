<template>
  <div class="venue-list-container">
    <!-- 头部标题 -->
    <div class="page-header">
      <h2>场地列表</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>场地管理</el-breadcrumb-item>
        <el-breadcrumb-item>场地列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索筛选区 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" :inline="true">
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="场地名称/编码"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item label="场地类型">
          <el-select
            v-model="queryForm.venueType"
            placeholder="请选择"
            clearable
            @change="handleSearch"
          >
            <el-option label="室内全场" :value="1" />
            <el-option label="室内半场" :value="2" />
            <el-option label="室外全场" :value="3" />
            <el-option label="室外半场" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="请选择"
            clearable
            @change="handleSearch"
          >
            <el-option label="可用" :value="1" />
            <el-option label="维护中" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 场地列表 -->
    <div v-loading="loading" class="venue-grid">
      <el-empty v-if="venueList.length === 0 && !loading" description="暂无场地数据" />

      <VenueCard
        v-for="venue in venueList"
        :key="venue.id"
        :venue="venue"
        :show-booking="true"
      />
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :page-sizes="[6, 12, 24, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { getVenueList } from '@/api/venue';
import VenueCard from '@/components/venue/VenueCard.vue';

const router = useRouter();

// 查询表单
const queryForm = reactive({
  keyword: '',
  venueType: null,
  status: null,
  page: 1,
  size: 6
});

// 数据
const loading = ref(false);
const venueList = ref([]);
const total = ref(0);

// 获取场地列表
const fetchVenueList = async () => {
  loading.value = true;
  try {
    const res = await getVenueList(queryForm);
    if (res.code === 200) {
      venueList.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('获取场地列表失败：', error);
    ElMessage.error('获取场地列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  queryForm.page = 1;
  fetchVenueList();
};

// 重置
const handleReset = () => {
  queryForm.keyword = '';
  queryForm.venueType = null;
  queryForm.status = null;
  queryForm.page = 1;
  fetchVenueList();
};

// 分页大小变化
const handleSizeChange = () => {
  queryForm.page = 1;
  fetchVenueList();
};

// 页码变化
const handlePageChange = () => {
  fetchVenueList();
};

// 初始化
onMounted(() => {
  fetchVenueList();
});
</script>

<style lang="scss" scoped>
.venue-list-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 10px;
    }
  }

  .search-card {
    margin-bottom: 20px;

    :deep(.el-card__body) {
      padding: 20px;
    }

    .el-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .venue-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
  }
}
</style>
