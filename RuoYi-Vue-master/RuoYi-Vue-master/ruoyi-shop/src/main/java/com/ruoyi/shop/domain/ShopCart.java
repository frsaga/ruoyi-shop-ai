package com.ruoyi.shop.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 购物车对象 shop_cart
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public class ShopCart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 购物车ID */
    private Long cartId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 数量 */
    @Excel(name = "数量")
    private Integer quantity;

    /** 是否选中（0否 1是） */
    @Excel(name = "是否选中", readConverterExp = "0=否,1=是")
    private String checked;

    /** 商品信息 - 用于返回给前端 */
    private ShopProduct product;

    /** 小计金额 - 用于返回给前端 */
    private BigDecimal totalPrice;

    public void setCartId(Long cartId) 
    {
        this.cartId = cartId;
    }

    public Long getCartId() 
    {
        return cartId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    public void setChecked(String checked) 
    {
        this.checked = checked;
    }

    public String getChecked() 
    {
        return checked;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cartId", getCartId())
            .append("userId", getUserId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("checked", getChecked())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

