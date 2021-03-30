package com.whut.ylogin.user;

import com.whut.ylogin.user.entity.UserEntity;
import com.whut.ylogin.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableDiscoveryClient
@SpringBootTest
class YloginUserApplicationTests {
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("ylogin");
        System.out.println(encode);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches("ylogin", "$2a$10$qWxidPBiUTfxDOppMeX6DO6DvV9h9woKDj/b4SkyO.ziKS0Qjhq1y");
        System.out.println(matches);
    }

}
