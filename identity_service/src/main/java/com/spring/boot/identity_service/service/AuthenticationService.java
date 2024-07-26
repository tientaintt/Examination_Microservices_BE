package com.spring.boot.identity_service.service;

import com.spring.boot.identity_service.dto.request.AuthenticationRequest;
import com.spring.boot.identity_service.dto.request.LogoutRequest;
import com.spring.boot.identity_service.dto.request.RefreshRequest;
import com.spring.boot.identity_service.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    void logout(LogoutRequest logoutRequest) throws Exception;

    AuthenticationResponse refreshToken(RefreshRequest request) throws Exception;
}
