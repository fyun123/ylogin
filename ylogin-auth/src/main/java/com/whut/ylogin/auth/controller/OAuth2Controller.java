package com.whut.ylogin.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.whut.ylogin.auth.feign.UserFeignService;
import com.whut.ylogin.common.vo.UserResponseVo;
import com.whut.ylogin.common.constant.AuthServerConstant;
import com.whut.ylogin.common.to.SocialUserTo;
import com.whut.ylogin.common.utils.HttpUtils;
import com.whut.ylogin.common.utils.R;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 处理社交登陆请求
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code,@RequestParam("state") String redirectURL, HttpServletResponse response) throws Exception {

        // 通过Authorization Code获取Access Token
        Map<String, String> map = new HashMap<>();
        map.put("grant_type","authorization_code");
        map.put("client_id",""); //app id
        map.put("client_secret",""); //app key
        map.put("code",code);
        map.put("redirect_uri","http://auth.ylogin.com/oauth2.0/weibo/success");
        HttpResponse res = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post",new HashMap<String, String>(), map, (byte[]) null);
        // 处理
        if (res.getStatusLine().getStatusCode() == 200){
            // 获取Access Token
            String json = EntityUtils.toString(res.getEntity());
            SocialUserTo socialUserTo = JSON.parseObject(json, SocialUserTo.class);
            // 1. 第一次登录进行自动注册
            R r = userFeignService.socialLogin(socialUserTo);
            if (r.getCode() == 0) {
                UserResponseVo data = r.getData(new TypeReference<UserResponseVo>() {
                });
                log.info("登录成功!用户信息:{}",data.toString());
//                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
                String token = UUID.randomUUID().toString().replace("-", "");
                redisTemplate.opsForValue().set(token, JSON.toJSONString(data),2, TimeUnit.MINUTES);
                Cookie cookie = new Cookie("sso_token", token);
                cookie.setPath("/");//设置cookie路径为"/"，否则会被过滤
                response.addCookie(cookie);
                return "redirect:"+redirectURL+"?token="+token;
            } else {
                return "redirect:http://auth.ylogin.com/login.html?redirectURL="+redirectURL;
            }
        } else {
            return "redirect:http://auth.ylogin.com/login.html?redirectURL="+redirectURL;
        }
    }
}
