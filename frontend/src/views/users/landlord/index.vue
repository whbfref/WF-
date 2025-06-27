<template>
  <div class="landlord-application">
    <div class="application-container">
          <div class="page-header">
      <h1 class="page-title">申请成为房东</h1>
      <p class="page-desc">请填写真实信息，我们将在1-3个工作日内完成审核</p>
      <div v-if="!userStore.isLoggedIn" class="login-notice">
        <el-alert
          title="请先登录"
          description="您需要先登录才能申请成为房东"
          type="warning"
          show-icon
          :closable="false">
          <template #default>
            <div style="margin-top: 10px;">
              <el-button type="primary" @click="goToLogin">立即登录</el-button>
            </div>
          </template>
        </el-alert>
      </div>
    </div>

      <!-- 认证状态显示 -->
      <el-card v-if="verificationStatus" class="status-card" shadow="hover">
        <div class="status-content">
          <div class="status-icon" :class="getStatusClass(verificationStatus.status)">
            <el-icon>
              <component :is="getStatusIcon(verificationStatus.status)" />
            </el-icon>
          </div>
          <div class="status-info">
            <div class="status-title">{{ getStatusTitle(verificationStatus.status) }}</div>
            <div class="status-desc">{{ getStatusDesc(verificationStatus.status) }}</div>
            <div v-if="verificationStatus.reviewComment" class="review-comment">
              <strong>审核意见：</strong>{{ verificationStatus.reviewComment }}
            </div>
          </div>
          <div class="status-time">
            <div>申请时间：{{ formatTime(verificationStatus.submitTime) }}</div>
            <div v-if="verificationStatus.reviewTime">
              审核时间：{{ formatTime(verificationStatus.reviewTime) }}
            </div>
          </div>
        </div>
        
        <!-- 认证通过后的跳转按钮 -->
        <div v-if="verificationStatus.status === 'APPROVED'" class="approved-actions">
          <el-button type="primary" size="large" @click="goToLandlordDashboard">
            进入房东管理系统
          </el-button>
        </div>
      </el-card>

      <!-- 认证申请表单 -->
      <el-card v-if="!verificationStatus || verificationStatus.status === 'REJECTED' || verificationStatus.status === 'NOT_APPLIED'" class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">认证信息</span>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="120px"
          class="verification-form"
        >
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="form.realName"
              placeholder="请输入真实姓名"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="身份证号码" prop="idCard">
            <el-input
              v-model="form.idCard"
              placeholder="请输入身份证号码"
            />
          </el-form-item>

          <el-form-item label="身份证正面" prop="idCardFrontImage">
            <el-upload
              class="id-card-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleFrontImageSuccess"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <img v-if="form.idCardFrontImageUrl" :src="form.idCardFrontImageUrl" class="id-card-image" />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">上传身份证正面</div>
              </div>
            </el-upload>
          </el-form-item>

          <el-form-item label="身份证背面" prop="idCardBackImage">
            <el-upload
              class="id-card-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleBackImageSuccess"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <img v-if="form.idCardBackImageUrl" :src="form.idCardBackImageUrl" class="id-card-image" />
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">上传身份证背面</div>
              </div>
            </el-upload>
          </el-form-item>

          <el-form-item label="银行卡号" prop="bankCard">
            <el-input
              v-model="form.bankCard"
              placeholder="请输入银行卡号"
            />
          </el-form-item>

          <el-form-item label="银行名称" prop="bankName">
            <el-select
              v-model="form.bankName"
              placeholder="请选择银行"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="bank in bankList"
                :key="bank"
                :label="bank"
                :value="bank"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="form.contactPhone"
              placeholder="请输入联系电话"
            />
          </el-form-item>

          <el-form-item label="联系邮箱" prop="contactEmail">
            <el-input
              v-model="form.contactEmail"
              placeholder="请输入联系邮箱（选填）"
              type="email"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              @click="handleSubmit"
              class="submit-btn"
            >
              提交认证申请
            </el-button>
            <el-button size="large" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 认证须知 -->
      <el-card class="notice-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">认证须知</span>
          </div>
        </template>

        <div class="notice-content">
          <div class="notice-item">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <div class="notice-text">
              <strong>认证材料：</strong>请确保身份证照片清晰完整，四角完整可见，信息清楚可辨认
            </div>
          </div>
          <div class="notice-item">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <div class="notice-text">
              <strong>银行信息：</strong>银行卡号和银行名称必须准确无误，用于后续收款
            </div>
          </div>
          <div class="notice-item">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <div class="notice-text">
              <strong>审核时间：</strong>我们将在1-3个工作日内完成审核，请耐心等待
            </div>
          </div>
          <div class="notice-item">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <div class="notice-text">
              <strong>信息安全：</strong>您的个人信息将严格保密，仅用于身份认证
            </div>
          </div>
          <div class="notice-item">
            <el-icon class="notice-icon"><InfoFilled /></el-icon>
            <div class="notice-text">
              <strong>认证通过：</strong>认证通过后，您将可以发布房源并管理租赁业务
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  Plus, InfoFilled, CircleCheck, Warning, Clock 
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { applyLandlordVerification, getLandlordVerificationStatus, testAuth } from '@/api/landlords';

const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const submitting = ref(false);
const formRef = ref();
const verificationStatus = ref(null);

// 表单数据
const form = ref({
  realName: '',
  idCard: '',
  idCardFrontImageUrl: '',
  idCardBackImageUrl: '',
  bankCard: '',
  bankName: '',
  contactPhone: '',
  contactEmail: ''
});

// 银行列表
const bankList = [
  '中国工商银行', '中国农业银行', '中国银行', '中国建设银行',
  '交通银行', '招商银行', '浦发银行', '中信银行',
  '中国光大银行', '华夏银行', '中国民生银行', '广发银行',
  '平安银行', '兴业银行', '浙商银行', '恒丰银行'
];

// 上传配置
const uploadAction = '/api/v1/upload/file';
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}));

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2到50个字符', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { pattern: /^(\d{15}|\d{18}|\d{17}[\dXx])$/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ],
  bankCard: [
    { required: true, message: '请输入银行卡号', trigger: 'blur' },
    { pattern: /^\d{16,19}$/, message: '银行卡号应为16-19位数字', trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请选择银行', trigger: 'change' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

// 获取认证状态
const fetchVerificationStatus = async () => {
  try {
    const response = await getLandlordVerificationStatus();
    if (response && response.code === 200) {
      verificationStatus.value = response.data;
    }
  } catch (error) {
    console.log('暂无认证申请记录');
  }
};

// 获取状态图标
const getStatusIcon = (status) => {
  switch (status) {
    case 'PENDING':
      return Clock;
    case 'APPROVED':
      return CircleCheck;
    case 'REJECTED':
      return Warning;
    case 'NOT_APPLIED':
      return Clock;
    default:
      return Clock;
  }
};

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 'PENDING':
      return 'pending';
    case 'APPROVED':
      return 'approved';
    case 'REJECTED':
      return 'rejected';
    case 'NOT_APPLIED':
      return 'not-applied';
    default:
      return 'pending';
  }
};

// 获取状态标题
const getStatusTitle = (status) => {
  switch (status) {
    case 'PENDING':
      return '审核中';
    case 'APPROVED':
      return '认证通过';
    case 'REJECTED':
      return '认证被拒';
    case 'NOT_APPLIED':
      return '未申请';
    default:
      return '未知状态';
  }
};

// 获取状态描述
const getStatusDesc = (status) => {
  switch (status) {
    case 'PENDING':
      return '您的认证申请正在审核中，请耐心等待';
    case 'APPROVED':
      return '恭喜！您的房东认证已通过，现在可以发布房源了';
    case 'REJECTED':
      return '很抱歉，您的认证申请被拒绝，请重新提交';
    case 'NOT_APPLIED':
      return '请填写真实信息，我们将在1-3个工作日内完成审核';
    default:
      return '';
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 身份证正面上传成功
const handleFrontImageSuccess = (response) => {
  console.log('身份证正面上传响应:', response);
  if (response.code === 200) {
    console.log('上传成功，图片URL:', response.data);
    // 确保URL格式正确，移除可能的/api/v1前缀
    let imageUrl = response.data;
    if (imageUrl.includes('/api/v1/files/')) {
      imageUrl = imageUrl.replace('/api/v1/files/', '/files/');
    }
    console.log('处理后的图片URL:', imageUrl);
    form.value.idCardFrontImageUrl = imageUrl;
    ElMessage.success('身份证正面上传成功');
  } else {
    console.error('上传失败，响应:', response);
    ElMessage.error('上传失败：' + response.message);
  }
};

// 身份证背面上传成功
const handleBackImageSuccess = (response) => {
  console.log('身份证背面上传响应:', response);
  if (response.code === 200) {
    console.log('上传成功，图片URL:', response.data);
    // 确保URL格式正确，移除可能的/api/v1前缀
    let imageUrl = response.data;
    if (imageUrl.includes('/api/v1/files/')) {
      imageUrl = imageUrl.replace('/api/v1/files/', '/files/');
    }
    console.log('处理后的图片URL:', imageUrl);
    form.value.idCardBackImageUrl = imageUrl;
    ElMessage.success('身份证背面上传成功');
  } else {
    console.error('上传失败，响应:', response);
    ElMessage.error('上传失败：' + response.message);
  }
};

// 提交申请
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    if (!form.value.idCardFrontImageUrl || !form.value.idCardBackImageUrl) {
      ElMessage.error('请上传身份证正反面照片');
      return;
    }
    
    // 额外的数据验证
    if (form.value.realName.trim().length < 2) {
      ElMessage.error('真实姓名至少需要2个字符');
      return;
    }
    
    if (!/^(\d{15}|\d{18}|\d{17}[\dXx])$/.test(form.value.idCard)) {
      ElMessage.error('身份证号格式不正确');
      return;
    }
    
    if (!/^\d{16,19}$/.test(form.value.bankCard)) {
      ElMessage.error('银行卡号格式不正确');
      return;
    }
    
    if (!/^1[3-9]\d{9}$/.test(form.value.contactPhone)) {
      ElMessage.error('手机号格式不正确');
      return;
    }
    
    // 调试信息：打印表单数据
    console.log('提交表单数据:', {
      realName: form.value.realName,
      idCard: form.value.idCard,
      idCardFrontImageUrl: form.value.idCardFrontImageUrl,
      idCardBackImageUrl: form.value.idCardBackImageUrl,
      bankCard: form.value.bankCard,
      bankName: form.value.bankName,
      contactPhone: form.value.contactPhone,
      contactEmail: form.value.contactEmail
    });
    
    submitting.value = true;
    
    // 创建FormData
    const formData = new FormData();
    formData.append('realName', form.value.realName);
    formData.append('idCard', form.value.idCard);
    formData.append('idCardFrontImageUrl', form.value.idCardFrontImageUrl);
    formData.append('idCardBackImageUrl', form.value.idCardBackImageUrl);
    formData.append('bankCard', form.value.bankCard);
    formData.append('bankName', form.value.bankName);
    formData.append('contactPhone', form.value.contactPhone);
    formData.append('contactEmail', form.value.contactEmail || '');
    
    // 调试信息：打印FormData内容
    console.log('FormData内容:');
    for (let [key, value] of formData.entries()) {
      console.log(key, ':', value);
    }
    
    const response = await applyLandlordVerification(formData);
    
    if (response && response.code === 200) {
      ElMessage.success('认证申请提交成功！');
      await fetchVerificationStatus();
      handleReset();
    } else {
      throw new Error(response?.message || '提交失败');
    }
  } catch (error) {
    console.error('提交认证申请失败:', error);
    
    // 根据错误类型提供更具体的错误信息
    let errorMessage = '提交失败：';
    if (error.message) {
      if (error.message.includes('认证申请已存在')) {
        errorMessage = '您已有认证申请，请勿重复提交';
      } else if (error.message.includes('身份证号已被使用')) {
        errorMessage = '该身份证号已被其他用户使用';
      } else if (error.message.includes('保存认证申请失败')) {
        errorMessage = '数据保存失败，请检查填写信息是否正确';
      } else {
        errorMessage += error.message;
      }
    } else {
      errorMessage += '请稍后重试';
    }
    
    ElMessage.error(errorMessage);
  } finally {
    submitting.value = false;
  }
};

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.value = {
    realName: '',
    idCard: '',
    idCardFrontImageUrl: '',
    idCardBackImageUrl: '',
    bankCard: '',
    bankName: '',
    contactPhone: '',
    contactEmail: ''
  };
};

// 跳转到房东管理系统
const goToLandlordDashboard = () => {
  router.push('/landlord/dashboard');
};

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login');
};

// 组件挂载时获取认证状态
onMounted(async () => {
  // 检查用户登录状态
  console.log('用户登录状态:', userStore.isLoggedIn);
  console.log('用户信息:', userStore.userInfo);
  console.log('用户token:', userStore.token);
  console.log('localStorage中的用户数据:', localStorage.getItem('rental_user'));
  
  // 尝试从localStorage恢复用户状态
  await userStore.getUserInfoAction();
  
  console.log('恢复后的用户登录状态:', userStore.isLoggedIn);
  console.log('恢复后的用户token:', userStore.token);
  
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再申请成为房东');
    router.push('/login');
    return;
  }
  
  // 获取认证状态
  try {
    await fetchVerificationStatus();
  } catch (error) {
    console.log('获取认证状态失败，可能是首次申请');
  }
  
  // 测试后端认证
  try {
    const authTest = await testAuth();
    console.log('后端认证测试结果:', authTest);
    
    if (authTest && authTest.code === 200) {
      console.log('认证成功，用户ID:', authTest.data.userId);
    } else {
      throw new Error('认证失败');
    }
  } catch (error) {
    console.error('后端认证测试失败:', error);
    ElMessage.error('认证失败，请重新登录');
    userStore.resetState();
    localStorage.removeItem('rental_user');
    router.push('/login');
  }
});
</script>

<style lang="scss" scoped>
.landlord-application {
  .application-container {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
  }

  .page-header {
    text-align: center;
    margin-bottom: 30px;

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: #2c3e50;
      margin: 0 0 10px 0;
    }

    .page-desc {
      font-size: 16px;
      color: #7f8c8d;
      margin: 0;
    }
  }

  .status-card {
    margin-bottom: 30px;

    .status-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .status-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;

        &.pending {
          background: #fff7e6;
          color: #fa8c16;
        }

        &.approved {
          background: #f6ffed;
          color: #52c41a;
        }

        &.rejected {
          background: #fff2f0;
          color: #ff4d4f;
        }

        &.not-applied {
          background: #f0f9ff;
          color: #1890ff;
        }
      }

      .status-info {
        flex: 1;

        .status-title {
          font-size: 20px;
          font-weight: 600;
          margin-bottom: 8px;
        }

        .status-desc {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
        }

        .review-comment {
          font-size: 14px;
          color: #ff4d4f;
          background: #fff2f0;
          padding: 8px 12px;
          border-radius: 4px;
          border-left: 3px solid #ff4d4f;
        }
      }

      .status-time {
        text-align: right;
        font-size: 12px;
        color: #999;

        div {
          margin-bottom: 4px;
        }
      }
    }

    .approved-actions {
      margin-top: 20px;
      text-align: center;
      padding-top: 20px;
      border-top: 1px solid #f0f0f0;
    }
  }

  .form-card {
    margin-bottom: 30px;

    .verification-form {
      .id-card-uploader {
        .id-card-image {
          width: 200px;
          height: 120px;
          object-fit: cover;
          border-radius: 6px;
          border: 1px solid #dcdfe6;
        }

        .upload-placeholder {
          width: 200px;
          height: 120px;
          border: 2px dashed #dcdfe6;
          border-radius: 6px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: border-color 0.3s;

          &:hover {
            border-color: #409eff;
          }

          .upload-icon {
            font-size: 24px;
            color: #8c939d;
            margin-bottom: 8px;
          }

          .upload-text {
            font-size: 14px;
            color: #8c939d;
          }
        }
      }

      .submit-btn {
        width: 200px;
      }
    }
  }

  .notice-card {
    .notice-content {
      .notice-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .notice-icon {
          color: #409eff;
          margin-right: 12px;
          margin-top: 2px;
          flex-shrink: 0;
        }

        .notice-text {
          font-size: 14px;
          line-height: 1.6;
          color: #606266;
        }
      }
    }
  }

  .card-header {
    .header-title {
      font-size: 18px;
      font-weight: 600;
      color: #2c3e50;
    }
  }
}
</style>