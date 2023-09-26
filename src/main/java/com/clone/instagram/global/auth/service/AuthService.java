package com.clone.instagram.global.auth.service;

import com.clone.instagram.global.auth.dto.LoginInfoDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;

public interface AuthService {
    void signUp(SignUpDto dto);
    TokenDto login(LoginInfoDto dto);
    void logout(String username);
}
