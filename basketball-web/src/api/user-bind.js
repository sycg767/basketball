import request from './request';

/**
 * 绑定手机号
 */
export function bindPhone(data) {
  return request({
    url: '/api/user/bind/phone',
    method: 'post',
    data
  });
}

/**
 * 解绑手机号
 */
export function unbindPhone() {
  return request({
    url: '/api/user/bind/phone',
    method: 'delete'
  });
}

/**
 * 绑定微信
 */
export function bindWechat(data) {
  return request({
    url: '/api/wechat/bind',
    method: 'post',
    data
  });
}

/**
 * 解绑微信
 */
export function unbindWechat() {
  return request({
    url: '/api/wechat/unbind',
    method: 'delete'
  });
}
