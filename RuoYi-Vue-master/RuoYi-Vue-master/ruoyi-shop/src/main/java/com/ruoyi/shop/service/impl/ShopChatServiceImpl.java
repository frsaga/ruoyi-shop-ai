package com.ruoyi.shop.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.mapper.ShopChatMapper;
import com.ruoyi.shop.domain.ShopChat;
import com.ruoyi.shop.service.IShopChatService;

/**
 * 客服聊天Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopChatServiceImpl implements IShopChatService 
{
    @Autowired
    private ShopChatMapper shopChatMapper;

    /**
     * 查询客服聊天
     * 
     * @param chatId 客服聊天主键
     * @return 客服聊天
     */
    @Override
    public ShopChat selectShopChatByChatId(Long chatId)
    {
        return shopChatMapper.selectShopChatByChatId(chatId);
    }

    /**
     * 查询客服聊天列表
     * 
     * @param shopChat 客服聊天
     * @return 客服聊天集合
     */
    @Override
    public List<ShopChat> selectShopChatList(ShopChat shopChat)
    {
        return shopChatMapper.selectShopChatList(shopChat);
    }

    /**
     * 查询聊天历史记录
     * 
     * @param buyerId 买家ID
     * @param sellerId 卖家ID
     * @param storeId 店铺ID（可选，用于区分同一卖家的不同店铺）
     * @return 聊天记录集合
     */
    @Override
    public List<ShopChat> selectChatHistory(Long buyerId, Long sellerId, Long storeId)
    {
        return shopChatMapper.selectChatHistory(buyerId, sellerId, storeId);
    }

    /**
     * 查询用户的聊天会话列表（买家视角）
     * 
     * @param buyerId 买家ID
     * @return 聊天会话列表
     */
    @Override
    public List<ShopChat> selectBuyerChatSessions(Long buyerId)
    {
        return shopChatMapper.selectBuyerChatSessions(buyerId);
    }

    /**
     * 查询用户的聊天会话列表（卖家视角）
     * 
     * @param sellerId 卖家ID
     * @return 聊天会话列表
     */
    @Override
    public List<ShopChat> selectSellerChatSessions(Long sellerId)
    {
        return shopChatMapper.selectSellerChatSessions(sellerId);
    }

    /**
     * 查询未读消息数量
     * 
     * @param userId 用户ID
     * @param userType 用户类型（0买家 1卖家）
     * @return 未读消息数量
     */
    @Override
    public int selectUnreadCount(Long userId, String userType)
    {
        return shopChatMapper.selectUnreadCount(userId, userType);
    }

    /**
     * 发送消息
     * 
     * @param shopChat 客服聊天
     * @return 结果
     */
    @Override
    public int sendMessage(ShopChat shopChat)
    {
        shopChat.setCreateTime(new Date());
        shopChat.setUpdateTime(new Date());
        shopChat.setIsRead("0"); // 默认未读
        if (shopChat.getMessageType() == null) {
            shopChat.setMessageType("0"); // 默认文本消息
        }
        return shopChatMapper.insertShopChat(shopChat);
    }

    /**
     * 修改客服聊天
     * 
     * @param shopChat 客服聊天
     * @return 结果
     */
    @Override
    public int updateShopChat(ShopChat shopChat)
    {
        shopChat.setUpdateTime(new Date());
        return shopChatMapper.updateShopChat(shopChat);
    }

    /**
     * 批量删除客服聊天
     * 
     * @param chatIds 需要删除的客服聊天主键集合
     * @return 结果
     */
    @Override
    public int deleteShopChatByChatIds(Long[] chatIds)
    {
        return shopChatMapper.deleteShopChatByChatIds(chatIds);
    }

    /**
     * 删除客服聊天信息
     * 
     * @param chatId 客服聊天主键
     * @return 结果
     */
    @Override
    public int deleteShopChatByChatId(Long chatId)
    {
        return shopChatMapper.deleteShopChatByChatId(chatId);
    }

    /**
     * 标记消息为已读
     * 
     * @param buyerId 买家ID
     * @param sellerId 卖家ID
     * @param userType 用户类型（0买家 1卖家）
     * @param storeId 店铺ID（可选，用于区分同一卖家的不同店铺）
     * @return 结果
     */
    @Override
    public int markAsRead(Long buyerId, Long sellerId, String userType, Long storeId)
    {
        return shopChatMapper.markAsRead(buyerId, sellerId, userType, storeId);
    }
}

