package com.hins.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-09-14
 */
@RestController
public class HelloWorld {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    @GetMapping("/hello")
    public Object hello(){

        logger.info("INFO LOGGING");

        return "Hello world!";
    }

}
