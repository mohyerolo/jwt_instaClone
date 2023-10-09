package com.clone.instagram.global.auth.repository;

import com.clone.instagram.global.auth.dto.AccessTokenDto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccessTokenRepository extends CrudRepository<AccessTokenDto, Long> {
    Optional<AccessTokenDto> findByAccessToken(String accessToken);
}
