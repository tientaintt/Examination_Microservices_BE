package com.spring.boot.exam_service.utils;


import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class WebUtils {
//    private UserProfileRepository userProfileRepository;
    public Object getCurrentLogedInUser() {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return userProfileRepository.findOneByLoginName(auth.getName()).orElseThrow(
//                UserNotFoundException::new
//        );
        return auth.getPrincipal();
    }
}
