package com.ruoyi.shop.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品对象 shop_product
 *
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long productId;

    /** 店铺ID */
    @Excel(name = "店铺ID")
    private Long storeId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称（非数据库字段） */
    private String categoryName;

    /** 店铺名称（非数据库字段） */
    private String storeName;

    /** 发布用户ID */
    private Long userId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 商品副标题 */
    @Excel(name = "商品副标题")
    private String productSubtitle;

    /** 主图 */
    @Excel(name = "主图")
    private String mainImage;

    /** 商品图片（多张，逗号分隔） */
    @Excel(name = "商品图片")
    private String subImages;

    /** 商品详情 */
    private String detail;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 库存 */
    @Excel(name = "库存")
    private Integer stock;

    /** 销量 */
    @Excel(name = "销量")
    private Integer salesCount;

    /** 状态（0 下架 1 上架） */
    @Excel(name = "状态")
    private String status;

    /** 推荐（0 否 1 是） */
    @Excel(name = "是否推荐")
    private String isRecommended;

    /** 删除标志（0 正常 1 删除） */
    private String delFlag;


    /* ===== Getter & Setter ===== */
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSubtitle() { return productSubtitle; }
    public void setProductSubtitle(String productSubtitle) { this.productSubtitle = productSubtitle; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public String getSubImages() { return subImages; }
    public void setSubImages(String subImages) { this.subImages = subImages; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getSalesCount() { return salesCount; }
    public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIsRecommended() { return isRecommended; }
    public void setIsRecommended(String isRecommended) { this.isRecommended = isRecommended; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("productId", getProductId())
                .append("storeId", getStoreId())
                .append("categoryId", getCategoryId())
                .append("categoryName", getCategoryName())
                .append("storeName", getStoreName())
                .append("userId", getUserId())
                .append("productName", getProductName())
                .append("productSubtitle", getProductSubtitle())
                .append("mainImage", getMainImage())
                .append("subImages", getSubImages())
                .append("detail", getDetail())
                .append("price", getPrice())
                .append("stock", getStock())
                .append("salesCount", getSalesCount())
                .append("status", getStatus())
                .append("isRecommended", getIsRecommended())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
