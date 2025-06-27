import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/user';
import Layout from '@/layout/index.vue';
import UserLayout from '@/layout/UserLayout.vue';
import LandlordLayout from '@/layout/LandlordLayout.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true }
      },

      {
        path: 'housing',
        name: 'Housing',
        component: () => import('@/views/housing/index.vue'),
        meta: { title: '房屋管理', requiresAuth: true }
      },
      {
        path: 'users/tenant',
        name: 'TenantUsers',
        component: () => import('@/views/users/tenant/index.vue'),
        meta: { title: '普通用户管理', requiresAuth: true }
      },
      {
        path: 'users/landlord',
        name: 'LandlordUsers',
        component: () => import('@/views/admin/landlords/index.vue'),
        meta: { title: '房主信息管理', requiresAuth: true }
      },

      // 管理员模块路由
      {
        path: 'admin/accounts',
        name: 'AdminAccounts',
        component: () => import('@/views/admin/accounts/index.vue'),
        meta: { title: '管理员账号管理', requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'admin/landlord-verify',
        name: 'LandlordVerify',
        component: () => import('@/views/admin/landlord-verify/index.vue'),
        meta: { title: '房东认证审核', requiresAuth: true, role: 'ADMIN' }
      },

      // 租金管理路由
      {
        path: 'rent-bills',
        name: 'RentBills',
        component: () => import('@/views/bills/rent/index.vue'),
        meta: { title: '租金管理', requiresAuth: true }
      },
      // 物业费管理路由
      {
        path: 'utility-bills',
        name: 'UtilityBills',
        component: () => import('@/views/bills/utility/index.vue'),
        meta: { title: '物业费管理', requiresAuth: true }
      },
      // 系统设置路由
      {
        path: 'setting',
        name: 'Setting',
        component: () => import('@/views/setting/index.vue'),
        meta: { title: '系统设置', requiresAuth: true }
      }
    ]
  },
  // 用户专用路由，使用UserLayout布局
  {
    path: '/user',
    component: UserLayout,
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/users/dashboard/index.vue'),
        meta: { title: '用户首页', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/users/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'browse-rooms',
        name: 'BrowseRooms',
        component: () => import('@/views/users/browse-rooms/index.vue'),
        meta: { title: '浏览房源', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'rentals',
        name: 'UserRentals',
        component: () => import('@/views/users/rentals/index.vue'),
        meta: { title: '我的租房', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'rent-bills',
        name: 'UserRentBills',
        component: () => import('@/views/users/rent-bills/index.vue'),
        meta: { title: '租金账单', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'utility-bills',
        name: 'UserUtilityBills',
        component: () => import('@/views/users/utility-bills/index.vue'),
        meta: { title: '物业费账单', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'feedback',
        name: 'UserFeedback',
        component: () => import('@/views/users/feedback/index.vue'),
        meta: { title: '意见反馈', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'applications',
        name: 'UserApplications',
        component: () => import('@/views/users/applications/index.vue'),
        meta: { title: '租房申请', requiresAuth: true, role: 'USER' }
      },
      {
        path: 'landlord',
        name: 'UserLandlordApplication',
        component: () => import('@/views/users/landlord/index.vue'),
        meta: { title: '申请成为房东', requiresAuth: true, role: 'USER' }
      }
    ]
  },
  // 房东专用路由，使用LandlordLayout布局
  {
    path: '/landlord',
    component: LandlordLayout,
    meta: { requiresAuth: true, role: 'LANDLORD' },
    children: [
      {
        path: 'dashboard',
        name: 'LandlordDashboard',
        component: () => import('@/views/landlord/dashboard/index.vue'),
        meta: { title: '房东仪表板', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'profile',
        name: 'LandlordProfile',
        component: () => import('@/views/landlord/profile/index.vue'),
        meta: { title: '个人信息', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'verification',
        name: 'LandlordVerification',
        component: () => import('@/views/landlord/verification/index.vue'),
        meta: { title: '房东认证', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'properties',
        name: 'LandlordProperties',
        component: () => import('@/views/landlord/properties/index.vue'),
        meta: { title: '房源管理', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'income',
        name: 'LandlordIncome',
        component: () => import('@/views/landlord/income/index.vue'),
        meta: { title: '收入管理', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'ratings',
        name: 'LandlordRatings',
        component: () => import('@/views/landlord/ratings/index.vue'),
        meta: { title: '评价管理', requiresAuth: true, role: 'LANDLORD' }
      },
      {
        path: 'todos',
        name: 'LandlordTodos',
        component: () => import('@/views/landlord/todos/index.vue'),
        meta: { title: '待办事项', requiresAuth: true, role: 'LANDLORD' }
      }
    ]
  },
  {
    path: '/test-api',
    name: 'TestApi',
    component: () => import('@/views/test-api.vue'),
    meta: { title: 'API测试', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 公寓管理系统` : '公寓管理系统';
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    const userStore = useUserStore();
    
    // 如果没有token，重定向到登录页
    if (!userStore.token) {
      next({ name: 'Login', query: { redirect: to.fullPath } });
      return;
    }
    
    // 如果有token但没有用户信息，尝试获取用户信息
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfoAction();
      } catch (error) {
        console.error('获取用户信息失败:', error);
        next({ name: 'Login', query: { redirect: to.fullPath } });
        return;
      }
    }
    
    // 检查角色权限
    if (to.meta.role && to.meta.role !== userStore.role) {
      // 根据用户角色跳转到对应的首页
      let defaultPath = '/dashboard';
      if (userStore.role === 'USER') {
        defaultPath = '/user/dashboard';
      } else if (userStore.role === 'LANDLORD') {
        defaultPath = '/landlord/dashboard';
      }
      next({ path: defaultPath, replace: true });
      return;
    }
    
    // 如果是普通用户访问管理员页面，重定向到用户仪表板
    if (userStore.role === 'USER' && to.path === '/dashboard') {
      next({ path: '/user/dashboard', replace: true });
      return;
    }
    
    // 如果是房东访问管理员页面，重定向到房东仪表板
    if (userStore.role === 'LANDLORD' && to.path === '/dashboard') {
      next({ path: '/landlord/dashboard', replace: true });
      return;
    }
    
    next();
  } else {
    next();
  }
});

// 路由错误处理
router.onError((error) => {
  console.error('Vue Router Error:', error);
  // 可以在这里添加错误上报或其他处理逻辑
});

export default router; 