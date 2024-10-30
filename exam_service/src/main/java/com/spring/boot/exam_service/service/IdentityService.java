package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.UserIdsDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.repository.httpclient.IdentityClient;
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
    public UserRequest getCurrentUser() {
        ApiResponse<UserRequest> response = identityClient.getCurrentUserLogin();
        return response.getData();
    }
    public UserRequest getUserVerifiedByIdAndStatus(String userId,Boolean status){
        log.info("getUserVerifiedByIdAndStatus response: {}",  identityClient.getUserVerifiedAndStatus(userId,status));

        ApiResponse<UserRequest> response = identityClient.getUserVerifiedAndStatus(userId,status);
        return response.getData();
    }
    public List<UserRequest> getAllUserByListId(List<String> listId){
        UserIdsDTO userIdsDTO= UserIdsDTO.builder().userIds(listId).build();
        ApiResponse<Page<UserRequest>> response=identityClient.getAllUsersByUserIds(userIdsDTO,0,null,12,null,null);
        log.info(response.getData().toString());
        return response.getData().getContent();
    }


}
