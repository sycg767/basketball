/**
 * HTTP请求工具
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 移除/api前缀，因为vite代理会处理
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const { data } = response

    // 这里假设后端返回格式为 { code: 200, data: {}, message: 'success' }
    if (data.code === 200) {
      return data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    console.error('响应错误:', error)

    let errorMessage = '网络错误'

    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 400:
          errorMessage = data.message || '请求参数错误'
          break
        case 401:
          errorMessage = '登录已过期，请重新登录'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/admin/login'
          break
        case 403:
          errorMessage = '没有权限访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      errorMessage = '网络连接失败'
    }

    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

// GET请求
export const get = (url, params = {}, config = {}) => {
  return service({
    method: 'get',
    url,
    params,
    ...config
  })
}

// POST请求
export const post = (url, data = {}, config = {}) => {
  return service({
    method: 'post',
    url,
    data,
    ...config
  })
}

// PUT请求
export const put = (url, data = {}, config = {}) => {
  return service({
    method: 'put',
    url,
    data,
    ...config
  })
}

// PATCH请求
export const patch = (url, data = {}, config = {}) => {
  return service({
    method: 'patch',
    url,
    data,
    ...config
  })
}

// DELETE请求
export const del = (url, config = {}) => {
  return service({
    method: 'delete',
    url,
    ...config
  })
}

// 文件上传
export const upload = (url, file, onProgress = null, config = {}) => {
  const formData = new FormData()
  formData.append('file', file)

  return service({
    method: 'post',
    url,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? (progressEvent) => {
      const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percent)
    } : undefined,
    ...config
  })
}

// 导出request对象
export const request = {
  get,
  post,
  put,
  patch,
  delete: del,
  upload
}

export default request