/**
 * 数据格式统一处理工具
 */

/**
 * 统一分页数据格式
 * @param {Object} data - 后端返回的分页数据
 * @returns {Object} 统一格式的分页数据
 */
export const normalizePageData = (data) => {
  if (!data) {
    return {
      list: [],
      total: 0,
      current: 1,
      size: 10
    };
  }

  return {
    list: data.records || data.list || [],
    total: data.total || 0,
    current: data.current || 1,
    size: data.size || 10
  };
};
