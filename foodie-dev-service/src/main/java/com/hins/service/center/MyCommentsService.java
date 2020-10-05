package com.hins.service.center;

import com.hins.pojo.OrderItems;
import com.hins.pojo.bo.center.OrderItemsCommentBO;
import com.hins.utils.PagedGridResult;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-05
 */
public interface MyCommentsService {

    /**
     * 查询待评价订单商品
     */
    List<OrderItems> queryPendingComments(String orderId);

    /**
     * 保存评论列表
     */
    void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentBOList);

    /**
     * 查询我的评论列表
     */
    PagedGridResult queryMyCommentVOList(String userId, Integer page, Integer pageSize);
}
