package com.ruoyi.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.shop.domain.ShopCart;

/**
 * 购物车Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopCartMapper 
{
    /**
     * 查询购物车
     * 
     * @param cartId 购物车主键
     * @return 购物车
     */
    public ShopCart selectShopCartByCartId(Long cartId);

    /**
     * 查询用户购物车列表
     * 
     * @param userId 用户ID
     * @return 购物车集合
     */
    public List<ShopCart> selectShopCartListByUserId(Long userId);

    /**
     * 查询购物车列表
     * 
     * @param shopCart 购物车
     * @return 购物车集合
     */
    public List<ShopCart> selectShopCartList(ShopCart shopCart);

    /**
     * 新增购物车
     * 
     * @param shopCart 购物车
     * @return 结果
     */
    public int insertShopCart(ShopCart shopCart);

    /**
     * 修改购物车
     * 
     * @param shopCart 购物车
     * @return 结果
     */
    public int updateShopCart(ShopCart shopCart);

    /**
     * 删除购物车
     * 
     * @param cartId 购物车主键
     * @return 结果
     */
    public int deleteShopCartByCartId(Long cartId);

    /**
     * 批量删除购物车
     * 
     * @param cartIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopCartByCartIds(Long[] cartIds);

    /**
     * 根据用户ID和商品ID查询购物车
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车
     */
    public ShopCart selectShopCartByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteShopCartByUserId(Long userId);
}

