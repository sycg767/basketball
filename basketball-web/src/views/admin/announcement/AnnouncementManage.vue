<template>
  <div class="announcement-container">
    <el-card class="page-header">
      <template #header>
        <div class="header-content">
          <h2>公告管理</h2>
          <el-button type="primary" @click="showCreateDialog">创建公告</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable style="width: 180px">
            <el-option label="系统通知" :value="1" />
            <el-option label="活动公告" :value="2" />
            <el-option label="维护公告" :value="3" />
            <el-option label="优惠活动" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchAnnouncements">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="content-card">
      <el-table :data="announcementList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.priority === 2" type="danger">紧急</el-tag>
            <el-tag v-else-if="row.priority === 1" type="warning">重要</el-tag>
            <el-tag v-else type="info">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览次数" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAnnouncement(row)">查看</el-button>
            <el-button type="warning" size="small" @click="editAnnouncement(row)" v-if="row.status === 0">编辑</el-button>
            <el-button type="success" size="small" @click="publishAnnouncement(row)" v-if="row.status === 0">发布</el-button>
            <el-button type="info" size="small" @click="offlineAnnouncement(row)" v-if="row.status === 1">下架</el-button>
            <el-button type="danger" size="small" @click="deleteAnnouncement(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建公告' : '编辑公告'"
      width="800px"
    >
      <el-form
        ref="announcementFormRef"
        :model="announcementForm"
        :rules="announcementRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-select v-model="announcementForm.type" placeholder="请选择公告类型">
            <el-option label="系统通知" :value="1" />
            <el-option label="活动公告" :value="2" />
            <el-option label="维护公告" :value="3" />
            <el-option label="优惠活动" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-select v-model="announcementForm.priority" placeholder="请选择优先级">
            <el-option label="普通" :value="0" />
            <el-option label="重要" :value="1" />
            <el-option label="紧急" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标对象" prop="targetType">
          <el-select v-model="announcementForm.targetType" placeholder="请选择目标对象">
            <el-option label="所有用户" :value="1" />
            <el-option label="会员" :value="2" />
            <el-option label="教练" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="announcementForm.isTop" />
        </el-form-item>

        <el-form-item label="是否弹窗" prop="isPopup">
          <el-switch v-model="announcementForm.isPopup" />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="announcementForm.coverImage" placeholder="请输入图片URL" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>

        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
            v-model="announcementForm.expireTime"
            type="datetime"
            placeholder="选择过期时间"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAnnouncement" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看公告对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="800px"
    >
      <div v-if="viewAnnouncementDetail">
        <div class="announcement-detail">
          <h3>{{ viewAnnouncementDetail.title }}</h3>
          <div class="announcement-meta">
            <el-tag :type="getTypeTag(viewAnnouncementDetail.type)" size="small">
              {{ getTypeText(viewAnnouncementDetail.type) }}
            </el-tag>
            <el-tag :type="getStatusTag(viewAnnouncementDetail.status)" size="small" class="ml-10">
              {{ getStatusText(viewAnnouncementDetail.status) }}
            </el-tag>
            <span class="view-count">浏览次数: {{ viewAnnouncementDetail.viewCount }}</span>
          </div>
          <div class="announcement-content" v-html="viewAnnouncementDetail.content"></div>
          <div class="announcement-footer">
            <p>发布时间: {{ formatDate(viewAnnouncementDetail.publishTime) }}</p>
            <p v-if="viewAnnouncementDetail.expireTime">
              过期时间: {{ formatDate(viewAnnouncementDetail.expireTime) }}
            </p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { request } from '@/utils/request.js'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const announcementFormRef = ref<FormInstance>()

const searchForm = reactive({
  type: '',
  status: ''
})

const announcementForm = reactive({
  id: null,
  title: '',
  type: 1,
  priority: 0,
  targetType: 1,
  isTop: false,
  isPopup: false,
  coverImage: '',
  content: '',
  expireTime: ''
})

const announcementRules: FormRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  targetType: [{ required: true, message: '请选择目标对象', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const announcementList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const viewAnnouncementDetail = ref(null)

const getAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined
    }
    const response = await request.get('/api/admin/announcement/page', { params })
    if (response.code === 200) {
      announcementList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取公告列表失败')
  }
  loading.value = false
}

const searchAnnouncements = () => {
  currentPage.value = 1
  getAnnouncements()
}

const resetSearch = () => {
  searchForm.type = ''
  searchForm.status = ''
  searchAnnouncements()
}

const showCreateDialog = () => {
  dialogType.value = 'create'
  announcementForm.id = null
  Object.assign(announcementForm, {
    title: '',
    type: 1,
    priority: 0,
    targetType: 1,
    isTop: false,
    isPopup: false,
    coverImage: '',
    content: '',
    expireTime: ''
  })
  dialogVisible.value = true
}

const editAnnouncement = (row) => {
  dialogType.value = 'edit'
  Object.assign(announcementForm, row)
  dialogVisible.value = true
}

const viewAnnouncement = async (row) => {
  try {
    const response = await request.get(`/api/admin/announcement/${row.id}`)
    if (response.code === 200) {
      viewAnnouncementDetail.value = response.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取公告详情失败')
  }
}

const saveAnnouncement = async () => {
  if (!announcementFormRef.value) return

  await announcementFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const url = dialogType.value === 'create'
          ? '/api/admin/announcement/create'
          : '/api/admin/announcement/update'

        const response = await request.post(url, announcementForm)
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'create' ? '创建成功' : '更新成功')
          dialogVisible.value = false
          getAnnouncements()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      }
      saving.value = false
    }
  })
}

const publishAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发布此公告吗？', '提示', {
      type: 'warning'
    })

    const response = await request.post(`/api/admin/announcement/publish/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('发布公告成功')
      getAnnouncements()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const offlineAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架此公告吗？', '提示', {
      type: 'warning'
    })

    const response = await request.post(`/api/admin/announcement/offline/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('下架公告成功')
      getAnnouncements()
    } else {
      ElMessage.error(response.message || '下架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

const deleteAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此公告吗？', '提示', {
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/announcement/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getAnnouncements()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getTypeText = (type) => {
  const map = {
    1: '系统通知',
    2: '活动公告',
    3: '维护公告',
    4: '优惠活动'
  }
  return map[type] || '未知'
}

const getTypeTag = (type) => {
  const map = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return map[type] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getAnnouncements()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getAnnouncements()
}

onMounted(() => {
  getAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-card {
  margin-bottom: 20px;
}

.mt-10 {
  margin-top: 10px;
}

.mt-20 {
  margin-top: 20px;
}

.ml-10 {
  margin-left: 10px;
}

.announcement-detail {
  padding: 20px;
}

.announcement-meta {
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.view-count {
  color: #666;
  margin-left: 10px;
}

.announcement-content {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  margin: 15px 0;
  line-height: 1.6;
}

.announcement-footer {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  color: #666;
  font-size: 14px;
}
</style>