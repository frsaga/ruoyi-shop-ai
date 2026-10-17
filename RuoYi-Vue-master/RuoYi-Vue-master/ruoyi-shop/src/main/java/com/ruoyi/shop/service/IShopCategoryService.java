package com.ruoyi.shop.service;

import java.util.List;
import com.ruoyi.shop.domain.ShopCategory;

/**
 * 商品分类Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopCategoryService 
{
    /**
     * 查询商品分类
     * 
     * @param categoryId 商品分类主键
     * @return 商品分类
     */
    public ShopCategory selectShopCategoryByCategoryId(Long categoryId);

    /**
     * 查询商品分类列表
     * 
     * @param shopCategory 商品分类
     * @return 商品分类集合
     */
    public List<ShopCategory> selectShopCategoryList(ShopCategory shopCategory);

    /**
     * 新增商品分类
     * 
     * @param shopCategory 商品分类
     * @return 结果
     */
    public int insertShopCategory(ShopCategory shopCategory);

    /**
     * 修改商品分类
     * 
     * @param shopCategory 商品分类
     * @return 结果
     */
    public int updateShopCategory(ShopCategory shopCategory);

    /**
     * 批量删除商品分类
     * 
     * @param categoryIds 需要删除的商品分类主键集合
     * @return 结果
     */
    public int deleteShopCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除商品分类信息
     * 
     * @param categoryId 商品分类主键
     * @return 结果
     */
    public int deleteShopCategoryByCategoryId(Long categoryId);

    /**
     * 检查分类是否可以删除（是否有商品或子分类）
     * 
     * @param categoryId 分类ID
     * @return 错误消息，如果为null表示可以删除
     */
    public String checkCategoryCanDelete(Long categoryId);
}

