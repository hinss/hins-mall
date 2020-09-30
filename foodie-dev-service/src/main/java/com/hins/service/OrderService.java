package com.hins.service;

import com.hins.pojo.bo.ShopOrderBO;

public interface OrderService {

    /**
     * 创建订单
     * @param shopOrderBO
     */
    public void createOrder(ShopOrderBO shopOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);
}
