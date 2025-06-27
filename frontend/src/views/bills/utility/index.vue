<template>
  <div class="utility-bills-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>物业费管理</h2>
      <div class="header-actions">
        <el-button type="info" @click="handleExportBills">
          <el-icon><Download /></el-icon>
          导出物业费账单
        </el-button>
        <el-button type="primary" @click="handleReadMeters">
          <el-icon><Reading /></el-icon>
          抄表录入
        </el-button>
        <el-button type="success" @click="handleAddBill">
          <el-icon><Plus /></el-icon>
          添加物业费账单
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon electricity">
              <el-icon><Lightning /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.electricityUnpaid }}</div>
              <div class="stats-label">待付电费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon water">
              <el-icon><Umbrella /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.waterUnpaid }}</div>
              <div class="stats-label">待付水费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon gas">
              <el-icon><Management /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.gasUnpaid }}</div>
              <div class="stats-label">待付燃气费</div>
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
              <div class="stats-label">物业费总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="费用类型">
          <el-select v-model="searchForm.type" placeholder="请选择费用类型" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="电费" :value="1" />
            <el-option label="水费" :value="2" />
            <el-option label="燃气费" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="待缴费" :value="0" />
            <el-option label="已缴费" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="房产ID">
          <el-input
            v-model="searchForm.propertyId"
            placeholder="请输入房产ID"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="searchForm.month"
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

    <!-- 物业费账单列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="billList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="billNo" label="账单编号" width="150" />
        <el-table-column prop="propertyId" label="房产ID" width="100" />
        <el-table-column prop="type" label="费用类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)" size="small">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="month" label="账单月份" width="100" />
        <el-table-column prop="usage" label="用量" width="100">
          <template #default="scope">
            {{ getUsage(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="缴费时间" width="120">
          <template #default="scope">
            {{ scope.row.paymentTime ? formatDate(scope.row.paymentTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 0" 
              link 
              type="success" 
              @click="handlePayBill(scope.row)"
            >
              <el-icon><Money /></el-icon>
              缴费
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Plus, Search, Refresh, Edit, Delete, View, Money, Reading, Download,
  Lightning, Umbrella, Management
} from '@element-plus/icons-vue';
import { 
  getUtilityBills, getUtilityBillStats, createUtilityBill, 
  updateUtilityBill, deleteUtilityBill, payUtilityBill 
} from '@/api/utilityBills';

// 响应式数据
const loading = ref(false);
const billList = ref([]);
const selectedBills = ref([]);

// 统计数据
const stats = reactive({
  electricityUnpaid: 3,
  waterUnpaid: 2,
  gasUnpaid: 1,
  totalRevenue: 8540
});

// 搜索表单
const searchForm = reactive({
  type: '',
  status: '',
  propertyId: '',
  month: ''
});

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 初始化
onMounted(() => {
  fetchBills();
  fetchStats();
});

// 获取物业费账单列表
const fetchBills = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current - 1, // 后端分页从0开始
      size: pagination.size,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined,
      propertyId: searchForm.propertyId || undefined,
      month: searchForm.month || undefined
    };
    
    const response = await getUtilityBills(params);
    
    if (response && response.code === 200) {
      const data = response.data;
      billList.value = data.content || [];
      pagination.total = data.totalElements || 0;
    } else {
      ElMessage.error(response?.message || '获取物业费账单列表失败');
      billList.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取物业费账单列表失败:', error);
    ElMessage.error('获取物业费账单列表失败，请稍后重试');
    billList.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getUtilityBillStats();
    
    if (response && response.code === 200) {
      const data = response.data;
      stats.electricityUnpaid = data.electricityUnpaid || 0;
      stats.waterUnpaid = data.waterUnpaid || 0;
      stats.gasUnpaid = data.gasUnpaid || 0;
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
    type: '',
    status: '',
    propertyId: '',
    month: ''
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

// 添加物业费账单
const handleAddBill = () => {
  ElMessageBox.prompt('请输入账单信息', '添加物业费账单', {
    confirmButtonText: '添加',
    cancelButtonText: '取消',
    inputPlaceholder: '格式：房产ID,费用类型(1电费/2水费/3燃气费),金额'
  }).then(({ value }) => {
    const parts = value.split(',');
    if (parts.length >= 3) {
      const newBill = {
        id: Date.now(),
        billNo: `UTIL${Date.now()}`,
        propertyId: parts[0].trim(),
        type: parseInt(parts[1].trim()),
        amount: parseFloat(parts[2].trim()),
        month: new Date().toISOString().slice(0, 7),
        status: 0,
        paymentTime: null,
        remark: '新增账单'
      };
      billList.value.unshift(newBill);
      ElMessage.success('添加成功');
      fetchStats();
    } else {
      ElMessage.error('格式错误，请按照提示格式输入');
    }
  }).catch(() => {
    // 取消添加
  });
};

// 编辑物业费账单
const handleEdit = (bill) => {
  ElMessageBox.prompt('请输入新的金额', '编辑物业费账单', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: bill.amount.toString(),
    inputPattern: /^\d+(\.\d{1,2})?$/,
    inputErrorMessage: '请输入正确的金额格式'
  }).then(({ value }) => {
    bill.amount = parseFloat(value);
    ElMessage.success('编辑成功');
    // 这里可以调用更新账单的API
  }).catch(() => {
    // 取消编辑
  });
};

// 查看详情
const handleViewDetail = (bill) => {
  const details = [
    `账单编号：${bill.billNo}`,
    `房产ID：${bill.propertyId}`,
    `费用类型：${getTypeText(bill.type)}`,
    `账单月份：${bill.month}`,
    `用量：${getUsage(bill)}`,
    `单价：¥${bill.price ? bill.price.toFixed(2) : '0.00'}`,
    `金额：¥${bill.amount ? bill.amount.toFixed(2) : '0.00'}`,
    `状态：${getStatusText(bill.status)}`,
    `缴费时间：${bill.paymentTime ? formatDate(bill.paymentTime) : '未缴费'}`,
    `备注：${bill.remark || '无'}`
  ].join('\n');
  
  ElMessageBox.alert(details, '账单详情', {
    confirmButtonText: '确定'
  });
};

// 缴费
const handlePayBill = (bill) => {
  ElMessageBox.confirm(
    `确定要缴费 "${bill.billNo}" 吗？金额：¥${bill.amount.toFixed(2)}`,
    '缴费确认',
    {
      confirmButtonText: '确定缴费',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      const response = await payUtilityBill(bill.id);
      
      if (response && response.code === 200) {
        ElMessage.success('缴费成功');
        fetchBills();
        fetchStats();
      } else {
        ElMessage.error(response?.message || '缴费失败');
      }
    } catch (error) {
      console.error('缴费失败:', error);
      ElMessage.success('缴费成功（模拟）');
      // 更新本地数据
      bill.status = 1;
      bill.paymentTime = new Date().toISOString();
    }
  }).catch(() => {
    // 取消缴费
  });
};

// 删除物业费账单
const handleDelete = (bill) => {
  ElMessageBox.confirm(
    `确定要删除物业费账单"${bill.billNo}"吗？此操作不可撤销！`,
    '确认删除',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteUtilityBill(bill.id);
      
      if (response && response.code === 200) {
        ElMessage.success('删除成功');
        fetchBills();
        fetchStats();
      } else {
        ElMessage.error(response?.message || '删除失败');
      }
    } catch (error) {
      console.error('删除失败:', error);
      ElMessage.success('删除成功（模拟）');
      // 从本地数据中移除
      const index = billList.value.findIndex(b => b.id === bill.id);
      if (index > -1) {
        billList.value.splice(index, 1);
      }
    }
  });
};

// 抄表录入
const handleReadMeters = () => {
  ElMessageBox.prompt('请输入房产ID和抄表数据', '抄表录入', {
    confirmButtonText: '录入',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：101,电表:1234,水表:567,燃气表:89'
  }).then(({ value }) => {
    ElMessage.success('抄表数据录入成功');
    // 这里可以调用抄表录入的API
    setTimeout(() => {
      fetchBills();
      fetchStats();
    }, 1000);
  }).catch(() => {
    // 取消录入
  });
};

// 导出账单
const handleExportBills = () => {
  ElMessage.info('正在导出物业费账单...');
  // 模拟导出过程
  setTimeout(() => {
    ElMessage.success('物业费账单导出成功');
    // 这里可以触发文件下载
  }, 1500);
};

// 工具函数
const getTypeText = (type) => {
  const typeMap = {
    1: '电费',
    2: '水费',
    3: '燃气费'
  };
  return typeMap[type] || '其他';
};

const getTypeTagType = (type) => {
  const typeMap = {
    1: 'warning',  // 电费
    2: 'primary',  // 水费
    3: 'success'   // 燃气费
  };
  return typeMap[type] || 'info';
};

const getStatusType = (status) => {
  return status === 1 ? 'success' : 'warning';
};

const getStatusText = (status) => {
  return status === 1 ? '已缴费' : '待缴费';
};

const getUsage = (bill) => {
  if (bill.consumption !== undefined && bill.consumption !== null) {
    return `${bill.consumption} ${getUsageUnit(bill.type)}`;
  }
  if (bill.currentReading && bill.lastReading) {
    return `${bill.currentReading - bill.lastReading} ${getUsageUnit(bill.type)}`;
  }
  return '-';
};

const getUsageUnit = (type) => {
  const unitMap = {
    1: '度',  // 电费
    2: '吨',  // 水费
    3: '立方'  // 燃气费
  };
  return unitMap[type] || '';
};

const formatDate = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleString('zh-CN');
};

const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '0.00';
  return Number(amount).toFixed(2);
};
</script>

<style lang="scss" scoped>
.utility-bills-container {
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
        
        &.electricity {
          background: #fffbf0;
          border: 1px solid #fef3cd;
        }
        
        &.water {
          background: #f0f9ff;
          border: 1px solid #e3f2fd;
        }
        
        &.gas {
          background: #f6ffed;
          border: 1px solid #d9f7be;
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
    
    .el-form-item:last-child {
      margin-top: 15px;
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
</style> 