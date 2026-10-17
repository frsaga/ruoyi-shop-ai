package com.ruoyi.web.controller.shop;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.shop.domain.ShopCategory;
import com.ruoyi.shop.service.IShopCategoryService;

/**
 * 商品分类Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/category")
public class ShopCategoryController extends BaseController
{
    @Autowired
    private IShopCategoryService shopCategoryService;

    /**
     * 查询商品分类列表
     */
    @PreAuthorize("@ss.hasPermi('shop:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopCategory shopCategory)
    {
        startPage();
        List<ShopCategory> list = shopCategoryService.selectShopCategoryList(shopCategory);
        return getDataTable(list);
    }

    /**
     * 导出商品分类列表
     */
    @PreAuthorize("@ss.hasPermi('shop:category:export')")
    @Log(title = "商品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopCategory shopCategory)
    {
        List<ShopCategory> list = shopCategoryService.selectShopCategoryList(shopCategory);
        ExcelUtil<ShopCategory> util = new ExcelUtil<ShopCategory>(ShopCategory.class);
        util.exportExcel(response, list, "商品分类数据");
    }

    /**
     * 获取商品分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(shopCategoryService.selectShopCategoryByCategoryId(categoryId));
    }

    /**
     * 新增商品分类
     */
    @PreAuthorize("@ss.hasPermi('shop:category:add')")
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopCategory shopCategory)
    {
        return toAjax(shopCategoryService.insertShopCategory(shopCategory));
    }

    /**
     * 修改商品分类
     */
    @PreAuthorize("@ss.hasPermi('shop:category:edit')")
    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopCategory shopCategory)
    {
        return toAjax(shopCategoryService.updateShopCategory(shopCategory));
    }

    /**
     * 删除商品分类
     */
    @PreAuthorize("@ss.hasPermi('shop:category:remove')")
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        // 检查每个分类是否可以删除
        for (Long categoryId : categoryIds)
        {
            String errorMsg = shopCategoryService.checkCategoryCanDelete(categoryId);
            if (errorMsg != null)
            {
                return error(errorMsg);
            }
        }
        return toAjax(shopCategoryService.deleteShopCategoryByCategoryIds(categoryIds));
    }

    /**
     * 获取商品分类树形结构（公开接口）
     */
    @GetMapping("/tree")
    public AjaxResult tree()
    {
        ShopCategory query = new ShopCategory();
        query.setStatus("0"); // 只查询正常状态的分类
        List<ShopCategory> list = shopCategoryService.selectShopCategoryList(query);
        return success(buildCategoryTree(list));
    }

    /**
     * 获取顶级分类列表（公开接口，用于商城首页）
     */
    @GetMapping("/topLevel")
    public AjaxResult getTopLevelCategories()
    {
        ShopCategory query = new ShopCategory();
        query.setStatus("0"); // 只查询正常状态
        query.setParentId(0L); // 顶级分类
        List<ShopCategory> list = shopCategoryService.selectShopCategoryList(query);
        return success(list);
    }

    /**
     * 根据父级ID获取子分类（公开接口）
     */
    @GetMapping("/children/{parentId}")
    public AjaxResult getChildrenCategories(@PathVariable Long parentId)
    {
        ShopCategory query = new ShopCategory();
        query.setStatus("0"); // 只查询正常状态
        query.setParentId(parentId);
        List<ShopCategory> list = shopCategoryService.selectShopCategoryList(query);
        return success(list);
    }

    /**
     * 构建分类树形结构
     */
    private List<ShopCategory> buildCategoryTree(List<ShopCategory> categories)
    {
        List<ShopCategory> tree = new java.util.ArrayList<>();
        for (ShopCategory category : categories)
        {
            if (category.getParentId() == null || category.getParentId() == 0)
            {
                tree.add(findChildren(category, categories));
            }
        }
        return tree;
    }

    /**
     * 递归查找子分类
     */
    private ShopCategory findChildren(ShopCategory parent, List<ShopCategory> allCategories)
    {
        List<ShopCategory> children = new java.util.ArrayList<>();
        for (ShopCategory category : allCategories)
        {
            if (category.getParentId() != null && category.getParentId().equals(parent.getCategoryId()))
            {
                children.add(findChildren(category, allCategories));
            }
        }
        parent.setChildren(children);
        return parent;
    }
}

