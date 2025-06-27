<template>
  <div class="landlord-ratings">
    <div class="ratings-container">
      <div class="page-header">
        <h1 class="page-title">评价管理</h1>
        <p class="page-desc">查看和回复租户对您的评价</p>
      </div>

      <!-- 评价统计 -->
      <div class="rating-stats">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="overall-rating">
              <div class="rating-score">{{ overallRating }}</div>
              <div class="rating-stars">
                <el-rate
                  v-model="overallRating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                />
              </div>
              <div class="rating-label">总体评分</div>
            </div>
            
            <div class="rating-breakdown">
              <div v-for="(count, rating) in ratingBreakdown" :key="rating" class="breakdown-item">
                <div class="breakdown-stars">
                  <el-rate :model-value="rating" disabled size="small" />
                </div>
                <div class="breakdown-bar">
                  <div class="bar-bg">
                    <div 
                      class="bar-fill" 
                      :style="{ width: getPercentage(count) + '%' }"
                    ></div>
                  </div>
                </div>
                <div class="breakdown-count">{{ count }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 筛选条件 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-container">
          <el-select v-model="filterRating" placeholder="评分筛选" @change="handleFilter">
            <el-option label="全部评分" value="" />
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星" :value="2" />
            <el-option label="1星" :value="1" />
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
        </div>
      </el-card>

      <!-- 评价列表 -->
      <el-card class="ratings-list-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="header-title">评价列表</span>
            <span class="header-count">共 {{ total }} 条评价</span>
          </div>
        </template>

        <div class="ratings-list" v-loading="loading">
          <div v-if="ratingsList.length > 0" class="rating-items">
            <div v-for="rating in ratingsList" :key="rating.id" class="rating-item">
              <div class="rating-header">
                <div class="user-info">
                  <el-avatar :size="40" :src="getUserAvatar(rating.user)">
                    {{ rating.user?.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <div class="user-details">
                    <div class="username">{{ rating.user?.username }}</div>
                    <div class="property-title">{{ rating.property?.title }}</div>
                  </div>
                </div>
                
                <div class="rating-info">
                  <el-rate :model-value="rating.rating" disabled size="small" />
                  <div class="rating-time">{{ formatTime(rating.createTime) }}</div>
                </div>
              </div>
              
              <div class="rating-content">
                <div class="rating-comment">
                  <p>{{ rating.comment }}</p>
                </div>
                
                <div v-if="rating.reply" class="rating-reply">
                  <div class="reply-header">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>您的回复：</span>
                  </div>
                  <div class="reply-content">{{ rating.reply.content }}</div>
                  <div class="reply-time">{{ formatTime(rating.reply.createTime) }}</div>
                </div>
                
                <div v-else class="rating-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleReply(rating)"
                  >
                    回复评价
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="暂无评价记录" />
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

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复评价"
      width="600px"
      :before-close="handleCloseReply"
    >
      <div v-if="currentRating" class="reply-dialog-content">
        <div class="original-rating">
          <div class="rating-header">
            <div class="user-info">
              <el-avatar :size="32" :src="getUserAvatar(currentRating.user)">
                {{ currentRating.user?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ currentRating.user?.username }}</span>
            </div>
            <el-rate :model-value="currentRating.rating" disabled size="small" />
          </div>
          <div class="rating-comment">{{ currentRating.comment }}</div>
        </div>
        
        <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules">
          <el-form-item label="回复内容" prop="content">
            <el-input
              v-model="replyForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入您的回复内容..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseReply">取消</el-button>
          <el-button 
            type="primary" 
            :loading="replying"
            @click="handleSubmitReply"
          >
            发送回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  Search, Refresh, ChatDotRound 
} from '@element-plus/icons-vue';
import { 
  getLandlordRatings, 
  replyToRating 
} from '@/api/landlords';

// 响应式数据
const loading = ref(false);
const ratingsList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 筛选条件
const filterRating = ref('');
const dateRange = ref([]);

// 评价统计
const overallRating = ref(0);
const ratingBreakdown = ref({
  5: 0,
  4: 0,
  3: 0,
  2: 0,
  1: 0
});

// 回复相关
const replyDialogVisible = ref(false);
const currentRating = ref(null);
const replyFormRef = ref();
const replying = ref(false);
const replyForm = ref({
  content: ''
});

// 回复表单验证规则
const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 5, max: 500, message: '回复内容长度在5到500个字符', trigger: 'blur' }
  ]
};

// 计算总评价数
const totalRatings = computed(() => {
  return Object.values(ratingBreakdown.value).reduce((sum, count) => sum + count, 0);
});

// 获取百分比
const getPercentage = (count) => {
  if (totalRatings.value === 0) return 0;
  return (count / totalRatings.value) * 100;
};

// 获取用户头像
const getUserAvatar = (user) => {
  if (user && user.avatarUrl) {
    return user.avatarUrl;
  }
  return null;
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) return '今天';
  if (days === 1) return '昨天';
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 获取评价列表
const fetchRatings = async () => {
  loading.value = true;
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };
    
    if (filterRating.value) {
      params.rating = filterRating.value;
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }

    const response = await getLandlordRatings(params);
    if (response && response.code === 200) {
      ratingsList.value = response.data.content || [];
      total.value = response.data.pageable?.total || 0;
      
      // 计算评价统计
      calculateRatingStats();
    } else {
      ElMessage.error(response?.message || '获取评价列表失败');
    }
  } catch (error) {
    console.error('获取评价列表失败:', error);
    ElMessage.error('获取评价列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 计算评价统计
const calculateRatingStats = () => {
  const breakdown = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 };
  let totalScore = 0;
  let count = 0;
  
  ratingsList.value.forEach(rating => {
    breakdown[rating.rating]++;
    totalScore += rating.rating;
    count++;
  });
  
  ratingBreakdown.value = breakdown;
  overallRating.value = count > 0 ? (totalScore / count).toFixed(1) : 0;
};

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1;
  fetchRatings();
};

// 重置筛选
const handleReset = () => {
  filterRating.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  fetchRatings();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchRatings();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchRatings();
};

// 回复评价
const handleReply = (rating) => {
  currentRating.value = rating;
  replyForm.value.content = '';
  replyDialogVisible.value = true;
};

// 关闭回复对话框
const handleCloseReply = () => {
  replyDialogVisible.value = false;
  currentRating.value = null;
  replyForm.value.content = '';
};

// 提交回复
const handleSubmitReply = async () => {
  try {
    await replyFormRef.value.validate();
    
    replying.value = true;
    
    const response = await replyToRating(currentRating.value.id, {
      content: replyForm.value.content
    });
    
    if (response && response.code === 200) {
      ElMessage.success('回复成功');
      handleCloseReply();
      fetchRatings(); // 刷新列表
    } else {
      ElMessage.error(response?.message || '回复失败');
    }
  } catch (error) {
    console.error('回复评价失败:', error);
    ElMessage.error('回复失败，请稍后重试');
  } finally {
    replying.value = false;
  }
};

// 组件挂载时执行
onMounted(() => {
  fetchRatings();
});
</script>

<style lang="scss" scoped>
.landlord-ratings {
  .ratings-container {
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

  .rating-stats {
    margin-bottom: 30px;

    .stats-card {
      .stats-content {
        display: flex;
        align-items: center;
        padding: 30px;

        .overall-rating {
          text-align: center;
          margin-right: 50px;

          .rating-score {
            font-size: 48px;
            font-weight: 700;
            color: #ff9900;
            line-height: 1;
          }

          .rating-stars {
            margin: 10px 0;
          }

          .rating-label {
            font-size: 14px;
            color: #7f8c8d;
          }
        }

        .rating-breakdown {
          flex: 1;

          .breakdown-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;

            &:last-child {
              margin-bottom: 0;
            }

            .breakdown-stars {
              width: 100px;
            }

            .breakdown-bar {
              flex: 1;
              margin: 0 15px;

              .bar-bg {
                height: 8px;
                background: #f0f0f0;
                border-radius: 4px;
                overflow: hidden;

                .bar-fill {
                  height: 100%;
                  background: #ff9900;
                  transition: width 0.3s ease;
                }
              }
            }

            .breakdown-count {
              width: 30px;
              text-align: right;
              font-size: 14px;
              color: #666;
            }
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

  .ratings-list-card {
    .ratings-list {
      .rating-items {
        .rating-item {
          border: 1px solid #eee;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 20px;
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          &:last-child {
            margin-bottom: 0;
          }

          .rating-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;

            .user-info {
              display: flex;
              align-items: center;

              .user-details {
                margin-left: 12px;

                .username {
                  font-weight: 600;
                  color: #2c3e50;
                  margin-bottom: 5px;
                }

                .property-title {
                  font-size: 12px;
                  color: #7f8c8d;
                }
              }
            }

            .rating-info {
              text-align: right;

              .rating-time {
                font-size: 12px;
                color: #999;
                margin-top: 5px;
              }
            }
          }

          .rating-content {
            .rating-comment {
              background: #f8f9fa;
              padding: 15px;
              border-radius: 8px;
              margin-bottom: 15px;

              p {
                margin: 0;
                line-height: 1.6;
                color: #2c3e50;
              }
            }

            .rating-reply {
              background: #e3f2fd;
              padding: 15px;
              border-radius: 8px;
              border-left: 4px solid #2196f3;

              .reply-header {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
                font-weight: 600;
                color: #1976d2;

                .el-icon {
                  margin-right: 5px;
                }
              }

              .reply-content {
                color: #2c3e50;
                line-height: 1.6;
                margin-bottom: 10px;
              }

              .reply-time {
                font-size: 12px;
                color: #999;
              }
            }

            .rating-actions {
              text-align: right;
            }
          }
        }
      }
    }

    .pagination-container {
      margin-top: 30px;
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

.reply-dialog-content {
  .original-rating {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;

    .rating-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .user-info {
        display: flex;
        align-items: center;

        .username {
          margin-left: 10px;
          font-weight: 600;
          color: #2c3e50;
        }
      }
    }

    .rating-comment {
      color: #2c3e50;
      line-height: 1.6;
    }
  }
}
</style> 