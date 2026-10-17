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
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.service.IShopProductService;
import com.ruoyi.shop.service.IShopStoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/product")
public class ShopProductController extends BaseController
{
    @Autowired
    private IShopProductService shopProductService;

    @Autowired
    private IShopStoreService shopStoreService;

    /**
     * 查询商品列表（商品管理页面使用，需要权限）
     */
    @PreAuthorize("@ss.hasPermi('shop:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopProduct shopProduct)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 如果是管理员或具有管理权限的角色，可以查看所有商品（不设置storeId和userId）
        if (SecurityUtils.hasAdminPermission(userId))
        {
            // 管理员可以查看所有商品，不设置任何过滤条件
        }
        else
        {
            // 非管理员，检查用户是否是卖家（角色ID=101）
            // 通过检查用户角色来判断，而不是通过是否有店铺来判断
            // 这样可以防止新注册的卖家（还没有创建店铺）看到所有商品
            boolean isSeller = false;
            try
            {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && loginUser.getUser() != null && loginUser.getUser().getRoles() != null)
                {
                    List<SysRole> roles = loginUser.getUser().getRoles();
                    // 检查是否拥有卖家角色（角色ID=101）
                    isSeller = roles.stream().anyMatch(role -> 
                        role.getRoleId() != null && 101L == role.getRoleId()
                    );
                }
            }
            catch (Exception e)
            {
                // 如果获取角色失败，为了安全，设置为非卖家
                isSeller = false;
            }
            
            if (isSeller)
            {
                // 用户是卖家角色，检查是否有店铺
                ShopStore queryStore = new ShopStore();
                queryStore.setUserId(userId);
                List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
                
                if (userStores != null && !userStores.isEmpty())
                {
                    // 用户有店铺，通过userId查询所有店铺的商品
                    // 如果前端传入了storeId，则优先使用storeId（支持单个店铺筛选）
                    if (shopProduct.getStoreId() == null)
                    {
                        // 没有指定店铺，查询该用户所有店铺的商品
                        shopProduct.setUserId(userId);
                    }
                    else
                    {
                        // 如果指定了storeId，需要验证该店铺是否属于当前用户
                        boolean isOwner = userStores.stream()
                            .anyMatch(store -> store.getStoreId().equals(shopProduct.getStoreId()));
                        if (!isOwner)
                        {
                            // 指定的店铺不属于当前用户，重置为查询所有店铺
                            shopProduct.setStoreId(null);
                            shopProduct.setUserId(userId);
                        }
                    }
                }
                else
                {
                    // 用户是卖家角色，但还没有创建店铺，应该返回空列表
                    // 设置一个不可能匹配的条件来确保返回空列表
                    shopProduct.setUserId(-1L);
                }
            }
            else
            {
                // 用户不是卖家角色，也不应该是管理员（已在上面的if中处理）
                // 为了安全，设置一个不可能匹配的条件来确保返回空列表
                shopProduct.setUserId(-1L);
            }
        }
        
        startPage();
        List<ShopProduct> list = shopProductService.selectShopProductList(shopProduct);
        return getDataTable(list);
    }

    /**
     * 导出商品列表
     */
    @PreAuthorize("@ss.hasPermi('shop:product:export')")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopProduct shopProduct)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 优先检查用户是否是卖家（有店铺）
        // 卖家可以导出自己所有店铺的商品
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 用户有店铺，说明是卖家，通过userId查询所有店铺的商品
            // 如果前端传入了storeId，则优先使用storeId（支持单个店铺筛选）
            if (shopProduct.getStoreId() == null)
            {
                // 没有指定店铺，查询该用户所有店铺的商品
                shopProduct.setUserId(userId);
            }
            // 如果指定了storeId，需要验证该店铺是否属于当前用户
            else
            {
                boolean isOwner = userStores.stream()
                    .anyMatch(store -> store.getStoreId().equals(shopProduct.getStoreId()));
                if (!isOwner)
                {
                    // 指定的店铺不属于当前用户，重置为查询所有店铺
                    shopProduct.setStoreId(null);
                    shopProduct.setUserId(userId);
                }
            }
        }
        // 如果用户没有店铺，且是管理员或具有管理权限的角色，可以导出所有商品（不设置storeId和userId）
        
        List<ShopProduct> list = shopProductService.selectShopProductList(shopProduct);
        ExcelUtil<ShopProduct> util = new ExcelUtil<ShopProduct>(ShopProduct.class);
        util.exportExcel(response, list, "商品数据");
    }

    /**
     * 获取商品详细信息（商品管理页面使用，需要权限）
     */
    @PreAuthorize("@ss.hasPermi('shop:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        ShopProduct product = shopProductService.selectShopProductByProductId(productId);
        if (product == null)
        {
            return error("商品不存在");
        }
        
        // 如果是卖家，验证店铺权限
        Long userId = SecurityUtils.getUserId();
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 卖家只能查看自己所有店铺的商品
            boolean isOwner = userStores.stream()
                .anyMatch(store -> store.getStoreId().equals(product.getStoreId()));
            if (!isOwner)
            {
                return error("只能查看自己店铺的商品");
            }
        }
        // 如果用户没有店铺，且是管理员或具有管理权限的角色，可以查看所有商品
        
        return success(product);
    }

    /**
     * 公开的商品详情查询（商城首页使用，只需登录，无需权限）
     * 只能查看在售商品的详情
     */
    @GetMapping(value = "/public/{productId}")
    public AjaxResult getPublicInfo(@PathVariable("productId") Long productId)
    {
        ShopProduct product = shopProductService.selectShopProductByProductId(productId);
        if (product == null)
        {
            return error("商品不存在");
        }
        
        // 公开接口只能查看在售商品
        if (!"1".equals(product.getStatus()))
        {
            return error("商品已下架或未上架");
        }
        
        return success(product);
    }

    /**
     * 公开的商品浏览接口（用于商城首页）
     * 所有登录用户都可以访问，只返回在售商品
     */
    @GetMapping("/public/list")
    public TableDataInfo publicList(ShopProduct shopProduct)
    {
        // 商城首页只显示在售商品
        shopProduct.setStatus("1");
        // 不设置storeId，返回所有店铺的在售商品
        startPage();
        List<ShopProduct> list = shopProductService.selectShopProductList(shopProduct);
        return getDataTable(list);
    }

    /**
     * 根据店铺ID查询商品列表（公开接口，用于店铺主页）
     */
    @GetMapping("/store/{storeId}")
    public TableDataInfo listByStore(@PathVariable("storeId") Long storeId, ShopProduct shopProduct)
    {
        shopProduct.setStoreId(storeId);
        shopProduct.setStatus("1"); // 只查询在售商品
        startPage();
        List<ShopProduct> list = shopProductService.selectShopProductList(shopProduct);
        return getDataTable(list);
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('shop:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopProduct shopProduct)
    {
        // 如果是卖家，验证店铺权限
        Long userId = SecurityUtils.getUserId();
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 卖家只能在自己拥有的店铺下发布商品
            if (shopProduct.getStoreId() == null)
            {
                // 如果未选择店铺，使用第一个店铺（默认店铺）
                shopProduct.setStoreId(userStores.get(0).getStoreId());
            }
            else
            {
                // 验证选择的店铺是否属于当前用户
                boolean isOwner = userStores.stream()
                    .anyMatch(store -> store.getStoreId().equals(shopProduct.getStoreId()));
                if (!isOwner)
                {
                    return error("只能在自己拥有的店铺下发布商品");
                }
            }
        }
        // 如果是管理员，可以自由选择店铺（不限制）
        return toAjax(shopProductService.insertShopProduct(shopProduct));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('shop:product:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopProduct shopProduct)
    {
        // 如果是卖家，验证店铺权限
        Long userId = SecurityUtils.getUserId();
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 卖家只能修改自己所有店铺的商品，需要先查询原商品的店铺ID
            if (shopProduct.getProductId() != null)
            {
                ShopProduct existingProduct = shopProductService.selectShopProductByProductId(shopProduct.getProductId());
                if (existingProduct == null)
                {
                    return error("商品不存在");
                }
                // 验证商品是否属于当前用户的任一店铺
                boolean isOwner = userStores.stream()
                    .anyMatch(store -> store.getStoreId().equals(existingProduct.getStoreId()));
                if (!isOwner)
            {
                return error("只能修改自己店铺的商品");
            }
                // 确保不能修改店铺ID（保持原商品的店铺ID）
                shopProduct.setStoreId(existingProduct.getStoreId());
            }
            else
            {
                return error("商品ID不能为空");
            }
        }
        // 如果用户没有店铺，且是管理员或具有管理权限的角色，可以修改所有商品
        
        return toAjax(shopProductService.updateShopProduct(shopProduct));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('shop:product:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        // 如果是卖家，验证店铺权限
        Long userId = SecurityUtils.getUserId();
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 卖家只能删除自己所有店铺的商品
            for (Long productId : productIds)
            {
                ShopProduct product = shopProductService.selectShopProductByProductId(productId);
                if (product == null)
                {
                    return error("商品不存在");
                }
                // 验证商品是否属于当前用户的任一店铺
                boolean isOwner = userStores.stream()
                    .anyMatch(store -> store.getStoreId().equals(product.getStoreId()));
                if (!isOwner)
                {
                    return error("只能删除自己店铺的商品");
                }
            }
        }
        // 如果用户没有店铺，且是管理员或具有管理权限的角色，可以删除所有商品
        
        // 检查每个商品是否可以删除（是否有订单）
        for (Long productId : productIds)
        {
            String errorMsg = shopProductService.checkProductCanDelete(productId);
            if (errorMsg != null)
            {
                return error(errorMsg);
            }
        }
        
        return toAjax(shopProductService.deleteShopProductByProductIds(productIds));
    }

    /**
     * 审核商品
     */
    @PreAuthorize("@ss.hasPermi('shop:product:audit')")
    @Log(title = "商品审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{productId}/{status}")
    public AjaxResult audit(@PathVariable Long productId, @PathVariable String status)
    {
        // 审核功能通常只有管理员可以使用，但为了安全起见，也进行权限验证
        // 如果是卖家，不能审核商品（审核应该由管理员完成）
        Long userId = SecurityUtils.getUserId();
        ShopStore queryStore = new ShopStore();
        queryStore.setUserId(userId);
        List<ShopStore> userStores = shopStoreService.selectShopStoreList(queryStore);
        if (userStores != null && !userStores.isEmpty())
        {
            // 卖家不能审核商品，审核权限应该只给管理员
            return error("卖家无权审核商品");
        }
        // 如果用户没有店铺，且是管理员或具有管理权限的角色，可以审核商品
        
        return toAjax(shopProductService.auditProduct(productId, status));
    }
}

