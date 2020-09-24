package com.hins.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hins.enums.ItemsCommentLevelEnum;
import com.hins.mapper.*;
import com.hins.pojo.*;
import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemCommentVO;
import com.hins.pojo.vo.ItemInfoVO;
import com.hins.service.ItemService;
import com.hins.utils.DesensitizationUtil;
import com.hins.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

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
        itemCommentVOS.stream().forEach(f-> DesensitizationUtil.commonDisplay(f.getNickName()));

        return getPagedGridResult(itemCommentVOS, page);
    }

    private PagedGridResult getPagedGridResult(List<ItemCommentVO> itemCommentVOS, Integer page){

        PageInfo<?> pageList = new PageInfo<>(itemCommentVOS);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(itemCommentVOS);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
