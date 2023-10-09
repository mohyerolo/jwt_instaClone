package com.clone.instagram.global.auth.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TokenDto {
    private String accessToken;
}
