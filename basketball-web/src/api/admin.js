import request from './request';

/**
 * 管理员API
 */

// ==================== 场地管理 ====================

/**
 * 获取场地列表（分页）
 */
export function getAdminVenueList(params) {
  return request({
    url: '/api/venue/list',
    method: 'get',
    params
  });
}

/**
 * 添加场地
 */
export function createVenue(data) {
  return request({
    url: '/api/admin/venue',
    method: 'post',
    data
  });
}

/**
 * 修改场地
 */
export function updateVenue(id, data) {
  return request({
    url: `/api/admin/venue/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除场地
 */
export function deleteVenue(id) {
  return request({
    url: `/api/admin/venue/${id}`,
    method: 'delete'
  });
}

/**
 * 设置场地价格
 */
export function setVenuePrice(data) {
  return request({
    url: '/api/admin/venue/price',
    method: 'post',
    data
  });
}

// ==================== 用户管理 ====================

/**
 * 获取用户列表（分页）
 */
export function getUserList(params) {
  return request({
    url: '/api/admin/user/list',
    method: 'get',
    params
  });
}

/**
 * 更新用户信息
 */
export function updateUser(id, data) {
  return request({
    url: `/api/admin/user/${id}`,
    method: 'put',
    data
  });
}

/**
 * 启用/禁用用户
 */
export function toggleUserStatus(id, status) {
  return request({
    url: `/api/admin/user/${id}/status`,
    method: 'put',
    data: { status }
  });
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/api/admin/user/${id}`,
    method: 'delete'
  });
}

// ==================== 预订管理 ====================

/**
 * 获取预订列表（分页）
 */
export function getBookingList(params) {
  return request({
    url: '/api/admin/booking/list',
    method: 'get',
    params
  });
}

/**
 * 获取预订详情
 */
export function getBookingDetail(id) {
  return request({
    url: `/api/admin/booking/${id}`,
    method: 'get'
  });
}

/**
 * 取消预订
 */
export function cancelBooking(id) {
  return request({
    url: `/api/admin/booking/${id}/cancel`,
    method: 'put'
  });
}

/**
 * 更新预订状态
 */
export function updateBookingStatus(id, status) {
  return request({
    url: `/api/admin/booking/${id}/status`,
    method: 'put',
    data: { status }
  });
}

// ==================== 统计数据 ====================

/**
 * 获取统计数据
 */
export function getStatistics() {
  return request({
    url: '/api/admin/statistics',
    method: 'get'
  });
}

/**
 * 获取预订趋势数据
 */
export function getBookingTrend(params) {
  return request({
    url: '/api/admin/statistics/booking-trend',
    method: 'get',
    params
  });
}

/**
 * 获取场地使用率
 */
export function getVenueUsageRate() {
  return request({
    url: '/api/admin/statistics/venue-usage',
    method: 'get'
  });
}

/**
 * 获取最近预订列表
 */
export function getRecentBookings(limit = 10) {
  return request({
    url: '/api/admin/statistics/recent-bookings',
    method: 'get',
    params: { limit }
  });
}

// ==================== 课程管理 ====================

/**
 * 获取课程列表（管理端）
 */
export function getCourseList(params) {
  return request({
    url: '/api/admin/course',
    method: 'get',
    params
  });
}

/**
 * 获取课程详情（管理端）
 */
export function getCourseDetail(id) {
  return request({
    url: `/api/admin/course/${id}`,
    method: 'get'
  });
}

/**
 * 创建课程
 */
export function createCourse(data) {
  return request({
    url: '/api/admin/course',
    method: 'post',
    data
  });
}

/**
 * 更新课程
 */
export function updateCourse(id, data) {
  return request({
    url: `/api/admin/course/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除课程
 */
export function deleteCourse(id) {
  return request({
    url: `/api/admin/course/${id}`,
    method: 'delete'
  });
}

/**
 * 更新课程状态
 */
export function updateCourseStatus(id, status) {
  return request({
    url: `/api/admin/course/${id}/status`,
    method: 'put',
    params: { status }
  });
}

/**
 * 获取课程排期列表
 */
export function getScheduleList(courseId, params) {
  return request({
    url: '/api/admin/course/schedule',
    method: 'get',
    params: {
      courseId,
      ...params
    }
  });
}

/**
 * 创建课程排期
 */
export function createSchedule(data) {
  return request({
    url: '/api/admin/course/schedule',
    method: 'post',
    data
  });
}

/**
 * 取消课程排期
 */
export function cancelSchedule(id, reason) {
  return request({
    url: `/api/admin/course/schedule/${id}/cancel`,
    method: 'put',
    params: { reason }
  });
}

/**
 * 获取排期报名列表
 */
export function getEnrollmentList(scheduleId, params) {
  return request({
    url: '/api/admin/course/enrollment',
    method: 'get',
    params: {
      scheduleId,
      ...params
    }
  });
}

/**
 * 学员签到
 */
export function checkInStudent(enrollmentId) {
  return request({
    url: `/api/admin/course/enrollment/${enrollmentId}/check-in`,
    method: 'put'
  });
}

/**
 * 获取教练列表（用于课程分配）
 */
export function getCoachList(params) {
  return request({
    url: '/api/admin/coach/list',
    method: 'get',
    params
  });
}

// ==================== 会员管理 ====================

/**
 * 获取会员卡类型列表
 */
export function getCardTypes(params) {
  return request({
    url: '/api/admin/member/card-types',
    method: 'get',
    params
  });
}

/**
 * 创建会员卡类型
 */
export function createCardType(data) {
  return request({
    url: '/api/admin/member/card-type',
    method: 'post',
    data
  });
}

/**
 * 更新会员卡类型
 */
export function updateCardType(id, data) {
  return request({
    url: `/api/admin/member/card-type/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除会员卡类型
 */
export function deleteCardType(id) {
  return request({
    url: `/api/admin/member/card-type/${id}`,
    method: 'delete'
  });
}

/**
 * 更新会员卡类型状态
 */
export function updateCardTypeStatus(id, status) {
  return request({
    url: `/api/admin/member/card-type/${id}/status`,
    method: 'put',
    params: { status }
  });
}

/**
 * 获取所有会员卡列表
 */
export function getAllCards(params) {
  return request({
    url: '/api/admin/member/cards',
    method: 'get',
    params
  });
}

/**
 * 激活会员卡
 */
export function activateCard(id) {
  return request({
    url: `/api/admin/member/card/${id}/activate`,
    method: 'put'
  });
}

/**
 * 冻结会员卡
 */
export function freezeCard(id) {
  return request({
    url: `/api/admin/member/card/${id}/freeze`,
    method: 'put'
  });
}
