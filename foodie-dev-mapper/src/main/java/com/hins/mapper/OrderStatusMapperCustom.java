package com.hins.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface OrderStatusMapperCustom{

    Integer queryStatusCounts(@Param("paramMap")Map<String,Object> map);
}