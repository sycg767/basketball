<template>
  <el-dialog
    v-model="visible"
    title="绑定手机号"
    width="450px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="0"
    >
      <el-form-item prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          size="large"
          prefix-icon="Iphone"
          maxlength="11"
        />
      </el-form-item>

      <el-form-item prop="code">
        <div class="code-input-wrapper">
          <el-input
            v-model="form.code"
            placeholder="请输入验证码"
            size="large"
            prefix-icon="Message"
            maxlength="6"
          />
          <SmsCountdown
            ref="smsCountdownRef"
            :disabled="!form.phone || !validatePhone(form.phone)"
            :loading="sendingCode"
            text="发送验证码"
            class="send-code-btn"
            @send="handleSendCode"
          />
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleBind">确认绑定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { validatePhone } from '@/utils/validate';
import { sendSmsCode } from '@/api/auth';
import { bindPhone } from '@/api/user-bind';
import SmsCountdown from './SmsCountdown.vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const visible = ref(false);
const loading = ref(false);
const sendingCode = ref(false);
const formRef = ref(null);
const smsCountdownRef = ref(null);

const form = reactive({
  phone: '',
  code: ''
});

watch(() => props.modelValue, (val) => {
  visible.value = val;
  if (!val) {
    resetForm();
  }
});

watch(visible, (val) => {
  emit('update:modelValue', val);
});

const validatePhoneRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'));
  } else if (!validatePhone(value)) {
    callback(new Error('请输入正确的手机号'));
  } else {
    callback();
  }
};

const validateCodeRule = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'));
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('请输入6位数字验证码'));
  } else {
    callback();
  }
};

const rules = {
  phone: [{ validator: validatePhoneRule, trigger: 'blur' }],
  code: [{ validator: validateCodeRule, trigger: 'blur' }]
};

const handleSendCode = async () => {
  if (!form.phone) {
    ElMessage.warning('请输入手机号');
    return;
  }

  if (!validatePhone(form.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }

  sendingCode.value = true;
  try {
    const res = await sendSmsCode({
      phone: form.phone,
      type: 'bind'
    });

    if (res.code === 200) {
      ElMessage.success('验证码已发送,请注意查收');
      smsCountdownRef.value?.startCountdown();
    } else {
      ElMessage.error(res.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    ElMessage.error('发送验证码失败,请稍后重试');
  } finally {
    sendingCode.value = false;
  }
};

const handleBind = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await bindPhone({
          phone: form.phone,
          code: form.code
        });

        if (res.code === 200) {
          ElMessage.success('手机号绑定成功');
          emit('success');
          visible.value = false;
        } else {
          ElMessage.error(res.message || '绑定失败');
        }
      } catch (error) {
        console.error('绑定手机号失败:', error);
        ElMessage.error('绑定失败,请检查验证码是否正确');
      } finally {
        loading.value = false;
      }
    }
  });
};

const resetForm = () => {
  form.phone = '';
  form.code = '';
  formRef.value?.clearValidate();
  smsCountdownRef.value?.stopCountdown();
};

const handleClose = () => {
  resetForm();
};
</script>

<style scoped>
.code-input-wrapper {
  display: flex;
  gap: 10px;
}

.code-input-wrapper .el-input {
  flex: 1;
}

.send-code-btn {
  width: 120px;
  flex-shrink: 0;
}
</style>
