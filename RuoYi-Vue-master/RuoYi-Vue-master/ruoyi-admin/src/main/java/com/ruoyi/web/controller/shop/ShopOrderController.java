package com.ruoyi.web.controller.shop;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.domain.ShopOrder;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.service.IShopOrderService;
import com.ruoyi.shop.service.IShopStoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/order")
public class ShopOrderController extends BaseController
{
    @Autowired
    private IShopOrderService shopOrderService;

    @Autowired
    private IShopStoreService shopStoreService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('shop:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopOrder shopOrder)
    {
        // 根据用户角色过滤数据
        Long userId = SecurityUtils.getUserId();
        
        // 管理员或具有管理权限的角色可以查看所有订单
        if (!SecurityUtils.hasAdminPermission(userId))
        {
            // 检查用户是否有卖家角色（通过是否有店铺判断）
            ShopStore store = shopStoreService.selectShopStoreByUserId(userId);
            if (store != null && store.getStoreId() != null)
            {
                // 用户有店铺，说明是卖家，只能查看自己店铺的订单
                shopOrder.setStoreId(store.getStoreId());
            }
            else
            {
                // 用户没有店铺，是买家，只能查看自己的订单
                shopOrder.setUserId(userId);
            }
        }
        // 如果是管理员或普通角色（角色ID=2），不设置userId和storeId，可以查看所有订单
        
        startPage();
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        return getDataTable(list);
    }

    /**
     * 公开的订单列表查询接口（商城使用，只需登录，无需权限）
     * 用户只能查看自己的订单
     */
    @GetMapping("/public/list")
    public TableDataInfo listPublic(ShopOrder shopOrder)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(401);
            dataTable.setMsg("用户未登录");
            return dataTable;
        }
        
        // 用户只能查看自己的订单
        shopOrder.setUserId(userId);
        
        startPage();
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('shop:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopOrder shopOrder)
    {
        List<ShopOrder> list = shopOrderService.selectShopOrderList(shopOrder);
        ExcelUtil<ShopOrder> util = new ExcelUtil<ShopOrder>(ShopOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:order:detail')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(shopOrderService.selectShopOrderByOrderId(orderId));
    }

    /**
     * 公开的订单详情查询接口（商城使用，只需登录，无需权限）
     * 用户只能查看自己的订单
     */
    @GetMapping(value = "/public/{orderId}")
    public AjaxResult getPublicInfo(@PathVariable("orderId") Long orderId)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return error("只能查看自己的订单");
        }
        
        return success(order);
    }

    /**
     * 创建订单
     */
    @PreAuthorize("@ss.hasPermi('shop:order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopOrder shopOrder)
    {
        // 设置当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        shopOrder.setUserId(userId);
        int result = shopOrderService.createOrder(shopOrder);
        if (result > 0) {
            // 返回订单信息，包含orderId和orderNo
            ShopOrder createdOrder = shopOrderService.selectShopOrderByOrderId(shopOrder.getOrderId());
            return success(createdOrder);
        }
        return error("订单创建失败");
    }

    /**
     * 批量创建订单（按店铺分组）
     */
    @PreAuthorize("@ss.hasPermi('shop:order:add')")
    @Log(title = "批量创建订单", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult addBatch(@RequestBody List<ShopOrder> shopOrderList)
    {
        // 设置当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        for (ShopOrder shopOrder : shopOrderList)
        {
            shopOrder.setUserId(userId);
        }
        List<ShopOrder> createdOrders = shopOrderService.createOrdersBatch(shopOrderList);
        return success(createdOrders);
    }

    /**
     * 公开的批量创建订单接口（商城使用，只需登录，无需权限）
     * 所有登录用户都可以创建订单
     */
    @Log(title = "批量创建订单", businessType = BusinessType.INSERT)
    @PostMapping("/public/batch")
    public AjaxResult addBatchPublic(@RequestBody List<ShopOrder> shopOrderList)
    {
        // 设置当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        for (ShopOrder shopOrder : shopOrderList)
        {
            shopOrder.setUserId(userId);
        }
        List<ShopOrder> createdOrders = shopOrderService.createOrdersBatch(shopOrderList);
        return success(createdOrders);
    }

    /**
     * 支付订单
     */
    @PreAuthorize("@ss.hasPermi('shop:order:pay')")
    @Log(title = "订单支付", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/{orderId}")
    public AjaxResult pay(@PathVariable Long orderId)
    {
        return toAjax(shopOrderService.payOrder(orderId));
    }

    /**
     * 公开的支付订单接口（商城使用，只需登录，无需权限）
     * 用户只能支付自己的订单
     */
    @Log(title = "订单支付", businessType = BusinessType.UPDATE)
    @PutMapping("/public/pay/{orderId}")
    public AjaxResult payPublic(@PathVariable Long orderId)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        // 验证订单是否属于当前用户
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return error("只能支付自己的订单");
        }
        
        return toAjax(shopOrderService.payOrder(orderId));
    }

    /**
     * 批量支付订单
     */
    @PreAuthorize("@ss.hasPermi('shop:order:pay')")
    @Log(title = "批量支付订单", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/batch")
    public AjaxResult payBatch(@RequestBody Long[] orderIds)
    {
        int result = shopOrderService.payOrdersBatch(orderIds);
        if (result > 0) {
            return success("成功支付 " + result + " 个订单");
        }
        return error("支付失败");
    }

    /**
     * 公开的批量支付订单接口（商城使用，只需登录，无需权限）
     * 用户只能支付自己的订单
     */
    @Log(title = "批量支付订单", businessType = BusinessType.UPDATE)
    @PutMapping("/public/pay/batch")
    public AjaxResult payBatchPublic(@RequestBody Long[] orderIds)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        // 验证所有订单是否属于当前用户
        for (Long orderId : orderIds) {
            ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
            if (order == null) {
                return error("订单不存在，订单ID: " + orderId);
            }
            if (!order.getUserId().equals(userId)) {
                return error("只能支付自己的订单，订单ID: " + orderId);
            }
        }
        
        int result = shopOrderService.payOrdersBatch(orderIds);
        if (result > 0) {
            return success("成功支付 " + result + " 个订单");
        }
        return error("支付失败");
    }

    /**
     * 发货
     */
    @PreAuthorize("@ss.hasPermi('shop:order:send')")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    @PutMapping("/send/{orderId}")
    public AjaxResult send(@PathVariable Long orderId)
    {
        return toAjax(shopOrderService.sendOrder(orderId));
    }

    /**
     * 确认收货
     */
    @PreAuthorize("@ss.hasPermi('shop:order:confirm')")
    @Log(title = "确认收货", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{orderId}")
    public AjaxResult confirm(@PathVariable Long orderId)
    {
        return toAjax(shopOrderService.confirmOrder(orderId));
    }

    /**
     * 公开的确认收货接口（商城使用，只需登录，无需权限）
     * 用户只能确认收货自己的订单
     */
    @Log(title = "确认收货", businessType = BusinessType.UPDATE)
    @PutMapping("/public/confirm/{orderId}")
    public AjaxResult confirmPublic(@PathVariable Long orderId)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        // 验证订单是否属于当前用户
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return error("只能确认收货自己的订单");
        }
        
        return toAjax(shopOrderService.confirmOrder(orderId));
    }

    /**
     * 取消订单
     */
    @PreAuthorize("@ss.hasPermi('shop:order:cancel')")
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        return toAjax(shopOrderService.cancelOrder(orderId));
    }

    /**
     * 公开的取消订单接口（商城使用，只需登录，无需权限）
     * 用户只能取消自己的订单
     */
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    @PutMapping("/public/cancel/{orderId}")
    public AjaxResult cancelPublic(@PathVariable Long orderId)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }
        
        // 验证订单是否属于当前用户
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null) {
            return error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return error("只能取消自己的订单");
        }
        
        return toAjax(shopOrderService.cancelOrder(orderId));
    }
}

