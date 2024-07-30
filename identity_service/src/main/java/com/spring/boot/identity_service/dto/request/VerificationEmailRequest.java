package com.spring.boot.identity_service.dto.request;

import com.spring.boot.identity_service.validate.ValidateVerificationEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ValidateVerificationEmail
public class VerificationEmailRequest {
    private String code;
}
