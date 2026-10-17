package com.ruoyi.shop.mapper;

import java.util.List;
import com.ruoyi.shop.domain.ShopReview;

/**
 * 商品评价Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopReviewMapper 
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
     * 根据商品ID查询评价列表
     * 
     * @param productId 商品ID
     * @return 商品评价集合
     */
    public List<ShopReview> selectShopReviewListByProductId(Long productId);

    /**
     * 根据用户ID查询评价列表
     * 
     * @param userId 用户ID
     * @return 商品评价集合
     */
    public List<ShopReview> selectShopReviewListByUserId(Long userId);

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
     * 删除商品评价
     * 
     * @param reviewId 商品评价主键
     * @return 结果
     */
    public int deleteShopReviewByReviewId(Long reviewId);

    /**
     * 批量删除商品评价
     * 
     * @param reviewIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopReviewByReviewIds(Long[] reviewIds);
}

