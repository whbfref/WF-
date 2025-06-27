<template>
  <div class="notification-center">
    <el-popover
      placement="bottom"
      :width="400"
      trigger="click"
      popper-class="notification-popover"
    >
      <template #reference>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
          <el-button type="text" class="notification-btn">
            <el-icon size="20"><Bell /></el-icon>
          </el-button>
        </el-badge>
      </template>

      <div class="notification-content">
        <div class="notification-header">
          <span class="header-title">通知消息</span>
          <el-button 
            v-if="unreadCount > 0"
            type="text" 
            size="small" 
            @click="markAllAsRead"
          >
            全部已读
          </el-button>
        </div>

        <div class="notification-list" v-loading="loading">
          <div v-if="notifications.length > 0" class="notification-items">
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              class="notification-item"
              :class="{ unread: !notification.isRead }"
              @click="handleNotificationClick(notification)"
            >
              <div class="notification-icon">
                <el-icon :class="getNotificationIconClass(notification.type)">
                  <component :is="getNotificationIcon(notification.type)" />
                </el-icon>
              </div>
              
              <div class="notification-body">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-content-text">{{ notification.content }}</div>
                <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
              </div>
              
              <div v-if="!notification.isRead" class="unread-dot"></div>
            </div>
          </div>
          
          <el-empty v-else description="暂无通知消息" :image-size="80" />
        </div>

        <div class="notification-footer">
          <el-button type="text" @click="viewAllNotifications">查看全部</el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  Bell, Document, Money, Warning, ChatDotRound, House 
} from '@element-plus/icons-vue';
import { 
  getUserNotifications, 
  markNotificationAsRead, 
  markAllNotificationsAsRead,
  getUnreadNotificationCount 
} from '@/api/notifications';

const router = useRouter();

// 响应式数据
const loading = ref(false);
const notifications = ref([]);
const unreadCount = ref(0);

// 获取通知图标
const getNotificationIcon = (type) => {
  switch (type) {
    case 'ROOM_APPLICATION':
      return House;
    case 'CONTRACT':
      return Document;
    case 'PAYMENT':
      return Money;
    case 'MAINTENANCE':
      return Warning;
    case 'RATING':
      return ChatDotRound;
    default:
      return Bell;
  }
};

// 获取通知图标样式类
const getNotificationIconClass = (type) => {
  switch (type) {
    case 'ROOM_APPLICATION':
      return 'application-icon';
    case 'CONTRACT':
      return 'contract-icon';
    case 'PAYMENT':
      return 'payment-icon';
    case 'MAINTENANCE':
      return 'maintenance-icon';
    case 'RATING':
      return 'rating-icon';
    default:
      return 'default-icon';
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (hours < 1) return '刚刚';
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 获取通知列表
const fetchNotifications = async () => {
  loading.value = true;
  
  try {
    const response = await getUserNotifications({
      page: 1,
      size: 10,
      isRead: null // 获取所有通知
    });
    
    if (response && response.code === 200) {
      notifications.value = response.data.content || [];
    }
  } catch (error) {
    console.error('获取通知失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取未读通知数量
const fetchUnreadCount = async () => {
  try {
    const response = await getUnreadNotificationCount();
    
    if (response && response.code === 200) {
      unreadCount.value = response.data || 0;
    }
  } catch (error) {
    console.error('获取未读通知数量失败:', error);
  }
};

// 处理通知点击
const handleNotificationClick = async (notification) => {
  try {
    // 标记为已读
    if (!notification.isRead) {
      await markNotificationAsRead(notification.id);
      notification.isRead = true;
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    }
    
    // 跳转到相关页面
    if (notification.route) {
      router.push(notification.route);
    }
  } catch (error) {
    console.error('处理通知点击失败:', error);
    ElMessage.error('操作失败');
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    await markAllNotificationsAsRead();
    
    // 更新本地状态
    notifications.value.forEach(notification => {
      notification.isRead = true;
    });
    unreadCount.value = 0;
    
    ElMessage.success('已标记所有通知为已读');
  } catch (error) {
    console.error('标记所有通知为已读失败:', error);
    ElMessage.error('操作失败');
  }
};

// 查看全部通知
const viewAllNotifications = () => {
  router.push('/notifications');
};

// 组件挂载时执行
onMounted(() => {
  fetchNotifications();
  fetchUnreadCount();
  
  // 定时刷新通知
  setInterval(() => {
    fetchUnreadCount();
  }, 30000); // 每30秒刷新一次
});
</script>

<style lang="scss" scoped>
.notification-center {
  .notification-badge {
    .notification-btn {
      padding: 8px;
      color: #606266;
      
      &:hover {
        color: #409eff;
      }
    }
  }
}

.notification-content {
  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 12px;

    .header-title {
      font-weight: 600;
      color: #303133;
    }
  }

  .notification-list {
    max-height: 400px;
    overflow-y: auto;

    .notification-items {
      .notification-item {
        display: flex;
        align-items: flex-start;
        padding: 12px 8px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;

        &:hover {
          background-color: #f5f7fa;
        }

        &.unread {
          background-color: #f0f9ff;
          
          .notification-title {
            font-weight: 600;
          }
        }

        .notification-icon {
          flex-shrink: 0;
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          .el-icon {
            font-size: 16px;
          }

          &.application-icon {
            background-color: #e1f3d8;
            color: #67c23a;
          }

          &.contract-icon {
            background-color: #fdf6ec;
            color: #e6a23c;
          }

          &.payment-icon {
            background-color: #fef0f0;
            color: #f56c6c;
          }

          &.maintenance-icon {
            background-color: #f4f4f5;
            color: #909399;
          }

          &.rating-icon {
            background-color: #ecf5ff;
            color: #409eff;
          }

          &.default-icon {
            background-color: #f4f4f5;
            color: #909399;
          }
        }

        .notification-body {
          flex: 1;
          min-width: 0;

          .notification-title {
            font-size: 14px;
            color: #303133;
            margin-bottom: 4px;
            line-height: 1.4;
          }

          .notification-content-text {
            font-size: 12px;
            color: #606266;
            margin-bottom: 4px;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .notification-time {
            font-size: 11px;
            color: #c0c4cc;
          }
        }

        .unread-dot {
          position: absolute;
          top: 12px;
          right: 8px;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background-color: #f56c6c;
        }
      }
    }
  }

  .notification-footer {
    text-align: center;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
    margin-top: 12px;
  }
}
</style>

<style lang="scss">
.notification-popover {
  padding: 16px !important;
}
</style> 