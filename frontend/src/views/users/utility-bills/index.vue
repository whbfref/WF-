<template>
  <div class="user-utility-bills">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">物业费账单</h1>
        <p class="page-subtitle">管理您的物业费缴纳记录</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon unpaid">
            <i class="icon">⚠️</i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ unpaidCount }}</div>
            <div class="stat-label">待缴费用</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon amount">
            <i class="icon">💰</i>
          </div>
          <div class="stat-content">
            <div class="stat-number">¥{{ formatMoney(unpaidAmount) }}</div>
            <div class="stat-label">未缴金额</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon paid">
            <i class="icon">✅</i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ paidCount }}</div>
            <div class="stat-label">已缴费用</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon total">
            <i class="icon">💳</i>
          </div>
          <div class="stat-content">
            <div class="stat-number">¥{{ formatMoney(totalAmount) }}</div>
            <div class="stat-label">累计缴费</div>
          </div>
        </div>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-item">
          <label>费用类型:</label>
          <select v-model="searchForm.billType" @change="handleSearch">
            <option value="">全部</option>
            <option value="PROPERTY_MANAGEMENT">物业管理费</option>
            <option value="WATER">水费</option>
            <option value="ELECTRICITY">电费</option>
            <option value="GAS">燃气费</option>
            <option value="INTERNET">网络费</option>
            <option value="PARKING">停车费</option>
          </select>
        </div>
        
        <div class="filter-item">
          <label>缴费状态:</label>
          <select v-model="searchForm.status" @change="handleSearch">
            <option value="">全部</option>
            <option value="UNPAID">待缴费</option>
            <option value="PAID">已缴费</option>
            <option value="OVERDUE">已逾期</option>
          </select>
        </div>
        
        <div class="filter-item">
          <label>时间范围:</label>
          <input 
            type="month" 
            v-model="searchForm.startMonth" 
            @change="handleSearch"
          />
          <span>至</span>
          <input 
            type="month" 
            v-model="searchForm.endMonth" 
            @change="handleSearch"
          />
        </div>
        
        <button class="btn-primary" @click="handleSearch">搜索</button>
        <button class="btn-secondary" @click="handleReset">重置</button>
      </div>

      <!-- 账单列表 -->
      <div class="bills-section">
        <div class="section-header">
          <h2>账单列表</h2>
          <button 
            class="btn-primary" 
            @click="handleBatchPay"
            :disabled="selectedBills.length === 0"
          >
            批量缴费 ({{ selectedBills.length }})
          </button>
        </div>
        
        <div class="bills-grid" v-if="billList.length > 0">
          <div 
            v-for="bill in billList" 
            :key="bill.id" 
            class="bill-card"
            :class="{ 'selected': selectedBills.includes(bill) }"
            @click="toggleBillSelection(bill)"
          >
            <div class="bill-header">
              <div class="bill-type">
                <span class="type-icon">{{ getBillTypeIcon(bill.billType) }}</span>
                <span class="type-text">{{ getBillTypeText(bill.billType) }}</span>
              </div>
              <div class="bill-status" :class="bill.status.toLowerCase()">
                {{ getStatusText(bill.status) }}
              </div>
            </div>
            
            <div class="bill-content">
              <div class="bill-period">{{ bill.billPeriod }}</div>
              <div class="room-info">
                <h3>{{ bill.roomTitle }}</h3>
                <p>{{ bill.roomAddress }}</p>
              </div>
              
              <div class="usage-info" v-if="bill.usage">
                <div class="usage">{{ bill.usage }}{{ getUsageUnit(bill.billType) }}</div>
                <div class="unit-price">¥{{ bill.unitPrice }}/{{ getUsageUnit(bill.billType) }}</div>
              </div>
              
              <div class="bill-amount">
                <span class="amount">¥{{ formatMoney(bill.amount) }}</span>
              </div>
            </div>
            
            <div class="bill-footer">
              <div class="due-date">
                应缴日期: {{ formatDate(bill.dueDate) }}
              </div>
              
              <div class="bill-actions">
                <button class="btn-small" @click.stop="handleViewDetail(bill)">
                  查看详情
                </button>
                <button 
                  v-if="bill.status === 'UNPAID'"
                  class="btn-primary btn-small" 
                  @click.stop="handlePay(bill)"
                >
                  立即缴费
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <div class="empty-icon">📋</div>
          <h3>暂无账单记录</h3>
          <p>您目前没有物业费账单记录</p>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <button 
          @click="handlePageChange(currentPage - 1)"
          :disabled="currentPage <= 1"
        >
          上一页
        </button>
        <span>第 {{ currentPage }} 页，共 {{ Math.ceil(total / pageSize) }} 页</span>
        <button 
          @click="handlePageChange(currentPage + 1)"
          :disabled="currentPage >= Math.ceil(total / pageSize)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 账单详情弹窗 -->
    <div v-if="detailDialogVisible" class="modal-overlay" @click="detailDialogVisible = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>账单详情</h3>
          <button class="close-btn" @click="detailDialogVisible = false">×</button>
        </div>
        
        <div class="modal-body" v-if="selectedBill">
          <div class="detail-section">
            <h4>账单信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">账单编号:</span>
                <span class="value">{{ selectedBill.billNumber }}</span>
              </div>
              <div class="detail-item">
                <span class="label">费用类型:</span>
                <span class="value">{{ getBillTypeText(selectedBill.billType) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">账单期间:</span>
                <span class="value">{{ selectedBill.billPeriod }}</span>
              </div>
              <div class="detail-item">
                <span class="label">费用金额:</span>
                <span class="value amount">¥{{ selectedBill.amount }}</span>
              </div>
              <div class="detail-item">
                <span class="label">应缴日期:</span>
                <span class="value">{{ formatDate(selectedBill.dueDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">账单状态:</span>
                <span class="value status" :class="selectedBill.status.toLowerCase()">
                  {{ getStatusText(selectedBill.status) }}
                </span>
              </div>
              <div class="detail-item" v-if="selectedBill.paidDate">
                <span class="label">缴费时间:</span>
                <span class="value">{{ formatDate(selectedBill.paidDate) }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section" v-if="selectedBill.usage">
            <h4>用量详情</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">本期用量:</span>
                <span class="value">{{ selectedBill.usage }}{{ getUsageUnit(selectedBill.billType) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">单价:</span>
                <span class="value">¥{{ selectedBill.unitPrice }}/{{ getUsageUnit(selectedBill.billType) }}</span>
              </div>
              <div class="detail-item" v-if="selectedBill.previousReading">
                <span class="label">上期读数:</span>
                <span class="value">{{ selectedBill.previousReading }}</span>
              </div>
              <div class="detail-item" v-if="selectedBill.currentReading">
                <span class="label">本期读数:</span>
                <span class="value">{{ selectedBill.currentReading }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>房源信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">房源名称:</span>
                <span class="value">{{ selectedBill.roomTitle }}</span>
              </div>
              <div class="detail-item">
                <span class="label">房源地址:</span>
                <span class="value">{{ selectedBill.roomAddress }}</span>
              </div>
              <div class="detail-item">
                <span class="label">房东姓名:</span>
                <span class="value">{{ selectedBill.landlordName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">联系电话:</span>
                <span class="value">{{ selectedBill.landlordPhone }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="detailDialogVisible = false">关闭</button>
          <button 
            v-if="selectedBill?.status === 'UNPAID'"
            class="btn-primary" 
            @click="handlePay(selectedBill)"
          >
            立即缴费
          </button>
        </div>
      </div>
    </div>

    <!-- 缴费弹窗 -->
    <div v-if="payDialogVisible" class="modal-overlay" @click="payDialogVisible = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>物业费缴费</h3>
          <button class="close-btn" @click="payDialogVisible = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="pay-summary">
            <h4>缴费信息</h4>
            <div class="bill-list">
              <div v-for="bill in payBills" :key="bill.id" class="bill-item">
                <span>{{ getBillTypeText(bill.billType) }} - {{ bill.billPeriod }}</span>
                <span class="amount">¥{{ bill.amount }}</span>
              </div>
            </div>
            <div class="total-summary">
              <span>总计:</span>
              <span class="total-amount">¥{{ payTotalAmount }}</span>
            </div>
          </div>
          
          <div class="pay-method">
            <h4>支付方式</h4>
            <div class="payment-options">
              <label class="payment-option">
                <input type="radio" v-model="payMethod" value="alipay" />
                <span>💳 支付宝</span>
              </label>
              <label class="payment-option">
                <input type="radio" v-model="payMethod" value="wechat" />
                <span>💬 微信支付</span>
              </label>
              <label class="payment-option">
                <input type="radio" v-model="payMethod" value="bank" />
                <span>🏦 银行卡</span>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="payDialogVisible = false">取消</button>
          <button class="btn-primary" @click="confirmPayment" :disabled="payLoading">
            {{ payLoading ? '处理中...' : '确认缴费' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { getUserUtilityBills, payUserUtilityBill } from '@/api/utilityBills';

// 响应式数据
const billList = ref([]);
const loading = ref(false);
const searchForm = reactive({
  status: '',
  billType: '',
  keyword: ''
});

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 模态框状态
const payDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const selectedBill = ref(null);
const payBills = ref([]);
const paymentMethod = ref('ALIPAY');
const payLoading = ref(false);
const payMethod = ref('ALIPAY');
const selectedBills = ref([]);

// 组件挂载时获取数据
onMounted(() => {
  fetchBillList();
});

// 获取账单列表
const fetchBillList = async () => {
  try {
    loading.value = true;
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      status: searchForm.status || undefined
    };
    
    const response = await getUserUtilityBills(params);
    if (response && response.code === 200) {
      const data = response.data;
      if (data && data.content) {
        billList.value = data.content.map(bill => ({
          id: bill.id,
          billNumber: bill.billNo || `UTIL${bill.id}`,
          billPeriod: bill.month || '2024年1月',
          billType: getBillTypeFromNumber(bill.type),
          roomTitle: '房间信息',
          roomAddress: '地址信息',
          amount: bill.amount || 0,
          dueDate: new Date().toISOString().split('T')[0],
          status: bill.status === 1 ? 'PAID' : 'UNPAID',
          landlordName: '房东',
          landlordPhone: '联系方式',
          paidDate: bill.paymentTime ? new Date(bill.paymentTime).toISOString().split('T')[0] : null,
          usage: bill.consumption || null,
          unitPrice: bill.price || null,
          previousReading: bill.lastReading || null,
          currentReading: bill.currentReading || null
        }));
        
        pagination.total = data.totalElements || 0;
      } else {
        billList.value = [];
        pagination.total = 0;
      }
    } else {
      billList.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取物业费账单失败:', error);
    billList.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 将数字类型转换为字符串类型
const getBillTypeFromNumber = (type) => {
  const typeMap = {
    1: 'ELECTRICITY',
    2: 'WATER', 
    3: 'GAS',
    4: 'PROPERTY_MANAGEMENT',
    5: 'INTERNET',
    6: 'PARKING'
  };
  return typeMap[type] || 'PROPERTY_MANAGEMENT';
};

// 计算属性
const unpaidCount = computed(() => {
  return billList.value.filter(bill => bill.status === 'UNPAID').length;
});

const unpaidAmount = computed(() => {
  return billList.value
    .filter(bill => bill.status === 'UNPAID')
    .reduce((sum, bill) => sum + bill.amount, 0);
});

const paidCount = computed(() => {
  return billList.value.filter(bill => bill.status === 'PAID').length;
});

const totalAmount = computed(() => {
  return billList.value
    .filter(bill => bill.status === 'PAID')
    .reduce((sum, bill) => sum + bill.amount, 0);
});

const payTotalAmount = computed(() => {
  return payBills.value.reduce((sum, bill) => sum + bill.amount, 0);
});

// 分页计算属性
const currentPage = computed(() => pagination.currentPage);
const pageSize = computed(() => pagination.pageSize);
const total = computed(() => pagination.total);

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchBillList();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    billType: '',
    status: '',
    startMonth: '',
    endMonth: ''
  });
  handleSearch();
};

// 分页处理
const handlePageChange = (page) => {
  if (page >= 1 && page <= Math.ceil(pagination.total / pagination.pageSize)) {
    pagination.currentPage = page;
    fetchBillList();
  }
};

// 选择账单
const toggleBillSelection = (bill) => {
  if (bill.status !== 'UNPAID') return;
  
  const index = selectedBills.value.findIndex(b => b.id === bill.id);
  if (index > -1) {
    selectedBills.value.splice(index, 1);
  } else {
    selectedBills.value.push(bill);
  }
};

// 查看详情
const handleViewDetail = (bill) => {
  selectedBill.value = bill;
  detailDialogVisible.value = true;
};

// 单笔缴费
const handlePay = (bill) => {
  payBills.value = [bill];
  payDialogVisible.value = true;
  detailDialogVisible.value = false;
};

// 批量缴费
const handleBatchPay = () => {
  if (selectedBills.value.length === 0) {
    alert('请选择要缴费的账单');
    return;
  }
  payBills.value = [...selectedBills.value];
  payDialogVisible.value = true;
};

// 确认缴费
const confirmPayment = async () => {
  payLoading.value = true;
  
  try {
    // 批量支付或单笔支付
    for (const bill of payBills.value) {
      await payUserUtilityBill(bill.id, { paymentMethod: payMethod.value });
    }
    
    alert('缴费成功');
    payDialogVisible.value = false;
    selectedBills.value = [];
    
    // 重新获取账单列表
    await fetchBillList();
  } catch (error) {
    console.error('支付失败:', error);
    alert('支付失败，请重试');
  } finally {
    payLoading.value = false;
  }
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleDateString();
};

// 获取账单类型图标
const getBillTypeIcon = (type) => {
  const icons = {
    'PROPERTY_MANAGEMENT': '🏢',
    'WATER': '💧',
    'ELECTRICITY': '⚡',
    'GAS': '🔥',
    'INTERNET': '🌐',
    'PARKING': '🚗'
  };
  return icons[type] || '📄';
};

// 获取账单类型文本
const getBillTypeText = (type) => {
  const texts = {
    'PROPERTY_MANAGEMENT': '物业管理费',
    'WATER': '水费',
    'ELECTRICITY': '电费',
    'GAS': '燃气费',
    'INTERNET': '网络费',
    'PARKING': '停车费'
  };
  return texts[type] || '未知';
};

// 获取用量单位
const getUsageUnit = (type) => {
  const units = {
    'WATER': '吨',
    'ELECTRICITY': '度',
    'GAS': '立方米'
  };
  return units[type] || '';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    'UNPAID': '待缴费',
    'PAID': '已缴费',
    'OVERDUE': '已逾期'
  };
  return statusTexts[status] || '未知';
};

// 格式化金额，保留两位小数
const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '0.00';
  return Number(amount).toFixed(2);
};
</script>

<style scoped>
.user-utility-bills {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.unpaid {
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
}

.stat-icon.amount {
  background: linear-gradient(45deg, #feca57, #ff9ff3);
}

.stat-icon.paid {
  background: linear-gradient(45deg, #48dbfb, #0abde3);
}

.stat-icon.total {
  background: linear-gradient(45deg, #1dd1a1, #10ac84);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.filter-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 30px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-weight: 500;
  color: #2c3e50;
}

.filter-item select,
.filter-item input {
  padding: 8px 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.filter-item select:focus,
.filter-item input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-primary {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-small {
  padding: 6px 12px;
  font-size: 12px;
}

.bills-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  color: #2c3e50;
  margin: 0;
}

.bills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.bill-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.bill-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.bill-card.selected {
  border-color: #667eea;
  background: #f8f9ff;
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.bill-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-icon {
  font-size: 1.2rem;
}

.type-text {
  font-weight: 600;
  color: #2c3e50;
}

.bill-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.bill-status.unpaid {
  background: #fff3cd;
  color: #856404;
}

.bill-status.paid {
  background: #d1edff;
  color: #0c5460;
}

.bill-status.overdue {
  background: #f8d7da;
  color: #721c24;
}

.bill-content {
  margin-bottom: 16px;
}

.bill-period {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.room-info h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.room-info p {
  margin: 0 0 12px 0;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.usage-info {
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
}

.usage {
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.9rem;
}

.unit-price {
  font-size: 0.8rem;
  color: #7f8c8d;
}

.bill-amount {
  text-align: right;
}

.amount {
  font-size: 1.5rem;
  font-weight: 700;
  color: #e74c3c;
}

.bill-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.due-date {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.bill-actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.empty-state p {
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.pagination button {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: #667eea;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button:hover:not(:disabled) {
  background: #5a67d8;
}

.pagination button:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #7f8c8d;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e9ecef;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.detail-item .label {
  color: #7f8c8d;
  font-weight: 500;
}

.detail-item .value {
  color: #2c3e50;
  font-weight: 600;
}

.detail-item .value.amount {
  color: #e74c3c;
  font-size: 1.2rem;
}

.pay-summary {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.pay-summary h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
}

.bill-list {
  margin-bottom: 12px;
}

.bill-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #e4e7ed;
}

.bill-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.total-summary {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
  font-size: 16px;
  padding-top: 8px;
  border-top: 2px solid #e4e7ed;
}

.total-amount {
  color: #e74c3c;
  font-size: 18px;
}

.pay-method h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.payment-option:hover {
  border-color: #667eea;
}

.payment-option input[type="radio"] {
  margin: 0;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .bills-grid {
    grid-template-columns: 1fr;
  }
  
  .bill-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 2rem;
  }
}
</style> 