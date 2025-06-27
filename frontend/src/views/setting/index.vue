<template>
  <div class="setting-container">
    <h2 class="page-title">系统设置</h2>
    
    <el-card class="setting-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>基本设置</h3>
          <el-button type="primary" @click="handleBasicSave">保存设置</el-button>
        </div>
      </template>
      <el-form ref="basicFormRef" :model="basicForm" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="basicForm.siteName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="系统描述">
          <el-input 
            v-model="basicForm.siteDescription" 
            type="textarea"
            placeholder="请输入系统描述"
          />
        </el-form-item>
        <el-form-item label="Logo地址">
          <div class="logo-upload">
            <el-upload
              class="avatar-uploader"
              action="/api/v1/admin/upload/logo"
              :show-file-list="false"
              :on-success="handleLogoSuccess"
              :before-upload="beforeLogoUpload"
            >
              <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <el-input v-model="basicForm.logoUrl" placeholder="请输入Logo URL" class="ml-10" />
          </div>
          <div class="form-tip">建议上传正方形图片，大小不超过2MB</div>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="setting-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>费率设置</h3>
          <el-button type="primary" @click="handleRateSave">保存设置</el-button>
        </div>
      </template>
      <el-form ref="rateFormRef" :model="rateForm" label-width="120px">
        <el-form-item label="水费费率(元/吨)">
          <el-input-number v-model="rateForm.waterRate" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="电费费率(元/度)">
          <el-input-number v-model="rateForm.electricityRate" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="燃气费费率(元/立方)">
          <el-input-number v-model="rateForm.gasRate" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="押金系数(月租金倍数)">
          <el-input-number v-model="rateForm.depositRate" :precision="1" :step="0.5" :min="0" />
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="setting-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>通知设置</h3>
          <el-button type="primary" @click="handleNotificationSave">保存设置</el-button>
        </div>
      </template>
      <el-form ref="notificationFormRef" :model="notificationForm" label-width="180px">
        <el-form-item label="启用系统通知">
          <el-switch v-model="notificationForm.enabled" />
        </el-form-item>
        <el-form-item label="账单到期提前提醒(天)">
          <el-input-number v-model="notificationForm.billDueReminder" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="合同到期提前提醒(天)">
          <el-input-number v-model="notificationForm.contractExpiryReminder" :min="1" :max="30" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

// 基本设置表单
const basicFormRef = ref(null);
const basicForm = ref({
  siteName: '公寓管理系统',
  siteDescription: '专业的公寓管理系统',
  logoUrl: '/static/images/logo.png'
});

// 费率设置表单
const rateFormRef = ref(null);
const rateForm = ref({
  waterRate: 5.00,
  electricityRate: 0.50,
  gasRate: 3.00,
  depositRate: 1.0
});

// 通知设置表单
const notificationFormRef = ref(null);
const notificationForm = ref({
  enabled: true,
  billDueReminder: 3,
  contractExpiryReminder: 15
});

// 组件挂载时执行
onMounted(async () => {
  // TODO: 加载系统设置
  await fetchSystemSettings();
});

// 获取系统设置
const fetchSystemSettings = async () => {
  try {
    // TODO: 调用API获取系统设置
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 使用模拟数据
    console.log('已加载系统设置');
  } catch (error) {
    console.error('获取系统设置失败:', error);
    ElMessage.error('获取系统设置失败，请稍后重试');
  }
};

// 保存基本设置
const handleBasicSave = async () => {
  try {
    // TODO: 调用API保存基本设置
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    ElMessage.success('基本设置保存成功');
  } catch (error) {
    console.error('保存基本设置失败:', error);
    ElMessage.error('保存基本设置失败，请稍后重试');
  }
};

// 保存费率设置
const handleRateSave = async () => {
  try {
    // TODO: 调用API保存费率设置
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    ElMessage.success('费率设置保存成功');
  } catch (error) {
    console.error('保存费率设置失败:', error);
    ElMessage.error('保存费率设置失败，请稍后重试');
  }
};

// 保存通知设置
const handleNotificationSave = async () => {
  try {
    // TODO: 调用API保存通知设置
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    ElMessage.success('通知设置保存成功');
  } catch (error) {
    console.error('保存通知设置失败:', error);
    ElMessage.error('保存通知设置失败，请稍后重试');
  }
};

const handleLogoSuccess = (res) => {
  if (res.code === 200) {
    basicForm.logoUrl = res.data;
    ElMessage.success('Logo上传成功');
  } else {
    ElMessage.error(res.message || 'Logo上传失败');
  }
};

const beforeLogoUpload = (file) => {
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
</script>

<style lang="scss" scoped>
.setting-container {
  padding: 20px;
  
  .page-title {
    margin-bottom: 20px;
    font-size: 24px;
    font-weight: 500;
  }
  
  .setting-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
      }
    }
  }
}

.logo-upload {
  display: flex;
  align-items: center;

  .avatar-uploader {
    .avatar {
      width: 100px;
      height: 100px;
      display: block;
      object-fit: contain;
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 100px;
      height: 100px;
      text-align: center;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409EFF;
      }
    }
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style> 