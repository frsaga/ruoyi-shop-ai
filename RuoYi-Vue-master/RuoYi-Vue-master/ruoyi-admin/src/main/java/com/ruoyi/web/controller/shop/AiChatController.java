package com.ruoyi.web.controller.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.service.IAiChatService;

/**
 * AI客服Controller
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/shop/ai-chat")
public class AiChatController extends BaseController
{
    @Autowired
    private IAiChatService aiChatService;

    /**
     * 发送消息给AI客服
     */
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody AiChatRequest request)
    {
        Long userId = SecurityUtils.getUserId();
        String message = request.getMessage();
        String conversationId = request.getConversationId();
        
        if (message == null || message.trim().isEmpty())
        {
            return error("消息内容不能为空");
        }
        
        String response = aiChatService.sendMessage(userId, message, conversationId);
        
        return success(new AiChatResponse(response, conversationId));
    }

    /**
     * 清除会话历史
     */
    @PostMapping("/clear")
    public AjaxResult clearConversation(@RequestBody(required = false) ClearConversationRequest request)
    {
        Long userId = SecurityUtils.getUserId();
        String conversationId = request != null ? request.getConversationId() : null;
        
        aiChatService.clearConversation(userId, conversationId);
        
        return success("会话已清除");
    }

    /**
     * 请求对象
     */
    public static class AiChatRequest
    {
        private String message;
        private String conversationId;

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getConversationId()
        {
            return conversationId;
        }

        public void setConversationId(String conversationId)
        {
            this.conversationId = conversationId;
        }
    }

    /**
     * 响应对象
     */
    public static class AiChatResponse
    {
        private String response;
        private String conversationId;

        public AiChatResponse(String response, String conversationId)
        {
            this.response = response;
            this.conversationId = conversationId;
        }

        public String getResponse()
        {
            return response;
        }

        public void setResponse(String response)
        {
            this.response = response;
        }

        public String getConversationId()
        {
            return conversationId;
        }

        public void setConversationId(String conversationId)
        {
            this.conversationId = conversationId;
        }
    }

    /**
     * 清除会话请求对象
     */
    public static class ClearConversationRequest
    {
        private String conversationId;

        public String getConversationId()
        {
            return conversationId;
        }

        public void setConversationId(String conversationId)
        {
            this.conversationId = conversationId;
        }
    }
}

