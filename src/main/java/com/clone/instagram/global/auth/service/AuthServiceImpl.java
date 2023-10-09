package com.clone.instagram.global.auth.service;

import com.clone.instagram.domain.user.entity.User;
import com.clone.instagram.domain.user.repository.UserRepository;
import com.clone.instagram.global.auth.dto.RefreshTokenDto;
import com.clone.instagram.global.auth.dto.SignInDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.jwt.JwtCode;
import com.clone.instagram.global.auth.jwt.JwtProvider;
import com.clone.instagram.global.auth.repository.AccessTokenRepository;
import com.clone.instagram.global.auth.repository.RefreshTokenRepository;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;

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
    public TokenDto signIn(SignInDto dto) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserName(), dto.getPassword())
        );
        String accessToken = jwtProvider.createAccessToken(authentication);
        jwtProvider.createRefreshToken(authentication, accessToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        if (!jwtProvider.validateToken(accessToken).equals(JwtCode.ACCESS)) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "유효하지 않은 토큰");
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        String userName = authentication.getName();
        Optional<RefreshTokenDto> optionalRefreshToken = refreshTokenRepository.findByUserName(userName);
        optionalRefreshToken.ifPresent(refreshTokenRepository::delete);

        jwtProvider.setLogout(accessToken);
    }
}
