import request from './request';

/**
 * 获取物业费账单列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getUtilityBills(params) {
  return request({
    url: '/api/v1/utility-bills',
    method: 'get',
    params
  });
}

/**
 * 获取物业费账单统计
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getUtilityBillStats(params) {
  return request({
    url: '/api/v1/utility-bills/stats',
    method: 'get',
    params
  });
}

/**
 * 创建物业费账单
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function createUtilityBill(data) {
  return request({
    url: '/api/v1/utility-bills',
    method: 'post',
    data
  });
}

/**
 * 更新物业费账单
 * @param {Number} id - 账单ID
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function updateUtilityBill(id, data) {
  return request({
    url: `/api/v1/utility-bills/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除物业费账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function deleteUtilityBill(id) {
  return request({
    url: `/api/v1/utility-bills/${id}`,
    method: 'delete'
  });
}

/**
 * 支付物业费账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function payUtilityBill(id) {
  return request({
    url: `/api/v1/utility-bills/${id}/pay`,
    method: 'post'
  });
}

/**
 * 获取用户物业费账单
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getUserUtilityBills(params) {
  return request({
    url: '/api/v1/users/utility-bills',
    method: 'get',
    params
  });
}

/**
 * 用户支付物业费账单
 * @param {Number} id - 账单ID
 * @param {Object} data - 支付数据
 * @returns {Promise} - 请求响应
 */
export function payUserUtilityBill(id, data) {
  return request({
    url: `/api/v1/users/utility-bills/${id}/pay`,
    method: 'post',
    data
  });
}

/**
 * 批量支付物业费账单
 * @param {Array} ids - 账单ID数组
 * @returns {Promise} - 请求响应
 */
export function batchPayUtilityBills(ids) {
  return request({
    url: '/api/v1/utility-bills/batch-pay',
    method: 'post',
    data: { ids }
  });
}

/**
 * 抄表录入
 * @param {Object} data - 抄表数据
 * @returns {Promise} - 请求响应
 */
export function recordMeterReading(data) {
  return request({
    url: '/api/v1/utility-bills/meter-reading',
    method: 'post',
    data
  });
}

/**
 * 导出物业费账单
 * @param {Object} params - 导出参数
 * @returns {Promise} - 请求响应
 */
export function exportUtilityBills(params) {
  return request({
    url: '/api/v1/utility-bills/export',
    method: 'get',
    params
  });
} 