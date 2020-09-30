package com.hins.service;

import com.hins.pojo.UserAddress;
import com.hins.pojo.bo.AddressBO;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
public interface AddressService {

    /**
     * 根据用户id查询用户的收货地址列表
     * @param userId
     * @return
     */
    List<UserAddress> queryAll(String userId);

    /**
     * 新增用户收货地址
     * @param addressBO
     */
    public void addNewAddress(AddressBO addressBO);

    /**
     * 修改用户收货地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 删除用户收货地址
     */
    public void deleteUserAddress(String userId,String addressId);

    /**
     * 设置用户默认收货地址
     */
    public void updateUserAddressToBeDefault(String userId,String addressId);

    /**
     * 通过用户Id 和 地址Id获得用户收货地址
     * @return
     */
    public UserAddress queryByUserIdAndAddressId(String userId, String addressId);

}
