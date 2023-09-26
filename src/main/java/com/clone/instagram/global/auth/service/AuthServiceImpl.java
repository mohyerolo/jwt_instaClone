package com.clone.instagram.global.auth.service;

import com.clone.instagram.domain.user.entity.User;
import com.clone.instagram.domain.user.repository.UserRepository;
import com.clone.instagram.global.auth.dto.LoginInfoDto;
import com.clone.instagram.global.auth.dto.RefreshTokenDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.jwt.JwtProvider;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public void signUp(SignUpDto dto) {
        if (userRepository.existsByUserName(dto.getUserName())) {
            throw new CustomException(ErrorCode.ACCEPTABLE_BUT_EXISTS, "이미 존재하는 아이디입니다.");
        }
        User user = User.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenDto login(LoginInfoDto dto) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserName(), dto.getPassword())
        );

        String accessToken = jwtProvider.createAccessToken(authentication);
        RefreshTokenDto refreshTokenDto = jwtProvider.createRefreshToken(authentication);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshTokenDto(refreshTokenDto)
                .build();
    }

    @Override
    public void logout(String username) {

    }
}
