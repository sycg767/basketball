/**
 * 表单验证工具类
 */

/**
 * 验证手机号
 */
export function validatePhone(phone) {
  const reg = /^1[3-9]\d{9}$/;
  return reg.test(phone);
}

/**
 * 验证用户名（3-20位字母数字下划线）
 */
export function validateUsername(username) {
  const reg = /^[a-zA-Z0-9_]{3,20}$/;
  return reg.test(username);
}

/**
 * 验证密码（6-20位）
 */
export function validatePassword(password) {
  return password && password.length >= 6 && password.length <= 20;
}

/**
 * 验证邮箱
 */
export function validateEmail(email) {
  const reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  return reg.test(email);
}