<template>
  <div class="template-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>通知模板管理</h3>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增模板
          </el-button>
        </div>
      </template>

      <!-- 搜索筛选 -->
      <div class="filter-bar">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="模板类型">
            <el-select v-model="queryParams.templateType" placeholder="全部" clearable style="width: 150px">
              <el-option label="系统通知" value="system" />
              <el-option label="短信通知" value="sms" />
              <el-option label="邮件通知" value="email" />
              <el-option label="微信通知" value="wechat" />
            </el-select>
          </el-form-item>

          <el-form-item label="场景">
            <el-select v-model="queryParams.scene" placeholder="全部" clearable style="width: 150px">
              <el-option label="会员到期" value="member_expire" />
              <el-option label="课程提醒" value="course_remind" />
              <el-option label="预订成功" value="booking_success" />
              <el-option label="支付成功" value="payment_success" />
            </el-select>
          </el-form-item>

          <el-form-item label="关键词">
            <el-input
              v-model="queryParams.keyword"
              placeholder="模板名称/编码/标题"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
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
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="templateList"
        border
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="templateCode" label="模板编码" width="200" />
        <el-table-column prop="templateName" label="模板名称" width="150" />
        <el-table-column prop="templateType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.templateType)">
              {{ getTypeName(row.templateType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scene" label="场景" width="120" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="priority" label="优先级" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="info" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="模板编码" prop="templateCode">
          <el-input
            v-model="formData.templateCode"
            placeholder="请输入模板编码，如：BOOKING_SUCCESS"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>

        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="formData.templateType" placeholder="请选择" style="width: 100%">
            <el-option label="系统通知" value="system" />
            <el-option label="短信通知" value="sms" />
            <el-option label="邮件通知" value="email" />
            <el-option label="微信通知" value="wechat" />
          </el-select>
        </el-form-item>

        <el-form-item label="场景" prop="scene">
          <el-input v-model="formData.scene" placeholder="如：member_expire, course_remind" />
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="通知标题" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="通知内容，支持变量：{{变量名}}"
          />
          <div class="form-tip">
            提示：使用 双花括号+变量名+双花括号 格式插入变量，如：双花括号userName双花括号、双花括号expireDate双花括号
          </div>
        </el-form-item>

        <el-form-item label="变量列表" prop="variables">
          <el-input
            v-model="formData.variables"
            placeholder='JSON数组格式，如：["userName","expireDate"]'
          />
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="formData.priority" :min="1" :max="10" />
          <span class="form-tip">（1-10，数字越大优先级越高）</span>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="模板详情"
      width="600px"
    >
      <el-descriptions v-if="currentTemplate" :column="1" border>
        <el-descriptions-item label="模板编码">{{ currentTemplate.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ currentTemplate.templateName }}</el-descriptions-item>
        <el-descriptions-item label="模板类型">
          <el-tag :type="getTypeTag(currentTemplate.templateType)">
            {{ getTypeName(currentTemplate.templateType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="场景">{{ currentTemplate.scene }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentTemplate.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentTemplate.content }}</el-descriptions-item>
        <el-descriptions-item label="变量列表">{{ currentTemplate.variables }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentTemplate.priority }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentTemplate.status === 1 ? 'success' : 'danger'">
            {{ currentTemplate.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentTemplate.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTemplate.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentTemplate.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import {
  getTemplatePage,
  addTemplate,
  updateTemplate,
  deleteTemplate,
  toggleTemplateStatus
} from '@/api/admin/notificationTemplate'

// 数据
const loading = ref(false)
const templateList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const currentTemplate = ref(null)
const formRef = ref(null)

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  templateType: '',
  scene: '',
  keyword: ''
})

// 表单数据
const formData = reactive({
  templateCode: '',
  templateName: '',
  templateType: 'system',
  scene: '',
  title: '',
  content: '',
  variables: '[]',
  priority: 2,
  status: 1,
  remark: ''
})

// 表单验证规则
const formRules = {
  templateCode: [
    { required: true, message: '请输入模板编码', trigger: 'blur' }
  ],
  templateName: [
    { required: true, message: '请输入模板名称', trigger: 'blur' }
  ],
  templateType: [
    { required: true, message: '请选择模板类型', trigger: 'change' }
  ],
  scene: [
    { required: true, message: '请输入场景', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ]
}

// 获取类型标签
const getTypeTag = (type) => {
  const map = {
    system: 'primary',
    sms: 'success',
    email: 'warning',
    wechat: 'info'
  }
  return map[type] || 'info'
}

// 获取类型名称
const getTypeName = (type) => {
  const map = {
    system: '系统通知',
    sms: '短信通知',
    email: '邮件通知',
    wechat: '微信通知'
  }
  return map[type] || type
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getTemplatePage(queryParams)
    if (res.code === 200) {
      templateList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryParams.page = 1
  queryParams.templateType = ''
  queryParams.scene = ''
  queryParams.keyword = ''
  loadData()
}

// 分页
const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增模板'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑模板'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  currentTemplate.value = row
  viewDialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteTemplate(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    await toggleTemplateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (isEdit.value) {
      await updateTemplate(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addTemplate(formData)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) {
      ElMessage.error('提交失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    templateCode: '',
    templateName: '',
    templateType: 'system',
    scene: '',
    title: '',
    content: '',
    variables: '[]',
    priority: 2,
    status: 1,
    remark: ''
  })
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.template-manage-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.filter-bar {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
