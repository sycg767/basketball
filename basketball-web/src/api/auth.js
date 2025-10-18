import request from './request';

/**
 * 发送短信验证码
 */
export function sendSmsCode(data) {
  return request({
    url: '/api/sms/send',
    method: 'post',
    data
  });
}

/**
 * 手机验证码登录
 */
export function loginByPhone(data) {
  return request({
    url: '/api/auth/phone/login',
    method: 'post',
    data
  });
}

/**
 * 获取微信授权URL
 */
export function getWechatAuthUrl() {
  return request({
    url: '/api/wechat/auth/url',
    method: 'get'
  });
}

/**
 * 用户名密码登录
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  });
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  });
}
