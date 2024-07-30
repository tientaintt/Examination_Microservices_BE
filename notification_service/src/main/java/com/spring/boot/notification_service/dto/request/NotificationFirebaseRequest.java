package com.spring.boot.notification_service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class NotificationFirebaseRequest {
    String title;
    String body;
    String image;
    String recipientToken;
    Map<String,String> data;
}
