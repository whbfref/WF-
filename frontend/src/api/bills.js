import request from './request';

/**
 * 获取账单列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getBills(params) {
  return request({
    url: '/api/v1/admin/bills',
    method: 'get',
    params
  });
}

/**
 * 获取账单统计
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function getBillStats(params) {
  return request({
    url: '/api/v1/admin/bills/stats',
    method: 'get',
    params
  });
}

/**
 * 创建账单
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function createBill(data) {
  return request({
    url: '/api/v1/admin/bills',
    method: 'post',
    data
  });
}

/**
 * 更新账单
 * @param {Number} id - 账单ID
 * @param {Object} data - 账单数据
 * @returns {Promise} - 请求响应
 */
export function updateBill(id, data) {
  return request({
    url: `/api/v1/admin/bills/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function deleteBill(id) {
  return request({
    url: `/api/v1/admin/bills/${id}`,
    method: 'delete'
  });
}

/**
 * 支付账单
 * @param {Number} id - 账单ID
 * @returns {Promise} - 请求响应
 */
export function payBill(id) {
  return request({
    url: `/api/v1/admin/bills/${id}/pay`,
    method: 'post'
  });
}

/**
 * 批量支付账单
 * @param {Array} ids - 账单ID数组
 * @returns {Promise} - 请求响应
 */
export function batchPayBills(ids) {
  return request({
    url: '/api/v1/admin/bills/batch-pay',
    method: 'post',
    data: { ids }
  });
}

/**
 * 生成账单
 * @param {Object} data - 生成参数
 * @returns {Promise} - 请求响应
 */
export function generateBills(data) {
  return request({
    url: '/api/v1/admin/bills/generate',
    method: 'post',
    data
  });
}

/**
 * 导出账单
 * @param {Object} params - 查询参数
 * @returns {Promise} - 请求响应
 */
export function exportBills(params) {
  return request({
    url: '/api/v1/admin/bills/export',
    method: 'get',
    params,
    responseType: 'blob'
  });
} 