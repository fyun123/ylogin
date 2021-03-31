package com.whut.ylogin.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.whut.ylogin.common.constant.AuthServerConstant;
import com.whut.ylogin.common.utils.HttpUtils;
import com.whut.ylogin.common.utils.R;
import com.whut.ylogin.common.vo.UserResponseVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.support.SimpleTriggerContext;
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

    private static final Map<String, String> sessionTokenMapping = new HashMap<>();

    @GetMapping("/")
    public String index(HttpServletRequest request,HttpSession session, @RequestParam(value = "token",required = false) String token) throws Exception {
        if (!StringUtils.isEmpty(token)){
            // 去认证服务器查询用户信息
            Map<String,String> map = new HashMap<>();
            map.put("token",token);
            HttpResponse response = HttpUtils.doGet("http://auth.ylogin.com", "/loginUserInfo", "GET", new HashMap<String, String>(), map);
            String s = EntityUtils.toString(response.getEntity());
            if (!StringUtils.isEmpty(s)){
                UserResponseVo userResponseVo = JSON.parseObject(s, new TypeReference<UserResponseVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER,userResponseVo);
                localSession.put(token,session);
                sessionTokenMapping.put(session.getId(),token);

            }
        }
        UserResponseVo attribute = (UserResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            return "index";
        } else {
            session.setAttribute("msg","请先进行登录");
            return "redirect:http://auth.ylogin.com/login.html?redirectURL=http://ylogin.com"+request.getServletPath();
        }
    }
    @ResponseBody
    @GetMapping("/deleteSession")
    public String deleteSession(@RequestParam("token") String token){
        HttpSession session = localSession.get(token);
//        session.removeAttribute(AuthServerConstant.LOGIN_USER);
        session.invalidate();
        return "logout";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        String sessionId = request.getSession().getId();
        String token = sessionTokenMapping.get(sessionId);
        return "redirect:http://auth.ylogin.com/logOut?redirectURL=http://ylogin.com&token="+token;
    }
}
