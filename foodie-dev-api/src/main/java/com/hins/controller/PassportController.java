package com.hins.controller;

import com.hins.pojo.Users;
import com.hins.pojo.bo.ShopcartBO;
import com.hins.pojo.bo.UserBO;
import com.hins.pojo.vo.UsersVO;
import com.hins.service.UserService;
import com.hins.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-20
 */
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
        UsersVO usersVO = convertUsersVO(userResult);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO),true);

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

        UsersVO usersVO = convertUsersVO(userResult);
        CookieUtils.setCookie(request, response,
                "user", JsonUtils.objectToJson(usersVO), true);

        // 用户注册登录应该同步cookie 与redis中的购物车数据
        synchShopcartData(userResult.getId(), request, response);

        return JSONResult.ok(userResult);
    }

    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        // 清楚用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        //分布式会话中需要清除用户数据
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        // 用户退出登录,需要清空购物车
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);


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

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
    }
}
