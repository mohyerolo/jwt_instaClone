package com.clone.instagram.global.auth.validator;

import com.clone.instagram.domain.user.repository.UserRepository;
import com.clone.instagram.global.auth.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AuthValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpDto dto = (SignUpDto) target;

        if (userRepository.existsByUserName(dto.getUserName())) {
            errors.rejectValue("userName", "exists", "이미 존재하는 유저네임입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "exists", "이미 존재하는 이메일입니다.");
        }
    }
}
