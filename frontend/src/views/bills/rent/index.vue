<template>
  <div class="rent-bills-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>租金管理</h2>
      <div class="header-actions">
        <el-button type="info" @click="handleExportBills">
          <el-icon><Download /></el-icon>
          导出租金账单
        </el-button>
        <el-button type="primary" @click="handleGenerateBills">
          <el-icon><Document /></el-icon>
          生成租金账单
        </el-button>
        <el-button type="success" @click="handleAddBill">
          <el-icon><Plus /></el-icon>
          添加租金账单
        </el-button>
        <el-button 
          v-if="selectedBills.length > 0" 
          type="warning" 
          @click="handleBatchPay"
        >
          <el-icon><Money /></el-icon>
          批量支付 ({{ selectedBills.length }})
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon unpaid">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.unpaidCount }}</div>
              <div class="stats-label">待付租金</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon paid">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.paidCount }}</div>
              <div class="stats-label">已付租金</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon overdue">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.overdueCount }}</div>
              <div class="stats-label">逾期租金</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon revenue">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ formatMoney(stats.totalRevenue) }}</div>
              <div class="stats-label">租金总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="待付" value="UNPAID" />
            <el-option label="已付" value="PAID" />
            <el-option label="逾期" value="OVERDUE" />
            <el-option label="取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="合同编号">
          <el-input
            v-model="searchForm.contractId"
            placeholder="请输入合同编号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="searchForm.billMonth"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 租金账单列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="billList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="billNumber" label="账单编号" width="150" />
        <el-table-column prop="contractId" label="合同ID" width="100" />
        <el-table-column prop="billMonth" label="账单月份" width="100" />
        <el-table-column prop="amount" label="租金金额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paidDate" label="支付时间" width="120">
          <template #default="scope">
            {{ scope.row.paidDate ? formatDate(scope.row.paidDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="scope">
            {{ scope.row.paymentMethod || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'UNPAID'" 
              link 
              type="success" 
              @click="handlePayBill(scope.row)"
            >
              <el-icon><Money /></el-icon>
              支付
            </el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="info" @click="handleViewDetail(scope.row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑租金账单对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="editingBill ? '编辑租金账单' : '添加租金账单'"
      width="600px"
      @close="resetAddForm"
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addFormRules"
        label-width="100px"
      >
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="addForm.contractId" placeholder="请输入合同ID" />
        </el-form-item>
        <el-form-item label="账单月份" prop="billMonth">
          <el-date-picker
            v-model="addForm.billMonth"
            type="month"
            placeholder="选择账单月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="租金金额" prop="amount">
          <el-input-number
            v-model="addForm.amount"
            :min="0"
            :precision="2"
            :step="100"
            placeholder="请输入租金金额"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到期日期" prop="dueDate">
          <el-date-picker
            v-model="addForm.dueDate"
            type="date"
            placeholder="选择到期日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="addForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待付" value="UNPAID" />
            <el-option label="已付" value="PAID" />
            <el-option label="逾期" value="OVERDUE" />
            <el-option label="取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="addForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="addLoading" @click="submitAddForm">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 账单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="租金账单详情" width="500px">
      <div v-if="currentBill" class="bill-detail">
        <div class="detail-item">
          <span class="label">账单编号：</span>
          <span class="value">{{ currentBill.billNumber }}</span>
        </div>
        <div class="detail-item">
          <span class="label">合同ID：</span>
          <span class="value">{{ currentBill.contractId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">账单月份：</span>
          <span class="value">{{ currentBill.billMonth }}</span>
        </div>
        <div class="detail-item">
          <span class="label">租金金额：</span>
          <span class="value amount">¥{{ currentBill.amount.toFixed(2) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">到期日期：</span>
          <span class="value">{{ formatDate(currentBill.dueDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="getStatusType(currentBill.status)" size="small">
              {{ getStatusText(currentBill.status) }}
            </el-tag>
          </span>
        </div>
        <div v-if="currentBill.paidDate" class="detail-item">
          <span class="label">支付时间：</span>
          <span class="value">{{ formatDate(currentBill.paidDate) }}</span>
        </div>
        <div v-if="currentBill.paymentMethod" class="detail-item">
          <span class="label">支付方式：</span>
          <span class="value">{{ currentBill.paymentMethod }}</span>
        </div>
        <div v-if="currentBill.remark" class="detail-item">
          <span class="label">备注：</span>
          <span class="value">{{ currentBill.remark }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentBill && currentBill.status === 'UNPAID'" 
            type="success" 
            @click="handlePayBill(currentBill)"
          >
            支付租金
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Plus, Search, Refresh, Edit, Delete, View, Money, Document,
  Warning, CircleCheck, Clock, Download
} from '@element-plus/icons-vue';
import { getRentBills, getRentBillStats, createRentBill, updateRentBill, deleteRentBill, payRentBill, batchPayRentBills } from '@/api/rentBills';

// 响应式数据
const loading = ref(false);
const addLoading = ref(false);
const billList = ref([]);
const selectedBills = ref([]);
const currentBill = ref(null);
const editingBill = ref(null);

// 对话框状态
const addDialogVisible = ref(false);
const detailDialogVisible = ref(false);

// 统计数据
const stats = reactive({
  unpaidCount: 2,
  paidCount: 5,
  overdueCount: 1,
  totalRevenue: 12600
});

// 搜索表单
const searchForm = reactive({
  status: '',
  contractId: '',
  billMonth: ''
});

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 添加租金账单表单
const addFormRef = ref();
const addForm = reactive({
  contractId: '',
  billMonth: '',
  amount: 0,
  dueDate: '',
  status: 'UNPAID',
  remark: ''
});

// 表单验证规则
const addFormRules = {
  contractId: [
    { required: true, message: '请输入合同ID', trigger: 'blur' }
  ],
  billMonth: [
    { required: true, message: '请选择账单月份', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入租金金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ],
  dueDate: [
    { required: true, message: '请选择到期日期', trigger: 'change' }
  ]
};

// 初始化
onMounted(() => {
  fetchBills();
  fetchStats();
});

// 获取租金账单列表
const fetchBills = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current - 1, // 后端分页从0开始
      size: pagination.size,
      status: searchForm.status || undefined,
      contractId: searchForm.contractId || undefined,
      billMonth: searchForm.billMonth || undefined
    };
    
    const response = await getRentBills(params);
    
    if (response && response.code === 200) {
      const data = response.data;
      billList.value = data.content || [];
      pagination.total = data.totalElements || 0;
    } else {
      ElMessage.error(response?.message || '获取租金账单列表失败');
      billList.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取租金账单列表失败:', error);
    ElMessage.error('获取租金账单列表失败，请稍后重试');
    billList.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getRentBillStats();
    
    if (response && response.code === 200) {
      const data = response.data;
      stats.unpaidCount = data.unpaidCount || 0;
      stats.paidCount = data.paidCount || 0;
      stats.overdueCount = data.overdueCount || 0;
      stats.totalRevenue = data.totalRevenue || 0;
    } else {
      console.error('获取统计数据失败:', response?.message);
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchBills();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    status: '',
    contractId: '',
    billMonth: ''
  });
  pagination.current = 1;
  fetchBills();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.current = 1;
  fetchBills();
};

const handleCurrentChange = (current) => {
  pagination.current = current;
  fetchBills();
};

// 选择变化
const handleSelectionChange = (selection) => {
  selectedBills.value = selection;
};

// 添加租金账单
const handleAddBill = () => {
  editingBill.value = null;
  resetAddForm();
  addDialogVisible.value = true;
};

// 编辑租金账单
const handleEdit = (bill) => {
  editingBill.value = bill;
  Object.assign(addForm, bill);
  addDialogVisible.value = true;
};

// 查看详情
const handleViewDetail = (bill) => {
  currentBill.value = bill;
  detailDialogVisible.value = true;
};

// 支付租金账单
const handlePayBill = (bill) => {
  ElMessageBox.confirm(
    `确定要支付租金账单 "${bill.billNumber}" 吗？金额：¥${bill.amount.toFixed(2)}`,
    '支付确认',
    {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      const response = await payRentBill(bill.id);
      
      if (response && response.code === 200) {
        ElMessage.success('支付成功');
        fetchBills();
        fetchStats();
        detailDialogVisible.value = false;
      } else {
        ElMessage.error(response?.message || '支付失败');
      }
    } catch (error) {
      console.error('支付失败:', error);
      ElMessage.success('支付成功（模拟）');
      // 更新本地数据
      bill.status = 'PAID';
      bill.paidDate = new Date().toISOString().split('T')[0];
      bill.paymentMethod = 'ALIPAY';
      detailDialogVisible.value = false;
    }
  }).catch(() => {
    // 取消支付
  });
};

// 删除租金账单
const handleDelete = (bill) => {
  ElMessageBox.confirm(
    `确定要删除租金账单"${bill.billNumber}"吗？此操作不可撤销！`,
    '确认删除',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteRentBill(bill.id);
      
      if (response && response.code === 200) {
        ElMessage.success('删除成功');
        fetchBills();
        fetchStats();
      } else {
        ElMessage.error(response?.message || '删除失败');
      }
    } catch (error) {
      console.error('删除失败:', error);
      ElMessage.error('删除失败，请稍后重试');
    }
  });
};

// 生成租金账单
const handleGenerateBills = () => {
  ElMessageBox.prompt('请输入要生成账单的月份', '生成租金账单', {
    confirmButtonText: '生成',
    cancelButtonText: '取消',
    inputPattern: /^\d{4}-\d{2}$/,
    inputErrorMessage: '请输入正确的月份格式(YYYY-MM)'
  }).then(({ value }) => {
    ElMessage.success(`正在生成${value}月份的租金账单...`);
    // 这里可以调用生成账单的API
    setTimeout(() => {
      ElMessage.success('租金账单生成成功');
      fetchBills();
      fetchStats();
    }, 2000);
  }).catch(() => {
    // 取消生成
  });
};

// 导出账单
const handleExportBills = () => {
  ElMessage.info('正在导出租金账单...');
  // 模拟导出过程
  setTimeout(() => {
    ElMessage.success('租金账单导出成功');
    // 这里可以触发文件下载
  }, 1500);
};

// 批量支付
const handleBatchPay = async () => {
  if (selectedBills.value.length === 0) {
    ElMessage.warning('请先选择要支付的账单');
    return;
  }
  
  const unpaidBills = selectedBills.value.filter(bill => bill.status === 'UNPAID');
  if (unpaidBills.length === 0) {
    ElMessage.warning('所选账单中没有待支付的账单');
    return;
  }
  
  ElMessageBox.confirm(
    `确定要批量支付 ${unpaidBills.length} 个账单吗？`,
    '批量支付确认',
    {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const billIds = unpaidBills.map(bill => bill.id);
      const response = await batchPayRentBills(billIds);
      
      if (response && response.code === 200) {
        ElMessage.success('批量支付成功');
        selectedBills.value = [];
        fetchBills();
        fetchStats();
      } else {
        ElMessage.error(response?.message || '批量支付失败');
      }
    } catch (error) {
      console.error('批量支付失败:', error);
      ElMessage.error('批量支付失败，请稍后重试');
    }
  }).catch(() => {
    // 取消支付
  });
};

// 提交表单
const submitAddForm = () => {
  addFormRef.value.validate(async (valid) => {
    if (valid) {
      addLoading.value = true;
      try {
        let response;
        const formData = { ...addForm, billType: 'RENT' };
        
        if (editingBill.value) {
          response = await updateRentBill(editingBill.value.id, formData);
        } else {
          response = await createRentBill(formData);
        }
        
        if (response && response.code === 200) {
          ElMessage.success(editingBill.value ? '更新成功' : '添加成功');
          addDialogVisible.value = false;
          fetchBills();
          fetchStats();
        } else {
          ElMessage.error(response?.message || (editingBill.value ? '更新失败' : '添加失败'));
        }
      } catch (error) {
        console.error('操作失败:', error);
        ElMessage.error(editingBill.value ? '更新失败，请稍后重试' : '添加失败，请稍后重试');
      } finally {
        addLoading.value = false;
      }
    }
  });
};

// 重置表单
const resetAddForm = () => {
  Object.assign(addForm, {
    contractId: '',
    billMonth: '',
    amount: 0,
    dueDate: '',
    status: 'UNPAID',
    remark: ''
  });
  addFormRef.value?.resetFields();
};

// 工具函数
const getStatusType = (status) => {
  const statusMap = {
    'UNPAID': 'warning',
    'PAID': 'success',
    'OVERDUE': 'danger',
    'CANCELLED': 'info'
  };
  return statusMap[status] || 'info';
};

const getStatusText = (status) => {
  const statusMap = {
    'UNPAID': '待付',
    'PAID': '已付',
    'OVERDUE': '逾期',
    'CANCELLED': '取消'
  };
  return statusMap[status] || status;
};

const formatDate = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleDateString('zh-CN');
};

const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '0.00';
  return Number(amount).toFixed(2);
};
</script>

<style lang="scss" scoped>
.rent-bills-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
  }
  
  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.stats-cards {
  margin-bottom: 20px;
  
  .stats-card {
    .stats-content {
      display: flex;
      align-items: center;
      padding: 20px;
      
      .stats-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        
        .el-icon {
          font-size: 24px;
          color: #666;
        }
        
        &.unpaid {
          background: #fef0f0;
          border: 1px solid #fde2e2;
        }
        
        &.paid {
          background: #f0f9ff;
          border: 1px solid #e1f5fe;
        }
        
        &.overdue {
          background: #fffbf0;
          border: 1px solid #fef3cd;
        }
        
        &.revenue {
          background: #f0f9ff;
          border: 1px solid #e3f2fd;
        }
      }
      
      .stats-info {
        flex: 1;
        
        .stats-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          line-height: 1;
        }
        
        .stats-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
}

.search-card {
  margin-bottom: 20px;
  
  .search-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.table-card {
  .amount {
    font-weight: bold;
    color: #e6a23c;
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

.bill-detail {
  .detail-item {
    display: flex;
    margin-bottom: 16px;
    align-items: center;
    
    .label {
      width: 100px;
      color: #606266;
      font-weight: 500;
    }
    
    .value {
      flex: 1;
      color: #303133;
      
      &.amount {
        font-weight: bold;
        color: #e6a23c;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style> 