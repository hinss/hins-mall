package com.hins.service;

import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemCommentVO;
import com.hins.pojo.vo.ItemInfoVO;
import com.hins.pojo.vo.ShopcartVO;
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
    public PagedGridResult getItemCommentList(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 根据keyword 搜索商品
     */
    public PagedGridResult getSearchItemList(String keyword, String sort, Integer page, Integer pageSize);

    /**
     * 根据三级分类id 搜索商品
     */
    public PagedGridResult getSearchItemList(Integer thirdCatId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ids查询最新的购物车中商品数据（用于刷新渲染购物车中的商品数据）
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemsBySpecIds(String specIds);


}
