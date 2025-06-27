import request from './request';

/**
 * 管理员登录
 * @param {Object} data - 登录信息
 * @returns {Promise} - 返回Promise对象
 */
export function adminLogin(data) {
  return request({
    url: '/api/v1/admin/login',
    method: 'post',
    data
  });
}

/**
 * 获取当前管理员信息
 * @returns {Promise} - 返回Promise对象
 */
export function getCurrentAdmin() {
  return request({
    url: '/api/v1/admin/me',
    method: 'get'
  });
}

/**
 * 修改管理员密码
 * @param {Object} data - 密码信息
 * @returns {Promise} - 返回Promise对象
 */
export function updateAdminPassword(data) {
  return request({
    url: '/api/v1/admin/password',
    method: 'put',
    data
  });
}

/**
 * 获取管理员系统概览数据
 * @returns {Promise} - 返回Promise对象
 */
export function getAdminOverview() {
  return request({
    url: '/api/v1/admin/overview',
    method: 'get'
  });
}

/**
 * 获取管理员账号列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getAdminAccounts(params) {
  return request({
    url: '/api/v1/admin/accounts',
    method: 'get',
    params
  });
}

/**
 * 创建管理员账号
 * @param {Object} data - 管理员信息
 * @returns {Promise} - 返回Promise对象
 */
export function createAdminAccount(data) {
  return request({
    url: '/api/v1/admin/accounts',
    method: 'post',
    data
  });
}

/**
 * 更新管理员账号
 * @param {Number} id - 管理员ID
 * @param {Object} data - 更新数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateAdminAccount(id, data) {
  return request({
    url: `/api/v1/admin/accounts/${id}`,
    method: 'put',
    data
  });
}

/**
 * 更新管理员账号状态
 * @param {Number} id - 管理员ID
 * @param {Number} status - 状态(0禁用,1启用)
 * @returns {Promise} - 返回Promise对象
 */
export function updateAdminAccountStatus(id, status) {
  return request({
    url: `/api/v1/admin/accounts/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 重置管理员密码
 * @param {Number} id - 管理员ID
 * @returns {Promise} - 返回Promise对象
 */
export function resetAdminPassword(id) {
  return request({
    url: `/api/v1/admin/accounts/${id}/password/reset`,
    method: 'post'
  });
}

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getUsers(params) {
  return request({
    url: '/api/v1/admin/users',
    method: 'get',
    params
  });
}

/**
 * 获取用户详情
 * @param {Number} id - 用户ID
 * @returns {Promise} - 返回Promise对象
 */
export function getUserDetails(id) {
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'get'
  });
}

/**
 * 更新用户状态
 * @param {Number} id - 用户ID
 * @param {Number} status - 状态(0禁用,1启用)
 * @returns {Promise} - 返回Promise对象
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/api/v1/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 删除用户
 * @param {Number} id - 用户ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteUser(id) {
  return request({
    url: `/api/v1/admin/users/${id}`,
    method: 'delete'
  });
}

/**
 * 重置用户密码
 * @param {Number} id - 用户ID
 * @returns {Promise} - 返回Promise对象
 */
export function resetUserPassword(id) {
  return request({
    url: `/api/v1/admin/users/${id}/password/reset`,
    method: 'post'
  });
}

/**
 * 获取房东认证申请列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordVerifyList(params) {
  return request({
    url: '/api/v1/admin/landlords/verify',
    method: 'get',
    params
  });
}

/**
 * 审核房东认证申请
 * @param {Number} applicationId - 申请ID
 * @param {Object} data - 审核数据
 * @returns {Promise} - 返回Promise对象
 */
export function verifyLandlordApplication(applicationId, data) {
  return request({
    url: `/api/v1/admin/landlords/verify/${applicationId}`,
    method: 'put',
    data
  });
}

/**
 * 获取待审核房源列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getPropertiesAudit(params) {
  return request({
    url: '/api/v1/admin/properties/audit',
    method: 'get',
    params
  });
}

/**
 * 审核房源
 * @param {Number} propertyId - 房源ID
 * @param {Object} data - 审核数据
 * @returns {Promise} - 返回Promise对象
 */
export function auditProperty(propertyId, data) {
  return request({
    url: `/api/v1/admin/properties/${propertyId}/audit`,
    method: 'put',
    data
  });
}

/**
 * 获取系统配置
 * @returns {Promise} - 返回Promise对象
 */
export function getSystemConfig() {
  return request({
    url: '/api/v1/admin/config',
    method: 'get'
  });
}

/**
 * 更新系统配置
 * @param {Object} data - 配置数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateSystemConfig(data) {
  return request({
    url: '/api/v1/admin/config',
    method: 'put',
    data
  });
}

/**
 * 获取系统日志
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getSystemLogs(params) {
  return request({
    url: '/api/v1/admin/logs',
    method: 'get',
    params
  });
}

/**
 * 获取数据统计
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getStatisticsOverview(params) {
  return request({
    url: '/api/v1/admin/statistics/overview',
    method: 'get',
    params
  });
} 