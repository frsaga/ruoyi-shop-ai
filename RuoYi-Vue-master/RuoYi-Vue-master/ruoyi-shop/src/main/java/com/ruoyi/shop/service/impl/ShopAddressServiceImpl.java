package com.ruoyi.shop.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.shop.mapper.ShopAddressMapper;
import com.ruoyi.shop.domain.ShopAddress;
import com.ruoyi.shop.service.IShopAddressService;

/**
 * 收货地址Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-22
 */
@Service
public class ShopAddressServiceImpl implements IShopAddressService 
{
    @Autowired
    private ShopAddressMapper shopAddressMapper;

    /**
     * 查询收货地址
     * 
     * @param addressId 收货地址主键
     * @return 收货地址
     */
    @Override
    public ShopAddress selectShopAddressByAddressId(Long addressId)
    {
        return shopAddressMapper.selectShopAddressByAddressId(addressId);
    }

    /**
     * 查询用户收货地址列表
     * 
     * @param userId 用户ID
     * @return 收货地址
     */
    @Override
    public List<ShopAddress> selectShopAddressListByUserId(Long userId)
    {
        return shopAddressMapper.selectShopAddressListByUserId(userId);
    }

    /**
     * 新增收货地址
     * 
     * @param shopAddress 收货地址
     * @return 结果
     */
    @Override
    @Transactional
    public int insertShopAddress(ShopAddress shopAddress)
    {
        // 如果设置为默认地址，先取消其他默认地址
        if ("1".equals(shopAddress.getIsDefault())) {
            shopAddressMapper.cancelDefaultAddress(shopAddress.getUserId());
        }
        
        shopAddress.setDelFlag("0");
        shopAddress.setCreateTime(new Date());
        shopAddress.setUpdateTime(new Date());
        return shopAddressMapper.insertShopAddress(shopAddress);
    }

    /**
     * 修改收货地址
     * 
     * @param shopAddress 收货地址
     * @return 结果
     */
    @Override
    @Transactional
    public int updateShopAddress(ShopAddress shopAddress)
    {
        // 如果设置为默认地址，先取消其他默认地址
        if ("1".equals(shopAddress.getIsDefault())) {
            shopAddressMapper.cancelDefaultAddress(shopAddress.getUserId());
        }
        
        shopAddress.setUpdateTime(new Date());
        return shopAddressMapper.updateShopAddress(shopAddress);
    }

    /**
     * 批量删除收货地址
     * 
     * @param addressIds 需要删除的收货地址主键
     * @return 结果
     */
    @Override
    public int deleteShopAddressByAddressIds(Long[] addressIds)
    {
        return shopAddressMapper.deleteShopAddressByAddressIds(addressIds);
    }

    /**
     * 删除收货地址信息
     * 
     * @param addressId 收货地址主键
     * @return 结果
     */
    @Override
    public int deleteShopAddressByAddressId(Long addressId)
    {
        return shopAddressMapper.deleteShopAddressByAddressId(addressId);
    }

    /**
     * 设置默认地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int setDefaultAddress(Long addressId, Long userId)
    {
        // 先取消所有默认地址
        shopAddressMapper.cancelDefaultAddress(userId);
        
        // 设置指定地址为默认
        ShopAddress address = new ShopAddress();
        address.setAddressId(addressId);
        address.setIsDefault("1");
        address.setUpdateTime(new Date());
        return shopAddressMapper.updateShopAddress(address);
    }
}

