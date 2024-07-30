package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateResetPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ValidateResetPassword
public class ResetPasswordRequest {
    private String password;
    private String code;
}
