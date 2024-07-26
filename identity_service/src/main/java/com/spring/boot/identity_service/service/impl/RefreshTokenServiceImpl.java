package com.spring.boot.identity_service.service.impl;

import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.exception.RefreshTokenNotFoundException;
import com.spring.boot.identity_service.repository.RefreshTokenRepository;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j

public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jwt.refresh-token-expiration}")
    private long JWT_REFRESH_TOKEN_VALIDITY;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userProfileRepository;
    @Override
    public Optional<RefreshToken> findByRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByRefreshToken(token);
        if (!refreshToken.isPresent()){
            log.error("Refresh token is not in database!");
            throw new RefreshTokenNotFoundException(token, "Refresh token is not in database!");
        }
        return refreshToken;
    }

    @Override
    public RefreshToken createRefreshToken(String userId) {
        return null;
    }


    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        return null;
    }

    @Override
    public int deleteByUserProfile(User userProfile) {
        return 0;
    }
}
