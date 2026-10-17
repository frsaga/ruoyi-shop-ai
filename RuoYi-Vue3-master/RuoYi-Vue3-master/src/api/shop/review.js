import request from '@/utils/request'

// 查询评价列表
export function listReview(query) {
  return request({
    url: '/shop/review/list',
    method: 'get',
    params: query
  })
}

// 根据商品ID查询评价列表（公开接口）
export function getReviewsByProductId(productId) {
  return request({
    url: '/shop/review/product/' + productId,
    method: 'get'
  })
}

// 检查订单商品是否可以评价
export function canReview(orderId, productId) {
  return request({
    url: '/shop/review/canReview/' + orderId + '/' + productId,
    method: 'get'
  })
}

// 获取订单的所有商品及评价状态
export function getOrderItemsWithReviewStatus(orderId) {
  return request({
    url: '/shop/review/orderItems/' + orderId,
    method: 'get'
  })
}

// 查询评价详细
export function getReview(reviewId) {
  return request({
    url: '/shop/review/' + reviewId,
    method: 'get'
  })
}

// 新增评价
export function addReview(data) {
  return request({
    url: '/shop/review',
    method: 'post',
    data: data
  })
}

// 修改评价
export function updateReview(data) {
  return request({
    url: '/shop/review',
    method: 'put',
    data: data
  })
}

// 删除评价
export function delReview(reviewIds) {
  // 如果是数组，转换为逗号分隔的字符串
  const ids = Array.isArray(reviewIds) ? reviewIds.join(',') : reviewIds;
  return request({
    url: '/shop/review/' + ids,
    method: 'delete'
  })
}

// 审核评价
export function auditReview(reviewId, status) {
  return request({
    url: '/shop/review/audit/' + reviewId + '/' + status,
    method: 'put'
  })
}

