<template>
  <div class="user-layout">
    <!-- 背景装饰 -->
    <div class="background-container">
      <div class="bg-gradient"></div>
      <div class="bg-pattern"></div>
      <div class="bg-overlay"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="user-header">
      <div class="header-container">
        <div class="logo-section">
          <img 
            :src="logoUrl || defaultLogo" 
            alt="Logo" 
            class="logo"
            @error="handleLogoError"
          >
          <h1 class="site-title">公寓管理系统</h1>
        </div>

        <nav class="nav-menu">
          <router-link to="/user/dashboard" class="nav-item" active-class="active">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/user/browse-rooms" class="nav-item" active-class="active">
            <el-icon><Search /></el-icon>
            <span>找房源</span>
          </router-link>
          <router-link to="/user/rentals" class="nav-item" active-class="active">
            <el-icon><House /></el-icon>
            <span>我的租房</span>
          </router-link>
          <router-link to="/user/rent-bills" class="nav-item" active-class="active">
            <el-icon><Money /></el-icon>
            <span>账单</span>
          </router-link>
          <router-link to="/user/feedback" class="nav-item" active-class="active">
            <el-icon><ChatDotRound /></el-icon>
            <span>反馈</span>
          </router-link>
          <router-link to="/user/landlord" class="nav-item" active-class="active">
            <el-icon><Star /></el-icon>
            <span>成为房东</span>
          </router-link>
        </nav>

        <div class="user-section">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userInfo?.avatarUrl || defaultAvatar">
                {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userInfo?.username || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="applications">
                  <el-icon><Files /></el-icon>
                  我的申请
                </el-dropdown-item>
                <el-dropdown-item command="utility-bills">
                  <el-icon><Lightning /></el-icon>
                  物业费账单
                </el-dropdown-item>
                <el-dropdown-item command="landlord">
                  <el-icon><Star /></el-icon>
                  申请成为房东
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <!-- 移动端菜单按钮 -->
        <div class="mobile-menu-btn" @click="toggleMobileMenu">
          <el-icon><Menu /></el-icon>
        </div>
      </div>
    </header>

    <!-- 移动端导航菜单 -->
    <div class="mobile-nav" :class="{ 'mobile-nav-open': mobileMenuOpen }">
      <div class="mobile-nav-overlay" @click="closeMobileMenu"></div>
      <div class="mobile-nav-content">
        <div class="mobile-nav-header">
          <div class="mobile-user-info">
            <el-avatar :size="48" :src="userInfo?.avatarUrl || defaultAvatar">
              {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <span class="mobile-username">{{ userInfo?.username || '用户' }}</span>
          </div>
          <el-button text @click="closeMobileMenu">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        
        <div class="mobile-nav-menu">
          <router-link to="/user/dashboard" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/user/browse-rooms" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><Search /></el-icon>
            <span>找房源</span>
          </router-link>
          <router-link to="/user/rentals" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><House /></el-icon>
            <span>我的租房</span>
          </router-link>
          <router-link to="/user/rent-bills" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><Money /></el-icon>
            <span>租金账单</span>
          </router-link>
          <router-link to="/user/utility-bills" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><Lightning /></el-icon>
            <span>物业费账单</span>
          </router-link>
          <router-link to="/user/applications" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><Files /></el-icon>
            <span>我的申请</span>
          </router-link>
          <router-link to="/user/feedback" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><ChatDotRound /></el-icon>
            <span>意见反馈</span>
          </router-link>
          <router-link to="/user/landlord" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><Star /></el-icon>
            <span>申请成为房东</span>
          </router-link>
          <router-link to="/user/profile" class="mobile-nav-item" @click="closeMobileMenu">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </router-link>
        </div>
        
        <div class="mobile-nav-footer">
          <el-button type="danger" @click="handleLogout" style="width: 100%;">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <main class="user-main">
      <div class="content-container">
        <router-view />
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="user-footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>关于我们</h4>
            <p>专业的公寓租赁管理平台</p>
          </div>
          <div class="footer-section">
            <h4>联系方式</h4>
            <p>客服热线：400-123-4567</p>
            <p>邮箱：service@apartment.com</p>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <router-link to="/user/browse-rooms">房源搜索</router-link>
            <router-link to="/user/feedback">意见反馈</router-link>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 公寓管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessageBox, ElMessage } from 'element-plus';
import { 
  HomeFilled, 
  House, 
  Money, 
  Search,
  ChatDotRound,
  User,
  Files,
  Lightning,
  ArrowDown,
  SwitchButton,
  Menu,
  Close,
  Star
} from '@element-plus/icons-vue';

// 默认头像和logo
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const defaultLogo = new URL('@/assets/images/logo.svg', import.meta.url).href;

// 系统logo
const logoUrl = ref('');

// 路由和状态
const router = useRouter();
const userStore = useUserStore();

// 用户信息
const userInfo = computed(() => userStore.userInfo);

// 组件挂载时执行
onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.getUserInfoAction();
  }
});

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile');
      break;
    case 'applications':
      router.push('/user/applications');
      break;
    case 'utility-bills':
      router.push('/user/utility-bills');
      break;
    case 'landlord':
      router.push('/user/landlord');
      break;
    case 'logout':
      handleLogout();
      break;
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
    ElMessage.success('退出登录成功');
    router.push('/login');
  }).catch(() => {
    // 取消退出
  });
};

// 处理logo加载错误
const handleLogoError = () => {
  console.error('Logo加载失败');
};

// 移动端菜单状态
const mobileMenuOpen = ref(false);

// 打开移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = true;
};

// 关闭移动端菜单
const closeMobileMenu = () => {
  mobileMenuOpen.value = false;
};
</script>

<style lang="scss" scoped>
.user-layout {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
}

// 背景装饰
.background-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%
  );
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 120%;
  height: 120%;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: patternMove 20s linear infinite;
}

.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(1px);
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes patternMove {
  0% { transform: translate(-10%, -10%) rotate(0deg); }
  100% { transform: translate(-10%, -10%) rotate(360deg); }
}

// 顶部导航栏
.user-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;

  .logo {
    width: 40px;
    height: 40px;
    border-radius: 8px;
  }

  .site-title {
    font-size: 24px;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
  }
}

.nav-menu {
  display: flex;
  gap: 30px;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 20px;
    text-decoration: none;
    color: #666;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;
      transform: translateY(-1px);
    }

    &.active {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
  }
}

.user-section {
  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 16px;
    border-radius: 25px;
    background: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 1);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .username {
      font-weight: 500;
      color: #333;
    }

    .dropdown-icon {
      color: #999;
      transition: transform 0.3s ease;
    }
  }
}

// 主要内容区域
.user-main {
  flex: 1;
  padding: 40px 0;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

// 页脚
.user-footer {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.footer-section {
  h4 {
    color: #333;
    margin-bottom: 15px;
    font-weight: 600;
  }

  p {
    color: #666;
    margin: 5px 0;
    line-height: 1.6;
  }

  a {
    display: block;
    color: #667eea;
    text-decoration: none;
    margin: 5px 0;
    transition: color 0.3s ease;

    &:hover {
      color: #764ba2;
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);

  p {
    color: #999;
    margin: 0;
  }
}

// 移动端菜单按钮
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 1);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
}

// 移动端导航菜单
.mobile-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  visibility: hidden;
  opacity: 0;
  transition: all 0.3s ease;

  &.mobile-nav-open {
    visibility: visible;
    opacity: 1;
  }
}

.mobile-nav-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
}

.mobile-nav-content {
  position: absolute;
  top: 0;
  right: 0;
  width: 280px;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transform: translateX(100%);
  transition: transform 0.3s ease;

  .mobile-nav-open & {
    transform: translateX(0);
  }
}

.mobile-nav-header {
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mobile-username {
  font-weight: 600;
  color: #333;
}

.mobile-nav-menu {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  text-decoration: none;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
  }

  &.router-link-active {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
  }
}

.mobile-nav-footer {
  padding: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

// 响应式设计
@media (max-width: 768px) {
  .header-container {
    padding: 0 15px;
    height: 60px;
  }

  .logo-section .site-title {
    font-size: 20px;
  }

  .nav-menu {
    display: none;
  }

  .user-section {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .content-container {
    padding: 0 15px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

// 全局样式覆盖，使表单半透明
:deep(.el-card) {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-form-item__label) {
  color: #333;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
}

:deep(.el-table) {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

:deep(.el-table th.el-table__cell) {
  background: rgba(255, 255, 255, 0.95);
}

:deep(.el-table tr) {
  background: rgba(255, 255, 255, 0.8);
}

:deep(.el-pagination) {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  padding: 10px;
}
</style> 