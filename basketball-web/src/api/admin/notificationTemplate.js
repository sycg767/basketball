import request from '@/utils/request'

/**
 * 分页查询通知模板
 */
export function getTemplatePage(params) {
  return request({
    url: '/api/admin/notification-template/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询模板详情
 */
export function getTemplateById(id) {
  return request({
    url: `/api/admin/notification-template/${id}`,
    method: 'get'
  })
}

/**
 * 新增通知模板
 */
export function addTemplate(data) {
  return request({
    url: '/api/admin/notification-template',
    method: 'post',
    data
  })
}

/**
 * 更新通知模板
 */
export function updateTemplate(id, data) {
  return request({
    url: `/api/admin/notification-template/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除通知模板
 */
export function deleteTemplate(id) {
  return request({
    url: `/api/admin/notification-template/${id}`,
    method: 'delete'
  })
}

/**
 * 启用/禁用模板
 */
export function toggleTemplateStatus(id, status) {
  return request({
    url: `/api/admin/notification-template/${id}/status`,
    method: 'put',
    params: { status }
  })
}
