<template>
  <div class="user-applications">
    <div class="page-header">
      <h2 class="page-title">租房申请</h2>
      <p class="page-desc">查看您的租房申请记录和状态</p>
    </div>

    <!-- 申请统计 -->
    <div class="application-stats">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon pending">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingCount }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon approved">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ approvedCount }}</div>
            <div class="stat-label">已通过</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon rejected">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ rejectedCount }}</div>
            <div class="stat-label">已拒绝</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCount }}</div>
            <div class="stat-label">总申请</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="$router.push('/user/browse-rooms')">
            <el-icon><Plus /></el-icon>
            新申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请列表 -->
    <el-card class="applications-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">申请列表</span>
        </div>
      </template>
      
      <div class="applications-content" v-loading="loading">
        <div v-if="applicationList.length > 0" class="application-items">
          <div 
            v-for="application in applicationList" 
            :key="application.id" 
            class="application-item"
          >
            <div class="application-image">
              <el-image
                :src="application.roomImageUrl || defaultRoomImage"
                fit="cover"
                class="room-image"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            
            <div class="application-info">
              <div class="info-header">
                <h3 class="room-title">{{ application.roomTitle }}</h3>
                <el-tag :type="getStatusType(application.status)" size="large">
                  {{ getStatusText(application.status) }}
                </el-tag>
              </div>
              
              <div class="room-details">
                <div class="detail-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ application.roomAddress }}</span>
                </div>
                <div class="detail-item">
                  <el-icon><Money /></el-icon>
                  <span>¥{{ application.monthlyRent }}/月</span>
                </div>
                <div class="detail-item">
                  <el-icon><Calendar /></el-icon>
                  <span>期望入住: {{ formatDate(application.moveInDate) }}</span>
                </div>
              </div>
              
              <div class="application-reason">
                <div class="reason-label">申请理由:</div>
                <div class="reason-text">{{ application.reason }}</div>
              </div>
              
              <div class="application-footer">
                <div class="application-time">
                  <span class="time-label">申请时间:</span>
                  <span class="time-value">{{ formatTime(application.createTime) }}</span>
                </div>
                
                <div class="application-actions">
                  <el-button size="small" @click="handleViewDetail(application)">
                    查看详情
                  </el-button>
                  <el-button 
                    v-if="application.status === 'PENDING'"
                    type="danger" 
                    size="small" 
                    @click="handleCancel(application)"
                  >
                    取消申请
                  </el-button>
                  <el-button 
                    v-if="application.status === 'APPROVED'"
                    type="success" 
                    size="small" 
                    @click="handleSign(application)"
                  >
                    签署合同
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <el-empty v-else description="暂无申请记录">
          <el-button type="primary" @click="$router.push('/user/browse-rooms')">
            去申请房源
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`申请详情 - ${selectedApplication?.roomTitle}`"
      width="700px"
    >
      <div v-if="selectedApplication" class="application-detail">
        <div class="detail-section">
          <h4>房源信息</h4>
          <div class="room-detail">
            <el-image
              :src="selectedApplication.roomImageUrl || defaultRoomImage"
              fit="cover"
              class="detail-image"
            />
            <div class="room-info">
              <div class="info-item">
                <span class="label">房源名称:</span>
                <span class="value">{{ selectedApplication.roomTitle }}</span>
              </div>
              <div class="info-item">
                <span class="label">房源地址:</span>
                <span class="value">{{ selectedApplication.roomAddress }}</span>
              </div>
              <div class="info-item">
                <span class="label">月租金:</span>
                <span class="value price">¥{{ selectedApplication.monthlyRent }}</span>
              </div>
              <div class="info-item">
                <span class="label">房东:</span>
                <span class="value">{{ selectedApplication.landlordName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话:</span>
                <span class="value">{{ selectedApplication.landlordPhone }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>申请信息</h4>
          <div class="application-info-detail">
            <div class="info-item">
              <span class="label">申请状态:</span>
              <el-tag :type="getStatusType(selectedApplication.status)">
                {{ getStatusText(selectedApplication.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">期望入住时间:</span>
              <span class="value">{{ formatDate(selectedApplication.moveInDate) }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话:</span>
              <span class="value">{{ selectedApplication.contactPhone }}</span>
            </div>
            <div class="info-item">
              <span class="label">申请时间:</span>
              <span class="value">{{ formatTime(selectedApplication.createTime) }}</span>
            </div>
          </div>
          
          <div class="reason-section">
            <div class="reason-label">申请理由:</div>
            <div class="reason-content">{{ selectedApplication.reason }}</div>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedApplication.rejectReason">
          <h4>拒绝原因</h4>
          <div class="reject-reason">{{ selectedApplication.rejectReason }}</div>
        </div>
        
        <div class="detail-section" v-if="selectedApplication.status === 'APPROVED'">
          <h4>审核通过</h4>
          <div class="approval-info">
            <p>恭喜！您的申请已通过审核，请尽快联系房东签署租房合同。</p>
            <div class="contact-info">
              <p><strong>房东联系方式:</strong> {{ selectedApplication.landlordPhone }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="selectedApplication?.status === 'PENDING'"
            type="danger" 
            @click="handleCancel(selectedApplication)"
          >
            取消申请
          </el-button>
          <el-button 
            v-if="selectedApplication?.status === 'APPROVED'"
            type="success" 
            @click="handleSign(selectedApplication)"
          >
            签署合同
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Clock, CircleCheck, CircleClose, Document, Search, Plus,
  Picture, Location, Money, Calendar
} from '@element-plus/icons-vue';
import { getRoomApplications, cancelRoomApplication } from '@/api/user';

// 默认房源图片
const defaultRoomImage = 'https://via.placeholder.com/300x200?text=房源图片';

// 响应式数据
const loading = ref(false);
const applicationList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索表单
const searchForm = reactive({
  status: '',
  dateRange: []
});

// 详情对话框
const detailDialogVisible = ref(false);
const selectedApplication = ref(null);

// 计算属性 - 统计数据
const pendingCount = computed(() => {
  return applicationList.value.filter(app => app.status === 'PENDING').length;
});

const approvedCount = computed(() => {
  return applicationList.value.filter(app => app.status === 'APPROVED').length;
});

const rejectedCount = computed(() => {
  return applicationList.value.filter(app => app.status === 'REJECTED').length;
});

const totalCount = computed(() => {
  return applicationList.value.length;
});

// 组件挂载时获取申请列表
onMounted(() => {
  fetchApplicationList();
});

// 获取申请列表
const fetchApplicationList = async () => {
  try {
    loading.value = true;
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    };
    
    // 处理日期范围
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0];
      params.endDate = searchForm.dateRange[1];
    }
    
    const response = await getRoomApplications(params);
    if (response.code === 200) {
      applicationList.value = response.data.content || [];
      total.value = response.data.totalElements || 0;
    }
  } catch (error) {
    console.error('获取申请列表失败:', error);
    ElMessage.error('获取申请列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchApplicationList();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    status: '',
    dateRange: []
  });
  handleSearch();
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchApplicationList();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchApplicationList();
};

// 查看详情
const handleViewDetail = (application) => {
  selectedApplication.value = application;
  detailDialogVisible.value = true;
};

// 取消申请
const handleCancel = async (application) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个申请吗？取消后无法恢复。',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 调用取消申请的API
    const response = await cancelRoomApplication(application.id);
    if (response.code === 200) {
      ElMessage.success('申请已取消');
      detailDialogVisible.value = false;
      fetchApplicationList();
    } else {
      ElMessage.error(response.message || '取消申请失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消申请失败:', error);
      ElMessage.error('取消申请失败');
    }
  }
};

// 签署合同
const handleSign = (application) => {
  ElMessageBox.alert(
    `请联系房东签署合同。\n房东联系方式: ${application.landlordPhone}`,
    '签署合同',
    {
      confirmButtonText: '我知道了',
      type: 'info'
    }
  );
};

// 获取状态类型
const getStatusType = (status) => {
  const statusTypes = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info'
  };
  return statusTypes[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'CANCELLED': '已取消'
  };
  return statusTexts[status] || '未知';
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleDateString();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-';
  return new Date(time).toLocaleString();
};
</script>

<style scoped>
.user-applications {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-desc {
  color: #909399;
  margin: 0;
}

.application-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: #e6a23c;
}

.stat-icon.approved {
  background: #67c23a;
}

.stat-icon.rejected {
  background: #f56c6c;
}

.stat-icon.total {
  background: #409eff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.applications-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.application-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-item {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
}

.application-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.application-image {
  height: 150px;
}

.room-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
  border-radius: 8px;
}

.application-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.room-details {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.application-reason {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
}

.reason-label {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.reason-text {
  color: #606266;
  line-height: 1.6;
}

.application-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.application-time {
  color: #909399;
  font-size: 14px;
}

.time-label {
  margin-right: 8px;
}

.time-value {
  font-weight: 500;
}

.application-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.application-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.room-detail {
  display: grid;
  grid-template-columns: 150px 1fr;
  gap: 16px;
}

.detail-image {
  width: 100%;
  height: 100px;
  border-radius: 6px;
}

.room-info,
.application-info-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  color: #909399;
  min-width: 80px;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}

.info-item .value.price {
  color: #f56c6c;
  font-size: 16px;
}

.reason-section {
  margin-top: 16px;
}

.reason-content {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
  margin-top: 8px;
}

.reject-reason {
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  color: #f56c6c;
  padding: 12px;
  border-radius: 6px;
  line-height: 1.6;
}

.approval-info {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  padding: 16px;
  border-radius: 8px;
}

.approval-info p {
  margin: 0 0 12px 0;
  color: #606266;
  line-height: 1.6;
}

.contact-info p {
  margin: 0;
  color: #303133;
}

@media (max-width: 768px) {
  .application-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .application-item {
    grid-template-columns: 1fr;
  }
  
  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .application-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .room-detail {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .application-stats {
    grid-template-columns: 1fr;
  }
}
</style> 