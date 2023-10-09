package com.clone.instagram.global.auth.repository;

import com.clone.instagram.global.auth.dto.RefreshTokenDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenDto, Long> {
    Optional<RefreshTokenDto> findByUserName(String userName);
    void deleteByUserName(String userName);
}
