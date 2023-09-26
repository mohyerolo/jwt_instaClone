package com.clone.instagram.global.config;

import com.clone.instagram.global.auth.jwt.JwtAccessDeniedHandler;
import com.clone.instagram.global.auth.jwt.JwtAuthenticationEntryPoint;
import com.clone.instagram.global.auth.jwt.JwtFilter;
import com.clone.instagram.global.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String[] PERMIT_ALL_PATTERNS = new String[]{
            "/",
            "/auth/**",
            "/WEB-INF/views/**",
            "/css/**",
            "/images/**",
            "/favicon.ico"
    };

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(Stream
                                .of(PERMIT_ALL_PATTERNS)
                                .map(AntPathRequestMatcher::antMatcher)
                                .toArray(AntPathRequestMatcher[]::new)).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> { e.authenticationEntryPoint(authenticationEntryPoint); e.accessDeniedHandler(jwtAccessDeniedHandler);})
                .formLogin(formLogin -> formLogin.loginPage("/auth/loginForm").permitAll().defaultSuccessUrl("/"))
                .addFilterBefore(new JwtFilter(jwtProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
