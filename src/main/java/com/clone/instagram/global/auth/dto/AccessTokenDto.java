package com.clone.instagram.global.auth.dto;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@RedisHash(value = "access")
public class AccessTokenDto {
    @Id
    private String id;

    @Indexed
    String accessToken;

    String value;

    @TimeToLive
    private Long expiration;
}
