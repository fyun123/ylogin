package com.whut.ylogin.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class YloginUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(YloginUserApplication.class, args);
    }

}
