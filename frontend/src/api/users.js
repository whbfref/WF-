import request from './request';

// 安全导入mock函数，防止模块错误影响整个应用
let getMockUsers, getMockLandlords, getMockLandlordProperties, mockSuccess;
try {
  const mockModule = require('./mock');
  getMockUsers = mockModule.getMockUsers;
  getMockLandlords = mockModule.getMockLandlords;
  getMockLandlordProperties = mockModule.getMockLandlordProperties;
  mockSuccess = mockModule.mockSuccess;
} catch (error) {
  console.warn('Mock module not available, using real API only:', error.message);
  // 提供默认的mock函数，防止调用时出错
  getMockUsers = () => ({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
  getMockLandlords = () => ({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
  getMockLandlordProperties = () => ({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
  mockSuccess = (data) => ({ code: 200, message: '操作成功', data });
}

// 是否使用模拟数据（当后端API不可用时设为true）
const USE_MOCK = false;

/**
 * 获取普通用户列表
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getRegularUsers(params) {
  if (USE_MOCK) {
    try {
      return Promise.resolve(getMockUsers({...params, role: 'USER'}));
    } catch (error) {
      console.error('Mock data error:', error);
      return Promise.resolve({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
    }
  }
  
  return request({
    url: '/api/v1/admin/users',
    method: 'get',
    params: {
      ...params
    }
  }).catch(error => {
    console.error('API request failed:', error);
    // 返回空数据而不是抛出错误，防止影响其他模块
    return { 
      code: 500, 
      message: '获取用户列表失败', 
      data: { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }
    };
  });
}

/**
 * 获取单个用户详情
 * @param {Number} id 用户ID
 * @returns {Promise} 请求的Promise对象
 */
export function getUserDetails(id) {
  if (USE_MOCK) {
    const mockUser = getMockUsers({}).data.content.find(user => user.id === id);
    return Promise.resolve(mockSuccess(mockUser || null));
  }
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'get'
  });
}

/**
 * 更新用户状态
 * @param {Number} id 用户ID
 * @param {Number} status 状态值（0禁用，1启用）
 * @returns {Promise} 请求的Promise对象
 */
export function updateUserStatus(id, status) {
  if (USE_MOCK) {
    return Promise.resolve(mockSuccess({ id, status }));
  }
  return request({
    url: `/api/v1/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 重置用户密码
 * @param {Number} id 用户ID
 * @returns {Promise} 请求的Promise对象
 */
export function resetUserPassword(id) {
  if (USE_MOCK) {
    return Promise.resolve(mockSuccess({ tempPassword: 'temp123456' }));
  }
  return request({
    url: `/api/v1/admin/users/${id}/password/reset`,
    method: 'post'
  });
}

/**
 * 创建用户
 * @param {Object} userData 用户数据
 * @returns {Promise} 请求的Promise对象
 */
export function createUser(userData) {
  if (USE_MOCK) {
    return Promise.resolve(mockSuccess({ 
      id: Date.now(), 
      ...userData, 
      createTime: new Date().toLocaleString() 
    }));
  }
  return request({
    url: '/api/v1/admin/users',
    method: 'post',
    data: userData
  });
}

/**
 * 获取房东列表
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getLandlords(params) {
  if (USE_MOCK) {
    try {
      return Promise.resolve(getMockLandlords(params));
    } catch (error) {
      console.error('Mock data error:', error);
      return Promise.resolve({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
    }
  }
  
  return request({
    url: '/api/v1/admin/landlords',
    method: 'get',
    params
  }).catch(error => {
    console.error('API request failed:', error);
    return { 
      code: 500, 
      message: '获取房东列表失败', 
      data: { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }
    };
  });
}

/**
 * 获取单个房东详情
 * @param {Number} id 房东ID
 * @returns {Promise} 请求的Promise对象
 */
export function getLandlordDetails(id) {
  if (USE_MOCK) {
    const mockLandlord = getMockLandlords({}).data.content.find(landlord => landlord.id === id);
    return Promise.resolve(mockSuccess(mockLandlord || null));
  }
  return request({
    url: `/api/v1/admin/landlords/${id}`,
    method: 'get'
  });
}

/**
 * 更新房东认证状态
 * @param {Number} id 房东ID
 * @param {Boolean} verified 认证状态
 * @returns {Promise} 请求的Promise对象
 */
export function updateLandlordVerified(id, verified) {
  if (USE_MOCK) {
    return Promise.resolve(mockSuccess({ id, verified }));
  }
  return request({
    url: `/api/v1/admin/landlords/${id}/verified`,
    method: 'put',
    data: { verified }
  });
}

/**
 * 获取房东的房产列表
 * @param {Number} landlordId 房东ID
 * @returns {Promise} 请求的Promise对象
 */
export function getLandlordProperties(landlordId) {
  if (USE_MOCK) {
    try {
      return Promise.resolve(getMockLandlordProperties(landlordId));
    } catch (error) {
      console.error('Mock data error:', error);
      return Promise.resolve({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 });
    }
  }
  
  return request({
    url: `/api/v1/admin/landlords/${landlordId}/properties`,
    method: 'get'
  }).catch(error => {
    console.error('API request failed:', error);
    return { 
      code: 500, 
      message: '获取房东房产列表失败', 
      data: { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }
    };
  });
} 