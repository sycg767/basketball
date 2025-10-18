/**
 * 日期时间工具类
 */
import dayjs from 'dayjs';

/**
 * 格式化日期
 * @param {Date|String} date - 日期
 * @param {String} format - 格式
 * @returns {String}
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  return dayjs(date).format(format);
}

/**
 * 格式化日期时间
 * @param {Date|String} date - 日期
 * @param {String} format - 格式
 * @returns {String}
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';
  return dayjs(date).format(format);
}

/**
 * 格式化时间
 * @param {String} time - 时间字符串
 * @param {String} format - 格式
 * @returns {String}
 */
export function formatTime(time, format = 'HH:mm') {
  if (!time) return '';
  return dayjs(time, 'HH:mm:ss').format(format);
}

/**
 * 获取当前日期
 * @param {String} format - 格式
 * @returns {String}
 */
export function getCurrentDate(format = 'YYYY-MM-DD') {
  return dayjs().format(format);
}

/**
 * 获取当前时间
 * @param {String} format - 格式
 * @returns {String}
 */
export function getCurrentDateTime(format = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs().format(format);
}

/**
 * 判断是否是今天
 * @param {Date|String} date - 日期
 * @returns {Boolean}
 */
export function isToday(date) {
  if (!date) return false;
  return dayjs(date).isSame(dayjs(), 'day');
}

/**
 * 判断日期是否在指定日期之前
 * @param {Date|String} date - 日期
 * @param {Date|String} targetDate - 目标日期
 * @returns {Boolean}
 */
export function isBefore(date, targetDate) {
  return dayjs(date).isBefore(dayjs(targetDate));
}

/**
 * 判断日期是否在指定日期之后
 * @param {Date|String} date - 日期
 * @param {Date|String} targetDate - 目标日期
 * @returns {Boolean}
 */
export function isAfter(date, targetDate) {
  return dayjs(date).isAfter(dayjs(targetDate));
}

/**
 * 计算两个日期之间的天数
 * @param {Date|String} startDate - 开始日期
 * @param {Date|String} endDate - 结束日期
 * @returns {Number}
 */
export function daysBetween(startDate, endDate) {
  return dayjs(endDate).diff(dayjs(startDate), 'day');
}

/**
 * 添加天数
 * @param {Date|String} date - 日期
 * @param {Number} days - 天数
 * @returns {String}
 */
export function addDays(date, days) {
  return dayjs(date).add(days, 'day').format('YYYY-MM-DD');
}

/**
 * 减少天数
 * @param {Date|String} date - 日期
 * @param {Number} days - 天数
 * @returns {String}
 */
export function subtractDays(date, days) {
  return dayjs(date).subtract(days, 'day').format('YYYY-MM-DD');
}
