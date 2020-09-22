package com.hins.service.impl;

import com.hins.mapper.CategoryCustomMapper;
import com.hins.mapper.CategoryMapper;
import com.hins.pojo.Category;
import com.hins.pojo.vo.CategoryVO;
import com.hins.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-22
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryCustomMapper categoryCustomMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> getRootCatList(){

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 1);

        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getCatByRootCatId(Integer rootCatId){

        return categoryCustomMapper.getByRootCatId(rootCatId);
    }



}
