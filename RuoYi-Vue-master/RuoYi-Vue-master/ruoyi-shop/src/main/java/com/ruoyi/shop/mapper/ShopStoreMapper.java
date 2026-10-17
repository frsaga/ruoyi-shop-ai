package com.ruoyi.shop.mapper;

import java.util.List;

import com.ruoyi.shop.domain.ShopStore;

/**
 * 店铺Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopStoreMapper 
{
    /**
     * 查询店铺
     * 
     * @param storeId 店铺主键
     * @return 店铺
     */
    public ShopStore selectShopStoreByStoreId(Long storeId);

    /**
     * 根据用户ID查询店铺
     * 
     * @param userId 用户ID
     * @return 店铺
     */
    public ShopStore selectShopStoreByUserId(Long userId);

    /**
     * 查询店铺列表
     * 
     * @param shopStore 店铺
     * @return 店铺集合
     */
    public List<ShopStore> selectShopStoreList(ShopStore shopStore);

    /**
     * 新增店铺
     * 
     * @param shopStore 店铺
     * @return 结果
     */
    public int insertShopStore(ShopStore shopStore);

    /**
     * 修改店铺
     * 
     * @param shopStore 店铺
     * @return 结果
     */
    public int updateShopStore(ShopStore shopStore);

    /**
     * 删除店铺
     * 
     * @param storeId 店铺主键
     * @return 结果
     */
    public int deleteShopStoreByStoreId(Long storeId);

    /**
     * 批量删除店铺
     * 
     * @param storeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopStoreByStoreIds(Long[] storeIds);

    /**
     * 统计店铺下的商品数量
     * 
     * @param storeId 店铺ID
     * @return 商品数量
     */
    public int countProductByStoreId(Long storeId);

    /**
     * 统计店铺的平均信用评分（基于已发布评价）
     * 
     * @param storeId 店铺ID
     * @return 平均评分，如果没有评价返回null
     */
    public Double calculateAverageRatingByStoreId(Long storeId);
}

