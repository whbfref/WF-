import request from './request';

/**
 * 获取房源列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export function getHousingList(params) {
  return request({
    url: '/api/v1/properties',
    method: 'get',
    params
  });
}

/**
 * 获取房源详情
 * @param {number} id - 房源ID
 * @returns {Promise} - 返回Promise对象
 */
export function getHousingDetail(id) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'get'
  });
}

/**
 * 添加房源
 * @param {Object} data - 房源数据
 * @returns {Promise} - 返回Promise对象
 */
export function addHousing(data) {
  return request({
    url: '/api/v1/properties',
    method: 'post',
    data
  });
}

/**
 * 更新房源
 * @param {number} id - 房源ID
 * @param {Object} data - 房源数据
 * @returns {Promise} - 返回Promise对象
 */
export function updateHousing(id, data) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除房源
 * @param {number} id - 房源ID
 * @returns {Promise} - 返回Promise对象
 */
export function deleteHousing(id) {
  return request({
    url: `/api/v1/properties/${id}`,
    method: 'delete'
  });
}

/**
 * 更新房源状态
 * @param {number} id - 房源ID
 * @param {string} status - 房源状态
 * @returns {Promise} - 返回Promise对象
 */
export function updateHousingStatus(id, status) {
  return request({
    url: `/api/v1/properties/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 上传房源图片
 * @param {FormData} formData - 包含图片文件的FormData对象
 * @returns {Promise} - 返回Promise对象
 */
export function uploadHousingImage(formData) {
  return request({
    url: '/api/v1/upload/housing-image',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  });
} 