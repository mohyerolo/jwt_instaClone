package com.clone.instagram.domain.user.dto;

import com.clone.instagram.domain.user.entity.User;
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
    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.email = user.getEmail();
        this.profileImgUrl = user.getProfileImgUrl();
        this.description = user.getDescription();
        this.nonPublic = user.isNonPublic();
    }
}
