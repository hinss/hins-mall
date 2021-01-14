
package com.hins.config;

import com.hins.pojo.vo.ItemSearchVO;
import com.hins.utils.JSONResult;
import com.hins.utils.PagedGridResult;
import com.hins.utils.RedisOperator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;


@ControllerAdvice
public class XlsxAdvice implements ResponseBodyAdvice<Object> {

    private static Logger logger = LoggerFactory.getLogger(XlsxAdvice.class);

    @Autowired
    private RedisOperator redisOperator;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 返回为空 或者不是JSONResult的对象都不处理
        if (body == null || !(body instanceof JSONResult)) {
            return body;
        }

        URI uri = request.getURI();
        System.out.println("$XlsxAdvice.beforeBodyWrite print request url: " + uri.getPath());

        JSONResult jsonResult = (JSONResult)body;

        // 拿出的对象不是 PagedGridResult 不处理
        Object data = jsonResult.getData();
        if(!(data instanceof PagedGridResult)){
            return body;
        }

        PagedGridResult gridResult = (PagedGridResult)data;

        // 这里的List的类型是不知道的，真实项目中需要从redis中获得前端传的对象类型
        List<ItemSearchVO> rows = (List<ItemSearchVO>)gridResult.getRows();

        // 这里演示写死商品列表的场景
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


        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + "test.xls");
        // 告诉浏览器返回的是 二进制数据流的返回类型
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        OutputStream output = null;
        try {
            output = response.getBody();

            workbook.write(output);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                output.close();
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return body;
    }


}
