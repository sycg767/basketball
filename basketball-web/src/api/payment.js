import request from './request';

/**
 * 创建支付订单(统一接口)
 */
export function createPayment(data) {
  return request({
    url: '/api/payment/create',
    method: 'post',
    data
  });
}

/**
 * 查询支付状态
 */
export function queryPayment(paymentNo) {
  return request({
    url: `/api/payment/query/${paymentNo}`,
    method: 'get'
  });
}

/**
 * 取消支付
 */
export function cancelPayment(paymentNo) {
  return request({
    url: `/api/payment/cancel/${paymentNo}`,
    method: 'post'
  });
}

/**
 * 创建微信Native扫码支付
 */
export function createWechatNativePay(data) {
  return request({
    url: '/api/payment/wechat/native',
    method: 'post',
    data
  });
}

/**
 * 创建微信JSAPI支付
 */
export function createWechatJsapiPay(data) {
  return request({
    url: '/api/payment/wechat/jsapi',
    method: 'post',
    data
  });
}

/**
 * 创建微信H5支付
 */
export function createWechatH5Pay(data) {
  return request({
    url: '/api/payment/wechat/h5',
    method: 'post',
    data
  });
}

/**
 * 查询微信支付订单
 */
export function queryWechatPayment(orderNo) {
  return request({
    url: `/api/payment/wechat/query/${orderNo}`,
    method: 'get'
  });
}

/**
 * 关闭微信支付订单
 */
export function closeWechatPayment(paymentNo) {
  return request({
    url: `/api/payment/wechat/close/${paymentNo}`,
    method: 'post'
  });
}

/**
 * 创建支付宝电脑网站支付
 */
export function createAlipayPagePay(data) {
  return request({
    url: '/api/payment/alipay/page',
    method: 'post',
    data
  });
}

/**
 * 创建支付宝手机网站支付
 */
export function createAlipayWapPay(data) {
  return request({
    url: '/api/payment/alipay/wap',
    method: 'post',
    data
  });
}

/**
 * 查询支付宝支付订单
 */
export function queryAlipayment(paymentNo) {
  return request({
    url: `/api/payment/alipay/query/${paymentNo}`,
    method: 'get'
  });
}

/**
 * 关闭支付宝支付订单
 */
export function closeAlipayment(paymentNo) {
  return request({
    url: `/api/payment/alipay/close/${paymentNo}`,
    method: 'post'
  });
}

/**
 * 模拟支付成功（仅测试环境）
 */
export function mockPaymentSuccess(paymentNo) {
  return request({
    url: `/api/mock/payment/scan-success/${paymentNo}`,
    method: 'post'
  });
}

/**
 * 模拟支付失败（仅测试环境）
 */
export function mockPaymentFail(paymentNo) {
  return request({
    url: `/api/mock/payment/scan-fail/${paymentNo}`,
    method: 'post'
  });
}
