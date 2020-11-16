package com.hins;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author: hins
 * @created: 2020-11-16 13:37
 * @desc:
 **/
@Configuration
public class ESConfig {


    /**
     * 解决netty引起的issue
     */
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


}
