package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateChangePassword;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidateChangePassword
@Builder
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
