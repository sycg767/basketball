<template>
  <div class="dictionary-container">
    <el-card class="page-header">
      <template #header>
        <div class="header-content">
          <h2>字典管理</h2>
          <el-button type="primary" @click="showCreateDialog">创建字典</el-button>
        </div>
      </template>

      <el-form :model="searchForm" inline>
        <el-form-item label="字典类型">
          <el-select v-model="searchForm.dictType" placeholder="请选择类型" clearable>
            <el-option
              v-for="type in dictTypeList"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="标签名">
          <el-input v-model="searchForm.dictLabel" placeholder="请输入标签名" clearable />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchDictionaries">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="content-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="所有字典" name="all" />
        <el-tab-pane label="按类型分组" name="grouped" />
      </el-tabs>

      <!-- 所有字典列表 -->
      <el-table v-if="activeTab === 'all'" :data="dictionaryList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dictType" label="字典类型" width="120" />
        <el-table-column prop="dictLabel" label="标签名" min-width="150" />
        <el-table-column prop="dictValue" label="值" width="100" />
        <el-table-column prop="remark" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dictSort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDictionary(row)">查看</el-button>
            <el-button type="warning" size="small" @click="editDictionary(row)">编辑</el-button>
            <el-button
              type="info"
              size="small"
              @click="toggleStatus(row)"
              :disabled="row.id === 1"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteDictionary(row)"
              :disabled="row.id === 1"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 按类型分组展示 -->
      <div v-if="activeTab === 'grouped'" class="grouped-content">
        <el-collapse v-model="activeGroups">
          <el-collapse-item
            v-for="group in groupedDictionaries"
            :key="group.type"
            :name="group.type"
          >
            <template #title>
              <span class="group-title">{{ group.type }}</span>
              <el-tag size="small" type="info">{{ group.items.length }} 项</el-tag>
            </template>

            <el-table :data="group.items" size="small" style="margin-top: 10px;">
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="dictLabel" label="标签名" min-width="120" />
              <el-table-column prop="dictValue" label="值" width="100" />
              <el-table-column prop="remark" label="描述" min-width="150" />
              <el-table-column prop="status" label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                    {{ row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="dictSort" label="排序" width="60" />
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="viewDictionary(row)">查看</el-button>
                  <el-button type="warning" size="small" @click="editDictionary(row)">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>

      <el-pagination
        v-if="activeTab === 'all'"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 创建/编辑字典对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建字典' : '编辑字典'"
      width="600px"
    >
      <el-form
        ref="dictionaryFormRef"
        :model="dictionaryForm"
        :rules="dictionaryRules"
        label-width="100px"
      >
        <el-form-item label="字典类型" prop="dictType">
          <el-select
            v-model="dictionaryForm.dictType"
            placeholder="请选择字典类型"
            :disabled="dialogType === 'edit'"
          >
            <el-option
              v-for="type in dictTypeList"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标签名" prop="dictLabel">
          <el-input
            v-model="dictionaryForm.dictLabel"
            placeholder="请输入标签名"
          />
        </el-form-item>

        <el-form-item label="值" prop="dictValue">
          <el-input
            v-model="dictionaryForm.dictValue"
            placeholder="请输入值"
          />
        </el-form-item>

        <el-form-item label="描述" prop="remark">
          <el-input
            v-model="dictionaryForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dictionaryForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序" prop="dictSort">
          <el-input-number
            v-model="dictionaryForm.dictSort"
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDictionary" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看字典对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="字典详情"
      width="600px"
    >
      <div v-if="viewDictionaryDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ viewDictionaryDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="字典类型">{{ viewDictionaryDetail.dictType }}</el-descriptions-item>
          <el-descriptions-item label="标签名">{{ viewDictionaryDetail.label }}</el-descriptions-item>
          <el-descriptions-item label="值">{{ viewDictionaryDetail.value }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="viewDictionaryDetail.status === 1 ? 'success' : 'danger'">
              {{ viewDictionaryDetail.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排序">{{ viewDictionaryDetail.dictSort }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ viewDictionaryDetail.remark }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(viewDictionaryDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(viewDictionaryDetail.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { request } from '@/utils/request.js'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const dictionaryFormRef = ref<FormInstance>()

const searchForm = reactive({
  dictType: '',
  status: '',
  dictLabel: ''
})

const dictionaryForm = reactive({
  id: null,
  dictType: '',
  dictLabel: '',
  dictValue: '',
  remark: '',
  status: 1,
  dictSort: 0
})

const dictionaryRules: FormRules = {
  dictType: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  dictLabel: [{ required: true, message: '请输入标签名', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入值', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const dictionaryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dictTypeList = ref([])
const groupedDictionaries = ref([])
const viewDictionaryDetail = ref(null)

const activeTab = ref('all')
const activeGroups = ref([])

const currentDictionaryList = computed(() => {
  return activeTab.value === 'all' ? dictionaryList.value : []
})

const getDictionaries = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const response = await request.get('/api/dictionary/page', { params })
    if (response.code === 200) {
      dictionaryList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取字典列表失败')
  }
  loading.value = false
}

const getDictTypes = async () => {
  try {
    const response = await request.get('/api/dictionary/types')
    if (response.code === 200) {
      dictTypeList.value = Object.keys(response.data)
    }
  } catch (error) {
    console.error('获取字典类型失败:', error)
  }
}

const getGroupedDictionaries = async () => {
  try {
    const response = await request.get('/api/dictionary/types')
    if (response.code === 200) {
      groupedDictionaries.value = Object.entries(response.data).map(([type, items]) => ({
        type,
        items
      }))
    }
  } catch (error) {
    console.error('获取分组字典失败:', error)
  }
}

const searchDictionaries = () => {
  currentPage.value = 1
  getDictionaries()
}

const resetSearch = () => {
  searchForm.dictType = ''
  searchForm.status = ''
  searchForm.dictLabel = ''
  searchDictionaries()
}

const handleTabClick = () => {
  if (activeTab.value === 'grouped') {
    getGroupedDictionaries()
  }
}

const showCreateDialog = () => {
  dialogType.value = 'create'
  dictionaryForm.id = null
  Object.assign(dictionaryForm, {
    dictType: '',
    dictLabel: '',
    dictValue: '',
    remark: '',
    status: 1,
    dictSort: 0
  })
  dialogVisible.value = true
}

const editDictionary = (row) => {
  dialogType.value = 'edit'
  Object.assign(dictionaryForm, row)
  dialogVisible.value = true
}

const viewDictionary = async (row) => {
  try {
    const response = await request.get(`/api/dictionary/${row.id}`)
    if (response.code === 200) {
      viewDictionaryDetail.value = response.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取字典详情失败')
  }
}

const saveDictionary = async () => {
  if (!dictionaryFormRef.value) return

  await dictionaryFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const url = dialogType.value === 'create'
          ? '/api/dictionary/create'
          : '/api/dictionary/update'

        const response = await request.post(url, dictionaryForm)
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'create' ? '创建成功' : '更新成功')
          dialogVisible.value = false
          getDictionaries()
          getDictTypes()
          if (activeTab.value === 'grouped') {
            getGroupedDictionaries()
          }
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

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(
      `确定要${newStatus === 1 ? '启用' : '禁用'}此字典项吗？`,
      '提示',
      { type: 'warning' }
    )

    const response = await request.post(`/api/dictionary/status/${row.id}`, null, {
      params: { status: newStatus }
    })
    if (response.code === 200) {
      ElMessage.success(`字典项已${newStatus === 1 ? '启用' : '禁用'}`)
      getDictionaries()
      getDictTypes()
      if (activeTab.value === 'grouped') {
        getGroupedDictionaries()
      }
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const deleteDictionary = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此字典项吗？', '提示', {
      type: 'warning'
    })

    const response = await request.delete(`/api/dictionary/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getDictionaries()
      getDictTypes()
      if (activeTab.value === 'grouped') {
        getGroupedDictionaries()
      }
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getDictionaries()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getDictionaries()
}

onMounted(() => {
  getDictionaries()
  getDictTypes()
})
</script>

<style scoped>
.dictionary-container {
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

.grouped-content {
  margin-top: 20px;
}

.group-title {
  font-weight: bold;
  margin-right: 10px;
}

:deep(.el-card) {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-collapse) {
  border: none;
}

:deep(.el-collapse-item__header) {
  font-weight: bold;
}
</style>