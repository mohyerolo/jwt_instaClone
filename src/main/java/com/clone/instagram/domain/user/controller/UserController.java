package com.clone.instagram.domain.user.controller;

import com.clone.instagram.domain.user.dto.CustomUserDetails;
import com.clone.instagram.domain.user.dto.UserProfileDto;
import com.clone.instagram.domain.user.dto.UserProfileUpdateDto;
import com.clone.instagram.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, @RequestParam Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long currentUserId = customUserDetails != null ? customUserDetails.getUser().getId() : null;
        UserProfileDto userProfileDto = userService.getUserProfileDto(id, currentUserId);
        model.addAttribute("userProfileDto", userProfileDto);
        return "user/profile";
    }

    @GetMapping("/updateForm")
    public String getUpdateForm() {
        return "user/update";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid UserProfileUpdateDto userProfileUpdateDto, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        redirectAttributes.addAttribute("id", customUserDetails.getUser().getId());
        return "redirect:/users/profile";
    }
}
