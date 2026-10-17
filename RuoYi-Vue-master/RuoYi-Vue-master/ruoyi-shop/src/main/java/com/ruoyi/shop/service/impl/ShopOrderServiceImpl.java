package com.ruoyi.shop.service.impl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.shop.domain.ShopOrder;
import com.ruoyi.shop.domain.ShopOrderItem;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.domain.ShopReview;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.mapper.ShopOrderItemMapper;
import com.ruoyi.shop.mapper.ShopOrderMapper;
import com.ruoyi.shop.service.IShopOrderService;
import com.ruoyi.shop.service.IShopProductService;
import com.ruoyi.shop.service.IShopReviewService;
import com.ruoyi.shop.service.IShopStoreService;

/**
 * 订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopOrderServiceImpl implements IShopOrderService 
{
    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopOrderItemMapper shopOrderItemMapper;

    @Autowired
    private IShopProductService shopProductService;

    @Autowired
    private IShopReviewService shopReviewService;

    @Autowired
    private IShopStoreService shopStoreService;

    /**
     * 查询订单
     * 
     * @param orderId 订单主键
     * @return 订单
     */
    @Override
    public ShopOrder selectShopOrderByOrderId(Long orderId)
    {
        ShopOrder order = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (order != null) {
            // 查询订单明细
            List<ShopOrderItem> orderItems = shopOrderItemMapper.selectShopOrderItemListByOrderId(orderId);
            order.setOrderItems(orderItems);
        }
        return order;
    }

    /**
     * 查询订单列表
     * 
     * @param shopOrder 订单
     * @return 订单
     */
    @Override
    public List<ShopOrder> selectShopOrderList(ShopOrder shopOrder)
    {
        List<ShopOrder> list = shopOrderMapper.selectShopOrderList(shopOrder);
        
        // 对状态为3（已完成）的订单，检查是否所有商品都已评价
        // 只检查订单的userId等于当前用户的userId的订单（即买家查看自己的订单）
        if (list != null && !list.isEmpty()) {
            Long userId = SecurityUtils.getUserId();
            if (userId != null) {
                for (ShopOrder order : list) {
                    // 只检查状态为3（已完成）且订单属于当前用户的订单
                    if (order.getStatus() != null && order.getStatus() == 3 
                        && order.getUserId() != null && order.getUserId().equals(userId)) {
                        // 检查该订单是否所有商品都已评价
                        boolean allReviewed = checkAllItemsReviewed(order.getOrderId(), userId);
                        order.setAllReviewed(allReviewed);
                    }
                    // 对于其他情况（卖家查看订单、非已完成状态等），allReviewed保持为null
                    // 前端可以根据allReviewed是否为null来判断是否显示评价按钮
                }
            }
        }
        
        return list;
    }

    /**
     * 检查订单的所有商品是否都已评价
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return true表示所有商品都已评价，false表示还有商品未评价
     */
    private boolean checkAllItemsReviewed(Long orderId, Long userId)
    {
        // 获取订单的所有商品
        List<ShopOrderItem> orderItems = shopOrderItemMapper.selectShopOrderItemListByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            // 如果没有商品，返回true（不需要评价）
            return true;
        }
        
        // 检查每个商品是否都已评价
        for (ShopOrderItem item : orderItems) {
            ShopReview reviewQuery = new ShopReview();
            reviewQuery.setOrderId(orderId);
            reviewQuery.setProductId(item.getProductId());
            reviewQuery.setUserId(userId);
            List<ShopReview> reviews = shopReviewService.selectShopReviewList(reviewQuery);
            
            // 如果该商品没有评价记录，说明还未评价
            if (reviews == null || reviews.isEmpty()) {
                return false;
            }
        }
        
        // 所有商品都已评价
        return true;
    }

    /**
     * 创建订单
     * 
     * @param shopOrder 订单
     * @return 结果
     */
    @Override
    @Transactional
    public int createOrder(ShopOrder shopOrder)
    {
        // 从订单明细的第一个商品获取storeId（如果订单缺少storeId）
        if (shopOrder.getStoreId() == null && shopOrder.getOrderItems() != null && shopOrder.getOrderItems().size() > 0) {
            ShopOrderItem firstItem = shopOrder.getOrderItems().get(0);
            if (firstItem.getProductId() != null) {
                ShopProduct product = shopProductService.selectShopProductByProductId(firstItem.getProductId());
                if (product != null && product.getStoreId() != null) {
                    shopOrder.setStoreId(product.getStoreId());
                } else {
                    throw new RuntimeException("商品信息不完整，商品ID: " + firstItem.getProductId() + " 缺少店铺信息");
                }
            } else {
                throw new RuntimeException("订单明细中商品ID不能为空");
            }
        }
        
        // 验证storeId是否已设置
        if (shopOrder.getStoreId() == null) {
            throw new RuntimeException("订单缺少店铺信息，无法创建订单");
        }
        
        // 生成订单号 创建时间
        shopOrder.setOrderNo(generateOrderNo());
        shopOrder.setStatus(0); // 待支付
        shopOrder.setCreateTime(DateUtils.getNowDate());
        
        // 插入订单
        int rows = shopOrderMapper.insertShopOrder(shopOrder);
        
        // 插入订单明细
        if (shopOrder.getOrderItems() != null && shopOrder.getOrderItems().size() > 0) {
            for (ShopOrderItem item : shopOrder.getOrderItems()) {
                item.setOrderId(shopOrder.getOrderId());
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                
                // 如果缺少商品信息，需要从商品表获取
                if (item.getProductId() != null) {
                    ShopProduct product = shopProductService.selectShopProductByProductId(item.getProductId());
                    if (product != null) {
                        // 设置商品名称（必填字段）
                        if (item.getProductName() == null || item.getProductName().isEmpty()) {
                            item.setProductName(product.getProductName());
                        }
                        // 设置当前价格（必填字段）
                        if (item.getCurrentPrice() == null) {
                            if (product.getPrice() != null) {
                                item.setCurrentPrice(product.getPrice());
                            } else {
                                item.setCurrentPrice(BigDecimal.ZERO);
                            }
                        }
                        // 设置商品图片（可以为空字符串）
                        if (item.getProductImage() == null || item.getProductImage().isEmpty()) {
                            if (product.getMainImage() != null && !product.getMainImage().isEmpty()) {
                                item.setProductImage(product.getMainImage());
                            } else {
                                item.setProductImage("");
                            }
                        }
                    } else {
                        throw new RuntimeException("商品不存在，商品ID: " + item.getProductId());
                    }
                } else {
                    throw new RuntimeException("订单明细中商品ID不能为空");
                }
                
                // 计算小计（totalPrice = currentPrice * quantity，必填字段）
                if (item.getCurrentPrice() != null && item.getQuantity() != null) {
                    BigDecimal total = item.getCurrentPrice().multiply(new BigDecimal(item.getQuantity()));
                    item.setTotalPrice(total);
                } else {
                    throw new RuntimeException("订单明细中价格或数量不能为空");
                }
                
                // 确保数量大于0
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new RuntimeException("订单明细中商品数量必须大于0");
                }
            }
            shopOrderItemMapper.batchInsertShopOrderItem(shopOrder.getOrderItems());
        }
        
        return rows;
    }

    /**
     * 批量创建订单（按店铺分组）
     * 
     * @param shopOrderList 订单列表（每个订单对应一个店铺）
     * @return 创建的订单列表
     */
    @Override
    @Transactional
    public List<ShopOrder> createOrdersBatch(List<ShopOrder> shopOrderList)
    {
        List<ShopOrder> createdOrders = new java.util.ArrayList<>();
        for (ShopOrder shopOrder : shopOrderList)
        {
            createOrder(shopOrder);
            // 重新查询订单以获取完整信息
            ShopOrder createdOrder = selectShopOrderByOrderId(shopOrder.getOrderId());
            createdOrders.add(createdOrder);
        }
        return createdOrders;
    }

    /**
     * 批量支付订单
     * 
     * @param orderIds 订单ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int payOrdersBatch(Long[] orderIds)
    {
        int successCount = 0;
        for (Long orderId : orderIds)
        {
            int result = payOrder(orderId);
            if (result > 0)
            {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelOrder(Long orderId)
    {
        // 查询订单信息，判断是否需要恢复库存
        ShopOrder existingOrder = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (existingOrder == null)
        {
            throw new RuntimeException("订单不存在，订单ID: " + orderId);
        }
        
        // 如果订单已支付（状态为1已支付或2已发货），需要恢复库存
        // 如果订单已完成（状态为3），需要恢复库存和销量
        // 状态：0待支付 1已支付 2已发货 3已完成 4已关闭 5已取消
        if (existingOrder.getStatus() != null && 
            (existingOrder.getStatus() == 1 || existingOrder.getStatus() == 2 || existingOrder.getStatus() == 3))
        {
            // 查询订单明细
            List<ShopOrderItem> orderItems = shopOrderItemMapper.selectShopOrderItemListByOrderId(orderId);
            if (orderItems != null && !orderItems.isEmpty())
            {
                // 用于统计每个店铺需要减少的销量
                java.util.Map<Long, Integer> storeSalesMap = new java.util.HashMap<>();
                
                // 恢复商品库存，如果订单已完成还需要恢复销量
                for (ShopOrderItem item : orderItems)
                {
                    Integer quantity = item.getQuantity();
                    if (item.getProductId() != null && quantity != null && quantity > 0)
                    {
                        // 查询商品当前信息
                        ShopProduct product = shopProductService.selectShopProductByProductId(item.getProductId());
                        if (product != null)
                        {
                            // 恢复库存
                            ShopProduct updateProduct = new ShopProduct();
                            updateProduct.setProductId(item.getProductId());
                            Integer currentStock = product.getStock();
                            updateProduct.setStock((currentStock == null ? 0 : currentStock) + quantity);
                            shopProductService.updateShopProduct(updateProduct);
                            
                            // 如果订单已完成，需要恢复商品销量和店铺销量
                            if (existingOrder.getStatus() == 3)
                            {
                                // 恢复商品销量
                                Integer currentSalesCount = product.getSalesCount();
                                if (currentSalesCount != null && currentSalesCount >= quantity)
                                {
                                    ShopProduct updateSalesProduct = new ShopProduct();
                                    updateSalesProduct.setProductId(item.getProductId());
                                    updateSalesProduct.setSalesCount(currentSalesCount - quantity);
                                    shopProductService.updateShopProduct(updateSalesProduct);
                                }
                                
                                // 统计店铺销量减少量
                                if (product.getStoreId() != null)
                                {
                                    storeSalesMap.put(product.getStoreId(), 
                                        storeSalesMap.getOrDefault(product.getStoreId(), 0) + quantity);
                                }
                            }
                        }
                    }
                }
                
                // 如果订单已完成，恢复店铺总销量
                if (existingOrder.getStatus() == 3 && !storeSalesMap.isEmpty())
                {
                    for (java.util.Map.Entry<Long, Integer> entry : storeSalesMap.entrySet())
                    {
                        Long storeId = entry.getKey();
                        Integer quantity = entry.getValue();
                        
                        ShopStore store = shopStoreService.selectShopStoreByStoreId(storeId);
                        if (store != null)
                        {
                            ShopStore updateStore = new ShopStore();
                            updateStore.setStoreId(storeId);
                            Long currentSalesVolume = store.getSalesVolume();
                            if (currentSalesVolume != null && currentSalesVolume >= quantity)
                            {
                                updateStore.setSalesVolume(currentSalesVolume - quantity);
                                shopStoreService.updateShopStore(updateStore);
                            }
                        }
                    }
                }
            }
        }
        
        // 更新订单状态
        ShopOrder order = new ShopOrder();
        order.setOrderId(orderId);
        order.setStatus(5); // 已取消
        order.setCloseTime(new Date());
        order.setUpdateTime(DateUtils.getNowDate());
        return shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int payOrder(Long orderId)
    {
        // 查询订单明细
        List<ShopOrderItem> orderItems = shopOrderItemMapper.selectShopOrderItemListByOrderId(orderId);
        if (orderItems != null && !orderItems.isEmpty())
        {
            // 减少商品库存
            for (ShopOrderItem item : orderItems)
            {
                Integer quantity = item.getQuantity();
                if (item.getProductId() != null && quantity != null && quantity > 0)
                {
                    // 查询商品当前信息
                    ShopProduct product = shopProductService.selectShopProductByProductId(item.getProductId());
                    if (product != null)
                    {
                        // 检查库存是否充足
                        Integer stock = product.getStock();
                        if (stock == null || stock < quantity)
                        {
                            throw new RuntimeException("库存不足，商品: " + product.getProductName() + "，当前库存: " + stock + "，需要: " + quantity);
                        }
                        
                        // 更新库存
                        ShopProduct updateProduct = new ShopProduct();
                        updateProduct.setProductId(item.getProductId());
                        updateProduct.setStock(stock - quantity);
                        shopProductService.updateShopProduct(updateProduct);
                    }
                }
            }
        }
        
        // 更新订单状态
        ShopOrder order = new ShopOrder();
        order.setOrderId(orderId);
        order.setStatus(1); // 已支付
        order.setPaymentTime(new Date());
        order.setUpdateTime(DateUtils.getNowDate());
        return shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 发货
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int sendOrder(Long orderId)
    {
        ShopOrder order = new ShopOrder();
        order.setOrderId(orderId);
        order.setStatus(2); // 已发货
        order.setSendTime(new Date());
        order.setUpdateTime(DateUtils.getNowDate());
        return shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int confirmOrder(Long orderId)
    {
        // 查询订单信息，获取店铺ID
        ShopOrder orderInfo = shopOrderMapper.selectShopOrderByOrderId(orderId);
        if (orderInfo == null)
        {
            throw new RuntimeException("订单不存在，订单ID: " + orderId);
        }
        
        // 查询订单明细
        List<ShopOrderItem> orderItems = shopOrderItemMapper.selectShopOrderItemListByOrderId(orderId);
        if (orderItems != null && !orderItems.isEmpty())
        {
            // 用于统计每个店铺的总销量增量
            java.util.Map<Long, Integer> storeSalesMap = new java.util.HashMap<>();
            
            // 增加商品销量和统计店铺销量
            for (ShopOrderItem item : orderItems)
            {
                Integer quantity = item.getQuantity();
                if (item.getProductId() != null && quantity != null && quantity > 0)
                {
                    // 查询商品当前信息
                    ShopProduct product = shopProductService.selectShopProductByProductId(item.getProductId());
                    if (product != null)
                    {
                        // 更新商品销量
                        ShopProduct updateProduct = new ShopProduct();
                        updateProduct.setProductId(item.getProductId());
                        Integer currentSalesCount = product.getSalesCount();
                        updateProduct.setSalesCount((currentSalesCount == null ? 0 : currentSalesCount) + quantity);
                        shopProductService.updateShopProduct(updateProduct);
                        
                        // 统计店铺销量增量
                        if (product.getStoreId() != null)
                        {
                            storeSalesMap.put(product.getStoreId(), 
                                storeSalesMap.getOrDefault(product.getStoreId(), 0) + quantity);
                        }
                    }
                }
            }
            
            // 更新店铺总销量
            for (java.util.Map.Entry<Long, Integer> entry : storeSalesMap.entrySet())
            {
                Long storeId = entry.getKey();
                Integer quantity = entry.getValue();
                
                ShopStore store = shopStoreService.selectShopStoreByStoreId(storeId);
                if (store != null)
                {
                    ShopStore updateStore = new ShopStore();
                    updateStore.setStoreId(storeId);
                    Long currentSalesVolume = store.getSalesVolume();
                    updateStore.setSalesVolume((currentSalesVolume == null ? 0L : currentSalesVolume) + quantity);
                    shopStoreService.updateShopStore(updateStore);
                }
            }
        }
        
        // 更新订单状态
        ShopOrder order = new ShopOrder();
        order.setOrderId(orderId);
        order.setStatus(3); // 已完成
        order.setEndTime(new Date());
        order.setUpdateTime(DateUtils.getNowDate());
        return shopOrderMapper.updateShopOrder(order);
    }

    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    private String generateOrderNo()
    {
        return "ORD" + System.currentTimeMillis() + IdUtils.fastSimpleUUID().substring(0, 6);
    }
}

