package com.spring.boot.identity_service.dto.response;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String userId;

    private String displayName;

    private String loginName;

    private String emailAddress;

    private Boolean isEmailAddressVerified;

    private String accessToken;

    private String refreshToken;

    private List<String> roles;
    private String imageUrl;
    private Date expired_in;

}
