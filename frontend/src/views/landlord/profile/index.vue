<template>
  <div class="landlord-profile">
    <div class="profile-container">
      <div class="page-header">
        <h1 class="page-title">个人信息</h1>
        <p class="page-desc">管理您的房东账户信息</p>
      </div>

      <div class="profile-content">
        <!-- 基本信息卡片 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">基本信息</span>
              <el-button type="primary" text @click="editMode = !editMode">
                {{ editMode ? '取消编辑' : '编辑信息' }}
              </el-button>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            class="profile-form"
            :disabled="!editMode"
          >
            <div class="form-row">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" type="email" />
              </el-form-item>
              <el-form-item label="银行名称" prop="bankName">
                <el-select v-model="form.bankName" placeholder="请选择银行" style="width: 100%">
                  <el-option
                    v-for="bank in bankList"
                    :key="bank"
                    :label="bank"
                    :value="bank"
                  />
                </el-select>
              </el-form-item>
            </div>

            <el-form-item label="银行卡号" prop="bankCard">
              <el-input 
                v-model="form.bankCard" 
                placeholder="请输入银行卡号"
                :formatter="formatBankCard"
                :parser="parseBankCard"
              />
            </el-form-item>

            <el-form-item v-if="editMode">
              <el-button
                type="primary"
                size="large"
                :loading="updating"
                @click="handleUpdate"
                class="update-btn"
              >
                保存修改
              </el-button>
              <el-button size="large" @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 认证状态卡片 -->
        <el-card class="status-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">认证状态</span>
              <el-button 
                v-if="!landlordInfo?.verified" 
                type="primary" 
                @click="router.push('/landlord/verification')"
              >
                申请认证
              </el-button>
            </div>
          </template>

          <div class="status-content">
            <div class="status-item">
              <div class="status-icon" :class="landlordInfo?.verified ? 'verified' : 'unverified'">
                <el-icon>
                  <component :is="landlordInfo?.verified ? CircleCheck : Warning" />
                </el-icon>
              </div>
              <div class="status-info">
                <div class="status-title">
                  {{ landlordInfo?.verified ? '已认证' : '未认证' }}
                </div>
                <div class="status-desc">
                  {{ landlordInfo?.verified ? '您已通过房东认证，可以发布房源' : '请完成房东认证后发布房源' }}
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 统计信息卡片 -->
        <el-card class="stats-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">房产统计</span>
            </div>
          </template>

          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon total">
                <el-icon><House /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ landlordInfo?.totalProperties || 0 }}</div>
                <div class="stat-label">总房产数</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon available">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ landlordInfo?.availableProperties || 0 }}</div>
                <div class="stat-label">可出租</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon rented">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ landlordInfo?.rentedProperties || 0 }}</div>
                <div class="stat-label">已出租</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon rating">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ landlordInfo?.rating || '0.0' }}</div>
                <div class="stat-label">评分</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 账户信息卡片 -->
        <el-card class="account-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">账户信息</span>
            </div>
          </template>

          <div class="account-info">
            <div class="info-item">
              <span class="info-label">用户ID：</span>
              <span class="info-value">{{ landlordInfo?.userId }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">房东ID：</span>
              <span class="info-value">{{ landlordInfo?.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">注册时间：</span>
              <span class="info-value">{{ formatTime(landlordInfo?.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最后更新：</span>
              <span class="info-value">{{ formatTime(landlordInfo?.updateTime) }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  House, Star, User, CircleCheck, Warning 
} from '@element-plus/icons-vue';
import { 
  getCurrentLandlordInfo, 
  updateCurrentLandlordInfo 
} from '@/api/landlords';

const router = useRouter();

// 响应式数据
const formRef = ref();
const landlordInfo = ref(null);
const editMode = ref(false);
const updating = ref(false);

// 表单数据
const form = ref({
  realName: '',
  contactPhone: '',
  contactEmail: '',
  bankCard: '',
  bankName: ''
});

// 原始数据备份
const originalForm = ref({});

// 银行列表
const bankList = [
  '中国工商银行',
  '中国农业银行',
  '中国银行',
  '中国建设银行',
  '交通银行',
  '招商银行',
  '浦发银行',
  '中信银行',
  '中国光大银行',
  '华夏银行',
  '中国民生银行',
  '广发银行',
  '平安银行',
  '兴业银行',
  '中国邮政储蓄银行'
];

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  bankCard: [
    { required: true, message: '请输入银行卡号', trigger: 'blur' },
    { pattern: /^\d{16,19}$/, message: '银行卡号格式不正确', trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请选择银行', trigger: 'change' }
  ]
};

// 格式化银行卡号
const formatBankCard = (value) => {
  if (!value) return '';
  return value.replace(/\s/g, '').replace(/(\d{4})(?=\d)/g, '$1 ');
};

// 解析银行卡号
const parseBankCard = (value) => {
  if (!value) return '';
  return value.replace(/\s/g, '');
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 更新信息
const handleUpdate = async () => {
  try {
    await formRef.value.validate();
    
    updating.value = true;
    
    const updateData = {
      contactPhone: form.value.contactPhone,
      contactEmail: form.value.contactEmail,
      bankCard: form.value.bankCard.replace(/\s/g, ''),
      bankName: form.value.bankName
    };

    const response = await updateCurrentLandlordInfo(updateData);
    if (response && response.code === 200) {
      ElMessage.success('信息更新成功');
      editMode.value = false;
      await fetchLandlordInfo();
    } else {
      ElMessage.error(response?.message || '更新失败');
    }
  } catch (error) {
    console.error('更新房东信息失败:', error);
    ElMessage.error('更新失败，请稍后重试');
  } finally {
    updating.value = false;
  }
};

// 取消编辑
const handleCancel = () => {
  editMode.value = false;
  // 恢复原始数据
  Object.assign(form.value, originalForm.value);
};

// 获取房东信息
const fetchLandlordInfo = async () => {
  try {
    const response = await getCurrentLandlordInfo();
    if (response && response.code === 200) {
      landlordInfo.value = response.data;
      
      // 填充表单数据
      form.value = {
        realName: response.data.realName || '',
        contactPhone: response.data.contactPhone || '',
        contactEmail: response.data.contactEmail || '',
        bankCard: response.data.bankCard || '',
        bankName: response.data.bankName || ''
      };
      
      // 备份原始数据
      originalForm.value = { ...form.value };
    }
  } catch (error) {
    console.error('获取房东信息失败:', error);
    ElMessage.error('获取信息失败，请刷新页面重试');
  }
};

// 组件挂载时执行
onMounted(() => {
  fetchLandlordInfo();
});
</script>

<style lang="scss" scoped>
.landlord-profile {
  .profile-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
  }

  .page-header {
    text-align: center;
    margin-bottom: 30px;

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 10px;
    }

    .page-desc {
      font-size: 16px;
      color: #7f8c8d;
      margin: 0;
    }
  }

  .profile-content {
    display: grid;
    gap: 30px;
  }

  .info-card {
    .profile-form {
      .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;

        @media (max-width: 768px) {
          grid-template-columns: 1fr;
        }
      }

      .update-btn {
        width: 150px;
        height: 40px;
        font-weight: 600;
      }
    }
  }

  .status-card {
    .status-content {
      .status-item {
        display: flex;
        align-items: center;
        padding: 20px;

        .status-icon {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          margin-right: 20px;

          &.verified {
            background: #d4edda;
            color: #155724;
          }

          &.unverified {
            background: #fff3cd;
            color: #856404;
          }
        }

        .status-info {
          .status-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 5px;
          }

          .status-desc {
            font-size: 14px;
            color: #7f8c8d;
          }
        }
      }
    }
  }

  .stats-card {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;

      @media (max-width: 768px) {
        grid-template-columns: repeat(2, 1fr);
      }

      .stat-item {
        display: flex;
        align-items: center;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 12px;

        .stat-icon {
          width: 50px;
          height: 50px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          margin-right: 15px;

          &.total {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
          }

          &.available {
            background: linear-gradient(135deg, #11998e, #38ef7d);
            color: white;
          }

          &.rented {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
          }

          &.rating {
            background: linear-gradient(135deg, #feca57, #ff9ff3);
            color: white;
          }
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1;
          }

          .stat-label {
            font-size: 12px;
            color: #7f8c8d;
            margin-top: 5px;
          }
        }
      }
    }
  }

  .account-card {
    .account-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          font-weight: 600;
          color: #606266;
        }

        .info-value {
          color: #2c3e50;
        }
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      font-size: 18px;
      font-weight: 600;
      color: #2c3e50;
    }
  }
}
</style> 