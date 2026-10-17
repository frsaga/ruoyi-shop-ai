package com.ruoyi.shop.service;

import java.util.List;
import com.ruoyi.shop.domain.ShopCart;

/**
 * 购物车Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopCartService 
{
    /**
     * 查询用户购物车列表
     * 
     * @param userId 用户ID
     * @return 购物车集合
     */
    public List<ShopCart> selectShopCartListByUserId(Long userId);

    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 结果
     */
    public int addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     * 
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 结果
     */
    public int updateCartQuantity(Long cartId, Integer quantity);

    /**
     * 删除购物车商品
     * 
     * @param cartIds 购物车ID数组
     * @return 结果
     */
    public int deleteShopCartByCartIds(Long[] cartIds);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int clearCart(Long userId);
}

