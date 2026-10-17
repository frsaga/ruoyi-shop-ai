package com.ruoyi.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.shop.domain.ShopFavorite;

/**
 * 商品收藏Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopFavoriteMapper 
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
     * 根据用户ID和商品ID查询收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 商品收藏
     */
    public ShopFavorite selectShopFavoriteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 新增商品收藏
     * 
     * @param shopFavorite 商品收藏
     * @return 结果
     */
    public int insertShopFavorite(ShopFavorite shopFavorite);

    /**
     * 删除商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    public int deleteShopFavoriteByFavoriteId(Long favoriteId);

    /**
     * 批量删除商品收藏
     * 
     * @param favoriteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopFavoriteByFavoriteIds(Long[] favoriteIds);

    /**
     * 根据用户ID和商品ID删除收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 结果
     */
    public int deleteShopFavoriteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}

