package com.ruoyi.shop.mapper;

import java.util.List;
import com.ruoyi.shop.domain.ShopProduct;

/**
 * 商品Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopProductMapper 
{
    /**
     * 查询商品
     * 
     * @param productId 商品主键
     * @return 商品
     */
    public ShopProduct selectShopProductByProductId(Long productId);

    /**
     * 查询商品列表
     * 
     * @param shopProduct 商品
     * @return 商品集合
     */
    public List<ShopProduct> selectShopProductList(ShopProduct shopProduct);

    /**
     * 新增商品
     * 
     * @param shopProduct 商品
     * @return 结果
     */
    public int insertShopProduct(ShopProduct shopProduct);

    /**
     * 修改商品
     * 
     * @param shopProduct 商品
     * @return 结果
     */
    public int updateShopProduct(ShopProduct shopProduct);

    /**
     * 删除商品
     * 
     * @param productId 商品主键
     * @return 结果
     */
    public int deleteShopProductByProductId(Long productId);

    /**
     * 批量删除商品
     * 
     * @param productIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopProductByProductIds(Long[] productIds);

    /**
     * 统计店铺下的商品数量
     * 
     * @param storeId 店铺ID
     * @return 商品数量
     */
    public int countProductByStoreId(Long storeId);

    /**
     * 统计商品是否有订单
     * 
     * @param productId 商品ID
     * @return 订单数量
     */
    public int countOrderByProductId(Long productId);
}

