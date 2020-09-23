package com.hins.service.impl;

import com.hins.mapper.ItemsImgMapper;
import com.hins.mapper.ItemsMapper;
import com.hins.mapper.ItemsParamMapper;
import com.hins.mapper.ItemsSpecMapper;
import com.hins.pojo.Items;
import com.hins.pojo.ItemsImg;
import com.hins.pojo.ItemsParam;
import com.hins.pojo.ItemsSpec;
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
}
