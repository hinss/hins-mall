package com.hins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.hins.mapper")
// 默认扫描com.hins 引入别的包要拓展component scan的范围
@ComponentScan(basePackages = {"com.hins", "org.n3r.idworker"})
//@EnableTransactionManagement
public class SSOApplication {


    public static void main(String[] args) {

        SpringApplication.run(SSOApplication.class, args);
    }
}
