package com.spring.boot.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User extends AbstractAuditingEntity{
    private static final String DISPLAY_NAME = "display_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String NEW_EMAIL_ADDRESS = "new_email_address";
    private static final String IS_EMAIL_ADDRESS_VERIFIED = "is_email_address_verified";
    private static final String VERIFY_CODE = "verify_code";
    private static final String VERIFY_EXPIRED_CODE_TIME = "verify_expired_code_time";
    private static final String RESET_PASSWORD_CODE = "reset_password_code";
    private static final String RESET_PASSWORD_EXPIRED_CODE_TIME = "reset_password_expired_code_time";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;

    @Column(name = DISPLAY_NAME)
    private String displayName;

    @Column(name = EMAIL_ADDRESS)
    private String emailAddress;

    @Column(name = NEW_EMAIL_ADDRESS)
    private String newEmailAddress;

    @Column(name = IS_EMAIL_ADDRESS_VERIFIED)
    private Boolean isEmailAddressVerified = Boolean.FALSE;

    @Column(name = VERIFY_CODE)
    private String verificationCode;

    @Column(name = VERIFY_EXPIRED_CODE_TIME)
    private Instant verificationExpiredCodeTime;

    @Column(name = RESET_PASSWORD_CODE)
    private String resetPasswordCode;

    @Column(name = RESET_PASSWORD_EXPIRED_CODE_TIME)
    private Instant resetPasswordExpiredCodeTime;

    @ManyToMany
    Set<Role> roles;
}
