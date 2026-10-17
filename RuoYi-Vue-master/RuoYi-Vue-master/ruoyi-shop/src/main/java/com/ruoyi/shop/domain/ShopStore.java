package com.ruoyi.shop.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 店铺对象 shop_store
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopStore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 店铺ID */
    private Long storeId;

    /** 店主用户ID */
    @Excel(name = "店主用户ID")
    private Long userId;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String storeName;

    /** 店铺Logo */
    @Excel(name = "店铺Logo")
    private String storeLogo;

    /** 店铺描述 */
    @Excel(name = "店铺描述")
    private String storeDescription;

    /** 店铺地址 */
    @Excel(name = "店铺地址")
    private String storeAddress;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String storePhone;

    /** 营业执照 */
    @Excel(name = "营业执照")
    private String businessLicense;

    /** 状态（0待审核 1正常 2停用） */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=正常,2=停用")
    private String status;

    /** 信用评分 */
    @Excel(name = "信用评分")
    private BigDecimal creditScore;

    /** 总销量 */
    @Excel(name = "总销量")
    private Long salesVolume;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setStoreId(Long storeId) 
    {
        this.storeId = storeId;
    }

    public Long getStoreId() 
    {
        return storeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setStoreName(String storeName) 
    {
        this.storeName = storeName;
    }

    public String getStoreName() 
    {
        return storeName;
    }

    public void setStoreLogo(String storeLogo) 
    {
        this.storeLogo = storeLogo;
    }

    public String getStoreLogo() 
    {
        return storeLogo;
    }

    public void setStoreDescription(String storeDescription) 
    {
        this.storeDescription = storeDescription;
    }

    public String getStoreDescription() 
    {
        return storeDescription;
    }

    public void setStoreAddress(String storeAddress) 
    {
        this.storeAddress = storeAddress;
    }

    public String getStoreAddress() 
    {
        return storeAddress;
    }

    public void setStorePhone(String storePhone) 
    {
        this.storePhone = storePhone;
    }

    public String getStorePhone() 
    {
        return storePhone;
    }

    public void setBusinessLicense(String businessLicense) 
    {
        this.businessLicense = businessLicense;
    }

    public String getBusinessLicense() 
    {
        return businessLicense;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setCreditScore(BigDecimal creditScore) 
    {
        this.creditScore = creditScore;
    }

    public BigDecimal getCreditScore() 
    {
        return creditScore;
    }

    public void setSalesVolume(Long salesVolume) 
    {
        this.salesVolume = salesVolume;
    }

    public Long getSalesVolume() 
    {
        return salesVolume;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("storeId", getStoreId())
            .append("userId", getUserId())
            .append("storeName", getStoreName())
            .append("storeLogo", getStoreLogo())
            .append("storeDescription", getStoreDescription())
            .append("storeAddress", getStoreAddress())
            .append("storePhone", getStorePhone())
            .append("businessLicense", getBusinessLicense())
            .append("status", getStatus())
            .append("creditScore", getCreditScore())
            .append("salesVolume", getSalesVolume())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

