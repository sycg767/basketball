import request from './request'

/**
 * 获取会员卡类型列表
 */
export const getCardTypes = () => {
  return request({
    url: '/api/member/card-types',
    method: 'get'
  })
}

/**
 * 购买会员卡
 */
export const purchaseCard = (data) => {
  return request({
    url: '/api/member/card/purchase',
    method: 'post',
    data
  })
}

/**
 * 会员卡充值
 */
export const rechargeCard = (data) => {
  return request({
    url: '/api/member/card/recharge',
    method: 'post',
    data
  })
}

/**
 * 获取我的会员卡列表
 */
export const getMyCards = (params) => {
  return request({
    url: '/api/member/card/my',
    method: 'get',
    params
  })
}

/**
 * 获取会员卡使用记录
 */
export const getCardRecords = (params) => {
  return request({
    url: '/api/member/card/records',
    method: 'get',
    params
  })
}

/**
 * 获取我的积分
 */
export const getMyPoints = () => {
  return request({
    url: '/api/member/points',
    method: 'get'
  })
}

/**
 * 获取积分明细
 */
export const getPointsRecords = (params) => {
  return request({
    url: '/api/member/points/records',
    method: 'get',
    params
  })
}

/**
 * 获取账户余额
 */
export const getUserBalance = () => {
  return request({
    url: '/api/member/balance',
    method: 'get'
  })
}

/**
 * 激活会员卡
 */
export const activateCard = (cardId) => {
  return request({
    url: '/api/member/card/activate',
    method: 'post',
    params: { cardId }
  })
}

/**
 * 账户余额充值
 */
export const rechargeBalance = (data) => {
  return request({
    url: '/api/member/balance/recharge',
    method: 'post',
    data
  })
}

/**
 * 获取余额充值记录
 */
export const getBalanceRechargeRecords = (params) => {
  return request({
    url: '/api/member/balance/records',
    method: 'get',
    params
  })
}
