package com.spring.boot.identity_service.repository;

import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Modifying
    int deleteByUser(User userProfile);

    @Modifying
    int deleteByUser_Id(String userID);
}
