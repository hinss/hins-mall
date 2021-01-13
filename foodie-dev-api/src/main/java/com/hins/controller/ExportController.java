package com.hins.controller;

import com.hins.pojo.vo.ItemSearchVO;
import com.hins.service.ItemService;
import com.hins.utils.JSONResult;
import com.hins.utils.PagedGridResult;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2021-01-13
 */
@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public void export(
            @ApiParam(name = "keywords", value = "搜索关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序规则", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页条目数", required = false)
            @RequestParam Integer pageSize,
            HttpServletResponse response) throws IOException {

        if(StringUtils.isBlank(keywords)){
            return ;
        }

        if(page == null){
            page = 1;
        }

        if(pageSize == null){
            pageSize = 10;
        }

        PagedGridResult gridResult = itemService.getSearchItemList(keywords, sort, page, pageSize);
        List<ItemSearchVO> rows = (List<ItemSearchVO>)gridResult.getRows();
        // 导出导浏览器直接下载的方式
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("test");

        for(int i = 0; i < rows.size(); i++){
            XSSFRow row = sheet.createRow(i);
            ItemSearchVO itemSearchVO = rows.get(i);

            XSSFCell itemIdCell = row.createCell(0);
            itemIdCell.setCellValue(itemSearchVO.getItemId());

            XSSFCell itemNameCell = row.createCell(1);
            itemNameCell.setCellValue(itemSearchVO.getItemName());

            XSSFCell countCell = row.createCell(2);
            countCell.setCellValue(itemSearchVO.getSellCounts());

            XSSFCell priceCell = row.createCell(3);
            priceCell.setCellValue(itemSearchVO.getPrice());

            XSSFCell urlCell = row.createCell(4);
            urlCell.setCellValue(itemSearchVO.getImgUrl());
        }

        // TODO 优化返回现在返回还会报错
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + new String("test".getBytes(),"iso-8859-1") + ".xls");
        response.setContentType("APPLICATION/OCTET-STREAM");

        workbook.write(output);

        output.close();

        return;
    }
}
