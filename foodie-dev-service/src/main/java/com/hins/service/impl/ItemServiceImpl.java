package com.hins.service.impl;

import com.hins.enums.ItemsCommentLevelEnum;
import com.hins.mapper.*;
import com.hins.pojo.*;
import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemInfoVO;
import com.hins.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-23
 */
@Service
public class ItemServiceImpl implements ItemService {

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

}
