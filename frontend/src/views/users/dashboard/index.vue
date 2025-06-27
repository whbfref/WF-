<template>
  <div class="user-dashboard">
    <div class="dashboard-container">
      <div class="page-header">
        <div class="welcome-section animated-welcome">
          <div class="welcome-card">
            <div class="welcome-content">
              <div class="welcome-text">
                <h1 class="page-title typing-effect">欢迎回来，{{ userInfo?.username }}</h1>
                <p class="page-desc fade-in-up">{{ getCurrentTimeGreeting() }}</p>
              </div>
              <div class="welcome-actions">
                <el-button type="primary" class="quick-search-btn" @click="quickSearch">
                  <el-icon><Search /></el-icon>
                  快速找房
                </el-button>
                <el-button class="quick-bill-btn" @click="router.push('/user/rent-bills')">
                  <el-icon><Money /></el-icon>
                  查看账单
                </el-button>
              </div>
            </div>
            <div class="welcome-decoration">
              <div class="floating-icon icon-1">
                <el-icon><House /></el-icon>
              </div>
              <div class="floating-icon icon-2">
                <el-icon><Star /></el-icon>
              </div>
              <div class="floating-icon icon-3">
                <el-icon><Location /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快速统计 -->
      <div class="quick-stats">
        <el-card class="stat-card" shadow="hover" @click="router.push('/user/rentals')">
          <div class="stat-content">
            <div class="stat-icon rental">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ rentalCount }}</div>
              <div class="stat-label">当前租房</div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card" shadow="hover" @click="router.push('/user/rent-bills')">
          <div class="stat-content">
            <div class="stat-icon unpaid">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ unpaidRentBills }}</div>
              <div class="stat-label">待缴租金</div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card" shadow="hover" @click="router.push('/user/utility-bills')">
          <div class="stat-content">
            <div class="stat-icon utility">
              <el-icon><Lightning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ unpaidUtilityBills }}</div>
              <div class="stat-label">待缴物业费</div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card" shadow="hover" @click="router.push('/user/applications')">
          <div class="stat-content">
            <div class="stat-icon application">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingApplications }}</div>
              <div class="stat-label">待审核申请</div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="dashboard-content">
        <!-- 快捷操作 -->
        <el-card class="quick-actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">快捷操作</span>
            </div>
          </template>
          
          <div class="actions-grid">
            <div class="action-item" @click="router.push('/user/browse-rooms')">
              <div class="action-icon browse">
                <el-icon><Search /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">浏览房源</div>
                <div class="action-desc">发现理想的居住空间</div>
              </div>
            </div>
            
            <div class="action-item" @click="router.push('/user/rent-bills')">
              <div class="action-icon pay">
                <el-icon><Money /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">缴纳租金</div>
                <div class="action-desc">查看和支付租金账单</div>
              </div>
            </div>
            
            <div class="action-item" @click="router.push('/user/utility-bills')">
              <div class="action-icon utility">
                <el-icon><Lightning /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">缴纳物业费</div>
                <div class="action-desc">查看和支付物业费账单</div>
              </div>
            </div>
            
            <div class="action-item" @click="router.push('/user/feedback')">
              <div class="action-icon feedback">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">意见反馈</div>
                <div class="action-desc">提交您的宝贵意见</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 最近账单 -->
        <el-card class="recent-bills-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">最近账单</span>
              <el-button type="primary" text @click="router.push('/user/rent-bills')">
                查看全部
              </el-button>
            </div>
          </template>
          
          <div class="bills-list" v-loading="billsLoading">
            <div v-if="recentBills.length > 0" class="bill-items">
              <div v-for="bill in recentBills" :key="bill.id" class="bill-item">
                <div class="bill-type">
                  <el-icon class="type-icon" :class="getBillTypeClass(bill.type)">
                    <component :is="getBillTypeIcon(bill.type)" />
                  </el-icon>
                  <span class="type-text">{{ getBillTypeText(bill.type) }}</span>
                </div>
                
                <div class="bill-info">
                  <div class="bill-period">{{ bill.period }}</div>
                  <div class="bill-amount">¥{{ bill.amount }}</div>
                </div>
                
                <div class="bill-status">
                  <el-tag :type="getBillStatusType(bill.status)" size="small">
                    {{ getBillStatusText(bill.status) }}
                  </el-tag>
                </div>
                
                <div class="bill-actions">
                  <el-button 
                    v-if="bill.status === 'UNPAID'"
                    type="primary" 
                    size="small"
                    @click="handlePayBill(bill)"
                  >
                    立即缴费
                  </el-button>
                </div>
              </div>
            </div>
            
            <el-empty v-else description="暂无账单记录" />
          </div>
        </el-card>
      </div>

      <!-- 租房信息卡片 -->
      <el-card v-if="currentRental" class="current-rental-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">当前租房</span>
            <el-tag type="success">进行中</el-tag>
          </div>
        </template>
        
        <div class="rental-summary">
          <div class="rental-image">
            <el-image
              :src="currentRental.imageUrl || defaultRoomImage"
              fit="cover"
              class="image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          
          <div class="rental-info">
            <h3 class="rental-title">{{ currentRental.title }}</h3>
            <p class="rental-address">
              <el-icon><Location /></el-icon>
              {{ currentRental.address }}
            </p>
            
            <div class="rental-details">
              <div class="detail-item">
                <span class="label">月租金:</span>
                <span class="value">¥{{ currentRental.monthlyRent }}</span>
              </div>
              <div class="detail-item">
                <span class="label">剩余天数:</span>
                <span class="value" :class="getRemainingDaysClass(currentRental.endDate)">
                  {{ getRemainingDays(currentRental.endDate) }}天
                </span>
              </div>
            </div>
            
            <div class="rental-actions">
                              <el-button type="primary" size="small" @click="router.push('/user/rentals')">
                  查看详情
                </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { 
  House, Warning, Lightning, Document, Search, Money, 
  ChatDotRound, Picture, Location, Star
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { getCurrentUser, getUserRentBills, getUserUtilityBills, getUserRentals, getRoomApplications, getDashboardStats } from '@/api/user';

const userStore = useUserStore();
const router = useRouter();

// 默认房源图片
const defaultRoomImage = 'https://via.placeholder.com/300x200?text=房源图片';

// 响应式数据
const billsLoading = ref(false);
const rentalCount = ref(0);
const unpaidRentBills = ref(0);
const unpaidUtilityBills = ref(0);
const pendingApplications = ref(0);
const recentBills = ref([]);
const currentRental = ref(null);

// 用户信息
const userInfo = computed(() => userStore.userInfo);

// 组件挂载时获取数据
onMounted(() => {
  fetchDashboardData();
});

// 获取仪表板数据
const fetchDashboardData = async () => {
  try {
    console.log('开始获取仪表板数据...');
    // 使用专门的仪表盘统计API
    const response = await getDashboardStats();
    console.log('仪表盘API响应:', response);
    
    if (response && response.code === 200) {
      const stats = response.data;
      console.log('仪表盘统计数据:', stats);
      
      // 更新统计数据
      rentalCount.value = stats.rentalCount || 0;
      unpaidRentBills.value = stats.unpaidRentBills || 0;
      unpaidUtilityBills.value = stats.unpaidUtilityBills || 0;
      pendingApplications.value = stats.pendingApplications || 0;
      
      console.log('更新后的统计数据:', {
        rentalCount: rentalCount.value,
        unpaidRentBills: unpaidRentBills.value,
        unpaidUtilityBills: unpaidUtilityBills.value,
        pendingApplications: pendingApplications.value
      });
      
      // 设置当前租房信息
      if (stats.currentRental) {
        currentRental.value = Array.isArray(stats.currentRental) 
          ? stats.currentRental.find(rental => rental.status === 'ACTIVE') || null
          : stats.currentRental;
        console.log('当前租房信息:', currentRental.value);
      }
      
      // 设置最近账单
      recentBills.value = stats.recentBills || [];
      console.log('最近账单:', recentBills.value);
    } else {
      console.log('仪表盘API失败，回退到原来的方式');
      // 如果API失败，回退到原来的方式
      await Promise.all([
        fetchRentalInfo(),
        fetchBillsInfo(),
        fetchApplicationsInfo()
      ]);
    }
  } catch (error) {
    console.error('获取仪表板数据失败:', error);
    // 如果API失败，回退到原来的方式
    try {
      console.log('尝试回退方式获取数据...');
      await Promise.all([
        fetchRentalInfo(),
        fetchBillsInfo(),
        fetchApplicationsInfo()
      ]);
    } catch (fallbackError) {
      console.error('回退方式也失败:', fallbackError);
    }
  }
};

// 获取租房信息
const fetchRentalInfo = async () => {
  try {
    const response = await getUserRentals();
    if (response && response.code === 200) {
      // 确保数据是数组格式
      let rentals = [];
      if (Array.isArray(response.data)) {
        rentals = response.data;
      } else if (response.data && Array.isArray(response.data.content)) {
        rentals = response.data.content;
      } else if (response.data) {
        // 如果是单个对象，转换为数组
        rentals = [response.data];
      }
      
      currentRental.value = rentals.find(rental => rental.status === 'ACTIVE') || null;
      rentalCount.value = rentals.filter(rental => rental.status === 'ACTIVE').length;
    } else {
      currentRental.value = null;
      rentalCount.value = 0;
    }
  } catch (error) {
    console.error('获取租房信息失败:', error);
    // 设置默认值
    currentRental.value = null;
    rentalCount.value = 0;
  }
};

// 获取账单信息
const fetchBillsInfo = async () => {
  try {
    billsLoading.value = true;
    
    // 获取租金账单
    const rentBillsResponse = await getUserRentBills({ page: 1, size: 5 });
    if (rentBillsResponse.code === 200) {
      const rentBills = rentBillsResponse.data?.content || [];
      unpaidRentBills.value = rentBills.filter(bill => bill.status === 'UNPAID').length;
      
      // 添加到最近账单列表
      recentBills.value.push(...rentBills.map(bill => ({
        ...bill,
        type: 'RENT',
        period: bill.billPeriod
      })));
    }
    
    // 获取物业费账单
    const utilityBillsResponse = await getUserUtilityBills({ page: 1, size: 5 });
    if (utilityBillsResponse.code === 200) {
      const utilityBills = utilityBillsResponse.data?.content || [];
      unpaidUtilityBills.value = utilityBills.filter(bill => bill.status === 'UNPAID').length;
      
      // 添加到最近账单列表
      recentBills.value.push(...utilityBills.map(bill => ({
        ...bill,
        type: 'UTILITY',
        period: bill.billPeriod
      })));
    }
    
    // 按时间排序，取最近的5条
    recentBills.value = recentBills.value
      .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      .slice(0, 5);
      
  } catch (error) {
    console.error('获取账单信息失败:', error);
  } finally {
    billsLoading.value = false;
  }
};

// 获取申请信息
const fetchApplicationsInfo = async () => {
  try {
    const response = await getRoomApplications();
    if (response.code === 200) {
      const applications = Array.isArray(response.data) ? response.data : (response.data?.content || []);
      // 统计待审核的申请数量
      pendingApplications.value = applications.filter(app => 
        app.status === 'PENDING' || app.status === 'REVIEWING'
      ).length;
    }
  } catch (error) {
    console.error('获取申请信息失败:', error);
    // 如果API失败，设置默认值
    pendingApplications.value = 0;
  }
};

// 获取当前时间问候语
const getCurrentTimeGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了，注意休息';
  if (hour < 9) return '早上好，新的一天开始了';
  if (hour < 12) return '上午好，祝您工作顺利';
  if (hour < 14) return '中午好，记得吃午饭';
  if (hour < 18) return '下午好，继续加油';
  if (hour < 22) return '晚上好，辛苦了一天';
  return '夜深了，早点休息';
};

// 处理缴费
const handlePayBill = (bill) => {
  if (bill.type === 'RENT') {
    router.push('/user/rent-bills');
  } else {
    router.push('/user/utility-bills');
  }
};

// 获取账单类型图标
const getBillTypeIcon = (type) => {
  return type === 'RENT' ? House : Lightning;
};

// 获取账单类型样式类
const getBillTypeClass = (type) => {
  return type === 'RENT' ? 'rent' : 'utility';
};

// 获取账单类型文本
const getBillTypeText = (type) => {
  return type === 'RENT' ? '租金' : '物业费';
};

// 获取账单状态类型
const getBillStatusType = (status) => {
  const statusTypes = {
    'UNPAID': 'warning',
    'PAID': 'success',
    'OVERDUE': 'danger'
  };
  return statusTypes[status] || 'info';
};

// 获取账单状态文本
const getBillStatusText = (status) => {
  const statusTexts = {
    'UNPAID': '待缴费',
    'PAID': '已缴费',
    'OVERDUE': '已逾期'
  };
  return statusTexts[status] || '未知';
};

// 获取剩余天数
const getRemainingDays = (endDate) => {
  if (!endDate) return 0;
  const today = new Date();
  const end = new Date(endDate);
  const diffTime = end - today;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return Math.max(0, diffDays);
};

// 获取剩余天数样式类
const getRemainingDaysClass = (endDate) => {
  const days = getRemainingDays(endDate);
  if (days <= 7) return 'danger';
  if (days <= 30) return 'warning';
  return '';
};

// 快速搜索房源
const quickSearch = () => {
  router.push('/user/browse-rooms');
};
</script>

<style scoped>
.user-dashboard {
  width: 100%;
}

.dashboard-container {
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
  padding: 30px 0;
}

.welcome-section {
  margin-bottom: 20px;
}

.animated-welcome {
  position: relative;
  overflow: hidden;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  animation: slideInDown 0.8s ease-out;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 2;
}

.welcome-text {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px 0;
  position: relative;
}

.typing-effect {
  overflow: hidden;
  border-right: 3px solid #667eea;
  white-space: nowrap;
  animation: typing 2s steps(20, end), blink-caret 0.75s step-end infinite;
}

.page-desc {
  color: #666;
  font-size: 18px;
  margin: 0;
  font-weight: 400;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out 0.5s forwards;
}

.welcome-actions {
  display: flex;
  gap: 16px;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out 1s forwards;
}

.quick-search-btn, .quick-bill-btn {
  padding: 12px 24px;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.quick-search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.quick-bill-btn {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  border: none;
  color: white;
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
}

.quick-bill-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(240, 147, 251, 0.4);
}

.welcome-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-icon {
  position: absolute;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.floating-icon.icon-1 {
  top: 20%;
  right: 10%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  animation-delay: 0s;
}

.floating-icon.icon-2 {
  top: 60%;
  right: 20%;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: white;
  animation-delay: 2s;
}

.floating-icon.icon-3 {
  top: 40%;
  right: 5%;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
  animation-delay: 4s;
}

/* 动画定义 */
@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  from { width: 0; }
  to { width: 100%; }
}

@keyframes blink-caret {
  from, to { border-color: transparent; }
  50% { border-color: #667eea; }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.quick-stats {
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

.stat-icon.rental {
  background: #409eff;
}

.stat-icon.unpaid {
  background: #e6a23c;
}

.stat-icon.utility {
  background: #67c23a;
}

.stat-icon.application {
  background: #909399;
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

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.action-icon.browse {
  background: #409eff;
}

.action-icon.pay {
  background: #f56c6c;
}

.action-icon.utility {
  background: #67c23a;
}

.action-icon.feedback {
  background: #e6a23c;
}

.action-info {
  flex: 1;
}

.action-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
}

.bills-list {
  min-height: 200px;
}

.bill-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bill-item {
  display: grid;
  grid-template-columns: auto 1fr auto auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.bill-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-icon {
  font-size: 16px;
}

.type-icon.rent {
  color: #409eff;
}

.type-icon.utility {
  color: #67c23a;
}

.type-text {
  font-weight: 500;
  color: #303133;
}

.bill-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.bill-period {
  font-size: 14px;
  color: #606266;
}

.bill-amount {
  font-weight: 600;
  color: #f56c6c;
}

.current-rental-card {
  margin-top: 20px;
}

.rental-summary {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
}

.rental-image {
  height: 120px;
}

.image {
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
  font-size: 24px;
  border-radius: 8px;
}

.rental-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rental-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.rental-address {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  margin: 0;
}

.rental-details {
  display: flex;
  gap: 20px;
}

.detail-item {
  display: flex;
  gap: 8px;
}

.detail-item .label {
  color: #909399;
}

.detail-item .value {
  color: #303133;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px 0;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .rental-summary {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .rental-image {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .quick-stats {
    grid-template-columns: 1fr;
  }
  
  .bill-item {
    grid-template-columns: 1fr;
    gap: 8px;
    text-align: center;
  }
  
  .rental-details {
    flex-direction: column;
    gap: 8px;
  }
}

.detail-item .value.warning {
  color: #e6a23c;
}

.detail-item .value.danger {
  color: #f56c6c;
}

.rental-actions {
  margin-top: auto;
}

@media (max-width: 768px) {
  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .rental-summary {
    grid-template-columns: 1fr;
  }
  
  .bill-item {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .quick-stats {
    grid-template-columns: 1fr;
  }
}
</style> 