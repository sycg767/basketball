<template>
  <div class="system-config-container">
    <el-card class="config-card">
      <template #header>
        <div class="header-content">
          <h3>系统配置管理</h3>
          <el-button type="primary" @click="showAddDialog">添加配置</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="配置键">
          <el-input v-model="searchForm.configKey" placeholder="请输入配置键" clearable />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchConfigs">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="configList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键" min-width="150" />
        <el-table-column prop="configValue" label="配置值" min-width="200" show-overflow-tooltip />
        <el-table-column prop="configName" label="配置名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editConfig(row)">编辑</el-button>
            <el-button
              type="warning"
              size="small"
              @click="toggleStatus(row)"
              :disabled="row.configKey === 'system.name'"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteConfig(row)"
              :disabled="row.configKey === 'system.name'"
            >
              删除
            </el-button>
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

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '添加配置' : '编辑配置'"
      width="600px"
    >
      <el-form
        ref="configFormRef"
        :model="configForm"
        :rules="configRules"
        label-width="120px"
      >
        <el-form-item label="配置键" prop="configKey">
          <el-input
            v-model="configForm.configKey"
            placeholder="请输入配置键（如：system.name）"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>

        <el-form-item label="配置名称" prop="configName">
          <el-input
            v-model="configForm.configName"
            placeholder="请输入配置名称"
          />
        </el-form-item>

        <el-form-item label="配置值" prop="configValue">
          <el-input
            v-model="configForm.configValue"
            type="textarea"
            :rows="3"
            placeholder="请输入配置值"
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="configForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入配置描述"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="configForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig" :loading="saving">保存</el-button>
      </template>
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
const dialogType = ref<'create' | 'edit'>('create')
const configFormRef = ref<FormInstance>()

const searchForm = reactive({
  configKey: '',
  status: ''
})

const configForm = reactive({
  id: null,
  configKey: '',
  configValue: '',
  configName: '',
  description: '',
  status: 1
})

const configRules: FormRules = {
  configKey: [
    { required: true, message: '请输入配置键', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._-]+$/, message: '配置键只能包含字母、数字、点、下划线和连字符', trigger: 'blur' }
  ],
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const configList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const getConfigs = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const response = await request.get('/api/system-config/page', { params })
    if (response.code === 200) {
      configList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取配置列表失败')
  }
  loading.value = false
}

const searchConfigs = () => {
  currentPage.value = 1
  getConfigs()
}

const resetSearch = () => {
  searchForm.configKey = ''
  searchForm.status = ''
  searchConfigs()
}

const showAddDialog = () => {
  dialogType.value = 'create'
  configForm.id = null
  Object.assign(configForm, {
    configKey: '',
    configValue: '',
    configName: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

const editConfig = (row) => {
  dialogType.value = 'edit'
  Object.assign(configForm, row)
  dialogVisible.value = true
}

const saveConfig = async () => {
  if (!configFormRef.value) return

  await configFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const url = dialogType.value === 'create'
          ? '/api/system-config/create'
          : '/api/system-config/update'

        const response = await request.post(url, configForm)
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'create' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          getConfigs()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
      saving.value = false
    }
  })
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(
      `确定要${newStatus === 1 ? '启用' : '禁用'}此配置吗？`,
      '提示',
      { type: 'warning' }
    )

    const response = await request.post(`/api/system-config/status/${row.id}`, null, {
      params: { status: newStatus }
    })
    if (response.code === 200) {
      ElMessage.success(`配置已${newStatus === 1 ? '启用' : '禁用'}`)
      getConfigs()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const deleteConfig = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此配置吗？', '提示', {
      type: 'warning'
    })

    const response = await request.delete(`/api/system-config/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getConfigs()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getConfigs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getConfigs()
}

onMounted(() => {
  getConfigs()
})
</script>

<style scoped>
.system-config-container {
  padding: 0;
}

.config-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  margin-bottom: 20px;
}
</style>