package com.hins.controller;

import com.hins.enums.YesOrNo;
import com.hins.pojo.Carousel;
import com.hins.pojo.vo.ItemCommentCountVO;
import com.hins.pojo.vo.ItemInfoVO;
import com.hins.service.ItemService;
import com.hins.utils.JSONResult;
import com.hins.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 商品页接口controller
 * @Author:Wyman
 * @Date: 2020-09-23
 */
@Api(value = "商品页", tags = {"商品页的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController{

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "根据商品id获得商品详情", notes = "根据商品id获得商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JSONResult info(@PathVariable String itemId){

        if(StringUtils.isBlank(itemId)){
            return JSONResult.errorMsg(null);
        }

        ItemInfoVO itemInfoVo = itemService.getItemInfoVoByItemId(itemId);

        return JSONResult.ok(itemInfoVo);
    }

    @ApiOperation(value = "根据商品id获得商品评价等级", notes = "根据商品id获得商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JSONResult commentLevel(@RequestParam String itemId){

        if(StringUtils.isBlank(itemId)){
            return JSONResult.errorMsg(null);
        }

        ItemCommentCountVO itemCommentCountVO = itemService.getCommentLevel(itemId);

        return JSONResult.ok(itemCommentCountVO);
    }

    @ApiOperation(value = "根据商品id获得商品评价列表", notes = "根据商品id获得商品评价列表", httpMethod = "GET")
    @GetMapping("/comments")
    public JSONResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "评论等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页条目数", required = false)
            @RequestParam Integer pageSize){

        if(StringUtils.isBlank(itemId)){
            return JSONResult.errorMsg(null);
        }

        if(page == null){
            page = 1;
        }

        if(pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult gridResult = itemService.getItemCommentList(itemId, level, page, pageSize);

        return JSONResult.ok(gridResult);
    }

}
