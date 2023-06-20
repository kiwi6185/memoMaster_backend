package com.casey.backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.casey.backend.dto.LoginFormDTO;
import com.casey.backend.dto.Result;
import com.casey.backend.dto.UserDTO;
import com.casey.backend.entity.User;
import com.casey.backend.mapper.UserMapper;
import com.casey.backend.service.IUserService;
import com.casey.backend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.casey.backend.utils.HttpUrlConnectionToInterface.doPostOrGet;
import static com.casey.backend.utils.RedisConstants.*;
import static com.casey.backend.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String email, HttpSession session) {
        // 1. 校验邮箱号
        if (RegexUtils.isEmailInvalid(email)) {
            // 2. 如果不符合，返回错误信息
            return Result.fail("邮箱号格式错误！");
        }
        // 3. 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4. 保存验证码到 session
        // 如果直接将 code 保存在 session，堕胎服务器时，session 不共享。因此这里考虑用 redis
//        session.setAttribute("code", code);
        // 以 String 的方式 (phone, code)
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5. 发送验证码
        log.debug("发送短信验证码成功：{}", code);
        System.out.println(code);
        emailValidation(email, code);
        // 返回 ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1. 校验邮箱号
        String email = loginForm.getEmail();
        if (RegexUtils.isEmailInvalid(email)) {
            // 2. 如果不存在，返回错误信息
            return Result.fail("邮箱格式错误！");
        }

        // 2. 从 redis 获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email);
        String code = loginForm.getCode();
        if(cacheCode == null || !cacheCode.equals(code)) {
            // 3. 不一致，报错
            return Result.fail("验证码错误！");
        }

        // 4. 一致，根据邮箱查询用户
        // ServiceImpl 是 mybatis-plus 提供的，我们可以对其进行单表的增删查改。
        User user = query().eq("email", email).one();

        // 5. 判断用户是否存在
        if (user == null) {
            // 6. 不存在，创建新用户
            user = createUserWithEmail(email);
        }

        // 7. 保存用户信息到 redis
        // 7.1 随机生成 token，作为登录令牌
        // simple 不带中划线 -
        String token = UUID.randomUUID().toString(true);
        // 7.2 将 User 对象转为 Hash 存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class); // 将 user 浅拷贝给 userDTO
        // 用这个 beanToMap 方法将返回值成员都变成 String，否则报错
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));    // 函数式编程
        // 7.3 存储
        String tokenkey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenkey, userMap);
        // 7.4 设置有效期
        stringRedisTemplate.expire(tokenkey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8. 返回 token
        System.out.println(Result.ok(token));
        return Result.ok(token); // 必须有 token 参数，因为前端要获取一个 token 变量
    }

    @Override
    public Result emailValidation(String email, String code) {
        // 1. 封装数据
        JSONObject data = new JSONObject();
        try{
            data.put("email", email);
            data.put("code", code);
        } catch(JSONException e){
            e.printStackTrace();
        }
        // 2. 访问python后端，发送 POST 请求
        doPostOrGet("http://127.0.0.1:5000/emailValidation", data);
        return Result.ok();
    }

    private User createUserWithEmail(String email) {
        // 1. 创建用户
        User user = new User();
        user.setEmail(email);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2. 保存用户
        save(user);     // mybatis-plus 提供的
        return user;
    }
}
