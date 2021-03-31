package com.whut.ylogin.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.whut.ylogin.common.constant.AuthServerConstant;
import com.whut.ylogin.common.utils.HttpUtils;
import com.whut.ylogin.common.vo.UserResponseVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ClientController {

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final Map<String, HttpSession> localSession = new HashMap<>();

    @GetMapping("/")
    public String index(HttpSession session, @RequestParam(value = "token",required = false) String token) throws Exception {
        if (!StringUtils.isEmpty(token)){
            Map<String,String> map = new HashMap<>();
            map.put("token",token);
            HttpResponse response = HttpUtils.doGet("http://auth.ylogin.com", "/loginUserInfo", "GET", new HashMap<String, String>(), map);
            String s = EntityUtils.toString(response.getEntity());
            if (!StringUtils.isEmpty(s)){
                UserResponseVo userResponseVo = JSON.parseObject(s, new TypeReference<UserResponseVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER,userResponseVo);
                localSession.put(token,session);
            }
        }
        UserResponseVo attribute = (UserResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            return "index";
        } else {
            // 由于域名不同，不能实现session共享
            session.setAttribute("msg","请先进行登录");
            return "redirect:http://auth.ylogin.com/login.html?redirectURL=http://ylogin.client1.com";
        }
    }

    @ResponseBody
    @GetMapping("/logout")
    public String logout(@RequestParam("token") String token){
        HttpSession session = localSession.get(token);
//        session.removeAttribute(AuthServerConstant.LOGIN_USER);
        session.invalidate();
        return "logout";
    }
}
