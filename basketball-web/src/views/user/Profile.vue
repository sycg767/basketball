<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>个人中心</h2>
    </div>

    <el-card class="profile-card">
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="infoForm.username" disabled />
            </el-form-item>

            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="infoForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <el-form-item label="手机号">
              <el-input v-model="infoForm.phone" disabled />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="性别">
              <el-radio-group v-model="infoForm.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="会员等级">
              <el-tag v-if="infoForm.memberLevel === 0">普通用户</el-tag>
              <el-tag v-else-if="infoForm.memberLevel === 1" type="info">银卡会员</el-tag>
              <el-tag v-else-if="infoForm.memberLevel === 2" type="warning">金卡会员</el-tag>
              <el-tag v-else-if="infoForm.memberLevel === 3" type="primary">铂金会员</el-tag>
              <el-tag v-else-if="infoForm.memberLevel === 4" type="danger">VIP会员</el-tag>
            </el-form-item>

            <el-form-item label="积分">
              <span>{{ infoForm.points || 0 }}</span>
            </el-form-item>

            <el-form-item label="余额">
              <span>¥{{ infoForm.balance || 0 }}</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 账号绑定 -->
        <el-tab-pane label="账号绑定" name="binding">
          <div class="binding-section">
            <!-- 手机号绑定 -->
            <div class="binding-item">
              <div class="binding-info">
                <div class="binding-icon">
                  <el-icon :size="24"><Iphone /></el-icon>
                </div>
                <div class="binding-content">
                  <h4>手机号</h4>
                  <p v-if="infoForm.phone" class="binding-value">
                    {{ maskPhone(infoForm.phone) }}
                  </p>
                  <p v-else class="binding-placeholder">未绑定</p>
                </div>
              </div>
              <div class="binding-action">
                <el-button
                  v-if="infoForm.phone"
                  type="danger"
                  plain
                  @click="handleUnbindPhone"
                >
                  解绑
                </el-button>
                <el-button
                  v-else
                  type="primary"
                  @click="showBindPhoneDialog"
                >
                  绑定
                </el-button>
              </div>
            </div>

            <el-divider />

            <!-- 微信绑定 -->
            <div class="binding-item">
              <div class="binding-info">
                <div class="binding-icon wechat">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-wechat"></use>
                  </svg>
                </div>
                <div class="binding-content">
                  <h4>微信账号</h4>
                  <p v-if="wechatBound" class="binding-value">
                    {{ wechatInfo.nickname || '已绑定' }}
                  </p>
                  <p v-else class="binding-placeholder">未绑定</p>
                </div>
              </div>
              <div class="binding-action">
                <el-button
                  v-if="wechatBound"
                  type="danger"
                  plain
                  @click="handleUnbindWechat"
                >
                  解绑
                </el-button>
                <el-button
                  v-else
                  type="success"
                  @click="handleBindWechat"
                >
                  绑定
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 绑定手机号对话框 -->
    <BindPhoneDialog
      v-model="bindPhoneDialogVisible"
      @success="handleBindPhoneSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Iphone } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/modules/user';
import { updateUserInfo, updatePassword } from '@/api/user';
import { unbindPhone, unbindWechat } from '@/api/user-bind';
import { validateEmail, validatePassword } from '@/utils/validate';
import BindPhoneDialog from '@/components/BindPhoneDialog.vue';

const userStore = useUserStore();

const activeTab = ref('info');
const infoFormRef = ref(null);
const passwordFormRef = ref(null);
const infoLoading = ref(false);
const passwordLoading = ref(false);
const bindPhoneDialogVisible = ref(false);
const wechatBound = ref(false);
const wechatInfo = reactive({
  nickname: '',
  avatar: ''
});

const infoForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  gender: 0,
  memberLevel: 0,
  points: 0,
  balance: 0
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 基本信息验证规则
const validateEmailRule = (rule, value, callback) => {
  if (value && !validateEmail(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

const infoRules = {
  email: [{ validator: validateEmailRule, trigger: 'blur' }]
};

// 密码验证规则
const validateNewPasswordRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'));
  } else if (!validatePassword(value)) {
    callback(new Error('密码长度为6-20位'));
  } else {
    callback();
  }
};

const validateConfirmPasswordRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'));
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ validator: validateNewPasswordRule, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPasswordRule, trigger: 'blur' }]
};

// 获取用户信息
const loadUserInfo = async () => {
  try {
    await userStore.getUserInfo();
    Object.assign(infoForm, userStore.userInfo);
  } catch (error) {
    console.error('获取用户信息失败', error);
  }
};

// 更新用户信息
const handleUpdateInfo = () => {
  infoFormRef.value.validate(async (valid) => {
    if (valid) {
      infoLoading.value = true;
      try {
        await updateUserInfo({
          realName: infoForm.realName,
          email: infoForm.email,
          gender: infoForm.gender
        });
        ElMessage.success('保存成功');
        await loadUserInfo();
      } catch (error) {
        console.error('保存失败', error);
      } finally {
        infoLoading.value = false;
      }
    }
  });
};

// 修改密码
const handleUpdatePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true;
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        });
        ElMessage.success('密码修改成功');
        // 清空表单
        passwordForm.oldPassword = '';
        passwordForm.newPassword = '';
        passwordForm.confirmPassword = '';
        passwordFormRef.value.resetFields();
      } catch (error) {
        console.error('密码修改失败', error);
      } finally {
        passwordLoading.value = false;
      }
    }
  });
};

// 手机号脱敏
const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

// 显示绑定手机号对话框
const showBindPhoneDialog = () => {
  bindPhoneDialogVisible.value = true;
};

// 绑定手机号成功
const handleBindPhoneSuccess = () => {
  loadUserInfo();
};

// 解绑手机号
const handleUnbindPhone = async () => {
  try {
    await ElMessageBox.confirm(
      '解绑后将无法使用手机号登录,确定要解绑吗?',
      '确认解绑',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const res = await unbindPhone();
    if (res.code === 200) {
      ElMessage.success('手机号解绑成功');
      await loadUserInfo();
    } else {
      ElMessage.error(res.message || '解绑失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解绑手机号失败:', error);
      ElMessage.error('解绑失败,请稍后重试');
    }
  }
};

// 绑定微信
const handleBindWechat = () => {
  ElMessage.info('微信绑定功能开发中');
  // TODO: 实现微信绑定逻辑
};

// 解绑微信
const handleUnbindWechat = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要解绑微信账号吗?',
      '确认解绑',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const res = await unbindWechat();
    if (res.code === 200) {
      ElMessage.success('微信解绑成功');
      wechatBound.value = false;
      wechatInfo.nickname = '';
      wechatInfo.avatar = '';
    } else {
      ElMessage.error(res.message || '解绑失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解绑微信失败:', error);
      ElMessage.error('解绑失败,请稍后重试');
    }
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;

  .profile-header {
    margin-bottom: 20px;

    h2 {
      font-size: 20px;
      color: #303133;
    }
  }

  .profile-card {
    :deep(.el-tabs__item) {
      font-size: 16px;
    }

    .binding-section {
      padding: 20px 0;

      .binding-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 20px 0;

        .binding-info {
          display: flex;
          align-items: center;
          gap: 16px;

          .binding-icon {
            width: 48px;
            height: 48px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            background: #F5F7FA;

            &.wechat {
              background: #07C160;

              .icon {
                width: 24px;
                height: 24px;
                fill: #fff;
              }
            }

            .el-icon {
              color: #409EFF;
            }
          }

          .binding-content {
            h4 {
              margin: 0 0 8px 0;
              font-size: 16px;
              color: #303133;
              font-weight: 500;
            }

            .binding-value {
              margin: 0;
              font-size: 14px;
              color: #606266;
            }

            .binding-placeholder {
              margin: 0;
              font-size: 14px;
              color: #909399;
            }
          }
        }

        .binding-action {
          flex-shrink: 0;
        }
      }
    }
  }
}
</style>