package com.whut.ylogin.auth.feign;

import com.whut.ylogin.common.to.SocialUserTo;
import com.whut.ylogin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient("ylogin-user")
public interface UserFeignService {
    @PostMapping("/user/user/oauth/login")
    R socialLogin(@RequestBody SocialUserTo socialUserTo) throws Exception;
}
