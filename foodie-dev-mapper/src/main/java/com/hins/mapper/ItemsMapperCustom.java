package com.hins.mapper;

import com.hins.pojo.vo.ItemSearchVO;
import com.hins.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {


    public List<ItemSearchVO> searchItems(@Param("paramMap")Map<String, Object> map);

    public List<ItemSearchVO> searchItemsByThirdCatId(@Param("paramMap")Map<String, Object> map);

    public List<ShopcartVO> queryItemsBySpecIds(@Param("paramList") List specIdsList);

    public int decreaseItemStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);
}