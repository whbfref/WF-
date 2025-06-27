import request from './request';

/**
 * 获取当前用户信息
 * @returns {Promise} 请求的Promise对象
 */
export function getCurrentUser() {
  return request({
    url: '/api/v1/users/me',
    method: 'get'
  });
}

/**
 * 更新当前用户信息
 * @param {Object} userData 用户数据
 * @returns {Promise} 请求的Promise对象
 */
export function updateCurrentUser(userData) {
  return request({
    url: '/api/v1/users/me',
    method: 'put',
    data: userData
  });
}

/**
 * 上传用户头像
 * @param {FormData} formData 包含头像文件的表单数据
 * @returns {Promise} 请求的Promise对象
 */
export function uploadAvatar(formData) {
  return request({
    url: '/api/v1/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 提交用户反馈
 * @param {Object} feedbackData 反馈数据
 * @returns {Promise} 请求的Promise对象
 */
export function submitFeedback(feedbackData) {
  return request({
    url: '/api/v1/users/feedback',
    method: 'post',
    data: feedbackData
  });
}

/**
 * 获取用户反馈列表
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getUserFeedback(params) {
  return request({
    url: '/api/v1/users/feedback',
    method: 'get',
    params
  });
}

/**
 * 获取用户租房信息
 * @returns {Promise} 请求的Promise对象
 */
export function getUserRentals() {
  return request({
    url: '/api/v1/users/rentals',
    method: 'get'
  });
}

/**
 * 获取用户租金账单
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getUserRentBills(params) {
  return request({
    url: '/api/v1/users/rent-bills',
    method: 'get',
    params
  });
}

/**
 * 获取用户物业费账单
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getUserUtilityBills(params) {
  return request({
    url: '/api/v1/users/utility-bills',
    method: 'get',
    params
  });
}

/**
 * 支付租金账单
 * @param {Number} billId 账单ID
 * @returns {Promise} 请求的Promise对象
 */
export function payRentBill(billId) {
  return request({
    url: `/api/v1/users/rent-bills/${billId}/pay`,
    method: 'post'
  });
}

/**
 * 支付物业费账单
 * @param {Number} billId 账单ID
 * @returns {Promise} 请求的Promise对象
 */
export function payUtilityBill(billId) {
  return request({
    url: `/api/v1/users/utility-bills/${billId}/pay`,
    method: 'post'
  });
}

/**
 * 获取可租房源列表
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getAvailableRooms(params) {
  return request({
    url: '/api/v1/users/available-rooms',
    method: 'get',
    params
  });
}

/**
 * 申请租房
 * @param {Object} applicationData 申请数据
 * @returns {Promise} 请求的Promise对象
 */
export function applyForRoom(applicationData) {
  return request({
    url: '/api/v1/users/room-applications',
    method: 'post',
    data: applicationData
  });
}

/**
 * 获取租房申请记录
 * @param {Object} params 查询参数
 * @returns {Promise} 请求的Promise对象
 */
export function getRoomApplications(params) {
  return request({
    url: '/api/v1/users/room-applications',
    method: 'get',
    params
  });
}

/**
 * 取消租房申请
 * @param {Number} applicationId 申请ID
 * @returns {Promise} 请求的Promise对象
 */
export function cancelRoomApplication(applicationId) {
  return request({
    url: `/api/v1/users/room-applications/${applicationId}/cancel`,
    method: 'put'
  });
}

/**
 * 获取用户仪表盘统计数据
 * @returns {Promise} 请求的Promise对象
 */
export function getDashboardStats() {
  return request({
    url: '/api/v1/users/dashboard/stats',
    method: 'get'
  });
}

/**
 * 发送验证码
 * @param {string} phone 手机号
 * @returns {Promise} 请求的Promise对象
 */
export function sendVerificationCode(phone) {
  return request({
    url: '/api/v1/users/send-verification-code',
    method: 'post',
    data: { phone }
  });
}

/**
 * 修改密码
 * @param {Object} data 修改密码数据
 * @returns {Promise} 请求的Promise对象
 */
export function changePassword(data) {
  return request({
    url: '/api/v1/users/change-password',
    method: 'post',
    data
  });
} 