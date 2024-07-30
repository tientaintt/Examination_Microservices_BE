package com.spring.boot.identity_service.service.impl;

import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.exception.AppException;
import com.spring.boot.identity_service.exception.ErrorCode;
import com.spring.boot.identity_service.mapper.UserMapper;
import com.spring.boot.identity_service.repository.StudentRepositoryRead;
import com.spring.boot.identity_service.service.StudentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentServiceImpl implements StudentService {
    StudentRepositoryRead studentRepositoryRead;
    UserMapper userMapper;

    @Override
    public APIResponse<UserResponse> getVerifiedStudentByIdAndStatus(String id, Boolean status) {
        Optional<User> user = studentRepositoryRead.findVerifiedStudentByIdAndStatus(id, status);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        } else
            return APIResponse.<UserResponse>builder().data(userMapper.toUserResponse(user.get())).build();
    }
}
