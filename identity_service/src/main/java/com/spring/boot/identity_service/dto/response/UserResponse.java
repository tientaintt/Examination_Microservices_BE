package com.spring.boot.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String loginName;
    String displayName;
    String emailAddress;
    String newEmailAddress;
    Boolean isEmailAddressVerified;
    Boolean isEnable;
    List<String> roles;
}
