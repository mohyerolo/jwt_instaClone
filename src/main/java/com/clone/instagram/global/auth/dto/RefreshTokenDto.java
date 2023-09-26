package com.clone.instagram.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenDto {
    private String refreshToken;
    private long refreshTokenExpiration;
}
