package com.clone.instagram.global.auth.controller;

import com.clone.instagram.global.auth.dto.LoginInfoDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
// jsp 사용은 RestController로 안 되고 Controller로만 반환 가능
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "auth/loginForm";
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(LoginInfoDto dto) {
        TokenDto tokenDto = authService.login(dto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", tokenDto.getAccessToken());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshTokenDto().getRefreshToken())
                .maxAge(tokenDto.getRefreshTokenDto().getRefreshTokenExpiration())
                .path("/")
                // https 환경에서만 쿠키가 발동
                .secure(true)
                // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능
                .sameSite("None")
                // 브라우저에서 쿠키에 접근할 수 없도록 제한
                .httpOnly(true)
                .build();
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid SignUpDto dto) {
        authService.signUp(dto);
        return ResponseEntity.ok().build();
    }
}
