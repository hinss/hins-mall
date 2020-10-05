package com.hins.service.center;

import com.hins.pojo.Orders;
import com.hins.pojo.vo.MyOrderStatusCountsVO;
import com.hins.utils.PagedGridResult;

public interface MyOrderService {

    public PagedGridResult queryMyOrderList(String userId, Integer orderStatus, Integer page, Integer pageSize);

    public void updateDeliverOrderStatus(String orderId);

    public boolean updateReceiveOrderStatus(String orderId);

    public boolean deleteOrder(String userId, String orderId);

    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 查询用户订单状态数量
     */
    public MyOrderStatusCountsVO queryOrderStatusCounts(String userId);

    /**
     * 查询用户订单状态数量
     */
    public PagedGridResult queryUserOrderTrend(String userId, Integer page, Integer pageSize);


}
