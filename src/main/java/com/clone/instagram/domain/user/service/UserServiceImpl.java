package com.clone.instagram.domain.user.service;

import com.clone.instagram.domain.user.dto.UserProfileDto;
import com.clone.instagram.domain.user.entity.User;
import com.clone.instagram.domain.user.repository.UserRepository;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileDto(Long targetUserId, Long currentUserId) {
        User target = userRepository.findById(targetUserId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_ERROR, "존재하지 않는 사용자입니다."));

        boolean loginUser = targetUserId.equals(currentUserId);

        return  UserProfileDto.builder()
                .user(target)
                .loginUser(loginUser)
                .build();
    }
}
