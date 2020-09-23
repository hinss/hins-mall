package com.hins.service;

import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemInfoVO;

public interface ItemService {

    /**
     * 根据商品id获得商品详情
     */
    public ItemInfoVO getItemInfoVoByItemId(String itemId);

    /**
     * 根据商品id 获得商品评价数量
     * @param itemId
     * @return
     */
    public ItemCommentCountVO getCommentLevel(String itemId);

}
