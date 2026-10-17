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
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.service.IShopStoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 店铺Controller
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@RestController
@RequestMapping("/shop/store")
public class ShopStoreController extends BaseController
{
    @Autowired
    private IShopStoreService shopStoreService;

    /**
     * 查询店铺列表
     */
    @PreAuthorize("@ss.hasPermi('shop:store:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShopStore shopStore)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 如果是管理员或具有管理权限的角色，可以查看所有店铺
        if (!SecurityUtils.hasAdminPermission(userId))
        {
            // 非管理员，检查用户是否是卖家（角色ID=101）
            // 通过检查用户角色来判断，而不是通过是否有店铺来判断
            // 这样可以防止新注册的卖家（还没有创建店铺）看到所有店铺
            try
            {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && loginUser.getUser() != null && loginUser.getUser().getRoles() != null)
                {
                    List<SysRole> roles = loginUser.getUser().getRoles();
                    // 检查是否拥有卖家角色（角色ID=101）
                    boolean isSeller = roles.stream().anyMatch(role -> 
                        role.getRoleId() != null && 101L == role.getRoleId()
                    );
                    
                    if (isSeller)
            {
                        // 卖家只能查看自己的店铺（即使还没有创建店铺，也只能看到自己的店铺，即看不到任何店铺）
                        shopStore.setUserId(userId);
                    }
                    // 如果是买家（角色ID=100），不应该有店铺管理权限，但为了安全，也设置为只能查看自己的店铺
                    // 实际上买家不应该访问店铺管理页面，因为需要shop:store:list权限
                }
            }
            catch (Exception e)
            {
                // 如果获取角色失败，为了安全，设置为只能查看自己的店铺
                shopStore.setUserId(userId);
            }
        }
        // 如果是管理员或普通角色（角色ID=2），可以查看所有店铺（不设置userId）
        
        startPage();
        List<ShopStore> list = shopStoreService.selectShopStoreList(shopStore);
        return getDataTable(list);
    }

    /**
     * 导出店铺列表
     */
    @PreAuthorize("@ss.hasPermi('shop:store:export')")
    @Log(title = "店铺", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShopStore shopStore)
    {
        List<ShopStore> list = shopStoreService.selectShopStoreList(shopStore);
        ExcelUtil<ShopStore> util = new ExcelUtil<ShopStore>(ShopStore.class);
        util.exportExcel(response, list, "店铺数据");
    }

    /**
     * 获取店铺详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:store:query')")
    @GetMapping(value = "/{storeId}")
    public AjaxResult getInfo(@PathVariable("storeId") Long storeId)
    {
        Long userId = SecurityUtils.getUserId();
        ShopStore store = shopStoreService.selectShopStoreByStoreId(storeId);
        
        if (store == null)
        {
            return error("店铺不存在");
        }
        
        // 如果是管理员或具有管理权限的角色，可以查看任何店铺
        if (!SecurityUtils.hasAdminPermission(userId))
        {
            // 非管理员，检查店铺是否属于当前用户
            // 直接检查店铺的userId，而不是通过selectShopStoreByUserId
            if (!userId.equals(store.getUserId()))
            {
                return error("无权查看其他用户的店铺");
            }
        }
        
        return success(store);
    }

    /**
     * 新增店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:add')")
    @Log(title = "店铺", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShopStore shopStore)
    {
        // 自动设置店主用户ID
        Long userId = SecurityUtils.getUserId();
        shopStore.setUserId(userId);
        return toAjax(shopStoreService.insertShopStore(shopStore));
    }

    /**
     * 修改店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:edit')")
    @Log(title = "店铺", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShopStore shopStore)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 如果是管理员或具有管理权限的角色，可以修改任何店铺
        if (!SecurityUtils.hasAdminPermission(userId))
        {
            // 非管理员，检查店铺是否属于当前用户
            // 先查询店铺信息，检查userId
            ShopStore existingStore = shopStoreService.selectShopStoreByStoreId(shopStore.getStoreId());
            if (existingStore == null)
            {
                return error("店铺不存在");
            }
            if (!userId.equals(existingStore.getUserId()))
            {
                return error("只能修改自己的店铺");
            }
            // 确保用户不能修改店铺的userId
            shopStore.setUserId(userId);
        }
        // 管理员和common角色可以修改任何店铺
        
        return toAjax(shopStoreService.updateShopStore(shopStore));
    }

    /**
     * 删除店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:remove')")
    @Log(title = "店铺", businessType = BusinessType.DELETE)
	@DeleteMapping("/{storeIds}")
    public AjaxResult remove(@PathVariable Long[] storeIds)
    {
        // 检查每个店铺是否可以删除
        for (Long storeId : storeIds)
        {
            String errorMsg = shopStoreService.checkStoreCanDelete(storeId);
            if (errorMsg != null)
            {
                return error(errorMsg);
            }
        }
        return toAjax(shopStoreService.deleteShopStoreByStoreIds(storeIds));
    }

    /**
     * 审核店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:audit')")
    @Log(title = "店铺审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{storeId}/{status}")
    public AjaxResult audit(@PathVariable Long storeId, @PathVariable String status)
    {
        return toAjax(shopStoreService.auditStore(storeId, status));
    }

    /**
     * 获取店铺主页信息（公开接口，无需权限验证）
     */
    @GetMapping("/public/{storeId}")
    public AjaxResult getStorePublic(@PathVariable("storeId") Long storeId)
    {
        ShopStore store = shopStoreService.selectShopStoreByStoreId(storeId);
        if (store == null)
        {
            return error("店铺不存在");
        }
        if (!"1".equals(store.getStatus()))
        {
            return error("店铺状态异常，无法访问（状态：" + store.getStatus() + "，1=正常，0=待审核，2=停用）");
        }
        return success(store);
    }
}

