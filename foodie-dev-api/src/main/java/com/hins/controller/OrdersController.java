package com.hins.controller;

import com.hins.enums.OrderStatusEnum;
import com.hins.pojo.bo.ShopOrderBO;
import com.hins.pojo.bo.ShopcartBO;
import com.hins.service.OrderService;
import com.hins.utils.CookieUtils;
import com.hins.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 订单相关接口
 * @Author:Wyman
 * @Date: 2020-09-30
 */
@Api(value = "订单", tags = {"订单相关接口"})
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单", notes = "创建订单", httpMethod = "POST")
    @PostMapping("/create")
    public JSONResult create(@RequestBody ShopOrderBO shopOrderBO,
                             HttpServletRequest request,
                             HttpServletResponse response){

        //1. 创建订单
        orderService.createOrder(shopOrderBO);

        //2. 创建订单后，移除购物车中已结算（已提交）的商品
        // TODO  整合redis之后，完善购物车中的已结算商品清楚，并且同步到前端的cookie中
//         CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);
        //3. 向支付中心发送当前订单，用于保存支付中心的订单数据

        return JSONResult.ok();
    }


    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){

        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }






}
