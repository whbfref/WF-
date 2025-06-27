import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 创建一个axios实例
const service = axios.create({
  baseURL: '', // 修改为空字符串，让请求走Vite代理
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const tokenString = localStorage.getItem('rental_user');
    
    // 如果有token则添加到请求头
    if (tokenString) {
      try {
        const parsedData = JSON.parse(tokenString);
        if (parsedData && parsedData.token) {
          config.headers['Authorization'] = `Bearer ${parsedData.token}`;
        }
      } catch (e) {
        console.error('解析token失败', e);
      }
    }
    
    return config;
  },
  error => {
    console.error('请求错误', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('请求错误:', error);
    
    // 处理认证错误
    if (error.response) {
      const { status } = error.response;
      
      if (status === 401) {
        // token过期或无效
        ElMessage.error('登录已过期，请重新登录');
        // 清除本地存储
        localStorage.removeItem('rental_user');
        // 跳转到登录页
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
      } else if (status === 403) {
        // 权限不足
        ElMessage.error('权限不足，请联系管理员');
      } else if (status >= 500) {
        // 服务器错误
        ElMessage.error('服务器错误，请稍后重试');
      }
    } else {
      // 网络错误
      ElMessage.error('网络连接失败，请检查网络');
    }
    
    return Promise.reject(error);
  }
);

export default service; 