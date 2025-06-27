import request from './request'

/**
 * 获取租房申请详情
 * @param {number} id 申请ID
 * @returns {Promise}
 */
export function getRoomApplicationById(id) {
  return request({
    url: `/api/v1/landlords/applications/${id}`,
    method: 'get'
  })
}

/**
 * 审核租房申请
 * @param {number} id 申请ID
 * @param {object} data 审核数据
 * @returns {Promise}
 */
export function reviewRoomApplication(id, data) {
  return request({
    url: `/api/v1/landlords/applications/${id}/review`,
    method: 'put',
    data
  })
}

/**
 * 获取房东的租房申请列表
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function getLandlordApplications(params) {
  return request({
    url: '/api/v1/landlords/applications',
    method: 'get',
    params
  })
} 