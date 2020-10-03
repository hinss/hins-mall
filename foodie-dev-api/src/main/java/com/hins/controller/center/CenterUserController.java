package com.hins.controller.center;

import com.hins.pojo.Users;
import com.hins.pojo.bo.center.CenterUserBO;
import com.hins.resource.FileUploadResource;
import com.hins.service.center.CenterUserService;
import com.hins.utils.CookieUtils;
import com.hins.utils.DateUtil;
import com.hins.utils.JSONResult;
import com.hins.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
@Api(value = "userInfo - 用户详情", tags = {"用户详情的相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUploadResource fileUploadResource;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public JSONResult userInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response){

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if(result.hasErrors()){
            Map<String, String> errors = getErrors(result);
            return JSONResult.errorMap(errors);
        }

        Users userInfo = centerUserService.updateUserInfo(centerUserBO, userId);
        userInfo = setNullParam(userInfo);

        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(userInfo), true);

        // TODO 后续要改,增加令牌token,会整合进redis，分布式会话

        return JSONResult.ok();
    }

    @ApiOperation(value = "头像图片上传", notes = "头像图片上传", httpMethod = "POST")
    @PostMapping("uploadFace")
    public JSONResult userInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            MultipartFile file,
            HttpServletRequest request,HttpServletResponse response){

        // 1.获得文件服务器(以本机作为文件服务器)上传路径，区分本地、测试、生产环境
        String fileSpace = fileUploadResource.getImageUserFaceLocation();

        // 为每个上传的路径添加用户userId,用于区分不同的用户上传
        String uploadPathPrefix = File.separator + userId;

        // 开始上传文件
        if(file != null){
            FileOutputStream fileOutputStream = null;
            try {
                // 获得上传的文件名
                String uploadFileName = file.getOriginalFilename();

                String[] uploadFileNameArr = uploadFileName.split("\\.");

                String suffix = uploadFileNameArr[uploadFileNameArr.length - 1];

                // 校验图片格式
                if(!suffix.equalsIgnoreCase("png")
                        && !suffix.equalsIgnoreCase("jpg")
                        && !suffix.equalsIgnoreCase("jpeg")){
                    return JSONResult.errorMsg("图片格式不正确!");
                }

                // 文件服务器中头像图片保存格式 /userId/face-userId.png
                // 文件名称重组 覆盖式上传，增量式：额外拼接当前时间
                String userFacePath = File.separator + "face-" + userId + "." + suffix;
                String finalPath = fileSpace + uploadPathPrefix + userFacePath;

                // 拼接web服务访问资源的路径
                uploadPathPrefix += userFacePath;

                File outFile = new File(finalPath);
                if(outFile.getParentFile() != null){
                    // 创建文件夹
                    outFile.getParentFile().mkdirs();
                }

                fileOutputStream = new FileOutputStream(outFile);
                InputStream inputStream = file.getInputStream();
                // 具体上传文件的IO操作
                IOUtils.copy(inputStream, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(fileOutputStream != null){
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        } else {
            return JSONResult.errorMsg("上传文件不能为空!");
        }

        //获得文件服务地址
        String imageServerUrl = fileUploadResource.getImageServerUrl();

        // 由于浏览器可能存在缓存的情况，所以在这里，我们需要加上时间戳来保证更新后的图片可以及时刷新
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix
                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        // 更新头像到数据库
        Users userInfo = centerUserService.updateUserFace(userId, finalUserFaceUrl);
        userInfo = setNullParam(userInfo);

        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(userInfo), true);

        // TODO 后续要改,增加令牌token,会整合进redis，分布式会话

        return JSONResult.ok();
    }

    private Map<String, String> getErrors(BindingResult result){

        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for(FieldError error : fieldErrors){
            // 发生验证错误所对应的一个属性
            String field = error.getField();
            // 验证错误的信息
            String defaultMessage = error.getDefaultMessage();

            map.put(field, defaultMessage);
        }

        return map;
    }


    private Users setNullParam(Users userResult) {

        userResult.setRealname(null);
        userResult.setPassword(null);
        userResult.setBirthday(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);

        return userResult;
    }



}
