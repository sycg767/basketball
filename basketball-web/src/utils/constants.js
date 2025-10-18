/**
 * 常量定义
 */

// 预订状态
export const BOOKING_STATUS = {
  PENDING: { value: 'PENDING', label: '待确认', color: 'warning' },
  CONFIRMED: { value: 'CONFIRMED', label: '已确认', color: 'success' },
  CANCELLED: { value: 'CANCELLED', label: '已取消', color: 'info' },
  COMPLETED: { value: 'COMPLETED', label: '已完成', color: 'primary' }
};

// 支付状态
export const PAYMENT_STATUS = {
  UNPAID: { value: 'UNPAID', label: '未支付', color: 'warning' },
  PAID: { value: 'PAID', label: '已支付', color: 'success' },
  REFUNDED: { value: 'REFUNDED', label: '已退款', color: 'info' }
};

// 会员等级
export const MEMBER_LEVEL = {
  0: { label: '普通用户', discount: 1.0, color: '#909399' },
  1: { label: '银卡会员', discount: 0.95, color: '#C0C4CC' },
  2: { label: '金卡会员', discount: 0.9, color: '#F7BA2A' },
  3: { label: '铂金会员', discount: 0.85, color: '#409EFF' },
  4: { label: 'VIP会员', discount: 0.8, color: '#E6A23C' }
};

// 课程状态
export const COURSE_STATUS = {
  DRAFT: { value: 'DRAFT', label: '草稿', color: 'info' },
  PUBLISHED: { value: 'PUBLISHED', label: '已发布', color: 'success' },
  FULL: { value: 'FULL', label: '已满员', color: 'warning' },
  CANCELLED: { value: 'CANCELLED', label: '已取消', color: 'danger' },
  COMPLETED: { value: 'COMPLETED', label: '已完成', color: 'primary' }
};

// 课程难度
export const COURSE_DIFFICULTY = {
  BEGINNER: { value: 'BEGINNER', label: '初级', color: 'success' },
  INTERMEDIATE: { value: 'INTERMEDIATE', label: '中级', color: 'warning' },
  ADVANCED: { value: 'ADVANCED', label: '高级', color: 'danger' }
};

// 场地类型
export const VENUE_TYPE = {
  INDOOR: { value: 'INDOOR', label: '室内', icon: 'house' },
  OUTDOOR: { value: 'OUTDOOR', label: '室外', icon: 'sunny' }
};

// 场地状态
export const VENUE_STATUS = {
  AVAILABLE: { value: 'AVAILABLE', label: '可用', color: 'success' },
  MAINTENANCE: { value: 'MAINTENANCE', label: '维护中', color: 'warning' },
  CLOSED: { value: 'CLOSED', label: '关闭', color: 'danger' }
};

// 性别
export const GENDER = {
  MALE: { value: 'MALE', label: '男' },
  FEMALE: { value: 'FEMALE', label: '女' },
  OTHER: { value: 'OTHER', label: '其他' }
};

// 时间段
export const TIME_SLOTS = [
  { value: '06:00', label: '06:00' },
  { value: '07:00', label: '07:00' },
  { value: '08:00', label: '08:00' },
  { value: '09:00', label: '09:00' },
  { value: '10:00', label: '10:00' },
  { value: '11:00', label: '11:00' },
  { value: '12:00', label: '12:00' },
  { value: '13:00', label: '13:00' },
  { value: '14:00', label: '14:00' },
  { value: '15:00', label: '15:00' },
  { value: '16:00', label: '16:00' },
  { value: '17:00', label: '17:00' },
  { value: '18:00', label: '18:00' },
  { value: '19:00', label: '19:00' },
  { value: '20:00', label: '20:00' },
  { value: '21:00', label: '21:00' },
  { value: '22:00', label: '22:00' }
];

// 分页配置
export const PAGE_SIZE = 10;
export const PAGE_SIZES = [5, 10, 20, 50, 100];

/**
 * 获取预订状态配置
 */
export function getBookingStatus(status) {
  return BOOKING_STATUS[status] || { label: status, color: 'info' };
}

/**
 * 获取支付状态配置
 */
export function getPaymentStatus(status) {
  return PAYMENT_STATUS[status] || { label: status, color: 'info' };
}

/**
 * 获取会员等级配置
 */
export function getMemberLevel(level) {
  return MEMBER_LEVEL[level] || MEMBER_LEVEL[0];
}

/**
 * 获取课程状态配置
 */
export function getCourseStatus(status) {
  return COURSE_STATUS[status] || { label: status, color: 'info' };
}

/**
 * 获取课程难度配置
 */
export function getCourseDifficulty(difficulty) {
  return COURSE_DIFFICULTY[difficulty] || { label: difficulty, color: 'info' };
}

/**
 * 获取场地类型配置
 */
export function getVenueType(type) {
  return VENUE_TYPE[type] || { label: type, icon: 'location' };
}

/**
 * 获取场地状态配置
 */
export function getVenueStatus(status) {
  return VENUE_STATUS[status] || { label: status, color: 'info' };
}
