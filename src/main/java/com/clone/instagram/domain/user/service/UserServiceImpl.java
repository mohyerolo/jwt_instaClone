package com.clone.instagram.domain.user.service;

import com.clone.instagram.domain.user.dto.UserDto;
import com.clone.instagram.domain.user.entity.User;
import com.clone.instagram.domain.user.repository.UserRepository;
import com.clone.instagram.global.error.ErrorCode;
import com.clone.instagram.global.error.exception.CustomException;
import com.clone.instagram.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto getUserInfo() {
        User user = userRepository.findByUserName(SecurityUtil.getUserName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ERROR, "사용자를 찾을 수 없습니다."));
        return UserDto.builder()
                .user(user)
                .build();
    }
}
