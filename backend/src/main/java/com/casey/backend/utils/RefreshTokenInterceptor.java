package com.casey.backend.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.casey.backend.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.casey.backend.utils.RedisConstants.LOGIN_USER_KEY;
import static com.casey.backend.utils.RedisConstants.LOGIN_USER_TTL;

/** 这个是第一个拦截器
 *  他的作用是将 token 处理成 user 存到 UserHolder 里面
 *  UserHolder 是用 ThreadLocal 实现的，这个是一个多线程的东西具体怎么将有点忘了，你看看黑马的视频
 *  就是不同的请求之间不会相互干扰
 *  大概是这个意思
 *
 *  为什么这个拦截器没发挥作用呢
 *  1. 就是刚刚那里 MVCConfig 没配置
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    // 前置拦截：进入 controllor 之前要做登录校验
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 Header 中的 token
        String token = request.getHeader("authorization");  // 由前端可知，请求头叫 authorization
        System.out.println("token = " + token);
        if (StrUtil.isEmpty(token)) {
            return true;
        }
        System.out.println("到这里了吗？");
        // 2. 基于 token 获取 redis 中的用户
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash()
                .entries(key);
        // 3. 判断用户是否存在
        // entries 方法会做判断，如果为 null 会返回一个空 Map。
        if (userMap.isEmpty()) {
            return true;
        }

        // 5. 将查询到的 Hash 转换成 UserDTO 对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        // 6. 存在，保存用户到 ThreadLocal
        UserHolder.saveUser(userDTO);   // 所以这里刚刚那样强转是错的

        // 7. 刷新 token 有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 8. 放行
        return true;
        // 第一个拦截器把 token 处理成用户了，记住这一点
    }

    // 视图渲染之后，返回一个用户之前：用户业务执行完毕，销毁用户信息，避免内存泄露
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
