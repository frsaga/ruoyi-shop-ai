package com.ruoyi.shop.service;

import java.util.List;
import com.ruoyi.shop.domain.ShopReview;

/**
 * 商品评价Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopReviewService 
{
    /**
     * 查询商品评价
     * 
     * @param reviewId 商品评价主键
     * @return 商品评价
     */
    public ShopReview selectShopReviewByReviewId(Long reviewId);

    /**
     * 查询商品评价列表
     * 
     * @param shopReview 商品评价
     * @return 商品评价集合
     */
    public List<ShopReview> selectShopReviewList(ShopReview shopReview);

    /**
     * 根据商品ID查询评价列表（已发布状态）
     * 
     * @param productId 商品ID
     * @return 商品评价集合
     */
    public List<ShopReview> selectShopReviewListByProductId(Long productId);

    /**
     * 根据店铺ID查询评价列表（卖家查看自己店铺商品的评价）
     * 
     * @param storeId 店铺ID
     * @return 商品评价集合
     */
    public List<ShopReview> selectShopReviewListByStoreId(Long storeId);

    /**
     * 新增商品评价
     * 
     * @param shopReview 商品评价
     * @return 结果
     */
    public int insertShopReview(ShopReview shopReview);

    /**
     * 修改商品评价
     * 
     * @param shopReview 商品评价
     * @return 结果
     */
    public int updateShopReview(ShopReview shopReview);

    /**
     * 批量删除商品评价
     * 
     * @param reviewIds 需要删除的商品评价主键集合
     * @return 结果
     */
    public int deleteShopReviewByReviewIds(Long[] reviewIds);

    /**
     * 删除商品评价信息
     * 
     * @param reviewId 商品评价主键
     * @return 结果
     */
    public int deleteShopReviewByReviewId(Long reviewId);

    /**
     * 审核评价
     * 
     * @param reviewId 评价ID
     * @param status 状态（0待审核 1已发布 2已屏蔽）
     * @return 结果
     */
    public int auditReview(Long reviewId, String status);
}

