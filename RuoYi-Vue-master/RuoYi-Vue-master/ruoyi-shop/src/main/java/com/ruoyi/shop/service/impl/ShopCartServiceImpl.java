package com.ruoyi.shop.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.mapper.ShopCartMapper;
import com.ruoyi.shop.domain.ShopCart;
import com.ruoyi.shop.service.IShopCartService;

/**
 * 购物车Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopCartServiceImpl implements IShopCartService 
{
    @Autowired
    private ShopCartMapper shopCartMapper;

    /**
     * 查询用户购物车列表
     * 
     * @param userId 用户ID
     * @return 购物车集合
     */
    @Override
    public List<ShopCart> selectShopCartListByUserId(Long userId)
    {
        List<ShopCart> cartList = shopCartMapper.selectShopCartListByUserId(userId);
        // 计算每个商品的小计
        for (ShopCart cart : cartList) {
            if (cart.getProduct() != null && cart.getProduct().getPrice() != null) {
                BigDecimal totalPrice = cart.getProduct().getPrice().multiply(new BigDecimal(cart.getQuantity()));
                cart.setTotalPrice(totalPrice);
            }
        }
        return cartList;
    }

    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 结果
     */
    @Override
    public int addToCart(Long userId, Long productId, Integer quantity)
    {
        // 检查购物车中是否已存在该商品
        ShopCart existCart = shopCartMapper.selectShopCartByUserIdAndProductId(userId, productId);
        if (existCart != null) {
            // 已存在，更新数量
            existCart.setQuantity(existCart.getQuantity() + quantity);
            existCart.setUpdateTime(new Date());
            return shopCartMapper.updateShopCart(existCart);
        } else {
            // 不存在，新增
            ShopCart cart = new ShopCart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked("1");
            cart.setCreateTime(new Date());
            cart.setUpdateTime(new Date());
            return shopCartMapper.insertShopCart(cart);
        }
    }

    /**
     * 更新购物车商品数量
     * 
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 结果
     */
    @Override
    public int updateCartQuantity(Long cartId, Integer quantity)
    {
        ShopCart cart = new ShopCart();
        cart.setCartId(cartId);
        cart.setQuantity(quantity);
        cart.setUpdateTime(new Date());
        return shopCartMapper.updateShopCart(cart);
    }

    /**
     * 删除购物车商品
     * 
     * @param cartIds 购物车ID数组
     * @return 结果
     */
    @Override
    public int deleteShopCartByCartIds(Long[] cartIds)
    {
        return shopCartMapper.deleteShopCartByCartIds(cartIds);
    }

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int clearCart(Long userId)
    {
        return shopCartMapper.deleteShopCartByUserId(userId);
    }
}

