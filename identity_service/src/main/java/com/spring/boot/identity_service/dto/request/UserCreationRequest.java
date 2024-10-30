package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateUserCreation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUserCreation
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    private String loginName;

    private String password;

    private String displayName;

    private String emailAddress;
}