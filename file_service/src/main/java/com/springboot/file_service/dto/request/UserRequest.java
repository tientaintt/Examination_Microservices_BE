package com.springboot.file_service.dto.request;

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
    List<String> roles;
}

