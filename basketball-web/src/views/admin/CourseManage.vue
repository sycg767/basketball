<template>
  <div class="course-manage-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入课程名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.courseType" placeholder="请选择类型" clearable>
            <el-option label="全部" value="" />
            <el-option label="基础班" :value="1" />
            <el-option label="提高班" :value="2" />
            <el-option label="专业班" :value="3" />
            <el-option label="私教课" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="primary" icon="Plus" @click="handleAdd">添加课程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="courseList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="courseCode" label="课程编号" width="140" />
        <el-table-column label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCourseTypeColor(row.courseType)">
              {{ getCourseTypeText(row.courseType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyColor(row.difficultyLevel)" size="small">
              {{ getDifficultyText(row.difficultyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="人数" width="100">
          <template #default="{ row }">
            {{ row.minStudents }}-{{ row.maxStudents }}人
          </template>
        </el-table-column>
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div>¥{{ row.price }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Calendar" @click="handleSchedule(row)">
              排期
            </el-button>
            <el-button type="primary" link icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="searchForm.page"
          v-model:page-size="searchForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程编号" prop="courseCode">
              <el-input v-model="form.courseCode" placeholder="请输入课程编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择类型" style="width: 100%">
                <el-option label="基础班" :value="1" />
                <el-option label="提高班" :value="2" />
                <el-option label="专业班" :value="3" />
                <el-option label="私教课" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教练" prop="coachId">
              <el-select v-model="form.coachId" placeholder="请选择教练" style="width: 100%">
                <el-option
                  v-for="coach in coachList"
                  :key="coach.id"
                  :label="coach.realName"
                  :value="coach.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficultyLevel">
              <el-select v-model="form.difficultyLevel" placeholder="请选择难度" style="width: 100%">
                <el-option label="入门" :value="1" />
                <el-option label="初级" :value="2" />
                <el-option label="中级" :value="3" />
                <el-option label="高级" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适龄范围" prop="ageRange">
              <el-input v-model="form.ageRange" placeholder="例如: 10-15岁" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最小人数" prop="minStudents">
              <el-input-number v-model="form.minStudents" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number v-model="form.maxStudents" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="课时(分钟)" prop="duration">
              <el-input-number v-model="form.duration" :min="30" :step="15" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="总课时数" prop="totalSessions">
              <el-input-number v-model="form.totalSessions" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-alert
              title="会员折扣说明"
              type="info"
              :closable="false"
              style="margin-bottom: 15px;">
              会员价格根据会员等级自动计算：1级9.5折，2级9折，3级8.5折，4级8折，5级7.5折
            </el-alert>
          </el-col>
        </el-row>

        <el-form-item label="课程封面">
          <el-input v-model="form.coverImage" placeholder="请输入封面图片URL" />
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>

        <el-form-item label="课程大纲">
          <el-input
            v-model="syllabusText"
            type="textarea"
            :rows="5"
            placeholder="每行一个课时内容，例如：第1课：基础知识"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 排期管理对话框 -->
    <el-dialog
      v-model="scheduleDialogVisible"
      title="课程排期管理"
      width="1000px"
    >
      <div class="schedule-header">
        <el-button type="primary" icon="Plus" @click="handleAddSchedule">
          添加排期
        </el-button>
      </div>

      <el-table :data="scheduleList" stripe border v-loading="scheduleLoading">
        <el-table-column prop="scheduleNo" label="排期编号" width="160" />
        <el-table-column label="上课日期" width="120">
          <template #default="{ row }">
            {{ row.scheduleDate }}
          </template>
        </el-table-column>
        <el-table-column label="上课时间" width="180">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="报名情况" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round((row.enrolledCount / row.maxStudents) * 100)"
              :color="getProgressColor(row.enrolledCount, row.maxStudents)"
            >
              <span>{{ row.enrolledCount }}/{{ row.maxStudents }}</span>
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="checkedInCount" label="已签到" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getScheduleStatusColor(row.status)">
              {{ getScheduleStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              icon="User"
              @click="handleViewEnrollments(row)"
            >
              学员
            </el-button>
            <el-button
              type="danger"
              link
              icon="Close"
              @click="handleCancelSchedule(row)"
              :disabled="row.status === 5"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加排期对话框 -->
    <el-dialog
      v-model="addScheduleDialogVisible"
      title="添加课程排期"
      width="500px"
    >
      <el-form
        ref="scheduleFormRef"
        :model="scheduleForm"
        :rules="scheduleFormRules"
        label-width="100px"
      >
        <el-form-item label="上课场地" prop="venueId">
          <el-input
            v-model="scheduleForm.venueId"
            type="number"
            placeholder="请输入场地ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="上课日期" prop="scheduleDate">
          <el-date-picker
            v-model="scheduleForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="scheduleForm.startTime"
            placeholder="选择时间"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="scheduleForm.endTime"
            placeholder="选择时间"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number
            v-model="scheduleForm.maxStudents"
            :min="1"
            :max="100"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="scheduleForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addScheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleScheduleSubmit" :loading="scheduleSubmitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getCourseList,
  getCourseDetail,
  createCourse,
  updateCourse,
  deleteCourse,
  updateCourseStatus,
  getScheduleList,
  createSchedule,
  cancelSchedule,
  getCoachList
} from '@/api/admin';

const loading = ref(false);
const courseList = ref([]);
const total = ref(0);

const searchForm = reactive({
  keyword: '',
  courseType: '',
  status: '',
  page: 1,
  pageSize: 10
});

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const submitting = ref(false);

const form = reactive({
  id: null,
  courseName: '',
  courseCode: '',
  courseType: null,
  coachId: null,
  maxStudents: 20,
  minStudents: 10,
  price: 0,
  duration: 90,
  totalSessions: 12,
  difficultyLevel: null,
  ageRange: '',
  description: '',
  coverImage: '',
  status: 1
});

const syllabusText = ref('');
const coachList = ref([]);

const formRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  minStudents: [{ required: true, message: '请输入最小人数', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入课时', trigger: 'blur' }],
  totalSessions: [{ required: true, message: '请输入总课时数', trigger: 'blur' }],
  description: [{ required: true, message: '请输入课程描述', trigger: 'blur' }]
};

// 排期管理
const scheduleDialogVisible = ref(false);
const scheduleLoading = ref(false);
const scheduleList = ref([]);
const currentCourse = ref(null);

// 添加排期
const addScheduleDialogVisible = ref(false);
const scheduleFormRef = ref(null);
const scheduleSubmitting = ref(false);

const scheduleForm = reactive({
  courseId: null,
  venueId: null,
  scheduleDate: '',
  startTime: '',
  endTime: '',
  maxStudents: 20,
  remark: ''
});

const scheduleFormRules = {
  venueId: [{ required: true, message: '请输入场地ID', trigger: 'blur' }],
  scheduleDate: [{ required: true, message: '请选择上课日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
};

// 获取课程列表
const loadCourseList = async () => {
  try {
    loading.value = true;
    const { data } = await getCourseList(searchForm);
    courseList.value = data.records || [];
    total.value = data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败:', error);
    ElMessage.error('获取课程列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  searchForm.page = 1;
  loadCourseList();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    courseType: '',
    status: '',
    page: 1,
    pageSize: 10
  });
  loadCourseList();
};

// 添加
const handleAdd = async () => {
  await loadCoachList();
  dialogTitle.value = '添加课程';
  resetForm();
  dialogVisible.value = true;
};

// 编辑
const handleEdit = async (row) => {
  await loadCoachList();
  dialogTitle.value = '编辑课程';
  try {
    const { data } = await getCourseDetail(row.id);
    Object.assign(form, data);

    // 处理课程大纲
    if (data.syllabus) {
      try {
        const syllabus = JSON.parse(data.syllabus);
        syllabusText.value = syllabus.join('\n');
      } catch (e) {
        syllabusText.value = '';
      }
    }

    dialogVisible.value = true;
  } catch (error) {
    console.error('获取课程详情失败:', error);
    ElMessage.error('获取课程详情失败');
  }
};

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除课程"${row.courseName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCourse(row.id);
      ElMessage.success('删除成功');
      loadCourseList();
    } catch (error) {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
};

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await updateCourseStatus(row.id, row.status);
    ElMessage.success('状态更新成功');
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('状态更新失败');
    row.status = row.status === 1 ? 0 : 1;
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    try {
      submitting.value = true;

      // 处理课程大纲
      const formData = { ...form };
      if (syllabusText.value) {
        formData.syllabus = JSON.stringify(
          syllabusText.value.split('\n').filter(line => line.trim())
        );
      }

      if (form.id) {
        await updateCourse(form.id, formData);
        ElMessage.success('更新成功');
      } else {
        await createCourse(formData);
        ElMessage.success('添加成功');
      }

      dialogVisible.value = false;
      loadCourseList();
    } catch (error) {
      console.error('提交失败:', error);
      ElMessage.error('提交失败');
    } finally {
      submitting.value = false;
    }
  });
};

// 排期管理
const handleSchedule = async (row) => {
  currentCourse.value = row;
  await loadScheduleList(row.id);
  scheduleDialogVisible.value = true;
};

// 获取排期列表
const loadScheduleList = async (courseId) => {
  try {
    scheduleLoading.value = true;
    const { data } = await getScheduleList(courseId);
    scheduleList.value = data || [];
  } catch (error) {
    console.error('获取排期列表失败:', error);
    ElMessage.error('获取排期列表失败');
  } finally {
    scheduleLoading.value = false;
  }
};

// 添加排期
const handleAddSchedule = () => {
  resetScheduleForm();
  scheduleForm.courseId = currentCourse.value.id;
  scheduleForm.maxStudents = currentCourse.value.maxStudents;
  addScheduleDialogVisible.value = true;
};

// 提交排期
const handleScheduleSubmit = async () => {
  if (!scheduleFormRef.value) return;

  await scheduleFormRef.value.validate(async (valid) => {
    if (!valid) return;

    try {
      scheduleSubmitting.value = true;
      await createSchedule(scheduleForm);
      ElMessage.success('添加排期成功');
      addScheduleDialogVisible.value = false;
      loadScheduleList(currentCourse.value.id);
    } catch (error) {
      console.error('添加排期失败:', error);
      ElMessage.error('添加排期失败');
    } finally {
      scheduleSubmitting.value = false;
    }
  });
};

// 取消排期
const handleCancelSchedule = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消排期', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入取消原因'
  }).then(async ({ value }) => {
    try {
      await cancelSchedule(row.id, value);
      ElMessage.success('取消成功');
      loadScheduleList(currentCourse.value.id);
    } catch (error) {
      console.error('取消失败:', error);
      ElMessage.error('取消失败');
    }
  }).catch(() => {});
};

// 查看学员
const handleViewEnrollments = (row) => {
  ElMessage.info('学员管理功能开发中...');
};

// 获取教练列表
const loadCoachList = async () => {
  try {
    const { data } = await getCoachList();
    coachList.value = data.records || [];
  } catch (error) {
    console.error('获取教练列表失败:', error);
  }
};

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    courseName: '',
    courseCode: '',
    courseType: null,
    coachId: null,
    maxStudents: 20,
    minStudents: 10,
    price: 0,
    duration: 90,
    totalSessions: 12,
    difficultyLevel: null,
    ageRange: '',
    description: '',
    coverImage: '',
    status: 1
  });
  syllabusText.value = '';
  formRef.value?.clearValidate();
};

// 重置排期表单
const resetScheduleForm = () => {
  Object.assign(scheduleForm, {
    courseId: null,
    scheduleDate: '',
    startTime: '',
    endTime: '',
    maxStudents: 20,
    remark: ''
  });
  scheduleFormRef.value?.clearValidate();
};

// 关闭对话框
const handleDialogClose = () => {
  resetForm();
};

// 辅助函数
const getCourseTypeText = (type) => {
  const map = { 1: '基础班', 2: '提高班', 3: '专业班', 4: '私教课' };
  return map[type] || '未知';
};

const getCourseTypeColor = (type) => {
  const map = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'danger' };
  return map[type] || 'info';
};

const getDifficultyText = (level) => {
  const map = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' };
  return map[level] || '未知';
};

const getDifficultyColor = (level) => {
  const map = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'danger' };
  return map[level] || 'info';
};

const getScheduleStatusText = (status) => {
  const map = {
    0: '未开始',
    1: '报名中',
    2: '已满员',
    3: '进行中',
    4: '已结束',
    5: '已取消'
  };
  return map[status] || '未知';
};

const getScheduleStatusColor = (status) => {
  const map = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: '',
    4: 'success',
    5: 'danger'
  };
  return map[status] || 'info';
};

const getProgressColor = (current, max) => {
  const percentage = (current / max) * 100;
  if (percentage < 50) return '#67c23a';
  if (percentage < 80) return '#e6a23c';
  return '#f56c6c';
};

onMounted(() => {
  loadCourseList();
});
</script>

<style lang="scss" scoped>
@use '@/styles/design-system/variables' as *;

.course-manage-container {
  .filter-card, .table-card {
    margin-bottom: 24px;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    box-shadow: $shadow-sm;

    :deep(.el-card__body) {
      padding: 24px;
    }

    :deep(.el-button) {
      border-radius: $radius-md;
      font-weight: 500;
      transition: all $duration-fast $ease-in-out;

      &:hover {
        transform: translateY(-1px);
      }
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
    }
  }

  .member-price {
    font-size: 12px;
    color: $text-tertiary;
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }

  .schedule-header {
    margin-bottom: 20px;
  }
}
</style>
