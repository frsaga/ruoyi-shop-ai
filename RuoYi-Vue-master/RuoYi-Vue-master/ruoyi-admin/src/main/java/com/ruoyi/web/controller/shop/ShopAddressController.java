package com.ruoyi.web.controller.shop;

import java.util.List;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.domain.ShopAddress;
import com.ruoyi.shop.service.IShopAddressService;

/**
 * 收货地址Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/address")
public class ShopAddressController extends BaseController
{
    @Autowired
    private IShopAddressService shopAddressService;

    /**
     * 查询收货地址列表
     */
    @PreAuthorize("@ss.hasPermi('shop:address:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = SecurityUtils.getUserId();
        List<ShopAddress> list = shopAddressService.selectShopAddressListByUserId(userId);
        return success(list);
    }

    /**
     * 获取收货地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:address:query')")
    @GetMapping(value = "/{addressId}")
    public AjaxResult getInfo(@PathVariable("addressId") Long addressId)
    {
        return success(shopAddressService.selectShopAddressByAddressId(addressId));
    }

    /**
     * 新增收货地址
     */
    @PreAuthorize("@ss.hasPermi('shop:address:add')")
    @Log(title = "收货地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopAddress shopAddress)
    {
        Long userId = SecurityUtils.getUserId();
        shopAddress.setUserId(userId);
        return toAjax(shopAddressService.insertShopAddress(shopAddress));
    }

    /**
     * 修改收货地址
     */
    @PreAuthorize("@ss.hasPermi('shop:address:edit')")
    @Log(title = "收货地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopAddress shopAddress)
    {
        Long userId = SecurityUtils.getUserId();
        shopAddress.setUserId(userId);
        return toAjax(shopAddressService.updateShopAddress(shopAddress));
    }

    /**
     * 设置默认地址
     */
    @PreAuthorize("@ss.hasPermi('shop:address:edit')")
    @Log(title = "设置默认地址", businessType = BusinessType.UPDATE)
    @PutMapping("/default/{addressId}")
    public AjaxResult setDefault(@PathVariable Long addressId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(shopAddressService.setDefaultAddress(addressId, userId));
    }

    /**
     * 删除收货地址
     */
    @PreAuthorize("@ss.hasPermi('shop:address:remove')")
    @Log(title = "收货地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{addressIds}")
    public AjaxResult remove(@PathVariable Long[] addressIds)
    {
        return toAjax(shopAddressService.deleteShopAddressByAddressIds(addressIds));
    }
}

