package com.hins.service;

import com.hins.pojo.Category;
import com.hins.pojo.vo.CategoryVO;
import com.hins.pojo.vo.RootCatItemsVO;

import java.util.List;

public interface CategoryService {

    /**
     * 获取所有一级分类
     */
    List<Category> getRootCatList();


    /**
     * 根据一级分类获得所有下级分类
     */
    List<CategoryVO> getCatByRootCatId(Integer rootCatId);

    /**
     * 根据一级分类获得推荐的最新6分商品
     * @param rootCatId
     * @return
     */
    List<RootCatItemsVO> getRootCatItemsVO(Integer rootCatId);
}
