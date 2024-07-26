package com.spring.boot.identity_service.service;

import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByRefreshToken(String token);

    RefreshToken createRefreshToken(String userId);

    RefreshToken verifyExpiration(RefreshToken token);

    @Transactional
    int deleteByUserProfile(User userProfile);
}
