package com.spring.boot.exam_service.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotificationRequest {
    private String receiverId;
    private String senderId;
    private String type;
    private String image;
    private String title;
    private Long idOfTypeNotify;
}