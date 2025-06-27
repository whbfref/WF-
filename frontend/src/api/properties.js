import request from './request';

/**
 * 获取房屋列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getProperties(params = {}) {
  return request({
    url: '/api/v1/properties',
    method: 'get',
    params
  });
}

/**
 * 根据ID获取房屋详情
 * @param {number} id - 房屋ID
 * @returns {Promise} - 返回Promise对象
 */
export function getPropertyById(id) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'get'
  });
}

/**
 * 创建新房屋
 * @param {Object} data - 房屋数据
 * @returns {Promise} - 返回Promise对象
 */
export function createProperty(data) {
  return request({
    url: '/api/v1/properties',
    method: 'post',
    data
  });
}

/**
 * 更新房屋信息
 * @param {number} id - 房屋ID
 * @param {Object} data - 更新的房屋数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateProperty(id, data) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除房屋
 * @param {number} id - 房屋ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteProperty(id) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'delete'
  });
}

/**
 * 获取房屋的房间列表
 * @param {number} propertyId - 房屋ID
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getPropertyRooms(propertyId, params = {}) {
  return request({
    url: `/api/v1/properties/${propertyId}/rooms`,
    method: 'get',
    params
  });
}

/**
 * 获取房屋统计数据
 * @param {number} propertyId - 房屋ID
 * @returns {Promise} - 返回Promise对象
 */
export function getPropertyStats(propertyId) {
  return request({
    url: `/api/v1/properties/${propertyId}/stats`,
    method: 'get'
  });
}

/**
 * 上传房屋图片
 * @param {number} id - 房屋ID
 * @param {FormData} formData - 图片文件数据
 * @returns {Promise} - 返回Promise对象
 */
export function uploadPropertyImage(id, formData) {
  return request({
    url: `/api/v1/properties/${id}/image`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 更新房屋状态
 * @param {number} id - 房屋ID
 * @param {string} status - 新状态
 * @returns {Promise} - 返回Promise对象
 */
export function updatePropertyStatus(id, status) {
  return request({
    url: `/api/v1/properties/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 批量删除房屋
 * @param {Array} ids - 房屋ID数组
 * @returns {Promise} - 返回Promise对象
 */
export function batchDeleteProperties(ids) {
  return request({
    url: '/api/v1/properties/batch',
    method: 'delete',
    data: { ids }
  });
}

/**
 * 获取房屋收入统计
 * @param {number} propertyId - 房屋ID
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getPropertyRevenue(propertyId, params = {}) {
  return request({
    url: `/api/v1/properties/${propertyId}/revenue`,
    method: 'get',
    params
  });
} 