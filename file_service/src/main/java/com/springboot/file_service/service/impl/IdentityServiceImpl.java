package com.springboot.file_service.service.impl;

import com.springboot.file_service.dto.request.UserRequest;
import com.springboot.file_service.dto.response.ApiResponse;
import com.springboot.file_service.repository.httpclient.IdentityClient;
import com.springboot.file_service.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class IdentityServiceImpl implements IdentityService {
    IdentityClient identityClient;
    @Override
    public UserRequest getCurrentUser() {
        ApiResponse<UserRequest> response = identityClient.getCurrentUserLogin();
        return response.getData();
    }
}
