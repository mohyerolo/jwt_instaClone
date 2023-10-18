package com.clone.instagram.global.auth.controller;

import com.clone.instagram.global.auth.dto.SignInDto;
import com.clone.instagram.global.auth.dto.SignUpDto;
import com.clone.instagram.global.auth.dto.TokenDto;
import com.clone.instagram.global.auth.service.AuthService;
import com.clone.instagram.global.auth.validator.AuthValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
