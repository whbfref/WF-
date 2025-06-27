import { defineStore } from 'pinia';
import { login, register, logout, getUserInfo } from '@/api/auth';
import { ElMessage } from 'element-plus';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: null,
    userInfo: null,
    roles: []
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isLandlord: (state) => state.roles.includes('LANDLORD'),
    isAdmin: (state) => state.roles.includes('ADMIN'),
    role: (state) => state.userInfo?.role || ''
  },
  actions: {
    async loginAction(credentials) {
      try {
        const response = await login(credentials);
        
        if (response && response.code === 200 && response.data) {
          const { token, user } = response.data;
          
          if (token) {
            this.token = token;
            this.userInfo = user;
            
            if (this.userInfo && this.userInfo.role) {
              this.roles = [this.userInfo.role];
            }
            
            localStorage.setItem('rental_user', JSON.stringify({
              token: this.token,
              userInfo: this.userInfo
            }));
            
            ElMessage.success('登录成功');
            return true;
          }
        }
        
        const errorMsg = response?.message || '登录失败，请检查用户名和密码';
        ElMessage.error(errorMsg);
        return false;
      } catch (error) {
        const errorMsg = error.response?.data?.message || error.message || '登录时发生错误';
        ElMessage.error(errorMsg);
        return false;
      }
    },
    
    async registerAction(userData) {
      try {
        const response = await register(userData);
        console.log('注册响应:', response);
        
        if (response && response.code === 200) {
          ElMessage.success('注册成功');
          return true;
        } else {
          ElMessage.error(response?.message || '注册失败');
          return false;
        }
      } catch (error) {
        console.error('注册出错:', error);
        ElMessage.error(error.response?.data?.message || error.message || '注册时发生错误');
        return false;
      }
    },
    
    async getUserInfoAction() {
      // 从localStorage恢复状态
      const storedData = localStorage.getItem('rental_user');
      if (storedData) {
        try {
          const data = JSON.parse(storedData);
          if (data.token) {
            this.token = data.token;
          }
          if (data.userInfo) {
            this.userInfo = data.userInfo;
            this.roles = [data.userInfo.role];
          }
        } catch (e) {
          console.error('解析存储的用户数据失败:', e);
        }
      }
      
      if (!this.token) {
        return false;
      }
      
      try {
        const response = await getUserInfo();
        console.log('获取用户信息响应:', response);
        
        if (response && response.code === 200) {
          this.userInfo = response.data;
          this.roles = [response.data.role];
          
          // 更新localStorage
          localStorage.setItem('rental_user', JSON.stringify({
            token: this.token,
            userInfo: this.userInfo
          }));
          
          return true;
        } else {
          return false;
        }
      } catch (error) {
        console.error('获取用户信息出错:', error);
        return false;
      }
    },
    
    async logoutAction() {
      try {
        await logout();
        this.resetState();
        localStorage.removeItem('rental_user');
        ElMessage.success('已退出登录');
        return true;
      } catch (error) {
        console.error('退出登录出错:', error);
        ElMessage.error('退出登录时发生错误');
        return false;
      }
    },
    
    resetState() {
      this.token = null;
      this.userInfo = null;
      this.roles = [];
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'rental_user',
        storage: localStorage,
        paths: ['token', 'userInfo', 'roles']
      }
    ]
  }
}); 