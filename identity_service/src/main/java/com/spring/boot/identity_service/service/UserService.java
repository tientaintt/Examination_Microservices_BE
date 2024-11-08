package com.spring.boot.identity_service.service;

import com.spring.boot.identity_service.dto.request.*;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    APIResponse<JwtResponse> createUser(UserCreationRequest userCreationRequest, Boolean isTeacher, Boolean isAdmin);
    APIResponse<JwtResponse> login(AuthenticationRequest authenticationRequest);
    APIResponse<?> refreshToken(RefreshRequest refreshTokenDTO);
    APIResponse<?> checkEmailVerified(String email);
    APIResponse<?> updateVerifyCode(String userId, String verifyCode);
    APIResponse<?> updateResetPassCode(String userId, String verifyCode);
    APIResponse<?> verifyEmail(String userID, VerificationEmailRequest verificationEmailDTO);
    APIResponse<?> getTotalStudents();
    APIResponse<?> resetPassword(String emailAddress, ResetPasswordRequest resetPasswordDTO);

    APIResponse<?> changePassword(ChangePasswordRequest changePassword);

    APIResponse<?> updateUserProfile(UpdateUserRequest dto);

    APIResponse<?> getAllStudentsByStatus(String search, int page, String column, int size, String sortType, boolean isActive);
    APIResponse<?> getAllTeachersByStatus(String search, int page, String column, int size, String sortType, boolean isActive);
    APIResponse<?> getAllVerifiedStudents(String search, int page, String column, int size, String sortType);
    APIResponse<?> getAllVerifiedTeachers(String search, int page, String column, int size, String sortType);
    APIResponse<?> getCurrentLoggedInUser();
    APIResponse<?> updateImage(MultipartFile file);
    APIResponse<?> deleteUser(String userID);
    APIResponse<?> getAllStudentId();

    APIResponse<?> getAllUserByListId(List<String> userIds,int page,String column,int size,String sortType,String search);
}
