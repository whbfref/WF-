import request from './request';

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} - 返回Promise对象
 */
export function login(data) {
  return request({
    url: '/api/v1/auth/login',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  });
}

/**
 * 测试登录
 * @param {Object} data - 登录信息
 * @returns {Promise} - 返回Promise对象
 */
export function testLogin(data) {
  return request({
    url: '/api/v1/test-login',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  });
}

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @returns {Promise} - 返回Promise对象
 */
export function register(data) {
  return request({
    url: '/api/v1/auth/register',
    method: 'post',
    data
  });
}

/**
 * 用户退出登录
 * @returns {Promise} - 返回Promise对象
 */
export function logout() {
  return request({
    url: '/api/v1/auth/logout',
    method: 'post'
  });
}

/**
 * 获取用户信息
 * @returns {Promise} - 返回Promise对象
 */
export function getUserInfo() {
  return request({
    url: '/api/v1/auth/info',
    method: 'get'
  });
}

/**
 * 获取验证码
 * @param {string} phone - 手机号
 * @returns {Promise} - 返回Promise对象
 */
export function getVerifyCode(phone) {
  return request({
    url: '/api/v1/auth/verify-code',
    method: 'get',
    params: { phone }
  });
}

/**
 * 重置密码
 * @param {Object} data - 重置密码信息
 * @param {string} data.phone - 手机号
 * @param {string} data.verifyCode - 验证码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} - 返回Promise对象
 */
export function resetPassword(data) {
  return request({
    url: '/api/v1/auth/reset-password',
    method: 'post',
    data
  });
}

/**
 * 测试登录 - 更详细的诊断版本
 * @param {Object} data - 登录信息
 * @returns {Promise} - 返回Promise对象
 */
export function debugLogin(data) {
  console.log('发送调试登录请求数据:', data);
  return request({
    url: '/api/v1/auth/test-login',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: {
      username: data.username,
      password: data.password
    }
  });
}

/**
 * 测试GET请求的登录接口
 * @returns {Promise} - 返回Promise对象
 */
export function testLoginGet() {
  return request({
    url: '/api/v1/auth/login-test',
    method: 'get'
  });
} 