package com.spring.boot.identity_service.service.impl;


import com.spring.boot.identity_service.constrant.PredefinedRole;
import com.spring.boot.identity_service.dto.request.*;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.AuthenticationResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.Role;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.exception.AppException;
import com.spring.boot.identity_service.exception.ErrorCode;

import com.spring.boot.identity_service.exception.RefreshTokenNotFoundException;
import com.spring.boot.identity_service.mapper.UserMapper;
import com.spring.boot.identity_service.repository.RoleRepository;
import com.spring.boot.identity_service.repository.StudentRepositoryRead;
import com.spring.boot.identity_service.repository.TeacherRepository;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.service.AuthenticationService;
import com.spring.boot.identity_service.service.FileService;
import com.spring.boot.identity_service.service.RefreshTokenService;
import com.spring.boot.identity_service.service.UserService;
import com.spring.boot.identity_service.util.EnumParentFileType;
import com.spring.boot.identity_service.util.PageUtils;
import com.spring.boot.identity_service.util.WebUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
//    @Value("${send-by-mail.verification-code-time}")

    private Long verificationCodeTime = 30L;
    private Long resetPasswordCodeTime = 15L;
    UserRepository userRepository;
    AuthenticationService authenticationService;
    RefreshTokenService refreshTokenService;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserMapper userMapper;
    WebUtils webUtils;
    StudentRepositoryRead studentRepositoryRead;
    FileService fileService;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public APIResponse<JwtResponse> createUser(UserCreationRequest userCreationRequest, Boolean isTeacher, Boolean isAdmin) {


        log.info("Start createUser()");
        User newUserProfile = new User();
        newUserProfile.setUsername(userCreationRequest.getLoginName());
        newUserProfile.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        // save user information to database
        newUserProfile.setDisplayName(userCreationRequest.getDisplayName());
        newUserProfile.setEmailAddress(userCreationRequest.getEmailAddress());
        newUserProfile.setIsEmailAddressVerified(false);
        newUserProfile.setIsEnable(true);
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.ROLE_STUDENT).ifPresent(roles::add);
        if (isAdmin) {

            roleRepository.findById(PredefinedRole.ROLE_ADMIN).ifPresent(roles::add);
        } else if (isTeacher) {

            roleRepository.findById(PredefinedRole.ROLE_TEACHER).ifPresent(roles::add);
        }

        newUserProfile.setRoles(roles);
      //  newUserProfile = userRepository.save(newUserProfile);
        newUserProfile=addStudent(newUserProfile);
        // create response information to user
        AuthenticationResponse tokenDetails = authenticationService.authenticate(
                new AuthenticationRequest(newUserProfile.getUsername(), userCreationRequest.getPassword())
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(newUserProfile.getId());
        log.info("End createUser()");
        return APIResponse.<JwtResponse>builder()
                .data(new JwtResponse().builder()
                        .userId(newUserProfile.getId())
                        .displayName(tokenDetails.getDisplayName())
                        .loginName(userCreationRequest.getLoginName())
                        .emailAddress(userCreationRequest.getEmailAddress())
                        .accessToken(tokenDetails.getAccessToken())
                        .refreshToken(refreshToken.getRefreshToken())
                        .roles(tokenDetails.getRoles())
                        .isEmailAddressVerified(false)
                        .expired_in(tokenDetails.getExpiryTime())
                        .build())
                .build();

    }

    @Override
    public APIResponse<JwtResponse> login(AuthenticationRequest authenticationRequest) {
        log.info("Start login");

        // Delete the old refresh before add the new refresh
        Optional<User> userProfile = userRepository.findOneByUsernameOrEmailAddressAndIsEmailAddressVerified(authenticationRequest.getLoginName(), authenticationRequest.getLoginName(), true);

        if (userProfile.isEmpty() || !passwordEncoder.matches(authenticationRequest.getPassword(), userProfile.get().getPassword())) {
            throw new BadCredentialsException("Wrong login or password.");
        }
        // Delete old refresh token
        refreshTokenService.deleteByUserProfile(userProfile.get());
        // Add new refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userProfile.get().getId());

        // get authentication information
        // set login name to loginVm to start create authenticate
        authenticationRequest.setLoginName(userProfile.get().getUsername());
        AuthenticationResponse tokenDetails = authenticationService.authenticate(authenticationRequest);
        log.info("End login");
        return APIResponse.<JwtResponse>builder().data(new JwtResponse(
                        userProfile.get().getId(),
                        tokenDetails.getDisplayName(),
                        authenticationRequest.getLoginName(),
                        tokenDetails.getEmailAddress(),
                        true,
                        tokenDetails.getAccessToken(),
                        refreshToken.getRefreshToken(),
                        tokenDetails.getRoles(),
                        tokenDetails.getExpiryTime()))
                .build()
                ;

    }

    /*
     * NOTE CHECK AGAIN*/
    @Override
    public APIResponse<?> refreshToken(RefreshRequest refreshTokenDTO) {
        log.info("Start refresh token");
        return APIResponse.builder().data(authenticationService.refreshToken(refreshTokenDTO)).build();
//        String requestRefreshToken = refreshTokenDTO.getRefreshToken();
//
//
//        Optional<RefreshToken> refreshToken = refreshTokenService.findByRefreshToken(requestRefreshToken);
//        return refreshTokenService.findByRefreshToken(requestRefreshToken)
//                .map(refreshTokenService::verifyExpiration)
//                .map(RefreshToken::getUser)
//                .map(userProfile -> {
//                    AuthenticationResponse tokenDetails = authenticationService.authenticate(
//                            new AuthenticationRequest(userProfile.getUsername(), userProfile.getPassword())
//                    );
//                    log.info("End refresh token");
//                    return APIResponse.<JwtResponse>builder().data(new JwtResponse(
//                                    userProfile.getId(),
//                                    tokenDetails.getDisplayName(),
//                                    userProfile.getUsername(),
//                                    tokenDetails.getEmailAddress(),
//                                    true,
//                                    tokenDetails.getAccessToken(),
//                                    requestRefreshToken,
//                                    tokenDetails.getRoles(),
//                                    tokenDetails.getExpiryTime()))
//                            .build()
//                            ;
//                })
//                .orElseThrow(() -> new RefreshTokenNotFoundException(requestRefreshToken,
//                        "Refresh token is not in database!"));
    }

    @Override
    public APIResponse<?> checkEmailVerified(String email) {
        Optional<User> user = userRepository.findOneByEmailAddressVerified(email);
        if (user.isPresent()) {
            return APIResponse.builder()
                    .data(userMapper.toUserResponse(user.get()))
                    .build();
        } else {
            return APIResponse.builder()

                    .build();
        }


    }

    @Override
    public APIResponse<?> updateVerifyCode(String userId, String verifyCode) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        user.setVerificationCode(verifyCode);
        user.setVerificationExpiredCodeTime(Instant.now().plusSeconds(verificationCodeTime * 60));
        //userRepository.save(user);
        updateStudent(user);
        return APIResponse.builder()
                .data(user)
                .build();

    }

    @Override
    public APIResponse<?> updateResetPassCode(String userId, String verifyCode) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        user.setResetPasswordCode(verifyCode);
        user.setResetPasswordExpiredCodeTime(Instant.now().plusSeconds(resetPasswordCodeTime * 60));
       // userRepository.save(user);
        updateStudent(user);
        return APIResponse.builder()
                .data(userMapper.toUserResponse(user))
                .build();
    }

    private void updateVerifiedEmail(User userProfile) {
        userProfile.setIsEmailAddressVerified(true);
        userProfile.setVerificationCode(null);
        userProfile.setVerificationExpiredCodeTime(null);
        if (Objects.nonNull(userProfile.getNewEmailAddress())) {
            userProfile.setEmailAddress(userProfile.getNewEmailAddress());
        }
        userProfile.setNewEmailAddress(null);
//        userRepository.save(userProfile);
        updateStudent(userProfile);
    }


    @Override
    public APIResponse<?> verifyEmail(String userID, VerificationEmailRequest verificationEmailDTO) {
        log.info("Start verify email by verification code");
        User userProfile = userRepository.findById(userID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String checkEmailAddress = userProfile.getEmailAddress();

        // If email address has been verified without value of new_email_address column
        if (Objects.isNull(userProfile.getNewEmailAddress()) && userProfile.getIsEmailAddressVerified()) {
            throw new AppException(ErrorCode.VERIFY_INVALID_STATUS);
        }

        // Make sure that current email address has not been verified by another user
        // If new email address is not null, make sure that this email has not been verified by another user
        if (Objects.nonNull(userProfile.getNewEmailAddress()) && userProfile.getIsEmailAddressVerified()) {
            checkEmailAddress = userProfile.getNewEmailAddress();
            // if new email address equals to old verified email address
            //Just for testing, we do not allow users to update the new email address
            // to the same as the old verified email address
            if (userProfile.getEmailAddress().equals(checkEmailAddress)) {
                log.warn("New email address is the same as value with verified email address : " + checkEmailAddress);
                return APIResponse.builder().build();
            }
        }
        // check email address before verifying
        Optional<User> value = userRepository
                .findOneByEmailAddressVerified(checkEmailAddress);
        if (value.isPresent()) {
            throw new AppException(ErrorCode.VERIFY_EMAIL_VERIFIED_BY_ANOTHER_USER);
        }

        // Check code and update user info.
        if (userProfile.getVerificationCode().equals(verificationEmailDTO.getCode())
                && Instant.now().isBefore(userProfile.getVerificationExpiredCodeTime())) {
            updateVerifiedEmail(userProfile);
            log.info("End verify email by verification code");
            return new APIResponse<>();
        } else {

            throw new AppException(ErrorCode.VERIFY_NOT_ACCEPTABLE);
        }
    }

    @Override
    public APIResponse<?> getTotalStudents() {
        Optional<Role> roleStudent=roleRepository.findByName("STUDENT");
        int count=userRepository.countAllByRolesIs(roleStudent.get());
        return APIResponse.builder().data(count).build();
    }

    private void updatePasswordReset(ResetPasswordRequest resetPasswordDTO, User userProfile) {
        userProfile.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        userProfile.setResetPasswordCode(null);
        userProfile.setResetPasswordExpiredCodeTime(null);
        userProfile.setUpdateBy(userProfile.getUsername());
        userProfile.setUpdateDate(Instant.now());
        //userRepository.save(userProfile);
        updateStudent(userProfile);
    }

    @Override
    public APIResponse<?> resetPassword(String emailAddress, ResetPasswordRequest resetPasswordDTO) {
        log.info("Start reset password!");
        User userProfile = userRepository.findOneByEmailAddressVerified(emailAddress).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (userProfile.getResetPasswordCode().equals(resetPasswordDTO.getCode())
                && Instant.now().isBefore(userProfile.getResetPasswordExpiredCodeTime())) {
            updatePasswordReset(resetPasswordDTO, userProfile);
            log.info("End reset password!");
            return APIResponse.builder().build();
        } else {
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.RESET_PASSWORD_NOT_ACCEPTABLE.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.RESET_PASSWORD_NOT_ACCEPTABLE.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.RESET_PASSWORD_NOT_ACCEPTABLE);
        }
    }

    @Override
    public APIResponse<?> changePassword(ChangePasswordRequest changePassword) {
        // Get current logged in user
        log.info("Change password!");
        User userProfile = webUtils.getCurrentLogedInUser();

        // If the old password is not correct.
        if (!passwordEncoder.matches(changePassword.getOldPassword(), userProfile.getPassword())) {
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.CHANGE_PASSWORD_WRONG_OLD_PASSWORD.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.CHANGE_PASSWORD_WRONG_OLD_PASSWORD.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.WRONG_OLD_PASSWORD);
        }
        // Update password
        userProfile.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userProfile.setUpdateBy(userProfile.getUsername());
        userProfile.setUpdateDate(Instant.now());
//        userRepository.save(userProfile);
        updateStudent(userProfile);
        // Delete old refresh token
        refreshTokenService.deleteByUserProfile(userProfile);
        // Add new refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userProfile.getId());

        // generate a new access token

        AuthenticationResponse tokenDetails = authenticationService.authenticate(
                new AuthenticationRequest(userProfile.getUsername(), changePassword.getNewPassword())
        );

        return APIResponse.<JwtResponse>builder().data(new JwtResponse(
                        userProfile.getId(),
                        tokenDetails.getDisplayName(),
                        userProfile.getUsername(),
                        tokenDetails.getEmailAddress(),
                        true,
                        tokenDetails.getAccessToken(),
                        refreshToken.getRefreshToken(),
                        tokenDetails.getRoles(),
                        tokenDetails.getExpiryTime()))
                .build()
                ;

    }

    @Override
    public APIResponse<?> updateUserProfile(UpdateUserRequest dto) {
        // Get current logged in user
        User userProfile = webUtils.getCurrentLogedInUser();
        // Update display name
        if (StringUtils.isNoneBlank(dto.getDisplayName())) {
            userProfile.setDisplayName(dto.getDisplayName());
            //userRepository.save(userProfile);
            updateStudent(userProfile);
        }
        // Update email address.
        if (StringUtils.isNoneBlank(dto.getEmailAddress())) {
            String newEmailAddress = dto.getEmailAddress();
            // If old email address has been verified
            if (userProfile.getIsEmailAddressVerified()) {
                if (userProfile.getEmailAddress().equals(newEmailAddress)) {
                    return APIResponse.builder().build();
                }
                userProfile.setNewEmailAddress(newEmailAddress);
            }
            // If old email address has not been verified
            else {
                userProfile.setEmailAddress(newEmailAddress);
            }
            userProfile.setUpdateBy(userProfile.getUsername());
            userProfile.setUpdateDate(Instant.now());
//            userRepository.save(userProfile);
            addStudent(userProfile);
//            mailService.sendVerificationEmail(userProfile.getLoginName());
        }
        return APIResponse.builder().data(userMapper.toUserResponse(userProfile)).build();
    }

    @Override
    public APIResponse<?> getAllStudentsByStatus(String search, int page, String column, int size, String sortType, boolean isActive) {
        log.info("Start get all active student searched by display name and email");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<User> listStudents = studentRepositoryRead
                .findAllSearchedStudentsByStatus(searchText, isActive, pageable);
        Page<UserResponse> response = listStudents.map(userMapper::toUserResponse);

        return APIResponse.builder()
                .data(response).build();
    }

    @Override
    public APIResponse<?> getAllTeachersByStatus(String search, int page, String column, int size, String sortType, boolean isActive) {
        log.info("Start get all teacher searched by display name and email");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<User> listTeachers = teacherRepository
                .findAllSearchedTeachersByStatus(searchText, isActive, pageable);
        Page<UserResponse> response = listTeachers.map(userMapper::toUserResponse);

        return APIResponse.builder()
                .data(response).build();
    }

    @Override
    public APIResponse<?> getAllVerifiedStudents(String search, int page, String column, int size, String sortType) {
        log.info("Get all verified student: start");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<User> listStudents = studentRepositoryRead.findAllVerifiedStudents(searchText, pageable);
        Page<UserResponse> response = listStudents
                .map(userMapper::toUserResponse);
        log.info("Get all verified student: end");
        return APIResponse.builder()
                .data(response).build();
    }

    @Override
    public APIResponse<?> getAllVerifiedTeachers(String search, int page, String column, int size, String sortType) {
        log.info("Get all verified teacher: start");
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<User> listStudents = teacherRepository.findAllVerifiedTeachers(searchText, pageable);
        Page<UserResponse> response = listStudents
                .map(userMapper::toUserResponse);
        log.info("Get all verified teacher: end");
        return APIResponse.builder()
                .data(response).build();
    }

    @Override
    public APIResponse<?> getCurrentLoggedInUser() {
        User userProfile = webUtils.getCurrentLogedInUser();
        return APIResponse.builder()
                .data(userMapper.toUserResponse(userProfile))
                .build();
//                ResponseEntity.ok(CustomBuilder.buildUserProfileResponse(userProfile));
    }

    @Override
    public APIResponse<?> updateImage(MultipartFile file) {
        User user = webUtils.getCurrentLogedInUser();
        FileRequest fileRequest = fileService.saveFile(file, user.getId(), EnumParentFileType.USER_AVATAR.name());
        return APIResponse.builder()
                .data(UserResponse.builder()
                        .imageUrl(fileRequest.getPath_file())
                        .id(user.getId())
                        .isEnable(user.getIsEnable())
                        .roles(user.getRoles().stream().map(role -> role.getName()).toList())
                        .newEmailAddress(user.getNewEmailAddress())
                        .isEmailAddressVerified(user.getIsEmailAddressVerified())
                        .displayName(user.getDisplayName())
                        .emailAddress(user.getEmailAddress())
                        .loginName(user.getUsername())
                        .build())
                .build();
    }

    @Override
    public APIResponse<?> deleteUser(String userId) {
        log.warn("Delete user: start");
        User userProfile = webUtils.getCurrentLogedInUser();
        if (!userProfile.getRoles().stream().toString().contains(PredefinedRole.ROLE_ADMIN)) {
            log.warn("Delete user: Logged-in user do not have permission to delete user");
            throw new AppException(ErrorCode.PERMISSION_DENIED);

        }
        Optional<User> targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            log.warn("Delete user: Not found");
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (targetUser.get().getRoles().stream().toString().contains(PredefinedRole.ROLE_ADMIN)) {
            log.warn("Delete user: Can not delete admin account");
            throw new AppException(ErrorCode.DELETED_ADMIN);


        }
       // userRepository.delete(targetUser.get());
        deleteStudent(targetUser.get());
        log.warn(String.format("Delete user: User with id %s has been deleted by admin with id %s ", userId, userProfile.getId()));
        log.warn("Delete user: end");
        return APIResponse.builder()
                .message("Deleted user")
                .build();
    }
    @CacheEvict(value = "userIds", allEntries = true)
    public User updateStudent(User user) {
        // Logic cập nhật thông tin sinh viên
       return studentRepositoryRead.save(user);
    }

    @CacheEvict(value = "userIds", allEntries = true)
    public User addStudent(User user) {
        return studentRepositoryRead.save(user);
    }

    @CacheEvict(value = "userIds", allEntries = true)
    public void deleteStudent(User user) {
        studentRepositoryRead.delete(user);
    }
    @Override
    public APIResponse<?> getAllStudentId() {
        List<String> userIds=getAllStudentIds();
        return APIResponse.builder().data(userIds).build();
    }
    @CacheEvict(value = "userIds")
    public List<String> getAllStudentIds() {
        return studentRepositoryRead.findAllStudentId();
    }
    @Override
    public APIResponse<?> getAllUserByListId(List<String> userIds, int page, String column, int size, String sortType,String search) {
        log.info("{} {} {} {}", page, column, size, sortType);
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Page<User> users = userRepository.findAllByIdIn(userIds, search, pageable);

        Page<UserResponse> responses = users.map(user -> {
            log.info(user.getUsername());
            return UserResponse.builder()
                    .id(user.getId())
                    .isEnable(user.getIsEnable())
                    .roles(user.getRoles().stream().map(role -> role.getName()).toList())
                    .newEmailAddress(user.getNewEmailAddress())
                    .isEmailAddressVerified(user.getIsEmailAddressVerified())
                    .displayName(user.getDisplayName())
                    .emailAddress(user.getEmailAddress())
                    .loginName(user.getUsername())
                    .build();
        });
        log.info(responses.toString());
        // Kiểm tra sự tồn tại của Pageable và Sort trong response
        if (responses instanceof Page) {
            Page<?> pageResponse = responses;
            Sort sort = pageResponse.getSort();
            Pageable pageableResponse = pageResponse.getPageable();

            // Kiểm tra Sort và Pageable trong response
            if (sort != null) {
                log.info("Sort exists in response");
                log.info(sort.toString());
            } else {
                log.info("Sort does not exist in response");
            }

            if (pageableResponse != null) {
                log.info("Pageable exists in response");
            } else {
                log.info("Pageable does not exist in response");
            }
        } else {
            log.info("Response is not a Page object");
        }
        return APIResponse.builder()
                .data(responses)
                .build();
    }

    @Override
    public APIResponse<?> getAllUser() {
        Optional<Role> role=roleRepository.findByName("ADMIN");
        if (role.isPresent()) {
            List<User> users=userRepository.getAllByRolesIsNot(role.get());
            List<UserResponse> responses=users.stream().map(user -> {
                return UserResponse.builder()
                        .id(user.getId())
                        .isEnable(user.getIsEnable())
                        .roles(user.getRoles().stream().map(role1 -> role1.getName()).toList())
                        .newEmailAddress(user.getNewEmailAddress())
                        .isEmailAddressVerified(user.getIsEmailAddressVerified())
                        .displayName(user.getDisplayName())
                        .emailAddress(user.getEmailAddress())
                        .loginName(user.getUsername())
                        .build();
            }).toList();
            return APIResponse.builder().data(responses).build();
        }
        return null;
    }
}

