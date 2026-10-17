import request from '@/utils/request'

// 查询店铺列表
export function listStore(query) {
  return request({
    url: '/shop/store/list',
    method: 'get',
    params: query
  })
}

// 查询店铺详细
export function getStore(storeId) {
  return request({
    url: '/shop/store/' + storeId,
    method: 'get'
  })
}

// 新增店铺
export function addStore(data) {
  return request({
    url: '/shop/store',
    method: 'post',
    data: data
  })
}

// 修改店铺
export function updateStore(data) {
  return request({
    url: '/shop/store',
    method: 'put',
    data: data
  })
}

// 删除店铺
export function delStore(storeId) {
  return request({
    url: '/shop/store/' + storeId,
    method: 'delete'
  })
}

// 审核店铺
export function auditStore(storeId, status) {
  return request({
    url: '/shop/store/audit/' + storeId + '/' + status,
    method: 'put'
  })
}

// 获取店铺公开信息（用于店铺主页）
export function getStorePublic(storeId) {
  return request({
    url: '/shop/store/public/' + storeId,
    method: 'get'
  })
}

