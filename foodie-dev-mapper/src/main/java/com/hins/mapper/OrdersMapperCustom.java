package com.hins.mapper;

import com.hins.pojo.OrderStatus;
import com.hins.pojo.vo.MyOrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {

    public List<MyOrderVO> queryMyOrderVOList(@Param("paramMap") Map<String,Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramMap") Map<String,Object> map);
}