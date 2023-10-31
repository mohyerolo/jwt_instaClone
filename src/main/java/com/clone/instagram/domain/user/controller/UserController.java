package com.clone.instagram.domain.user.controller;

import com.clone.instagram.domain.user.dto.CustomUserDetails;
import com.clone.instagram.domain.user.dto.UserProfileDto;
import com.clone.instagram.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ModelAndView profile(@RequestParam Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long currentUserId = customUserDetails != null ? customUserDetails.getUser().getId() : null;
        UserProfileDto userProfileDto = userService.getUserProfileDto(id, currentUserId);

        ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("userProfileDto", userProfileDto);
        return mav;
    }
}
