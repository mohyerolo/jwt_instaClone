package com.clone.instagram.domain.user.dto;

import com.clone.instagram.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileDto {
    @NotNull
    private User user;

    private boolean loginUser;

    private boolean follow;

    private Integer postCount;

    private Integer userFollowerCount;
    private Integer userFollowingCount;

    @Builder
    public UserProfileDto (User user, boolean loginUser) {
        this.user = user;
        this.loginUser = loginUser;
        this.follow = false;
        this.postCount = 0;
        this.userFollowerCount = 0;
        this.userFollowingCount = 0;
    }
}
