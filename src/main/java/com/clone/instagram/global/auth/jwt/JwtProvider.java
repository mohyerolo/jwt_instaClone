package com.clone.instagram.global.auth.jwt;

import com.clone.instagram.global.auth.dto.RefreshTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 2 * 60 * 1000L;

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    protected  void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public RefreshTokenDto createRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);

        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(refreshToken).getBody().getExpiration();
        redisTemplate.opsForValue().set("RefreshToken:" + authentication.getName(), refreshToken, expiration.getTime(), TimeUnit.MILLISECONDS);

        return RefreshTokenDto.builder()
                .refreshToken(refreshToken)
                .refreshTokenExpiration(expiration.getTime())
                .build();
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

}
