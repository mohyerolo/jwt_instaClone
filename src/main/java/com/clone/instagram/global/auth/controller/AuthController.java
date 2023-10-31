package com.clone.instagram.global.auth.controller;

import com.clone.instagram.global.auth.dto.SignInDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.service.AuthService;
import com.clone.instagram.global.auth.validator.AuthValidator;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final AuthValidator authValidator;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "auth/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(Model model) {
        model.addAttribute("signUpDto", new SignUpDto());
        return "auth/joinForm";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute SignUpDto signUpDto, BindingResult bindingResult) {
        authValidator.validate(signUpDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/joinForm";
        }
        authService.signUp(signUpDto);
        return "redirect:/auth/loginForm";
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Void> login(SignInDto dto, HttpServletResponse response) {
        TokenDto tokenDto = authService.signIn(dto);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Authorization", tokenDto.getAccessToken());

        Cookie cookie = new Cookie("token", tokenDto.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                accessToken = cookie.getValue();
            }
        }

        if (accessToken == null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "쿠키에 저장된 토큰이 없습니다.");
        }
        authService.logout(accessToken);

        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
