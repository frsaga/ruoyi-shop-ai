package com.ruoyi.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.shop.domain.ShopAddress;

/**
 * 收货地址Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
public interface ShopAddressMapper 
{
    /**
     * 查询收货地址
     * 
     * @param addressId 收货地址主键
     * @return 收货地址
     */
    public ShopAddress selectShopAddressByAddressId(Long addressId);

    /**
     * 查询用户收货地址列表
     * 
     * @param userId 用户ID
     * @return 收货地址集合
     */
    public List<ShopAddress> selectShopAddressListByUserId(Long userId);

    /**
     * 新增收货地址
     * 
     * @param shopAddress 收货地址
     * @return 结果
     */
    public int insertShopAddress(ShopAddress shopAddress);

    /**
     * 修改收货地址
     * 
     * @param shopAddress 收货地址
     * @return 结果
     */
    public int updateShopAddress(ShopAddress shopAddress);

    /**
     * 删除收货地址
     * 
     * @param addressId 收货地址主键
     * @return 结果
     */
    public int deleteShopAddressByAddressId(Long addressId);

    /**
     * 批量删除收货地址
     * 
     * @param addressIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopAddressByAddressIds(Long[] addressIds);

    /**
     * 取消用户的默认地址
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int cancelDefaultAddress(Long userId);
}

