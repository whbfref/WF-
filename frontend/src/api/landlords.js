import request from './request';

/**
 * 获取房东列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlords(params = {}) {
  return request({
    url: '/api/v1/landlords',
    method: 'get',
    params
  });
}

/**
 * 根据ID获取房东详情
 * @param {number} id - 房东ID
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordById(id) {
  return request({
    url: `/api/v1/landlords/${id}`,
    method: 'get'
  });
}

/**
 * 创建新房东
 * @param {Object} data - 房东数据
 * @returns {Promise} - 返回Promise对象
 */
export function createLandlord(data) {
  return request({
    url: '/api/v1/landlords',
    method: 'post',
    data
  });
}

/**
 * 更新房东信息
 * @param {number} id - 房东ID
 * @param {Object} data - 更新的房东数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateLandlord(id, data) {
  return request({
    url: `/api/v1/landlords/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除房东
 * @param {number} id - 房东ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteLandlord(id) {
  return request({
    url: `/api/v1/landlords/${id}`,
    method: 'delete'
  });
}

/**
 * 获取房东的房产列表
 * @param {number} landlordId - 房东ID
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordProperties(landlordId, params = {}) {
  return request({
    url: `/api/v1/landlords/${landlordId}/properties`,
    method: 'get',
    params
  });
}

/**
 * 获取房东统计数据
 * @param {number} landlordId - 房东ID
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordStats(landlordId) {
  return request({
    url: `/api/v1/landlords/${landlordId}/stats`,
    method: 'get'
  });
}

/**
 * 获取房东收入统计
 * @param {number} landlordId - 房东ID
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordIncome(landlordId, params = {}) {
  return request({
    url: `/api/v1/landlords/${landlordId}/income`,
    method: 'get',
    params
  });
}

/**
 * 获取房东收入明细
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordIncomeDetails(params = {}) {
  return request({
    url: '/api/v1/landlords/income/details',
    method: 'get',
    params
  });
}

/**
 * 房东认证申请
 * @param {FormData} data - 认证申请数据
 * @returns {Promise} - 返回Promise对象
 */
export function applyLandlordVerification(data) {
  return request({
    url: '/api/v1/landlords/verify',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取房东认证状态
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordVerificationStatus() {
  return request({
    url: '/api/v1/landlords/verify/status',
    method: 'get'
  });
}

/**
 * 获取当前房东信息
 * @returns {Promise} - 返回Promise对象
 */
export function getCurrentLandlordInfo() {
  return request({
    url: '/api/v1/landlords/me',
    method: 'get'
  });
}

/**
 * 更新当前房东信息
 * @param {Object} data - 更新的房东数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateCurrentLandlordInfo(data) {
  return request({
    url: '/api/v1/landlords/me',
    method: 'put',
    data
  });
}

/**
 * 获取房东评价列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordRatings(params = {}) {
  return request({
    url: '/api/v1/landlords/ratings',
    method: 'get',
    params
  });
}

/**
 * 回复评价
 * @param {number} ratingId - 评价ID
 * @param {Object} data - 回复内容
 * @returns {Promise} - 返回Promise对象
 */
export function replyToRating(ratingId, data) {
  return request({
    url: `/api/v1/landlords/ratings/${ratingId}/reply`,
    method: 'post',
    data
  });
}

/**
 * 获取房东收入统计
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordIncomeStats(params = {}) {
  return request({
    url: '/api/v1/landlords/income/stats',
    method: 'get',
    params
  });
}

/**
 * 获取房东待办事项
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordTodos(params = {}) {
  return request({
    url: '/api/v1/landlords/todos',
    method: 'get',
    params
  });
}

/**
 * 处理待办事项
 * @param {number} todoId - 待办事项ID
 * @param {Object} data - 更新数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateTodoStatus(todoId, data) {
  return request({
    url: `/api/v1/landlords/todos/${todoId}`,
    method: 'put',
    data
  });
}

/**
 * 更新房东认证状态
 * @param {number} landlordId - 房东ID
 * @param {boolean} verified - 认证状态
 * @returns {Promise} - 返回Promise对象
 */
export function updateLandlordVerified(landlordId, verified) {
  return request({
    url: `/api/v1/landlords/${landlordId}/verified`,
    method: 'put',
    data: { verified }
  });
}

/**
 * 测试用户认证状态
 * @returns {Promise} - 返回Promise对象
 */
export function testAuth() {
  return request({
    url: '/api/v1/landlords/test/auth',
    method: 'get'
  });
}

/**
 * 获取房东的房间列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordRooms(params = {}) {
  return request({
    url: '/api/v1/landlords/rooms',
    method: 'get',
    params
  });
}

/**
 * 获取房东的房间统计数据
 * @returns {Promise} - 返回Promise对象
 */
export function getLandlordRoomStats() {
  return request({
    url: '/api/v1/landlords/rooms/stats',
    method: 'get'
  });
}

/**
 * 获取当前房东统计数据
 * @returns {Promise} - 返回Promise对象
 */
export function getCurrentLandlordStats() {
  return request({
    url: '/api/v1/landlords/stats',
    method: 'get'
  });
}

/**
 * 更新房间状态
 * @param {number} roomId - 房间ID
 * @param {string} status - 新状态 (VACANT, RENTED, MAINTENANCE)
 * @returns {Promise} - 返回Promise对象
 */
export function updateRoomStatus(roomId, status) {
  return request({
    url: `/api/v1/landlords/rooms/${roomId}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 获取房间租户信息
 * @param {number} roomId - 房间ID
 * @returns {Promise} - 返回Promise对象
 */
export function getRoomTenant(roomId) {
  return request({
    url: `/api/v1/landlords/rooms/${roomId}/tenant`,
    method: 'get'
  });
} 