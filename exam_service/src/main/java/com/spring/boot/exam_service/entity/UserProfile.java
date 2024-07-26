package com.spring.boot.exam_service.entity;


import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user_profile")
public class UserProfile extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USER_ID = "user_id";
    private static final String LOGIN_NAME = "login_name";
    private static final String HASH_PASSWORD = "hash_password";
    private static final String DISPLAY_NAME = "display_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String NEW_EMAIL_ADDRESS = "new_email_address";
    private static final String IS_EMAIL_ADDRESS_VERIFIED = "is_email_address_verified";
    private static final String VERIFY_CODE = "verify_code";
    private static final String VERIFY_EXPIRED_CODE_TIME = "verify_expired_code_time";
    private static final String RESET_PASSWORD_CODE = "reset_password_code";
    private static final String RESET_PASSWORD_EXPIRED_CODE_TIME = "reset_password_expired_code_time";
    private static final String ROLES = "roles";

    @Id
    @Column(name = USER_ID)
    private Long userID;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = ROLES)
    private List<String> roles = new ArrayList<>();


    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL
    )
    private List<ClassroomRegistration> ClassroomRegistrations;

    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL
    )
    private List<Score> scores;

    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL
    )
    private List<TestTracking> doTestHistories;

    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL
    )
    private List<Classroom> classroomManager;

    public UserProfile(Long userID, String displayName, String emailAddress, List<String> roles) {
        this.userID = userID;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.roles = roles;
    }
}
