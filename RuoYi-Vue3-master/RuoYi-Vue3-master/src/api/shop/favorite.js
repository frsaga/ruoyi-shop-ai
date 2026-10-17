import request from '@/utils/request'

// 查询收藏列表
export function listFavorite(query) {
  return request({
    url: '/shop/favorite/list',
    method: 'get',
    params: query
  })
}

// 检查是否已收藏
export function checkFavorite(productId) {
  return request({
    url: '/shop/favorite/check/' + productId,
    method: 'get'
  })
}

// 新增收藏
export function addFavorite(data) {
  return request({
    url: '/shop/favorite',
    method: 'post',
    data: data
  })
}

// 取消收藏
export function cancelFavorite(productId) {
  return request({
    url: '/shop/favorite/product/' + productId,
    method: 'delete'
  })
}

// 批量删除收藏（根据收藏ID）
export function delFavorite(favoriteIds) {
  // 如果是数组，转换为逗号分隔的字符串
  const ids = Array.isArray(favoriteIds) ? favoriteIds.join(',') : favoriteIds;
  return request({
    url: '/shop/favorite/' + ids,
    method: 'delete'
  })
}

