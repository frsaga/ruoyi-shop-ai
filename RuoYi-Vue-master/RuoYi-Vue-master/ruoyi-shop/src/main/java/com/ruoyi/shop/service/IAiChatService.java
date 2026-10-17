package com.ruoyi.shop.service;

/**
 * AI客服聊天Service接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface IAiChatService 
{
    /**
     * 发送消息给AI客服
     * 
     * @param userId 用户ID
     * @param message 用户消息
     * @param conversationId 会话ID（可选，用于保持上下文）
     * @return AI回复
     */
    public String sendMessage(Long userId, String message, String conversationId);
    
    /**
     * 清除会话历史
     * 
     * @param userId 用户ID
     * @param conversationId 会话ID
     */
    public void clearConversation(Long userId, String conversationId);
}

