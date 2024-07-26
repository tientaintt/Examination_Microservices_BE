package com.spring.boot.identity_service.dto.response;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
        private String displayName;

        private String accessToken;

        private String emailAddress;

        private Date expiryTime;

        private List<String> roles = new ArrayList<>();


}
