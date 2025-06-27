<template>
  <div class="landlord-verification-info">
    <div class="verification-container">
      <div class="page-header">
        <h1 class="page-title">房东认证信息</h1>
        <p class="page-desc">查看您的认证状态和个人信息</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 认证信息显示 -->
      <div v-else-if="landlordInfo" class="info-container">
        <!-- 认证状态卡片 -->
        <el-card class="status-card" shadow="hover">
          <div class="status-content">
            <div class="status-icon" :class="getStatusClass(landlordInfo.verified)">
              <el-icon>
                <component :is="getStatusIcon(landlordInfo.verified)" />
              </el-icon>
            </div>
            <div class="status-info">
              <div class="status-title">{{ getStatusTitle(landlordInfo.verified) }}</div>
              <div class="status-desc">{{ getStatusDesc(landlordInfo.verified) }}</div>
              <div class="rating-info" v-if="landlordInfo.verified && landlordInfo.rating">
                <el-rate
                  v-model="landlordInfo.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                />
              </div>
            </div>
            <div class="status-time">
              <div>注册时间：{{ formatTime(landlordInfo.createdAt) }}</div>
              <div v-if="landlordInfo.updatedAt">
                更新时间：{{ formatTime(landlordInfo.updatedAt) }}
              </div>
            </div>
          </div>
        </el-card>

        <!-- 个人信息卡片 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">个人信息</span>
              <el-button type="primary" size="small" @click="handleEdit" v-if="landlordInfo.verified">
                编辑信息
              </el-button>
            </div>
          </template>

          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">真实姓名</div>
              <div class="info-value">{{ landlordInfo.realName || '未填写' }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">身份证号</div>
              <div class="info-value">{{ maskIdCard(landlordInfo.idCard) || '未填写' }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">银行卡号</div>
              <div class="info-value">{{ maskBankCard(landlordInfo.bankCard) || '未填写' }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">银行名称</div>
              <div class="info-value">{{ landlordInfo.bankName || '未填写' }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">联系电话</div>
              <div class="info-value">{{ maskPhone(userInfo.phone) || '未填写' }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">联系邮箱</div>
              <div class="info-value">{{ userInfo.email || '未填写' }}</div>
            </div>
          </div>
        </el-card>

        <!-- 身份证照片卡片 -->
        <el-card class="images-card" shadow="hover" v-if="landlordInfo.verified">
          <template #header>
            <div class="card-header">
              <span class="header-title">身份证照片</span>
            </div>
          </template>

          <div class="images-grid">
            <div class="image-item">
              <div class="image-label">身份证正面</div>
              <div class="image-container">
                <img 
                  v-if="landlordInfo.idCardFrontUrl" 
                  :src="landlordInfo.idCardFrontUrl" 
                  class="id-card-image"
                  @click="previewImage(landlordInfo.idCardFrontUrl)"
                />
                <div v-else class="no-image">暂无图片</div>
              </div>
            </div>
            
            <div class="image-item">
              <div class="image-label">身份证背面</div>
              <div class="image-container">
                <img 
                  v-if="landlordInfo.idCardBackUrl" 
                  :src="landlordInfo.idCardBackUrl" 
                  class="id-card-image"
                  @click="previewImage(landlordInfo.idCardBackUrl)"
                />
                <div v-else class="no-image">暂无图片</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 统计信息卡片 -->
        <el-card class="stats-card" shadow="hover" v-if="landlordInfo.verified">
          <template #header>
            <div class="card-header">
              <span class="header-title">经营统计</span>
            </div>
          </template>

          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalProperties || 0 }}</div>
              <div class="stat-label">房产数量</div>
            </div>
            
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalRooms || 0 }}</div>
              <div class="stat-label">房间数量</div>
            </div>
            
            <div class="stat-item">
              <div class="stat-number">{{ stats.rentedRooms || 0 }}</div>
              <div class="stat-label">已出租</div>
            </div>
            
            <div class="stat-item">
              <div class="stat-number">{{ stats.vacantRooms || 0 }}</div>
              <div class="stat-label">空置中</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 未认证提示 -->
      <div v-else class="no-data-container">
        <el-empty description="暂无认证信息">
          <el-button type="primary" @click="goToApplication">
            申请房东认证
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="50%" center>
      <div class="preview-container">
        <img :src="previewImageUrl" class="preview-image" />
      </div>
    </el-dialog>

    <!-- 编辑信息对话框 -->
    <el-dialog v-model="editVisible" title="编辑个人信息" width="600px">
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="银行卡号" prop="bankCard">
          <el-input v-model="editForm.bankCard" placeholder="请输入银行卡号" />
        </el-form-item>
        
        <el-form-item label="银行名称" prop="bankName">
          <el-select v-model="editForm.bankName" placeholder="请选择银行" style="width: 100%">
            <el-option
              v-for="bank in bankList"
              :key="bank"
              :label="bank"
              :value="bank"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible = false">取消</el-button>
          <el-button type="primary" :loading="updating" @click="handleUpdate">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  CircleCheck, Warning, Clock 
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { getCurrentLandlordInfo, updateCurrentLandlordInfo, getCurrentLandlordStats } from '@/api/landlords';

const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const loading = ref(true);
const updating = ref(false);
const landlordInfo = ref(null);
const userInfo = ref({});
const stats = ref({});
const previewVisible = ref(false);
const previewImageUrl = ref('');
const editVisible = ref(false);
const editFormRef = ref();

// 编辑表单
const editForm = ref({
  bankCard: '',
  bankName: ''
});

// 银行列表
const bankList = [
  '中国工商银行', '中国农业银行', '中国银行', '中国建设银行',
  '交通银行', '招商银行', '浦发银行', '中信银行',
  '中国光大银行', '华夏银行', '中国民生银行', '广发银行',
  '平安银行', '兴业银行', '浙商银行', '恒丰银行'
];

// 编辑表单验证规则
const editRules = {
  bankCard: [
    { required: true, message: '请输入银行卡号', trigger: 'blur' },
    { pattern: /^\d{16,19}$/, message: '请输入正确的银行卡号', trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请选择银行', trigger: 'change' }
  ]
};

// 获取房东信息
const fetchLandlordInfo = async () => {
  try {
    loading.value = true;
    const response = await getCurrentLandlordInfo();
    if (response && response.code === 200) {
      landlordInfo.value = response.data;
      userInfo.value = userStore.userInfo || {};
      
      // 获取统计信息
      if (landlordInfo.value.verified) {
        await fetchStats();
      }
    }
  } catch (error) {
    console.error('获取房东信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取统计信息
const fetchStats = async () => {
  try {
    const response = await getCurrentLandlordStats();
    if (response && response.code === 200) {
      stats.value = response.data;
    }
  } catch (error) {
    console.error('获取统计信息失败:', error);
  }
};

// 获取状态图标
const getStatusIcon = (verified) => {
  return verified ? CircleCheck : Clock;
};

// 获取状态样式类
const getStatusClass = (verified) => {
  return verified ? 'approved' : 'pending';
};

// 获取状态标题
const getStatusTitle = (verified) => {
  return verified ? '认证通过' : '待认证';
};

// 获取状态描述
const getStatusDesc = (verified) => {
  return verified ? '您已通过房东认证，可以正常使用所有功能' : '请联系管理员完成认证';
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 脱敏身份证号
const maskIdCard = (idCard) => {
  if (!idCard) return '';
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
};

// 脱敏银行卡号
const maskBankCard = (bankCard) => {
  if (!bankCard) return '';
  return bankCard.replace(/(\d{4})\d+(\d{4})/, '$1****$2');
};

// 脱敏手机号
const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
  previewVisible.value = true;
};

// 编辑信息
const handleEdit = () => {
  editForm.value = {
    bankCard: landlordInfo.value.bankCard || '',
    bankName: landlordInfo.value.bankName || ''
  };
  editVisible.value = true;
};

// 更新信息
const handleUpdate = async () => {
  if (!editFormRef.value) return;
  
  try {
    await editFormRef.value.validate();
    
    updating.value = true;
    
    const response = await updateCurrentLandlordInfo(editForm.value);
    
    if (response && response.code === 200) {
      ElMessage.success('信息更新成功');
      editVisible.value = false;
      await fetchLandlordInfo();
    } else {
      throw new Error(response?.message || '更新失败');
    }
  } catch (error) {
    console.error('更新信息失败:', error);
    ElMessage.error('更新失败：' + (error.message || '请稍后重试'));
  } finally {
    updating.value = false;
  }
};

// 跳转到申请页面
const goToApplication = () => {
  router.push('/users/landlord');
};

// 组件挂载时获取数据
onMounted(() => {
  fetchLandlordInfo();
});
</script>

<style lang="scss" scoped>
.landlord-verification-info {
  .verification-container {
    padding: 20px;
    max-width: 1200px;
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

  .loading-container {
    padding: 40px;
  }

  .info-container {
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
            margin-bottom: 12px;
          }

          .rating-info {
            margin-top: 8px;
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
    }

    .info-card {
      margin-bottom: 30px;

      .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 20px;

        .info-item {
          display: flex;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          .info-label {
            width: 100px;
            font-weight: 500;
            color: #666;
          }

          .info-value {
            flex: 1;
            color: #333;
          }
        }
      }
    }

    .images-card {
      margin-bottom: 30px;

      .images-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 30px;

        .image-item {
          text-align: center;

          .image-label {
            font-weight: 500;
            margin-bottom: 12px;
            color: #666;
          }

          .image-container {
            .id-card-image {
              width: 200px;
              height: 120px;
              object-fit: cover;
              border-radius: 6px;
              border: 1px solid #dcdfe6;
              cursor: pointer;
              transition: transform 0.3s;

              &:hover {
                transform: scale(1.05);
              }
            }

            .no-image {
              width: 200px;
              height: 120px;
              border: 2px dashed #dcdfe6;
              border-radius: 6px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: #999;
              font-size: 14px;
            }
          }
        }
      }
    }

    .stats-card {
      .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 20px;

        .stat-item {
          text-align: center;
          padding: 20px;
          background: #f8f9fa;
          border-radius: 8px;

          .stat-number {
            font-size: 32px;
            font-weight: 700;
            color: #409eff;
            margin-bottom: 8px;
          }

          .stat-label {
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }

  .no-data-container {
    padding: 60px 20px;
    text-align: center;
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

  .preview-container {
    text-align: center;

    .preview-image {
      max-width: 100%;
      max-height: 500px;
      border-radius: 8px;
    }
  }
}
</style> 