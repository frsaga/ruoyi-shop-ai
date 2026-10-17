import request from '@/utils/request'

// 查询聊天列表
export function listChat(query) {
  return request({
    url: '/shop/chat/list',
    method: 'get',
    params: query
  })
}

// 查询买家聊天会话列表
export function getBuyerSessions() {
  return request({
    url: '/shop/chat/public/sessions/buyer',
    method: 'get'
  })
}

// 查询卖家聊天会话列表
export function getSellerSessions() {
  return request({
    url: '/shop/chat/public/sessions/seller',
    method: 'get'
  })
}

// 查询聊天历史记录
export function getChatHistory(buyerId, sellerId, storeId) {
  return request({
    url: '/shop/chat/public/history',
    method: 'get',
    params: { 
      buyerId, 
      sellerId,
      storeId
    }
  })
}

// 查询未读消息数量
export function getUnreadCount(userType) {
  return request({
    url: '/shop/chat/unread/count',
    method: 'get',
    params: { userType }
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/shop/chat/public',
    method: 'post',
    data: data
  })
}

// 标记消息为已读
export function markAsRead(buyerId, sellerId, userType, storeId) {
  return request({
    url: '/shop/chat/public/read',
    method: 'put',
    params: { 
      buyerId, 
      sellerId, 
      userType,
      storeId
    }
  })
}

// 删除聊天记录
export function delChat(chatIds) {
  return request({
    url: '/shop/chat/' + chatIds,
    method: 'delete'
  })
}

