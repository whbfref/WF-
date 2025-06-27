<template>
  <div class="change-password-container">
    <el-dialog
      v-model="visible"
      title="修改密码"
      width="500px"
      :before-close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="手机验证码" prop="verifyCode">
          <div class="verify-code-input">
            <el-input
              v-model="form.verifyCode"
              placeholder="请输入验证码"
              clearable
            />
            <el-button
              :disabled="countdown > 0"
              :loading="sendingCode"
              @click="sendVerifyCode"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            clearable
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleSubmit"
          >
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { changePassword, sendVerificationCode } from '@/api/user';

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  userPhone: {
    type: String,
    default: ''
  }
});

// Emits
const emit = defineEmits(['update:modelValue', 'success']);

// 响应式数据
const visible = ref(false);
const loading = ref(false);
const sendingCode = ref(false);
const countdown = ref(0);
const formRef = ref(null);

// 表单数据
const form = reactive({
  oldPassword: '',
  verifyCode: '',
  newPassword: '',
  confirmPassword: ''
});

// 验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 监听props变化
watch(() => props.modelValue, (val) => {
  visible.value = val;
});

watch(visible, (val) => {
  emit('update:modelValue', val);
  if (!val) {
    resetForm();
  }
});

// 发送验证码
const sendVerifyCode = async () => {
  if (!props.userPhone) {
    ElMessage.warning('请先在个人信息中设置手机号');
    return;
  }
  
  try {
    sendingCode.value = true;
    const response = await sendVerificationCode(props.userPhone);
    
    if (response && response.code === 200) {
      ElMessage.success('验证码已发送');
      
      // 显示验证码弹窗
      if (response.data && response.data.verifyCode) {
        ElMessageBox.alert(
          `您的验证码是：${response.data.verifyCode}`,
          '验证码',
          {
            confirmButtonText: '确定',
            type: 'info',
            center: true
          }
        );
      }
      
      startCountdown();
    } else {
      ElMessage.error(response?.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    ElMessage.error('发送验证码失败，请稍后重试');
  } finally {
    sendingCode.value = false;
  }
};

// 开始倒计时
const startCountdown = () => {
  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      loading.value = true;
      const response = await changePassword({
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
        verifyCode: form.verifyCode,
        phone: props.userPhone
      });
      
      if (response && response.code === 200) {
        ElMessage.success('密码修改成功');
        emit('success');
        handleClose();
      } else {
        ElMessage.error(response?.message || '密码修改失败');
      }
    } catch (error) {
      console.error('密码修改失败:', error);
      ElMessage.error('密码修改失败，请稍后重试');
    } finally {
      loading.value = false;
    }
  });
};

// 关闭对话框
const handleClose = () => {
  visible.value = false;
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(form, {
    oldPassword: '',
    verifyCode: '',
    newPassword: '',
    confirmPassword: ''
  });
  countdown.value = 0;
};
</script>

<style scoped>
.verify-code-input {
  display: flex;
  gap: 10px;
}

.verify-code-input .el-input {
  flex: 1;
}

.verify-code-input .el-button {
  white-space: nowrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 