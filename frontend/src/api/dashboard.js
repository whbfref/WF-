import request from './request';

/**
 * 获取仪表盘数据
 * @returns {Promise} - 返回Promise对象
 */
export function getDashboardData() {
  return request({
    url: '/api/v1/dashboard/data',
    method: 'get'
  });
}

/**
 * 获取用户访问来源数据
 * @returns {Promise} - 返回Promise对象
 */
export function getVisitSourceData() {
  return request({
    url: '/api/v1/dashboard/visit-source',
    method: 'get'
  });
}

/**
 * 获取每周用户活跃量数据
 * @returns {Promise} - 返回Promise对象
 */
export function getWeeklyActivityData() {
  return request({
    url: '/api/v1/dashboard/weekly-activity',
    method: 'get'
  });
}

/**
 * 获取每年租金变化数据
 * @returns {Promise} - 返回Promise对象
 */
export function getYearlyRentData() {
  return request({
    url: '/api/v1/dashboard/yearly-rent',
    method: 'get'
  });
}

/**
 * 获取每月租金变化数据
 * @returns {Promise} - 返回Promise对象
 */
export function getMonthlyRentData() {
  return request({
    url: '/api/v1/dashboard/monthly-rent',
    method: 'get'
  });
} 