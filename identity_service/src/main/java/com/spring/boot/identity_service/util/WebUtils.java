package com.spring.boot.identity_service.util;


import com.spring.boot.identity_service.exception.AppException;
import com.spring.boot.identity_service.exception.ErrorCode;
import com.spring.boot.identity_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.spring.boot.identity_service.entity.User;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class WebUtils {
    private UserRepository userProfileRepository;
    public User getCurrentLogedInUser() {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userProfileRepository.findOneByUsername(auth.getName()).orElseThrow(
                () ->new AppException(ErrorCode.USER_NOT_EXISTED)
        );
    }
}
