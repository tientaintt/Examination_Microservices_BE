package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.repository.httpclient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
    public UserRequest getCurrentUser() {
        ApiResponse<UserRequest> response = identityClient.getCurrentUserLogin();
        return response.getData();
    }
    public UserRequest getUserVerifiedByIdAndStatus(String userId,Boolean status){
        ApiResponse<UserRequest> response = identityClient.getUserVerifiedAndStatus(userId,status);
        return response.getData();
    }
}
