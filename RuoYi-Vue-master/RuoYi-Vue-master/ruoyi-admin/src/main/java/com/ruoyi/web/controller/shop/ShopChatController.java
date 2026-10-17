package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.domain.ShopChat;
import com.ruoyi.shop.service.IShopChatService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 客服聊天Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/chat")
public class ShopChatController extends BaseController
{
    @Autowired
    private IShopChatService shopChatService;

    /**
     * 查询客服聊天列表
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopChat shopChat)
    {
        startPage();
        List<ShopChat> list = shopChatService.selectShopChatList(shopChat);
        return getDataTable(list);
    }

    /**
     * 查询聊天会话列表（买家视角）
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:query')")
    @GetMapping("/sessions/buyer")
    public AjaxResult getBuyerSessions()
    {
        Long buyerId = SecurityUtils.getUserId();
        List<ShopChat> sessions = shopChatService.selectBuyerChatSessions(buyerId);
        return success(sessions);
    }

    /**
     * 公开的查询聊天会话列表（买家视角）- 商城使用，只需登录，无需权限
     */
    @GetMapping("/public/sessions/buyer")
    public AjaxResult getBuyerSessionsPublic()
    {
        Long buyerId = SecurityUtils.getUserId();
        if (buyerId == null) {
            return error("用户未登录");
        }
        List<ShopChat> sessions = shopChatService.selectBuyerChatSessions(buyerId);
        return success(sessions);
    }

    /**
     * 查询聊天会话列表（卖家视角）
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:query')")
    @GetMapping("/sessions/seller")
    public AjaxResult getSellerSessions()
    {
        Long sellerId = SecurityUtils.getUserId();
        List<ShopChat> sessions = shopChatService.selectSellerChatSessions(sellerId);
        return success(sessions);
    }

    /**
     * 公开的查询聊天会话列表（卖家视角）- 商城使用，只需登录，无需权限
     */
    @GetMapping("/public/sessions/seller")
    public AjaxResult getSellerSessionsPublic()
    {
        Long sellerId = SecurityUtils.getUserId();
        if (sellerId == null) {
            return error("用户未登录");
        }
        List<ShopChat> sessions = shopChatService.selectSellerChatSessions(sellerId);
        return success(sessions);
    }

    /**
     * 查询聊天历史记录
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:query')")
    @GetMapping("/history")
    public AjaxResult getChatHistory(@RequestParam Long buyerId, @RequestParam Long sellerId, @RequestParam(required = false) Long storeId)
    {
        // 权限验证：确保当前用户是买家或卖家之一
        Long currentUserId = SecurityUtils.getUserId();
        if (!currentUserId.equals(buyerId) && !currentUserId.equals(sellerId)) {
            return error("无权查看该聊天记录");
        }
        
        List<ShopChat> history = shopChatService.selectChatHistory(buyerId, sellerId, storeId);
        return success(history);
    }

    /**
     * 公开的查询聊天历史记录接口 - 商城使用，只需登录，无需权限
     */
    @GetMapping("/public/history")
    public AjaxResult getChatHistoryPublic(@RequestParam Long buyerId, @RequestParam Long sellerId, @RequestParam(required = false) Long storeId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return error("用户未登录");
        }
        
        // 权限验证：确保当前用户是买家或卖家之一
        if (!currentUserId.equals(buyerId) && !currentUserId.equals(sellerId)) {
            return error("无权查看该聊天记录");
        }
        
        List<ShopChat> history = shopChatService.selectChatHistory(buyerId, sellerId, storeId);
        return success(history);
    }

    /**
     * 查询未读消息数量
     */
    @GetMapping("/unread/count")
    public AjaxResult getUnreadCount(@RequestParam String userType)
    {
        Long userId = SecurityUtils.getUserId();
        int count = shopChatService.selectUnreadCount(userId, userType);
        return success(count);
    }

    /**
     * 发送消息
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:send')")
    @Log(title = "客服聊天", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult sendMessage(@RequestBody ShopChat shopChat)
    {
        Long currentUserId = SecurityUtils.getUserId();
        
        // 根据发送者类型设置发送者ID和对应的买家/卖家ID
        if ("0".equals(shopChat.getSenderType())) {
            // 买家发送
            shopChat.setBuyerId(currentUserId);
            shopChat.setSenderId(currentUserId);
            // 确保sellerId已设置（如果前端没有传递，需要从已有消息中获取或报错）
            if (shopChat.getSellerId() == null) {
                return error("卖家ID不能为空");
            }
        } else if ("1".equals(shopChat.getSenderType())) {
            // 卖家发送
            shopChat.setSellerId(currentUserId);
            shopChat.setSenderId(currentUserId);
            // 确保buyerId已设置（如果前端没有传递，需要从已有消息中获取或报错）
            if (shopChat.getBuyerId() == null) {
                return error("买家ID不能为空");
            }
        } else {
            return error("发送者类型错误");
        }
        
        // 验证：确保当前用户是聊天中的买家或卖家
        if (shopChat.getBuyerId() != null && !shopChat.getBuyerId().equals(currentUserId) 
            && shopChat.getSellerId() != null && !shopChat.getSellerId().equals(currentUserId)) {
            return error("无权发送消息");
        }
        
        return toAjax(shopChatService.sendMessage(shopChat));
    }

    /**
     * 公开的发送消息接口 - 商城使用，只需登录，无需权限
     */
    @Log(title = "客服聊天", businessType = BusinessType.INSERT)
    @PostMapping("/public")
    public AjaxResult sendMessagePublic(@RequestBody ShopChat shopChat)
    {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return error("用户未登录");
        }
        
        // 根据发送者类型设置发送者ID和对应的买家/卖家ID
        if ("0".equals(shopChat.getSenderType())) {
            // 买家发送
            shopChat.setBuyerId(currentUserId);
            shopChat.setSenderId(currentUserId);
            // 确保sellerId已设置（如果前端没有传递，需要从已有消息中获取或报错）
            if (shopChat.getSellerId() == null) {
                return error("卖家ID不能为空");
            }
        } else if ("1".equals(shopChat.getSenderType())) {
            // 卖家发送
            shopChat.setSellerId(currentUserId);
            shopChat.setSenderId(currentUserId);
            // 确保buyerId已设置（如果前端没有传递，需要从已有消息中获取或报错）
            if (shopChat.getBuyerId() == null) {
                return error("买家ID不能为空");
            }
        } else {
            return error("发送者类型错误");
        }
        
        // 验证：确保当前用户是聊天中的买家或卖家
        if (shopChat.getBuyerId() != null && !shopChat.getBuyerId().equals(currentUserId) 
            && shopChat.getSellerId() != null && !shopChat.getSellerId().equals(currentUserId)) {
            return error("无权发送消息");
        }
        
        return toAjax(shopChatService.sendMessage(shopChat));
    }

    /**
     * 标记消息为已读
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:read')")
    @Log(title = "客服聊天", businessType = BusinessType.UPDATE)
    @PutMapping("/read")
    public AjaxResult markAsRead(@RequestParam Long buyerId, @RequestParam Long sellerId, @RequestParam String userType, @RequestParam(required = false) Long storeId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        
        // 验证：确保当前用户是聊天中的买家或卖家
        if (!buyerId.equals(currentUserId) && !sellerId.equals(currentUserId)) {
            return error("无权操作");
        }
        
        // 标记为已读，即使更新0行也返回成功（可能是所有消息都已经是已读状态）
        shopChatService.markAsRead(buyerId, sellerId, userType, storeId);
        return success("操作成功");
    }

    /**
     * 公开的标记消息为已读接口 - 商城使用，只需登录，无需权限
     */
    @Log(title = "客服聊天", businessType = BusinessType.UPDATE)
    @PutMapping("/public/read")
    public AjaxResult markAsReadPublic(@RequestParam Long buyerId, @RequestParam Long sellerId, @RequestParam String userType, @RequestParam(required = false) Long storeId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return error("用户未登录");
        }
        
        // 验证：确保当前用户是聊天中的买家或卖家
        if (!buyerId.equals(currentUserId) && !sellerId.equals(currentUserId)) {
            return error("无权操作");
        }
        
        // 标记为已读，即使更新0行也返回成功（可能是所有消息都已经是已读状态）
        shopChatService.markAsRead(buyerId, sellerId, userType, storeId);
        return success("操作成功");
    }

    /**
     * 删除客服聊天
     */
    @PreAuthorize("@ss.hasPermi('shop:chat:remove')")
    @Log(title = "客服聊天", businessType = BusinessType.DELETE)
	@DeleteMapping("/{chatIds}")
    public AjaxResult remove(@PathVariable Long[] chatIds)
    {
        return toAjax(shopChatService.deleteShopChatByChatIds(chatIds));
    }
}

