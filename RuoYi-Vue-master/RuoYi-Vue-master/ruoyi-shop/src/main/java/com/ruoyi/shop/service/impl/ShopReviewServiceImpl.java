package com.ruoyi.shop.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.domain.ShopReview;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.mapper.ShopReviewMapper;
import com.ruoyi.shop.service.IShopProductService;
import com.ruoyi.shop.service.IShopReviewService;
import com.ruoyi.shop.service.IShopStoreService;

/**
 * 商品评价Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopReviewServiceImpl implements IShopReviewService 
{
    @Autowired
    private ShopReviewMapper shopReviewMapper;

    @Autowired
    private IShopProductService shopProductService;

    @Autowired
    private IShopStoreService shopStoreService;

    /**
     * 查询商品评价
     * 
     * @param reviewId 商品评价主键
     * @return 商品评价
     */
    @Override
    public ShopReview selectShopReviewByReviewId(Long reviewId)
    {
        return shopReviewMapper.selectShopReviewByReviewId(reviewId);
    }

    /**
     * 查询商品评价列表
     * 
     * @param shopReview 商品评价
     * @return 商品评价
     */
    @Override
    public List<ShopReview> selectShopReviewList(ShopReview shopReview)
    {
        return shopReviewMapper.selectShopReviewList(shopReview);
    }

    /**
     * 根据商品ID查询评价列表（已发布状态）
     * 
     * @param productId 商品ID
     * @return 商品评价集合
     */
    @Override
    public List<ShopReview> selectShopReviewListByProductId(Long productId)
    {
        return shopReviewMapper.selectShopReviewListByProductId(productId);
    }

    /**
     * 根据店铺ID查询评价列表（卖家查看自己店铺商品的评价）
     * 
     * @param storeId 店铺ID
     * @return 商品评价集合
     */
    @Override
    public List<ShopReview> selectShopReviewListByStoreId(Long storeId)
    {
        return shopReviewMapper.selectShopReviewListByStoreId(storeId);
    }

    /**
     * 新增商品评价
     * 
     * @param shopReview 商品评价
     * @return 结果
     */
    @Override
    public int insertShopReview(ShopReview shopReview)
    {
        shopReview.setCreateTime(DateUtils.getNowDate());
        shopReview.setStatus("0"); // 默认待审核
        shopReview.setDelFlag("0");
        return shopReviewMapper.insertShopReview(shopReview);
    }

    /**
     * 修改商品评价
     * 
     * @param shopReview 商品评价
     * @return 结果
     */
    @Override
    public int updateShopReview(ShopReview shopReview)
    {
        shopReview.setUpdateTime(DateUtils.getNowDate());
        return shopReviewMapper.updateShopReview(shopReview);
    }

    /**
     * 批量删除商品评价
     * 
     * @param reviewIds 需要删除的商品评价主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteShopReviewByReviewIds(Long[] reviewIds)
    {
        // 在删除前查询所有评价信息，收集需要更新信用评分的店铺ID
        java.util.Set<Long> storeIdsToUpdate = new java.util.HashSet<>();
        for (Long reviewId : reviewIds)
        {
            ShopReview review = shopReviewMapper.selectShopReviewByReviewId(reviewId);
            if (review != null && "1".equals(review.getStatus()) && review.getProductId() != null)
            {
                ShopProduct product = shopProductService.selectShopProductByProductId(review.getProductId());
                if (product != null && product.getStoreId() != null)
                {
                    storeIdsToUpdate.add(product.getStoreId());
                }
            }
        }
        
        // 批量删除评价（逻辑删除）
        int result = shopReviewMapper.deleteShopReviewByReviewIds(reviewIds);
        
        // 重新计算受影响店铺的信用评分
        if (result > 0 && !storeIdsToUpdate.isEmpty())
        {
            for (Long storeId : storeIdsToUpdate)
            {
                updateStoreCreditScore(storeId);
            }
        }
        
        return result;
    }

    /**
     * 删除商品评价信息
     * 
     * @param reviewId 商品评价主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteShopReviewByReviewId(Long reviewId)
    {
        // 在删除前查询评价信息，用于后续更新信用评分
        ShopReview review = shopReviewMapper.selectShopReviewByReviewId(reviewId);
        Long storeId = null;
        if (review != null && review.getProductId() != null)
        {
            ShopProduct product = shopProductService.selectShopProductByProductId(review.getProductId());
            if (product != null && product.getStoreId() != null)
            {
                storeId = product.getStoreId();
            }
        }
        
        // 删除评价（逻辑删除）
        int result = shopReviewMapper.deleteShopReviewByReviewId(reviewId);
        
        // 如果删除的是已发布的评价，需要重新计算店铺信用评分
        if (result > 0 && storeId != null && review != null && "1".equals(review.getStatus()))
        {
            updateStoreCreditScore(storeId);
        }
        
        return result;
    }

    /**
     * 审核评价
     * 
     * @param reviewId 评价ID
     * @param status 状态（0待审核 1已发布 2已屏蔽）
     * @return 结果
     */
    @Override
    @Transactional
    public int auditReview(Long reviewId, String status)
    {
        // 更新评价状态
        ShopReview shopReview = new ShopReview();
        shopReview.setReviewId(reviewId);
        shopReview.setStatus(status);
        shopReview.setUpdateTime(DateUtils.getNowDate());
        int result = shopReviewMapper.updateShopReview(shopReview);
        
        // 如果评价状态变为已发布（1）或从已发布变为其他状态（0或2），需要重新计算店铺信用评分
        if (result > 0)
        {
            // 查询评价信息，获取店铺ID
            ShopReview review = shopReviewMapper.selectShopReviewByReviewId(reviewId);
            if (review != null && review.getProductId() != null)
            {
                // 通过商品获取店铺ID
                ShopProduct product = shopProductService.selectShopProductByProductId(review.getProductId());
                if (product != null && product.getStoreId() != null)
                {
                    // 重新计算并更新店铺信用评分
                    updateStoreCreditScore(product.getStoreId());
                }
            }
        }
        
        return result;
    }

    /**
     * 更新店铺信用评分
     * 基于该店铺所有已发布评价的平均评分
     * 
     * @param storeId 店铺ID
     */
    private void updateStoreCreditScore(Long storeId)
    {
        try
        {
            // 调用店铺服务的统计方法计算平均评分
            Double avgRating = shopStoreService.calculateAverageRatingByStoreId(storeId);
            
            // 如果没有评价，保持默认评分5.0
            BigDecimal creditScore;
            if (avgRating != null && avgRating > 0)
            {
                // 保留1位小数，四舍五入
                creditScore = BigDecimal.valueOf(avgRating).setScale(1, java.math.RoundingMode.HALF_UP);
                // 确保评分在1.0-5.0之间
                if (creditScore.compareTo(BigDecimal.ONE) < 0)
                {
                    creditScore = BigDecimal.ONE;
                }
                else if (creditScore.compareTo(new BigDecimal("5.0")) > 0)
                {
                    creditScore = new BigDecimal("5.0");
                }
            }
            else
            {
                creditScore = new BigDecimal("5.0");
            }
            
            // 更新店铺信用评分
            ShopStore updateStore = new ShopStore();
            updateStore.setStoreId(storeId);
            updateStore.setCreditScore(creditScore);
            shopStoreService.updateShopStore(updateStore);
        }
        catch (Exception e)
        {
            // 记录日志但不影响评价审核流程
            System.err.println("更新店铺信用评分失败，店铺ID: " + storeId + ", 错误: " + e.getMessage());
        }
    }
}

