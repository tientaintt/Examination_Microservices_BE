package com.spring.boot.notification_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String id;
    String loginName;
    String displayName;
    String emailAddress;
    String newEmailAddress;
    Boolean isEmailAddressVerified;
    Boolean isEnable;
    String imageUrl;
    List<String> roles;
}

