package com.hins.service;

import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemCommentVO;
import com.hins.pojo.vo.ItemInfoVO;
import com.hins.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品id获得商品详情
     */
    public ItemInfoVO getItemInfoVoByItemId(String itemId);

    /**
     * 根据商品id 获得商品评价数量
     */
    public ItemCommentCountVO getCommentLevel(String itemId);

    /**
     * 根据商品id获得商品评价
     * @return
     */
    PagedGridResult getItemCommentList(String itemId, Integer level, Integer page, Integer pageSize);
}
