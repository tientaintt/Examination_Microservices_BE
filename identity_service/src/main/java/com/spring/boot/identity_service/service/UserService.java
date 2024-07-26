package com.spring.boot.identity_service.service;

import com.spring.boot.identity_service.dto.request.AuthenticationRequest;
import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;

public interface UserService {
    APIResponse<JwtResponse> createUser(UserCreationRequest userCreationRequest, Boolean isTeacher, Boolean isAdmin);
    APIResponse<JwtResponse> login(AuthenticationRequest authenticationRequest);
}
