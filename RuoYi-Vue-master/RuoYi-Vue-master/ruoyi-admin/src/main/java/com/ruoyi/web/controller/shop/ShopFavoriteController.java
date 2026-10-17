package com.ruoyi.web.controller.shop;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.domain.ShopFavorite;
import com.ruoyi.shop.service.IShopFavoriteService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品收藏Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/favorite")
public class ShopFavoriteController extends BaseController
{
    @Autowired
    private IShopFavoriteService shopFavoriteService;

    /**
     * 查询用户收藏列表
     */
    @PreAuthorize("@ss.hasPermi('shop:favorite:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopFavorite shopFavorite)
    {
        // 只能查看自己的收藏
        Long userId = SecurityUtils.getUserId();
        shopFavorite.setUserId(userId);
        
        startPage();
        List<ShopFavorite> list = shopFavoriteService.selectShopFavoriteList(shopFavorite);
        return getDataTable(list);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public AjaxResult checkFavorite(@PathVariable("productId") Long productId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isFavorite = shopFavoriteService.isFavorite(userId, productId);
        return success(isFavorite);
    }

    /**
     * 新增收藏
     */
    @PreAuthorize("@ss.hasPermi('shop:favorite:add')")
    @Log(title = "商品收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopFavorite shopFavorite)
    {
        // 自动设置当前用户ID
        Long userId = SecurityUtils.getUserId();
        shopFavorite.setUserId(userId);
        
        int result = shopFavoriteService.insertShopFavorite(shopFavorite);
        if (result > 0) {
            return success("收藏成功");
        } else {
            return error("该商品已收藏");
        }
    }

    /**
     * 取消收藏（根据商品ID）
     */
    @PreAuthorize("@ss.hasPermi('shop:favorite:remove')")
    @Log(title = "商品收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/product/{productId}")
    public AjaxResult remove(@PathVariable("productId") Long productId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(shopFavoriteService.cancelFavorite(userId, productId));
    }

    /**
     * 批量删除收藏（根据收藏ID）
     */
    @PreAuthorize("@ss.hasPermi('shop:favorite:remove')")
    @Log(title = "商品收藏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{favoriteIds}")
    public AjaxResult removeBatch(@PathVariable Long[] favoriteIds)
    {
        return toAjax(shopFavoriteService.deleteShopFavoriteByFavoriteIds(favoriteIds));
    }
}

