package com.ruoyi.shop.mapper;

import java.util.List;
import com.ruoyi.shop.domain.ShopOrder;

/**
 * 订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopOrderMapper 
{
    /**
     * 查询订单
     * 
     * @param orderId 订单主键
     * @return 订单
     */
    public ShopOrder selectShopOrderByOrderId(Long orderId);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单
     */
    public ShopOrder selectShopOrderByOrderNo(String orderNo);

    /**
     * 查询订单列表
     * 
     * @param shopOrder 订单
     * @return 订单集合
     */
    public List<ShopOrder> selectShopOrderList(ShopOrder shopOrder);

    /**
     * 新增订单
     * 
     * @param shopOrder 订单
     * @return 结果
     */
    public int insertShopOrder(ShopOrder shopOrder);

    /**
     * 修改订单
     * 
     * @param shopOrder 订单
     * @return 结果
     */
    public int updateShopOrder(ShopOrder shopOrder);

    /**
     * 删除订单
     * 
     * @param orderId 订单主键
     * @return 结果
     */
    public int deleteShopOrderByOrderId(Long orderId);

    /**
     * 批量删除订单
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopOrderByOrderIds(Long[] orderIds);
}

