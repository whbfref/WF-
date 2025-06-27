<template>
  <div class="application-detail">
    <div class="detail-container">
      <div class="page-header">
        <el-button @click="goBack" type="text" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h1 class="page-title">租房申请详情</h1>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>

      <div v-else-if="application" class="application-content">
        <!-- 申请基本信息 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">申请信息</span>
              <el-tag :type="getStatusTagType(application.status)" size="large">
                {{ getStatusText(application.status) }}
              </el-tag>
            </div>
          </template>

          <div class="application-info">
            <div class="info-row">
              <div class="info-item">
                <label>申请人：</label>
                <span>{{ application.username }}</span>
              </div>
              <div class="info-item">
                <label>联系电话：</label>
                <span>{{ application.userPhone }}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <label>期望入住时间：</label>
                <span>{{ formatDate(application.expectedMoveInDate) }}</span>
              </div>
              <div class="info-item">
                <label>租期长度：</label>
                <span>{{ application.leaseDuration }}个月</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <label>申请时间：</label>
                <span>{{ formatDateTime(application.createTime) }}</span>
              </div>
              <div class="info-item" v-if="application.reviewTime">
                <label>审核时间：</label>
                <span>{{ formatDateTime(application.reviewTime) }}</span>
              </div>
            </div>
            
            <div class="info-row" v-if="application.remarks">
              <div class="info-item full-width">
                <label>申请备注：</label>
                <p class="remarks">{{ application.remarks }}</p>
              </div>
            </div>
            
            <div class="info-row" v-if="application.reviewRemarks">
              <div class="info-item full-width">
                <label>审核备注：</label>
                <p class="remarks">{{ application.reviewRemarks }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 房间信息 -->
        <el-card class="room-card" shadow="hover">
          <template #header>
            <span class="header-title">房间信息</span>
          </template>

          <div class="room-info">
            <div class="room-image" v-if="application.roomImageUrl">
              <el-image 
                :src="application.roomImageUrl" 
                fit="cover"
                :preview-src-list="[application.roomImageUrl]"
              />
            </div>
            
            <div class="room-details">
              <h3>{{ application.roomTitle }}</h3>
              <div class="room-meta">
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ application.roomAddress }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Money /></el-icon>
                  <span>¥{{ application.monthlyRent }}/月</span>
                </div>
                <div class="meta-item" v-if="application.roomArea">
                  <el-icon><House /></el-icon>
                  <span>{{ application.roomArea }}㎡</span>
                </div>
              </div>
              <p class="room-description" v-if="application.roomDescription">
                {{ application.roomDescription }}
              </p>
            </div>
          </div>
        </el-card>

        <!-- 审核操作 -->
        <el-card v-if="application.status === 'PENDING'" class="action-card" shadow="hover">
          <template #header>
            <span class="header-title">审核操作</span>
          </template>

          <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="100px">
            <el-form-item label="审核结果" prop="status">
              <el-radio-group v-model="reviewForm.status">
                <el-radio label="APPROVED">通过</el-radio>
                <el-radio label="REJECTED">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="审核备注" prop="reviewRemarks">
              <el-input
                v-model="reviewForm.reviewRemarks"
                type="textarea"
                :rows="3"
                placeholder="请输入审核备注（可选）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmitReview">
                提交审核
              </el-button>
              <el-button @click="resetReviewForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <el-empty v-else description="申请不存在或已被删除" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  ArrowLeft, Location, Money, House 
} from '@element-plus/icons-vue';
import { 
  getRoomApplicationById, 
  reviewRoomApplication 
} from '@/api/applications';

const route = useRoute();
const router = useRouter();

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const application = ref(null);

// 审核表单
const reviewFormRef = ref();
const reviewForm = ref({
  status: '',
  reviewRemarks: ''
});

// 审核表单验证规则
const reviewRules = {
  status: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ]
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString();
};

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '';
  return new Date(datetime).toLocaleString();
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'APPROVED':
      return 'success';
    case 'REJECTED':
      return 'danger';
    case 'CANCELLED':
      return 'info';
    default:
      return '';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '待审核';
    case 'APPROVED':
      return '已通过';
    case 'REJECTED':
      return '已拒绝';
    case 'CANCELLED':
      return '已取消';
    default:
      return '未知';
  }
};

// 获取申请详情
const fetchApplicationDetail = async () => {
  loading.value = true;
  
  try {
    const applicationId = route.params.id;
    const response = await getRoomApplicationById(applicationId);
    
    if (response && response.code === 200) {
      application.value = response.data;
    } else {
      ElMessage.error(response?.message || '获取申请详情失败');
    }
  } catch (error) {
    console.error('获取申请详情失败:', error);
    ElMessage.error('获取申请详情失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 提交审核
const handleSubmitReview = async () => {
  try {
    await reviewFormRef.value.validate();
    
    await ElMessageBox.confirm(
      `确认${reviewForm.value.status === 'APPROVED' ? '通过' : '拒绝'}该申请吗？`,
      '确认审核',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    submitting.value = true;
    
    const response = await reviewRoomApplication(application.value.id, {
      status: reviewForm.value.status,
      reviewRemarks: reviewForm.value.reviewRemarks
    });
    
    if (response && response.code === 200) {
      ElMessage.success('审核成功');
      fetchApplicationDetail(); // 刷新数据
    } else {
      ElMessage.error(response?.message || '审核失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error);
      ElMessage.error('审核失败，请稍后重试');
    }
  } finally {
    submitting.value = false;
  }
};

// 重置审核表单
const resetReviewForm = () => {
  reviewForm.value = {
    status: '',
    reviewRemarks: ''
  };
};

// 组件挂载时执行
onMounted(() => {
  fetchApplicationDetail();
});
</script>

<style lang="scss" scoped>
.application-detail {
  .detail-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
  }

  .page-header {
    display: flex;
    align-items: center;
    margin-bottom: 30px;

    .back-btn {
      margin-right: 15px;
      font-size: 16px;
    }

    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0;
    }
  }

  .loading-container {
    padding: 40px;
  }

  .application-content {
    .info-card,
    .room-card,
    .action-card {
      margin-bottom: 20px;

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

    .application-info {
      .info-row {
        display: flex;
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-item {
          flex: 1;
          display: flex;
          align-items: flex-start;

          &.full-width {
            flex: none;
            width: 100%;
            flex-direction: column;
          }

          label {
            font-weight: 600;
            color: #606266;
            margin-right: 10px;
            min-width: 100px;
          }

          span {
            color: #303133;
          }

          .remarks {
            margin: 5px 0 0 0;
            padding: 10px;
            background-color: #f5f7fa;
            border-radius: 4px;
            color: #606266;
            line-height: 1.6;
          }
        }
      }
    }

    .room-info {
      display: flex;
      gap: 20px;

      .room-image {
        flex-shrink: 0;
        width: 200px;
        height: 150px;

        .el-image {
          width: 100%;
          height: 100%;
          border-radius: 8px;
          overflow: hidden;
        }
      }

      .room-details {
        flex: 1;

        h3 {
          margin: 0 0 15px 0;
          font-size: 20px;
          color: #2c3e50;
        }

        .room-meta {
          margin-bottom: 15px;

          .meta-item {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            color: #606266;

            .el-icon {
              margin-right: 8px;
              color: #909399;
            }
          }
        }

        .room-description {
          color: #606266;
          line-height: 1.6;
          margin: 0;
        }
      }
    }
  }
}
</style> 