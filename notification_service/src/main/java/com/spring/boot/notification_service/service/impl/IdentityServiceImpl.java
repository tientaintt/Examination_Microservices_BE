package com.spring.boot.notification_service.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.notification_service.dto.request.UserRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.repository.httpclient.IdentityClient;
import com.spring.boot.notification_service.service.IdentityService;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class IdentityServiceImpl implements IdentityService {
    IdentityClient identityClient;

    @Override
    public UserRequest checkEmailVerified(String email) {
        log.info("Checking email verified");
        APIResponse<?> response = identityClient.checkEmailVerified(email);
        log.info(response.toString());
//        Object data = response.getData();
//        if (data instanceof LinkedHashMap) {
//            LinkedHashMap<?, ?> userData = (LinkedHashMap<?, ?>) data;
//            return UserRequest.builder()
//                    .id(userData.get("id").toString())
//                    .isEmailAddressVerified(Boolean.valueOf(userData.get("isEmailAddressVerified").toString()))
//                    .emailAddress(userData.get("emailAddress").toString())
//                    .displayName(userData.get("displayName").toString())
//                    .roles(Collections.singletonList(userData.get("roles").toString()))
//                    .newEmailAddress(userData.get("newEmailAddress").toString())
//                    .isEnable(Boolean.valueOf(userData.get("isEnable").toString()))
//                    .loginName(userData.get("loginName").toString())
//                    .build()
//                    ;
//        }
//        return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Chuyển đổi dữ liệu từ response.getData() thành chuỗi JSON trước khi ánh xạ
            String jsonData = objectMapper.writeValueAsString(response.getData());

            // Ánh xạ dữ liệu từ chuỗi JSON thành đối tượng UserRequest
            UserRequest userRequest = objectMapper.readValue(jsonData, UserRequest.class);

            return userRequest;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateVerificationEmailCode(String userId, String verificationCode) {
        identityClient.updateVerifyCode(userId, verificationCode);

    }

    @Override
    public UserRequest getCurrentUser() {
        APIResponse<UserRequest> response = identityClient.getCurrentUserLogin();
        return response.getData();
    }

    @Override
    public UserRequest getUserVerifiedAndStatus(String userId, Boolean status) {
        APIResponse<UserRequest> response = identityClient.getUserVerifiedAndStatus(userId, status);
        return response.getData();
    }

    @Override
    public void updateVerificationPassCode(String userId, String verificationPassCode) {
        identityClient.updateVerifyPasswordCode(userId, verificationPassCode);
    }
}
