import request from '@/utils/request'

// 查询收货地址列表
export function listAddress() {
  return request({
    url: '/shop/address/list',
    method: 'get'
  })
}

// 查询收货地址详细
export function getAddress(addressId) {
  return request({
    url: '/shop/address/' + addressId,
    method: 'get'
  })
}

// 新增收货地址
export function addAddress(data) {
  return request({
    url: '/shop/address',
    method: 'post',
    data: data
  })
}

// 修改收货地址
export function updateAddress(data) {
  return request({
    url: '/shop/address',
    method: 'put',
    data: data
  })
}

// 设置默认地址
export function setDefaultAddress(addressId) {
  return request({
    url: '/shop/address/default/' + addressId,
    method: 'put'
  })
}

// 删除收货地址
export function delAddress(addressId) {
  return request({
    url: '/shop/address/' + addressId,
    method: 'delete'
  })
}

