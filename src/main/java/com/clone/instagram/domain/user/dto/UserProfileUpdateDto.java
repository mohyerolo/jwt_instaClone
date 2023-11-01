package com.clone.instagram.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDto {

    @NotBlank(message = "사용자 이름을 입력해 주세요. 로그인 시 필요합니다.")
    private String userName;

    @NotBlank(message = "이름을 입력해 주세요. 다른 사용자가 나를 알아볼 때 필요합니다.")
    private String name;

    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    private String profileImgUrl;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    private String description;
}
