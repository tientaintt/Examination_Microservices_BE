package com.spring.boot.identity_service.service;

import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;

public interface StudentService {
    APIResponse<UserResponse> getVerifiedStudentByIdAndStatus(String id, Boolean status);

}
