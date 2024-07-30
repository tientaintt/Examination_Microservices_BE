package com.spring.boot.notification_service.service.impl;


import com.spring.boot.notification_service.dto.request.MultipleChoiceTestRequest;
import com.spring.boot.notification_service.dto.request.UserRequest;
import com.spring.boot.notification_service.exception.AppException;
import com.spring.boot.notification_service.exception.ErrorCode;
import com.spring.boot.notification_service.repository.httpclient.IdentityClient;
import com.spring.boot.notification_service.service.IdentityService;
import com.spring.boot.notification_service.service.MailService;
import com.spring.boot.notification_service.service.ThymeleafService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=\"utf-8\"";

    @Value("${config.mail.host}")
    protected String host="smtp.gmail.com";
    @Value("${config.mail.port}")
    protected  String port="587";
    @Value("${config.mail.username}")
    protected  String email="phancongtu25032002@gmail.com";
    @Value("${config.mail.password}")
    protected String password="udvepsndgybmyzye";
    @Value("${send-by-mail.verification-code-time}")
    protected Long verificationCodeTime=30L;
    @Value("${send-by-mail.reset-password-code-time}")
    protected  Long resetPasswordCodeTime=15L;

    IdentityService identityService;

    ThymeleafService thymeleafService;

//    @Autowired
//    UserProfileRepository userProfileRepository;
//
//    @Autowired
//    ClassroomRegistrationRepository classroomRegistrationRepository;

    private Message getEmailMessage() {
        log.info("Get email message");
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });
        Message message = new MimeMessage(session);
        return message;
    }

    @Override
    @Async
    public void sendTestUpdatedNotificationEmail(MultipleChoiceTestRequest multipleChoiceTest) {
        List<String> registerUserEmails =
                classroomRegistrationRepository.findUserEmailOfClassroom(multipleChoiceTest.getClassRoom().getId());
        sendEmailTestUpdatedNotification(multipleChoiceTest, registerUserEmails);
    }

    @Async
    protected void sendEmailTestUpdatedNotification(MultipleChoiceTestRequest multipleChoiceTest, List<String> registerUserEmails) {
        Timestamp stamp = new Timestamp(multipleChoiceTest.getStartDate());
        Date date = new Date(stamp.getTime());
        String startDate = String.format("%s:%s %s/%s", date.getHours(), date.getMinutes(), date.getDate(), date.getMonth()+1);
        String classroomName = multipleChoiceTest.getClassName();
        String testingTime = multipleChoiceTest.getTestingTime().toString() + " minutes";
        String testName = multipleChoiceTest.getTestName();
        try {
            Message message = getEmailMessage();
            String emails = String.join(",", registerUserEmails);
            // start send mail
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));
            message.setFrom(new InternetAddress(email));
            message.setSubject("[ONLINE EXAM PLATFORM] Your exam has been updated!");
            message.setContent(
                    thymeleafService.getTestUpdatedNotificationMailContent
                            (classroomName, testName, startDate, testingTime), CONTENT_TYPE_TEXT_HTML);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendTestCreatedNotificationEmail(Long classroomId, MultipleChoiceTestRequest multipleChoiceTest) {
    List<String> registerUserEmails =
            classroomRegistrationRepository.findUserEmailOfClassroom(classroomId);
        sendEmailTestCreatedNotification(registerUserEmails, multipleChoiceTest);
    }

    @Override
    @Async
    public void sendTestDeletedNotificationEmail(MultipleChoiceTestRequest multipleChoiceTest) {
        List<String> registerUserEmails =
                classroomRegistrationRepository.findUserEmailOfClassroom(multipleChoiceTest.getClassRoom().getId());
        sendEmailTestDeletedNotification(registerUserEmails, multipleChoiceTest);
    }
    @Async
    protected void sendEmailTestDeletedNotification(List<String> registerUserEmails, MultipleChoiceTestRequest multipleChoiceTest) {
        Timestamp stamp = new Timestamp(multipleChoiceTest.getStartDate());
        Date date = new Date(stamp.getTime());
        String startDate = String.format("%s:%s %s/%s", date.getHours(), date.getMinutes(), date.getDate(), date.getMonth()+1);
        String classroomName = multipleChoiceTest.getClassName();
        String testingTime = multipleChoiceTest.getTestingTime().toString() + " minutes";
        String testName = multipleChoiceTest.getTestName();
        try {
            Message message = getEmailMessage();
            String emails = String.join(",", registerUserEmails);
            // start send mail
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));
            message.setFrom(new InternetAddress(email));
            message.setSubject("[ONLINE EXAM PLATFORM] Your exam has been cancelled!");
            message.setContent(
                    thymeleafService.getTestDeletedNotificationMailContent
                            (classroomName, testName, startDate, testingTime), CONTENT_TYPE_TEXT_HTML);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    protected void sendEmailTestCreatedNotification(List<String> registerUserEmails, MultipleChoiceTestRequest multipleChoiceTest) {
        Timestamp stamp = new Timestamp(multipleChoiceTest.getStartDate());
        Date date = new Date(stamp.getTime());
        String startDate = String.format("%s:%s %s/%s", date.getHours(), date.getMinutes(), date.getDate(), date.getMonth()+1);
        String classroomName = multipleChoiceTest.getClassName();
        String testingTime = multipleChoiceTest.getTestingTime().toString() + " minutes";
        String testName = multipleChoiceTest.getTestName();
        try {
            Message message = getEmailMessage();
            String emails = String.join(",", registerUserEmails);
            // start send mail
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));
            message.setFrom(new InternetAddress(email));
            message.setSubject("[ONLINE EXAM PLATFORM] Your classroom has a new exam!");
            message.setContent(
                    thymeleafService.getTestCreatedNotificationMailContent
                            (classroomName, testName, startDate, testingTime), CONTENT_TYPE_TEXT_HTML);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send verification code to user for verify email based on login name
     *
     *
     */
    @Override
    @Async
    public ResponseEntity<?> sendVerificationEmail() {
        log.info("Sending verification code...");
        UserRequest userRequest = identityService.getCurrentUser();
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        String verificationCode = String.valueOf(rnd.nextInt(999999));



        String checkEmailAddress = userRequest.getEmailAddress();

        // If email address has been verified without value of new_email_address column
        if (Objects.isNull(userRequest.getNewEmailAddress()) && userRequest.getIsEmailAddressVerified()) {
            throw new AppException(ErrorCode.VERIFY_INVALID_STATUS);
        }

        // If new email address is not null, make sure that this email has not been verified by another user
        if(Objects.nonNull(userRequest.getNewEmailAddress()) && userRequest.getIsEmailAddressVerified()){
            checkEmailAddress = userRequest.getNewEmailAddress();
            // if new email address equals to old verified email address
            // Just for testing, we do not allow users to update the new email address
            // to the same as the old verified email address
            if(userRequest.getEmailAddress().equals(checkEmailAddress)){
                log.error("New email address is the same as value with verified email address : " + checkEmailAddress);
                userRequest.setNewEmailAddress(null);
                return ResponseEntity.noContent().build();
            }
        }
        UserRequest value = identityService.checkEmailVerified(checkEmailAddress);

        if (value!=null){
            throw new AppException(ErrorCode.VERIFY_EMAIL_VERIFIED_BY_ANOTHER_USER);
        }
        // update verification code and expired time into database
        updateVerificationCode(verificationCode, userRequest.getId());
        sendEmailVerification(verificationCode, checkEmailAddress);

        return ResponseEntity.noContent().build();
    }

    @Async
    protected void sendEmailVerification(String verificationCode, String checkEmailAddress) {
        try {
            log.info("Sending verification email...");
            Message message = getEmailMessage();

            // start send mail
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(checkEmailAddress)});

            message.setFrom(new InternetAddress(email));
            message.setSubject("[ONLINE EXAM PLATFORM] Verify your email");
            message.setContent(thymeleafService.getVerificationMailContent(verificationCode), CONTENT_TYPE_TEXT_HTML);

            Transport.send(message);
            log.info("Email sent");

        } catch (MessagingException  e) {
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e){
            log.info(e.toString());
        }
    }

    /**
     * Update usage information for email verification
     *
     * @param verificationCode : The verification code
     * @param userId      : The user id
     */
    private void updateVerificationCode(String verificationCode, String userId) {
//        userProfile.setVerificationExpiredCodeTime(Instant.now().plusSeconds(verificationCodeTime * 60));
//        userProfile.setVerificationCode(verificationCode);
//        userProfileRepository.save(userProfile);
        log.info("Updating verification code to " + verificationCode);
        identityService.updateVerificationEmailCode(userId,verificationCode);
    }

    @Override
    @Async
    public ResponseEntity<?> sendResetPasswordEmail(String emailAddress) {

        int length = 6;
        boolean useLetters = true;
        boolean useNumbers = false;
        String resetPasswordCode = RandomStringUtils.random(length, useLetters, useNumbers);

        UserRequest value = identityService.checkEmailVerified(emailAddress);
        if (value==null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // update verification code and expired time into database
        updateResetPasswordCode(resetPasswordCode, value.getId());
        sendEmailResetPassword(resetPasswordCode, value);

        return ResponseEntity.noContent().build();
    }

    @Async
    protected void sendEmailResetPassword(String resetPasswordCode, UserRequest userProfile) {
        try {
            Message message = getEmailMessage();
            // start send mail
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(userProfile.getEmailAddress())});
            message.setFrom(new InternetAddress(email));
            message.setSubject("[ONLINE EXAM PLATFORM] Reset your account password!");
            message.setContent(thymeleafService.getResetPasswordMailContent(resetPasswordCode), CONTENT_TYPE_TEXT_HTML);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void updateResetPasswordCode(String resetPasswordCode, String userId) {
//        userProfile.setResetPasswordExpiredCodeTime(Instant.now().plusSeconds(resetPasswordCodeTime * 60));
//        userProfile.setResetPasswordCode(resetPasswordCode);
//        userProfileRepository.save(userProfile);
        identityService.updateVerificationPassCode(userId,resetPasswordCode);
    }
}
