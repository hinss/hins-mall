package com.hins.service;

import com.hins.pojo.OrderStatus;
import com.hins.pojo.bo.ShopOrderBO;
import com.hins.pojo.vo.OrderVO;

public interface OrderService {

    /**
     * 创建订单
     * @param shopOrderBO
     */
    public OrderVO createOrder(ShopOrderBO shopOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatus(String orderId);
}
