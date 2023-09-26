package com.clone.instagram.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    @NotNull
    private Long id;

    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    private String profileImgUrl;

    private String description;

    @NotNull
    private boolean nonPublic;

    @Builder
    public UserDto(Long id, String userName, String email, String profileImgUrl, String description, boolean nonPublic) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.description = description;
        this.nonPublic = nonPublic;
    }
}
