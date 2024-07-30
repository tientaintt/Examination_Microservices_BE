package com.spring.boot.notification_service.service;

import com.spring.boot.notification_service.dto.request.UserRequest;

public interface IdentityService {
    UserRequest checkEmailVerified(String email);
    void updateVerificationEmailCode(String userId, String verificationCode);
    UserRequest getCurrentUser();
    UserRequest getUserVerifiedAndStatus(String userId,Boolean status);
    void updateVerificationPassCode(String userId, String verificationPassCode);
}
