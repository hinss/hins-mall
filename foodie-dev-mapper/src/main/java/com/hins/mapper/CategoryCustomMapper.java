package com.hins.mapper;

import com.hins.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryCustomMapper {

    List<CategoryVO> getByRootCatId(Integer rooCatId);


}