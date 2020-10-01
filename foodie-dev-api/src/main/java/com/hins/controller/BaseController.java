package com.hins.controller;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                      -> 回调通知的url
    String payReturnUrl = "http://t7uxhc.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 支付中心创建商户订单url
    String paymentCenterUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

}
