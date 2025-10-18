import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { getToken, removeToken } from '@/utils/auth';

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加Token
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;

    // 根据返回的code判断
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败');

      // 401: 未授权，跳转登录
      if (res.code === 401) {
        removeToken();
        router.push('/login');
      }

      return Promise.reject(new Error(res.message || 'Error'));
    }

    return res;
  },
  error => {
    console.error('响应错误：', error);

    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('请先登录');
          removeToken();
          router.push('/login');
          break;
        case 403:
          ElMessage.error('没有权限');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器错误');
          break;
        default:
          ElMessage.error(error.response.data.message || '请求失败');
      }
    } else {
      ElMessage.error('网络错误，请稍后重试');
    }

    return Promise.reject(error);
  }
);

export default service;