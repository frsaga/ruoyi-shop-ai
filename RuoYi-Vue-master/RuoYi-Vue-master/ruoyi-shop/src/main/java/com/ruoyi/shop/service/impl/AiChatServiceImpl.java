package com.ruoyi.shop.service.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.shop.service.IAiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI客服聊天Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class AiChatServiceImpl implements IAiChatService 
{
    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);
    
    @Value("${QWEN_API_KEY:}")
    private String apiKey;
    
    @Value("${qwen.apiUrl:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String apiUrl;
    
    @Value("${qwen.model:qwen-turbo}")
    private String model;
    
    @Value("${qwen.maxTokens:2000}")
    private Integer maxTokens;
    
    @Value("${qwen.temperature:0.7}")
    private Double temperature;
    
    @Value("${qwen.systemPrompt:你是一个友好的AI客服助手，专门帮助用户解答关于商城购物的问题。请用简洁、友好、专业的语言回答用户的问题。}")
    private String systemPrompt;
    
    @Autowired
    private AiDataQueryHelper dataQueryHelper;
    
    private final RestTemplate restTemplate;
    
    public AiChatServiceImpl()
    {
        this.restTemplate = new RestTemplate();
    }
    
    // 存储用户会话历史（实际生产环境建议使用Redis）
    private Map<String, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    
    @Override
    public String sendMessage(Long userId, String message, String conversationId)
    {
        if (apiKey == null || apiKey.trim().isEmpty()) {
        {
            log.warn("Qwen API Key未配置");
            return "AI客服服务暂未配置，请联系管理员。";
        }
        
        try
        {
            // 生成会话ID
            String sessionId = conversationId != null ? conversationId : "session_" + userId + "_" + System.currentTimeMillis();
            
            // 获取或创建会话历史
            List<Map<String, String>> history = conversationHistory.getOrDefault(sessionId, new ArrayList<>());
            
            // 查询相关数据（商品、订单、店铺等）
            String dataContext = dataQueryHelper.queryRelevantData(userId, message);
            
            // 添加用户原始消息到历史（用于保存对话历史）
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            history.add(userMessage);
            
            // 构建请求
            JSONObject request = new JSONObject();
            request.put("model", model);
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统提示（增强版，说明可以访问数据库）
            String enhancedSystemPrompt = systemPrompt + 
                "\n\n重要提示：你可以访问商城数据库中的商品、订单、店铺等信息。当用户询问具体商品、订单或店铺信息时，系统会自动查询相关数据并提供给你。请基于这些真实数据回答用户的问题，确保信息的准确性和时效性。";
            
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", enhancedSystemPrompt);
            messages.add(systemMsg);
            
            // 添加历史对话（最多保留最近10轮）
            int historySize = history.size();
            int startIndex = Math.max(0, historySize - 10);
            for (int i = startIndex; i < historySize - 1; i++) // 排除当前消息
            {
                messages.add(history.get(i));
            }
            
            // 添加当前用户消息（如果查询到数据，则包含数据上下文）
            Map<String, String> currentUserMessage = new HashMap<>();
            currentUserMessage.put("role", "user");
            if (!dataContext.isEmpty())
            {
                StringBuilder enhancedMessage = new StringBuilder();
                enhancedMessage.append("以下是系统查询到的相关数据，请基于这些数据回答用户问题：\n\n");
                enhancedMessage.append(dataContext);
                enhancedMessage.append("用户问题：").append(message);
                currentUserMessage.put("content", enhancedMessage.toString());
            }
            else
            {
                currentUserMessage.put("content", message);
            }
            messages.add(currentUserMessage);
            
            request.put("input", new JSONObject().fluentPut("messages", messages));
            
            JSONObject parameters = new JSONObject();
            parameters.put("max_tokens", maxTokens);
            parameters.put("temperature", temperature);
            request.put("parameters", parameters);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("X-DashScope-SSE", "disable");
            
            HttpEntity<String> entity = new HttpEntity<>(request.toJSONString(), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK)
            {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                
                // 解析响应 - Qwen API返回格式: {"output": {"text": "回复内容", "finish_reason": "stop"}}
                if (responseJson.containsKey("output"))
                {
                    JSONObject output = responseJson.getJSONObject("output");
                    String content = null;
                    
                    // 优先尝试解析 text 字段（Qwen API标准格式）
                    if (output.containsKey("text"))
                    {
                        content = output.getString("text");
                    }
                    // 兼容 choices 格式（如果API返回的是这种格式）
                    else if (output.containsKey("choices") && output.getJSONArray("choices").size() > 0)
                    {
                        JSONObject choice = output.getJSONArray("choices").getJSONObject(0);
                        if (choice.containsKey("message"))
                        {
                            JSONObject aiMessage = choice.getJSONObject("message");
                            content = aiMessage.getString("content");
                        }
                        else if (choice.containsKey("text"))
                        {
                            content = choice.getString("text");
                        }
                    }
                    
                    if (content != null && !content.isEmpty())
                    {
                        // 添加AI回复到历史
                        Map<String, String> assistantMessage = new HashMap<>();
                        assistantMessage.put("role", "assistant");
                        assistantMessage.put("content", content);
                        history.add(assistantMessage);
                        
                        // 保存会话历史
                        conversationHistory.put(sessionId, history);
                        
                        log.debug("Qwen API响应成功，会话ID: {}, 回复长度: {}", sessionId, content.length());
                        return content;
                    }
                }
                
                // 如果响应格式不正确，返回错误信息
                log.error("Qwen API响应格式异常，无法解析回复内容: {}", response.getBody());
                return "抱歉，AI客服暂时无法响应，请稍后再试。";
            }
            else
            {
                log.error("Qwen API请求失败: {}", response.getStatusCode());
                return "抱歉，AI客服服务暂时不可用，请稍后再试。";
            }
        }
        catch (Exception e)
        {
            log.error("调用Qwen API异常", e);
            return "抱歉，发生了错误，请稍后再试。";
        }
    }
    
    @Override
    public void clearConversation(Long userId, String conversationId)
    {
        if (conversationId != null)
        {
            conversationHistory.remove(conversationId);
        }
        else
        {
            // 清除该用户的所有会话
            conversationHistory.entrySet().removeIf(entry -> entry.getKey().startsWith("session_" + userId + "_"));
        }
    }
}

