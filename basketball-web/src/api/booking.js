import request from './request';

/**
 * 预订相关API
 */

/**
 * 创建预订
 */
export function createBooking(data) {
  return request({
    url: '/api/booking',
    method: 'post',
    data
  });
}

/**
 * 获取我的预订列表
 */
export function getMyBookingList(params) {
  return request({
    url: '/api/booking/my',
    method: 'get',
    params
  });
}

/**
 * 获取预订详情
 */
export function getBookingDetail(id) {
  return request({
    url: `/api/booking/${id}`,
    method: 'get'
  });
}

/**
 * 取消预订
 */
export function cancelBooking(id, data) {
  return request({
    url: `/api/booking/${id}/cancel`,
    method: 'put',
    data
  });
}

/**
 * 支付预订
 */
export function payBooking(id, data) {
  return request({
    url: `/api/booking/${id}/pay`,
    method: 'put',
    data
  });
}

/**
 * 检查场地时间段是否可用
 */
export function checkVenueAvailable(params) {
  return request({
    url: '/api/booking/check-available',
    method: 'get',
    params
  });
}
