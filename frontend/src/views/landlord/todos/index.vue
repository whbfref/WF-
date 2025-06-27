<template>
  <div class="landlord-todos">
    <div class="todos-container">
      <div class="page-header">
        <h1 class="page-title">待办事项</h1>
        <p class="page-desc">管理您的待办任务和提醒</p>
      </div>

      <!-- 待办统计 -->
      <div class="todo-stats">
        <el-card class="stat-card pending" shadow="hover" @click="filterByStatus('PENDING')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todoStats.pending || 0 }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card processing" shadow="hover" @click="filterByStatus('PROCESSING')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todoStats.processing || 0 }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card done" shadow="hover" @click="filterByStatus('DONE')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todoStats.done || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card ignored" shadow="hover" @click="filterByStatus('IGNORED')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todoStats.ignored || 0 }}</div>
              <div class="stat-label">已忽略</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-container">
          <el-select v-model="filterType" placeholder="任务类型" @change="handleFilter">
            <el-option label="全部类型" value="" />
            <el-option label="合同审核" value="CONTRACT_REVIEW" />
            <el-option label="租金收取" value="RENT_COLLECTION" />
            <el-option label="维修处理" value="MAINTENANCE" />
            <el-option label="评价回复" value="RATING_REPLY" />
            <el-option label="租房申请" value="ROOM_APPLICATION" />
          </el-select>
          
          <el-select v-model="filterStatus" placeholder="任务状态" @change="handleFilter">
            <el-option label="全部状态" value="" />
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="DONE" />
            <el-option label="已忽略" value="IGNORED" />
          </el-select>
          
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            筛选
          </el-button>
          
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </el-card>

      <!-- 待办事项列表 -->
      <el-card class="todos-list-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">待办列表</span>
            <span class="header-count">共 {{ total }} 项任务</span>
          </div>
        </template>

        <div class="todos-list" v-loading="loading">
          <div v-if="todosList.length > 0" class="todo-items">
            <div 
              v-for="todo in todosList" 
              :key="todo.id" 
              class="todo-item"
              :class="{ 
                urgent: isUrgent(todo),
                completed: todo.status === 'DONE',
                ignored: todo.status === 'IGNORED'
              }"
            >
              <div class="todo-header">
                <div class="todo-type">
                  <el-icon class="type-icon" :class="getTodoTypeClass(todo.type)">
                    <component :is="getTodoTypeIcon(todo.type)" />
                  </el-icon>
                  <span class="type-text">{{ getTodoTypeText(todo.type) }}</span>
                </div>
                
                <div class="todo-status">
                  <el-tag :type="getStatusTagType(todo.status)" size="small">
                    {{ getStatusText(todo.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="todo-content">
                <div class="todo-title">{{ todo.title }}</div>
                <div class="todo-description">{{ todo.description }}</div>
                
                <div class="todo-meta">
                  <div class="todo-time">
                    <el-icon><Clock /></el-icon>
                    <span>{{ formatTime(todo.createTime) }}</span>
                  </div>
                  <div v-if="todo.relatedId" class="todo-related">
                    <el-icon><Link /></el-icon>
                    <span>关联ID: {{ todo.relatedId }}</span>
                  </div>
                </div>
              </div>
              
              <div class="todo-actions">
                <el-button 
                  v-if="todo.status === 'PENDING'"
                  type="primary" 
                  size="small"
                  @click="handleProcess(todo)"
                >
                  开始处理
                </el-button>
                
                <el-button 
                  v-if="todo.status === 'PROCESSING'"
                  type="success" 
                  size="small"
                  @click="handleComplete(todo)"
                >
                  标记完成
                </el-button>
                
                <el-button 
                  v-if="['PENDING', 'PROCESSING'].includes(todo.status)"
                  type="warning" 
                  size="small"
                  @click="handleIgnore(todo)"
                >
                  忽略
                </el-button>
                
                <el-button 
                  v-if="todo.type === 'CONTRACT_REVIEW'"
                  type="info" 
                  size="small"
                  @click="handleViewContract(todo)"
                >
                  查看详情
                </el-button>
                
                <el-button 
                  v-if="todo.type === 'RATING_REPLY'"
                  type="info" 
                  size="small"
                  @click="handleViewRating(todo)"
                >
                  查看评价
                </el-button>
                
                <el-button 
                  v-if="todo.type === 'ROOM_APPLICATION'"
                  type="primary" 
                  size="small"
                  @click="handleViewApplication(todo)"
                >
                  处理申请
                </el-button>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="暂无待办事项" />
        </div>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-container">
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
      </el-card>
    </div>

    <!-- 处理对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      title="处理待办事项"
      width="600px"
      :before-close="handleCloseProcess"
    >
      <div v-if="currentTodo" class="process-dialog-content">
        <div class="todo-info">
          <h4>{{ currentTodo.title }}</h4>
          <p>{{ currentTodo.description }}</p>
        </div>
        
        <el-form ref="processFormRef" :model="processForm" :rules="processRules">
          <el-form-item label="处理状态" prop="status">
            <el-radio-group v-model="processForm.status">
              <el-radio label="PROCESSING">开始处理</el-radio>
              <el-radio label="DONE">标记完成</el-radio>
              <el-radio label="IGNORED">忽略任务</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="处理备注" prop="comment">
            <el-input
              v-model="processForm.comment"
              type="textarea"
              :rows="3"
              placeholder="请输入处理备注（可选）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseProcess">取消</el-button>
          <el-button 
            type="primary" 
            :loading="processing"
            @click="handleSubmitProcess"
          >
            确认处理
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Clock, Loading, CircleCheck, Close, Search, Refresh,
  Document, Money, Warning, ChatDotRound, Link, Bell
} from '@element-plus/icons-vue';
import { 
  getLandlordTodos, 
  updateTodoStatus 
} from '@/api/landlords';

const router = useRouter();

// 响应式数据
const loading = ref(false);
const todosList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 筛选条件
const filterType = ref('');
const filterStatus = ref('');

// 待办统计
const todoStats = ref({
  pending: 0,
  processing: 0,
  done: 0,
  ignored: 0
});

// 处理对话框
const processDialogVisible = ref(false);
const currentTodo = ref(null);
const processFormRef = ref();
const processing = ref(false);
const processForm = ref({
  status: '',
  comment: ''
});

// 处理表单验证规则
const processRules = {
  status: [
    { required: true, message: '请选择处理状态', trigger: 'change' }
  ]
};

// 判断是否紧急
const isUrgent = (todo) => {
  const createTime = new Date(todo.createTime);
  const now = new Date();
  const diffHours = (now - createTime) / (1000 * 60 * 60);
  
  // 超过24小时未处理的任务标记为紧急
  return diffHours > 24 && ['PENDING', 'PROCESSING'].includes(todo.status);
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  
  if (hours < 1) return '刚刚';
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 获取待办事项类型图标
const getTodoTypeIcon = (type) => {
  switch (type) {
    case 'CONTRACT_REVIEW':
      return Document;
    case 'RENT_COLLECTION':
      return Money;
    case 'MAINTENANCE':
      return Warning;
    case 'RATING_REPLY':
      return ChatDotRound;
    case 'ROOM_APPLICATION':
      return Bell;
    default:
      return Bell;
  }
};

// 获取待办事项类型样式类
const getTodoTypeClass = (type) => {
  switch (type) {
    case 'CONTRACT_REVIEW':
      return 'contract';
    case 'RENT_COLLECTION':
      return 'rent';
    case 'MAINTENANCE':
      return 'maintenance';
    case 'RATING_REPLY':
      return 'rating';
    case 'ROOM_APPLICATION':
      return 'application';
    default:
      return 'default';
  }
};

// 获取待办事项类型文本
const getTodoTypeText = (type) => {
  switch (type) {
    case 'CONTRACT_REVIEW':
      return '合同审核';
    case 'RENT_COLLECTION':
      return '租金收取';
    case 'MAINTENANCE':
      return '维修处理';
    case 'RATING_REPLY':
      return '评价回复';
    case 'ROOM_APPLICATION':
      return '租房申请';
    default:
      return '其他';
  }
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'PROCESSING':
      return 'primary';
    case 'DONE':
      return 'success';
    case 'IGNORED':
      return 'info';
    default:
      return '';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '待处理';
    case 'PROCESSING':
      return '处理中';
    case 'DONE':
      return '已完成';
    case 'IGNORED':
      return '已忽略';
    default:
      return '未知';
  }
};

// 按状态筛选
const filterByStatus = (status) => {
  filterStatus.value = status;
  handleFilter();
};

// 获取待办事项列表
const fetchTodos = async () => {
  loading.value = true;
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };
    
    if (filterType.value) {
      params.type = filterType.value;
    }
    
    if (filterStatus.value) {
      params.status = filterStatus.value;
    }

    const response = await getLandlordTodos(params);
    if (response && response.code === 200) {
      todosList.value = response.data.content || [];
      total.value = response.data.pageable?.total || 0;
      
      // 计算统计数据
      calculateStats();
    } else {
      ElMessage.error(response?.message || '获取待办事项失败');
    }
  } catch (error) {
    console.error('获取待办事项失败:', error);
    ElMessage.error('获取待办事项失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 计算统计数据
const calculateStats = () => {
  const stats = {
    pending: 0,
    processing: 0,
    done: 0,
    ignored: 0
  };
  
  todosList.value.forEach(todo => {
    switch (todo.status) {
      case 'PENDING':
        stats.pending++;
        break;
      case 'PROCESSING':
        stats.processing++;
        break;
      case 'DONE':
        stats.done++;
        break;
      case 'IGNORED':
        stats.ignored++;
        break;
    }
  });
  
  todoStats.value = stats;
};

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1;
  fetchTodos();
};

// 重置筛选
const handleReset = () => {
  filterType.value = '';
  filterStatus.value = '';
  currentPage.value = 1;
  fetchTodos();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchTodos();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchTodos();
};

// 开始处理
const handleProcess = (todo) => {
  currentTodo.value = todo;
  processForm.value = {
    status: 'PROCESSING',
    comment: ''
  };
  processDialogVisible.value = true;
};

// 标记完成
const handleComplete = (todo) => {
  currentTodo.value = todo;
  processForm.value = {
    status: 'DONE',
    comment: ''
  };
  processDialogVisible.value = true;
};

// 忽略任务
const handleIgnore = (todo) => {
  ElMessageBox.confirm(
    '确定要忽略这个任务吗？',
    '确认忽略',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    updateTodo(todo.id, 'IGNORED', '用户选择忽略');
  });
};

// 查看合同详情
const handleViewContract = (todo) => {
  router.push(`/landlord/contracts/${todo.relatedId}`);
};

// 查看评价详情
const handleViewRating = (todo) => {
  router.push(`/landlord/ratings?id=${todo.relatedId}`);
};

// 查看租房申请详情
const handleViewApplication = (todo) => {
  router.push(`/landlord/applications/${todo.relatedId}`);
};

// 关闭处理对话框
const handleCloseProcess = () => {
  processDialogVisible.value = false;
  currentTodo.value = null;
  processForm.value = {
    status: '',
    comment: ''
  };
};

// 提交处理
const handleSubmitProcess = async () => {
  try {
    await processFormRef.value.validate();
    
    processing.value = true;
    
    await updateTodo(
      currentTodo.value.id, 
      processForm.value.status, 
      processForm.value.comment
    );
    
    handleCloseProcess();
  } catch (error) {
    console.log('表单验证失败:', error);
  } finally {
    processing.value = false;
  }
};

// 更新待办事项
const updateTodo = async (todoId, status, comment) => {
  try {
    const response = await updateTodoStatus(todoId, {
      status,
      comment
    });
    
    if (response && response.code === 200) {
      ElMessage.success('待办事项更新成功');
      fetchTodos(); // 刷新列表
    } else {
      ElMessage.error(response?.message || '更新失败');
    }
  } catch (error) {
    console.error('更新待办事项失败:', error);
    ElMessage.error('更新失败，请稍后重试');
  }
};

// 组件挂载时执行
onMounted(() => {
  fetchTodos();
});
</script>

<style lang="scss" scoped>
.landlord-todos {
  .todos-container {
    max-width: 1200px;
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

  .todo-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;

    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
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
          width: 50px;
          height: 50px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          margin-right: 15px;
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1;
          }

          .stat-label {
            font-size: 12px;
            color: #7f8c8d;
            margin-top: 5px;
          }
        }
      }

      &.pending .stat-icon {
        background: linear-gradient(135deg, #feca57, #ff9ff3);
        color: white;
      }

      &.processing .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
      }

      &.done .stat-icon {
        background: linear-gradient(135deg, #11998e, #38ef7d);
        color: white;
      }

      &.ignored .stat-icon {
        background: linear-gradient(135deg, #95a5a6, #7f8c8d);
        color: white;
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

  .todos-list-card {
    .todos-list {
      .todo-items {
        .todo-item {
          border: 1px solid #eee;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 15px;
          transition: all 0.3s ease;
          position: relative;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          &.urgent {
            border-left: 4px solid #e74c3c;
            background: #fdf2f2;
          }

          &.completed {
            opacity: 0.7;
            background: #f8f9fa;
          }

          &.ignored {
            opacity: 0.5;
            background: #f8f9fa;
          }

          &:last-child {
            margin-bottom: 0;
          }

          .todo-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;

            .todo-type {
              display: flex;
              align-items: center;

              .type-icon {
                width: 32px;
                height: 32px;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 10px;

                &.contract {
                  background: #e3f2fd;
                  color: #1976d2;
                }

                &.rent {
                  background: #f3e5f5;
                  color: #7b1fa2;
                }

                &.maintenance {
                  background: #fff3e0;
                  color: #f57c00;
                }

                &.rating {
                  background: #e8f5e8;
                  color: #388e3c;
                }
              }

              .type-text {
                font-weight: 600;
                color: #2c3e50;
              }
            }
          }

          .todo-content {
            margin-bottom: 15px;

            .todo-title {
              font-size: 16px;
              font-weight: 600;
              color: #2c3e50;
              margin-bottom: 8px;
            }

            .todo-description {
              font-size: 14px;
              color: #7f8c8d;
              line-height: 1.5;
              margin-bottom: 10px;
            }

            .todo-meta {
              display: flex;
              gap: 20px;
              font-size: 12px;
              color: #999;

              .todo-time,
              .todo-related {
                display: flex;
                align-items: center;
                gap: 5px;
              }
            }
          }

          .todo-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
          }
        }
      }
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

.process-dialog-content {
  .todo-info {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;

    h4 {
      margin: 0 0 10px 0;
      color: #2c3e50;
    }

    p {
      margin: 0;
      color: #7f8c8d;
      line-height: 1.5;
    }
  }
}
</style> 