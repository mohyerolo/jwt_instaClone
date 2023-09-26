package com.clone.instagram.global.auth.jwt;

import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String[] SHOULD_NOT_FILTER_URI_LIST = new String[]{
            "/",
            "/auth/**",
            "/WEB-INF/views/**",
            "/css/**",
            "/images/**",
            "/favicon.ico"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Stream.of(SHOULD_NOT_FILTER_URI_LIST).anyMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(request.getRequestURI());
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            JwtCode jwtCode = jwtProvider.validateToken(token);
            if (jwtCode.equals(JwtCode.ACCESS) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwtCode.equals(JwtCode.EXPIRED)) {
                log.info("AccessToken 만료");
                String refreshToken = getRefreshToken(request);

                if (StringUtils.hasText(refreshToken)) {
                    jwtCode = jwtProvider.validateToken(refreshToken);
                    if (jwtCode.equals(JwtCode.ACCESS)) {
                        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
                        token = jwtProvider.createAccessToken(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        response.setHeader(HttpHeaders.AUTHORIZATION, token);
                        log.info("유효한 리프레시 토큰으로 새로운 토큰 발급");
                    } else if (jwtCode.equals(JwtCode.EXPIRED)) {
                        Cookie[] cookies = request.getCookies();
                        for (Cookie cookie : cookies) {
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                        // TODO: 로그인 페이지로 이동
                        throw new CustomException(ErrorCode.VALIDATION_ERROR, "리프레시 토큰 만료");
                    }
                } else {
                    throw new CustomException(ErrorCode.VALIDATION_ERROR, "refreshToken이 없습니다.");
                }

            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
