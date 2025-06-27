<template>
  <div class="app-layout">
    <el-container>
      <el-header height="60px">
        <div class="header-content flex-between">
          <div class="logo-container flex-center">
            <img 
              :src="logoUrl || defaultLogo" 
              alt="Logo" 
              class="logo"
              @error="handleLogoError"
            >
            <h2 class="title">公寓管理系统</h2>
          </div>
          <div class="user-info flex-center">
            <el-dropdown trigger="click">
              <div class="avatar-container flex-center">
                <el-avatar :size="32" :src="userInfo?.avatarUrl || defaultAvatar">
                  {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username ml-10">{{ userInfo?.username || '用户' }}</span>
                <el-icon class="ml-5"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="viewProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-container>
        <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
          <div class="collapse-btn" @click="toggleSidebar">
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409eff"
            :collapse="isCollapse"
            router
          >
            <el-menu-item :index="userStore.role === 'USER' ? '/user/dashboard' : '/dashboard'">
              <el-icon><HomeFilled /></el-icon>
              <template #title>首页</template>
            </el-menu-item>
            
            <!-- 普通用户菜单 -->
            <template v-if="userStore.role === 'USER'">
              <el-menu-item index="/user/profile">
                <el-icon><User /></el-icon>
                <template #title>个人中心</template>
              </el-menu-item>
              
              <el-menu-item index="/user/browse-rooms">
                <el-icon><Search /></el-icon>
                <template #title>浏览房源</template>
              </el-menu-item>
              
              <el-sub-menu index="/user/rental">
                <template #title>
                  <el-icon><House /></el-icon>
                  <span>我的租房</span>
                </template>
                <el-menu-item index="/user/rentals">
                  <el-icon><Document /></el-icon>
                  <span>租房合同</span>
                </el-menu-item>
                <el-menu-item index="/user/applications">
                  <el-icon><Files /></el-icon>
                  <span>租房申请</span>
                </el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="/user/bills">
                <template #title>
                  <el-icon><Money /></el-icon>
                  <span>我的账单</span>
                </template>
                <el-menu-item index="/user/rent-bills">
                  <el-icon><House /></el-icon>
                  <span>租金账单</span>
                </el-menu-item>
                <el-menu-item index="/user/utility-bills">
                  <el-icon><Lightning /></el-icon>
                  <span>物业费账单</span>
                </el-menu-item>
              </el-sub-menu>
              
              <el-menu-item index="/user/feedback">
                <el-icon><ChatDotRound /></el-icon>
                <template #title>意见反馈</template>
              </el-menu-item>
            </template>
            
            <!-- 管理员和房东菜单 -->
            <template v-else>
              <el-menu-item index="/housing">
                <el-icon><House /></el-icon>
                <template #title>房屋管理</template>
              </el-menu-item>
              
              <el-sub-menu index="/users">
                <template #title>
                  <el-icon><Avatar /></el-icon>
                  <span>用户管理</span>
                </template>
                <el-menu-item index="/users/tenant">
                  <el-icon><User /></el-icon>
                  <span>普通用户管理</span>
                </el-menu-item>
                <el-menu-item index="/users/landlord">
                  <el-icon><UserFilled /></el-icon>
                  <span>房主信息管理</span>
                </el-menu-item>
              </el-sub-menu>
              
              <!-- 管理员功能菜单，只对ADMIN角色显示 -->
              <el-sub-menu index="/admin" v-if="userStore.role === 'ADMIN'">
                <template #title>
                  <el-icon><Setting /></el-icon>
                  <span>管理员功能</span>
                </template>
                <el-menu-item index="/admin/accounts">
                  <el-icon><User /></el-icon>
                  <span>管理员账号</span>
                </el-menu-item>
                <el-menu-item index="/admin/landlord-verify">
                  <el-icon><UserFilled /></el-icon>
                  <span>房东认证审核</span>
                </el-menu-item>
              </el-sub-menu>
              
              <el-sub-menu index="/bills">
                <template #title>
                  <el-icon><Money /></el-icon>
                  <span>账单管理</span>
                </template>
                <el-menu-item index="/rent-bills">
                  <el-icon><House /></el-icon>
                  <span>租金管理</span>
                </el-menu-item>
                <el-menu-item index="/utility-bills">
                  <el-icon><Money /></el-icon>
                  <span>物业费管理</span>
                </el-menu-item>
              </el-sub-menu>
              
              <el-menu-item index="/setting">
                <el-icon><Setting /></el-icon>
                <template #title>系统设置</template>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessageBox } from 'element-plus';
import { 
  HomeFilled, 
  House, 
  Money, 
  Setting, 
  ArrowDown,
  Fold,
  Expand,
  User,
  UserFilled,
  Avatar,
  Search,
  Document,
  Files,
  Lightning,
  ChatDotRound
} from '@element-plus/icons-vue';

// 默认头像和logo
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const defaultLogo = new URL('@/assets/images/logo.svg', import.meta.url).href;

// 系统logo
const logoUrl = ref('');

// 路由和状态
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path;
});

// 用户信息
const userInfo = computed(() => userStore.userInfo);

// 侧边栏收缩功能
const isCollapse = ref(false);
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// 组件挂载时执行
onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.getUserInfoAction();
  }
});

// 查看个人资料
const viewProfile = () => {
  if (userStore.role === 'USER') {
    router.push('/user/profile');
  } else {
    // 管理员和房东的个人中心页面
    console.log('查看个人资料');
  }
};

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await userStore.logoutAction();
    router.push('/login');
  }).catch(() => {
    // 取消退出
  });
};

// 处理logo加载错误
const handleLogoError = () => {
  // 处理logo加载错误后的逻辑
  console.error('Logo加载失败');
};

// 处理菜单点击
const handleMenuClick = (path) => {
  console.log('菜单点击:', path);
  router.push(path).catch(err => {
    console.error('路由跳转失败:', err);
  });
};
</script>

<style lang="scss" scoped>
.app-layout {
  height: 100%;
  
  .el-header {
    background-color: #fff;
    color: #333;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    
    .header-content {
      height: 100%;
      
      .logo-container {
        .logo {
          height: 40px;
          width: 40px;
        }
        
        .title {
          margin-left: 10px;
          font-size: 18px;
          font-weight: 600;
        }
      }
      
      .user-info {
        .avatar-container {
          cursor: pointer;
          
          .username {
            font-size: 14px;
          }
        }
      }
    }
  }
  
  .el-aside {
    background-color: #304156;
    transition: width 0.3s;
    
    .el-menu {
      border-right: none;
      height: 100%;
    }
  }
  
  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}

// 助手类
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.ml-5 {
  margin-left: 5px;
}

.ml-10 {
  margin-left: 10px;
}

.sidebar-container {
  .collapse-btn {
    height: 50px;
    color: #bfcbd9;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    
    &:hover {
      color: #fff;
    }
  }
}
</style> 