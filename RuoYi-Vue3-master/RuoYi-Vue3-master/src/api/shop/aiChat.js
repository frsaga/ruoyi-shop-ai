import request from '@/utils/request'

// 发送消息给AI客服
export function sendAiMessage(data) {
  return request({
    url: '/shop/ai-chat/send',
    method: 'post',
    data: data
  })
}

// 清除会话历史
export function clearAiConversation(data) {
  return request({
    url: '/shop/ai-chat/clear',
    method: 'post',
    data: data
  })
}

