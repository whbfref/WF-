import request from './request';

/**
 * 获取租金账单列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getRentBills(params) {
  return request({
    url: '/api/v1/admin/rent-bills',
    method: 'get',
    params
  });
}

/**
 * 获取租金账单统计
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getRentBillStats(params) {
  return request({
    url: '/api/v1/admin/rent-bills/stats',
    method: 'get',
    params
  });
}

/**
 * 创建租金账单
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function createRentBill(data) {
  return request({
    url: '/api/v1/admin/rent-bills',
    method: 'post',
    data
  });
}

/**
 * 更新租金账单
 * @param {Number} id - 账单ID
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function updateRentBill(id, data) {
  return request({
    url: `/api/v1/admin/rent-bills/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除租金账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function deleteRentBill(id) {
  return request({
    url: `/api/v1/admin/rent-bills/${id}`,
    method: 'delete'
  });
}

/**
 * 支付租金账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function payRentBill(id) {
  return request({
    url: `/api/v1/admin/rent-bills/${id}/pay`,
    method: 'post'
  });
}

/**
 * 批量支付租金账单
 * @param {Array} ids - 账单ID数组
 * @returns {Promise} - 请求响应
 */
export function batchPayRentBills(ids) {
  return request({
    url: '/api/v1/admin/rent-bills/batch-pay',
    method: 'post',
    data: { ids }
  });
}

/**
 * 生成租金账单
 * @param {Object} data - 生成参数
 * @returns {Promise} - 请求响应
 */
export function generateRentBills(data) {
  return request({
    url: '/api/v1/admin/rent-bills/generate',
    method: 'post',
    data
  });
}

/**
 * 导出租金账单
 * @param {Object} params - 导出参数
 * @returns {Promise} - 请求响应
 */
export function exportRentBills(params) {
  return request({
    url: '/api/v1/admin/rent-bills/export',
    method: 'get',
    params
  });
}

/**
 * 获取用户租金账单
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getUserRentBills(params) {
  return request({
    url: '/api/v1/users/rent-bills',
    method: 'get',
    params
  });
}

/**
 * 用户支付租金账单
 * @param {Number} id - 账单ID
 * @param {Object} data - 支付数据
 * @returns {Promise} - 请求响应
 */
export function payUserRentBill(id, data) {
  return request({
    url: `/api/v1/users/rent-bills/${id}/pay`,
    method: 'post',
    data
  });
} 