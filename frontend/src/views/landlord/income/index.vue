<template>
  <div class="landlord-income">
    <div class="income-container">
      <div class="page-header">
        <h1 class="page-title">收入管理</h1>
        <p class="page-desc">查看您的收入统计和明细</p>
      </div>

      <!-- 收入统计卡片 -->
      <div class="income-stats">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(incomeStats?.totalIncome) }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card rental" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(incomeStats?.rentalIncome) }}</div>
              <div class="stat-label">租金收入</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card deposit" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CreditCard /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(incomeStats?.depositIncome) }}</div>
              <div class="stat-label">押金收入</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(incomeStats?.pending) }}</div>
              <div class="stat-label">待收款</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 年份选择和月度统计图表 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">月度收入趋势</span>
            <el-select v-model="selectedYear" @change="fetchIncomeStats" style="width: 120px">
              <el-option
                v-for="year in yearOptions"
                :key="year"
                :label="year + '年'"
                :value="year"
              />
            </el-select>
          </div>
        </template>

        <div class="chart-container" ref="chartContainer">
          <!-- 这里可以集成图表库，如 ECharts -->
          <div class="monthly-stats">
            <div 
              v-for="month in 12" 
              :key="month" 
              class="month-item"
              :class="{ active: month === currentMonth }"
            >
              <div class="month-label">{{ month }}月</div>
              <div class="month-value">
                ¥{{ formatMoney(getMonthlyIncome(month)) }}
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 筛选条件 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-container">
          <el-select v-model="filterType" placeholder="收入类型" @change="handleFilter">
            <el-option label="全部类型" value="" />
            <el-option label="租金" value="RENT" />
            <el-option label="押金" value="DEPOSIT" />
            <el-option label="水电费" value="UTILITY" />
          </el-select>
          
          <el-select v-model="filterProperty" placeholder="房产筛选" @change="handleFilter">
            <el-option label="全部房产" value="" />
            <el-option
              v-for="property in propertyOptions"
              :key="property.id"
              :label="property.title"
              :value="property.id"
            />
          </el-select>
          
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleFilter"
          />
          
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            筛选
          </el-button>
          
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </div>
      </el-card>

      <!-- 收入明细列表 -->
      <el-card class="details-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">收入明细</span>
            <span class="header-count">共 {{ total }} 条记录</span>
          </div>
        </template>

        <el-table
          :data="incomeDetails"
          border
          style="width: 100%"
          v-loading="loading"
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column type="index" width="60" align="center" fixed />
          
          <el-table-column label="收入类型" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getIncomeTypeTagType(scope.row.type)">
                {{ getIncomeTypeText(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="propertyTitle" label="房产" min-width="200" show-overflow-tooltip />
          
          <el-table-column prop="username" label="租户" width="120" show-overflow-tooltip />
          
          <el-table-column label="金额" width="120" align="center">
            <template #default="scope">
              <span class="amount-text">¥{{ formatMoney(scope.row.amount) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip />
          
          <el-table-column label="收款时间" width="180" align="center">
            <template #default="scope">
              {{ formatDateTime(scope.row.paymentTime) }}
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-container">
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
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  Money, House, CreditCard, Clock, Search, Refresh, Download 
} from '@element-plus/icons-vue';
import { 
  getLandlordIncomeStats, 
  getLandlordIncomeDetails 
} from '@/api/landlords';

// 响应式数据
const loading = ref(false);
const incomeStats = ref(null);
const incomeDetails = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 年份选择
const selectedYear = ref(new Date().getFullYear());
const yearOptions = ref([]);

// 筛选条件
const filterType = ref('');
const filterProperty = ref('');
const dateRange = ref([]);
const propertyOptions = ref([]);

// 当前月份
const currentMonth = computed(() => new Date().getMonth() + 1);

// 初始化年份选项
const initYearOptions = () => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let i = currentYear; i >= currentYear - 5; i--) {
    years.push(i);
  }
  yearOptions.value = years;
};

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '0';
  return Number(amount).toLocaleString();
};

// 格式化日期时间
const formatDateTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 获取月度收入
const getMonthlyIncome = (month) => {
  if (!incomeStats.value?.monthlyStats) return 0;
  const monthData = incomeStats.value.monthlyStats.find(item => item.month === month);
  return monthData?.income || 0;
};

// 获取收入类型文本
const getIncomeTypeText = (type) => {
  switch (type) {
    case 'RENT':
      return '租金';
    case 'DEPOSIT':
      return '押金';
    case 'UTILITY':
      return '水电费';
    default:
      return '其他';
  }
};

// 获取收入类型标签类型
const getIncomeTypeTagType = (type) => {
  switch (type) {
    case 'RENT':
      return 'success';
    case 'DEPOSIT':
      return 'warning';
    case 'UTILITY':
      return 'info';
    default:
      return '';
  }
};

// 获取收入统计
const fetchIncomeStats = async () => {
  try {
    const response = await getLandlordIncomeStats({
      year: selectedYear.value
    });
    if (response && response.code === 200) {
      incomeStats.value = response.data;
    }
  } catch (error) {
    console.error('获取收入统计失败:', error);
  }
};

// 获取收入明细
const fetchIncomeDetails = async () => {
  loading.value = true;
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };
    
    if (filterType.value) {
      params.type = filterType.value;
    }
    
    if (filterProperty.value) {
      params.propertyId = filterProperty.value;
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }

    const response = await getLandlordIncomeDetails(params);
    if (response && response.code === 200) {
      incomeDetails.value = response.data.content || [];
      total.value = response.data.pageable?.total || 0;
    } else {
      ElMessage.error(response?.message || '获取收入明细失败');
    }
  } catch (error) {
    console.error('获取收入明细失败:', error);
    ElMessage.error('获取收入明细失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1;
  fetchIncomeDetails();
};

// 重置筛选
const handleReset = () => {
  filterType.value = '';
  filterProperty.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  fetchIncomeDetails();
};

// 导出数据
const handleExport = () => {
  ElMessage.info('导出功能开发中...');
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchIncomeDetails();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchIncomeDetails();
};

// 组件挂载时执行
onMounted(() => {
  initYearOptions();
  fetchIncomeStats();
  fetchIncomeDetails();
});
</script>

<style lang="scss" scoped>
.landlord-income {
  .income-container {
    max-width: 1400px;
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

  .income-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .stat-card {
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }

      .stat-content {
        display: flex;
        align-items: center;
        padding: 20px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          margin-right: 20px;
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1;
            margin-bottom: 5px;
          }

          .stat-label {
            font-size: 14px;
            color: #7f8c8d;
          }
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
      }

      &.rental .stat-icon {
        background: linear-gradient(135deg, #11998e, #38ef7d);
        color: white;
      }

      &.deposit .stat-icon {
        background: linear-gradient(135deg, #feca57, #ff9ff3);
        color: white;
      }

      &.pending .stat-icon {
        background: linear-gradient(135deg, #ff6b6b, #ee5a24);
        color: white;
      }
    }
  }

  .chart-card {
    margin-bottom: 30px;

    .chart-container {
      .monthly-stats {
        display: grid;
        grid-template-columns: repeat(6, 1fr);
        gap: 15px;

        @media (max-width: 768px) {
          grid-template-columns: repeat(3, 1fr);
        }

        .month-item {
          text-align: center;
          padding: 20px 10px;
          background: #f8f9fa;
          border-radius: 8px;
          transition: all 0.3s ease;

          &:hover {
            background: #e9ecef;
          }

          &.active {
            background: #e3f2fd;
            border: 2px solid #2196f3;
          }

          .month-label {
            font-size: 14px;
            color: #7f8c8d;
            margin-bottom: 10px;
          }

          .month-value {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
          }
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 30px;

    .filter-container {
      display: flex;
      gap: 15px;
      align-items: center;
      flex-wrap: wrap;
    }
  }

  .details-card {
    .amount-text {
      font-weight: 600;
      color: #27ae60;
    }

    .pagination-container {
      margin-top: 20px;
      text-align: center;
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

    .header-count {
      font-size: 14px;
      color: #7f8c8d;
    }
  }
}
</style> 