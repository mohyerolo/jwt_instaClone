package com.clone.instagram.global.auth.jwt;

import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final String[] SHOULD_NOT_FILTER_URI_LIST = new String[]{
            "/",
            "/auth/**",
    };

    private static final String[] SHOULD_NOT_FILTER_GET_URL_LIST = new String[] {
//            "/users/profile"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (Arrays.stream(SHOULD_NOT_FILTER_URI_LIST).anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()))) {
            return true;
        } else if (request.getMethod().equals("GET")) {
            return Arrays.stream(SHOULD_NOT_FILTER_GET_URL_LIST)
                    .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[doFilterInternal]: " + request.getRequestURI());
        String token = resolveToken(request);
        Authentication authentication = null;
        try {
            if (StringUtils.hasText(token) && !token.equals("null")) {
                // 사용 가능한 토큰인지 검증
                JwtCode jwtCode = jwtProvider.validateToken(token);
                if (jwtCode.equals(JwtCode.ACCESS) && !jwtProvider.isLogout(token)) {
                    authentication = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (jwtCode.equals(JwtCode.EXPIRED)) {
                    String refreshToken = jwtProvider.getRefreshToken(request.getHeader("Auth"));
                    if (StringUtils.hasText(refreshToken)) {
                        authentication = jwtProvider.getAuthentication(refreshToken);
                        String accessToken = jwtProvider.createAccessToken(authentication);

                        Cookie cookie = new Cookie("token", accessToken);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(60 * 60);
                        response.addCookie(cookie);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("새 accessToken 발급");
                    }
                } else {
                    log.info("jwtFilter 기타 에러 발생");
                    jwtProvider.setLogout(token);
                    Cookie cookie = new Cookie("token", null);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "jwt 에러로 재로그인 필요");
                }
            }
            filterChain.doFilter(request, response);
        } catch (CustomException c) {
            response.setStatus(c.getErrorCode().getStatus());
            response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(c.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("refresh token 만료, 재로그인 필요");
            jwtProvider.setLogout(token);
            Cookie cookie = new Cookie("token", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "jwt 에러로 재로그인 필요");
        }
    }

    private String resolveToken(HttpServletRequest request) {
//        return request.getHeader(HttpHeaders.AUTHORIZATION);
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
