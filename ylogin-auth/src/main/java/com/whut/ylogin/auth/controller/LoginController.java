package com.whut.ylogin.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.whut.ylogin.auth.feign.UserFeignService;
import com.whut.ylogin.common.constant.AuthServerConstant;
import com.whut.ylogin.common.to.UserLoginTo;
import com.whut.ylogin.common.utils.HttpUtils;
import com.whut.ylogin.common.utils.R;
import com.whut.ylogin.common.vo.UserResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 从redis中获取登录用户信息
     * @param token
     * @return
     */
    @ResponseBody
    @GetMapping("/loginUserInfo")
    public String loginUserInfo(@RequestParam("token") String token){
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }

    /**
     * 跳转到登录页面
     * @param url 重定向地址
     * @param sso_token sso登录令牌
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirectURL") String url, @CookieValue(value = "sso_token",required = false) String sso_token){
        // 先判断是否在其他系统登录过
        if (!StringUtils.isEmpty(sso_token)){
            // 添加登录地址
            addLoginUrl(url);
            System.out.println("已登录");
            return "redirect:"+url+"?token="+sso_token;
        }
        return "login";
    }

    /**
     * 保存登录过的地址到redis
     * @param url
     */
    private void addLoginUrl(String url){
        String s = redisTemplate.opsForValue().get("loginUrl");
        if (StringUtils.isEmpty(s)){
            List<String> urls = new ArrayList<>();
            urls.add(url);
            redisTemplate.opsForValue().set("loginUrl",JSON.toJSONString(urls));
        } else{
            List<String> urls = JSON.parseObject(s, new TypeReference<List<String>>() {
            });
            urls.add(url);
            redisTemplate.opsForValue().set("loginUrl",JSON.toJSONString(urls));
        }
    }

    /**
     * @param to 页面表单Key-Value
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginTo to, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        //远程登陆
         R login = userFeignService.login(to);
        if (login.getCode() == 0) {
            UserResponseVo data = login.getData(new TypeReference<UserResponseVo>() {
            });
            log.info("登录成功！用户信息"+data.toString());
            String token = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(token, JSON.toJSONString(data),2, TimeUnit.MINUTES);
            // 添加登录地址
            addLoginUrl(to.getRedirectURL());
            Cookie cookie = new Cookie("sso_token", token);
            response.addCookie(cookie);
            return "redirect:"+to.getRedirectURL()+"?token="+token;
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", login.get("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.ylogin.com/login.html?redirectURL="+to.getRedirectURL();
        }

    }

    /**
     * 登出
     * @param request
     * @param response
     * @param url 返回到登录页时携带的重定向地址
     * @return
     */
    @GetMapping("/logOut")
    public String logout(HttpServletRequest request, HttpServletResponse response,@RequestParam("redirectURL") String url, @RequestParam("token") String token) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sso_token")){
                    // 验证令牌
                    if (cookie.getValue().equals(token)){
                        String value = cookie.getValue();
                        // 清除各应用系统的session
                        String s = redisTemplate.opsForValue().get("loginUrl");
                        Map<String, String> map = new HashMap<>();
                        map.put("token",value);
                        if (!StringUtils.isEmpty(s)){
                            List<String> urls = JSON.parseObject(s, new TypeReference<List<String>>() {
                            });
                            for (String loginUrl : urls) {
                                HttpUtils.doGet(loginUrl, "/deleteSession", "GET",new HashMap<String, String>(), map);
                            }
                        }
                        // 删除redis中保存的用户信息
                        redisTemplate.delete(value);
                        // 清除SSO服务器的cookie令牌
                        Cookie cookie1 = new Cookie("sso_token", "");
                        cookie1.setPath("/");
                        cookie1.setMaxAge(0);
                        response.addCookie(cookie1);
                    }
                }
            }
        }
        // 清除redis保存的登录url
        redisTemplate.delete("loginUrl");
        return "redirect:"+url;
    }
}
