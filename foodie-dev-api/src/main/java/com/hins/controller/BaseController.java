package com.hins.controller;

import com.hins.pojo.Orders;
import com.hins.pojo.Users;
import com.hins.pojo.vo.UsersVO;
import com.hins.service.center.MyOrderService;
import com.hins.utils.JSONResult;
import com.hins.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class BaseController {

    @Autowired
    public MyOrderService myOrderService;

    @Autowired
    private RedisOperator redisOperator;


    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                      -> 回调通知的url
    String payReturnUrl = "http://api.hinsmall.club:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";

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

    public UsersVO convertUsersVO(Users users){

        // 生成用户token，存入redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(), uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);

        return usersVO;
    }

}
