package com.hins.mapper;

import com.hins.pojo.vo.ItemSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {


    public List<ItemSearchVO> searchItems(@Param("paramMap")Map<String, Object> map);
}