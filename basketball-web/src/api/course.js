import request from './request';

/**
 * 获取课程列表
 */
export function getCourseList(params) {
  return request({
    url: '/api/course',
    method: 'get',
    params
  });
}

/**
 * 获取课程详情
 */
export function getCourseDetail(id) {
  return request({
    url: `/api/course/${id}`,
    method: 'get'
  });
}

/**
 * 获取课程排期
 */
export function getCourseSchedules(id, params) {
  return request({
    url: `/api/course/${id}/schedules`,
    method: 'get',
    params
  });
}

/**
 * 报名课程
 */
export function enrollCourse(data) {
  return request({
    url: '/api/course/enroll',
    method: 'post',
    data
  });
}

/**
 * 取消报名
 */
export function cancelEnrollment(id) {
  return request({
    url: `/api/course/enrollment/${id}`,
    method: 'delete'
  });
}

/**
 * 获取我的课程报名
 */
export function getMyEnrollments(params) {
  return request({
    url: '/api/course/my-enrollments',
    method: 'get',
    params
  });
}

/**
 * 获取报名详情
 */
export function getEnrollmentDetail(id) {
  return request({
    url: `/api/course/enrollment/${id}`,
    method: 'get'
  });
}

/**
 * 评价课程
 */
export function rateCourse(id, data) {
  return request({
    url: `/api/course/enrollment/${id}/rate`,
    method: 'put',
    params: data
  });
}

/**
 * 计算课程价格
 */
export function calculateCoursePrice(data) {
  return request({
    url: '/api/course/calculate-price',
    method: 'post',
    data
  });
}

/**
 * 支付课程报名
 */
export function payEnrollment(id, paymentData) {
  return request({
    url: `/api/course/enrollment/${id}/pay`,
    method: 'put',
    data: {
      paymentMethod: paymentData.paymentMethod,
      paymentType: paymentData.paymentType || ''
    }
  });
}
