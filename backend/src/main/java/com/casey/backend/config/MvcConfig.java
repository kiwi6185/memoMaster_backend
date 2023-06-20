package com.casey.backend.config;

import com.casey.backend.utils.LoginInterceptor;
import com.casey.backend.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

// 记得加注解！！！
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//            // 登录拦截器
//            registry.addInterceptor(new LoginInterceptor())
//                    // 这个是排除路径
//                    .excludePathPatterns(
//                            "/user/code",
//                            "/user/login"
////                            "/shop/**",
////                            "/shop-type/**",
////                            "/voucher/**"
//                    ).order(1); // 这个是第2个拦截器 排序1
//            // token 的刷新拦截器
//            registry.addInterceptor((new RefreshTokenInterceptor(stringRedisTemplate)))
//                    .addPathPatterns("/**").order(0);   // 这个是第1个拦截器，拦截所有路径，排序是0
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //允许跨域访问资源定义
        registry.addMapping("/**")
                //(只允许本地的指定端口访问)允许所有
                .allowedOrigins("http://localhost:8080")
                // 允许发送凭证: 前端如果配置改属性为true之后，则必须同步配置
                .allowCredentials(true)
                // 允许所有方法
                .allowedMethods("*")
                .allowedHeaders("*");
    }

}
