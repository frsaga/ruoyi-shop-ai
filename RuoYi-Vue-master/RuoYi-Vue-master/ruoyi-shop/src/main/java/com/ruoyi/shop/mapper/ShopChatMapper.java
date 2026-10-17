package com.ruoyi.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.shop.domain.ShopChat;

/**
 * 客服聊天Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopChatMapper 
{
    /**
     * 查询客服聊天
     * 
     * @param chatId 客服聊天主键
     * @return 客服聊天
     */
    public ShopChat selectShopChatByChatId(Long chatId);

    /**
     * 查询客服聊天列表
     * 
     * @param shopChat 客服聊天
     * @return 客服聊天集合
     */
    public List<ShopChat> selectShopChatList(ShopChat shopChat);

    /**
     * 查询买家与卖家的聊天记录
     * 
     * @param buyerId 买家ID
     * @param sellerId 卖家ID
     * @param storeId 店铺ID（可选，用于区分同一卖家的不同店铺）
     * @return 聊天记录集合
     */
    public List<ShopChat> selectChatHistory(@Param("buyerId") Long buyerId, @Param("sellerId") Long sellerId, @Param("storeId") Long storeId);

    /**
     * 查询用户的聊天会话列表（买家视角）
     * 
     * @param buyerId 买家ID
     * @return 聊天会话列表
     */
    public List<ShopChat> selectBuyerChatSessions(Long buyerId);

    /**
     * 查询用户的聊天会话列表（卖家视角）
     * 
     * @param sellerId 卖家ID
     * @return 聊天会话列表
     */
    public List<ShopChat> selectSellerChatSessions(Long sellerId);

    /**
     * 查询未读消息数量
     * 
     * @param userId 用户ID
     * @param userType 用户类型（0买家 1卖家）
     * @return 未读消息数量
     */
    public int selectUnreadCount(@Param("userId") Long userId, @Param("userType") String userType);

    /**
     * 新增客服聊天
     * 
     * @param shopChat 客服聊天
     * @return 结果
     */
    public int insertShopChat(ShopChat shopChat);

    /**
     * 修改客服聊天
     * 
     * @param shopChat 客服聊天
     * @return 结果
     */
    public int updateShopChat(ShopChat shopChat);

    /**
     * 删除客服聊天
     * 
     * @param chatId 客服聊天主键
     * @return 结果
     */
    public int deleteShopChatByChatId(Long chatId);

    /**
     * 批量删除客服聊天
     * 
     * @param chatIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopChatByChatIds(Long[] chatIds);

    /**
     * 标记消息为已读
     * 
     * @param buyerId 买家ID
     * @param sellerId 卖家ID
     * @param userType 用户类型（0买家 1卖家）
     * @param storeId 店铺ID（可选，用于区分同一卖家的不同店铺）
     * @return 结果
     */
    public int markAsRead(@Param("buyerId") Long buyerId, @Param("sellerId") Long sellerId, @Param("userType") String userType, @Param("storeId") Long storeId);
}

