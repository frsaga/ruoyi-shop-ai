package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shop.domain.ShopCart;
import com.ruoyi.shop.service.IShopCartService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 购物车Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/cart")
public class ShopCartController extends BaseController
{
    @Autowired
    private IShopCartService shopCartService;

    /**
     * 查询购物车列表
     */
    @PreAuthorize("@ss.hasPermi('shop:cart:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = SecurityUtils.getUserId();
        List<ShopCart> list = shopCartService.selectShopCartListByUserId(userId);
        return success(list);
    }

    /**
     * 添加商品到购物车
     */
    @PreAuthorize("@ss.hasPermi('shop:cart:add')")
    @Log(title = "购物车", businessType = BusinessType.INSERT)
    @PostMapping("/{productId}/{quantity}")
    public AjaxResult add(@PathVariable Long productId, @PathVariable Integer quantity)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(shopCartService.addToCart(userId, productId, quantity));
    }

    /**
     * 更新购物车商品数量
     */
    @PreAuthorize("@ss.hasPermi('shop:cart:edit')")
    @Log(title = "购物车", businessType = BusinessType.UPDATE)
    @PutMapping("/{cartId}/{quantity}")
    public AjaxResult edit(@PathVariable Long cartId, @PathVariable Integer quantity)
    {
        return toAjax(shopCartService.updateCartQuantity(cartId, quantity));
    }

    /**
     * 删除购物车商品
     */
    @PreAuthorize("@ss.hasPermi('shop:cart:remove')")
    @Log(title = "购物车", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cartIds}")
    public AjaxResult remove(@PathVariable Long[] cartIds)
    {
        return toAjax(shopCartService.deleteShopCartByCartIds(cartIds));
    }

    /**
     * 清空购物车
     */
    @PreAuthorize("@ss.hasPermi('shop:cart:clear')")
    @Log(title = "购物车", businessType = BusinessType.DELETE)
	@DeleteMapping("/clear")
    public AjaxResult clear()
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(shopCartService.clearCart(userId));
    }
}

