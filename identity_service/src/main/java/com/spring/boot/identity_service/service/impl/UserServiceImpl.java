package com.spring.boot.identity_service.service.impl;

import com.spring.boot.identity_service.constrant.PredefinedRole;
import com.spring.boot.identity_service.dto.request.AuthenticationRequest;
import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.AuthenticationResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.Role;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.service.AuthenticationService;
import com.spring.boot.identity_service.service.RefreshTokenService;
import com.spring.boot.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    AuthenticationService authenticationService;
    RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public APIResponse<JwtResponse> createUser(UserCreationRequest userCreationRequest, Boolean isTeacher, Boolean isAdmin) {


        log.info("Start createUser()");
        User newUserProfile = new User();
        newUserProfile.setUsername(userCreationRequest.getLoginName());
        newUserProfile.setPassword(userCreationRequest.getPassword());
        // save user information to database
//        newUserProfile.setDisplayName(signupVM.getDisplayName());
//        newUserProfile.setEmailAddress(signupVM.getEmailAddress());
//        newUserProfile.setHashPassword(passwordEncoder.encode(signupVM.getPassword()));
//        newUserProfile.setLoginName(signupVM.getLoginName());
//        newUserProfile.setIsEmailAddressVerified(false);
//        newUserProfile.setIsEnable(true);
        Role role = new Role();
        role.setName(PredefinedRole.ROLE_STUDENT);
        role.setDescription("Student");
        if (isAdmin) {

            role.setName(PredefinedRole.ROLE_ADMIN);
            role.setDescription("Student");
        } else if (isTeacher) {

            role.setName(PredefinedRole.ROLE_TEACHER);
            role.setDescription("Student");
        }
        newUserProfile.setRoles(Set.of(role));
        newUserProfile = userRepository.save(newUserProfile);

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
                        .expired_in(tokenDetails.getExpiryTime())
                        .build())
                .build();

    }

    @Override
    public APIResponse<JwtResponse> login(AuthenticationRequest authenticationRequest) {
        log.info("Start login");

        // Delete the old refresh before add the new refresh
        Optional<User> userProfile = userRepository.findOneByUsername(authenticationRequest.getUsername());
        if (userProfile.isEmpty() || !passwordEncoder.matches(authenticationRequest.getPassword(), userProfile.get().getPassword())) {
            throw new BadCredentialsException("Wrong login or password.");
        }
        // Delete old refresh token
        refreshTokenService.deleteByUserProfile(userProfile.get());
        // Add new refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userProfile.get().getId());

        // get authentication information
        // set login name to loginVm to start create authenticate
        authenticationRequest.setUsername(userProfile.get().getUsername());
        AuthenticationResponse tokenDetails = authenticationService.authenticate(authenticationRequest);
        log.info("End login");
        return APIResponse.<JwtResponse>builder().data(new JwtResponse(
                userProfile.get().getId(),
                tokenDetails.getDisplayName(),
                authenticationRequest.getUsername(),
                tokenDetails.getEmailAddress(),
                true,
                tokenDetails.getAccessToken(),
                refreshToken.getRefreshToken(),
                tokenDetails.getRoles(),
                tokenDetails.getExpiryTime()))
                .build()
                ;

    }
}

