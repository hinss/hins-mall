package com.hins.exception;

import com.hins.utils.JSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 上传文件超过500K,捕获异常: MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JSONResult handlerMaxUploadSizeExceededException(MaxUploadSizeExceededException ex){
        return JSONResult.errorMsg("文件上传大小不能超过500K,请压缩图片或者降低图片质量再上传!");
    }
}
