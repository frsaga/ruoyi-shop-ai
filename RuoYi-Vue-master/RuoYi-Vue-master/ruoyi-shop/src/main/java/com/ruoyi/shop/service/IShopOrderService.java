package com.ruoyi.shop.service;

import java.util.List;
import com.ruoyi.shop.domain.ShopOrder;

/**
 * 订单Service接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface IShopOrderService 
{
    /**
     * 查询订单
     * 
     * @param orderId 订单主键
     * @return 订单
     */
    public ShopOrder selectShopOrderByOrderId(Long orderId);

    /**
     * 查询订单列表
     * 
     * @param shopOrder 订单
     * @return 订单集合
     */
    public List<ShopOrder> selectShopOrderList(ShopOrder shopOrder);

    /**
     * 创建订单
     * 
     * @param shopOrder 订单
     * @return 结果
     */
    public int createOrder(ShopOrder shopOrder);

    /**
     * 批量创建订单（按店铺分组）
     * 
     * @param shopOrderList 订单列表（每个订单对应一个店铺）
     * @return 创建的订单列表
     */
    public List<ShopOrder> createOrdersBatch(List<ShopOrder> shopOrderList);

    /**
     * 批量支付订单
     * 
     * @param orderIds 订单ID数组
     * @return 结果
     */
    public int payOrdersBatch(Long[] orderIds);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int cancelOrder(Long orderId);

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int payOrder(Long orderId);

    /**
     * 发货
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int sendOrder(Long orderId);

    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int confirmOrder(Long orderId);
}

