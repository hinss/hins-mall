package com.hins.service;

import com.github.pagehelper.PageInfo;
import com.hins.utils.PagedGridResult;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-05
 */
public class BaseService {

    public PagedGridResult getPagedGridResult(List<?> itemCommentVOS, Integer page){

        PageInfo<?> pageList = new PageInfo<>(itemCommentVOS);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(itemCommentVOS);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
