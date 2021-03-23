package com.whut.ylogin.user;

import com.whut.ylogin.user.entity.UserEntity;
import com.whut.ylogin.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootTest
class YloginUserApplicationTests {
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        UserEntity byId = userService.getById(1);
        System.out.println(byId);
    }

}
