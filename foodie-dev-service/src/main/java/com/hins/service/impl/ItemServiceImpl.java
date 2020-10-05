package com.hins.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hins.enums.ItemsCommentLevelEnum;
import com.hins.enums.YesOrNo;
import com.hins.mapper.*;
import com.hins.pojo.*;
import com.hins.pojo.vo.*;
import com.hins.service.BaseService;
import com.hins.service.ItemService;
import com.hins.utils.DesensitizationUtil;
import com.hins.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-23
 */
@Service
public class ItemServiceImpl extends BaseService implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemInfoVO getItemInfoVoByItemId(String itemId) {

        Items items = itemsMapper.selectByPrimaryKey(itemId);

        Example example = new Example(ItemsImg.class);
        example.createCriteria().andEqualTo("itemId", itemId);

        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);

        Example itemSpecExample = new Example(ItemsSpec.class);
        itemSpecExample.createCriteria().andEqualTo("itemId", itemId);

        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(itemSpecExample);

        Example itemsParamExample = new Example(ItemsParam.class);
        itemsParamExample.createCriteria().andEqualTo("itemId", itemId);

        ItemsParam itemsParam = itemsParamMapper.selectOneByExample(itemsParamExample);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(items);
        itemInfoVO.setItemImgList(itemsImgs);
        itemInfoVO.setItemSpecList(itemsSpecs);
        itemInfoVO.setItemParams(itemsParam);

        return itemInfoVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemCommentCountVO getCommentLevel(String itemId){

        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        itemsComments.setCommentLevel(ItemsCommentLevelEnum.GOOD.type);

        int goodCounts = itemsCommentsMapper.selectCount(itemsComments);

        itemsComments.setCommentLevel(ItemsCommentLevelEnum.NORMAL.type);
        int normalCounts = itemsCommentsMapper.selectCount(itemsComments);

        itemsComments.setCommentLevel(ItemsCommentLevelEnum.BAD.type);
        int badCounts = itemsCommentsMapper.selectCount(itemsComments);

        Integer totalCounts = goodCounts + normalCounts + badCounts;

        ItemCommentCountVO itemCommentCountVO = new ItemCommentCountVO();
        itemCommentCountVO.setGoodCounts(goodCounts);
        itemCommentCountVO.setNormalCounts(normalCounts);
        itemCommentCountVO.setBadCounts(badCounts);
        itemCommentCountVO.setTotalCounts(totalCounts);

        return itemCommentCountVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getItemCommentList(String itemId, Integer level, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);

        if(level != null){
            map.put("level", level);
        }

        // 开启分页
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> itemCommentVOS = itemsCommentsMapperCustom.selectItemComments(map);
        itemCommentVOS.stream().forEach(f-> f.setNickname(DesensitizationUtil.commonDisplay(f.getNickname())));

        return getPagedGridResult(itemCommentVOS, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getSearchItemList(String keyword, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);

        List<ItemSearchVO> itemSearchVOS = itemsMapperCustom.searchItems(map);

        return getPagedGridResult(itemSearchVOS, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getSearchItemList(Integer thirdCatId, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("thirdCatId", thirdCatId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);

        List<ItemSearchVO> itemSearchVOS = itemsMapperCustom.searchItemsByThirdCatId(map);

        return getPagedGridResult(itemSearchVOS, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {

        String ids[] = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryByItemSpecId(String itemSpecId) {
        return itemsSpecMapper.selectByPrimaryKey(itemSpecId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemNameByItemId(String itemId) {
        Items items = itemsMapper.selectByPrimaryKey(itemId);
        return items != null ? items.getItemName() : "";
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemImgByItemId(String itemId) {

        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);

        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemStock(String itemSpecId, int stockCounts) {

        int res = itemsMapperCustom.decreaseItemStock(itemSpecId, stockCounts);
        if(res != 1){
            throw new RuntimeException("创建订单失败,该商品规格库存不足!");
        }
    }
}
