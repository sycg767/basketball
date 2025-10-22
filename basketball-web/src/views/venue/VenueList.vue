<template>
  <div class="venue-list-container">
    <!-- 头部标题 -->
    <BackButton text="返回" />
    <h2 class="page-title">场地列表</h2>

    <!-- 搜索筛选区 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" label-width="70px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="5">
            <el-form-item label="关键词">
              <el-input
                v-model="queryForm.keyword"
                placeholder="场地名称/编码"
                clearable
                @clear="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="5" :xl="4">
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
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
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
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="5" :xl="4">
            <el-form-item label="价格范围">
              <el-select
                v-model="queryForm.priceRange"
                placeholder="请选择"
                clearable
                @change="handleSearch"
              >
                <el-option label="0-50元" :value="1" />
                <el-option label="50-100元" :value="2" />
                <el-option label="100-200元" :value="3" />
                <el-option label="200元以上" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="5" :xl="4">
            <el-form-item label="设施">
              <el-select
                v-model="queryForm.facilities"
                placeholder="请选择"
                clearable
                multiple
                collapse-tags
                @change="handleSearch"
              >
                <el-option label="空调" :value="1" />
                <el-option label="更衣室" :value="2" />
                <el-option label="淋浴" :value="3" />
                <el-option label="停车场" :value="4" />
                <el-option label="餐饮" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="4">
            <el-form-item label=" " class="button-group">
              <el-button type="primary" :icon="Search" @click="handleSearch">
                搜索
              </el-button>
              <el-button :icon="Refresh" @click="handleReset">
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
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
import { ElMessage } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { getVenueList } from '@/api/venue';
import VenueCard from '@/components/venue/VenueCard.vue';
import BackButton from '@/components/common/BackButton.vue';

// 查询表单
const queryForm = reactive({
  keyword: '',
  venueType: null,
  status: null,
  // 以下为前端临时筛选条件，后端暂不支持
  priceRange: null,
  facilities: [],
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
    // 构建API参数，只传递后端支持的筛选条件
    const apiParams = {
      page: queryForm.page,
      size: queryForm.size
    };

    // 只添加非空的筛选条件
    if (queryForm.keyword) {
      apiParams.keyword = queryForm.keyword;
    }
    if (queryForm.venueType) {
      apiParams.venueType = queryForm.venueType.toString();
    }
    if (queryForm.status !== null) {
      apiParams.status = queryForm.status;
    }

    const res = await getVenueList(apiParams);
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
  queryForm.priceRange = null;
  queryForm.facilities = [];
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
@use '@/styles/design-system/variables' as *;
@use '@/styles/design-system/mixins' as *;

.venue-list-container {
  @include container;
  padding: $spacing-8 $spacing-6;
  min-height: 100vh;
  background: $bg-secondary;

  .page-title {
    @include text-heading-2;
    margin: $spacing-6 0 $spacing-8 0;
  }

  .search-card {
    @include card-base;
    margin-bottom: $spacing-6;
    border: none;

    :deep(.el-card__body) {
      padding: $spacing-6;
    }

    .el-form {
      .el-form-item {
        margin-bottom: $spacing-3;

        :deep(.el-form-item__label) {
          font-size: 13px;
          font-weight: 500;
          color: $text-secondary;
          padding-right: 8px;
        }

        :deep(.el-input__wrapper) {
          @include input-base;
          box-shadow: none;
          width: 100%;
          height: 36px;
          border-radius: 8px;
          font-size: 14px;
          padding: 0 12px;
        }

        :deep(.el-select) {
          width: 100%;

          .el-input__wrapper {
            @include input-base;
            box-shadow: none;
            height: 36px;
            border-radius: 8px;
            font-size: 14px;
            padding: 0 12px;
          }
        }

        :deep(.el-input__inner) {
          height: 36px;
          line-height: 36px;
          font-size: 14px;
        }
      }

      .button-group {
        :deep(.el-form-item__content) {
          display: flex;
          gap: $spacing-2;
        }
      }

      .el-button {
        @include button-base;
        height: 36px;
        padding: 0 16px;
        font-size: 14px;
        border-radius: 8px;
        font-weight: 500;

        &.el-button--primary {
          @include button-primary;
        }
      }
    }
  }

  .venue-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: $spacing-6;
    margin-bottom: $spacing-8;

    @include respond-to(md) {
      grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    }

    @include respond-to(lg) {
      gap: $spacing-8;
    }
  }

  .pagination-container {
    @include flex-center;
    margin-top: $spacing-10;
    padding: $spacing-6 0;

    :deep(.el-pagination) {
      .el-pager li {
        min-width: 36px;
        height: 36px;
        line-height: 36px;
        border-radius: $radius-sm;
        font-weight: $font-weight-medium;
        transition: $transition-fast;

        &.is-active {
          background: $primary;
          color: $white;
        }

        &:hover:not(.is-active) {
          background: $gray-100;
        }
      }

      .btn-prev,
      .btn-next {
        min-width: 36px;
        height: 36px;
        border-radius: $radius-sm;
        transition: $transition-fast;

        &:hover:not(:disabled) {
          background: $gray-100;
        }
      }
    }
  }
}
</style>
