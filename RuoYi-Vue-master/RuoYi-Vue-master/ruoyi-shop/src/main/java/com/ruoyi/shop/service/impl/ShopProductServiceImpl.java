package com.ruoyi.shop.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.mapper.ShopProductMapper;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.service.IShopProductService;

/**
 * 商品Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopProductServiceImpl implements IShopProductService 
{
    @Autowired
    private ShopProductMapper shopProductMapper;

    /**
     * 查询商品
     * 
     * @param productId 商品主键
     * @return 商品
     */
    @Override
    public ShopProduct selectShopProductByProductId(Long productId)
    {
        return shopProductMapper.selectShopProductByProductId(productId);
    }

    /**
     * 查询商品列表
     * 
     * @param shopProduct 商品
     * @return 商品
     */
    @Override
    public List<ShopProduct> selectShopProductList(ShopProduct shopProduct)
    {
        return shopProductMapper.selectShopProductList(shopProduct);
    }

    /**
     * 新增商品
     * 
     * @param shopProduct 商品
     * @return 结果
     */
    @Override
    public int insertShopProduct(ShopProduct shopProduct)
    {
        shopProduct.setCreateTime(DateUtils.getNowDate());
        shopProduct.setStatus("0"); // 默认待审核状态
        return shopProductMapper.insertShopProduct(shopProduct);
    }

    /**
     * 修改商品
     * 
     * @param shopProduct 商品
     * @return 结果
     */
    @Override
    public int updateShopProduct(ShopProduct shopProduct)
    {
        shopProduct.setUpdateTime(DateUtils.getNowDate());
        return shopProductMapper.updateShopProduct(shopProduct);
    }

    /**
     * 批量删除商品
     * 
     * @param productIds 需要删除的商品主键
     * @return 结果
     */
    @Override
    public int deleteShopProductByProductIds(Long[] productIds)
    {
        return shopProductMapper.deleteShopProductByProductIds(productIds);
    }

    /**
     * 删除商品信息
     * 
     * @param productId 商品主键
     * @return 结果
     */
    @Override
    public int deleteShopProductByProductId(Long productId)
    {
        return shopProductMapper.deleteShopProductByProductId(productId);
    }

    /**
     * 商品审核
     * 
     * @param productId 商品ID
     * @param status 审核状态 (1通过 2拒绝)
     * @return 结果
     */
    @Override
    public int auditProduct(Long productId, String status)
    {
        ShopProduct product = new ShopProduct();
        product.setProductId(productId);
        product.setStatus(status);
        product.setUpdateTime(DateUtils.getNowDate());
        return shopProductMapper.updateShopProduct(product);
    }

    /**
     * 检查商品是否可以删除（是否有订单）
     * 
     * @param productId 商品ID
     * @return 错误消息，如果为null表示可以删除
     */
    @Override
    public String checkProductCanDelete(Long productId)
    {
        // 检查是否有订单
        int orderCount = shopProductMapper.countOrderByProductId(productId);
        if (orderCount > 0)
        {
            return "该商品已产生订单，无法删除";
        }
        
        return null; // 可以删除
    }
}

