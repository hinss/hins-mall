package com.hins.controller;

import com.hins.pojo.Users;
import com.hins.pojo.vo.UsersVO;
import com.hins.service.UserService;
import com.hins.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Description: 单点登录controller
 * @Author:Wyman
 * @Date: 2020-11-08
 */
@Controller
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";

    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    /**
     * 各系统单点登录的入口
     *
     * @return
     */
    @GetMapping("/login")
    public String login(Model model,
                        String returnUrl,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);

        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified) {
            // 如果判断已经单点登陆过了 创建临时ticket 校验登录
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        // 这里返回的是 classpath/templates文件下视图前缀名
        return "login";

    }

    /**
     * 校验CAS全局用户门票
     *
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {

        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }

        // 1. 验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }

        return true;
    }

    /**
     * CAS的统一登录接口
     * 目的：
     * 1. 登录后创建用户的全局会话                 ->  uniqueToken
     * 2. 创建用户全局门票，用以表示在CAS端是否登录  ->  userTicket
     * 3. 创建用户的临时票据，用于回跳回传          ->  tmpTicket
     */
    @PostMapping("/doLogin")
    public String doLogin(Model model, String username, String password, String returnUrl,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 0.校验用户名 or 密码是否为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)) {

            model.addAttribute("errmsg", "用户名或者密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.userLogin(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码输出错误");
            return "login";
        }

        // 2.创建用户会话
        String uniqueToken = UUID.randomUUID().toString().trim();

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(),
                JsonUtils.objectToJson(usersVO));

        // 3. 生成ticket门票，全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        // 3.1 用户全局门票需要放入CAS端的cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        // 4. userTicket关联用户id，并且放入到redis中，代表这个用户有门票了，可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        // 5. 生成临时票据，回跳到调用端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();

        /**
         * userTicket: 用于表示用户在CAS端的一个登录状态：已经登录
         * tmpTicket: 用于颁发给用户进行一次性的验证的票据，有时效性
         */

        /**
         * 举例：
         *      我们去动物园玩耍，大门口买了一张统一的门票，这个就是CAS系统的全局门票和用户全局会话。
         *      动物园里有一些小的景点，需要凭你的门票去领取一次性的票据，有了这张票据以后就能去一些小的景点游玩了。
         *      这样的一个个的小景点其实就是我们这里所对应的一个个的站点。
         *      当我们使用完毕这张临时票据以后，就需要销毁。
         */

        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }


    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public JSONResult verifyTmpTicket(String tmpTicket, HttpServletRequest request) throws Exception {

        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String redisTmpTicket = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(redisTmpTicket)) {
            return JSONResult.errorTokenMsg("用户票据异常");
        }

        if (!redisTmpTicket.equalsIgnoreCase(MD5Utils.getMD5Str(tmpTicket))) {
            return JSONResult.errorTokenMsg("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);

        }

        // 校验成功
        // 1. 从CAS端cookid拿到用户userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        if (StringUtils.isBlank(userTicket)) {
            return JSONResult.errorTokenMsg("用户票据异常");
        }

        // 2. 并从redis会话中返回用户信息
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        String userInfo = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userInfo)) {
            return JSONResult.errorTokenMsg("用户票据异常");
        }
        UsersVO usersVO = JsonUtils.jsonToPojo(userInfo, UsersVO.class);

        return JSONResult.ok(usersVO);
    }


    private void setCookie(String key, String value, HttpServletResponse response) {

        Cookie cookie = new Cookie(key, value);
        // 设置 公共域sso.com
        cookie.setDomain("sso.com");
        // 设置路径
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    /**
     * 创建临时票据
     *
     * @return
     */
    private String createTmpTicket() {

        String tmpTicket = UUID.randomUUID().toString().trim();

        try {
            // 将临时票据 加密放入redis中 时间为600s 过期
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket,
                    MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tmpTicket;
    }

    private String getCookie(HttpServletRequest request, String key) {

        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (int i = 0; i < cookieList.length; i++) {
            if (cookieList[i].getName().equals(key)) {
                cookieValue = cookieList[i].getValue();
                break;
            }
        }

        return cookieValue;
    }

    @PostMapping("/logout")
    @ResponseBody
    public JSONResult logout(String userId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        // 0. 获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        // 1. 清除userTicket票据，redis/cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        // 2. 清除用户全局会话（分布式会话）
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        return JSONResult.ok();
    }

    private void deleteCookie(String key,
                              HttpServletResponse response) {

        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        // 设置过期
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
