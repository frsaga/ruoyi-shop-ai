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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shop.domain.ShopReview;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.domain.ShopOrder;
import com.ruoyi.shop.domain.ShopOrderItem;
import com.ruoyi.shop.service.IShopReviewService;
import com.ruoyi.shop.service.IShopStoreService;
import com.ruoyi.shop.service.IShopProductService;
import com.ruoyi.shop.service.IShopOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品评价Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/review")
public class ShopReviewController extends BaseController
{
    @Autowired
    private IShopReviewService shopReviewService;

    @Autowired
    private IShopStoreService shopStoreService;

    @Autowired
    private IShopProductService shopProductService;

    @Autowired
    private IShopOrderService shopOrderService;

    /**
     * 查询商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('shop:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopReview shopReview)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 管理员或具有管理权限的角色可以查看所有评价
        if (!SecurityUtils.hasAdminPermission(userId))
        {
            // 检查用户是否有店铺（是否为卖家）
            com.ruoyi.shop.domain.ShopStore userStore = shopStoreService.selectShopStoreByUserId(userId);
            if (userStore != null && userStore.getStoreId() != null)
            {
                // 用户有店铺，说明是卖家，只能查看自己店铺商品的评价
                shopReview.setStoreId(userStore.getStoreId());
            }
            else
            {
                // 用户没有店铺，是买家，只能查看自己的评价
                shopReview.setUserId(userId);
            }
        }
        // 如果是管理员或普通角色（角色ID=2），不设置userId和storeId，可以查看所有评价
        
        startPage();
        List<ShopReview> list = shopReviewService.selectShopReviewList(shopReview);
        return getDataTable(list);
    }

    /**
     * 根据商品ID查询评价列表（公开接口，用于商品详情页）
     */
    @GetMapping("/product/{productId}")
    public AjaxResult getReviewsByProductId(@PathVariable("productId") Long productId)
    {
        List<ShopReview> list = shopReviewService.selectShopReviewListByProductId(productId);
        return success(list);
    }

    /**
     * 导出商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('shop:review:export')")
    @Log(title = "商品评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopReview shopReview)
    {
        List<ShopReview> list = shopReviewService.selectShopReviewList(shopReview);
        ExcelUtil<ShopReview> util = new ExcelUtil<ShopReview>(ShopReview.class);
        util.exportExcel(response, list, "商品评价数据");
    }

    /**
     * 获取商品评价详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:review:query')")
    @GetMapping(value = "/{reviewId}")
    public AjaxResult getInfo(@PathVariable("reviewId") Long reviewId)
    {
        return success(shopReviewService.selectShopReviewByReviewId(reviewId));
    }

    /**
     * 新增商品评价
     */
    @PreAuthorize("@ss.hasPermi('shop:review:add')")
    @Log(title = "商品评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopReview shopReview)
    {
        // 自动设置当前用户ID
        Long userId = SecurityUtils.getUserId();
        shopReview.setUserId(userId);
        
        // 验证订单状态：只能对已完成(3)的订单进行评价
        if (shopReview.getOrderId() != null)
        {
            ShopOrder order = shopOrderService.selectShopOrderByOrderId(shopReview.getOrderId());
            if (order == null)
            {
                return error("订单不存在");
            }
            if (!order.getUserId().equals(userId))
            {
                return error("只能评价自己的订单");
            }
            if (order.getStatus() != 3)
            {
                return error("只能对已确认收货的订单进行评价");
            }
            
            // 验证该订单该商品是否已评价
            ShopReview existingReview = new ShopReview();
            existingReview.setOrderId(shopReview.getOrderId());
            existingReview.setProductId(shopReview.getProductId());
            existingReview.setUserId(userId);
            List<ShopReview> existingReviews = shopReviewService.selectShopReviewList(existingReview);
            if (existingReviews != null && !existingReviews.isEmpty())
            {
                return error("该商品已评价过了");
            }
            
            // 自动设置商品所属店铺ID
            if (shopReview.getProductId() != null)
            {
                ShopProduct product = shopProductService.selectShopProductByProductId(shopReview.getProductId());
                if (product != null && product.getStoreId() != null)
                {
                    shopReview.setStoreId(product.getStoreId());
                }
            }
        }
        
        return toAjax(shopReviewService.insertShopReview(shopReview));
    }

    /**
     * 修改商品评价
     */
    @PreAuthorize("@ss.hasPermi('shop:review:edit')")
    @Log(title = "商品评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopReview shopReview)
    {
        // 权限验证：只能修改自己的评价
        Long userId = SecurityUtils.getUserId();
        ShopReview existingReview = shopReviewService.selectShopReviewByReviewId(shopReview.getReviewId());
        if (existingReview == null || !existingReview.getUserId().equals(userId))
        {
            return error("只能修改自己的评价");
        }
        return toAjax(shopReviewService.updateShopReview(shopReview));
    }

    /**
     * 删除商品评价
     */
    @PreAuthorize("@ss.hasPermi('shop:review:remove')")
    @Log(title = "商品评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reviewIds}")
    public AjaxResult remove(@PathVariable Long[] reviewIds)
    {
        // 权限验证：买家只能删除自己的评价，卖家可以删除自己店铺商品的评价
        Long userId = SecurityUtils.getUserId();
        com.ruoyi.shop.domain.ShopStore userStore = shopStoreService.selectShopStoreByUserId(userId);
        
        if (userStore == null || userStore.getStoreId() == null)
        {
            // 买家：只能删除自己的评价
            for (Long reviewId : reviewIds)
            {
                ShopReview review = shopReviewService.selectShopReviewByReviewId(reviewId);
                if (review == null || !review.getUserId().equals(userId))
                {
                    return error("只能删除自己的评价");
                }
            }
        }
        else
        {
            // 卖家：只能删除自己店铺商品的评价
            for (Long reviewId : reviewIds)
            {
                ShopReview review = shopReviewService.selectShopReviewByReviewId(reviewId);
                if (review != null && review.getProductId() != null)
                {
                    // 需要查询商品所属店铺
                    ShopProduct product = shopProductService.selectShopProductByProductId(review.getProductId());
                    if (product == null || !product.getStoreId().equals(userStore.getStoreId()))
                    {
                        return error("只能删除自己店铺商品的评价");
                    }
                }
            }
        }
        
        return toAjax(shopReviewService.deleteShopReviewByReviewIds(reviewIds));
    }

    /**
     * 审核评价
     */
    @PreAuthorize("@ss.hasPermi('shop:review:audit')")
    @Log(title = "商品评价", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{reviewId}/{status}")
    public AjaxResult auditReview(@PathVariable("reviewId") Long reviewId, @PathVariable("status") String status)
    {
        // 权限验证：只有卖家可以审核自己店铺商品的评价，管理员可以审核所有评价
        Long userId = SecurityUtils.getUserId();
        ShopReview review = shopReviewService.selectShopReviewByReviewId(reviewId);
        if (review == null)
        {
            return error("评价不存在");
        }
        
        // 检查是否是卖家，如果是卖家，需要检查评价的商品是否属于自己的店铺
        com.ruoyi.shop.domain.ShopStore userStore = shopStoreService.selectShopStoreByUserId(userId);
        if (userStore != null && userStore.getStoreId() != null)
        {
            // 是卖家，检查商品是否属于该店铺
            if (review.getProductId() != null)
            {
                ShopProduct product = shopProductService.selectShopProductByProductId(review.getProductId());
                if (product == null || !product.getStoreId().equals(userStore.getStoreId()))
                {
                    return error("只能审核自己店铺商品的评价");
                }
            }
        }
        // 管理员可以审核所有评价（没有店铺的用户，可能是管理员）
        
        return toAjax(shopReviewService.auditReview(reviewId, status));
    }

    /**
     * 检查订单商品是否可以评价
     */
    @GetMapping("/canReview/{orderId}/{productId}")
    public AjaxResult canReview(@PathVariable("orderId") Long orderId, @PathVariable("productId") Long productId)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 验证订单状态
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权访问");
        }
        
        if (order.getStatus() != 3)
        {
            return AjaxResult.success("不可评价", false);
        }
        
        // 检查是否已评价
        ShopReview existingReview = new ShopReview();
        existingReview.setOrderId(orderId);
        existingReview.setProductId(productId);
        existingReview.setUserId(userId);
        List<ShopReview> existingReviews = shopReviewService.selectShopReviewList(existingReview);
        
        if (existingReviews != null && !existingReviews.isEmpty())
        {
            return AjaxResult.success("已评价", false);
        }
        
        return AjaxResult.success("可以评价", true);
    }

    /**
     * 获取订单的所有商品及评价状态
     */
    @GetMapping("/orderItems/{orderId}")
    public AjaxResult getOrderItemsWithReviewStatus(@PathVariable("orderId") Long orderId)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 验证订单权限
        ShopOrder order = shopOrderService.selectShopOrderByOrderId(orderId);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权访问");
        }
        
        // 获取订单商品列表
        List<ShopOrderItem> items = order.getOrderItems();
        if (items != null)
        {
            for (ShopOrderItem item : items)
            {
                // 检查每个商品的评价状态
                ShopReview reviewQuery = new ShopReview();
                reviewQuery.setOrderId(orderId);
                reviewQuery.setProductId(item.getProductId());
                reviewQuery.setUserId(userId);
                List<ShopReview> reviews = shopReviewService.selectShopReviewList(reviewQuery);
                
                // 设置是否已评价
                item.setReviewed(reviews != null && !reviews.isEmpty());
            }
        }
        
        return success(items);
    }
}

