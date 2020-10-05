package com.hins.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.hins.enums.YesOrNo;
import com.hins.mapper.*;
import com.hins.pojo.OrderItems;
import com.hins.pojo.OrderStatus;
import com.hins.pojo.Orders;
import com.hins.pojo.bo.center.OrderItemsCommentBO;
import com.hins.pojo.vo.MyCommentVO;
import com.hins.service.BaseService;
import com.hins.service.center.MyCommentsService;
import com.hins.utils.PagedGridResult;
import org.n3r.idworker.Sid;
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
 * @Date:
 */
@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComments(String orderId) {

        Example example = new Example(OrderItems.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);

        return orderItemsMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentBOList) {

        // 1.保存评论
        for(OrderItemsCommentBO commentBO : commentBOList){

            String commentId = sid.nextShort();
            commentBO.setCommentId(commentId);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentBOList);
        itemsCommentsMapperCustom.saveCommets(map);

        // 2.修改订单已评论状态
        Orders updateOrder = new Orders();
        updateOrder.setId(orderId);
        updateOrder.setIsComment(YesOrNo.YES.type);

        ordersMapper.updateByPrimaryKeySelective(updateOrder);

        // 3.修改订单状态表的留言时间
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyCommentVOList(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<MyCommentVO> myCommentVOS = itemsCommentsMapperCustom.selectMyCommentVOs(userId);

        return getPagedGridResult(myCommentVOS, page);
    }
}
