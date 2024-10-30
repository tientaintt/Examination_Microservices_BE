package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateAuthentication;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateAuthentication
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    String loginName;
    String password;
}
