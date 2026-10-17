import request from '@/utils/request'

// 查询购物车列表
export function listCart() {
  return request({
    url: '/shop/cart/list',
    method: 'get'
  })
}

// 添加商品到购物车
export function addToCart(productId, quantity) {
  return request({
    url: '/shop/cart/' + productId + '/' + quantity,
    method: 'post'
  })
}

// 更新购物车商品数量
export function updateCart(cartId, quantity) {
  return request({
    url: '/shop/cart/' + cartId + '/' + quantity,
    method: 'put'
  })
}

// 删除购物车商品
export function delCart(cartId) {
  return request({
    url: '/shop/cart/' + cartId,
    method: 'delete'
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/shop/cart/clear',
    method: 'delete'
  })
}

