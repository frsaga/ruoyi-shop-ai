package com.ruoyi.shop.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客服聊天对象 shop_chat
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopChat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 聊天ID */
    private Long chatId;

    /** 买家ID */
    @Excel(name = "买家ID")
    private Long buyerId;

    /** 卖家ID */
    @Excel(name = "卖家ID")
    private Long sellerId;

    /** 店铺ID */
    @Excel(name = "店铺ID")
    private Long storeId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 消息类型（0文本 1图片 2系统消息） */
    @Excel(name = "消息类型", readConverterExp = "0=文本,1=图片,2=系统消息")
    private String messageType;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String content;

    /** 发送者类型（0买家 1卖家） */
    @Excel(name = "发送者类型", readConverterExp = "0=买家,1=卖家")
    private String senderType;

    /** 发送者ID */
    @Excel(name = "发送者ID")
    private Long senderId;

    /** 是否已读（0未读 1已读） */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private String isRead;

    /** 买家信息 - 用于返回给前端 */
    private String buyerName;

    /** 卖家信息 - 用于返回给前端 */
    private String sellerName;

    /** 店铺信息 - 用于返回给前端 */
    private String storeName;

    public void setChatId(Long chatId) 
    {
        this.chatId = chatId;
    }

    public Long getChatId() 
    {
        return chatId;
    }

    public void setBuyerId(Long buyerId) 
    {
        this.buyerId = buyerId;
    }

    public Long getBuyerId() 
    {
        return buyerId;
    }

    public void setSellerId(Long sellerId) 
    {
        this.sellerId = sellerId;
    }

    public Long getSellerId() 
    {
        return sellerId;
    }

    public void setStoreId(Long storeId) 
    {
        this.storeId = storeId;
    }

    public Long getStoreId() 
    {
        return storeId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setMessageType(String messageType) 
    {
        this.messageType = messageType;
    }

    public String getMessageType() 
    {
        return messageType;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setSenderType(String senderType) 
    {
        this.senderType = senderType;
    }

    public String getSenderType() 
    {
        return senderType;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setIsRead(String isRead) 
    {
        this.isRead = isRead;
    }

    public String getIsRead() 
    {
        return isRead;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("chatId", getChatId())
            .append("buyerId", getBuyerId())
            .append("sellerId", getSellerId())
            .append("storeId", getStoreId())
            .append("orderId", getOrderId())
            .append("messageType", getMessageType())
            .append("content", getContent())
            .append("senderType", getSenderType())
            .append("senderId", getSenderId())
            .append("isRead", getIsRead())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

