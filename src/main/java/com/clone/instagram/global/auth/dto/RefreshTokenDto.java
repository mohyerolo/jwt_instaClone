package com.clone.instagram.global.auth.dto;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@RedisHash(value = "refresh", timeToLive = 60 * 60 * 2)
public class RefreshTokenDto {
    @Id
    private String id;

    private String refreshToken;

    @Indexed
    private String userName;
}