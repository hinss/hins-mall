package com.hins.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hins.enums.OrderStatusEnum;
import com.hins.enums.YesOrNo;
import com.hins.mapper.OrderStatusMapper;
import com.hins.mapper.OrderStatusMapperCustom;
import com.hins.mapper.OrdersMapper;
import com.hins.mapper.OrdersMapperCustom;
import com.hins.pojo.OrderStatus;
import com.hins.pojo.Orders;
import com.hins.pojo.vo.MyOrderStatusCountsVO;
import com.hins.pojo.vo.MyOrderVO;
import com.hins.service.BaseService;
import com.hins.service.center.MyOrderService;
import com.hins.utils.DateUtil;
import com.hins.utils.PagedGridResult;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-04
 */
@Service
public class MyOrderServiceImpl extends BaseService implements MyOrderService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapperCustom orderStatusMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrderList(String userId, Integer orderStatus,
                                            Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", userId);
        map.put("orderStatus", orderStatus);

        PageHelper.startPage(page, pageSize);

        List<MyOrderVO> myOrderVOS = ordersMapperCustom.queryMyOrderVOList(map);

        PagedGridResult pagedGridResult = getPagedGridResult(myOrderVOS, page);

        return pagedGridResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {

        OrderStatus updateOrderStatus = new OrderStatus();
        updateOrderStatus.setOrderId(orderId);
        updateOrderStatus.setDeliverTime(new Date());
        updateOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);

        orderStatusMapper.updateByExampleSelective(updateOrderStatus, example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);

        int res = orderStatusMapper.updateByExampleSelective(orderStatus, example);
        return res == 1 ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String userId, String orderId) {

        Orders updateOrders = new Orders();
        updateOrders.setUserId(userId);
        updateOrders.setId(orderId);
        updateOrders.setIsDelete(YesOrNo.YES.type);

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        int res = ordersMapper.updateByExampleSelective(updateOrders, example);

        return res == 1 ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(orderId);
        orders.setIsDelete(YesOrNo.NO.type);

        return ordersMapper.selectOne(orders);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public MyOrderStatusCountsVO queryOrderStatusCounts(String userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);

        Integer waitPayCounts = orderStatusMapperCustom.queryStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        Integer waitDeliverCounts = orderStatusMapperCustom.queryStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        Integer waitReceiveCounts = orderStatusMapperCustom.queryStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);
        Integer waitCommentCounts = orderStatusMapperCustom.queryStatusCounts(map);

        MyOrderStatusCountsVO myOrderStatusCountsVO = new MyOrderStatusCountsVO();
        myOrderStatusCountsVO.setWaitCommentCounts(waitCommentCounts);
        myOrderStatusCountsVO.setWaitDeliverCounts(waitDeliverCounts);
        myOrderStatusCountsVO.setWaitPayCounts(waitPayCounts);
        myOrderStatusCountsVO.setWaitReceiveCounts(waitReceiveCounts);

        return myOrderStatusCountsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryUserOrderTrend(String userId, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);

        List<OrderStatus> myOrderTrend = ordersMapperCustom.getMyOrderTrend(map);

        return getPagedGridResult(myOrderTrend, page);
    }
}
