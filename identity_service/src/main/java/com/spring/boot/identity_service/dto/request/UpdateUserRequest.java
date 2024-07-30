package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateUpdateUser;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateUser
public class UpdateUserRequest {
    private String displayName;
    private String emailAddress;
}
