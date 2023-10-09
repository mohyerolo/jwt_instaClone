package com.clone.instagram.global.auth.controller;

import com.clone.instagram.global.auth.dto.SignInDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid SignUpDto dto) {
        authService.signUp(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "auth/loginForm";
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(SignInDto dto) {
        TokenDto tokenDto = authService.signIn(dto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", tokenDto.getAccessToken());

        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader(HttpHeaders.AUTHORIZATION));
        return ResponseEntity.ok().build();
    }
}
