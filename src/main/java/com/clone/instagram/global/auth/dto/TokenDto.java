package com.clone.instagram.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private String accessToken;
    private RefreshTokenDto refreshTokenDto;
}
