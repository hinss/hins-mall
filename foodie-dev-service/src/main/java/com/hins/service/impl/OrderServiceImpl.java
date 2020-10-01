package com.hins.service.impl;

import com.hins.enums.OrderStatusEnum;
import com.hins.enums.YesOrNo;
import com.hins.mapper.OrderItemsMapper;
import com.hins.mapper.OrderStatusMapper;
import com.hins.mapper.OrdersMapper;
import com.hins.pojo.*;
import com.hins.pojo.bo.ShopOrderBO;
import com.hins.pojo.vo.MerchantOrderVO;
import com.hins.pojo.vo.OrderVO;
import com.hins.service.AddressService;
import com.hins.service.ItemService;
import com.hins.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-21
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(ShopOrderBO shopOrderBO) {

        String userId = shopOrderBO.getUserId();
        String addressId = shopOrderBO.getAddressId();
        String itemSpecIds = shopOrderBO.getItemSpecIds();
        String leftMsg = shopOrderBO.getLeftMsg();
        Integer payMethod = shopOrderBO.getPayMethod();
        // 默认订单邮费为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();

        UserAddress userAddress = addressService.queryByUserIdAndAddressId(userId, addressId);

        // 1.新增订单详情
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);
        newOrder.setReceiverName(userAddress.getReceiver());
        newOrder.setReceiverMobile(userAddress.getMobile());
        newOrder.setReceiverAddress(userAddress.getProvince() + " " + userAddress.getCity() + " " + userAddress.getDistrict() + " " + userAddress.getDetail());
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setPayMethod(payMethod);
        newOrder.setPostAmount(postAmount);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        // 2.新增订单商品
        Integer totalAmount = 0;
        Integer realPayAmount = 0;
        String[] itemSpecs = itemSpecIds.split(",");
        for(String itemSpecId : itemSpecs){

            ItemsSpec itemsSpec = itemService.queryByItemSpecId(itemSpecId);
            String itemId = itemsSpec.getItemId();
            // TODO 商品购买数量先默认为1 实际应该从redis中用户购物车中得到
            Integer buyCounts = 1;

            String itemName = itemService.queryItemNameByItemId(itemId);
            String imgUrl = itemService.queryItemImgByItemId(itemId);

            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            String orderItemId = sid.nextShort();
            OrderItems orderItem = new OrderItems();
            orderItem.setId(orderItemId);
            orderItem.setOrderId(orderId);
            orderItem.setItemId(itemId);
            orderItem.setItemImg(imgUrl);
            orderItem.setItemName(itemName);
            orderItem.setItemSpecId(itemSpecId);
            orderItem.setItemSpecName(itemsSpec.getName());
            orderItem.setBuyCounts(buyCounts);
            orderItem.setPrice(itemsSpec.getPriceNormal());

            orderItemsMapper.insert(orderItem);

            // 扣除库存
            itemService.decreaseItemStock(itemSpecId, buyCounts);
        }
        newOrder.setRealPayAmount(realPayAmount);
        newOrder.setTotalAmount(totalAmount);
        ordersMapper.insert(newOrder);

        // 3.新增订单状态
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrderVO merchantOrderVO = new MerchantOrderVO();
        merchantOrderVO.setMerchantOrderId(orderId);
        merchantOrderVO.setMerchantUserId(userId);
        merchantOrderVO.setAmount(realPayAmount + postAmount);
        merchantOrderVO.setPayMethod(payMethod);

        // 5. 构建OrderVO
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrderVO(merchantOrderVO);

        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        OrderStatus updateOrderStatus = new OrderStatus();
        updateOrderStatus.setOrderId(orderId);
        updateOrderStatus.setOrderStatus(orderStatus);
        updateOrderStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(updateOrderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatus(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }
}
