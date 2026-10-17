import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/shop/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(orderId) {
  return request({
    url: '/shop/order/' + orderId,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/shop/order',
    method: 'post',
    data: data
  })
}

// 批量创建订单（按店铺分组）
export function createOrdersBatch(data) {
  return request({
    url: '/shop/order/public/batch',
    method: 'post',
    data: data
  })
}

// 支付订单
export function payOrder(orderId) {
  return request({
    url: '/shop/order/public/pay/' + orderId,
    method: 'put'
  })
}

// 批量支付订单
export function payOrdersBatch(orderIds) {
  return request({
    url: '/shop/order/public/pay/batch',
    method: 'put',
    data: orderIds
  })
}

// 发货
export function sendOrder(orderId) {
  return request({
    url: '/shop/order/send/' + orderId,
    method: 'put'
  })
}

// 确认收货
export function confirmOrder(orderId) {
  return request({
    url: '/shop/order/confirm/' + orderId,
    method: 'put'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: '/shop/order/public/cancel/' + orderId,
    method: 'put'
  })
}

