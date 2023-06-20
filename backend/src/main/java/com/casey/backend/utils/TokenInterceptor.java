package com.casey.backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// 这个应该是解决跨域问题的，登录的拦截和他没关系
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        //该字段可选，是个布尔值，表示是否可以携带cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())){
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        // 检查防盗链
        return true;//checkReferer(request, response) && checkTimeStamp(request, response) && checkSign(request, response);
    }
}
