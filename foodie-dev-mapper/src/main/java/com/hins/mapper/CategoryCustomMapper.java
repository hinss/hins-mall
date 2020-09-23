package com.hins.mapper;

import com.hins.pojo.vo.CategoryVO;
import com.hins.pojo.vo.RootCatItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryCustomMapper {

    List<CategoryVO> getByRootCatId(Integer rootCatId);

    List<RootCatItemsVO> getRootCatItemsList(@Param("paramsMap") Map<String, Object> map);

}