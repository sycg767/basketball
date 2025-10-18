import request from './request';

// ==================== 会员活跃度分析 ====================

/**
 * 获取会员活跃度趋势
 * @param {Object} params - { startDate, endDate, days }
 */
export function getMemberActivityTrend(params) {
  return request({
    url: '/api/admin/analytics/member/activity-trend',
    method: 'get',
    params
  });
}

/**
 * 获取活跃用户列表
 * @param {Object} params - { startDate, endDate, minScore, page, size }
 */
export function getActiveUsers(params) {
  return request({
    url: '/api/admin/analytics/member/active-users',
    method: 'get',
    params
  });
}

/**
 * 获取不活跃用户列表
 * @param {Object} params - { days, maxScore, page, size }
 */
export function getInactiveUsers(params) {
  return request({
    url: '/api/admin/analytics/member/inactive-users',
    method: 'get',
    params
  });
}

/**
 * 获取流失风险用户列表
 * @param {Object} params - { riskLevel, page, size }
 */
export function getChurnRiskUsers(params) {
  return request({
    url: '/api/admin/analytics/member/churn-risk',
    method: 'get',
    params
  });
}

/**
 * 获取用户活跃度详情
 * @param {Number} userId - 用户ID
 * @param {Object} params - { days }
 */
export function getMemberActivityDetail(userId, params) {
  return request({
    url: '/api/admin/analytics/member/detail',
    method: 'get',
    params: { userId, ...params }
  });
}

/**
 * 手动触发会员活跃度分析
 * @param {Object} data - { date }
 */
export function analyzeMemberActivity(data) {
  return request({
    url: '/api/admin/analytics/member/analyze',
    method: 'post',
    data
  });
}

// ==================== 课程热度分析 ====================

/**
 * 获取课程热度排行榜
 * @param {Object} params - { startDate, endDate, limit }
 */
export function getCoursePopularityRanking(params) {
  return request({
    url: '/api/admin/analytics/course/popularity-ranking',
    method: 'get',
    params
  });
}

/**
 * 获取课程热度趋势
 * @param {Object} params - { courseId, startDate, endDate, days }
 */
export function getCoursePopularityTrend(params) {
  return request({
    url: '/api/admin/analytics/course/popularity-trend',
    method: 'get',
    params
  });
}

/**
 * 获取课程热度详情
 * @param {Number} courseId - 课程ID
 * @param {Object} params - { date }
 */
export function getCoursePopularityDetail(courseId, params) {
  return request({
    url: '/api/admin/analytics/course/popularity-detail',
    method: 'get',
    params: { courseId, ...params }
  });
}

/**
 * 获取热门课程列表
 * @param {Object} params - { startDate, endDate, minScore, limit }
 */
export function getHotCourses(params) {
  return request({
    url: '/api/admin/analytics/course/hot-courses',
    method: 'get',
    params
  });
}

/**
 * 获取冷门课程列表
 * @param {Object} params - { startDate, endDate, maxScore, limit }
 */
export function getColdCourses(params) {
  return request({
    url: '/api/admin/analytics/course/cold-courses',
    method: 'get',
    params
  });
}

/**
 * 获取课程转化率分析
 * @param {Object} params - { startDate, endDate, courseId }
 */
export function getCourseConversionAnalysis(params) {
  return request({
    url: '/api/admin/analytics/course/conversion-analysis',
    method: 'get',
    params
  });
}

/**
 * 手动触发课程热度分析
 * @param {Object} data - { date }
 */
export function analyzeCoursePopularity(data) {
  return request({
    url: '/api/admin/analytics/course/analyze',
    method: 'post',
    data
  });
}

// ==================== 场地使用分析 ====================

/**
 * 获取场地使用率排行榜
 * @param {Object} params - { startDate, endDate, limit }
 */
export function getVenueUsageRanking(params) {
  return request({
    url: '/api/admin/analytics/venue/usage-ranking',
    method: 'get',
    params
  });
}

/**
 * 获取场地使用趋势
 * @param {Object} params - { venueId, startDate, endDate, days }
 */
export function getVenueUsageTrend(params) {
  return request({
    url: '/api/admin/analytics/venue/usage-trend',
    method: 'get',
    params
  });
}

/**
 * 获取场地使用详情
 * @param {Number} venueId - 场地ID
 * @param {Object} params - { date }
 */
export function getVenueUsageDetail(venueId, params) {
  return request({
    url: '/api/admin/analytics/venue/usage-detail',
    method: 'get',
    params: { venueId, ...params }
  });
}

/**
 * 获取高使用率场地列表
 * @param {Object} params - { startDate, endDate, minUsage, limit }
 */
export function getHighUsageVenues(params) {
  return request({
    url: '/api/admin/analytics/venue/high-usage',
    method: 'get',
    params
  });
}

/**
 * 获取低使用率场地列表
 * @param {Object} params - { startDate, endDate, maxUsage, limit }
 */
export function getLowUsageVenues(params) {
  return request({
    url: '/api/admin/analytics/venue/low-usage',
    method: 'get',
    params
  });
}

/**
 * 获取场地收入分析
 * @param {Object} params - { startDate, endDate, venueId }
 */
export function getVenueRevenueAnalysis(params) {
  return request({
    url: '/api/admin/analytics/venue/revenue-analysis',
    method: 'get',
    params
  });
}

/**
 * 获取高峰时段分析
 * @param {Object} params - { startDate, endDate, venueId }
 */
export function getPeakPeriodAnalysis(params) {
  return request({
    url: '/api/admin/analytics/venue/peak-period',
    method: 'get',
    params
  });
}

/**
 * 手动触发场地使用分析
 * @param {Object} data - { date }
 */
export function analyzeVenueUsage(data) {
  return request({
    url: '/api/admin/analytics/venue/analyze',
    method: 'post',
    data
  });
}
