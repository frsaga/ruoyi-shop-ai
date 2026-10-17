package com.ruoyi.shop.service.impl;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.shop.domain.ShopProduct;
import com.ruoyi.shop.domain.ShopOrder;
import com.ruoyi.shop.domain.ShopStore;
import com.ruoyi.shop.service.IShopProductService;
import com.ruoyi.shop.service.IShopOrderService;
import com.ruoyi.shop.service.IShopStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI数据查询助手
 * 用于根据用户问题智能查询相关数据
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Component
public class AiDataQueryHelper
{
    private static final Logger log = LoggerFactory.getLogger(AiDataQueryHelper.class);
    
    @Autowired
    private IShopProductService shopProductService;
    
    @Autowired
    private IShopOrderService shopOrderService;
    
    @Autowired
    private IShopStoreService shopStoreService;
    
    // 商品相关关键词
    private static final String[] PRODUCT_KEYWORDS = {"商品", "产品", "物品", "东西", "货", "价格", "多少钱", "库存", "销量", "推荐", "有什么", "哪些", "查看", "浏览", "搜索", "查找"};
    
    // 订单相关关键词
    private static final String[] ORDER_KEYWORDS = {"订单", "下单", "购买", "支付", "发货", "收货", "物流", "订单号", "我的订单", "订单状态", "订单查询"};
    
    // 店铺相关关键词
    private static final String[] STORE_KEYWORDS = {"店铺", "商店", "商家", "卖家", "店铺名", "店铺信息"};
    
    /**
     * 根据用户问题查询相关数据
     * 
     * @param userId 用户ID
     * @param message 用户消息
     * @return 查询到的数据上下文（用于提供给AI）
     */
    public String queryRelevantData(Long userId, String message)
    {
        if (message == null || message.trim().isEmpty())
        {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        String lowerMessage = message.toLowerCase();
        
        // 查询商品信息
        if (containsKeywords(lowerMessage, PRODUCT_KEYWORDS))
        {
            String productContext = queryProducts(lowerMessage);
            if (!productContext.isEmpty())
            {
                context.append("=== 商品信息 ===\n").append(productContext).append("\n\n");
            }
        }
        
        // 查询订单信息（仅查询当前用户的订单）
        if (containsKeywords(lowerMessage, ORDER_KEYWORDS))
        {
            String orderContext = queryUserOrders(userId, lowerMessage);
            if (!orderContext.isEmpty())
            {
                context.append("=== 订单信息 ===\n").append(orderContext).append("\n\n");
            }
        }
        
        // 查询店铺信息
        if (containsKeywords(lowerMessage, STORE_KEYWORDS))
        {
            String storeContext = queryStores(lowerMessage);
            if (!storeContext.isEmpty())
            {
                context.append("=== 店铺信息 ===\n").append(storeContext).append("\n\n");
            }
        }
        
        return context.toString();
    }
    
    /**
     * 检查消息是否包含关键词
     */
    private boolean containsKeywords(String message, String[] keywords)
    {
        for (String keyword : keywords)
        {
            if (message.contains(keyword))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 查询商品信息
     */
    private String queryProducts(String message)
    {
        try
        {
            ShopProduct query = new ShopProduct();
            query.setStatus("1"); // 只查询在售商品
            
            // 尝试从消息中提取商品名称（更智能的提取）
            String productName = extractProductName(message);
            if (productName != null && !productName.isEmpty() && productName.length() > 1)
            {
                query.setProductName(productName);
            }
            else
            {
                // 如果没有指定商品名称，查询推荐商品
                query.setIsRecommended("1");
            }
            
            // 查询商品列表
            List<ShopProduct> products = shopProductService.selectShopProductList(query);
            
            if (products == null || products.isEmpty())
            {
                // 如果指定了商品名称但没找到，返回提示
                if (productName != null && !productName.isEmpty())
                {
                    return String.format("抱歉，未找到名称为\"%s\"的商品。您可以尝试搜索其他商品名称，或浏览推荐商品。", productName);
                }
                return "目前没有推荐商品。";
            }
            
            // 限制返回数量（避免token过多）
            int maxResults = Math.min(5, products.size());
            StringBuilder result = new StringBuilder();
            
            if (productName != null && !productName.isEmpty())
            {
                result.append(String.format("找到%d个名称为\"%s\"的商品（显示前%d个）：\n\n", products.size(), productName, maxResults));
            }
            else
            {
                result.append(String.format("为您推荐%d个热门商品（显示前%d个）：\n\n", products.size(), maxResults));
            }
            
            for (int i = 0; i < maxResults; i++)
            {
                ShopProduct product = products.get(i);
                result.append(String.format("%d. 商品名称: %s", i + 1, product.getProductName()));
                if (product.getProductSubtitle() != null && !product.getProductSubtitle().isEmpty())
                {
                    result.append(String.format(" (%s)", product.getProductSubtitle()));
                }
                result.append("\n");
                result.append(String.format("   价格: ¥%.2f | 库存: %d件 | 销量: %d件", 
                    product.getPrice(), 
                    product.getStock(), 
                    product.getSalesCount() != null ? product.getSalesCount() : 0));
                if (product.getStoreName() != null)
                {
                    result.append(String.format(" | 店铺: %s", product.getStoreName()));
                }
                result.append("\n");
            }
            
            if (products.size() > maxResults)
            {
                result.append(String.format("\n(还有%d个商品未显示，如需查看请提供更具体的商品名称)\n", products.size() - maxResults));
            }
            
            return result.toString();
        }
        catch (Exception e)
        {
            log.error("查询商品信息失败", e);
            return "";
        }
    }
    
    /**
     * 查询用户订单信息
     */
    private String queryUserOrders(Long userId, String message)
    {
        try
        {
            ShopOrder query = new ShopOrder();
            query.setUserId(userId); // 只查询当前用户的订单
            
            // 尝试从消息中提取订单号
            String orderNo = extractOrderNo(message);
            if (orderNo != null && !orderNo.isEmpty())
            {
                query.setOrderNo(orderNo);
            }
            
            // 查询订单列表
            List<ShopOrder> orders = shopOrderService.selectShopOrderList(query);
            
            if (orders == null || orders.isEmpty())
            {
                return "您目前没有订单记录。";
            }
            
            // 限制返回数量
            int maxResults = Math.min(5, orders.size());
            StringBuilder result = new StringBuilder();
            
            result.append(String.format("找到%d个订单（显示最近%d个）：\n\n", orders.size(), maxResults));
            
            for (int i = 0; i < maxResults; i++)
            {
                ShopOrder order = orders.get(i);
                result.append(String.format("%d. 订单号: %s\n", i + 1, order.getOrderNo()));
                result.append(String.format("   状态: %s | 金额: ¥%.2f", 
                    getOrderStatusText(order.getStatus()), 
                    order.getPaymentAmount()));
                if (order.getStoreName() != null)
                {
                    result.append(String.format(" | 店铺: %s", order.getStoreName()));
                }
                if (order.getCreateTime() != null)
                {
                    result.append(String.format(" | 下单时间: %s", order.getCreateTime()));
                }
                result.append("\n");
            }
            
            if (orders.size() > maxResults)
            {
                result.append(String.format("\n(还有%d个订单未显示)\n", orders.size() - maxResults));
            }
            
            return result.toString();
        }
        catch (Exception e)
        {
            log.error("查询订单信息失败", e);
            return "";
        }
    }
    
    /**
     * 查询店铺信息
     */
    private String queryStores(String message)
    {
        try
        {
            ShopStore query = new ShopStore();
            
            // 尝试从消息中提取店铺名称
            String storeName = extractStoreName(message);
            if (storeName != null && !storeName.isEmpty())
            {
                query.setStoreName(storeName);
            }
            
            // 查询店铺列表（最多返回5个）
            List<ShopStore> stores = shopStoreService.selectShopStoreList(query);
            
            if (stores == null || stores.isEmpty())
            {
                return "";
            }
            
            // 限制返回数量
            int maxResults = Math.min(5, stores.size());
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < maxResults; i++)
            {
                ShopStore store = stores.get(i);
                result.append(String.format("店铺ID: %d\n", store.getStoreId()));
                result.append(String.format("店铺名称: %s\n", store.getStoreName()));
                if (store.getStoreDescription() != null)
                {
                    result.append(String.format("店铺描述: %s\n", store.getStoreDescription()));
                }
                result.append(String.format("店铺状态: %s\n", getStoreStatusText(store.getStatus())));
                if (store.getCreditScore() != null)
                {
                    result.append(String.format("信用评分: %d\n", store.getCreditScore()));
                }
                result.append("\n");
            }
            
            return result.toString();
        }
        catch (Exception e)
        {
            log.error("查询店铺信息失败", e);
            return "";
        }
    }
    
    /**
     * 从消息中提取商品名称
     */
    private String extractProductName(String message)
    {
        // 更智能的商品名称提取
        // 匹配"商品名"、"产品名"、"什么商品"等模式
        Pattern pattern1 = Pattern.compile("(?:商品|产品|物品|东西|货)[:：]?([^，,。.！!？?\\s]{2,20})");
        Matcher matcher1 = pattern1.matcher(message);
        if (matcher1.find())
        {
            String name = matcher1.group(1).trim();
            if (name.length() >= 2 && name.length() <= 20)
            {
                return name;
            }
        }
        
        // 匹配"有没有XX"、"XX多少钱"等模式
        Pattern pattern2 = Pattern.compile("(?:有没有|查询|查找|搜索|找|看看|看看|了解)([^，,。.！!？?\\s]{2,20})(?:商品|产品|物品|东西|货|吗|？|\\?)");
        Matcher matcher2 = pattern2.matcher(message);
        if (matcher2.find())
        {
            String name = matcher2.group(1).trim();
            if (name.length() >= 2 && name.length() <= 20)
            {
                return name;
            }
        }
        
        // 匹配"XX多少钱"、"XX价格"等模式
        Pattern pattern3 = Pattern.compile("([^，,。.！!？?\\s]{2,20})(?:多少钱|价格|售价|卖多少|多少元|多少块)");
        Matcher matcher3 = pattern3.matcher(message);
        if (matcher3.find())
        {
            String name = matcher3.group(1).trim();
            if (name.length() >= 2 && name.length() <= 20)
            {
                return name;
            }
        }
        
        // 如果没有匹配到，返回null，让查询返回热门/推荐商品
        return null;
    }
    
    /**
     * 从消息中提取订单号
     */
    private String extractOrderNo(String message)
    {
        // 匹配订单号格式（通常是数字或字母数字组合）
        Pattern pattern = Pattern.compile("(?:订单号|订单)[:：]?([A-Za-z0-9]{6,})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * 从消息中提取店铺名称
     */
    private String extractStoreName(String message)
    {
        Pattern pattern = Pattern.compile("(?:店铺|商店|商家)[:：]?([^，,。.！!？?\\s]+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status)
    {
        if (status == null)
        {
            return "未知";
        }
        switch (status)
        {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已关闭";
            case 5: return "已取消";
            default: return "未知";
        }
    }
    
    /**
     * 获取店铺状态文本
     */
    private String getStoreStatusText(String status)
    {
        if (status == null)
        {
            return "未知";
        }
        switch (status)
        {
            case "0": return "待审核";
            case "1": return "正常营业";
            case "2": return "已关闭";
            default: return "未知";
        }
    }
}

