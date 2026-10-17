package com.ruoyi.shop.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.mapper.ShopCategoryMapper;
import com.ruoyi.shop.domain.ShopCategory;
import com.ruoyi.shop.service.IShopCategoryService;

/**
 * 商品分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopCategoryServiceImpl implements IShopCategoryService 
{
    @Autowired
    private ShopCategoryMapper shopCategoryMapper;

    /**
     * 查询商品分类
     * 
     * @param categoryId 商品分类主键
     * @return 商品分类
     */
    @Override
    public ShopCategory selectShopCategoryByCategoryId(Long categoryId)
    {
        return shopCategoryMapper.selectShopCategoryByCategoryId(categoryId);
    }

    /**
     * 查询商品分类列表
     * 
     * @param shopCategory 商品分类
     * @return 商品分类
     */
    @Override
    public List<ShopCategory> selectShopCategoryList(ShopCategory shopCategory)
    {
        return shopCategoryMapper.selectShopCategoryList(shopCategory);
    }

    /**
     * 新增商品分类
     * 
     * @param shopCategory 商品分类
     * @return 结果
     */
    @Override
    public int insertShopCategory(ShopCategory shopCategory)
    {
        return shopCategoryMapper.insertShopCategory(shopCategory);
    }

    /**
     * 修改商品分类
     * 
     * @param shopCategory 商品分类
     * @return 结果
     */
    @Override
    public int updateShopCategory(ShopCategory shopCategory)
    {
        return shopCategoryMapper.updateShopCategory(shopCategory);
    }

    /**
     * 批量删除商品分类
     * 
     * @param categoryIds 需要删除的商品分类主键
     * @return 结果
     */
    @Override
    public int deleteShopCategoryByCategoryIds(Long[] categoryIds)
    {
        return shopCategoryMapper.deleteShopCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除商品分类信息
     * 
     * @param categoryId 商品分类主键
     * @return 结果
     */
    @Override
    public int deleteShopCategoryByCategoryId(Long categoryId)
    {
        return shopCategoryMapper.deleteShopCategoryByCategoryId(categoryId);
    }

    /**
     * 检查分类是否可以删除（是否有商品或子分类）
     * 
     * @param categoryId 分类ID
     * @return 错误消息，如果为null表示可以删除
     */
    @Override
    public String checkCategoryCanDelete(Long categoryId)
    {
        // 检查是否有子分类
        int childCount = shopCategoryMapper.countChildrenByCategoryId(categoryId);
        if (childCount > 0)
        {
            return "该分类下存在子分类，无法删除";
        }
        
        // 检查是否有商品
        int productCount = shopCategoryMapper.countProductByCategoryId(categoryId);
        if (productCount > 0)
        {
            return "该分类下存在商品，无法删除";
        }
        
        return null; // 可以删除
    }
}

