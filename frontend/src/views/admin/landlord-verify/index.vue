<template>
  <div class="landlord-verify">
    <h2 class="page-title">房东认证审核</h2>
    
    <div class="filter-container">
      <el-select v-model="selectedStatus" placeholder="审核状态" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已拒绝" value="REJECTED" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        @change="handleSearch"
      />
      <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
      <el-button type="info" icon="Refresh" @click="handleRefresh">刷新</el-button>
    </div>
    
    <el-table
      :data="applicationList"
      border
      style="width: 100%"
      v-loading="loading"
      table-layout="auto"
      :header-cell-style="{textAlign: 'center'}"
      :cell-style="{textAlign: 'center'}"
    >
      <el-table-column type="index" width="60" align="center" fixed />
      <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="realName" label="真实姓名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="idCard" label="身份证号" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          {{ maskIdCard(scope.row.idCard) }}
        </template>
      </el-table-column>
      <el-table-column prop="contactPhone" label="联系电话" min-width="130" show-overflow-tooltip />
      <el-table-column label="审核状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" min-width="180" show-overflow-tooltip />
      <el-table-column prop="reviewTime" label="审核时间" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看详情</el-button>
          <el-button 
            v-if="scope.row.status === 'PENDING'"
            type="success" 
            size="small" 
            @click="handleApprove(scope.row)"
          >
            通过
          </el-button>
          <el-button 
            v-if="scope.row.status === 'PENDING'"
            type="danger" 
            size="small" 
            @click="handleReject(scope.row)"
          >
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      title="房东认证详情"
      v-model="detailDialogVisible"
      width="800px"
    >
      <div v-if="currentApplication" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentApplication.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ currentApplication.realName }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentApplication.idCard }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusText(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentApplication.submitTime }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ currentApplication.reviewTime || '未审核' }}</el-descriptions-item>
          <el-descriptions-item label="审核意见">{{ currentApplication.reviewComment || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="images-section" v-if="currentApplication.idCardFrontUrl || currentApplication.idCardBackUrl">
          <h4>身份证照片</h4>
          <div class="images-container">
            <div v-if="currentApplication.idCardFrontUrl" class="image-item">
              <p>身份证正面</p>
              <el-image
                :src="currentApplication.idCardFrontUrl"
                :preview-src-list="[currentApplication.idCardFrontUrl]"
                fit="cover"
                style="width: 200px; height: 120px;"
              />
            </div>
            <div v-if="currentApplication.idCardBackUrl" class="image-item">
              <p>身份证反面</p>
              <el-image
                :src="currentApplication.idCardBackUrl"
                :preview-src-list="[currentApplication.idCardBackUrl]"
                fit="cover"
                style="width: 200px; height: 120px;"
              />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentApplication && currentApplication.status === 'PENDING'"
            type="success" 
            @click="handleApprove(currentApplication)"
          >
            通过审核
          </el-button>
          <el-button 
            v-if="currentApplication && currentApplication.status === 'PENDING'"
            type="danger" 
            @click="handleReject(currentApplication)"
          >
            拒绝审核
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      :title="auditTitle"
      v-model="auditDialogVisible"
      width="500px"
    >
      <el-form
        ref="auditFormRef"
        :model="auditForm"
        :rules="auditRules"
        label-width="80px"
      >
        <el-form-item label="审核结果">
          <el-tag :type="auditForm.action === 'APPROVE' ? 'success' : 'danger'">
            {{ auditForm.action === 'APPROVE' ? '通过' : '拒绝' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="auditForm.action === 'APPROVE' ? '请输入通过理由（可选）' : '请输入拒绝理由'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAudit" :loading="auditLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { getLandlordVerifyList, verifyLandlordApplication } from '@/api/admin';

// 搜索和筛选条件
const selectedStatus = ref('');
const dateRange = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载状态
const loading = ref(false);
const auditLoading = ref(false);

// 认证申请列表数据
const applicationList = ref([]);

// 详情对话框相关
const detailDialogVisible = ref(false);
const currentApplication = ref(null);

// 审核对话框相关
const auditDialogVisible = ref(false);
const auditTitle = ref('');
const auditFormRef = ref(null);
const auditForm = ref({
  action: '',
  comment: ''
});

// 审核表单验证规则
const auditRules = {
  comment: [
    { 
      validator: (rule, value, callback) => {
        if (auditForm.value.action === 'REJECT' && (!value || value.trim() === '')) {
          callback(new Error('拒绝审核时必须填写拒绝理由'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ]
};

// 组件挂载时执行
onMounted(() => {
  fetchApplicationList();
});

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '待审核';
    case 'APPROVED':
      return '已通过';
    case 'REJECTED':
      return '已拒绝';
    default:
      return '未知';
  }
};

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'APPROVED':
      return 'success';
    case 'REJECTED':
      return 'danger';
    default:
      return '';
  }
};

// 身份证号脱敏
const maskIdCard = (idCard) => {
  if (!idCard) return '';
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
};

// 获取认证申请列表数据
const fetchApplicationList = async () => {
  loading.value = true;
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };
    
    if (selectedStatus.value) {
      params.status = selectedStatus.value;
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    
    const response = await getLandlordVerifyList(params);
    
    if (response && response.code === 200) {
      if (response.data) {
        if (response.data.content) {
          // 分页对象格式
          applicationList.value = response.data.content;
          total.value = response.data.pageable?.total || response.data.totalElements || 0;
        } else if (Array.isArray(response.data)) {
          // 直接返回数组格式
          applicationList.value = response.data;
          total.value = response.data.length;
        } else {
          applicationList.value = [];
          total.value = 0;
        }
      } else {
        applicationList.value = [];
        total.value = 0;
      }
    } else {
      ElMessage.error(response?.message || '获取认证申请列表失败');
    }
  } catch (error) {
    console.error('获取认证申请列表异常:', error);
    ElMessage.error('获取认证申请列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchApplicationList();
};

// 刷新处理
const handleRefresh = () => {
  selectedStatus.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  fetchApplicationList();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchApplicationList();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchApplicationList();
};

// 查看详情
const handleViewDetail = (application) => {
  currentApplication.value = application;
  detailDialogVisible.value = true;
};

// 通过审核
const handleApprove = (application) => {
  currentApplication.value = application;
  auditForm.value = {
    action: 'APPROVE',
    comment: ''
  };
  auditTitle.value = '通过房东认证';
  auditDialogVisible.value = true;
  detailDialogVisible.value = false;
};

// 拒绝审核
const handleReject = (application) => {
  currentApplication.value = application;
  auditForm.value = {
    action: 'REJECT',
    comment: ''
  };
  auditTitle.value = '拒绝房东认证';
  auditDialogVisible.value = true;
  detailDialogVisible.value = false;
};

// 提交审核
const handleSubmitAudit = async () => {
  if (!auditFormRef.value) return;
  
  try {
    await auditFormRef.value.validate();
    auditLoading.value = true;
    
    const response = await verifyLandlordApplication(currentApplication.value.applicationId, {
      action: auditForm.value.action,
      comment: auditForm.value.comment
    });
    
    if (response && response.code === 200) {
      ElMessage.success('审核操作成功');
      auditDialogVisible.value = false;
      fetchApplicationList();
    } else {
      ElMessage.error(response?.message || '审核操作失败');
    }
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      console.error('审核操作失败:', error);
      ElMessage.error('审核操作失败，请稍后重试');
    }
  } finally {
    auditLoading.value = false;
  }
};
</script>

<style scoped>
.landlord-verify {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.filter-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.detail-content {
  padding: 10px 0;
}

.images-section {
  margin-top: 20px;
}

.images-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.images-container {
  display: flex;
  gap: 20px;
}

.image-item {
  text-align: center;
}

.image-item p {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 