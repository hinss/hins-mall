package com.hins.controller;

import com.hins.enums.YesOrNo;
import com.hins.pojo.Carousel;
import com.hins.pojo.Category;
import com.hins.pojo.vo.CategoryVO;
import com.hins.pojo.vo.RootCatItemsVO;
import com.hins.service.CarouselService;
import com.hins.service.CategoryService;
import com.hins.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel(){

        List<Carousel> indexCarouselList = carouselService.getIndexCarouselList(YesOrNo.YES.type);

        return JSONResult.ok(indexCarouselList);
    }

    /**
     * 首页分类展示需求:
     * 1. 第一次刷新主页查询大分类，渲染展示到主页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类 则不需要加载(懒加载)
     */
    @ApiOperation(value = "获取全部一级分类", notes = "获取全部一级分类", httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats(){

        List<Category> rootCatList = categoryService.getRootCatList();
        return JSONResult.ok(rootCatList);
    }

    @ApiOperation(value = "获取一级分类下所有分类", notes = "获取一级分类下所有分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(@PathVariable Integer rootCatId){

        if(rootCatId == null){
            return JSONResult.errorMsg("");
        }

        List<CategoryVO> catByRootCatId = categoryService.getCatByRootCatId(rootCatId);
        return JSONResult.ok(catByRootCatId);
    }

    @ApiOperation(value = "根据一级分类获取最新6个推荐商品", notes = "根据一级分类获取最新6个推荐商品", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(@PathVariable Integer rootCatId){

        if(rootCatId == null){
            return JSONResult.errorMsg("");
        }

        List<RootCatItemsVO> rootCatItemsVO = categoryService.getRootCatItemsVO(rootCatId);
        return JSONResult.ok(rootCatItemsVO);
    }

}
