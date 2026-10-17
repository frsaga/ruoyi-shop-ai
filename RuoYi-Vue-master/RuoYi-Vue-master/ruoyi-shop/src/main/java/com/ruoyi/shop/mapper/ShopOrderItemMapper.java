package com.ruoyi.shop.mapper;

import java.util.List;
import com.ruoyi.shop.domain.ShopOrderItem;

/**
 * 订单明细Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopOrderItemMapper 
{
    /**
     * 查询订单明细
     * 
     * @param orderItemId 订单明细主键
     * @return 订单明细
     */
    public ShopOrderItem selectShopOrderItemByOrderItemId(Long orderItemId);

    /**
     * 根据订单ID查询订单明细列表
     * 
     * @param orderId 订单ID
     * @return 订单明细集合
     */
    public List<ShopOrderItem> selectShopOrderItemListByOrderId(Long orderId);

    /**
     * 查询订单明细列表
     * 
     * @param shopOrderItem 订单明细
     * @return 订单明细集合
     */
    public List<ShopOrderItem> selectShopOrderItemList(ShopOrderItem shopOrderItem);

    /**
     * 新增订单明细
     * 
     * @param shopOrderItem 订单明细
     * @return 结果
     */
    public int insertShopOrderItem(ShopOrderItem shopOrderItem);

    /**
     * 批量新增订单明细
     * 
     * @param orderItems 订单明细列表
     * @return 结果
     */
    public int batchInsertShopOrderItem(List<ShopOrderItem> orderItems);

    /**
     * 修改订单明细
     * 
     * @param shopOrderItem 订单明细
     * @return 结果
     */
    public int updateShopOrderItem(ShopOrderItem shopOrderItem);

    /**
     * 删除订单明细
     * 
     * @param orderItemId 订单明细主键
     * @return 结果
     */
    public int deleteShopOrderItemByOrderItemId(Long orderItemId);

    /**
     * 批量删除订单明细
     * 
     * @param orderItemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopOrderItemByOrderItemIds(Long[] orderItemIds);

    /**
     * 根据订单ID删除订单明细
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteShopOrderItemByOrderId(Long orderId);
}

