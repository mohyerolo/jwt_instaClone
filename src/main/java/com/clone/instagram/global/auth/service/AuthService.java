package com.clone.instagram.global.auth.service;

import com.clone.instagram.global.auth.dto.SignInDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;

public interface AuthService {
    void signUp(SignUpDto dto);

    TokenDto signIn(SignInDto dto);

    void logout(String accessToken);
}
