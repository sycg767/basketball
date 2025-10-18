import request from './request';

/**
 * 获取未读通知列表
 * @param {Object} params - 查询参数 { page, size }
 */
export function getUnreadNotifications(params) {
  return request({
    url: '/api/notification/unread',
    method: 'get',
    params
  });
}

/**
 * 获取所有通知列表
 * @param {Object} params - 查询参数 { page, size, type }
 */
export function getNotificationList(params) {
  return request({
    url: '/api/notification/list',
    method: 'get',
    params
  });
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request({
    url: '/api/notification/unread-count',
    method: 'get'
  });
}

/**
 * 标记单条通知为已读
 * @param {Number} id - 通知ID
 */
export function markAsRead(id) {
  return request({
    url: `/api/notification/read/${id}`,
    method: 'post'
  });
}

/**
 * 标记所有通知为已读
 */
export function markAllAsRead() {
  return request({
    url: '/api/notification/read-all',
    method: 'post'
  });
}

/**
 * 删除通知
 * @param {Number} id - 通知ID
 */
export function deleteNotification(id) {
  return request({
    url: `/api/notification/${id}`,
    method: 'delete'
  });
}
