package com.hins.interceptor;

import com.hins.utils.JSONResult;
import com.hins.utils.JsonUtils;
import com.hins.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description: 用户token拦截器
 * @Author:Wyman
 * @Date: 2020-11-08
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求，在访问controller调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if(StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response, JSONResult.errorMsg("请登录"));
                return false;
            }else{

                if(!userToken.equalsIgnoreCase(uniqueToken)){
                    returnErrorResponse(response, JSONResult.errorMsg("账号在异地登录"));
                    return false;
                }
            }
        }else{
            returnErrorResponse(response, JSONResult.errorMsg("请登录"));
            return false;
        }
        /**
         * false: 请求被拦截，被驳回，验证出现问题
         * true: 请求再经过验证校验以后是OK的，是可以放行的
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response,
                                    JSONResult jsonResult){

        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(jsonResult).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 请求访问controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller之后，渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
