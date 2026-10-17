package com.ruoyi.shop.service;

import java.util.List;

import com.ruoyi.shop.domain.ShopStore;

/**
 * 店铺Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopStoreService 
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
     * 批量删除店铺
     * 
     * @param storeIds 需要删除的店铺主键集合
     * @return 结果
     */
    public int deleteShopStoreByStoreIds(Long[] storeIds);

    /**
     * 删除店铺信息
     * 
     * @param storeId 店铺主键
     * @return 结果
     */
    public int deleteShopStoreByStoreId(Long storeId);

    /**
     * 店铺审核
     * 
     * @param storeId 店铺ID
     * @param status 审核状态 (1通过 2拒绝)
     * @return 结果
     */
    public int auditStore(Long storeId, String status);

    /**
     * 检查店铺是否可以删除（是否有商品）
     * 
     * @param storeId 店铺ID
     * @return 错误消息，如果为null表示可以删除
     */
    public String checkStoreCanDelete(Long storeId);

    /**
     * 计算店铺的平均信用评分（基于已发布评价）
     * 
     * @param storeId 店铺ID
     * @return 平均评分，如果没有评价返回null
     */
    public Double calculateAverageRatingByStoreId(Long storeId);
}

