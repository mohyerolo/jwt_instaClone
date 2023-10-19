package com.clone.instagram.domain.user.service;

import com.clone.instagram.domain.user.dto.UserProfileDto;

public interface UserService {
    UserProfileDto getUserProfileDto(Long targetUserId, Long currentUserId);
}
