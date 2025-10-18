/**
 * 工具函数入口
 */

/**
 * 格式化金额
 * @param {Number} amount - 金额
 * @returns {String}
 */
export function formatMoney(amount) {
  if (amount === null || amount === undefined) return '¥0.00';
  return `¥${Number(amount).toFixed(2)}`;
}

/**
 * 格式化手机号
 * @param {String} phone - 手机号
 * @returns {String}
 */
export function formatPhone(phone) {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
}

/**
 * 验证手机号
 * @param {String} phone - 手机号
 * @returns {Boolean}
 */
export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone);
}

/**
 * 验证邮箱
 * @param {String} email - 邮箱
 * @returns {Boolean}
 */
export function isValidEmail(email) {
  return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);
}

/**
 * 防抖函数
 * @param {Function} func - 函数
 * @param {Number} wait - 等待时间
 * @returns {Function}
 */
export function debounce(func, wait = 300) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * 节流函数
 * @param {Function} func - 函数
 * @param {Number} wait - 等待时间
 * @returns {Function}
 */
export function throttle(func, wait = 300) {
  let timeout;
  return function executedFunction(...args) {
    if (!timeout) {
      func(...args);
      timeout = setTimeout(() => {
        timeout = null;
      }, wait);
    }
  };
}

/**
 * 深拷贝
 * @param {Object} obj - 对象
 * @returns {Object}
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj);
  if (obj instanceof Array) return obj.map(item => deepClone(item));

  const cloneObj = {};
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      cloneObj[key] = deepClone(obj[key]);
    }
  }
  return cloneObj;
}

/**
 * 生成UUID
 * @returns {String}
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

/**
 * 下载文件
 * @param {String} url - 文件URL
 * @param {String} filename - 文件名
 */
export function downloadFile(url, filename) {
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/**
 * 获取文件扩展名
 * @param {String} filename - 文件名
 * @returns {String}
 */
export function getFileExtension(filename) {
  if (!filename) return '';
  const parts = filename.split('.');
  return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : '';
}

/**
 * 格式化文件大小
 * @param {Number} bytes - 字节数
 * @returns {String}
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}
