package com.casey.backend.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这个是第二个拦截器
 * 这个才是真正判断是否有登录的
 * 他会从 UserHolder 里面那用户，拿得到就有登录，没拿到就没登录
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    // 前置拦截：进入 controllor 之前要做登录校验
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 判断是否需要拦截（ThreadLodal 是否有用户）
        if(UserHolder.getUser() == null) {
            // 没有，需要拦截，设置状态码
            response.setStatus(401);    // 是这里返回的 成功拦截了没登陆的
            // 拦截
            return false;
        }

        // 2. 有用户，放行
        return true;
    }
}
