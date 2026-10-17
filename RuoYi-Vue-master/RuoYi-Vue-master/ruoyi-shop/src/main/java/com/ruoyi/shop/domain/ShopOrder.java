package com.ruoyi.shop.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单对象 shop_order
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 店铺ID */
    @Excel(name = "店铺ID")
    private Long storeId;

    /** 收货人姓名 */
    @Excel(name = "收货人姓名")
    private String receiverName;

    /** 收货电话 */
    @Excel(name = "收货电话")
    private String receiverPhone;

    /** 省份 */
    @Excel(name = "省份")
    private String receiverProvince;

    /** 城市 */
    @Excel(name = "城市")
    private String receiverCity;

    /** 区县 */
    @Excel(name = "区县")
    private String receiverDistrict;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String receiverAddress;

    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private BigDecimal totalAmount;

    /** 实付金额 */
    @Excel(name = "实付金额")
    private BigDecimal paymentAmount;

    /** 运费 */
    @Excel(name = "运费")
    private BigDecimal freight;

    /** 支付方式（1在线支付 2货到付款） */
    @Excel(name = "支付方式", readConverterExp = "1=在线支付,2=货到付款")
    private Integer paymentType;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    /** 发货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 关闭时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "关闭时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    /** 订单状态（0待支付 1已支付 2已发货 3已完成 4已关闭 5已取消） */
    @Excel(name = "订单状态", readConverterExp = "0=待支付,1=已支付,2=已发货,3=已完成,4=已关闭,5=已取消")
    private Integer status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 订单明细列表 */
    private List<ShopOrderItem> orderItems;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String storeName;

    /** 是否所有商品已评价（用于前端显示） */
    private Boolean allReviewed;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setStoreId(Long storeId) 
    {
        this.storeId = storeId;
    }

    public Long getStoreId() 
    {
        return storeId;
    }

    public void setReceiverName(String receiverName) 
    {
        this.receiverName = receiverName;
    }

    public String getReceiverName() 
    {
        return receiverName;
    }

    public void setReceiverPhone(String receiverPhone) 
    {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPhone() 
    {
        return receiverPhone;
    }

    public void setReceiverProvince(String receiverProvince) 
    {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverProvince() 
    {
        return receiverProvince;
    }

    public void setReceiverCity(String receiverCity) 
    {
        this.receiverCity = receiverCity;
    }

    public String getReceiverCity() 
    {
        return receiverCity;
    }

    public void setReceiverDistrict(String receiverDistrict) 
    {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverDistrict() 
    {
        return receiverDistrict;
    }

    public void setReceiverAddress(String receiverAddress) 
    {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverAddress() 
    {
        return receiverAddress;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) 
    {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount() 
    {
        return paymentAmount;
    }

    public void setFreight(BigDecimal freight) 
    {
        this.freight = freight;
    }

    public BigDecimal getFreight() 
    {
        return freight;
    }

    public void setPaymentType(Integer paymentType) 
    {
        this.paymentType = paymentType;
    }

    public Integer getPaymentType() 
    {
        return paymentType;
    }

    public void setPaymentTime(Date paymentTime) 
    {
        this.paymentTime = paymentTime;
    }

    public Date getPaymentTime() 
    {
        return paymentTime;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setCloseTime(Date closeTime) 
    {
        this.closeTime = closeTime;
    }

    public Date getCloseTime() 
    {
        return closeTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
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

    public List<ShopOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<ShopOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Boolean getAllReviewed() {
        return allReviewed;
    }

    public void setAllReviewed(Boolean allReviewed) {
        this.allReviewed = allReviewed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("userId", getUserId())
            .append("storeId", getStoreId())
            .append("receiverName", getReceiverName())
            .append("receiverPhone", getReceiverPhone())
            .append("receiverProvince", getReceiverProvince())
            .append("receiverCity", getReceiverCity())
            .append("receiverDistrict", getReceiverDistrict())
            .append("receiverAddress", getReceiverAddress())
            .append("totalAmount", getTotalAmount())
            .append("paymentAmount", getPaymentAmount())
            .append("freight", getFreight())
            .append("paymentType", getPaymentType())
            .append("paymentTime", getPaymentTime())
            .append("sendTime", getSendTime())
            .append("endTime", getEndTime())
            .append("closeTime", getCloseTime())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

