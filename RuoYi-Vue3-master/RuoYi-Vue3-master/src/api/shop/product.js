import request from '@/utils/request'

// 查询商品列表（商品管理页面使用，需要权限）
export function listProduct(query) {
  return request({
    url: '/shop/product/list',
    method: 'get',
    params: query
  })
}

// 公开的商品列表查询（商城首页使用，只需登录，无需权限）
export function listPublicProduct(query) {
  return request({
    url: '/shop/product/public/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细（商品管理页面使用，需要权限）
export function getProduct(productId) {
  return request({
    url: '/shop/product/' + productId,
    method: 'get'
  })
}

// 公开的商品详情查询（商城首页使用，只需登录，无需权限）
export function getPublicProduct(productId) {
  return request({
    url: '/shop/product/public/' + productId,
    method: 'get'
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/shop/product',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/shop/product',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delProduct(productId) {
  return request({
    url: '/shop/product/' + productId,
    method: 'delete'
  })
}

// 审核商品
export function auditProduct(productId, status) {
  return request({
    url: '/shop/product/audit/' + productId + '/' + status,
    method: 'put'
  })
}

// 根据店铺ID查询商品列表
export function listProductByStore(storeId, query) {
  return request({
    url: '/shop/product/store/' + storeId,
    method: 'get',
    params: query
  })
}

