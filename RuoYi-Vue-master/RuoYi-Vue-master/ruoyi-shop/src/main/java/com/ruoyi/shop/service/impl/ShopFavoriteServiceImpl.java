package com.ruoyi.shop.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.mapper.ShopFavoriteMapper;
import com.ruoyi.shop.domain.ShopFavorite;
import com.ruoyi.shop.service.IShopFavoriteService;

/**
 * 商品收藏Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopFavoriteServiceImpl implements IShopFavoriteService 
{
    @Autowired
    private ShopFavoriteMapper shopFavoriteMapper;

    /**
     * 查询商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 商品收藏
     */
    @Override
    public ShopFavorite selectShopFavoriteByFavoriteId(Long favoriteId)
    {
        return shopFavoriteMapper.selectShopFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 查询用户收藏列表
     * 
     * @param userId 用户ID
     * @return 商品收藏集合
     */
    @Override
    public List<ShopFavorite> selectShopFavoriteListByUserId(Long userId)
    {
        return shopFavoriteMapper.selectShopFavoriteListByUserId(userId);
    }

    /**
     * 查询商品收藏列表
     * 
     * @param shopFavorite 商品收藏
     * @return 商品收藏集合
     */
    @Override
    public List<ShopFavorite> selectShopFavoriteList(ShopFavorite shopFavorite)
    {
        return shopFavoriteMapper.selectShopFavoriteList(shopFavorite);
    }

    /**
     * 检查用户是否已收藏商品
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @Override
    public boolean isFavorite(Long userId, Long productId)
    {
        ShopFavorite favorite = shopFavoriteMapper.selectShopFavoriteByUserIdAndProductId(userId, productId);
        return favorite != null;
    }

    /**
     * 新增商品收藏
     * 
     * @param shopFavorite 商品收藏
     * @return 结果
     */
    @Override
    public int insertShopFavorite(ShopFavorite shopFavorite)
    {
        // 检查是否已收藏
        ShopFavorite existing = shopFavoriteMapper.selectShopFavoriteByUserIdAndProductId(
            shopFavorite.getUserId(), shopFavorite.getProductId());
        if (existing != null) {
            // 已收藏，返回0表示无需重复收藏
            return 0;
        }
        
        shopFavorite.setCreateTime(new Date());
        return shopFavoriteMapper.insertShopFavorite(shopFavorite);
    }

    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 结果
     */
    @Override
    public int cancelFavorite(Long userId, Long productId)
    {
        return shopFavoriteMapper.deleteShopFavoriteByUserIdAndProductId(userId, productId);
    }

    /**
     * 删除商品收藏信息
     * 
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    @Override
    public int deleteShopFavoriteByFavoriteId(Long favoriteId)
    {
        return shopFavoriteMapper.deleteShopFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 批量删除商品收藏
     * 
     * @param favoriteIds 需要删除的商品收藏主键集合
     * @return 结果
     */
    @Override
    public int deleteShopFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        return shopFavoriteMapper.deleteShopFavoriteByFavoriteIds(favoriteIds);
    }
}

