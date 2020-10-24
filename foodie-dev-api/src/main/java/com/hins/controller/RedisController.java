package com.hins.controller;

import com.hins.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: redis 测试类
 * @Author:Wyman
 * @Date: 2020-10-25
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public void set(String key, String value){

        redisOperator.set(key, value);
    }

    @GetMapping("/get")
    public String get(String key){

        return redisOperator.get(key);
    }

    @GetMapping("/del")
    public void del(String key){
        redisOperator.del(key);
    }


}
