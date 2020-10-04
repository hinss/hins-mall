package com.hins.service.center;

import com.hins.pojo.Orders;
import com.hins.utils.PagedGridResult;

public interface MyOrderService {

    public PagedGridResult queryMyOrderList(String userId, Integer orderStatus, Integer page, Integer pageSize);

    public void updateDeliverOrderStatus(String orderId);

    public boolean updateReceiveOrderStatus(String orderId);

    public boolean deleteOrder(String userId, String orderId);

    public Orders queryMyOrder(String userId, String orderId);


}
