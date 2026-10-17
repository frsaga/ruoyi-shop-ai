package com.ruoyi.shop.service;

import java.util.List;
import com.ruoyi.shop.domain.ShopFavorite;

/**
 * 商品收藏Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopFavoriteService 
{
    /**
     * 查询商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 商品收藏
     */
    public ShopFavorite selectShopFavoriteByFavoriteId(Long favoriteId);

    /**
     * 查询用户收藏列表
     * 
     * @param userId 用户ID
     * @return 商品收藏集合
     */
    public List<ShopFavorite> selectShopFavoriteListByUserId(Long userId);

    /**
     * 查询商品收藏列表
     * 
     * @param shopFavorite 商品收藏
     * @return 商品收藏集合
     */
    public List<ShopFavorite> selectShopFavoriteList(ShopFavorite shopFavorite);

    /**
     * 检查用户是否已收藏商品
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    public boolean isFavorite(Long userId, Long productId);

    /**
     * 新增商品收藏
     * 
     * @param shopFavorite 商品收藏
     * @return 结果
     */
    public int insertShopFavorite(ShopFavorite shopFavorite);

    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 结果
     */
    public int cancelFavorite(Long userId, Long productId);

    /**
     * 删除商品收藏信息
     * 
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    public int deleteShopFavoriteByFavoriteId(Long favoriteId);

    /**
     * 批量删除商品收藏
     * 
     * @param favoriteIds 需要删除的商品收藏主键集合
     * @return 结果
     */
    public int deleteShopFavoriteByFavoriteIds(Long[] favoriteIds);
}

