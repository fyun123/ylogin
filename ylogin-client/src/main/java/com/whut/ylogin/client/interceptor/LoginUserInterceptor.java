package com.whut.ylogin.client.interceptor;

import com.whut.ylogin.common.constant.AuthServerConstant;
import com.whut.ylogin.common.vo.UserResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserResponseVo attribute = (UserResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            loginUser.set(attribute);
            return true;
        } else {
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.ylogin.com/login.html");
            return false;
        }
    }
}
