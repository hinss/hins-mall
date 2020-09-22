package com.hins.controller;

import com.hins.pojo.Users;
import com.hins.pojo.bo.UserBO;
import com.hins.service.UserService;
import com.hins.utils.CookieUtils;
import com.hins.utils.JSONResult;
import com.hins.utils.JsonUtils;
import com.hins.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {

        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名不能为空");
        }

        // 2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if(isExist){
            return JSONResult.errorMsg("用户名已经存在");
        }

        // 3. 请求成功,用户名没有重复
        return JSONResult.ok();
    }

    @PostMapping("/regist")
    public JSONResult register(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 1.校验用户名 or 密码是否为空
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(confirmPassword)){
            return JSONResult.errorMsg("用户名或者密码不能为空");
        }

        // 2.校验用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if(isExist){
            return JSONResult.errorMsg("用户名已经存在");
        }

        // 3.校验密码是否小于6位
        if(password.length() < 6){
            return JSONResult.errorMsg("密码不能小于6位");
        }

        // 4.校验确认密码
        if(!password.equals(confirmPassword)){
            return JSONResult.errorMsg("确认密码与密码不匹配");
        }

        Users userResult = userService.createUser(userBO);
        userResult = setNullParam(userResult);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult));

        return JSONResult.ok();
    }

    @PostMapping("/login")
    public JSONResult login(@RequestBody UserBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 1.校验用户名 or 密码是否为空
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(password)){
            return JSONResult.errorMsg("用户名或者密码不能为空");
        }

        Users userResult = userService.userLogin(username, MD5Utils.getMD5Str(password));
        if(userResult == null){
            return JSONResult.errorMsg("用户名或密码输出错误");
        }

        userResult = setNullParam(userResult);

        CookieUtils.setCookie(request, response,
                "user", JsonUtils.objectToJson(userResult), true);

        return JSONResult.ok(userResult);
    }

    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        // 清楚用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录,需要清空购物车
        // TODO 分布式会话中需要清除用户数据

        return JSONResult.ok();
    }

    /**
     * 将不需要展示在cookie中返回的信息设置为null
     * @param userResult
     * @return
     */
    private Users setNullParam(Users userResult) {

        userResult.setRealname(null);
        userResult.setPassword(null);
        userResult.setBirthday(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);

        return userResult;
    }
}
