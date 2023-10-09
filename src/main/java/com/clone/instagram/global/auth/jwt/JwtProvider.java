package com.clone.instagram.global.auth.jwt;

import com.clone.instagram.global.auth.dto.AccessTokenDto;
import com.clone.instagram.global.auth.dto.RefreshTokenDto;
import com.clone.instagram.global.auth.repository.AccessTokenRepository;
import com.clone.instagram.global.auth.repository.RefreshTokenRepository;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;

    @PostConstruct
    protected  void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    @Transactional
    public void createRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);

        refreshTokenRepository.save(RefreshTokenDto.builder()
            .userName(authentication.getName())
            .refreshToken(refreshToken)
            .build());
    }

    private String createToken(Authentication authentication, long tokenValidTime) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidTime);

        Claims claims = Jwts.claims()
                .setSubject(authentication.getName());
        claims.put("AUTHORITIES", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtCode validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            return JwtCode.EXPIRED;
        } catch (Exception e) {
            return JwtCode.DENIED;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("AUTHORITIES").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public String getRefreshToken(String userName) {
        return refreshTokenRepository.findByUserName(userName).orElseThrow(() ->
                new CustomException(ErrorCode.NEED_LOGIN, "refresh token 만료, 재로그인 필요")).getRefreshToken();
    }

    public Long getAccessTokenExpiration(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "Token expiration error");
        }
    }

    public boolean isLogout(String accessToken) {
        if (accessTokenRepository.findByAccessToken(accessToken).isPresent()) {
            throw new CustomException(ErrorCode.NEED_LOGIN, "로그인이 필요합니다.");
        }
        return false;
    }

    public void setLogout(String accessToken) {
        Long accessTokenExpiration = getAccessTokenExpiration(accessToken);
        accessTokenRepository.save(AccessTokenDto.builder()
                .accessToken(accessToken)
                .value("logout")
                .expiration(accessTokenExpiration * 60 * 60)
                .build());
    }
}
