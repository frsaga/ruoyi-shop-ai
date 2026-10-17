package com.ruoyi.shop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.mapper.ShopStoreMapper;
import com.ruoyi.shop.service.IShopStoreService;

/**
 * 店铺Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopStoreServiceImpl implements IShopStoreService 
{
    @Autowired
    private ShopStoreMapper shopStoreMapper;

    /**
     * 查询店铺
     * 
     * @param storeId 店铺主键
     * @return 店铺
     */
    @Override
    public ShopStore selectShopStoreByStoreId(Long storeId)
    {
        return shopStoreMapper.selectShopStoreByStoreId(storeId);
    }

    /**
     * 根据用户ID查询店铺
     * 
     * @param userId 用户ID
     * @return 店铺
     */
    @Override
    public ShopStore selectShopStoreByUserId(Long userId)
    {
        return shopStoreMapper.selectShopStoreByUserId(userId);
    }

    /**
     * 查询店铺列表
     * 
     * @param shopStore 店铺
     * @return 店铺
     */
    @Override
    public List<ShopStore> selectShopStoreList(ShopStore shopStore)
    {
        return shopStoreMapper.selectShopStoreList(shopStore);
    }

    /**
     * 新增店铺
     * 
     * @param shopStore 店铺
     * @return 结果
     */
    @Override
    public int insertShopStore(ShopStore shopStore)
    {
        // 一个卖家可以创建多个店铺，不再限制
        // 但需要检查店铺名称是否重复
        ShopStore query = new ShopStore();
        query.setStoreName(shopStore.getStoreName());
        query.setUserId(shopStore.getUserId());
        List<ShopStore> existingStores = shopStoreMapper.selectShopStoreList(query);
        if (existingStores != null && !existingStores.isEmpty())
        {
            // 检查是否有同名的未删除店铺
            for (ShopStore store : existingStores) {
                if (store.getDelFlag() != null && store.getDelFlag().equals("0")) {
                    throw new ServiceException("您已有同名店铺，请使用不同的店铺名称");
                }
            }
        }
        
        shopStore.setCreateTime(DateUtils.getNowDate());
        shopStore.setStatus("0"); // 默认待审核状态
        return shopStoreMapper.insertShopStore(shopStore);
    }

    /**
     * 修改店铺
     * 
     * @param shopStore 店铺
     * @return 结果
     */
    @Override
    public int updateShopStore(ShopStore shopStore)
    {
        shopStore.setUpdateTime(DateUtils.getNowDate());
        return shopStoreMapper.updateShopStore(shopStore);
    }

    /**
     * 批量删除店铺
     * 
     * @param storeIds 需要删除的店铺主键
     * @return 结果
     */
    @Override
    public int deleteShopStoreByStoreIds(Long[] storeIds)
    {
        return shopStoreMapper.deleteShopStoreByStoreIds(storeIds);
    }

    /**
     * 删除店铺信息
     * 
     * @param storeId 店铺主键
     * @return 结果
     */
    @Override
    public int deleteShopStoreByStoreId(Long storeId)
    {
        return shopStoreMapper.deleteShopStoreByStoreId(storeId);
    }

    /**
     * 店铺审核
     * 
     * @param storeId 店铺ID
     * @param status 审核状态 (1通过 2拒绝)
     * @return 结果
     */
    @Override
    public int auditStore(Long storeId, String status)
    {
        ShopStore store = new ShopStore();
        store.setStoreId(storeId);
        store.setStatus(status);
        store.setUpdateTime(DateUtils.getNowDate());
        return shopStoreMapper.updateShopStore(store);
    }

    /**
     * 检查店铺是否可以删除（是否有商品）
     * 
     * @param storeId 店铺ID
     * @return 错误消息，如果为null表示可以删除
     */
    @Override
    public String checkStoreCanDelete(Long storeId)
    {
        // 检查是否有商品
        int productCount = shopStoreMapper.countProductByStoreId(storeId);
        if (productCount > 0)
        {
            return "该店铺下存在商品，无法删除";
        }
        
        return null; // 可以删除
    }

    /**
     * 计算店铺的平均信用评分（基于已发布评价）
     * 
     * @param storeId 店铺ID
     * @return 平均评分，如果没有评价返回null
     */
    @Override
    public Double calculateAverageRatingByStoreId(Long storeId)
    {
        return shopStoreMapper.calculateAverageRatingByStoreId(storeId);
    }
}

