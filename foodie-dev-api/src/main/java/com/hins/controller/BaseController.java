package com.hins.controller;

import com.hins.pojo.Orders;
import com.hins.service.center.MyOrderService;
import com.hins.utils.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

    @Autowired
    public MyOrderService myOrderService;


    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                      -> 回调通知的url
    String payReturnUrl = "http://75q4k4.natappfree.cc/foodie-dev-api/orders/notifyMerchantOrderPaid";

    // 支付中心创建商户订单url
    String paymentCenterUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";


    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public JSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrderService.queryMyOrder(userId, orderId);
        if (order == null) {
            return JSONResult.errorMsg("订单不存在！");
        }
        return JSONResult.ok(order);
    }

}
