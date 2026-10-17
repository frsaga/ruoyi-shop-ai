package com.ruoyi.shop.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品评价对象 shop_review
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long reviewId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 评分（1-5星） */
    @Excel(name = "评分", readConverterExp = "1=1星,2=2星,3=3星,4=4星,5=5星")
    private Integer rating;

    /** 评价内容 */
    @Excel(name = "评价内容")
    private String content;

    /** 评价图片 */
    private String images;

    /** 状态（0待审核 1已发布 2已屏蔽） */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=已发布,2=已屏蔽")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 商品信息 - 用于返回给前端 */
    private ShopProduct product;

    /** 订单信息 - 用于返回给前端 */
    private ShopOrder order;

    /** 用户名称 - 用于返回给前端 */
    private String userName;

    /** 商品名称 - 用于返回给前端 */
    private String productName;

    /** 店铺ID - 用于数据范围控制 */
    private Long storeId;

    public void setReviewId(Long reviewId) 
    {
        this.reviewId = reviewId;
    }

    public Long getReviewId() 
    {
        return reviewId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRating(Integer rating) 
    {
        this.rating = rating;
    }

    public Integer getRating() 
    {
        return rating;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setImages(String images) 
    {
        this.images = images;
    }

    public String getImages() 
    {
        return images;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
    }

    public ShopOrder getOrder() {
        return order;
    }

    public void setOrder(ShopOrder order) {
        this.order = order;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("productId", getProductId())
            .append("orderId", getOrderId())
            .append("userId", getUserId())
            .append("rating", getRating())
            .append("content", getContent())
            .append("images", getImages())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

