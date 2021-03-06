package com.hins.controller.center;

import com.hins.controller.BaseController;
import com.hins.pojo.Orders;
import com.hins.pojo.vo.MyOrderStatusCountsVO;
import com.hins.service.center.MyOrderService;
import com.hins.utils.JSONResult;
import com.hins.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
@Api(value = "userInfo - 用户订单", tags = {"用户订单的相关接口"})
@RestController
@RequestMapping("myorders")
public class MyOrderController extends BaseController {

    @ApiOperation(value = "根据用户id获得订单列表", notes = "根据用户id获得订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public JSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页条目数", required = false)
            @RequestParam Integer pageSize){

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg(null);
        }

        if(page == null){
            page = 1;
        }

        if(pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult gridResult = myOrderService.queryMyOrderList(userId, orderStatus, page, pageSize);

        return JSONResult.ok(gridResult);
    }

    /**
     * 商家发货没有后端，所以这个接口仅仅只是用于模拟
     *
     */
    @ApiOperation(value="商家发货", notes="商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public JSONResult deliver(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) throws Exception {

        if (StringUtils.isBlank(orderId)) {
            return JSONResult.errorMsg("订单ID不能为空");
        }
        myOrderService.updateDeliverOrderStatus(orderId);
        return JSONResult.ok();
    }


    @ApiOperation(value="用户确认收货", notes="用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public JSONResult confirmReceive(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        JSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrderService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return JSONResult.errorMsg("订单确认收货失败！");
        }

        return JSONResult.ok();
    }

    @ApiOperation(value="用户删除订单", notes="用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public JSONResult delete(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        JSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrderService.deleteOrder(userId, orderId);
        if (!res) {
            return JSONResult.errorMsg("订单删除失败！");
        }

        return JSONResult.ok();
    }

    @ApiOperation(value="用户订单状态数量", notes="用户订单状态数量", httpMethod = "POST")
    @PostMapping("/statusCounts")
    public JSONResult statusCounts(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg(null);
        }

        MyOrderStatusCountsVO myOrderStatusCountsVO = myOrderService.queryOrderStatusCounts(userId);

        return JSONResult.ok(myOrderStatusCountsVO);
    }

    @ApiOperation(value="用户订单动向", notes="用户订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public JSONResult trend(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页条目数", required = false)
            @RequestParam Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult grid = myOrderService.queryUserOrderTrend(userId,
                page,
                pageSize);

        return JSONResult.ok(grid);

    }


}
