/**
 * 全局错误处理器
 * 防止单个模块的错误影响整个应用
 */

import { ElMessage } from 'element-plus';

// 错误类型枚举
export const ERROR_TYPES = {
  API_ERROR: 'API_ERROR',
  NETWORK_ERROR: 'NETWORK_ERROR',
  DATA_FORMAT_ERROR: 'DATA_FORMAT_ERROR',
  COMPONENT_ERROR: 'COMPONENT_ERROR',
  ROUTE_ERROR: 'ROUTE_ERROR'
};

// 错误处理器类
class ErrorHandler {
  constructor() {
    this.errorQueue = [];
    this.maxErrors = 10; // 最大错误数量
    this.setupGlobalHandlers();
  }

  // 设置全局错误处理器
  setupGlobalHandlers() {
    // 捕获未处理的Promise错误
    window.addEventListener('unhandledrejection', (event) => {
      console.error('未处理的Promise错误:', event.reason);
      this.handleError(event.reason, ERROR_TYPES.API_ERROR);
      event.preventDefault(); // 防止错误传播
    });

    // 捕获JavaScript运行时错误
    window.addEventListener('error', (event) => {
      console.error('JavaScript运行时错误:', event.error);
      this.handleError(event.error, ERROR_TYPES.COMPONENT_ERROR);
      event.preventDefault(); // 防止错误传播
    });
  }

  // 处理错误
  handleError(error, type = ERROR_TYPES.API_ERROR, context = '') {
    const errorInfo = {
      type,
      message: error?.message || error || '未知错误',
      stack: error?.stack,
      context,
      timestamp: new Date().toISOString()
    };

    // 添加到错误队列
    this.addToErrorQueue(errorInfo);

    // 根据错误类型处理
    switch (type) {
      case ERROR_TYPES.API_ERROR:
        this.handleApiError(error, context);
        break;
      case ERROR_TYPES.NETWORK_ERROR:
        this.handleNetworkError(error, context);
        break;
      case ERROR_TYPES.DATA_FORMAT_ERROR:
        this.handleDataFormatError(error, context);
        break;
      case ERROR_TYPES.COMPONENT_ERROR:
        this.handleComponentError(error, context);
        break;
      case ERROR_TYPES.ROUTE_ERROR:
        this.handleRouteError(error, context);
        break;
      default:
        this.handleGenericError(error, context);
    }
  }

  // 添加到错误队列
  addToErrorQueue(errorInfo) {
    this.errorQueue.push(errorInfo);
    if (this.errorQueue.length > this.maxErrors) {
      this.errorQueue.shift(); // 移除最旧的错误
    }
  }

  // 处理API错误
  handleApiError(error, context) {
    console.error(`API错误 [${context}]:`, error);
    
    // 避免重复显示相同的错误消息
    const recentErrors = this.errorQueue.slice(-3);
    const isDuplicate = recentErrors.some(e => 
      e.message === error?.message && 
      e.context === context &&
      Date.now() - new Date(e.timestamp).getTime() < 5000 // 5秒内
    );

    if (!isDuplicate) {
      if (error?.response?.status === 401) {
        ElMessage.error('登录已过期，请重新登录');
      } else if (error?.response?.status === 403) {
        ElMessage.error('权限不足，无法访问该功能');
      } else if (error?.response?.status >= 500) {
        ElMessage.warning('服务暂时不可用，请稍后重试');
      } else {
        ElMessage.error(error?.message || '操作失败，请稍后重试');
      }
    }
  }

  // 处理网络错误
  handleNetworkError(error, context) {
    console.error(`网络错误 [${context}]:`, error);
    ElMessage.error('网络连接异常，请检查网络后重试');
  }

  // 处理数据格式错误
  handleDataFormatError(error, context) {
    console.error(`数据格式错误 [${context}]:`, error);
    ElMessage.warning('数据格式异常，已使用默认数据');
  }

  // 处理组件错误
  handleComponentError(error, context) {
    console.error(`组件错误 [${context}]:`, error);
    // 组件错误通常不需要显示给用户，只记录日志
  }

  // 处理路由错误
  handleRouteError(error, context) {
    console.error(`路由错误 [${context}]:`, error);
    ElMessage.error('页面加载失败，请刷新重试');
  }

  // 处理通用错误
  handleGenericError(error, context) {
    console.error(`通用错误 [${context}]:`, error);
    ElMessage.error('系统异常，请稍后重试');
  }

  // 获取错误统计
  getErrorStats() {
    const stats = {};
    this.errorQueue.forEach(error => {
      stats[error.type] = (stats[error.type] || 0) + 1;
    });
    return stats;
  }

  // 清空错误队列
  clearErrors() {
    this.errorQueue = [];
  }
}

// 创建全局错误处理器实例
const errorHandler = new ErrorHandler();

// 导出错误处理函数
export const handleError = (error, type, context) => {
  errorHandler.handleError(error, type, context);
};

// 导出安全执行函数
export const safeExecute = async (fn, fallback = null, context = '') => {
  try {
    return await fn();
  } catch (error) {
    handleError(error, ERROR_TYPES.API_ERROR, context);
    return fallback;
  }
};

// 导出安全数据处理函数
export const safeDataProcess = (data, processor, fallback = null, context = '') => {
  try {
    return processor(data);
  } catch (error) {
    handleError(error, ERROR_TYPES.DATA_FORMAT_ERROR, context);
    return fallback;
  }
};

export default errorHandler; 