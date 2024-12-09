package com.spring.boot.chat_service.service;

import com.spring.boot.chat_service.dto.request.UserRequest;
import com.spring.boot.chat_service.dto.response.APIResponse;
import com.spring.boot.chat_service.repository.httpclient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
    public List<UserRequest> getAllUser(){

        APIResponse<List<UserRequest>> response=identityClient.getAllUsers();
        log.info(response.getData().toString());
        return response.getData();
    }
}
