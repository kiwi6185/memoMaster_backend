package com.casey.backend.service;

import com.casey.backend.dto.LoginFormDTO;
import com.casey.backend.dto.Result;

import javax.servlet.http.HttpSession;

public interface IUserService {
    Result sendCode(String email, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result emailValidation(String email, String code);
}
