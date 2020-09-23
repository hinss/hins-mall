package com.hins.service;

import com.hins.pojo.vo.ItemInfoVO;

public interface ItemService {

    /**
     * 根据商品id获得商品详情
     */
    public ItemInfoVO getItemInfoVoByItemId(String itemId);

}
