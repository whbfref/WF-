import request from './request'

/**
 * 获取用户通知列表
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function getUserNotifications(params) {
  return request({
    url: '/api/v1/notifications',
    method: 'get',
    params
  })
}

/**
 * 标记通知为已读
 * @param {number} notificationId 通知ID
 * @returns {Promise}
 */
export function markNotificationAsRead(notificationId) {
  return request({
    url: `/api/v1/notifications/${notificationId}/read`,
    method: 'put'
  })
}

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export function markAllNotificationsAsRead() {
  return request({
    url: '/api/v1/notifications/read-all',
    method: 'put'
  })
}

/**
 * 获取未读通知数量
 * @returns {Promise}
 */
export function getUnreadNotificationCount() {
  return request({
    url: '/api/v1/notifications/unread-count',
    method: 'get'
  })
}

/**
 * 获取通知详情
 * @param {number} notificationId 通知ID
 * @returns {Promise}
 */
export function getNotificationById(notificationId) {
  return request({
    url: `/api/v1/notifications/${notificationId}`,
    method: 'get'
  })
} 