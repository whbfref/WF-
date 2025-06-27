<template>
  <div class="dashboard-content">
    <div class="page-header">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>分析页</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="tabs">
        <el-button type="primary" plain size="small" class="active">分析页</el-button>
        <el-button type="info" plain size="small">工作台</el-button>
      </div>
    </div>
    
    <!-- 数据卡片区域 -->
    <el-row :gutter="20" class="data-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-content">
            <div class="card-icon users">
              <img src="@/assets/images/user-icon.svg" alt="用户" class="card-image" />
            </div>
            <div class="card-info">
              <div class="card-title">新增用户</div>
              <div class="card-value">{{ dashboardData.newUsers }}</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-content">
            <div class="card-icon apartments">
              <img src="@/assets/images/building-icon.svg" alt="公寓" class="card-image" />
            </div>
            <div class="card-info">
              <div class="card-title">物业数量</div>
              <div class="card-value">{{ dashboardData.propertyCount }}</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-content">
            <div class="card-icon revenue">
              <img src="@/assets/images/money-icon.svg" alt="金额" class="card-image" />
            </div>
            <div class="card-info">
              <div class="card-title">成交金额</div>
              <div class="card-value">{{ dashboardData.totalRevenue }}</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-content">
            <div class="card-icon rented">
              <img src="@/assets/images/key-icon.svg" alt="在租" class="card-image" />
            </div>
            <div class="card-info">
              <div class="card-title">在租数量</div>
              <div class="card-value">{{ dashboardData.rentedCount }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 在租数量分析区域 -->
    <el-row :gutter="20" class="analysis-container" v-if="dashboardData.rentedCount === 0">
      <el-col :span="24">
        <div class="analysis-card">
          <div class="analysis-header">
            <el-icon class="warning-icon"><Warning /></el-icon>
            <span class="analysis-title">在租数量为0的原因分析</span>
          </div>
          <div class="analysis-content">
            <div class="reason-list">
              <div class="reason-item" v-for="reason in rentAnalysisReasons" :key="reason.type">
                <div class="reason-icon" :class="reason.type">
                  <el-icon><component :is="reason.icon" /></el-icon>
                </div>
                <div class="reason-info">
                  <div class="reason-title">{{ reason.title }}</div>
                  <div class="reason-desc">{{ reason.description }}</div>
                  <div class="reason-suggestion">建议：{{ reason.suggestion }}</div>
                </div>
                <div class="reason-status" :class="reason.severity">
                  {{ reason.severity === 'high' ? '高' : reason.severity === 'medium' ? '中' : '低' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-container">
      <el-col :xs="24" :sm="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">用户访问来源</span>
          </div>
          <div class="chart-content">
            <div ref="sourcePieChart" class="chart"></div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">每月租金变化</span>
          </div>
          <div class="chart-content">
            <div ref="weeklyActivityChart" class="chart"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 年度租金变化折线图 -->
    <el-row :gutter="20" class="charts-container">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">每年租金变化趋势</span>
          </div>
          <div class="chart-content">
            <div ref="yearlyRentChart" class="chart"></div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getDashboardData, getVisitSourceData, getWeeklyActivityData, getYearlyRentData } from '@/api/dashboard';
import { Warning, House, Money, User, Tools, Star } from '@element-plus/icons-vue';

// 仪表盘数据
const dashboardData = ref({
  newUsers: 2,
  propertyCount: 4,
  totalRevenue: 9800,
  rentedCount: 2
});

// 访问来源数据
const visitSourceData = ref([]);

// 每周活跃量数据
const weeklyActivityData = ref({
  days: [],
  data: []
});

// 每年租金变化数据
const yearlyRentData = ref({
  years: [],
  actual: [],
  predicted: []
});

// 在租数量分析原因
const rentAnalysisReasons = ref([
  {
    type: 'property',
    icon: House,
    title: '房源数量不足',
    description: '当前系统中房源数量较少，缺乏足够的房屋供租赁',
    suggestion: '增加房源录入，联系更多房主',
    severity: 'high'
  },
  {
    type: 'price',
    icon: Money,
    title: '价格竞争力不足',
    description: '房源价格可能过高，不符合当前市场预期',
    suggestion: '调研市场价格，调整租金定价策略',
    severity: 'medium'
  },
  {
    type: 'condition',
    icon: Tools,
    title: '房屋条件待改善',
    description: '房屋可能处于维修状态或条件不佳',
    suggestion: '完善房屋设施，提升居住体验',
    severity: 'medium'
  },
  {
    type: 'marketing',
    icon: Star,
    title: '推广宣传不足',
    description: '房源曝光度较低，潜在租客不了解',
    suggestion: '加强线上线下推广，提高房源知名度',
    severity: 'low'
  },
  {
    type: 'service',
    icon: User,
    title: '服务体验待优化',
    description: '租赁流程或服务质量可能存在问题',
    suggestion: '优化租赁流程，提升客户服务体验',
    severity: 'low'
  }
]);

// 图表引用
const sourcePieChart = ref(null);
const weeklyActivityChart = ref(null);
const yearlyRentChart = ref(null);

// 加载中状态
const loading = ref(false);

// 初始化图表
onMounted(async () => {
  // 获取仪表盘数据
  await fetchDashboardData();
  
  // 初始化图表
  await Promise.all([
    fetchVisitSourceData(),
    fetchWeeklyActivityData(),
    fetchYearlyRentData()
  ]);
  
  // 响应式处理
  window.addEventListener('resize', () => {
    if (sourcePieChart.value && weeklyActivityChart.value && yearlyRentChart.value) {
      echarts.getInstanceByDom(sourcePieChart.value)?.resize();
      echarts.getInstanceByDom(weeklyActivityChart.value)?.resize();
      echarts.getInstanceByDom(yearlyRentChart.value)?.resize();
    }
  });
});

// 获取仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true;
  try {
    // 直接使用dashboard API，不再尝试管理员API
    const response = await getDashboardData();
    if (response && response.code === 200 && response.data) {
      dashboardData.value = response.data;
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    // 使用默认数据
    dashboardData.value = {
      newUsers: 0,
      propertyCount: 0,
      totalRevenue: 0,
      rentedCount: 0
    };
  } finally {
    loading.value = false;
  }
};

// 获取访问来源数据
const fetchVisitSourceData = async () => {
  try {
    const response = await getVisitSourceData();
    if (response && response.code === 200 && response.data) {
      // 注意：后端返回的是 {sourceData: [...]} 格式
      visitSourceData.value = response.data.sourceData || [];
      initSourcePieChart();
    }
  } catch (error) {
    console.error('获取访问来源数据失败:', error);
    // 使用默认数据初始化图表
    initSourcePieChart();
  }
};

// 获取每周活跃量数据
const fetchWeeklyActivityData = async () => {
  try {
    const response = await getWeeklyActivityData();
    if (response && response.code === 200 && response.data) {
      // 注意：后端返回的是 {months: [...], amounts: [...]} 格式
      // 转换为前端使用的 {days: [...], data: [...]} 格式
      weeklyActivityData.value = {
        days: response.data.months || [],
        data: response.data.amounts || []
      };
      initWeeklyActivityChart();
    }
  } catch (error) {
    console.error('获取每周活跃量数据失败:', error);
    // 使用默认数据初始化图表
    initWeeklyActivityChart();
  }
};

// 获取每年租金变化数据
const fetchYearlyRentData = async () => {
  try {
    const response = await getYearlyRentData();
    if (response && response.code === 200 && response.data) {
      // 注意：后端返回的是 {months: [...], actual: [...], predicted: [...]} 格式
      // 转换为前端使用的 {years: [...], actual: [...], predicted: [...]} 格式
      yearlyRentData.value = {
        years: response.data.months || [],
        actual: response.data.actual || [],
        predicted: response.data.predicted || []
      };
      initYearlyRentChart();
    }
  } catch (error) {
    console.error('获取每年租金变化数据失败:', error);
    // 使用默认数据初始化图表
    initYearlyRentChart();
  }
};

// 初始化来源饼图
const initSourcePieChart = () => {
  if (!sourcePieChart.value) return;
  
  const chart = echarts.init(sourcePieChart.value);
  
  // 如果有真实数据，使用真实数据
  const seriesData = visitSourceData.value.length > 0 
    ? visitSourceData.value 
    : [
        { value: 335, name: '小红书' },
        { value: 310, name: '抖音' },
        { value: 234, name: '58同城' },
        { value: 135, name: '自如网站' },
        { value: 1548, name: '搜索引擎' }
      ];
  
  const legendData = seriesData.map(item => item.name);
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: legendData
    },
    color: ['#6366f1', '#84cc16', '#f97316', '#ef4444', '#38bdf8'],
    series: [
      {
        name: '访问来源',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: seriesData
      }
    ]
  };
  chart.setOption(option);
};

// 初始化月租金变化柱状图
const initWeeklyActivityChart = () => {
  if (!weeklyActivityChart.value) return;
  
  const chart = echarts.init(weeklyActivityChart.value);
  
  // 如果有真实数据，使用真实数据
  const xAxisData = weeklyActivityData.value.days && weeklyActivityData.value.days.length > 0 
    ? weeklyActivityData.value.days 
    : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
    
  const seriesData = weeklyActivityData.value.data && weeklyActivityData.value.data.length > 0 
    ? weeklyActivityData.value.data 
    : [2500, 2700, 3000, 3200, 3500, 3700, 3600, 3400, 3200, 3000, 2800, 2600];
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '8%',
      right: '6%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: xAxisData,
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          interval: 0,
          rotate: 0,
          fontSize: 12,
          margin: 8,
          overflow: 'truncate',
          width: 40
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '租金 (元)',
        nameTextStyle: {
          fontSize: 12
        },
        axisLabel: {
          fontSize: 10
        }
      }
    ],
    series: [
      {
        name: '月租金',
        type: 'bar',
        barWidth: '60%',
        data: seriesData,
        itemStyle: {
          color: '#6366f1'
        }
      }
    ]
  };
  
  chart.setOption(option, true);
  
  // 监听容器大小变化
  const resizeObserver = new ResizeObserver(() => {
    chart.resize();
  });
  resizeObserver.observe(weeklyActivityChart.value);
};

// 初始化年度租金变化折线图
const initYearlyRentChart = () => {
  if (!yearlyRentChart.value) return;
  
  const chart = echarts.init(yearlyRentChart.value);
  
  // 如果有真实数据，使用真实数据
  const xAxisData = yearlyRentData.value.years && yearlyRentData.value.years.length > 0 
    ? yearlyRentData.value.years 
    : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
    
  const actualData = yearlyRentData.value.actual && yearlyRentData.value.actual.length > 0 
    ? yearlyRentData.value.actual 
    : [120, 80, 90, 150, 160, 140, 145, 250, 110, 60, 100, 120];
    
  const predictedData = yearlyRentData.value.predicted && yearlyRentData.value.predicted.length > 0 
    ? yearlyRentData.value.predicted 
    : [100, 120, 160, 130, 105, 160, 165, 110, 165, 185, 115, 120];
  
  const option = {
    title: {
      text: '每年租金变化趋势',
      textStyle: {
        fontWeight: 'normal',
        fontSize: 14
      },
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['预计', '实际'],
      top: 30,
      textStyle: {
        fontSize: 12
      }
    },
    grid: {
      left: '8%',
      right: '6%',
      bottom: '12%',
      top: 70,
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: xAxisData,
        axisLabel: {
          fontSize: 10,
          interval: 0,
          rotate: 0,
          margin: 8,
          overflow: 'truncate',
          width: 40
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '租金 (元)',
        nameTextStyle: {
          fontSize: 12
        },
        axisLabel: {
          fontSize: 10
        }
      }
    ],
    series: [
      {
        name: '预计',
        type: 'line',
        stack: '总量',
        smooth: true,
        lineStyle: {
          width: 2
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#5470c6'
        },
        emphasis: {
          focus: 'series'
        },
        data: predictedData
      },
      {
        name: '实际',
        type: 'line',
        stack: '总量',
        smooth: true,
        lineStyle: {
          width: 2
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#91cc75'
        },
        emphasis: {
          focus: 'series'
        },
        data: actualData
      }
    ]
  };
  
  chart.setOption(option, true);
  
  // 监听容器大小变化
  const resizeObserver = new ResizeObserver(() => {
    chart.resize();
  });
  resizeObserver.observe(yearlyRentChart.value);
};
</script>

<style lang="scss" scoped>
.dashboard-content {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .tabs {
      .active {
        background-color: #409eff;
        color: white;
      }
    }
  }
  
  .data-cards {
    margin-bottom: 20px;
    
    .data-card {
      background-color: #fff;
      border-radius: 4px;
      padding: 20px;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      height: 120px;
      margin-bottom: 20px;
      transition: all 0.3s;
      
      .card-content {
        display: flex;
        align-items: center;
        height: 100%;
        
        .card-icon {
          width: 64px;
          height: 64px;
          border-radius: 8px;
          display: flex;
          justify-content: center;
          align-items: center;
          margin-right: 20px;
          transition: all 0.3s;
          background-color: transparent;
          
          .card-image {
            width: 40px;
            height: 40px;
            object-fit: contain;
          }
        }
        
        .card-info {
          flex: 1;
          
          .card-title {
            font-size: 16px;
            color: #909399;
            margin-bottom: 5px;
          }
          
          .card-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
          }
        }
      }
      
      &:hover {
        .card-icon {
          &.users {
            background-color: rgba(74, 222, 128, 0.3);
          }
          
          &.apartments {
            background-color: rgba(56, 189, 248, 0.3);
          }
          
          &.revenue {
            background-color: rgba(248, 113, 113, 0.3);
          }
          
          &.rented {
            background-color: rgba(45, 212, 191, 0.3);
          }
          
          transform: scale(1.05);
          box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
  
  .analysis-container {
    margin-bottom: 20px;
    
    .analysis-card {
      background-color: #fff;
      border-radius: 4px;
      padding: 20px;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      
      .analysis-header {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        
        .warning-icon {
          color: #f56c6c;
          margin-right: 10px;
        }
        
        .analysis-title {
          font-size: 16px;
          font-weight: 500;
        }
      }
      
      .analysis-content {
        .reason-list {
          .reason-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            
            .reason-icon {
              width: 32px;
              height: 32px;
              border-radius: 4px;
              display: flex;
              justify-content: center;
              align-items: center;
              margin-right: 10px;
              transition: all 0.3s;
              background-color: transparent;
              
              .el-icon {
                font-size: 16px;
                color: #303133;
              }
            }
            
            .reason-info {
              flex: 1;
              
              .reason-title {
                font-size: 14px;
                font-weight: 500;
                margin-bottom: 5px;
              }
              
              .reason-desc {
                font-size: 12px;
                color: #606266;
              }
              
              .reason-suggestion {
                font-size: 12px;
                color: #909399;
              }
            }
            
            .reason-status {
              font-size: 12px;
              font-weight: 500;
              padding: 2px 8px;
              border-radius: 4px;
              color: #fff;
              
              &.high {
                background-color: #f56c6c;
              }
              
              &.medium {
                background-color: #e6a23c;
              }
              
              &.low {
                background-color: #909399;
              }
            }
          }
        }
      }
    }
  }
  
  .charts-container {
    .chart-card {
      background-color: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      margin-bottom: 20px;
      
      .chart-header {
        padding: 16px;
        border-bottom: 1px solid #ebeef5;
        
        .chart-title {
          font-size: 16px;
          font-weight: 500;
        }
      }
      
      .chart-content {
        padding: 16px;
        
        .chart {
          height: 300px;
          width: 100%;
          min-height: 250px;
          overflow: hidden;
        }
      }
    }
  }
}

// 响应式样式
@media (max-width: 768px) {
  .dashboard-content {
    .charts-container {
      .chart-card {
        .chart-content {
          .chart {
            height: 250px;
            min-height: 200px;
          }
        }
      }
    }
    
    .data-cards {
      .data-card {
        height: auto;
        min-height: 100px;
        
        .card-content {
          .card-info {
            .card-value {
              font-size: 24px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .dashboard-content {
    .charts-container {
      .chart-card {
        .chart-content {
          padding: 8px;
          
          .chart {
            height: 200px;
            min-height: 180px;
          }
        }
      }
    }
  }
}
</style> 