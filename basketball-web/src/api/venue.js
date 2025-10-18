import request from './request';

/**
 * 获取场地列表（分页）
 */
export function getVenueList(params) {
  return request({
    url: '/api/venue/list',
    method: 'get',
    params
  });
}

/**
 * 获取场地详情
 */
export function getVenueDetail(id) {
  return request({
    url: `/api/venue/${id}`,
    method: 'get'
  });
}

/**
 * 获取可用场地列表
 */
export function getAvailableVenues() {
  return request({
    url: '/api/venue/available',
    method: 'get'
  });
}

/**
 * 获取场地价格列表
 */
export function getVenuePrices(id) {
  return request({
    url: `/api/venue/${id}/prices`,
    method: 'get'
  });
}
