package com.casey.backend.controller;

import com.casey.backend.dto.LoginFormDTO;
import com.casey.backend.dto.Result;
import com.casey.backend.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Resource
    private IUserService userService;

//    @Resource
//    private IUserInfoService userInfoService;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("email") String email, HttpSession session) {
        // 发送邮箱验证码并保存验证码
        return userService.sendCode(email, session);
    }
    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
//    josn格式才用@RequestBody，否则数据获取不到
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        // 实现登录功能
        return userService.login(loginForm, session);
    }
    @PostMapping("/emailValidation")
    public Result emailValidation(@RequestParam("email") String email, String code){
        return userService.emailValidation(email, code);
    }
}
